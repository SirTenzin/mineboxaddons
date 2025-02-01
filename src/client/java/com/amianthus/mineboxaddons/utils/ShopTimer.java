package com.amianthus.mineboxaddons.utils;

public class ShopTimer {
    public enum Shop {
        COFFEE(15),  // Refreshes at 15th minute of every hour
        BAKERY(30),  // Refreshes at 30th minute of every hour
        COCKTAIL(45), // Refreshes at 45th minute of every hour
        CHEESE(50);  // Refreshes at 50th minute of every hour

        private final int refreshMinute; // Minute of the hour when shop refreshes

        Shop(int refreshMinute) {
            this.refreshMinute = refreshMinute;
        }

        public int getRefreshMinute() {
            return refreshMinute;
        }
    }

    /**
     * Calculates minutes remaining until the next shop refresh
     * @param shop The shop to check
     * @param currentTimeMillis Current system time in milliseconds
     * @return Number of minutes until next refresh
     */
    public static int getMinutesRemaining(Shop shop, long currentTimeMillis) {
        int currentMinuteOfHour = (int) ((currentTimeMillis / 60000) % 60);
        int targetMinute = shop.getRefreshMinute();

        // If we haven't passed the refresh time this hour
        if (currentMinuteOfHour < targetMinute) {
            return targetMinute - currentMinuteOfHour;
        } else if(currentMinuteOfHour == targetMinute) {
            return 0;
        }
        // If we've passed the refresh time, calculate time until next hour's refresh
        else return (60 - currentMinuteOfHour) + targetMinute;
    }

    /**
     * Gets the shop's current status
     * @param shop The shop to check
     * @param currentTimeMillis Current system time in milliseconds
     * @return "now!" if shop is refreshing, otherwise minutes until next refresh
     */
    public static String getShopStatus(Shop shop, long currentTimeMillis) {
        int minutesRemaining = getMinutesRemaining(shop, currentTimeMillis);
        return minutesRemaining == 0 ? "now!" : String.valueOf(minutesRemaining);
    }
}