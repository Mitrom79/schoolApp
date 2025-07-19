package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentByName;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.*;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    public StudentService(StudentRepository studentRepository) {

        this.studentRepository = studentRepository;
    }

    @Transactional
    public Student addStudent(Student newStudent) {
        logger.info("Was invoked method for add Student");
        return studentRepository.save(newStudent);
    }

    @Transactional
    public Student getStudent(long id) {
        logger.info("Was invoked method for get Student");
        return studentRepository.findById(id).get();
    }

    @Transactional
    public Student editStudent(Student updateStudent) {
        logger.info("Was invoked method for edit Student");
        return studentRepository.save(updateStudent);
    }

    @Transactional
    public Student deleteStudent(long id) {
        Student student = studentRepository.findById(id).get();
        studentRepository.deleteById(id);
        logger.info("Was invoked method for delete Student");
        return student;
    }

    @Transactional
    public Collection<Student> filterForAge(int years) {
        logger.info("Was invoked method for filter For Age");
        return studentRepository.findByAge(years);
    }

    @Transactional
    public Collection<Student> findStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find Students By Age Between");
        return studentRepository.findByAgeBetween(min, max);
    }

    @Transactional
    public Faculty getStudentFaculty(long id) {
        logger.info("Was invoked method for get Student Faculty");
        return getStudent(id).getFaculty();
    }

    @Transactional
    public Integer getNumberOfStudents() {
        logger.info("Was invoked method for get Number Of Students");
        return studentRepository.getNumberOfStudents();
    }

    @Transactional
    public Integer getAvgOfStudents() {
        logger.info("Was invoked method for get Avg Of Students");
        return studentRepository.getAvgOfStudents();
    }

    @Transactional
    public List<StudentByName> getStudentByName() {
        logger.info("Was invoked method for get Student By Name");
        return studentRepository.getStudentByName();
    }

    public Collection<String> getWithNameOnA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .toList();
    }

    public Double getAVGAge() {
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);
    }

    public void printParallelStudents() {
        List<Student> list = studentRepository.findAll();
        if (list.size() < 6) {
            logger.info("Для демонстрации тремя потоками студентов недостаточно");
            return;
        }

        System.out.println(Thread.currentThread().getName() + ": " + list.get(0).getName());
        System.out.println(Thread.currentThread().getName() + ": " + list.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + list.get(2).getName());
            System.out.println(Thread.currentThread().getName() + ": " + list.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + list.get(4).getName());
            System.out.println(Thread.currentThread().getName() + ": " + list.get(5).getName());
        });
        thread1.start();
        thread2.start();


        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Поток прерван:", e);
        }

    }

    public void printParallelStudentsSynchronized() {
        List<Student> list = studentRepository.findAll();
        if (list.size() < 6) {
            logger.info("Для демонстрации тремя потоками студентов недостаточно");
            return;
        }

        printStudentName(list.get(0).getName());
        printStudentName(list.get(1).getName());

        Thread thread1 = new Thread(() -> {
            printStudentName(list.get(2).getName());
            printStudentName(list.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            printStudentName(list.get(4).getName());
            printStudentName(list.get(5).getName());
        });
        thread1.start();
        thread2.start();


        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            logger.error("Поток был прерван :", e);
        }
    }

    private synchronized void printStudentName(String name) {
        System.out.println(Thread.currentThread().getName() + ": " + name);
    }


}
