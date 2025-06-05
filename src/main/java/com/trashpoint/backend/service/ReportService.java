package com.trashpoint.backend.service;

import com.trashpoint.backend.domain.WasteReport;
import com.trashpoint.backend.repository.ReportRepository;
import com.trashpoint.backend.exception.CustomErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private static final String UPLOAD_DIR = "uploads/reports/";

    public WasteReport getReport(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new CustomErrorResponse("Reporte no encontrado", 404));
    }

    public Map<String, Object> uploadPhoto(UUID reportId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomErrorResponse("Archivo vacío", 400);
        }

        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            WasteReport report = getReport(reportId);
            report.setPhotoPath(filename);
            reportRepository.save(report);

            return Map.of(
                    "message", "Foto subida correctamente",
                    "filename", filename,
                    "url", "http://localhost:8080/uploads/reports/" + filename
            );
        } catch (IOException e) {
            throw new CustomErrorResponse("Error al guardar la foto", 500);
        }
    }
}
