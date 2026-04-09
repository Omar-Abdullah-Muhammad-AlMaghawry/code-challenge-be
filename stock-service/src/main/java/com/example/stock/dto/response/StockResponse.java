package com.example.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    private String symbol;
    private String name;
    private double price;
    private double change;
    private double changePercent;
    private String market;
    private String currency;
}
