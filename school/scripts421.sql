ALTER TABLE student
     ADD CONSTRAINT age_constraint CHECK (age > 15);

ALTER TABLE student
    ALTER COLUMN name SET NOT NULL;
ALTER TABLE student
    ADD CONSTRAINT unique_name UNIQUE(name);

ALTER TABLE faculty
    ADD CONSTRAINT name_color_unique UNIQUE(name, color);

ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;