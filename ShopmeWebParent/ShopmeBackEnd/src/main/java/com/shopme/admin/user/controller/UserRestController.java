package com.shopme.admin.user.controller;

import com.shopme.admin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserRestController {
    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/check_email")
    public String checkDuplicatedEmail(@RequestParam Map<String, String> payload) {
        String userEmail = payload.get("email");
        String userIdString = payload.get("id");
        Integer userId = userIdString.isEmpty() ? null : Integer.parseInt(userIdString);
        System.out.println(userEmail + " " + userId);
        return userService.isEmailUnique(userEmail, userId) ? "OK" : "Duplicated";
    }
}
