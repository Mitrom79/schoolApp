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
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FacultyRepository facultyRepository;
    @SpyBean
    private FacultyService facultyService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void testGetFacultyInfo_found() throws Exception {
        Faculty faculty = new Faculty(1L, "красный", "Тестер");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестер"));
    }

    @Test
    public void testCreateFaculty() throws Exception {
        Long id = 1L;
        String name = "Тестер";

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", name);
        facultyObject.put("color", "красный");

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor("красный");

        when(facultyRepository.save(Mockito.any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));

    }

    @Test
    public void testEditFaculty_success() throws Exception {
        Faculty request = new Faculty(1L, "красный", "Тестер1");
        Faculty updated = new Faculty(1L, "синий", "Тестер2");
        Mockito.when(facultyService.editFaculty(Mockito.any(Faculty.class))).thenReturn(updated);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("синий"));
    }

    @Test
    public void testEditFaculty_notFound() throws Exception {
        Faculty request = new Faculty(1L, "красный", "Никто");
        Mockito.when(facultyService.editFaculty(Mockito.any(Faculty.class))).thenReturn(null);
        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        Faculty deleted = new Faculty(1L, "красный", "Удаляемый");
        Mockito.when(facultyRepository.findById(1L)).thenReturn(Optional.of(deleted));
        Mockito.doNothing().when(facultyRepository).deleteById(1L);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Удаляемый"));
    }


    @Test
    void filterByName() throws Exception {
        Faculty f1 = new Faculty(1L, "красный", "Тестер1");
        Mockito.when(facultyService.filterFaculties("Тестер1", null))
                .thenReturn(List.of(f1));

        mockMvc.perform(get("/faculty/filter")
                        .param("name", "Тестер1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Тестер1"))
                .andExpect(jsonPath("$[0].color").value("красный"));
    }

    // Тест фильтрации по цвету
    @Test
    void filterByColor() throws Exception {
        Faculty f1 = new Faculty(1L, "красный", "Тестер1");
        Faculty f2 = new Faculty(2L, "красный", "Тестер2");
        Mockito.when(facultyService.filterFaculties(null, "красный"))
                .thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/faculty/filter")
                        .param("color", "красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].color").value("красный"))
                .andExpect(jsonPath("$[1].color").value("красный"));
    }

    // Тест фильтрации по имени и цвету
    @Test
    void filterByNameAndColor() throws Exception {
        Faculty f1 = new Faculty(1L, "красный", "Тестер1");
        Mockito.when(facultyService.filterFaculties("Тестер1", "красный"))
                .thenReturn(List.of(f1));

        mockMvc.perform(get("/faculty/filter")
                        .param("name", "Тестер1")
                        .param("color", "красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Тестер1"))
                .andExpect(jsonPath("$[0].color").value("красный"));
    }

    @Test
    public void testGetFacultyStudents() throws Exception {
        Faculty f1 = new Faculty(1L, "красный", "Тестер1");
        Student s1 = new Student(1, "Гарри", 1L);
        Student s2 = new Student(13, "Рон", 2L);
        s1.setFaculty(f1);
        s2.setFaculty(f1);
        Mockito.when(facultyRepository.findById(1L)).thenReturn(Optional.of(f1));
        Mockito.when(facultyService.getFacultyStudents(1L)).thenReturn(List.of(s1, s2));
        mockMvc.perform(get("/faculty/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
