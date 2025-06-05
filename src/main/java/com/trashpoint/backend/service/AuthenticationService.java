package com.trashpoint.backend.service;

import com.trashpoint.backend.domain.User;
import com.trashpoint.backend.dto.LoginRequest;
import com.trashpoint.backend.exception.CustomErrorResponse;
import com.trashpoint.backend.repository.UserRepository;
import com.trashpoint.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    public String login(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new CustomErrorResponse("Credenciales inválidas", 401);
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));

        if (!user.isActive()) {
            throw new CustomErrorResponse("Usuario desactivado", 403);
        }

        return jwtService.generateToken(user);
    }
}
