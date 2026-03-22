package com.sacconnect.service;

import com.sacconnect.model.Professor;
import com.sacconnect.model.ProfessorOverallRating;
import com.sacconnect.repository.CourseProfessorRepository;
import com.sacconnect.repository.ProfessorOverallRatingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorRatingSyncService {

    private static final BigDecimal FALLBACK_RATING = BigDecimal.valueOf(3.0);

    private final CourseProfessorRepository courseProfessorRepository;
    private final ProfessorOverallRatingRepository professorOverallRatingRepository;
    private final RateMyProfessorClient rateMyProfessorClient;
    private final String schoolName;

    public ProfessorRatingSyncService(
            CourseProfessorRepository courseProfessorRepository,
            ProfessorOverallRatingRepository professorOverallRatingRepository,
            RateMyProfessorClient rateMyProfessorClient,
            @Value("${rmp.school.name:California State University Sacramento}") String schoolName
    ) {
        this.courseProfessorRepository = courseProfessorRepository;
        this.professorOverallRatingRepository = professorOverallRatingRepository;
        this.rateMyProfessorClient = rateMyProfessorClient;
        this.schoolName = schoolName;
    }

    @Transactional
    public SyncSummary syncAllProfessorRatings() throws Exception {
        List<Professor> professors = courseProfessorRepository.findDistinctProfessors();
        Optional<String> schoolId = rateMyProfessorClient.findSchoolId(schoolName);

        if (schoolId.isEmpty()) {
            throw new IllegalStateException("Could not find Rate My Professors school ID for " + schoolName);
        }

        int synced = 0;
        int fallbackCount = 0;

        for (Professor professor : professors) {
            RatingResult result = fetchRating(professor, schoolId.get());
            upsertProfessorRating(professor.getId(), result);
            synced++;

            if (result.reviewCount() == 0) {
                fallbackCount++;
            }
        }

        return new SyncSummary(synced, fallbackCount);
    }

    RatingResult fetchRating(Professor professor, String schoolId) throws Exception {
        String normalizedProfessorName = normalizeProfessorName(professor.getName());
        List<RateMyProfessorClient.RateMyProfessorTeacher> candidates =
                rateMyProfessorClient.searchTeachers(normalizedProfessorName, schoolId);

        Optional<RateMyProfessorClient.RateMyProfessorTeacher> bestMatch = candidates.stream()
                .filter(candidate -> namesMatch(professor.getName(), candidate.fullName()))
                .min(Comparator.comparing(candidate -> scoreNameMatch(professor.getName(), candidate.fullName())));

        if (bestMatch.isEmpty()) {
            return RatingResult.fallback();
        }

        if (bestMatch.get().avgRating() == null
                || bestMatch.get().numRatings() == null
                || bestMatch.get().numRatings() == 0) {
            return RatingResult.fallback();
        }

        BigDecimal rating = BigDecimal.valueOf(bestMatch.get().avgRating())
                .setScale(1, RoundingMode.HALF_UP);

        return new RatingResult(rating, bestMatch.get().numRatings());
    }

    private void upsertProfessorRating(Long professorId, RatingResult result) {
        ProfessorOverallRating rating = professorOverallRatingRepository.findById(professorId)
                .orElseGet(() -> new ProfessorOverallRating(professorId, result.overallRating(), result.reviewCount()));

        rating.setOverallRating(result.overallRating());
        rating.setReviewCount(result.reviewCount());

        professorOverallRatingRepository.save(rating);
    }

    private boolean namesMatch(String sourceName, String candidateName) {
        String normalizedSource = normalizeProfessorName(sourceName);
        String normalizedCandidate = normalizeProfessorName(candidateName);

        return normalizedSource.equals(normalizedCandidate)
                || normalizedCandidate.contains(normalizedSource)
                || normalizedSource.contains(normalizedCandidate);
    }

    private int scoreNameMatch(String sourceName, String candidateName) {
        String normalizedSource = normalizeProfessorName(sourceName);
        String normalizedCandidate = normalizeProfessorName(candidateName);
        return Math.abs(normalizedSource.length() - normalizedCandidate.length());
    }

    private String normalizeProfessorName(String name) {
        return name == null
                ? ""
                : name.toLowerCase()
                        .replaceAll("\\b(dr|prof|professor)\\.?\\s+", "")
                        .replaceAll("[^a-z0-9 ]", " ")
                        .replaceAll("\\s+", " ")
                        .trim();
    }

    public record SyncSummary(int professorsProcessed, int fallbackCount) {
    }

    record RatingResult(BigDecimal overallRating, Integer reviewCount) {
        static RatingResult fallback() {
            return new RatingResult(FALLBACK_RATING, 0);
        }
    }
}
