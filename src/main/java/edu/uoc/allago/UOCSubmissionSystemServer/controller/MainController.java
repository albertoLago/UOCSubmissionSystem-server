package edu.uoc.allago.UOCSubmissionSystemServer.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/view_files")
    public String view_files() {
        return "view_files";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/create_pool.html")
    public String createDeliveryPool() {
        return "create_pool";
    }

    @PostMapping("/login")
    public String postLogin() {
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout"; // Redirige a la página de login con un mensaje de éxito
    }
}