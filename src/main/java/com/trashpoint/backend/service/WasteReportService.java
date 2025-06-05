package com.trashpoint.backend.service;

import com.trashpoint.backend.domain.User;
import com.trashpoint.backend.domain.WasteReport;
import com.trashpoint.backend.dto.WasteReportRequest;
import com.trashpoint.backend.repository.UserRepository;
import com.trashpoint.backend.repository.WasteReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WasteReportService {

    private final WasteReportRepository wasteReportRepository;
    private final UserRepository userRepository;

    public WasteReport createReport(WasteReportRequest request) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        WasteReport report = WasteReport.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .user(user)
                .build();

        return wasteReportRepository.save(report);
    }

    public List<WasteReport> getMyReports() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return wasteReportRepository.findByUserId(user.getId());
    }

    public List<WasteReport> getAllReports() {
        return wasteReportRepository.findAll();
    }
}
