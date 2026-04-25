package com.bazarou.service;

import com.bazarou.dto.RegistroDTO;
import com.bazarou.model.User;
import com.bazarou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registrar(RegistroDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Ese nombre de usuario ya está ocupado, ¡échale otro!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ese correo ya está registrado");
        }

        User u = User.builder()
                .username(dto.getUsername().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombreCompleto(dto.getNombreCompleto())
                .ciudad(dto.getCiudad())
                .estado(dto.getEstado())
                .build();
        return userRepository.save(u);
    }

    public User getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    public User actualizarPerfil(User u) {
        return userRepository.save(u);
    }
}
