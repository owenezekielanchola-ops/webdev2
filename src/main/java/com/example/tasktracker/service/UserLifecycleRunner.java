package com.example.tasktracker.service;

import com.example.tasktracker.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserLifecycleRunner implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setCreatedAt(LocalDateTime.now());

        entityManager.persist(user);
        entityManager.flush();

        entityManager.detach(user);

        user.setEmail("changed_email@example.com");
    }
}