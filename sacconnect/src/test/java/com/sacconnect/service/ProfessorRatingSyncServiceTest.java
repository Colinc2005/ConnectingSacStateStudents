package com.sacconnect.service;

import com.sacconnect.model.Professor;
import com.sacconnect.model.ProfessorOverallRating;
import com.sacconnect.repository.CourseProfessorRepository;
import com.sacconnect.repository.ProfessorOverallRatingRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfessorRatingSyncServiceTest {

    @Test
    void syncAllProfessorRatings_usesFallbackWhenNoTeacherMatchExists() throws Exception {
        CourseProfessorRepository courseProfessorRepository = mock(CourseProfessorRepository.class);
        ProfessorOverallRatingRepository professorOverallRatingRepository = mock(ProfessorOverallRatingRepository.class);
        RateMyProfessorClient rateMyProfessorClient = mock(RateMyProfessorClient.class);

        Professor professor = new Professor();
        professor.setId(7L);
        professor.setName("Jane Smith");

        when(courseProfessorRepository.findDistinctProfessors()).thenReturn(List.of(professor));
        when(rateMyProfessorClient.findSchoolId("California State University Sacramento"))
                .thenReturn(Optional.of("school-1"));
        when(rateMyProfessorClient.searchTeachers("jane smith", "school-1")).thenReturn(List.of());
        when(professorOverallRatingRepository.findById(7L)).thenReturn(Optional.empty());

        ProfessorRatingSyncService service = new ProfessorRatingSyncService(
                courseProfessorRepository,
                professorOverallRatingRepository,
                rateMyProfessorClient,
                "California State University Sacramento"
        );

        ProfessorRatingSyncService.SyncSummary summary = service.syncAllProfessorRatings();

        assertEquals(1, summary.professorsProcessed());
        assertEquals(1, summary.fallbackCount());
        verify(professorOverallRatingRepository).save(any(ProfessorOverallRating.class));
    }

    @Test
    void fetchRating_returnsLiveRatingForExactNameMatch() throws Exception {
        CourseProfessorRepository courseProfessorRepository = mock(CourseProfessorRepository.class);
        ProfessorOverallRatingRepository professorOverallRatingRepository = mock(ProfessorOverallRatingRepository.class);
        RateMyProfessorClient rateMyProfessorClient = mock(RateMyProfessorClient.class);

        Professor professor = new Professor();
        professor.setId(12L);
        professor.setName("Dr. John Doe");

        when(rateMyProfessorClient.searchTeachers("john doe", "school-1"))
                .thenReturn(List.of(new RateMyProfessorClient.RateMyProfessorTeacher(
                        "teacher-1",
                        "John",
                        "Doe",
                        "California State University Sacramento",
                        4.26,
                        14
                )));

        ProfessorRatingSyncService service = new ProfessorRatingSyncService(
                courseProfessorRepository,
                professorOverallRatingRepository,
                rateMyProfessorClient,
                "California State University Sacramento"
        );

        ProfessorRatingSyncService.RatingResult result = service.fetchRating(professor, "school-1");

        assertEquals(BigDecimal.valueOf(4.3).setScale(1), result.overallRating());
        assertEquals(14, result.reviewCount());
    }
}
