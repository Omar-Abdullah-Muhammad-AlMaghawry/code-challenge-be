package com.example.codechallenge.service;

import com.example.codechallenge.dto.request.LoginRequest;
import com.example.codechallenge.dto.request.RegisterRequest;
import com.example.codechallenge.dto.response.LoginResponse;
import com.example.codechallenge.entity.User;
import com.example.codechallenge.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = (User) userService.loadUserByUsername(request.username());
        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getUsername());
    }

    public LoginResponse register(RegisterRequest request) {
        User user = userService.register(request.username(), request.password());
        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getUsername());
    }
}
