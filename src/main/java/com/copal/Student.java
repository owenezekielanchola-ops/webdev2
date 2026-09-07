package com.copal;

public class Student implements Gradable{
    private String name;
    private String course;
    private int age;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCourse() {
        return course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public int getAge() {
        return age;
    }
   public void setAge(int age) {
        if (age < 0) {
            throw new InvalidAgeException("Invalid age: " + age + ". Age cannot be negative.");
        }
        this.age = age;
    }
    public Student(String name, String course, int age) {
        this.name = name;
        this.course = course;
        setAge(age);
    }
    @Override
    public String computeStanding() {
            return "Good Standing";

    }
    
    
}
