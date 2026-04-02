package com.sacconnect.controller;

import com.sacconnect.service.CsusCourseImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
public class CsusCourseImportController {

    private final CsusCourseImportService csusCourseImportService;

    public CsusCourseImportController(CsusCourseImportService csusCourseImportService) {
        this.csusCourseImportService = csusCourseImportService;
    }

    @PostMapping("/csus/csc")
    public ResponseEntity<String> importCsusCourses() {
        try {
            int count = csusCourseImportService.importCourses();
            return ResponseEntity.ok("Imported " + count + " records.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Import failed: " + e.getMessage());
        }
    }
}