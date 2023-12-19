package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdminRestController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService,
                               RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }
    @GetMapping("/admin")
    public ModelAndView getUsersList(Principal principal, Model model) {
        ModelAndView admin = new ModelAndView("admin/control-panel");
        addAttributesToMainPage(model, principal);
        return admin;
    }

    private void addAttributesToMainPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(new User());
        List<String> roles = user.getRoles().stream()
                .map(Role::getRole)
                .map(role -> role.split("_")[1])
                .toList();

        model.addAttribute("authUser", user);
        model.addAttribute("userRoles", roles);
        model.addAttribute("listRoles", roleService.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity<HttpStatus> createUser(@RequestBody @Valid User user,
                                                 BindingResult bindingResult) {
        Optional<User> findUsername = userService.findByUsername(user.getUsername());
        if (findUsername.isPresent()) {
            bindingResult.rejectValue("username", "error.username",
                    "This username is already in use");
        }

        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/users")
    public ResponseEntity<HttpStatus> editUser(@RequestBody @Valid User user,
                                               BindingResult bindingResult) {
        Optional<User> findUsername = userService.findByUsername(user.getUsername());
        if (findUsername.isPresent() && (!findUsername.get().getId().equals(user.getId()))) {
            bindingResult.rejectValue("username", "error.username",
                    "This username is already in use");
        }


        userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
