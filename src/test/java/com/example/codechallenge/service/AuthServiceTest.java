package com.example.codechallenge.service;

import com.example.codechallenge.dto.request.LoginRequest;
import com.example.codechallenge.dto.request.RegisterRequest;
import com.example.codechallenge.dto.response.LoginResponse;
import com.example.codechallenge.entity.User;
import com.example.codechallenge.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("john")
                .password("encodedPass")
                .build();
    }

    // --- login ---

    @Test
    void login_validCredentials_returnsTokenAndUsername() {
        when(userService.loadUserByUsername("john")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("john", "secret"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.username()).isEqualTo("john");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_invalidCredentials_throwsBadCredentialsException() {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(new LoginRequest("john", "wrong")))
                .isInstanceOf(BadCredentialsException.class);

        verify(userService, never()).loadUserByUsername(any());
    }

    // --- register ---

    @Test
    void register_newUser_returnsTokenAndUsername() {
        when(userService.register("john", "secret")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.register(new RegisterRequest("john", "secret"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.username()).isEqualTo("john");
    }

    @Test
    void register_duplicateUsername_throwsIllegalArgumentException() {
        when(userService.register("john", "secret"))
                .thenThrow(new IllegalArgumentException("Username already taken"));

        assertThatThrownBy(() -> authService.register(new RegisterRequest("john", "secret")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(jwtUtil, never()).generateToken(any());
    }
}
