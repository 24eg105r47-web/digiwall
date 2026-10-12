package com.mansi.wallet_system.service;

import com.mansi.wallet_system.dto.JwtResponse;
import com.mansi.wallet_system.dto.LoginRequest;
import com.mansi.wallet_system.entity.User;
import com.mansi.wallet_system.repository.UserRepository;
import com.mansi.wallet_system.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public JwtResponse login(LoginRequest loginRequest) {
        if (loginRequest == null ||
                loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String normalizedEmail = loginRequest.getEmail().trim().toLowerCase();
        Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(normalizedEmail);

        if (optionalUser.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = optionalUser.get();
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());

        return new JwtResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
