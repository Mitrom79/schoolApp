package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long>{
    Collection<Faculty> findByNameIgnoreCaseAndColorIgnoreCase(
        @Param("name") String name,
        @Param("color") String color
);

    Collection<Faculty> findByNameIgnoreCase(String name);

    Collection<Faculty> findByColorIgnoreCase(String color);
}

