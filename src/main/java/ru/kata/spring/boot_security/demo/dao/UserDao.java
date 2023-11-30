package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.models.User;

public interface UserDao {
    User findByUsername(String username);
}
