package com.copal.webdev2.repository;

import com.copal.webdev2.model.MenuItem;
import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    MenuItem save(MenuItem item);
    Optional<MenuItem> findById(Long id);
    List<MenuItem> findAll();
    MenuItem update(MenuItem item);
    boolean deleteById(Long id);
    boolean existsById(Long id);
}