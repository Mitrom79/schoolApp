package ru.hogwarts.school.model;

import jakarta.persistence.*;

@Entity
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath;
    private long fileSize;
    private String mediaType;
    private byte[] data;
    @OneToOne
    @JoinColumn(name = "student_id", unique = true)
    private Student student;

    public Avatar() {
    }

    public Avatar(Student student, byte[] data, String mediaType, long fileSize, String filePath, Long id) {
        this.student = student;
        this.data = data;
        this.mediaType = mediaType;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public byte[] getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setId(Long id) {
        this.id = id;
    }
}