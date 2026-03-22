package com.sacconnect.controller;

import com.sacconnect.dto.response.CatalogCourseResponse;
import com.sacconnect.dto.response.CourseSectionResponse;
import com.sacconnect.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@CrossOrigin(origins = "*")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/majors/{majorName}/courses")
    public ResponseEntity<List<CatalogCourseResponse>> getCoursesByMajor(@PathVariable String majorName) {
        return ResponseEntity.ok(catalogService.getCoursesByMajor(majorName));
    }

    @GetMapping("/courses/{courseId}/sections")
    public ResponseEntity<List<CourseSectionResponse>> getSectionsByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(catalogService.getSectionsByCourseId(courseId));
    }
}
