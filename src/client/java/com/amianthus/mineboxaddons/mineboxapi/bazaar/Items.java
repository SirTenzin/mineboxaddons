package com.amianthus.mineboxaddons.mineboxapi.bazaar;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.Item;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.MarketData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;
import net.minecraft.client.MinecraftClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

// Define the Items class
public class Items {
    // Define the ApiResponse record for searching by name
    public record ApiResponse(List<Item> items, int page, int pageSize, int total) {}

    private static final String BASE_URL = "https://api.minebox.co";
    private final OkHttpClient client;
    private final Gson gson;

    public Items() {
        this.client = new OkHttpClient();
        this.gson = new GsonBuilder()
                .setNumberToNumberStrategy(ToNumberPolicy.DOUBLE)
                .create();
    }

    public static String getCurrentLocale() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            String locale = client.getLanguageManager().getLanguage();
            return locale.split("_")[0]; // Extract the 2-character language code
        }
        return "en"; // Default to English if not found
    }

    public Item searchItemByName(String name) {
        // Construct the URL with the search parameter
        String url = String.format("%s/items?page=1&pageSize=1&search=%s&locale=%s", BASE_URL, name, getCurrentLocale());

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .get() // Use GET method
                .build();

        // Execute the request synchronously
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Handle the successful response
                assert response.body() != null;
                String responseData = response.body().string();
                MineboxAddonsClient.LOGGER.info(responseData);
                ApiResponse apiResponse = gson.fromJson(responseData, ApiResponse.class);

                // Check if items is null or empty
                if (apiResponse.items() != null && !apiResponse.items().isEmpty()) {
                    return apiResponse.items().getFirst(); // Return the first item
                } else {
                    System.err.println("No items found in the response.");
                    return null;
                }
            } else {
                // Handle the error response
                System.err.println("Request failed with status code: " + response.code());
                System.err.println("Response message: " + response.message());
                String errorResponse = response.body() != null ? response.body().string() : "No response body";
                System.err.println("Error response: " + errorResponse);
                return null;
            }
        } catch (IOException e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
            return null;
        } // Return null if no item is found or an error occurs
    }

    public Item getItemByID(String id) {
        // Construct the URL with the item ID
        String url = String.format("%s/item/%s?locale=%s", BASE_URL, id, getCurrentLocale());

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .get() // Use GET method
                .build();

        // Execute the request synchronously
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Handle the successful response
                assert response.body() != null;
                String responseData = response.body().string();
                return gson.fromJson(responseData, Item.class); // Parse the full Item
            } else {
                // Handle the error response
                System.err.println("Request failed with status code: " + response.code());
                System.err.println("Response message: " + response.message());
                String errorResponse = response.body() != null ? response.body().string() : "No response body";
                System.err.println("Error response: " + errorResponse);
            }
        } catch (IOException e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
        }

        return null; // Return null if no item is found or an error occurs
    }

    public ImmutablePair<String, MarketData> getItemPriceByName(String name, String period) {
        Item searchedItem = this.searchItemByName(name);
        String id = searchedItem != null ? searchedItem.id() : null;
        if (searchedItem != null) {
            return getMarketData(id, period, searchedItem);
        }
        return null;
    }

    public ImmutablePair<String, MarketData> getItemPriceById(String id, String period) {
        Item searchedItem = this.getItemByID(id);
        return getMarketData(id, period, searchedItem);
    }

    @Nullable
    private ImmutablePair<String, MarketData> getMarketData(String id, String period, Item searchedItem) {

        // Construct the URL with the item ID
        String url = String.format("%s/stats?period=%s&item_id=%s", BASE_URL, period, id);

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .get() // Use GET method
                .build();

        // Execute the request synchronously
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Handle the successful response
                assert response.body() != null;
                String responseData = response.body().string();
                try {
                    return new ImmutablePair<>(searchedItem.name(), gson.fromJson(responseData, MarketData.class)); // Return the item name and MarketData
                } catch (JsonSyntaxException e) {
                    System.err.println("JSON Parsing error: " + e.getMessage());
                    MineboxAddonsClient.LOGGER.error(e.toString());
                }
            } else {
                // Handle the error response
                System.err.println("Request failed with status code: " + response.code());
                System.err.println("Response message: " + response.message());
                String errorResponse = response.body() != null ? response.body().string() : "No response body";
                System.err.println("Error response: " + errorResponse);
            }
        } catch (IOException e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
        }

        return null;
    }

    public static Items[] getItems(int page) {
        String url = String.format("%s/items?page=%d&pageSize=50&locale=%s", BASE_URL, page, getCurrentLocale());

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = new OkHttpClient().newCall(request).execute()) {

        } catch (Exception e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
        }
    }
}
