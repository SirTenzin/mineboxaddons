package com.amianthus.mineboxaddons.utils;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@SuppressWarnings("CallToPrintStackTrace")
public class RarityImageLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger("mineboxaddons");

    private static BufferedImage loadRarityTexture(String modId, String rarity) {
        try {
            // Load the image as a BufferedImage
            Identifier imageIdentifier = Identifier.of(modId, "textures/rarity/" + rarity + ".png");
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(imageIdentifier).orElseThrow();

            return ImageIO.read(resource.getInputStream());
        } catch (Exception e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
            return null;
        }
    }

    public static void createRarityTexture(String modId, String rarity) {
        try {
            // Load the image
            BufferedImage bufferedImage = loadRarityTexture(modId, rarity);
            ImageRenderer.createTextureFromBufferedImage(bufferedImage, Identifier.of(modId, "rarity_" + rarity).toString());
            LOGGER.info("Loaded rarity texture: " + rarity + " with id " + Identifier.of(modId, "rarity_" + rarity));
        } catch (Exception e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
        }
    }

    public static void createRarityTextures() {
        String[] rarities = new String[] { "common", "uncommon", "rare", "epic", "mythic", "legendary" };
        for (String rarity : rarities) {
            createRarityTexture("mineboxaddons", rarity);
        }
    }
}