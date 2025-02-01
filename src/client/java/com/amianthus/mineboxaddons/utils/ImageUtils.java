package com.amianthus.mineboxaddons.utils;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@SuppressWarnings("CallToPrintStackTrace")
public class ImageUtils {

    public static String encodeImageToBase64(String filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage decodeBase64ToImage(String base64Image) {
        try {
            // Remove the data URL prefix if present
            if (base64Image.startsWith("data:image")) {
                base64Image = base64Image.split(",")[1];
            }

            // Decode the Base64 string
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Convert the byte array to a BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(bis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage getTextureAsBufferedImage(ResourceManager resourceManager, String texturePath) {
        // Create the Identifier for the texture
        Identifier textureId = Identifier.of("mineboxaddons", texturePath);

        try (InputStream inputStream = resourceManager.getResource(textureId).get().getInputStream()) {
            // Read the file into a BufferedImage
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle the error appropriately
        }
    }
}