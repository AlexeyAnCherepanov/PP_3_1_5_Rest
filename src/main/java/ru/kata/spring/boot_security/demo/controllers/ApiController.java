package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserService userService;

    @Autowired
    public ApiController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAll();
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
