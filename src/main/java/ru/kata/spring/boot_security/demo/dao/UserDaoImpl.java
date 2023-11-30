package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
@Repository
public class UserDaoImpl implements UserDao{
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public User findByUsername(String username) {
        Query query = entityManager.createQuery
                ("select u from User u left join fetch u.roles where u.username=:name", User.class);
        query.setParameter("name", username);
        return (User) query.getSingleResult();
    }
}
