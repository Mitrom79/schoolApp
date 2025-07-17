SELECT student.name, student.age, faculty.name
FROM student
         LEFT JOIN faculty ON student.faculty_id = faculty.id;

SELECT student.name, student.age, avatar.file_path
FROM student
         INNER JOIN avatar ON student.avatar_id = avatar.id;