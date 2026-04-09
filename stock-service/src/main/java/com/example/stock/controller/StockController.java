package com.example.stock.controller;

import com.example.stock.dto.response.StockResponse;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockResponse>> getStocks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String market) {
        return ResponseEntity.ok(stockService.getAllStocks(search, market));
    }
}
