package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.*;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {

        this.studentRepository = studentRepository;
    }

    @Transactional
    public Student addStudent(Student newStudent) {

        return studentRepository.save(newStudent);
    }

    @Transactional
    public Student getStudent(long id) {

        return studentRepository.findById(id).get();
    }

    @Transactional
    public Student editStudent(Student updateStudent) {

        return studentRepository.save(updateStudent);
    }

    @Transactional
    public Student deleteStudent(long id) {
        Student student = studentRepository.findById(id).get();
        studentRepository.deleteById(id);
        return student;
    }

    @Transactional
    public Collection<Student> filterForAge(int years) {

        return studentRepository.findByAge(years);
    }
    @Transactional
    public Collection<Student> findStudentsByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    @Transactional
    public Faculty getStudentFaculty(long id) {
        return getStudent(id).getFaculty();
    }


}
