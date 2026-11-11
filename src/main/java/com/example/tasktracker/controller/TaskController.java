package com.example.tasktracker.controller;

import com.example.tasktracker.exception.TaskNotFoundException;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "task-list";
    }

    @GetMapping("/tasks/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    @PostMapping("/tasks")
    public String createTask(@Valid @ModelAttribute("task") Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "task-form";
        }
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}")
    public String getTaskDetail(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
        model.addAttribute("task", task);
        return "task-detail";
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public String handleTaskNotFound(TaskNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/not-found";
    }
}