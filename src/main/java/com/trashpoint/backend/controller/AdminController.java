// src/main/java/com/trashpoint/backend/controller/AdminController.java

package com.trashpoint.backend.controller;

import com.trashpoint.backend.domain.User;
import com.trashpoint.backend.repository.UserRepository;
import com.trashpoint.backend.exception.CustomErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleUserActive(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));


        user.setActive(!user.isActive());
        userRepository.save(user);

        return ResponseEntity.ok().body("Estado actualizado. Ahora el usuario está " + (user.isActive() ? "activo" : "inactivo"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> logicallyDeleteUser(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));

        if (!user.isActive()) {
            return ResponseEntity.ok("El usuario ya estaba inactivo.");
        }

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok("Usuario desactivado correctamente.");
    }
}
