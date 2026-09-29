package com.copal.webdev2.repository;

import com.copal.webdev2.model.MenuItem;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMenuItemRepository implements MenuItemRepository {

    private final Map<Long, MenuItem> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public InMemoryMenuItemRepository() {
        save(new MenuItem(null, "Iced Spanish Latte", "Coffee", new BigDecimal("165.00"), 25));
        save(new MenuItem(null, "Matcha Cloud Cream", "Beverages", new BigDecimal("185.00"), 18));
        save(new MenuItem(null, "Butter Croissant", "Pastries", new BigDecimal("95.00"), 30));
        save(new MenuItem(null, "Creamy Truffle Pasta", "Mains", new BigDecimal("275.00"), 12));
    }

    @Override
    public MenuItem save(MenuItem item) {
        if (item.getId() == null) {
            item.setId(idGenerator.getAndIncrement());
        }
        database.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public MenuItem update(MenuItem item) {
        database.put(item.getId(), item);
        return item;
    }

    @Override
    public boolean deleteById(Long id) {
        return database.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return database.containsKey(id);
    }
}