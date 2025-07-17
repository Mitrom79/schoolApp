--liquibase formatted sql
--changeset mitrom:1-index-student-name
CREATE INDEX student_name_index ON student (name);

--changeset mitrom:2-index-faculty-name-color
CREATE INDEX faculty_name_color_index ON faculty(name, color);