package com.trashpoint.backend.service;

import com.trashpoint.backend.domain.User;
import com.trashpoint.backend.dto.RegisterRequest;
import com.trashpoint.backend.dto.UserUpdateRequest;
import com.trashpoint.backend.exception.CustomErrorResponse;
import com.trashpoint.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new CustomErrorResponse("El nombre de usuario ya existe", 409);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new CustomErrorResponse("El correo ya está registrado", 409);
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .role(request.role())
                .active(true)
                .build();


        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));
    }

    public User updateProfile(UserUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomErrorResponse("Usuario no encontrado", 404));

        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.email() != null) user.setEmail(request.email());
        if (request.phone() != null) user.setPhone(request.phone());

        return userRepository.save(user);
    }
}
