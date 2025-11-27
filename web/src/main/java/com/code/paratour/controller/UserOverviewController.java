package com.code.paratour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.code.paratour.repositories.AdminUserRepository;
import com.code.paratour.repositories.ClientRepository;
import com.code.paratour.repositories.UserRepository;

/**
 * Controller responsible for displaying a consolidated overview of all user types
 * in the administration panel.
 *
 * This controller queries three separate repositories:
 *  - AdminUserRepository → system administrators
 *  - ClientRepository     → client accounts
 *  - UserRepository       → general application users
 *
 * The fetched data is then injected into the model and rendered in the "users" view.
 */
@Controller
public class UserOverviewController {

    private final AdminUserRepository adminUserRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    /**
     * Constructor-based dependency injection for improved testability and clarity.
     */
    public UserOverviewController(AdminUserRepository adminUserRepository,
                                  ClientRepository clientRepository,
                                  UserRepository userRepository) {
        this.adminUserRepository = adminUserRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    /**
     * Handles GET requests for the administration user overview page.
     * Retrieves all admins, clients, and general users from their
     * respective repositories and exposes them to the view layer.
     *
     * @param model the model used to pass data to the Thymeleaf template
     * @return the name of the view template to render ("users")
     */
    @GetMapping("/admin/users")
    public String showUsers(Model model) {
        model.addAttribute("adminUsers", adminUserRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("users", userRepository.findAll());

        return "users";
    }
}
