package com.amianthus.mineboxaddons.mineboxapi.bazaar.structures;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MarketData {
    @SerializedName("BUY")
    private BuyData buy;

    @SerializedName("SELL")
    private SellData sell;

    @SerializedName("DIRECT")
    private Object direct;

    // Simple data structures to match JSON exactly
    public static class BuyData {
        public Object stream;
        public List<List<String>> values; // array of arrays
    }

    public static class SellData {
        public Object stream;
        public List<List<String>> values; // array of arrays
    }

    // Helper methods for processing the data
    public List<PriceEntry> getBuyEntries() {
        if(buy == null) {
            return Collections.emptyList();
        }
        return convertToPriceEntries(buy.values);
    }

    public List<PriceEntry> getSellEntries() {
        if(sell == null) {
            return Collections.emptyList();
        }
        return convertToPriceEntries(sell.values);
    }

    private List<PriceEntry> convertToPriceEntries(List<List<String>> rawValues) {
        if (rawValues == null) return new ArrayList<>();

        List<PriceEntry> entries = new ArrayList<>();
        for (List<String> pair : rawValues) {
            long timestamp = Long.parseLong(pair.get(0));
            double value = Double.parseDouble(pair.get(1));
            entries.add(new PriceEntry(timestamp, value));
        }

        // Sort by timestamp descending (newest first)
        entries.sort((a, b) -> Long.compare(b.timestamp(), a.timestamp()));
        return entries;
    }

    // Clean record for processed data
    public record PriceEntry(long timestamp, double value) {
        public String getFormattedDate() {
            return Instant.ofEpochSecond(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
    }

    // Utility methods
    public Optional<PriceEntry> getNewestDay(List<PriceEntry> entries) {
        return entries.isEmpty() ? Optional.empty() : Optional.of(entries.getFirst());
    }

    public Pair<String, String> getLatestBuyPrice() {
        List<PriceEntry> buyEntries = getBuyEntries();
        if (buyEntries.isEmpty()) {
            return Pair.of("No data", "No data");
        }

        PriceEntry latestEntry = buyEntries.getFirst();
        return Pair.of(latestEntry.getFormattedDate(), String.valueOf(latestEntry.value()));
    }

    public Pair<String, String> getLatestSellPrice() {
        List<PriceEntry> sellEntries = getSellEntries();
        if (sellEntries.isEmpty()) {
            return Pair.of("No data", "No data");
        }

        PriceEntry latestEntry = sellEntries.getFirst();
        return Pair.of(latestEntry.getFormattedDate(), String.valueOf(latestEntry.value()));
    }

    public List<PriceEntry> getNewestWeek(List<PriceEntry> entries) {
        return getNewestDays(entries, 7);
    }

    public List<PriceEntry> getNewestMonth(List<PriceEntry> entries) {
        return getNewestDays(entries, 30);
    }

    private List<PriceEntry> getNewestDays(List<PriceEntry> entries, int days) {
        if (entries.isEmpty()) return Collections.emptyList();

        long newestTimestamp = entries.getFirst().timestamp();
        long cutoffTimestamp = newestTimestamp - ((long) days * 24 * 60 * 60);

        return entries.stream()
                .filter(e -> e.timestamp() >= cutoffTimestamp)
                .collect(Collectors.toList());
    }

    public List<PriceEntry> flattenEntries(List<PriceEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyList();
        }

        // Group entries by date
        Map<String, List<PriceEntry>> entriesByDate = entries.stream()
                .collect(Collectors.groupingBy(PriceEntry::getFormattedDate));

        // Sort the flattened entries by timestamp in descending order
        return entriesByDate.values().stream()
                .map(dateEntries -> dateEntries.stream()
                        .max(Comparator.comparingLong(PriceEntry::timestamp))
                        .orElse(null))
                .filter(Objects::nonNull).sorted((a, b) -> Long.compare(b.timestamp(), a.timestamp())).collect(Collectors.toList());
    }
}