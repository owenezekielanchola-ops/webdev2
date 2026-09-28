package com.copal.webdev2.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

import com.copal.webdev2.model.Book;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final List<Book> books = new ArrayList<>();

    public BookController() {
        books.add(new Book(1L, "Clean Code", "Robert C. Martin"));
        books.add(new Book(2L, "The Pragmatic Programmer", "Andy Hunt"));
        books.add(new Book(3L, "Clean Architecture", "Robert C. Martin"));
    }

    @GetMapping
    @ResponseBody
    public List<Book> getAllBooks(@RequestParam(required = false) String author) {
        if (author != null && !author.trim().isEmpty()) {
            return books.stream()
                    .filter(b -> b.getAuthor().equalsIgnoreCase(author.trim()))
                    .toList();
        }
        return books;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> foundBook = books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();

        return foundBook
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/view/{id}")
    public String getBookDetailView(@PathVariable Long id, Model model) {
        Book foundBook = books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);

        model.addAttribute("book", foundBook);
        return "book-detail";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createBook(@RequestBody Book newBook) {
        if (newBook.getId() == null) {
            long nextId = books.stream()
                    .mapToLong(Book::getId)
                    .max()
                    .orElse(0L) + 1;
            newBook.setId(nextId);
        }

        books.add(newBook);
        return "redirect:/books";
    }
}