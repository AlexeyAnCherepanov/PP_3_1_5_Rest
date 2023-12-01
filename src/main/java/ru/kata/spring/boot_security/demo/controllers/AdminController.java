package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public AdminController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String showAllUsers(Model model, Principal principal) {
        model.addAttribute("users", userService.findAll());
        addAttributesToMainPage(model, principal);
        return "admin/listUsers";
    }

//    @GetMapping("/showUser/{id}")
//    public String findUser(@PathVariable("id") Long id, Model model) {
//        User user = userService.findUserById(id);
//        model.addAttribute("user", user);
//        model.addAttribute("userRoles", user.getRoles());
//        return "user/showUser";
//    }
    @GetMapping("/users/edit")
    public String editUserForm(@RequestParam("id") Long id, Model model) {
        User userById = userService.findUserById(id);

        if (userById != null) {
            model.addAttribute("user", userById);
            model.addAttribute("listRoles", roleService.findAll());
            return "/admin/edit";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PatchMapping("/users/edit")
    public String editUser(@ModelAttribute("updatingUser") @Valid User updatingUser,
                           BindingResult bindingResult, Model model, Principal principal) {
        Optional<User> userByEmail = userService.findByUsername(updatingUser.getUsername());
        if (userByEmail.isPresent() && (!userByEmail.get().getId().equals(updatingUser.getId()))) {
            bindingResult.rejectValue("username", "error.username",
                    "This email is already in use");
        }

        if (bindingResult.hasErrors()) {
            addAttributesToMainPage(model, principal);
            model.addAttribute("editUserError", true);
            return "/admin/listUsers";
        }

        userService.updateUser(updatingUser);
        return "redirect:/admin/users";
    }

    @DeleteMapping("/users/delete")
    public String deleteUser(@ModelAttribute("deletingUser") User user) {
        Long id = user.getId();
        if (userService.findUserById(id) != null) {
            userService.deleteUserById(id);
        }
        return "redirect:/admin/users";
    }
    @PostMapping("/users")
    public String createUser(@ModelAttribute("newUser") @Valid User newUser,
                             BindingResult bindingResult, Model model, Principal principal) {
        Optional<User> user = userService.findByUsername(newUser.getUsername());
        if (user.isPresent()) {
            bindingResult.rejectValue("username", "error.username",
                    "This Username is already in use");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("hasErrors", true);
            addAttributesToMainPage(model, principal);
            return "admin/listUsers";
        }

        this.userService.saveUser(newUser);
        return "redirect:/admin/users";
    }
    private void addAttributesToMainPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(new User());

        List<String> roles = user.getRoles().stream()
                .map(Role::getRole)
                .map(role -> role.split("_")[1])
                .toList();

        model.addAttribute("authUser", user);
        model.addAttribute("userRoles", roles);

        if (!model.containsAttribute("updatingUser")) {
            model.addAttribute("updatingUser", new User());
        }

        if (!model.containsAttribute("newUser")) {
            model.addAttribute("newUser", new User());
        }

        if (!model.containsAttribute("deletingUser")) {
            model.addAttribute("deletingUser", new User());
        }

        model.addAttribute("listRoles", roleService.findAll());
        model.addAttribute("users", userService.findAll());
    }
}
