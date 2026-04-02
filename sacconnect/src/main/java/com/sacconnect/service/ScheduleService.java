package com.sacconnect.service;

import com.sacconnect.dto.request.ScheduleGenerateRequest;
import com.sacconnect.dto.response.ScheduleGenerateResponse;
import com.sacconnect.dto.response.ScheduleOptionResponse;
import com.sacconnect.dto.response.ScheduleSectionResponse;
import com.sacconnect.dto.response.SectionMeetingResponse;
import com.sacconnect.model.CourseSection;
import com.sacconnect.model.ProfessorOverallRating;
import com.sacconnect.model.SectionMeeting;
import com.sacconnect.repository.CourseSectionRepository;
import com.sacconnect.repository.ProfessorOverallRatingRepository;
import com.sacconnect.repository.SectionMeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private static final int DEFAULT_PREFERRED_START = 9 * 60;
    private static final int DEFAULT_PREFERRED_END = 17 * 60;
    private static final int DEFAULT_TOP_N = 3;
    private static final int MAX_TOP_N = 10;
    private static final int MAX_COMBINATIONS = 50000;
    private static final BigDecimal DEFAULT_PROFESSOR_RATING = BigDecimal.valueOf(3.0);

    private final CourseSectionRepository courseSectionRepository;
    private final SectionMeetingRepository sectionMeetingRepository;
    private final ProfessorOverallRatingRepository professorOverallRatingRepository;

    public ScheduleService(
            CourseSectionRepository courseSectionRepository,
            SectionMeetingRepository sectionMeetingRepository,
            ProfessorOverallRatingRepository professorOverallRatingRepository
    ) {
        this.courseSectionRepository = courseSectionRepository;
        this.sectionMeetingRepository = sectionMeetingRepository;
        this.professorOverallRatingRepository = professorOverallRatingRepository;
    }

    @Transactional(readOnly = true)
    public ScheduleGenerateResponse generateSchedules(ScheduleGenerateRequest request) {
        List<Long> requestedCourseIds = normalizeCourseIds(request.getCourseIds());
        log.debug("Normalized course ids: {}", requestedCourseIds);
        if (requestedCourseIds.isEmpty()) {
            throw new IllegalArgumentException("courseIds is required.");
        }

        int preferredStart = request.getPreferredStartMin() == null
                ? DEFAULT_PREFERRED_START : request.getPreferredStartMin();
        int preferredEnd = request.getPreferredEndMin() == null
                ? DEFAULT_PREFERRED_END : request.getPreferredEndMin();
        if (preferredStart < 0 || preferredEnd > 24 * 60 || preferredStart >= preferredEnd) {
            throw new IllegalArgumentException("preferredStartMin/preferredEndMin is invalid.");
        }

        int topN = request.getTopNPerCategory() == null ? DEFAULT_TOP_N : request.getTopNPerCategory();
        if (topN <= 0) {
            topN = DEFAULT_TOP_N;
        }
        topN = Math.min(topN, MAX_TOP_N);
        log.debug("Schedule generation parameters: preferredStart={}, preferredEnd={}, topN={}",
                preferredStart, preferredEnd, topN);

        List<List<SectionOption>> optionsByCourse = buildOptionsByCourse(requestedCourseIds);
        if (log.isDebugEnabled()) {
            for (int i = 0; i < requestedCourseIds.size(); i++) {
                log.debug("Course {} has {} schedulable section options", requestedCourseIds.get(i), optionsByCourse.get(i).size());
            }
        }
        List<Long> emptyCourseIds = new ArrayList<>();
        for (int i = 0; i < requestedCourseIds.size(); i++) {
            if (optionsByCourse.get(i).isEmpty()) {
                emptyCourseIds.add(requestedCourseIds.get(i));
            }
        }
        if (!emptyCourseIds.isEmpty()) {
            throw new IllegalArgumentException("No schedulable sections found for courseIds: " + emptyCourseIds);
        }

        List<EvaluatedSchedule> allValidSchedules = new ArrayList<>();
        GenerationState state = new GenerationState();
        backtrack(
                0,
                optionsByCourse,
                new ArrayList<>(),
                allValidSchedules,
                state,
                preferredStart,
                preferredEnd
        );
        log.debug("Backtracking finished: validSchedules={}, truncated={}", allValidSchedules.size(), state.truncated);

        Comparator<EvaluatedSchedule> overallComparator = Comparator
                .comparingDouble(EvaluatedSchedule::overallScore)
                .thenComparingInt(EvaluatedSchedule::distinctDays)
                .thenComparingInt(EvaluatedSchedule::totalGapMinutes)
                .thenComparing((EvaluatedSchedule e) -> e.totalProfessorRating().negate());

        Comparator<EvaluatedSchedule> leastDaysComparator = Comparator
                .comparingInt(EvaluatedSchedule::distinctDays)
                .thenComparingInt(EvaluatedSchedule::totalGapMinutes)
                .thenComparingInt(EvaluatedSchedule::outsidePreferredMinutes)
                .thenComparing((EvaluatedSchedule e) -> e.totalProfessorRating().negate());

        Comparator<EvaluatedSchedule> bestTimeComparator = Comparator
                .comparingInt(EvaluatedSchedule::outsidePreferredMinutes)
                .thenComparingInt(EvaluatedSchedule::totalGapMinutes)
                .thenComparingInt(EvaluatedSchedule::distinctDays)
                .thenComparing((EvaluatedSchedule e) -> e.totalProfessorRating().negate());

        Comparator<EvaluatedSchedule> smallestGapComparator = Comparator
                .comparingInt(EvaluatedSchedule::totalGapMinutes)
                .thenComparingInt(EvaluatedSchedule::distinctDays)
                .thenComparingInt(EvaluatedSchedule::outsidePreferredMinutes)
                .thenComparing((EvaluatedSchedule e) -> e.totalProfessorRating().negate());

        return new ScheduleGenerateResponse(
                allValidSchedules.size(),
                state.truncated,
                topSchedules(allValidSchedules, overallComparator, topN),
                topSchedules(allValidSchedules, leastDaysComparator, topN),
                topSchedules(allValidSchedules, bestTimeComparator, topN),
                topSchedules(allValidSchedules, smallestGapComparator, topN)
        );
    }

    private List<List<SectionOption>> buildOptionsByCourse(List<Long> requestedCourseIds) {
        List<List<SectionOption>> optionsByCourse = new ArrayList<>();

        for (Long courseId : requestedCourseIds) {
            List<CourseSection> sections = courseSectionRepository.findByCourseIdOrderBySectionNumberAsc(courseId);
            List<Long> sectionIds = sections.stream().map(CourseSection::getId).toList();

            Map<Long, List<SectionMeeting>> meetingsBySection = new HashMap<>();
            for (Long sectionId : sectionIds) {
                List<SectionMeeting> meetings = sectionMeetingRepository.findBySectionIdOrderByDayOfWeekAscStartMinAsc(sectionId);
                if (!meetings.isEmpty()) {
                    meetingsBySection.put(sectionId, meetings);
                }
            }

            Set<Long> professorIds = sections.stream()
                    .map(section -> section.getProfessor().getId())
                    .collect(Collectors.toSet());

            Map<Long, ProfessorOverallRating> ratingsByProfessor = professorOverallRatingRepository
                    .findAllById(professorIds)
                    .stream()
                    .collect(Collectors.toMap(ProfessorOverallRating::getProfessorId, rating -> rating));

            List<SectionOption> options = new ArrayList<>();
            for (CourseSection section : sections) {
                List<SectionMeeting> meetings = meetingsBySection.get(section.getId());
                if (meetings == null || meetings.isEmpty()) {
                    continue;
                }

                ProfessorOverallRating rating = ratingsByProfessor.get(section.getProfessor().getId());
                BigDecimal overall = rating == null ? DEFAULT_PROFESSOR_RATING : rating.getOverallRating();
                int reviewCount = rating == null ? 0 : rating.getReviewCount();

                options.add(new SectionOption(section, meetings, overall, reviewCount));
            }

            optionsByCourse.add(options);
        }

        return optionsByCourse;
    }

    private void backtrack(
            int courseIndex,
            List<List<SectionOption>> optionsByCourse,
            List<SectionOption> current,
            List<EvaluatedSchedule> allValidSchedules,
            GenerationState state,
            int preferredStart,
            int preferredEnd
    ) {
        if (state.truncated) {
            return;
        }
        if (courseIndex == optionsByCourse.size()) {
            allValidSchedules.add(evaluate(current, preferredStart, preferredEnd));
            if (allValidSchedules.size() >= MAX_COMBINATIONS) {
                state.truncated = true;
                log.warn("Schedule generation truncated at MAX_COMBINATIONS={}", MAX_COMBINATIONS);
            }
            return;
        }

        for (SectionOption candidate : optionsByCourse.get(courseIndex)) {
            if (hasOverlap(current, candidate)) {
                log.trace("Skipping section {} due to overlap", candidate.section().getId());
                continue;
            }
            current.add(candidate);
            backtrack(
                    courseIndex + 1,
                    optionsByCourse,
                    current,
                    allValidSchedules,
                    state,
                    preferredStart,
                    preferredEnd
            );
            current.remove(current.size() - 1);
            if (state.truncated) {
                return;
            }
        }
    }

    private boolean hasOverlap(List<SectionOption> current, SectionOption candidate) {
        for (SectionOption existing : current) {
            if (overlaps(existing.meetings(), candidate.meetings())) {
                return true;
            }
        }
        return false;
    }

    private boolean overlaps(List<SectionMeeting> left, List<SectionMeeting> right) {
        for (SectionMeeting l : left) {
            for (SectionMeeting r : right) {
                if (!l.getDayOfWeek().equals(r.getDayOfWeek())) {
                    continue;
                }
                if (l.getStartMin() < r.getEndMin() && r.getStartMin() < l.getEndMin()) {
                    return true;
                }
            }
        }
        return false;
    }

    private EvaluatedSchedule evaluate(List<SectionOption> sections, int preferredStart, int preferredEnd) {
        Map<String, List<TimeBlock>> byDay = new HashMap<>();
        BigDecimal totalRating = BigDecimal.ZERO;
        List<ScheduleSectionResponse> sectionResponses = new ArrayList<>();

        for (SectionOption section : sections) {
            totalRating = totalRating.add(section.professorRating());

            List<SectionMeetingResponse> meetingResponses = section.meetings().stream()
                    .map(m -> new SectionMeetingResponse(m.getDayOfWeek(), m.getStartMin(), m.getEndMin()))
                    .toList();

            sectionResponses.add(new ScheduleSectionResponse(
                    section.section().getCourse().getId(),
                    section.section().getCourse().getCode(),
                    section.section().getCourse().getTitle(),
                    section.section().getId(),
                    section.section().getSectionNumber(),
                    section.section().getSourceCrn(),
                    section.section().getProfessor().getId(),
                    section.section().getProfessor().getName(),
                    section.professorRating(),
                    section.reviewCount(),
                    meetingResponses
            ));

            for (SectionMeeting meeting : section.meetings()) {
                byDay.computeIfAbsent(meeting.getDayOfWeek(), d -> new ArrayList<>())
                        .add(new TimeBlock(meeting.getStartMin(), meeting.getEndMin()));
            }
        }

        int distinctDays = byDay.size();
        int totalGapMinutes = 0;
        int outsidePreferredMinutes = 0;

        for (List<TimeBlock> dayBlocks : byDay.values()) {
            dayBlocks.sort(Comparator.comparingInt(TimeBlock::startMin));

            for (int i = 1; i < dayBlocks.size(); i++) {
                int gap = dayBlocks.get(i).startMin() - dayBlocks.get(i - 1).endMin();
                if (gap > 0) {
                    totalGapMinutes += gap;
                }
            }

            for (TimeBlock block : dayBlocks) {
                outsidePreferredMinutes += outsideWindowMinutes(block, preferredStart, preferredEnd);
            }
        }

        double score = (outsidePreferredMinutes * 3.0)
                + (totalGapMinutes * 1.0)
                + (distinctDays * 90.0)
                - (totalRating.doubleValue() * 20.0);

        totalRating = totalRating.setScale(1, RoundingMode.HALF_UP);

        return new EvaluatedSchedule(
                score,
                distinctDays,
                totalGapMinutes,
                outsidePreferredMinutes,
                totalRating,
                sectionResponses
        );
    }

    private int outsideWindowMinutes(TimeBlock block, int preferredStart, int preferredEnd) {
        int outside = 0;
        if (block.startMin() < preferredStart) {
            outside += preferredStart - block.startMin();
        }
        if (block.endMin() > preferredEnd) {
            outside += block.endMin() - preferredEnd;
        }
        return Math.max(outside, 0);
    }

    private List<ScheduleOptionResponse> topSchedules(
            List<EvaluatedSchedule> all,
            Comparator<EvaluatedSchedule> comparator,
            int topN
    ) {
        List<ScheduleOptionResponse> result = all.stream()
                .sorted(comparator)
                .limit(topN)
                .map(this::toResponse)
                .toList();
        if (log.isDebugEnabled()) {
            log.debug("Computed top {} schedules from {}", result.size(), all.size());
            for (int i = 0; i < result.size(); i++) {
                ScheduleOptionResponse option = result.get(i);
                log.debug("Top[{}]: score={}, days={}, gaps={}, outsideWindow={}, totalRating={}",
                        i,
                        option.getScore(),
                        option.getDistinctDays(),
                        option.getTotalGapMinutes(),
                        option.getOutsidePreferredMinutes(),
                        option.getTotalProfessorRating());
            }
        }
        return result;
    }

    private ScheduleOptionResponse toResponse(EvaluatedSchedule evaluated) {
        return new ScheduleOptionResponse(
                evaluated.overallScore(),
                evaluated.distinctDays(),
                evaluated.totalGapMinutes(),
                evaluated.outsidePreferredMinutes(),
                evaluated.totalProfessorRating(),
                evaluated.sections()
        );
    }

    private List<Long> normalizeCourseIds(Collection<Long> rawCourseIds) {
        if (rawCourseIds == null) {
            return List.of();
        }
        return new ArrayList<>(new LinkedHashSet<>(rawCourseIds.stream()
                .filter(id -> id != null && id > 0)
                .toList()));
    }

    private record TimeBlock(int startMin, int endMin) {
    }

    private record SectionOption(
            CourseSection section,
            List<SectionMeeting> meetings,
            BigDecimal professorRating,
            int reviewCount
    ) {
    }

    private record EvaluatedSchedule(
            double overallScore,
            int distinctDays,
            int totalGapMinutes,
            int outsidePreferredMinutes,
            BigDecimal totalProfessorRating,
            List<ScheduleSectionResponse> sections
    ) {
    }

    private static class GenerationState {
        private boolean truncated = false;
    }
}
