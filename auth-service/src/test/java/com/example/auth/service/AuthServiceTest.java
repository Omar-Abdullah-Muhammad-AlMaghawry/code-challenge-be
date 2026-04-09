package com.example.auth.service;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.LoginResponse;
import com.example.auth.entity.User;
import com.example.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserService userService;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    @Test
    void login_validCredentials_returnsTokenAndUsername() {
        User user = User.builder().username("alice").password("encoded").build();
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("alice", null));
        when(userService.loadUserByUsername("alice")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("alice", "pass"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.username()).isEqualTo("alice");
    }

    @Test
    void login_badCredentials_throwsAuthenticationException() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("alice", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void register_newUser_returnsTokenAndUsername() {
        User user = User.builder().username("bob").password("encoded").build();
        when(userService.register("bob", "pass")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.register(new RegisterRequest("bob", "pass"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.username()).isEqualTo("bob");
    }

    @Test
    void register_duplicateUsername_throwsIllegalArgumentException() {
        when(userService.register("alice", "pass")).thenThrow(new IllegalArgumentException("Username already taken"));

        assertThatThrownBy(() -> authService.register(new RegisterRequest("alice", "pass")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
