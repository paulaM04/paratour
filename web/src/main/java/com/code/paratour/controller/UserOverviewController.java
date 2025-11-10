package com.code.paratour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.code.paratour.repositories.AdminUserRepository;
import com.code.paratour.repositories.ClientRepository;
import com.code.paratour.repositories.UserRepository;

@Controller
public class UserOverviewController {

    private final AdminUserRepository adminUserRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public UserOverviewController(AdminUserRepository adminUserRepository,
                                  ClientRepository clientRepository,
                                  UserRepository userRepository) {
        this.adminUserRepository = adminUserRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/users")
    public String showUsers(Model model) {
        model.addAttribute("adminUsers", adminUserRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }
}
