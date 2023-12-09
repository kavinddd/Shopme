package com.shopme.admin.user.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {
    UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,
                              Model model){
        String email = loggedUser.getUsername();
        User user = userService.getByEmail(email);
        model.addAttribute("user", user);
        return "users/account_form";
    }
    @PostMapping("/account/update")
    public String saveDetails(User user, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal ShopmeUserDetails shopmeUserDetails,
                           @RequestParam("image") MultipartFile multipartFile,
                           Model model) throws IOException, UserNotFoundException {
        boolean isImageUploaded = !multipartFile.isEmpty();
        boolean isUserHadPhoto = !(user.getPhotos().isEmpty());

        System.out.println("Is image uploaded? : " + isImageUploaded);
        System.out.println("Is user had photo? : " + isUserHadPhoto);
        // if image is uploaded, we update the new file
        if (isImageUploaded) {
            user.setPhotos(multipartFile.getOriginalFilename());
        }
        // no image uploaded also user didn't have any photo
        else if (!isUserHadPhoto) {
            user.setPhotos(null);
        }
        // save user
        User savedUser = userService.updateAccount(user);

        // new image uploaded -> save the file in the right path
        if (isImageUploaded) {
            Integer savedUserId = savedUser.getId();
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String uploadDir = "user-photos/" + savedUserId;
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");

        shopmeUserDetails.setFirstName(savedUser.getFirstName());
        shopmeUserDetails.setLastName(savedUser.getLastName());
        return "redirect:/account";
    }
}
