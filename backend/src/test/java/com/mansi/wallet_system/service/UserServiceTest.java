package com.mansi.wallet_system.service;

import com.mansi.wallet_system.entity.User;
import com.mansi.wallet_system.entity.Wallet;
import com.mansi.wallet_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUser_Success() {
        User user = new User();
        user.setName("  John Doe  ");
        user.setEmail("  John.Doe@Example.com  ");
        user.setPassword("password123");

        when(userRepository.existsByEmailIgnoreCase("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        User saved = userService.saveUser(user);

        assertNotNull(saved);
        assertEquals(10L, saved.getId());
        assertEquals("John Doe", saved.getName());
        assertEquals("john.doe@example.com", saved.getEmail());
        assertEquals("encodedPassword", saved.getPassword());
        assertEquals("USER", saved.getRole());

        // Verify wallet creation
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletService, times(1)).createWallet(walletCaptor.capture());
        Wallet createdWallet = walletCaptor.getValue();
        assertEquals(10L, createdWallet.getUserId());
        assertEquals("John Doe", createdWallet.getOwnerName());
        assertEquals(BigDecimal.ZERO, createdWallet.getBalance());
    }

    @Test
    void testSaveUser_DuplicateEmail() {
        User user = new User();
        user.setName("John");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(user)
        );
        assertEquals("Email is already in use.", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(walletService, never()).createWallet(any());
    }

    @Test
    void testSaveUser_ShortPassword() {
        User user = new User();
        user.setName("John");
        user.setEmail("test@example.com");
        user.setPassword("12345");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(user)
        );
        assertTrue(ex.getMessage().contains("at least 8 characters"));
    }

    @Test
    void testSaveUser_BlankFields() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null));

        user.setName("  ");
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user));

        user.setName("John");
        user.setEmail(" ");
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user));

        user.setEmail("john@example.com");
        user.setPassword(" ");
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user));
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }
}
