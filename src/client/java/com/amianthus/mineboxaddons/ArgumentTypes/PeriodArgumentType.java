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

public class PeriodArgumentType implements ArgumentType<Period> {
    private static final DynamicCommandExceptionType INVALID_OPTION = new DynamicCommandExceptionType(name ->
            Text.literal(name + " is not a valid period."));

    private static final Period[] VALUES = Period.values();

    @Override
    public Period parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString().toUpperCase();
        for (Period period : VALUES) {
            if (period.name().equals(argument)) {
                return period;
            }
        }
        throw INVALID_OPTION.create(argument);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Arrays.stream(VALUES).map(Enum::name), builder);
    }

    public static Period getPeriod(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Period.class);
    }
}