package com.amianthus.mineboxaddons.utils;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageRenderer {
    public static Identifier createTextureFromBufferedImage(BufferedImage image, String identifierName) {
        try {
            // Convert BufferedImage to NativeImage
            NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), false);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int argb = image.getRGB(x, y);
                    nativeImage.setColorArgb(x, y, argb);
                }
            }
            // Register the texture
            Identifier identifier = Identifier.of("mineboxaddons", identifierName);
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(nativeImage));

            return identifier;
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null;
        }
    }

    public static Identifier createTextureFromBase64(@NotNull String base64, String identifierName) {
        try {
            // Convert base64 string to BufferedImage
            BufferedImage image = ImageUtils.decodeBase64ToImage(base64);
            return createTextureFromBufferedImage(image, identifierName);
        } catch (Exception e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
            MineboxAddonsClient.LOGGER.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
}