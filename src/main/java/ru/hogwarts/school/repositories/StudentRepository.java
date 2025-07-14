package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;
import java.util.Collection;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{
    Collection<Student> findByAge(int age);

    Collection<Student> findByAgeBetween(int min, int max);

    @Query(value = "SELECT COUNT(id) FROM student", nativeQuery = true)
    Integer getNumberOfStudents();

    @Query(value = "SELECT AVG(age) from student", nativeQuery = true)
    Integer getAvgOfStudents();

    @Query(value = "SELECT * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<StudentByName> getStudentByName();
}
