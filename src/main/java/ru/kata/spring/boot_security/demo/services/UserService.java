package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;


public interface UserService{

    List<User> findAll();
    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id);

    void updateUser(User user);

    void saveUser(User user);

    public void deleteById(Long id);

}