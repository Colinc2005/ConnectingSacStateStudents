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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProfessorRatingSyncService {

    private static final BigDecimal FALLBACK_RATING = BigDecimal.valueOf(3.0);
    private static final Set<String> INVALID_PROFESSOR_NAMES = Set.of(
            "",
            "staff",
            "tba",
            "to be announced",
            "arranged",
            "unknown"
    );

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

        if (normalizedProfessorName.isBlank() || INVALID_PROFESSOR_NAMES.contains(normalizedProfessorName)) {
            return RatingResult.fallback();
        }

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

    boolean namesMatch(String sourceName, String candidateName) {
        NameParts source = parseName(sourceName);
        NameParts candidate = parseName(candidateName);

        if (source.normalizedFullName().isBlank() || candidate.normalizedFullName().isBlank()) {
            return false;
        }

        if (source.normalizedFullName().equals(candidate.normalizedFullName())) {
            return true;
        }

        if (!source.lastName().isBlank() && source.lastName().equals(candidate.lastName())) {
            if (!source.firstName().isBlank() && source.firstName().equals(candidate.firstName())) {
                return true;
            }

            if (!source.firstName().isBlank() && !candidate.firstName().isBlank()
                    && source.firstName().charAt(0) == candidate.firstName().charAt(0)) {
                return true;
            }
        }

        return source.tokens().equals(candidate.tokens())
                || candidate.normalizedFullName().contains(source.normalizedFullName())
                || source.normalizedFullName().contains(candidate.normalizedFullName());
    }

    private int scoreNameMatch(String sourceName, String candidateName) {
        NameParts source = parseName(sourceName);
        NameParts candidate = parseName(candidateName);
        int score = Math.abs(source.normalizedFullName().length() - candidate.normalizedFullName().length());

        if (!source.lastName().isBlank() && source.lastName().equals(candidate.lastName())) {
            score -= 20;
        }

        if (!source.firstName().isBlank() && source.firstName().equals(candidate.firstName())) {
            score -= 10;
        }

        if (source.tokens().equals(candidate.tokens())) {
            score -= 5;
        }

        return score;
    }

    private String normalizeProfessorName(String name) {
        return parseName(name).normalizedFullName();
    }

    private NameParts parseName(String rawName) {
        String cleaned = rawName == null ? "" : rawName.trim().toLowerCase();

        cleaned = cleaned
                .replaceAll("\\b(dr|prof|professor)\\.?\\s+", "")
                .replaceAll("\\b(phd|ph d|md|m d|jr|sr|ii|iii|iv)\\b\\.?", " ")
                .replace("&", " ")
                .replaceAll("\\([^)]*\\)", " ")
                .replaceAll("[^a-z0-9, ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleaned.contains(",")) {
            String[] parts = cleaned.split(",", 2);
            String last = normalizeWords(parts[0]);
            String firstAndMiddle = parts.length > 1 ? normalizeWords(parts[1]) : "";
            cleaned = (firstAndMiddle + " " + last).trim();
        } else {
            cleaned = normalizeWords(cleaned);
        }

        List<String> tokens = Arrays.stream(cleaned.split(" "))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .filter(token -> token.length() > 1)
                .toList();

        if (tokens.isEmpty()) {
            return new NameParts("", "", "", Set.of());
        }

        String firstName = tokens.get(0);
        String lastName = tokens.get(tokens.size() - 1);
        String normalizedFullName = String.join(" ", tokens);
        Set<String> tokenSet = tokens.stream().collect(Collectors.toSet());

        return new NameParts(normalizedFullName, firstName, lastName, tokenSet);
    }

    private String normalizeWords(String value) {
        return value.replaceAll("\\s+", " ").trim();
    }

    public record SyncSummary(int professorsProcessed, int fallbackCount) {
    }

    record RatingResult(BigDecimal overallRating, Integer reviewCount) {
        static RatingResult fallback() {
            return new RatingResult(FALLBACK_RATING, 0);
        }
    }

    record NameParts(String normalizedFullName, String firstName, String lastName, Set<String> tokens) {
    }
}
