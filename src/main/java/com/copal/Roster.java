package com.copal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Roster {
    private List<Student> students;

    public Roster() {
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        if (student != null) {
            students.add(student);
        }
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<String> generateAdultStudentReport() {
        List<String> adultStudentNames = students.stream()
                .filter(student -> student.getAge() >= 18)
                .map(Student::getName)
                .collect(Collectors.toList());

        System.out.println("\n--- Stream Report Summary ---");
        System.out.println("Total qualified students (Age 18+): " + adultStudentNames.size());

        return adultStudentNames;
    }
}