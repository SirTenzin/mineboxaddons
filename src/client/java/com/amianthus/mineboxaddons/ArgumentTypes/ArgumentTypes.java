package com.amianthus.mineboxaddons.ArgumentTypes;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

public class ArgumentTypes {
    public static void register() {
        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of("mineboxaddons", "period"),
                PeriodArgumentType.class,
                ConstantArgumentSerializer.of(PeriodArgumentType::new)
        );
        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of("mineboxaddons", "direction"),
                MarketDirectionArgumentType.class,
                ConstantArgumentSerializer.of(MarketDirectionArgumentType::new)
        );
    }
}
