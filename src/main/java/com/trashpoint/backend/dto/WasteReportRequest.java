package com.trashpoint.backend.dto;

public record WasteReportRequest(
        String title,
        String description,
        String location
) {}
