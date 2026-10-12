package com.mansi.wallet_system.service;

import com.mansi.wallet_system.dto.JwtResponse;
import com.mansi.wallet_system.dto.LoginRequest;
import com.mansi.wallet_system.entity.User;
import com.mansi.wallet_system.repository.UserRepository;
import com.mansi.wallet_system.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("  Alice@Example.COM  ");
        request.setPassword("Secret123");

        User user = new User();
        user.setId(5L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("encodedSecret");
        user.setRole("USER");

        when(userRepository.findByEmailIgnoreCase("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Secret123", "encodedSecret")).thenReturn(true);
        when(jwtService.generateToken("alice@example.com", 5L, "USER")).thenReturn("mock-jwt-token");

        JwtResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(5L, response.getUserId());
        assertEquals("Alice", response.getName());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("WrongPassword");

        User user = new User();
        user.setId(5L);
        user.setEmail("alice@example.com");
        user.setPassword("encodedSecret");

        when(userRepository.findByEmailIgnoreCase("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "encodedSecret")).thenReturn(false);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () ->
                authService.login(request)
        );
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () ->
                authService.login(request)
        );
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void testLogin_NullOrEmptyFields() {
        assertThrows(BadCredentialsException.class, () -> authService.login(null));

        LoginRequest emptyReq = new LoginRequest();
        assertThrows(BadCredentialsException.class, () -> authService.login(emptyReq));

        emptyReq.setEmail(" ");
        emptyReq.setPassword("pass");
        assertThrows(BadCredentialsException.class, () -> authService.login(emptyReq));

        emptyReq.setEmail("valid@email.com");
        emptyReq.setPassword("");
        assertThrows(BadCredentialsException.class, () -> authService.login(emptyReq));
    }
}
