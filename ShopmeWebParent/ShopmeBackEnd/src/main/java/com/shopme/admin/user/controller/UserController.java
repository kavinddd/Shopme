package com.shopme.admin.user.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.export.Exporter;
import com.shopme.admin.export.UserCsvExporterStrategy;
import com.shopme.admin.export.UserExcelExporterStrategy;
import com.shopme.admin.export.UserPdfExporterStrategy;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.UserServiceImpl;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "id", "asc", null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") int pageNum, Model model,
                             @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
                             @RequestParam(name = "keyword", required = false) String keyword){

        Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);

        List<User> users = page.getContent();
        model.addAttribute("users", users);

        long startCount = (pageNum - 1) * UserServiceImpl.USERS_PER_PAGE + 1;
        long endCount = startCount + UserServiceImpl.USERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        // data used for showing page number in UI
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());

        // data used for button UI
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", (sortDir.equals("asc")) ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        return "users/users";
    }
    @GetMapping("/users/new")
    public String newUser(Model model){
        List<Role> listRoles = service.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image")MultipartFile multipartFile,
                           Model model) throws IOException  {
        boolean isImageUploaded = !multipartFile.isEmpty();
        boolean isUserHadPhoto = !(user.getPhotos().isEmpty());

        // if image is uploaded, we update the new file
        if (isImageUploaded) {
            user.setPhotos(multipartFile.getOriginalFilename());
        }
        // no image uploaded, but the user already had the photo
        else if (!isUserHadPhoto) {
            user.setPhotos(null);
        }
        // save user
        User savedUser = service.save(user);

        // new image uploaded -> save the file in the right path
        if (isImageUploaded) {
            Integer savedUserId = savedUser.getId();
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String uploadDir = "user-photos/" + savedUserId;
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }


        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

        String firstPartOfTheEmail = savedUser.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfTheEmail;
    }

    @GetMapping("users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = service.get(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + id  + ") ");
            List<Role> listRoles = service.listRoles();
            model.addAttribute("listRoles", listRoles);
            return "users/user_form";

        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message", "Successfully delete user id: " + id);
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        service.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled":"disabled" ;
        redirectAttributes.addFlashAttribute("message", "User " + id +"'s status has been " + status );
        return "redirect:/users";
    }

    @GetMapping("/users/export/{exportType}")
    public void export(HttpServletResponse response,
                       @PathVariable(name="exportType") String exportType) throws IOException {
        List<User> allUsers = service.listAll();
        Exporter<User> userExporter =
                switch (exportType) {
            case "pdf" -> new Exporter<>(new UserPdfExporterStrategy());
            case "csv" -> new Exporter<>(new UserCsvExporterStrategy());
            case "excel" -> new Exporter<>(new UserExcelExporterStrategy());
            default -> null;
        };

        if (userExporter != null) userExporter.export(allUsers, response);

    }

}
