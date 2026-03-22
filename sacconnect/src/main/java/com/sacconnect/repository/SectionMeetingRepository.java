package com.sacconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.sacconnect.model.SectionMeeting;

public interface SectionMeetingRepository extends JpaRepository<SectionMeeting, Long> {
    @Transactional
    void deleteBySectionId(Long sectionId);
}
