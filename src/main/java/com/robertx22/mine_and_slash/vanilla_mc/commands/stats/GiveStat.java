package com.robertx22.mine_and_slash.vanilla_mc.commands.stats;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType;
import com.robertx22.mine_and_slash.vanilla_mc.commands.CommandRefs;
import com.robertx22.library_of_exile.command_wrapper.CommandSuggestions;
import com.robertx22.mine_and_slash.vanilla_mc.commands.suggestions.StatSuggestions;
import com.robertx22.mine_and_slash.vanilla_mc.commands.suggestions.StatTypeSuggestions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class GiveStat {

    static class ModOrExact extends CommandSuggestions {
        @Override
        public List<String> suggestions() {
            return Arrays.asList("exact", "scaling");
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                literal(CommandRefs.ID)
                        .then(literal("stat").requires(e -> e.hasPermission(2))
                                .then(literal("give")
                                        .requires(e -> e.hasPermission(2))
                                        .then(argument("target", EntityArgument.entity())
                                                .then(argument("scaling", StringArgumentType.string())
                                                        .suggests(new ModOrExact())
                                                        .then(argument("statGUID", StringArgumentType.string())
                                                                .suggests(new StatSuggestions())
                                                                .then(argument("statType", StringArgumentType.string())
                                                                        .suggests(new StatTypeSuggestions())
                                                                        .then(argument("Key/Identifier", StringArgumentType
                                                                                .string())
                                                                                .then(argument("value", FloatArgumentType.floatArg())
                                                                                        .then(argument("Silent", BoolArgumentType.bool())
                                                                                                .executes(ctx -> {
                                                                                                    return run(EntityArgument
                                                                                                            .getPlayer(ctx, "target"), StringArgumentType
                                                                                                            .getString(ctx, "scaling"), StringArgumentType
                                                                                                            .getString(ctx, "statGUID"), StringArgumentType
                                                                                                            .getString(ctx, "statType"), StringArgumentType
                                                                                                            .getString(ctx, "Key/Identifier"), FloatArgumentType
                                                                                                            .getFloat(ctx, "value"), BoolArgumentType
                                                                                                            .getBool(ctx, "Silent")
                                                                                                    );
                                                                                                })))))))))));
    }

    private static int run(Entity en, String scaling, String statGUID, String statType,
                           String GUID, float v1, Boolean silent) {

        try {

            if (en instanceof LivingEntity e) {
                EntityData data = Load.Unit(en);

                if (scaling.equals("exact")) {
                    data.getCustomExactStats().addExactStat(GUID, statGUID, v1, ModType.valueOf(statType));
                    if (!silent) {
                        e.sendSystemMessage(Component.literal("Stat Applied."));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
