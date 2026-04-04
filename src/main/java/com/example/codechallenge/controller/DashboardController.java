package com.example.codechallenge.controller;

import com.example.codechallenge.dto.response.ActivityResponse;
import com.example.codechallenge.dto.response.StatResponse;
import com.example.codechallenge.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<List<StatResponse>> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/activity")
    public ResponseEntity<List<ActivityResponse>> getActivity() {
        return ResponseEntity.ok(dashboardService.getActivity());
    }
}
