package com.socia1ca3t.timepillbackup.controller;

import com.socia1ca3t.timepillbackup.config.CurrentUser;
import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    private static final Log logger = LogFactory.getLog(LoginController.class);


    @GetMapping("/login")
    public String login(@CurrentUser UserDTO user, HttpSession session, Model model) {

        if (user != null) {
            return "redirect:/home";
        }

        if (session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {

            AuthenticationException exception = (AuthenticationException) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            model.addAttribute("errorMsg", exception.getMessage());
        }

        return "login";
    }


    @GetMapping("/logout")
    public String logout() {

        return "login";
    }

    @GetMapping("/success")
    public String success() {

        return "success";
    }

    @GetMapping("/index")
    public String index() {

        return "index";
    }

    @GetMapping("/notices")
    public String notices() {

        return "use_notices";
    }


}
