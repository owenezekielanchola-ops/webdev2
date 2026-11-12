package com.example.tasktracker.repository;

import com.example.tasktracker.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
