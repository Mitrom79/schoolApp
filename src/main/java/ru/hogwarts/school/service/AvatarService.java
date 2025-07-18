package ru.hogwarts.school.service;

//import org.assertj.core.util.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;


import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {
    @Value("${path.to.avatars.folder}")
    private String avatarsDir;
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }
    @Transactional
    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentRepository.getById(studentId);
        Path filePath = Path.of(avatarsDir, student + "." + getExtensions(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        } catch (IOException e) {
            logger.error("Ошибка при загрузке файла на диск по пути {} для студента id={}", filePath, studentId, e);
            throw e;
        }
        try {
            Avatar avatar = findAvatar(studentId);
            avatar.setStudent(student);
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(avatarFile.getSize());
            avatar.setMediaType(avatarFile.getContentType());
            avatar.setData(avatarFile.getBytes());
            logger.debug("Avatar ID before save: " + avatar.getId());
            logger.debug("Saving avatar for student: " + student.getId());
            logger.debug("Path: " + filePath);
            logger.debug("Media type: " + avatarFile.getContentType());
            logger.debug("Размер данных: " + avatarFile.getBytes().length);
            logger.debug("Avatar id: " + avatar.getId());
            avatarRepository.save(avatar);
            logger.info("Was invoked method for upload Avatar");
        } catch (Exception e) {
            logger.error("Ошибка при сохранении аватара студента id={}", studentId, e);
            throw e;
        }

    }
    @Transactional
    private String getExtensions(String fileName) {
        int dotIndex = fileName != null ? fileName.lastIndexOf(".") : -1;
        logger.info("Was invoked method for get Extensions");
        return dotIndex != -1 ? fileName.substring(dotIndex + 1) : "unknown";
    }
    @Transactional
    public Avatar findAvatar(long studentId) {
        logger.info("Was invoked method for find Avatar");
        return avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar());
    }
    @Transactional
    public Page<Avatar> findAll(Integer offset, Integer limit){
        logger.info("Was invoked method for find All");
        return avatarRepository.findAll(PageRequest.of(offset, limit));
    }
}
