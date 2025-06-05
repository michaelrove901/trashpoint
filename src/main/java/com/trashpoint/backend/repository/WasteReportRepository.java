package com.trashpoint.backend.repository;

import com.trashpoint.backend.domain.WasteReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WasteReportRepository extends JpaRepository<WasteReport, UUID> {
    List<WasteReport> findByUserId(UUID userId);

}
