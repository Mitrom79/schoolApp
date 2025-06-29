package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.*;


@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Transactional
    public Faculty addFaculty(Faculty newFaculty) {
        return facultyRepository.save(newFaculty);
    }

    @Transactional
    public Faculty getFaculty(long id) {
        return facultyRepository.findById(id).get();

    }

    @Transactional
    public Faculty editFaculty(Faculty updateFaculty) {
        return facultyRepository.save(updateFaculty);
    }

    @Transactional
    public Faculty deleteFaculty(long id) {
        Faculty faculty = facultyRepository.findById(id).get();
        facultyRepository.deleteById(id);
        return faculty;
    }

    @Transactional
    public Collection<Faculty> filterForColor(String color) {
        return facultyRepository.findByColor(color);
    }
}
