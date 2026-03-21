package com.sacconnect.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.sacconnect.model.Course;
import com.sacconnect.model.CourseProfessor;
import com.sacconnect.model.Major;
import com.sacconnect.model.Professor;
import com.sacconnect.repository.CourseProfessorRepository;
import com.sacconnect.repository.CourseRepository;
import com.sacconnect.repository.MajorRepository;
import com.sacconnect.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CsusCourseImportService {

    private static final String URL =
            "https://classschedule.webhost.csus.edu/api/cs/spring-2026/CSC";

    private final MajorRepository majorRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final CourseProfessorRepository courseProfessorRepository;
    private final ObjectMapper objectMapper;

    public CsusCourseImportService(
            MajorRepository majorRepository,
            CourseRepository courseRepository,
            ProfessorRepository professorRepository,
            CourseProfessorRepository courseProfessorRepository,
            ObjectMapper objectMapper
    ) {
        this.majorRepository = majorRepository;
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
        this.courseProfessorRepository = courseProfessorRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public int importCourses() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode courses = objectMapper.readTree(response.body());
        int importedCount = 0;

        for (JsonNode courseNode : courses) {
            JsonNode sections = courseNode.get("sections");
            if (sections == null || !sections.isArray()) {
                continue;
            }

            for (JsonNode section : sections) {
                String subjectCode = text(section, "subject_code");
                String catalogNumber = text(section, "catalog_number");
                String classTitle = text(section, "class_title");
                String instructorName = text(section, "instructor");

                if (subjectCode.isBlank() || catalogNumber.isBlank() || instructorName.isBlank()) {
                    continue;
                }

                Major major = majorRepository.findByName(subjectCode)
                        .orElseGet(() -> majorRepository.save(new Major(subjectCode)));

                String courseCode = subjectCode + " " + catalogNumber;

                Course course = courseRepository.findByCode(courseCode)
                        .orElseGet(() -> {
                            Course newCourse = new Course();
                            newCourse.setMajor(major);
                            newCourse.setCode(courseCode);
                            newCourse.setTitle(classTitle);
                            return courseRepository.save(newCourse);
                        });

                Professor professor = professorRepository.findByName(instructorName)
                        .orElseGet(() -> {
                            Professor newProfessor = new Professor();
                            newProfessor.setName(instructorName);
                            newProfessor.setDepartment(subjectCode);
                            return professorRepository.save(newProfessor);
                        });

                boolean relationshipExists = courseProfessorRepository
                        .findByCourseAndProfessor(course, professor)
                        .isPresent();

                if (!relationshipExists) {
                    CourseProfessor courseProfessor = new CourseProfessor();
                    courseProfessor.setCourse(course);
                    courseProfessor.setProfessor(professor);
                    courseProfessorRepository.save(courseProfessor);
                }

                importedCount++;
            }
        }

        return importedCount;
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? "" : value.asText().trim();
    }
}