package com.trashpoint.backend.controller;

import com.trashpoint.backend.domain.WasteReport;
import com.trashpoint.backend.domain.User;
import com.trashpoint.backend.exception.CustomErrorResponse;
import com.trashpoint.backend.repository.ReportRepository;
import com.trashpoint.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private static final String REPORT_UPLOAD_DIR = "uploads/reports/";
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody WasteReport report,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));
        report.setUser(user);
        WasteReport saved = reportRepository.save(report);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<?> uploadReportPhoto(@PathVariable UUID id,
                                               @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomErrorResponse("Archivo vacío", 400);
        }

        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(REPORT_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            WasteReport report = reportRepository.findById(id)
                    .orElseThrow(() -> new CustomErrorResponse("Reporte no encontrado", 404));

            report.setPhotoPath(filename);
            reportRepository.save(report);

            return ResponseEntity.ok(Map.of(
                    "message", "Foto subida correctamente",
                    "filename", filename,
                    "url", "http://localhost:8080/uploads/reports/" + filename
            ));

        } catch (IOException e) {
            throw new CustomErrorResponse("Error al guardar la imagen", 500);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllReports(@RequestParam(required = false) String status) {
        List<WasteReport> reports;

        try {
            if (status != null) {
                WasteReport.ReportStatus enumStatus = WasteReport.ReportStatus.valueOf(status.toUpperCase());
                reports = reportRepository.findByStatus(enumStatus);
            } else {
                reports = reportRepository.findAll();
            }
        } catch (IllegalArgumentException e) {
            throw new CustomErrorResponse("Estado inválido: " + status, 400);
        }

        List<Map<String, Object>> response = reports.stream().map(report -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", report.getId());
            map.put("title", report.getTitle());
            map.put("description", report.getDescription());
            map.put("location", report.getLocation());
            map.put("status", report.getStatus());
            map.put("createdAt", report.getCreatedAt());
            map.put("user", report.getUser().getUsername());
            map.put("photoUrl", report.getPhotoPath() != null ?
                    "http://localhost:8080/uploads/reports/" + report.getPhotoPath() : null);
            return map;
        }).toList();


        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateReportStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        WasteReport report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomErrorResponse("Reporte no encontrado", 404));

        String newStatus = body.get("status");
        try {
            WasteReport.ReportStatus statusEnum = WasteReport.ReportStatus.valueOf(newStatus.toUpperCase());
            report.setStatus(statusEnum);
            reportRepository.save(report);

            return ResponseEntity.ok(Map.of(
                    "message", "Estado actualizado correctamente",
                    "status", statusEnum
            ));
        } catch (IllegalArgumentException e) {
            throw new CustomErrorResponse("Estado inválido: " + newStatus, 400);
        }
    }
    @GetMapping("/mine")
    public ResponseEntity<?> getMyReports(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        List<WasteReport> reports = reportRepository.findByUserUsername(username);
        List<Map<String, Object>> response = reports.stream().map(report -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", report.getId());
            map.put("title", report.getTitle());
            map.put("description", report.getDescription());
            map.put("location", report.getLocation());
            map.put("status", report.getStatus());
            map.put("createdAt", report.getCreatedAt());
            map.put("user", report.getUser().getUsername());
            map.put("photoUrl", report.getPhotoPath() != null
                    ? "http://localhost:8080/uploads/reports/" + report.getPhotoPath()
                    : null);
            return map;
        }).toList();


        return ResponseEntity.ok(response);
    }

}
