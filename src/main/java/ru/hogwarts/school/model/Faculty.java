package ru.hogwarts.school.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties({"faculty", "avatar"})
@Entity
@Table(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("color")
    private String color;

    @OneToMany(mappedBy = "faculty")
    private List<Student> students;

    public Faculty() {}

    public Faculty(Long id, String color, String name) {

        this.id = id;
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Student> getStudents() { return students; }
}
