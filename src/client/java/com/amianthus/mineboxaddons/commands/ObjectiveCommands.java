package com.amianthus.mineboxaddons.commands;

import com.amianthus.mineboxaddons.utils.ChatUtils;
import com.amianthus.mineboxaddons.widgets.ObjectivesWidget;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.amianthus.mineboxaddons.MineboxAddonsClient.hudManager;

public class ObjectiveCommands {

    public static int createObjective(CommandContext<FabricClientCommandSource> context) {
        String objectives = StringArgumentType.getString(context, "objectives");
        String[] objectivesArray = objectives.split(",");
        ObjectivesWidget widget = new ObjectivesWidget(objectivesArray);
        hudManager.registerWidget(widget);
        context.getSource().sendFeedback(ChatUtils.createPrefixedText("Objectives widget created."));
        return 0;
    }

    public static int nextObjective(CommandContext<FabricClientCommandSource> context) {
        ObjectivesWidget widget = hudManager.getWidget(
                ObjectivesWidget.IDENTIFIER,
                ObjectivesWidget.class
        );

        if (widget == null) {
            context.getSource().sendFeedback(ChatUtils.createPrefixedText("No objectives widget found.", true));
            context.getSource().sendFeedback(ChatUtils.createPrefixedText("Trying fix...", true));
        } else {
            int didEnd = widget.next();
            if (didEnd == 1) {
                context.getSource().sendFeedback(ChatUtils.createPrefixedText("Completed objective!"));
                hudManager.deRegisterWidget(widget);
            } else {
                context.getSource().sendFeedback(ChatUtils.createPrefixedText("Next objective set."));
            }
        }
        return 0;
    }
}
