package com.sacconnect.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "rmp.sync.enabled", havingValue = "true")
public class ProfessorRatingSyncRunner implements ApplicationRunner {

    private final ProfessorRatingSyncService professorRatingSyncService;

    public ProfessorRatingSyncRunner(ProfessorRatingSyncService professorRatingSyncService) {
        this.professorRatingSyncService = professorRatingSyncService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ProfessorRatingSyncService.SyncSummary summary = professorRatingSyncService.syncAllProfessorRatings();
        System.out.println("Professor rating sync complete. Processed "
                + summary.professorsProcessed()
                + " professors, fallback used for "
                + summary.fallbackCount()
                + ".");
    }
}
