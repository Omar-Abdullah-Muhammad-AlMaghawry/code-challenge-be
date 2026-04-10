package com.example.stock.controller;

import com.example.stock.dto.response.CandlePoint;
import com.example.stock.dto.response.StockResponse;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<CandlePoint>> getHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1D") String period) {
        return ResponseEntity.ok(stockService.getHistory(symbol, period));
    }
}
