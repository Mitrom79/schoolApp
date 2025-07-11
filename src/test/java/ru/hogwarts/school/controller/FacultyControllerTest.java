package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FacultyService facultyService;

    @Test
    void contextLoads() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    public void testGetFacultyInfo() throws Exception {
        Faculty mockFaculty = new Faculty();
        mockFaculty.setId(1L);
        mockFaculty.setName("Тестер");
        Mockito.when(facultyService.getFaculty(1L)).thenReturn(mockFaculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/1",
                Faculty.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Тестер");
    }

    @Test
    public void testFilterFaculties() throws Exception {
        Faculty faculty1 = new Faculty(1L, "красный", "Тестер1");
        Faculty faculty2 = new Faculty(2L, "красный", "Тестер2");
        Faculty faculty3 = new Faculty(3L, "синий", "Тестер3");


        Mockito.when(facultyService.filterFaculties(null, "красный"))
                .thenReturn(List.of(faculty1, faculty2));

        ResponseEntity<List<Faculty>> colorResponse = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/filter?color=красный",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        System.out.println("Color filter actual data:");
        colorResponse.getBody().forEach(f ->
                System.out.println("id=" + f.getId() + ", name=" + f.getName() + ", color=" + f.getColor()));

        assertThat(colorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(colorResponse.getBody())
                .hasSize(2)
                .extracting(Faculty::getColor)
                .containsExactlyInAnyOrder("красный", "красный");


        Mockito.when(facultyService.filterFaculties("Тестер1", null))
                .thenReturn(List.of(faculty1));

        ResponseEntity<List<Faculty>> nameResponse = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/filter?name=Тестер1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        System.out.println("Name filter actual data:");
        nameResponse.getBody().forEach(f ->
                System.out.println("id=" + f.getId() + ", name=" + f.getName() + ", color=" + f.getColor()));

        assertThat(nameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(nameResponse.getBody())
                .hasSize(1)
                .extracting(Faculty::getName)
                .containsExactly("Тестер1");


        Mockito.when(facultyService.filterFaculties("Тестер1", "красный"))
                .thenReturn(List.of(faculty1));

        ResponseEntity<List<Faculty>> combinedResponse = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/filter?name=Тестер1&color=красный",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        System.out.println("Combined filter actual data:");
        combinedResponse.getBody().forEach(f ->
                System.out.println("id=" + f.getId() + ", name=" + f.getName() + ", color=" + f.getColor()));

        assertThat(combinedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(combinedResponse.getBody())
                .hasSize(1)
                .extracting(Faculty::getId)
                .containsExactly(1L);
    }

    @Test
    public void testGetFacultyStudents() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Тестер1");

        Student student = new Student();
        student.setId(1L);
        student.setName("Первый Тестов");
        student.setFaculty(faculty);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFaculty(faculty);
        student2.setName("Второй Тестов");

        Student student3 = new Student();
        student3.setId(3L);
        student3.setFaculty(faculty);
        student3.setName("Третий Тестов");

        List<Student> mockStudents = List.of(student, student2, student3);
        Mockito.when(facultyService.getFacultyStudents(1L)).thenReturn(mockStudents);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/students/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody()).extracting(Student::getName)
                .containsExactly("Первый Тестов", "Второй Тестов", "Третий Тестов");
    }

    @Test
    public void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Тестер1");
        faculty.setColor("красный");

        Mockito.when(facultyService.addFaculty(Mockito.any(Faculty.class)))
                .thenReturn(faculty);

        Faculty response = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Тестер1");
    }

    @Test
    public void testEditFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Тестер1");
        faculty.setColor("синий");

        Faculty editedFaculty = new Faculty();
        editedFaculty.setId(1L);
        editedFaculty.setName("Тестер1");
        editedFaculty.setColor("красный");

        Mockito.when(facultyService.editFaculty(Mockito.any(Faculty.class)))
                .thenReturn(editedFaculty);

        HttpEntity<Faculty> entity = new HttpEntity<>(faculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                entity,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getColor()).isEqualTo("красный");
    }

    @Test
    public void testEditFaculty_notFound() {
        Faculty faculty = new Faculty();
        faculty.setId(9L);
        faculty.setName("Неведомый");

        Mockito.when(facultyService.editFaculty(Mockito.any(Faculty.class)))
                .thenReturn(null);

        HttpEntity<Faculty> entity = new HttpEntity<>(faculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                entity,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Удаляемый");

        Mockito.when(facultyService.deleteFaculty(1L)).thenReturn(faculty);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/1",
                HttpMethod.DELETE,
                null,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Удаляемый");
    }
}