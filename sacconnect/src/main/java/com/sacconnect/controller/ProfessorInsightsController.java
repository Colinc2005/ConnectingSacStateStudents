package com.sacconnect.controller;

import com.sacconnect.dto.response.ProfessorAiSummaryResponse;
import com.sacconnect.service.ProfessorAiSummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/professors")
@CrossOrigin(origins = "*")
public class ProfessorInsightsController {

    private final ProfessorAiSummaryService professorAiSummaryService;

    public ProfessorInsightsController(ProfessorAiSummaryService professorAiSummaryService) {
        this.professorAiSummaryService = professorAiSummaryService;
    }

    @GetMapping("/{id}/ai-summary")
    public ResponseEntity<?> getAiSummary(@PathVariable Long id) {
        try {
            ProfessorAiSummaryResponse response = professorAiSummaryService.summarizeProfessor(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to summarize professor reviews."));
        }
    }

    @GetMapping("/ai-summary")
    public ResponseEntity<?> getAiSummaryByName(@RequestParam("name") String name) {
        try {
            ProfessorAiSummaryResponse response = professorAiSummaryService.summarizeProfessorByName(name);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to summarize professor reviews."));
        }
    }
}
