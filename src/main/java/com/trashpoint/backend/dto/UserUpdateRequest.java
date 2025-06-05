
package com.trashpoint.backend.dto;

public record UserUpdateRequest(
        String fullName,
        String email,
        String phone
) {}
