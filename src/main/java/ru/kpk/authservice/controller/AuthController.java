package ru.kpk.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kpk.authservice.dto.RegisterRequest;
import ru.kpk.authservice.model.Role;
import ru.kpk.authservice.model.RoleType;
import ru.kpk.authservice.model.User;
import ru.kpk.authservice.model.UserStatus;
import ru.kpk.authservice.repository.RoleRepository;
import ru.kpk.authservice.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail((request.getEmail()))) {
            return ResponseEntity.badRequest().body("Ошибка: пользователь с таким email уже существует.");
        }

        Role userRole = roleRepository.findByCode(RoleType.USER).
                orElseGet(()-> roleRepository.save(new Role(RoleType.USER,
                        "Слушатель", java.util.List.of("READ_COURSES"))));

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(userRole);
        newUser.setStatus(UserStatus.ACTIVE);

        userRepository.save(newUser);
        return ResponseEntity.ok("Пользователь успешно зарегестрирован.");
    }
}
