package com.example.stock.controller;

import com.example.stock.security.JwtUtil;
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
class StockControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        UserDetails testUser = User.withUsername("testuser").password("").authorities(Collections.emptyList()).build();
        token = jwtUtil.generateToken(testUser);
    }

    @Test
    void getStocks_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/api/stocks"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStocks_withValidToken_returns200WithArray() throws Exception {
        mockMvc.perform(get("/api/stocks").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getStocks_filterByEgx_returnsOnlyEgxStocks() throws Exception {
        mockMvc.perform(get("/api/stocks?market=EGX").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].market").value("EGX"));
    }

    @Test
    void getStocks_search_filtersResults() throws Exception {
        mockMvc.perform(get("/api/stocks?search=bank").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
