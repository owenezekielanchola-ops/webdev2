# Lab 4: Server-Rendered CRUD Interface

## Completed Tasks
- **Task 1: Product List Template** - Created `products.html` with `th:each`, `th:text`, and `th:if` empty list handling.
- **Task 2: Product Detail Template** - Created `product-detail.html` rendering product attributes and nested property `product.category.name`.
- **Task 3: Shared Navigation Fragment** - Created `fragments/navbar.html` with `th:fragment="navbar"` and included it in both templates using `th:replace`.
- **Task 4: Create Form** - Created `product-form.html` bound to the Product model using `th:object` and `th:field`, redirecting back to `/products` upon submission.
- **Task 5: Binding Errors** - Updated `POST /products` to accept `BindingResult` and redisplay the form on errors, displaying field errors with `th:errors`.