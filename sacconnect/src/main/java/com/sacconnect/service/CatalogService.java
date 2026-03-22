package com.sacconnect.service;

import com.sacconnect.dto.response.CatalogCourseResponse;
import com.sacconnect.dto.response.CourseSectionResponse;
import com.sacconnect.dto.response.SectionMeetingResponse;
import com.sacconnect.model.Course;
import com.sacconnect.model.CourseSection;
import com.sacconnect.model.ProfessorOverallRating;
import com.sacconnect.model.SectionMeeting;
import com.sacconnect.repository.CourseRepository;
import com.sacconnect.repository.CourseSectionRepository;
import com.sacconnect.repository.ProfessorOverallRatingRepository;
import com.sacconnect.repository.SectionMeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {
    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    private static final BigDecimal DEFAULT_OVERALL_RATING = BigDecimal.valueOf(3.0);

    private final CourseRepository courseRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final SectionMeetingRepository sectionMeetingRepository;
    private final ProfessorOverallRatingRepository professorOverallRatingRepository;

    public CatalogService(
            CourseRepository courseRepository,
            CourseSectionRepository courseSectionRepository,
            SectionMeetingRepository sectionMeetingRepository,
            ProfessorOverallRatingRepository professorOverallRatingRepository
    ) {
        this.courseRepository = courseRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.sectionMeetingRepository = sectionMeetingRepository;
        this.professorOverallRatingRepository = professorOverallRatingRepository;
    }

    public List<CatalogCourseResponse> getCoursesByMajor(String majorName) {
        String normalizedMajor = majorName == null ? "" : majorName.trim();
        if (normalizedMajor.isBlank()) {
            return List.of();
        }

        List<Course> courses;
        try {
            courses = courseRepository.findByMajor_NameIgnoreCaseOrderByCodeAsc(normalizedMajor);
            log.debug("Catalog major lookup by relation: major={}, results={}", normalizedMajor, courses.size());
        } catch (RuntimeException ex) {
            log.warn("Catalog major lookup by relation failed for major={}. Falling back to course-code prefix lookup. Cause={}",
                    normalizedMajor, ex.getMessage());
            courses = List.of();
        }

        if (courses.isEmpty()) {
            String prefix = normalizedMajor.toUpperCase() + " ";
            courses = courseRepository.findByCodeStartingWithIgnoreCaseOrderByCodeAsc(prefix);
            log.debug("Catalog major fallback by code prefix: prefix='{}', results={}", prefix, courses.size());
        }

        return courses.stream().map(this::toCourseResponse).toList();
    }

    public List<CourseSectionResponse> getSectionsByCourseId(Long courseId) {
        return courseSectionRepository.findByCourseIdOrderBySectionNumberAsc(courseId).stream()
                .map(this::toSectionResponse)
                .toList();
    }

    private CatalogCourseResponse toCourseResponse(Course course) {
        return new CatalogCourseResponse(
                course.getId(),
                course.getCode(),
                course.getTitle()
        );
    }

    private CourseSectionResponse toSectionResponse(CourseSection section) {
        List<SectionMeetingResponse> meetings = sectionMeetingRepository
                .findBySectionIdOrderByDayOfWeekAscStartMinAsc(section.getId()).stream()
                .map(this::toMeetingResponse)
                .toList();

        Optional<ProfessorOverallRating> ratingOptional =
                professorOverallRatingRepository.findById(section.getProfessor().getId());

        BigDecimal overallRating = ratingOptional
                .map(ProfessorOverallRating::getOverallRating)
                .orElse(DEFAULT_OVERALL_RATING);
        Integer reviewCount = ratingOptional
                .map(ProfessorOverallRating::getReviewCount)
                .orElse(0);

        return new CourseSectionResponse(
                section.getId(),
                section.getSectionNumber(),
                section.getSourceCrn(),
                section.getLocation(),
                section.getModality(),
                section.getTerm(),
                section.getProfessor().getId(),
                section.getProfessor().getName(),
                overallRating,
                reviewCount,
                meetings
        );
    }

    private SectionMeetingResponse toMeetingResponse(SectionMeeting meeting) {
        return new SectionMeetingResponse(
                meeting.getDayOfWeek(),
                meeting.getStartMin(),
                meeting.getEndMin()
        );
    }
}
