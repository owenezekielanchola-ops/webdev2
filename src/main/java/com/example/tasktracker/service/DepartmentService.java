package com.example.tasktracker.service;

import com.example.tasktracker.model.Department;
import com.example.tasktracker.model.Employee;
import com.example.tasktracker.repository.DepartmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public void printAllDepartmentsWithEmployees() {
        List<Department> departments = departmentRepository.findAll();
        for (Department d : departments) {
            List<String> names = d.getEmployees().stream().map(Employee::getFullName).toList();
            System.out.println("TASK1_READBACK - Department: " + d.getName() + " | Employees: " + names);
        }
    }

    @Transactional
    public void removeEmployeeByName(Long departmentId, String fullName) {
        Department dept = departmentRepository.findById(departmentId).orElseThrow();
        Employee target = dept.getEmployees().stream()
                .filter(e -> fullName.equals(e.getFullName()))
                .findFirst()
                .orElseThrow();
        dept.removeEmployee(target);
        departmentRepository.save(dept);
    }
}
