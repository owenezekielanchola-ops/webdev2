# WEBDEV2 — Week 9 Lab Answers

Name: Owen Ezekiel A. Anchola
Student ID: 2511003
GitHub repo: https://github.com/owenezekielanchola-ops/webdev2
Date submitted: 2026-11-13

---

## Task 1 — One-to-One + One-to-Many Mappings

**Approach / explanation:**
The one-to-one relationship was implemented by having the `User` entity as the owner (using `@JoinColumn(name = "profile_id")`) and `UserProfile` as the inverse side (`mappedBy = "profile"`). In the `Employee` entity, the one-to-many relationship was implemented using `@ManyToOne(fetch = FetchType.LAZY)` with `@JoinColumn(name = "department_id", nullable = false)`. The `Department` entity uses `mappedBy = "department"` and `addEmployee()` and `removeEmployee()`.

**Code / endpoint(s) used (if applicable):**
```java
@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "profile")
    private User user;
}

@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }
}

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
Hibernate: insert into user_profiles (bio) values (?)
Hibernate: insert into app_users (created_at,email,profile_id,username) values (?,?,?,?)
Hibernate: insert into department (name) values (?)
Hibernate: insert into employee (department_id,full_name) values (?,?)
Hibernate: insert into employee (department_id,full_name) values (?,?)
Hibernate: select d1_0.id,d1_0.name from department d1_0
Hibernate: select e1_0.department_id,e1_0.id,e1_0.full_name from employee e1_0 where e1_0.department_id=?
TASK1_READBACK - Department: Engineering | Employees: [Alice Reyes, Marco Santos]
```

**Reflection question:**
_Which entity owns the `Department`–`Employee` relationship and why, and what value must `mappedBy` contain?_

Your answer:
The `Employee` entity owns the relationship because there is a foreign key of `department_id` in the `employee` table. The `mappedBy` should be set to `"department"`.

---

## Task 2 — Many-to-Many Join Table + Association Entity

**Approach / explanation:**
I initially implemented the many-to-many relationship between `Student` and `Course` using `@ManyToMany` and `@JoinTable`. However, I changed it to use an `Enrollment` entity to store additional fields of `enrollmentDate` and `grade`. A unique constraint was added with a name of `uk_student_course` on the fields `student_id` and `course_id`.

**Code / endpoint(s) used (if applicable):**
```java
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
    name = "student_course",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id")
)
private List<Course> courses = new ArrayList<>();

@Entity
@Table(
    name = "enrollment",
    uniqueConstraints = @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})
)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate enrollmentDate;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
Hibernate: create table enrollment (enrollment_date date, course_id bigint not null, id bigint not null auto_increment, student_id bigint not null, grade varchar(255), primary key (id)) engine=InnoDB
Hibernate: alter table enrollment add constraint uk_student_course unique (student_id, course_id)
Hibernate: insert into student (full_name) values (?)
Hibernate: insert into course (code,title) values (?,?)
Hibernate: insert into enrollment (course_id,enrollment_date,grade,student_id) values (?,?,?,?)
TASK2_ENROLL_OK_ID=1 GRADE=A
Hibernate: insert into enrollment (course_id,enrollment_date,grade,student_id) values (?,?,?,?)
WARN  org.hibernate.orm.jdbc.error : Duplicate entry '1-1' for key 'enrollment.uk_student_course'
TASK2_DUPLICATE_REJECTED=Duplicate entry '1-1' for key 'enrollment.uk_student_course'
```

**Reflection question:**
_When must a many-to-many relationship be modeled as an association entity instead of a direct `@ManyToMany`, and what did the unique constraint protect in your test?_

Your answer:
You would want to use an association entity if there were additional fields in the relationship. The unique constraint stops the same `student_id` and `course_id` from being added more than once.

---

## Task 3 — Fetch Strategies Experiment — LAZY vs EAGER

**Approach / explanation:**
I attempted to get the department name of the employee by using `employee.getDepartment().getName()` without a transaction. This caused a `LazyInitializationException`. I fixed this by applying the `@Transactional(readOnly = true)` annotation. This was compared to using a `JOIN FETCH` strategy, and I then compared how many SQL queries were executed.

**Code / endpoint(s) used (if applicable):**
```java
public Employee getEmployeeWithoutTransaction(Long id) {
    return employeeRepository.findById(id).orElseThrow();
}

@Transactional(readOnly = true)
public String getDepartmentNameTransactional(Long id) {
    Employee employee = employeeRepository.findById(id).orElseThrow();
    return employee.getDepartment().getName();
}

@Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
Optional<Employee> findByIdWithDepartment(@Param("id") Long id);
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
-- Lazy fail (1 query):
Hibernate: select e1_0.id,e1_0.department_id,e1_0.full_name from employee e1_0 where e1_0.id=?
TASK3_LAZY_EXCEPTION=org.hibernate.LazyInitializationException: Could not initialize proxy [com.example.tasktracker.model.Department#1] - no session

-- @Transactional fix (2 queries):
Hibernate: select e1_0.id,e1_0.department_id,e1_0.full_name from employee e1_0 where e1_0.id=?
Hibernate: select d1_0.id,d1_0.name from department d1_0 where d1_0.id=?
TASK3_TRANSACTIONAL_RESULT=Engineering

-- JOIN FETCH fix (1 query):
Hibernate: select e1_0.id,e1_0.department_id,d1_0.id,d1_0.name,e1_0.full_name from employee e1_0 join department d1_0 on d1_0.id=e1_0.department_id where e1_0.id=?
TASK3_JOIN_FETCH_RESULT=Engineering
```
`@Transactional` executed 2 queries (one for `Employee`, then another for `Department`), whereas `JOIN FETCH` loaded both in 1 query. Blanket `EAGER` on collections is avoided because it loads extra data on every query even when it isn't needed.

**Reflection question:**
_What causes `LazyInitializationException`, and why is prefer-LAZY plus explicit fetch join/`@EntityGraph` preferable to marking everything EAGER?_

Your answer:
`LazyInitializationException` occurs when you attempt to get a lazy association after the Hibernate session has closed. Using `LAZY` means that we only get the associated data when we explicitly call it using `JOIN FETCH` or `@EntityGraph`, instead of marking everything `EAGER` which always pulls extra related rows and causes N+1 queries.

---

## Task 4 — Cascade + orphanRemoval — Safe vs. Dangerous

**Approach / explanation:**
In the `Department` entity, `employees` has `cascade = {CascadeType.PERSIST, CascadeType.MERGE}` and `orphanRemoval = true`. This means that if an employee is removed from the list and saved, the row gets deleted. In the `Student`–`Course` relationship, I avoided `CascadeType.REMOVE` and `ALL` so that if the student is deleted, the course remains.

**Code / endpoint(s) used (if applicable):**
```java
@OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();

@OneToMany(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
private List<Enrollment> enrollments = new ArrayList<>();

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "course_id", nullable = false)
private Course course;
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
```text
Hibernate: select count(*) from employee e1_0
TASK4_EMPLOYEES_BEFORE_REMOVE=2
Hibernate: delete from employee where id=?
Hibernate: select count(*) from employee e1_0
TASK4_EMPLOYEES_AFTER_REMOVE=1

Hibernate: delete from enrollment where id=?
Hibernate: delete from student where id=?
Hibernate: select count(*) from enrollment e1_0
TASK4_ENROLLMENTS_AFTER_STUDENT_DELETE=0
Hibernate: select count(*) from course c1_0
TASK4_COURSES_AFTER_STUDENT_DELETE=1
```

**Reflection question:**
_What is the difference between `CascadeType.REMOVE` and `orphanRemoval = true`, and in which situation is each appropriate or dangerous?_

Your answer:
`CascadeType.REMOVE` will only delete children if the parent is deleted, whereas `orphanRemoval = true` will also delete the child if it is removed from the parent's list. These are safe to use for relationships the parent owns such as `Department` to `Employee`. However, they are dangerous to use for shared entities like `Course`, as if one of the `Student` rows is deleted, it would delete the course for everyone.

---

## Task 5 — Capstone Milestone 2 — ERD to JPA Entity Model

**Approach / explanation:**
I mapped the Vet Clinic ERD to JPA entities, with `Pet` owning `owner_id` and `Appointment` and `VaccinationRecord` owning the join foreign keys. All `@ManyToOne` relationships use `FetchType.LAZY`, and the cascades were set to `PERSIST` and `MERGE`.

**Code / endpoint(s) used (if applicable):**
```java
Owner owner = ownerRepository.save(new Owner("Maria Clara", "09171234567", "maria@example.com"));
Pet bantay = petRepository.save(new Pet("MC-9001", "Bantay", "Dog", "Aspin", owner));
Veterinarian vet = veterinarianRepository.save(new Veterinarian("Dr. Jose Rizal", "VET-101", "General"));
Vaccine vaccine = vaccineRepository.save(new Vaccine("Rabies", "Zoetis"));
appointmentRepository.save(new Appointment(LocalDateTime.now().plusDays(1), "Checkup", bantay, vet));
vaccinationRecordRepository.save(new VaccinationRecord(LocalDate.now(), LocalDate.now().plusYears(1), bantay, vaccine));
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
| ERD Table | Entity | Cardinality & Owning Side | FK / Join | Fetch & Cascade Justification |
| :--- | :--- | :--- | :--- | :--- |
| `owner` | `Owner` | 1:N `Pet` (inverse) | `mappedBy = "owner"` | `LAZY` list; `PERSIST, MERGE` only |
| `pet` | `Pet` | N:1 `Owner` (`Pet` owns) | `owner_id` | `LAZY` so pet queries don't always fetch owner |
| `veterinarian` | `Veterinarian` | 1:N `Appointment` (inverse) | `mappedBy = "veterinarian"` | `LAZY` list; `PERSIST, MERGE` only |
| `appointment` | `Appointment` | N:1 `Pet` & `Veterinarian` (`Appointment` owns) | `pet_id`, `vet_id` | `LAZY` on both FKs; no `REMOVE` to keep pet/vet |
| `vaccine` | `Vaccine` | 1:N `VaccinationRecord` (inverse) | `mappedBy = "vaccine"` | `LAZY` list; no `REMOVE` on shared catalog |
| `vaccination_record` | `VaccinationRecord` | N:M `Pet`–`Vaccine` (`VaccinationRecord` owns) | `pet_id`, `vaccine_id` | `LAZY` on both FKs; no `REMOVE` so `Vaccine` survives |

```text
Hibernate: insert into owner (email,full_name,phone) values (?,?,?)
Hibernate: insert into pet (breed,microchip_number,name,owner_id,species) values (?,?,?,?,?)
Hibernate: insert into veterinarian (full_name,license_number,specialization) values (?,?,?)
Hibernate: insert into vaccine (manufacturer,name) values (?,?)
Hibernate: insert into appointment (appointment_date,pet_id,reason,vet_id) values (?,?,?,?)
Hibernate: insert into vaccination_record (administered_date,next_due_date,pet_id,vaccine_id) values (?,?,?,?)
TASK5_OWNER=Maria Clara PET=Bantay
TASK5_APPOINTMENTS=1 VACCINATION_RECORDS=1
```

**Reflection question:**
_For one relationship in your capstone model, state the cardinality, the owning side, where the FK/join table lives, and why you chose that fetch and cascade setting._

Your answer:
`Pet` and `Vaccine` have an N:M relationship and are joined using the association entity `VaccinationRecord`. `VaccinationRecord` is the owner and has foreign keys of `pet_id` and `vaccine_id` in `vaccination_record`. I used `FetchType.LAZY` so that I don't get the full entities loaded unless needed, and I did not use `CascadeType.REMOVE` so that if the record was deleted, the `Vaccine` wouldn't be deleted as it is shared.

---

## Self-Check

- [x] All Week 9 work on the `week9` branch (project at branch root, no `week9/` folder)
- [x] This file completed and renamed `answers.md`
- [x] Repository link pasted into Moodle (no files uploaded)
