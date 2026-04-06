package com.example.codechallenge.security;

import com.example.codechallenge.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET =
            "3d8f2a91c7b4e56f0a1d9e2c3b7f8a4d5e6c7b8a9d0e1f2c3b4a5d6e7f8c9d0";
    private static final long EXPIRATION = 86_400_000L; // 1 day

    private JwtUtil jwtUtil;
    private User user;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
        user = User.builder()
                .username("john")
                .password("encodedPass")
                .build();
    }

    @Test
    void generateToken_returnsNonBlankToken() {
        String token = jwtUtil.generateToken(user);
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtil.generateToken(user);
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("john");
    }

    @Test
    void isValid_validTokenAndMatchingUser_returnsTrue() {
        String token = jwtUtil.generateToken(user);
        assertThat(jwtUtil.isValid(token, user)).isTrue();
    }

    @Test
    void isValid_tokenWithDifferentUser_returnsFalse() {
        String token = jwtUtil.generateToken(user);
        User otherUser = User.builder().username("jane").password("pass").build();
        assertThat(jwtUtil.isValid(token, otherUser)).isFalse();
    }

    @Test
    void isValid_expiredToken_returnsFalse() {
        JwtUtil shortLivedJwt = new JwtUtil(SECRET, -1L); // already expired
        String token = shortLivedJwt.generateToken(user);
        assertThat(shortLivedJwt.isValid(token, user)).isFalse();
    }
}
