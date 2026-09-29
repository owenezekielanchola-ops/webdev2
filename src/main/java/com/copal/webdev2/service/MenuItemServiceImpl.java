package com.copal.webdev2.service;

import com.copal.webdev2.model.MenuItem;
import com.copal.webdev2.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Menu item not found with ID: " + id));
    }

    @Override
    public MenuItem createMenuItem(MenuItem item) {
        item.setId(null);
        return menuItemRepository.save(item);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem item) {
        if (!menuItemRepository.existsById(id)) {
            throw new NoSuchElementException("Cannot update. Menu item not found with ID: " + id);
        }
        item.setId(id);
        return menuItemRepository.update(item);
    }

    @Override
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new NoSuchElementException("Cannot delete. Menu item not found with ID: " + id);
        }
        menuItemRepository.deleteById(id);
    }
}