package com.copal.webdev2.service;

import com.copal.webdev2.model.MenuItem;
import java.util.List;

public interface MenuItemService {
    List<MenuItem> getAllMenuItems();
    MenuItem getMenuItemById(Long id);
    MenuItem createMenuItem(MenuItem item);
    MenuItem updateMenuItem(Long id, MenuItem item);
    void deleteMenuItem(Long id);
}