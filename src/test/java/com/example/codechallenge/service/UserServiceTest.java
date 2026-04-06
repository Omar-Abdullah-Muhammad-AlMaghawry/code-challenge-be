package com.example.codechallenge.service;

import com.example.codechallenge.entity.User;
import com.example.codechallenge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("john")
                .password("encodedPass")
                .build();
    }

    // --- loadUserByUsername ---

    @Test
    void loadUserByUsername_existingUser_returnsUser() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("john");

        assertThat(result.getUsername()).isEqualTo("john");
    }

    @Test
    void loadUserByUsername_unknownUser_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost");
    }

    // --- register ---

    @Test
    void register_newUsername_savesAndReturnsUser() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register("john", "secret");

        assertThat(result.getUsername()).isEqualTo("john");
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsIllegalArgumentException() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("john", "secret"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(userRepository, never()).save(any());
    }
}
