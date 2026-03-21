CREATE DATABASE IF NOT EXISTS sacstatehornet;
USE sacstatehornet;

CREATE TABLE IF NOT EXISTS majors (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS professors (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  department VARCHAR(120)
);

CREATE TABLE IF NOT EXISTS courses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  major_id BIGINT NOT NULL,
  code VARCHAR(20) NOT NULL,
  title VARCHAR(200) NOT NULL,
  CONSTRAINT fk_courses_major
    FOREIGN KEY (major_id) REFERENCES majors(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS course_professors (
  course_id BIGINT NOT NULL,
  professor_id BIGINT NOT NULL,
  PRIMARY KEY (course_id, professor_id),
  CONSTRAINT fk_cp_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_cp_prof
    FOREIGN KEY (professor_id) REFERENCES professors(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  course_id BIGINT NOT NULL,
  professor_id BIGINT NOT NULL,
  rating DECIMAL(2,1),
  grade VARCHAR(10),
  description TEXT,
  source_url VARCHAR(500),
  review_date DATETIME,
  scraped_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reviews_course
    FOREIGN KEY (course_id) REFERENCES courses(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_reviews_prof
    FOREIGN KEY (professor_id) REFERENCES professors(id)
    ON DELETE CASCADE
);

