package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.hogwarts.school.service.StudentService;


import java.util.Collection;
import java.util.List;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private StudentService studentService;

    @Test
    void contextLoads() throws Exception {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void testGetStudentById() throws Exception {
        Student mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setName("Тест Тестов");

        Mockito.when(studentService.getStudent(1L)).thenReturn(mockStudent);
        ResponseEntity<Student> response = this.restTemplate
                .getForEntity("http://localhost:" + port + "/student/1", Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Тест Тестов");
    }

    @Test
    public void testFilterForAge() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Первый Тестов");
        student.setAge(11);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setAge(11);
        student2.setName("Второй Тестов");

        Student student3 = new Student();
        student3.setId(3L);
        student3.setAge(11);
        student3.setName("Третий Тестов");

        Collection<Student> mockCollection = List.of(student, student2, student3);

        Mockito.when(studentService.filterForAge(11)).thenReturn(mockCollection);

        ResponseEntity<List<Object>> response = restTemplate.
                exchange("http://localhost:" + port + "/student/filter?years=11",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object>>() {
                }
        );

        ObjectMapper mapper = new ObjectMapper();
        List<Student> students = response.getBody().stream()
                .map(obj -> mapper.convertValue(obj, Student.class))
                .collect(Collectors.toList());


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students).hasSize(3);
        assertThat(students).extracting(Student::getName)
                .contains("Первый Тестов", "Второй Тестов", "Третий Тестов");
    }

    @Test
    public void testFindStudentsByAgeBetween() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Первый Тестов");
        student.setAge(11);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setAge(12);
        student2.setName("Второй Тестов");

        Student student3 = new Student();
        student3.setId(3L);
        student3.setAge(13);
        student3.setName("Третий Тестов");
        Collection<Student> mockStudents = List.of(student, student2, student3);

        Mockito.when(studentService.findStudentsByAgeBetween(10, 14))
                .thenReturn(mockStudents);

        ResponseEntity<List<Object>> response = restTemplate.
                exchange("http://localhost:" + port + "/student/between?min=10&max=14",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object>>() {
                }
        );
        ObjectMapper mapper = new ObjectMapper();
        List<Student> students = response.getBody().stream()
                .map(obj -> mapper.convertValue(obj, Student.class))
                .collect(Collectors.toList());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students).hasSize(3);
        assertThat(students).extracting(Student::getName)
                .contains("Первый Тестов", "Второй Тестов", "Третий Тестов");
    }

    @Test
    public void testGetStudentFaculty() {
        Faculty facultyTest = new Faculty();
        facultyTest.setName("Тестер");
        Mockito.when(studentService.getStudentFaculty(1)).thenReturn(facultyTest);
        ResponseEntity<Faculty> response = restTemplate.
                getForEntity("http://localhost:" + port + "/student/faculty/1", Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Тестер");
    }


    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Первый Тестов");
        student.setAge(11);
        Mockito.when(studentService.addStudent(Mockito.any(Student.class)))
                .thenReturn(student);

        Student response = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Первый Тестов");
    }


    @Test
    public void testEditStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Первый Тестов");
        student.setAge(11);
        Student editedStudent = new Student();
        editedStudent.setId(1L);
        editedStudent.setName("Первый Тестов");
        editedStudent.setAge(12);
        Mockito.when(studentService.editStudent(Mockito.any(Student.class)))
                .thenReturn(editedStudent);
        HttpEntity<Student> entity = new HttpEntity<>(student);
        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                entity,
                Student.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response).isNotNull();
        assertThat(response.getBody().getAge()).isEqualTo(12);
    }

    @Test
    public void testEditStudent_notFound() throws Exception {
        Student student = new Student();
        student.setId(8L);
        student.setName("Первый Найденный");
        Mockito.when(studentService.editStudent(Mockito.any(Student.class))).thenReturn(null);
        HttpEntity<Student> entity = new HttpEntity<>(student);
        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                entity,
                Student.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Первый Удаляемый");
        Mockito.when(studentService.deleteStudent(1L)).thenReturn(student);
        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/1",
                HttpMethod.DELETE,
                null,
                Student.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Первый Удаляемый");
    }

    @Test
    public void testPrintParallelStudents() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/student/print-parallel", String.class);
        Mockito.verify(studentService, times(1)).printParallelStudents();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Имена студентов выведены в консоль");
    }

    @Test
    public void testPrintParallelStudentsSynchronized() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/student/print-synchronized", String.class);
        Mockito.verify(studentService, times(1)).printParallelStudentsSynchronized();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Имена студентов выведены в консоль");
    }
}
