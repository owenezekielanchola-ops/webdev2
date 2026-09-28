package com.copal.webdev2.controler;

import com.copal.webdev2.model.Category;
import com.copal.webdev2.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final List<Product> products = new ArrayList<>();

    public ProductController() {
        Category tech = new Category(1L, "Electronics");
        Category reading = new Category(2L, "Books");

        products.add(new Product(1L, "Laptop", 1200.0, tech));
        products.add(new Product(2L, "Headphones", 150.0, tech));
        products.add(new Product(3L, "Java Programming Book", 45.0, reading));
    }

    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        Product product = products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);

        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @PostMapping
    public String createProduct(@ModelAttribute Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "product-form";
        }

        if (product.getId() == null) {
            long nextId = products.stream()
                    .mapToLong(Product::getId)
                    .max()
                    .orElse(0L) + 1;
            product.setId(nextId);
        }

        if (product.getCategory() == null || product.getCategory().getName() == null) {
            product.setCategory(new Category(99L, "General"));
        }

        products.add(product);
        return "redirect:/products";
    }
}