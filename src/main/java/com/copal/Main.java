package com.copal;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Roster roster = new Roster();

        try {
            Student invalidStudent = new Student("Ghost Student", "BS Computer Science", -5);
            roster.addStudent(invalidStudent);
        } catch (InvalidAgeException e) {
            System.err.println("[CAUGHT EXCEPTION]: " + e.getMessage());
            System.out.println("Program recovered successfully and keeps running!\n");
        }

        roster.addStudent(new Student("Alice", "BS Computer Science", 18));
        roster.addStudent(new Student("Liam", "BS Computer Science", 23));
        roster.addStudent(new Student("Owen", "BS Computer Science", 22));
        roster.addStudent(new Student("Louie", "BS Computer Science", 17));
        roster.addStudent(new Student("jeff", "BS Computer Science", 17));
        roster.addStudent(new Student("Sofeah", "BS Computer Science", 18));
        
        System.out.println("=== Full Student Roster ===");
        for (Student s : roster.getStudents()) {
            System.out.println("Name: " + s.getName() + 
                               " | Course: " + s.getCourse() + 
                               " | Age: " + s.getAge() + 
                               " | Standing: " + s.computeStanding());
        }
    
        System.out.println("\n=== Stream-Based Roster Report (Task 5) ===");
        List<String> qualifiedNames = roster.generateAdultStudentReport();

        System.out.println("List of Qualified Student Names:");
        qualifiedNames.forEach(name -> System.out.println(" - " + name));
    }
}