package com.sacconnect.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.sacconnect.model.Course;
import com.sacconnect.model.CourseProfessor;
import com.sacconnect.model.CourseSection;
import com.sacconnect.model.Major;
import com.sacconnect.model.Professor;
import com.sacconnect.model.SectionMeeting;
import com.sacconnect.repository.CourseProfessorRepository;
import com.sacconnect.repository.CourseRepository;
import com.sacconnect.repository.CourseSectionRepository;
import com.sacconnect.repository.MajorRepository;
import com.sacconnect.repository.ProfessorRepository;
import com.sacconnect.repository.SectionMeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CsusCourseImportService {

    private static final String URL =
            "https://classschedule.webhost.csus.edu/api/cs/spring-2026/CSC";

    private final MajorRepository majorRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final CourseProfessorRepository courseProfessorRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final SectionMeetingRepository sectionMeetingRepository;
    private final ObjectMapper objectMapper;

    public CsusCourseImportService(
            MajorRepository majorRepository,
            CourseRepository courseRepository,
            ProfessorRepository professorRepository,
            CourseProfessorRepository courseProfessorRepository,
            CourseSectionRepository courseSectionRepository,
            SectionMeetingRepository sectionMeetingRepository,
            ObjectMapper objectMapper
    ) {
        this.majorRepository = majorRepository;
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
        this.courseProfessorRepository = courseProfessorRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.sectionMeetingRepository = sectionMeetingRepository;
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
                String classSection = text(section, "class_section");
                String classNumber = text(section, "class_number");
                String termCode = text(section, "term_code");
                String building = text(section, "building");
                String room = text(section, "room");
                String component = text(section, "component");
                String days = text(section, "days");
                String startTime = text(section, "start_time");
                String endTime = text(section, "end_time");

                if (subjectCode.isBlank() || catalogNumber.isBlank() || instructorName.isBlank()) {
                    continue;
                }
                if (days.isBlank() || startTime.isBlank() || endTime.isBlank()) {
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

                CourseSection courseSection = courseSectionRepository.findBySourceCrn(classNumber)
                        .orElseGet(() -> {
                            CourseSection newSection = new CourseSection();
                            newSection.setSourceCrn(classNumber);
                            return newSection;
                        });
                courseSection.setCourse(course);
                courseSection.setProfessor(professor);
                courseSection.setSectionNumber(classSection);
                courseSection.setLocation(joinLocation(building, room));
                courseSection.setModality(component);
                courseSection.setTerm(termCode);
                courseSection = courseSectionRepository.save(courseSection);

                sectionMeetingRepository.deleteBySectionId(courseSection.getId());

                int startMin = parseTimeToMinutes(startTime);
                int endMin = parseTimeToMinutes(endTime);
                if (endMin > startMin) {
                    for (String day : parseDays(days)) {
                        SectionMeeting meeting = new SectionMeeting();
                        meeting.setSection(courseSection);
                        meeting.setDayOfWeek(day);
                        meeting.setStartMin(startMin);
                        meeting.setEndMin(endMin);
                        sectionMeetingRepository.save(meeting);
                    }
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

    private String joinLocation(String building, String room) {
        if (building.isBlank() && room.isBlank()) {
            return null;
        }
        if (building.isBlank()) {
            return room;
        }
        if (room.isBlank()) {
            return building;
        }
        return building + " " + room;
    }

    private int parseTimeToMinutes(String time) {
        // Format example: 0200PM / 1150AM
        String normalized = time == null ? "" : time.trim().toUpperCase(Locale.US);
        if (normalized.length() != 6) {
            return -1;
        }
        int hour = Integer.parseInt(normalized.substring(0, 2));
        int minute = Integer.parseInt(normalized.substring(2, 4));
        String meridiem = normalized.substring(4, 6);

        if (hour == 12) {
            hour = 0;
        }
        if ("PM".equals(meridiem)) {
            hour += 12;
        }

        return hour * 60 + minute;
    }

    private List<String> parseDays(String compactDays) {
        List<String> days = new ArrayList<>();
        for (char c : compactDays.toUpperCase(Locale.US).toCharArray()) {
            switch (c) {
                case 'M' -> days.add("MON");
                case 'T' -> days.add("TUE");
                case 'W' -> days.add("WED");
                case 'R' -> days.add("THU");
                case 'F' -> days.add("FRI");
                case 'S' -> days.add("SAT");
                case 'U' -> days.add("SUN");
                default -> {
                    // ignore unknown day markers
                }
            }
        }
        return days;
    }
}
