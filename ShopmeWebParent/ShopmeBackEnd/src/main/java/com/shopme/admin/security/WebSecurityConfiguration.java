package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
         return new ShopmeUserDetailsService();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/users/**").hasAuthority("Admin")
                                .requestMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                                .requestMatchers("/customers/**").hasAnyAuthority("Admin",  "Salesperson")
                                .requestMatchers("/shipping/**").hasAnyAuthority("Admin", "Salesperson")
                                .requestMatchers("/orders/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                                .requestMatchers("/report/**").hasAnyAuthority("Admin", "Salesperson")
                                .requestMatchers("/articles/**").hasAnyAuthority("Admin", "Salesperson")
                                .requestMatchers("/menus/**").hasAnyAuthority("Admin", "Salesperson")
                                .requestMatchers("/settings/**").hasAnyAuthority("Admin", "Salesperson")
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/users")
                                .usernameParameter("email")
                                .permitAll())
                .logout(logout -> logout.permitAll())
                .authenticationProvider(authenticationProvider())
                .rememberMe(rememberMe ->
                        rememberMe.key("AadsfsdDFresrf12313rtdszP_")
                                .tokenValiditySeconds(7 * 24 * 60 * 60));// 7 days
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer configure() throws Exception {
        return  (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }

}
