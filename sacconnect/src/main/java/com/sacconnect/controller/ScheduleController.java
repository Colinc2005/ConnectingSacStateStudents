package com.sacconnect.controller;

import com.sacconnect.dto.request.ScheduleGenerateRequest;
import com.sacconnect.dto.response.ScheduleGenerateResponse;
import com.sacconnect.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
public class ScheduleController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateSchedules(@RequestBody ScheduleGenerateRequest request) {
        log.info("Schedule generate request received: courseIds={}, preferredStartMin={}, preferredEndMin={}, topNPerCategory={}",
                request.getCourseIds(), request.getPreferredStartMin(), request.getPreferredEndMin(), request.getTopNPerCategory());
        try {
            ScheduleGenerateResponse response = scheduleService.generateSchedules(request);
            log.info("Schedule generation completed: totalValidSchedules={}, truncated={}, overallBestCount={}, leastDaysCount={}, bestTimeCount={}, smallestGapsCount={}",
                    response.getTotalValidSchedules(),
                    response.isTruncated(),
                    response.getOverallBest() == null ? 0 : response.getOverallBest().size(),
                    response.getLeastDays() == null ? 0 : response.getLeastDays().size(),
                    response.getBestTime() == null ? 0 : response.getBestTime().size(),
                    response.getSmallestGaps() == null ? 0 : response.getSmallestGaps().size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Schedule generation rejected: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
