package com.example.tasktracker.service;

import com.example.tasktracker.model.Employee;
import com.example.tasktracker.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeWithoutTransaction(Long id) {
        return employeeRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public String getDepartmentNameTransactional(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        return employee.getDepartment().getName();
    }

    public String getDepartmentNameJoinFetch(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartment(id).orElseThrow();
        return employee.getDepartment().getName();
    }
}
