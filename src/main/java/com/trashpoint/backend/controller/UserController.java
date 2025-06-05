// src/main/java/com/trashpoint/backend/controller/UserController.java
package com.trashpoint.backend.controller;

import com.trashpoint.backend.domain.User;
import com.trashpoint.backend.dto.UserUpdateRequest;
import com.trashpoint.backend.repository.UserRepository;
import com.trashpoint.backend.service.UserService;
import com.trashpoint.backend.exception.CustomErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String UPLOAD_DIR = "uploads/";

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));

        String avatarUrl = user.getAvatar() != null
                ? "http://localhost:8080/uploads/" + user.getAvatar()
                : null;

        return ResponseEntity.ok(new UserProfileResponse(
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                avatarUrl,
                user.getCreatedAt()
        ));
    }

    public record UserProfileResponse(
            String username,
            String fullName,
            String email,
            String phone,
            String role,
            String avatarUrl,
            LocalDateTime createdAt
    ) {}

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal UserDetails userDetails) {
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

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));

            user.setAvatar(filename); // ✅ Usar 'avatar' en lugar de avatarPath
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Archivo subido exitosamente",
                    "filename", filename,
                    "avatarUrl", "http://localhost:8080/uploads/" + filename
            ));
        } catch (IOException e) {
            throw new CustomErrorResponse("Error al guardar archivo", 500);
        }
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@RequestBody UserUpdateRequest request) {
        User updated = userService.updateProfile(request);
        return ResponseEntity.ok(updated);
    }


    @PatchMapping("/active")
    public ResponseEntity<?> toggleUserActiveStatus(@RequestBody Map<String, Boolean> body,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        boolean desiredStatus = body.getOrDefault("active", true);

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));

        if (user.isActive() == desiredStatus) {
            String estado = desiredStatus ? "activo" : "inactivo";
            return ResponseEntity.ok(Map.of(
                    "message", "El usuario ya estaba " + estado
            ));
        }

        user.setActive(desiredStatus);
        userRepository.save(user);

        String nuevoEstado = desiredStatus ? "activada" : "desactivada";

        return ResponseEntity.ok(Map.of(
                "message", "Cuenta " + nuevoEstado + " con éxito"
        ));
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


}
