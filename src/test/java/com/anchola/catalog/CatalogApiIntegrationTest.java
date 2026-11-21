package com.anchola.catalog;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anchola.catalog.entity.Category;
import com.anchola.catalog.entity.Product;
import com.anchola.catalog.entity.ProductStatus;
import com.anchola.catalog.repository.CategoryRepository;
import com.anchola.catalog.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void categoryCrudAndDuplicateConflictFlow() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Electronics",
                                  "description": "Gadgets and devices"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name", is("Electronics")));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Electronics",
                                  "description": "Duplicate"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));

        Category saved = categoryRepository.findByName("Electronics").orElseThrow();

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/categories/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Electronics")));

        mockMvc.perform(put("/api/categories/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Consumer Electronics",
                                  "description": "Updated description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Consumer Electronics")));

        mockMvc.perform(delete("/api/categories/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categories/{id}", saved.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void productCrudPaginationFilteringCustomQueryAndConflicts() throws Exception {
        Category electronics = categoryRepository.save(new Category("Electronics", "Devices"));
        Category books = categoryRepository.save(new Category("Books", "Reading"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Wireless Mouse",
                                  "sku": "PROD-101",
                                  "price": 25.00,
                                  "stockQuantity": 15,
                                  "categoryId": %d
                                }
                                """.formatted(electronics.getId())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.categoryName", is("Electronics")));

        // Duplicate SKU -> 409
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Another Mouse",
                                  "sku": "PROD-101",
                                  "price": 30.00,
                                  "stockQuantity": 5,
                                  "categoryId": %d
                                }
                                """.formatted(electronics.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));

        // Add 2 more products for pagination, sorting, filtering, and custom @Query
        Product keyboard = new Product();
        keyboard.setName("Mechanical Keyboard");
        keyboard.setSku("PROD-102");
        keyboard.setPrice(new BigDecimal("79.99"));
        keyboard.setStockQuantity(8);
        keyboard.setStatus(ProductStatus.ACTIVE);
        keyboard.setCategory(electronics);
        productRepository.save(keyboard);

        Product novel = new Product();
        novel.setName("Alpha Guide");
        novel.setSku("BOOK-201");
        novel.setPrice(new BigDecimal("19.50"));
        novel.setStockQuantity(4);
        novel.setStatus(ProductStatus.DISCONTINUED);
        novel.setCategory(books);
        productRepository.save(novel);

        // Pagination + sort
        mockMvc.perform(get("/api/products?page=0&size=2&sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Alpha Guide")))
                .andExpect(jsonPath("$.content[1].name", is("Mechanical Keyboard")));

        // Filter by categoryId
        mockMvc.perform(get("/api/products")
                        .param("categoryId", electronics.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)));

        // Page size cap (50)
        mockMvc.perform(get("/api/products?size=200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", lessThanOrEqualTo(50)));

        // Custom @Query via status endpoint
        mockMvc.perform(get("/api/products/status/DISCONTINUED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sku", is("BOOK-201")));

        // PUT duplicate SKU -> 409
        mockMvc.perform(put("/api/products/{id}", keyboard.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mechanical Keyboard",
                                  "sku": "PROD-101",
                                  "price": 79.99,
                                  "stockQuantity": 8,
                                  "status": "ACTIVE",
                                  "categoryId": %d
                                }
                                """.formatted(electronics.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));

        // Deleting a category with products -> 409
        mockMvc.perform(delete("/api/categories/{id}", electronics.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));

        // Deleting a product -> 204
        mockMvc.perform(delete("/api/products/{id}", novel.getId()))
                .andExpect(status().isNoContent());

        // Category now empty -> 204
        mockMvc.perform(delete("/api/categories/{id}", books.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void validationAndClientErrorsReturnStructured4xxNot500() throws Exception {
        // Bean validation failure -> 400 with field errors
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "sku": "invalid sku",
                                  "price": 0,
                                  "stockQuantity": -5,
                                  "categoryId": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(4))));

        // Invalid enum value in JSON -> 400 (not 500)
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Valid Name",
                                  "sku": "PROD-301",
                                  "price": 10,
                                  "stockQuantity": 1,
                                  "status": "INVALID_STATUS",
                                  "categoryId": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        // Type mismatch on path variable -> 400 (not 500)
        mockMvc.perform(get("/api/products/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        // Invalid sort property -> 400 (not 500)
        mockMvc.perform(get("/api/products?sort=nonExistentField,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        // Non-existent resource -> 404
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        // Unsupported HTTP method -> 405 (not 500)
        mockMvc.perform(patch("/api/products/1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status", is(405)));
    }
}
