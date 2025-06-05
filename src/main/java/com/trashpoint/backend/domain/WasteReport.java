package com.trashpoint.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "waste_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasteReport {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;
    private String location;

    private String photoPath;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.ACTIVE; // valor por defecto


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    public enum ReportStatus {
        ACTIVE,
        SOLVED,
        INACTIVE
    }

}
