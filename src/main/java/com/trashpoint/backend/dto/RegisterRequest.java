package com.trashpoint.backend.dto;

import com.trashpoint.backend.domain.User.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        String fullName,

        @Email(message = "El correo debe ser válido")
        String email,

        String phone,

        @NotNull(message = "El rol es obligatorio")
        Role role
) {}
