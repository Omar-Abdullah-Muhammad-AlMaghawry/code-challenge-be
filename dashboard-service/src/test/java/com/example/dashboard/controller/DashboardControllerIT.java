package com.example.dashboard.controller;

import com.example.dashboard.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        UserDetails testUser = User.withUsername("testuser").password("").authorities(Collections.emptyList()).build();
        token = jwtUtil.generateToken(testUser);
    }

    @Test
    void getStats_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStats_withValidToken_returns200WithStats() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].label").isNotEmpty())
                .andExpect(jsonPath("$[0].value").isNotEmpty());
    }

    @Test
    void getActivity_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard/activity"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getActivity_withValidToken_returns200WithActivity() throws Exception {
        mockMvc.perform(get("/api/dashboard/activity").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].text").isNotEmpty())
                .andExpect(jsonPath("$[0].time").isNotEmpty());
    }
}
