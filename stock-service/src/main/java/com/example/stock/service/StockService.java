package com.example.stock.service;

import com.example.stock.dto.response.StockResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockService {

    private static final Map<String, String> USA_SYMBOLS = Map.ofEntries(
            Map.entry("AAPL", "Apple Inc."),
            Map.entry("MSFT", "Microsoft Corp."),
            Map.entry("NVDA", "NVIDIA Corp."),
            Map.entry("AMZN", "Amazon.com Inc."),
            Map.entry("GOOGL", "Alphabet Inc."),
            Map.entry("META", "Meta Platforms Inc."),
            Map.entry("TSLA", "Tesla Inc."),
            Map.entry("JPM", "JPMorgan Chase & Co."),
            Map.entry("V", "Visa Inc."),
            Map.entry("JNJ", "Johnson & Johnson"),
            Map.entry("WMT", "Walmart Inc."),
            Map.entry("XOM", "Exxon Mobil Corp."),
            Map.entry("MA", "Mastercard Inc."),
            Map.entry("PG", "Procter & Gamble Co."),
            Map.entry("HD", "Home Depot Inc."),
            Map.entry("CVX", "Chevron Corp."),
            Map.entry("MRK", "Merck & Co. Inc."),
            Map.entry("LLY", "Eli Lilly and Co."),
            Map.entry("ABBV", "AbbVie Inc."),
            Map.entry("PEP", "PepsiCo Inc.")
    );

    private static final List<StockResponse> EGX_STOCKS = List.of(
            StockResponse.builder().symbol("COMI").name("Commercial International Bank").price(56.80).change(2.30).changePercent(4.22).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("HRHO").name("EFG Hermes Holding").price(24.15).change(1.05).changePercent(4.54).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("ETEL").name("Telecom Egypt").price(33.40).change(-0.60).changePercent(-1.76).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("PHDC").name("Palm Hills Developments").price(8.72).change(0.43).changePercent(5.19).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("CLHO").name("Cleopatra Hospital").price(15.30).change(0.80).changePercent(5.52).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("EKHO").name("Edita Food Industries").price(22.60).change(-1.10).changePercent(-4.64).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("JUFO").name("Juhayna Food Industries").price(12.45).change(0.35).changePercent(2.89).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("EAST").name("Eastern Company").price(18.90).change(0.90).changePercent(5.00).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("ABUK").name("Abu Kir Fertilizers").price(41.20).change(-0.80).changePercent(-1.90).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("ORWE").name("Oriental Weavers").price(9.85).change(0.25).changePercent(2.61).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("SKPC").name("Sidi Kerir Petrochemicals").price(14.70).change(0.70).changePercent(5.00).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("MNHD").name("Madinet Nasr Housing").price(6.30).change(0.30).changePercent(5.00).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("OCDI").name("Orascom Development").price(11.20).change(-0.50).changePercent(-4.27).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("ISWP").name("Integrated Diagnostics Holdings").price(19.85).change(1.25).changePercent(6.72).market("EGX").currency("EGP").build(),
            StockResponse.builder().symbol("SWDY").name("Sixth of October Development").price(7.60).change(0.40).changePercent(5.56).market("EGX").currency("EGP").build()
    );

    @Value("${finnhub.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private List<StockResponse> cachedUsaStocks = Collections.emptyList();
    private Instant cacheExpiry = Instant.EPOCH;

    public List<StockResponse> getAllStocks(String search, String market) {
        List<StockResponse> stocks = new ArrayList<>();

        boolean includeUsa = market == null || market.equalsIgnoreCase("USA") || market.equalsIgnoreCase("ALL");
        boolean includeEgx = market == null || market.equalsIgnoreCase("EGX") || market.equalsIgnoreCase("ALL");

        if (includeUsa) stocks.addAll(getUsaStocks());
        if (includeEgx) stocks.addAll(EGX_STOCKS);

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            stocks = stocks.stream()
                    .filter(s -> s.getSymbol().toLowerCase().contains(q) || s.getName().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        stocks.sort(Comparator.comparingDouble(StockResponse::getChangePercent).reversed());
        return stocks;
    }

    private synchronized List<StockResponse> getUsaStocks() {
        if (apiKey.isBlank()) return Collections.emptyList();
        if (Instant.now().isBefore(cacheExpiry)) return cachedUsaStocks;

        List<StockResponse> fresh = new ArrayList<>();
        for (Map.Entry<String, String> entry : USA_SYMBOLS.entrySet()) {
            try {
                String url = "https://finnhub.io/api/v1/quote?symbol=" + entry.getKey() + "&token=" + apiKey;
                FinnhubQuote quote = restTemplate.getForObject(url, FinnhubQuote.class);
                if (quote != null && quote.getCurrentPrice() > 0) {
                    fresh.add(StockResponse.builder()
                            .symbol(entry.getKey())
                            .name(entry.getValue())
                            .price(quote.getCurrentPrice())
                            .change(quote.getChange())
                            .changePercent(quote.getChangePercent())
                            .market("USA")
                            .currency("USD")
                            .build());
                }
            } catch (Exception ignored) {
            }
        }

        cachedUsaStocks = fresh;
        cacheExpiry = Instant.now().plusSeconds(300);
        return cachedUsaStocks;
    }

    @Data
    private static class FinnhubQuote {
        @JsonProperty("c") private double currentPrice;
        @JsonProperty("d") private double change;
        @JsonProperty("dp") private double changePercent;
    }
}
