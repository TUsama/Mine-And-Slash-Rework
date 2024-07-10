package com.robertx22.age_of_exile.vanilla_mc.commands.wrapper;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.robertx22.age_of_exile.vanilla_mc.commands.suggestions.CommandSuggestions;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IGUID;

import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;

public class StringWrapper extends ArgumentWrapper<String> {
    @Override
    public String id() {
        return "string";
    }

    @Override
    public RequiredArgumentBuilder getType() {
        var b = argument(id(), StringArgumentType.string());
        return b;
    }

    @Override
    public String get(CommandContext ctx) {
        return StringArgumentType.getString(ctx, id());
    }


    public StringWrapper registrySuggestion(ExileRegistryType type) {
        this.suggestions = new CommandSuggestions() {
            @Override
            public List<String> suggestions() {
                ExileRegistryContainer<? extends IGUID> reg = Database.getRegistry(type);
                var list = reg.getList().stream().map(x -> x.GUID()).collect(Collectors.toList());
                list.add("random");
                return list;

            }
        };
        return this;
    }

}
