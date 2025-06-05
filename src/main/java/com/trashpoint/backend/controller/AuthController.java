// src/main/java/com/trashpoint/backend/controller/AuthController.java
package com.trashpoint.backend.controller;

import com.trashpoint.backend.dto.LoginRequest;
import com.trashpoint.backend.dto.RegisterRequest;
import com.trashpoint.backend.domain.User;
import com.trashpoint.backend.service.AuthenticationService;
import com.trashpoint.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }
}
