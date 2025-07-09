package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentRepository studentRepository;
    @SpyBean
    private StudentService studentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetStudentInfo_found() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Гарри");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гарри"));
    }

    @Test
    public void testCreateStudent() throws Exception {
        Long id = 1L;
        String name = "Первый Тестов";

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", 11);

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(11);

        when(studentRepository.save(Mockito.any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    public void testEditStudent_success() throws Exception {
        Student request = new Student();
        request.setId(1L);
        request.setName("Старый");

        Student updated = new Student();
        updated.setId(1L);
        updated.setName("Обновлённый");
        Mockito.when(studentService.editStudent(Mockito.any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновлённый"));
    }

    @Test
    public void testEditStudent_notFound() throws Exception {
        Student request = new Student();
        request.setId(1L);
        request.setName("Никто");

        Mockito.when(studentService.editStudent(Mockito.any(Student.class))).thenReturn(null);
        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Student deleted = new Student();
        deleted.setId(1L);
        deleted.setName("Удаляемый");
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(deleted));
        Mockito.doNothing().when(studentRepository).deleteById(1L);
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Удаляемый"));
    }


    @Test
    public void testFilterForAge() throws Exception {
        Student s1 = new Student(12, "Гарри", 1L);
        Student s2 = new Student(12, "Рон", 2L);
        Mockito.when(studentService.filterForAge(12)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/student/filter").param("years", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testFindStudentsByAgeBetween() throws Exception {
        Student s1 = new Student(12, "Гарри", 1L);
        Student s2 = new Student(11, "Рон", 2L);
        Mockito.when(studentService.findStudentsByAgeBetween(10, 13)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/student/between")
                        .param("min", "10")
                        .param("max", "13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetStudentFaculty() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Гарри");

        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");

        student.setFaculty(faculty);

        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гриффиндор"));
    }
}
