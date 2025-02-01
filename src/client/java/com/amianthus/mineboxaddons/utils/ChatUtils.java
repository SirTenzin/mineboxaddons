package com.amianthus.mineboxaddons.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatUtils {
    public static Text createGradientText(String message, int[] colors) {
        List<Text> textParts = new ArrayList<>();

        for (int i = 0; i < message.length(); i++) {
            float percent = (float) i / (message.length() - 1);
            int color = interpolateColors(colors, percent);

            // Create a colored text for each character
            Text coloredChar = Text.literal(String.valueOf(message.charAt(i)))
                    .setStyle(Style.EMPTY.withColor(color));
            textParts.add(coloredChar);
        }

        // Combine all parts into a single Text object
        MutableText finalText = Text.empty();
        for (Text part : textParts) {
            finalText = finalText.append(part);
        }

        return finalText;
    }

    public static Text createGradientText(String message) {
        return createGradientText(message, new int[]{0xFFE259, 0xFFA751});
    }

    public static Text createGradientText(String message, Boolean error) {
        if(error) return createGradientText(message, new int[]{0xCB2D3E, 0xEF473A});
        else return createGradientText(message, new int[]{0xFFE259, 0xFFA751});
    }

    public static Text createPrefixedText(String message) {
        Text prefix = createGradientText("[MineboxAddons] ");

        // Combine all parts into a single Text object
        MutableText finalText = Text.empty();
        finalText = finalText.append(prefix).append(message);

        return finalText;
    }

    public static Text createPrefixedText(String message, Boolean error) {
        Text prefix = createGradientText("[MineboxAddons] ", error);

        // Combine all parts into a single Text object
        MutableText finalText = Text.empty();
        finalText = finalText.append(prefix).append(message);

        return finalText;
    }

    public static Text createMultiLinePrefixedText(String message) {
        String[] lines = message.split("\n");
        Text prefix = createGradientText("[MineboxAddons] ");

        // Combine all parts into a single Text object
        MutableText finalText = Text.empty();
        for (String line: lines) {
            if(lines[0].equals(line)) finalText = finalText.append(prefix).append(line);
            else finalText = finalText.append("\n").append(prefix).append(line);
        }

        return finalText;
    }

    public static Text createMultiLinePrefixedText(String message, Boolean error) {
        String[] lines = message.split("\n");
        Text prefix = createGradientText("[MineboxAddons] ", error);

        // Combine all parts into a single Text object
        MutableText finalText = Text.empty();
        for (String line: lines) {
            finalText = finalText.append("\n").append(prefix).append(line);
        }

        return finalText;
    }

    private static int interpolateColors(int[] colors, float percent) {
        int index = (int) (percent * (colors.length - 1));
        float remainder = percent * (colors.length - 1) - index;

        int r1 = (colors[index] >> 16) & 0xFF;
        int g1 = (colors[index] >> 8) & 0xFF;
        int b1 = colors[index] & 0xFF;

        if (index < colors.length - 1) {
            int r2 = (colors[index + 1] >> 16) & 0xFF;
            int g2 = (colors[index + 1] >> 8) & 0xFF;
            int b2 = colors[index + 1] & 0xFF;

            r1 = (int) (r1 + remainder * (r2 - r1));
            g1 = (int) (g1 + remainder * (g2 - g1));
            b1 = (int) (b1 + remainder * (b2 - b1));
        }

        return (r1 << 16) | (g1 << 8) | b1;
    }

    public static String toCapitalCase(String snakeCase) {
        // Split the string by underscores
        String[] words = snakeCase.split("_");

        // Capitalize each word
        StringBuilder capitalCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if(word.equalsIgnoreCase("to")) {
                    capitalCase.append("to ");
                } else {
                    capitalCase.append(Character.toUpperCase(word.charAt(0))) // Capitalize the first letter
                            .append(word.substring(1).toLowerCase()) // Lowercase the rest
                            .append(" "); // Add a space

                }
            }
        }

        // Remove the trailing space and return the result
        return capitalCase.toString().trim();
    }

    public static final Formatting COMMON = Formatting.GRAY;
    public static final Formatting UNCOMMON = Formatting.GREEN;
    public static final Formatting RARE = Formatting.BLUE;
    public static final Formatting EPIC = Formatting.DARK_PURPLE;
    public static final Formatting LEGENDARY = Formatting.GOLD;
    public static final Formatting MYTHIC = Formatting.DARK_RED;

    // Create a Map for easy lookup
    public static final Map<String, Formatting> RARITY_MAP = new HashMap<>();

    static {
        RARITY_MAP.put("COMMON", COMMON);
        RARITY_MAP.put("UNCOMMON", UNCOMMON);
        RARITY_MAP.put("RARE", RARE);
        RARITY_MAP.put("EPIC", EPIC);
        RARITY_MAP.put("LEGENDARY", LEGENDARY);
        RARITY_MAP.put("MYTHIC", MYTHIC);
    }

}
