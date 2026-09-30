package com.copal.webdev2.controller;

import com.copal.webdev2.model.MenuItem;
import com.copal.webdev2.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/menu")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public String listMenuItems(Model model) {
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        return "menu/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("menuItem")) {
            model.addAttribute("menuItem", new MenuItem());
        }
        return "menu/create";
    }

    @PostMapping
    public String createMenuItem(@Valid @ModelAttribute("menuItem") MenuItem menuItem,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "menu/create";
        }
        menuItemService.createMenuItem(menuItem);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item added successfully!");
        return "redirect:/menu";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        MenuItem menuItem = menuItemService.getMenuItemById(id);
        model.addAttribute("menuItem", menuItem);
        return "menu/edit";
    }

    @PostMapping("/update/{id}")
    public String updateMenuItem(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("menuItem") MenuItem menuItem,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "menu/edit";
        }
        menuItemService.updateMenuItem(id, menuItem);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item updated successfully!");
        return "redirect:/menu";
    }

    @PostMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        menuItemService.deleteMenuItem(id);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item removed from catalog!");
        return "redirect:/menu";
    }
}