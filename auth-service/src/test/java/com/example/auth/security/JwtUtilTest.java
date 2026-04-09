package com.example.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "3d8f2a91c7b4e56f0a1d9e2c3b7f8a4d5e6c7b8a9d0e1f2c3b4a5d6e7f8c9d0";
    private static final long EXPIRATION = 86400000L;

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
        userDetails = User.withUsername("alice").password("pass").authorities(Collections.emptyList()).build();
    }

    @Test
    void generateToken_returnsNonBlankToken() {
        assertThat(jwtUtil.generateToken(userDetails)).isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtil.generateToken(userDetails);
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void isValid_validTokenAndMatchingUser_returnsTrue() {
        String token = jwtUtil.generateToken(userDetails);
        assertThat(jwtUtil.isValid(token, userDetails)).isTrue();
    }

    @Test
    void isValid_tokenForDifferentUser_returnsFalse() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails other = User.withUsername("bob").password("pass").authorities(Collections.emptyList()).build();
        assertThat(jwtUtil.isValid(token, other)).isFalse();
    }

    @Test
    void isValid_expiredToken_returnsFalse() {
        JwtUtil shortLived = new JwtUtil(SECRET, -1000L);
        String token = shortLived.generateToken(userDetails);
        assertThat(jwtUtil.isValid(token, userDetails)).isFalse();
    }
}
