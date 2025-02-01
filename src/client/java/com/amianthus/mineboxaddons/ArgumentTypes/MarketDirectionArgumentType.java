package com.amianthus.mineboxaddons.ArgumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class MarketDirectionArgumentType implements ArgumentType<MarketDirection> {
    private static final DynamicCommandExceptionType INVALID_OPTION = new DynamicCommandExceptionType(name ->
            Text.literal(name + " is not a valid market direction."));

    private static final MarketDirection[] VALUES = MarketDirection.values();

    @Override
    public MarketDirection parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString().toUpperCase();
        for (MarketDirection direction : VALUES) {
            if (direction.name().equals(argument)) {
                return direction;
            }
        }
        throw INVALID_OPTION.create(argument);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Arrays.stream(VALUES).map(Enum::name), builder);
    }

    public static MarketDirection getDirection(final CommandContext<?> context, final String name) {
        return context.getArgument(name, MarketDirection.class);
    }
}