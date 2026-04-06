package com.example.codechallenge.controller;

import com.example.codechallenge.entity.User;
import com.example.codechallenge.security.JwtUtil;
import com.example.codechallenge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DashboardControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        try {
            User user = userService.register("dashuser", "password");
            token = jwtUtil.generateToken(user);
        } catch (IllegalArgumentException e) {
            // user already exists from a previous test — just generate a token
            User user = (User) userService.loadUserByUsername("dashuser");
            token = jwtUtil.generateToken(user);
        }
    }

    // --- GET /api/dashboard/stats ---

    @Test
    void getStats_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStats_withValidToken_returns200WithStats() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].label").isNotEmpty())
                .andExpect(jsonPath("$[0].value").isNotEmpty());
    }

    // --- GET /api/dashboard/activity ---

    @Test
    void getActivity_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard/activity"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getActivity_withValidToken_returns200WithActivity() throws Exception {
        mockMvc.perform(get("/api/dashboard/activity")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].text").isNotEmpty())
                .andExpect(jsonPath("$[0].time").isNotEmpty());
    }
}
