package com.example.tasktracker.service;

import com.example.tasktracker.model.Appointment;
import com.example.tasktracker.model.Course;
import com.example.tasktracker.model.Department;
import com.example.tasktracker.model.Employee;
import com.example.tasktracker.model.Enrollment;
import com.example.tasktracker.model.Owner;
import com.example.tasktracker.model.Pet;
import com.example.tasktracker.model.Student;
import com.example.tasktracker.model.User;
import com.example.tasktracker.model.UserProfile;
import com.example.tasktracker.model.VaccinationRecord;
import com.example.tasktracker.model.Vaccine;
import com.example.tasktracker.model.Veterinarian;
import com.example.tasktracker.repository.AppointmentRepository;
import com.example.tasktracker.repository.CourseRepository;
import com.example.tasktracker.repository.DepartmentRepository;
import com.example.tasktracker.repository.EmployeeRepository;
import com.example.tasktracker.repository.EnrollmentRepository;
import com.example.tasktracker.repository.OwnerRepository;
import com.example.tasktracker.repository.PetRepository;
import com.example.tasktracker.repository.StudentRepository;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.repository.VaccinationRecordRepository;
import com.example.tasktracker.repository.VaccineRepository;
import com.example.tasktracker.repository.VeterinarianRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class Week9LabRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final AppointmentRepository appointmentRepository;
    private final VaccineRepository vaccineRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;

    public Week9LabRunner(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            DepartmentService departmentService,
            EmployeeRepository employeeRepository,
            EmployeeService employeeService,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            OwnerRepository ownerRepository,
            PetRepository petRepository,
            VeterinarianRepository veterinarianRepository,
            AppointmentRepository appointmentRepository,
            VaccineRepository vaccineRepository,
            VaccinationRecordRepository vaccinationRecordRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.departmentService = departmentService;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.appointmentRepository = appointmentRepository;
        this.vaccineRepository = vaccineRepository;
        this.vaccinationRecordRepository = vaccinationRecordRepository;
    }

    @Override
    public void run(String... args) {
        User user = new User();
        user.setUsername("jane_doe");
        user.setEmail("jane@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setProfile(new UserProfile("Backend developer"));
        userRepository.save(user);

        Department dept = new Department("Engineering");
        dept.addEmployee(new Employee("Alice Reyes"));
        dept.addEmployee(new Employee("Marco Santos"));
        Department savedDept = departmentRepository.save(dept);

        departmentService.printAllDepartmentsWithEmployees();
        List<Employee> allEmployees = employeeRepository.findAll();
        Long firstEmployeeId = allEmployees.get(0).getId();

        try {
            Employee lazyEmp = employeeService.getEmployeeWithoutTransaction(firstEmployeeId);
            System.out.println(lazyEmp.getDepartment().getName());
        } catch (Exception ex) {
            System.out.println("TASK3_LAZY_EXCEPTION=" + ex.getClass().getName() + ": " + ex.getMessage());
        }

        String txDeptName = employeeService.getDepartmentNameTransactional(firstEmployeeId);
        System.out.println("TASK3_TRANSACTIONAL_RESULT=" + txDeptName);

        String joinFetchDeptName = employeeService.getDepartmentNameJoinFetch(firstEmployeeId);
        System.out.println("TASK3_JOIN_FETCH_RESULT=" + joinFetchDeptName);

        Student student = studentRepository.save(new Student("Carlos Mendoza"));
        Course course = courseRepository.save(new Course("WEBDEV2", "Web Development 2"));

        Enrollment e1 = enrollmentRepository.save(new Enrollment(student, course, LocalDate.now(), "A"));
        System.out.println("TASK2_ENROLL_OK_ID=" + e1.getId() + " GRADE=" + e1.getGrade());

        try {
            enrollmentRepository.save(new Enrollment(student, course, LocalDate.now(), "B"));
        } catch (DataIntegrityViolationException ex) {
            System.out.println("TASK2_DUPLICATE_REJECTED=" + ex.getMostSpecificCause().getMessage());
        }

        System.out.println("TASK4_EMPLOYEES_BEFORE_REMOVE=" + employeeRepository.count());
        departmentService.removeEmployeeByName(savedDept.getId(), "Marco Santos");
        System.out.println("TASK4_EMPLOYEES_AFTER_REMOVE=" + employeeRepository.count());

        studentRepository.deleteById(student.getId());
        System.out.println("TASK4_ENROLLMENTS_AFTER_STUDENT_DELETE=" + enrollmentRepository.count());
        System.out.println("TASK4_COURSES_AFTER_STUDENT_DELETE=" + courseRepository.count());

        Owner owner = ownerRepository.save(new Owner("Maria Clara", "09171234567", "maria@example.com"));
        Pet bantay = petRepository.save(new Pet("MC-9001", "Bantay", "Dog", "Aspin", owner));
        Veterinarian vet = veterinarianRepository.save(new Veterinarian("Dr. Jose Rizal", "VET-101", "General"));
        Vaccine vaccine = vaccineRepository.save(new Vaccine("Rabies", "Zoetis"));
        appointmentRepository.save(new Appointment(LocalDateTime.now().plusDays(1), "Checkup", bantay, vet));
        vaccinationRecordRepository.save(new VaccinationRecord(LocalDate.now(), LocalDate.now().plusYears(1), bantay, vaccine));

        System.out.println("TASK5_OWNER=" + owner.getFullName() + " PET=" + bantay.getName());
        System.out.println("TASK5_APPOINTMENTS=" + appointmentRepository.findByPetId(bantay.getId()).size()
                + " VACCINATION_RECORDS=" + vaccinationRecordRepository.findByPetId(bantay.getId()).size());
    }
}
