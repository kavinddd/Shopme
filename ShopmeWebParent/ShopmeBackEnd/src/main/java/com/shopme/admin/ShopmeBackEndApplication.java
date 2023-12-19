package com.shopme.admin;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

@SpringBootApplication
@EntityScan({"com.shopme.common.entity"})//, "com.shopme.admin.user"})
public class ShopmeBackEndApplication {

    public static void main(String[] args) {

        SpringApplication.run(ShopmeBackEndApplication.class, args);
    }
}