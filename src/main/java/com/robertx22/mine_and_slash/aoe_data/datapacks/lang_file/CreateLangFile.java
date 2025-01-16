package com.robertx22.mine_and_slash.aoe_data.datapacks.lang_file;

import com.google.common.collect.Lists;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.mine_and_slash.capability.player.data.PlayerBuffData;
import com.robertx22.mine_and_slash.database.data.stats.StatGuiGroup;
import com.robertx22.mine_and_slash.database.data.stats.priority.StatPriority;
import com.robertx22.mine_and_slash.gui.screens.stat_gui.StatGuiGroupSection;
import com.robertx22.mine_and_slash.loot.LootModifierEnum;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.tags.ModTag;
import com.robertx22.mine_and_slash.tags.TagType;
import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocDesc;
import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocName;
import com.robertx22.mine_and_slash.uncommon.localization.Formatter;
import com.robertx22.mine_and_slash.uncommon.localization.*;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.DirUtils;
import com.robertx22.mine_and_slash.vanilla_mc.items.gemrunes.GemItem;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CreateLangFile {

    public static String create() {

        List<String> translatorGuide = Lists.newArrayList(
                "This section provides guidance to translators, offering insights into internal details:",
                "1. Some lang lines have corresponding locale context, particularly the formatter lines. Understanding these contexts helps translators comprehend how the lang line function. Translating these lines is unnecessary.",
                "2. Certain lines contain line break format(\\n). translator can add/remove that format at will based on the actual length of the translated text."
        );

        String json = "";//"{\n";

        for (String x : translatorGuide) {
            json += "\t" + "\"" + "translator_guide_" + translatorGuide.indexOf(x) + "\": \"" + StringEscapeUtils.escapeJava(x) + "\",\n";
        }


        json += DirUtils.getManualString();

        // dont print duplicates
        List<String> usedGUIDS = new ArrayList<>();

        for (Map.Entry<String, List<IAutoLocName>> entry : getMap().entrySet()) {

            json += CreateLangFileUtils.comment(entry.getKey());
            for (IAutoLocName iauto : entry.getValue()) {


                boolean bad = false;
                if (iauto.locNameLangFileGUID().isEmpty()) {
                    bad = true;
                }
                if (!iauto.shouldRegisterLangName()) {
                    bad = true;
                }
                if (iauto.locNameForLangFile() == null || iauto.locNameForLangFile().isEmpty()) {
                    bad = true;
                }

                if (!bad) {

                    if (iauto.locNameForLangFile().contains("\"")) {
                        try {
                            throw new Exception(iauto.locNameForLangFile() + " contains double \"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (usedGUIDS.contains(iauto.formattedLocNameLangFileGUID())) {
                        continue;
                    }
                    usedGUIDS.add(iauto.formattedLocNameLangFileGUID());

                    if (iauto.formattedLocNameLangFileGUID().isEmpty()) {
                        continue;
                    }
                    // I dont know why I got encoding error when export the lang file, have to add this.
                    //If you dont want it, just delete and change the \n in Itemtips.java to \\n.
                    json += "\t" + "\"" + iauto.formattedLocNameLangFileGUID() + "\": \"" + StringEscapeUtils.escapeJava(iauto.locNameForLangFile()) + "\",\n";
                    if (iauto.additionLocInformation() != null) {
                        json += "\t" + "\"" + iauto.formattedLocNameLangFileGUID() + ".locale_context_for_translator\": \"" + StringEscapeUtils.escapeJava(iauto.additionLocInformation()) + "\",\n";
                    }
                }
            }
            json += CreateLangFileUtils.comment(entry.getKey());

        }
        usedGUIDS.clear();

        for (Map.Entry<String, List<IAutoLocDesc>> entry : getDescMap().entrySet()) {

            json += CreateLangFileUtils.comment(entry.getKey());
            for (IAutoLocDesc iauto : entry.getValue()) {
                if (!iauto.locDescLangFileGUID().isEmpty() && iauto.shouldRegisterLangDesc() && iauto.locDescForLangFile() != null && iauto.locDescForLangFile().isEmpty() == false) {

                    if (iauto.locDescForLangFile()
                            .contains("\"")) {
                        try {
                            throw new Exception(iauto.locDescForLangFile() + " contains double \"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (usedGUIDS.contains(iauto.formattedLocDescLangFileGUID())) {
                        continue;
                    }
                    usedGUIDS.add(iauto.formattedLocDescLangFileGUID());

                    json += "\t" + "\"" + iauto.formattedLocDescLangFileGUID() + "\": \"" + StringEscapeUtils.escapeJava(iauto.locDescForLangFile()) + "\",\n";
                }
            }
            json += CreateLangFileUtils.comment(entry.getKey());

        }

        usedGUIDS.clear();

        //   json += "\n}";

        // json = CreateLangFileUtils.replaceLast(json, ",", ""); // removes last , or else json wont work


        return json;
/*

        try {
            //  ExileLog.get().log("Starting to create lang file");

            if (Files.exists(Paths.get(DirUtils.langFilePath())) == false) {
                Files.createFile(Paths.get(DirUtils.langFilePath()));
            }

            File file = new File(DirUtils.langFilePath());

            FileWriter fw = new FileWriter(file);
            fw.write(json);
            fw.close();
            //ExileLog.get().log("Saved lang file to " + file.toPath()                    .toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

 */

    }

    public static HashMap<String, List<IAutoLocName>> getMap() {
        HashSet<IAutoLocName> list = CreateLangFileUtils.getFromRegistries(IAutoLocName.class);

        for (ExileRegistryContainer reg : Database.getAllRegistries()) {
            for (Object o : reg.getSerializable()) {
                if (o instanceof IAutoLocName loc && o instanceof ExileRegistry<?> r) {
                    if (r.getRegistrationInfo().modid.equals(SlashRef.MODID)) {
                        list.add(loc);
                    }
                }
            }
            for (Object o : reg.getList()) {
                if (o instanceof IAutoLocName loc && o instanceof ExileRegistry<?> r) {
                    if (r.getRegistrationInfo().modid.equals(SlashRef.MODID)) {
                        list.add(loc);
                    }
                }
            }

        }

        list.addAll(Arrays.asList(Chats.values()));
        list.addAll(Arrays.asList(Words.values()));
        list.addAll(Arrays.asList(StatGuiGroupSection.values()));
        list.addAll(Arrays.asList(Formatter.values()));
        list.addAll(Arrays.asList(Gui.values()));
        list.addAll(Arrays.asList(Itemtips.values()));
        list.addAll(Arrays.asList(StatGuiGroup.values()));
        list.addAll(Arrays.asList(Command.values()));

        for (StatPriority prio : StatPriority.MAP.values()) {
            list.add(prio);
        }


        //

        for (Map.Entry<TagType, List<ModTag>> entry : ModTag.MAP.entrySet()) {
            list.addAll(entry.getValue());
        }
        //
        list.addAll(Arrays.asList(GemItem.GemType.values()));
        list.addAll(Arrays.asList(GemItem.GemRank.values()));
        list.addAll(Arrays.asList(PlayerBuffData.Type.values()));
        list.addAll(Arrays.asList(ChestContent.chestTypeEnum.values()));
        list.addAll(Arrays.asList(LootModifierEnum.values()));

        HashMap<IAutoLocName.AutoLocGroup, List<IAutoLocName>> map = new HashMap<>();

        for (IAutoLocName.AutoLocGroup autoLocGroup : IAutoLocName.AutoLocGroup.values()) {
            map.put(autoLocGroup, list.stream().filter(x -> x.locNameGroup().equals(autoLocGroup)).collect(Collectors.toList())
            );
        }

        HashMap<String, List<IAutoLocName>> sortedMap = new HashMap<>();
        for (Map.Entry<IAutoLocName.AutoLocGroup, List<IAutoLocName>> entry : map.entrySet()) {
            List<IAutoLocName> sortedlist = new ArrayList<>(entry.getValue());
            CreateLangFileUtils.sortName(sortedlist);
            if (sortedlist.size() > 0) {
                sortedMap.put(entry.getValue()
                        .get(0)
                        .getGroupName(), sortedlist);
            }
        }

        return sortedMap;

    }

    public static HashMap<String, List<IAutoLocDesc>> getDescMap() {
        HashSet<IAutoLocDesc> list = CreateLangFileUtils.getFromRegistries(IAutoLocDesc.class);

        for (ExileRegistryContainer reg : Database.getAllRegistries()) {
            for (Object o : reg.getSerializable()) {
                if (o instanceof IAutoLocDesc loc && o instanceof ExileRegistry<?> r) {
                    if (r.getRegistrationInfo().modid.equals(SlashRef.MODID)) {
                        list.add(loc);
                    }
                }
            }
            for (Object o : reg.getList()) {
                if (o instanceof IAutoLocDesc loc && o instanceof ExileRegistry<?> r) {
                    if (r.getRegistrationInfo().modid.equals(SlashRef.MODID)) {
                        list.add(loc);
                    }
                }
            }

        }


        HashMap<IAutoLocName.AutoLocGroup, List<IAutoLocDesc>> map = new HashMap<>();

        for (IAutoLocName.AutoLocGroup autoLocGroup : IAutoLocName.AutoLocGroup.values()) {
            map.put(
                    autoLocGroup,
                    list.stream()
                            .filter(x -> x.locDescGroup()
                                    .equals(autoLocGroup))
                            .collect(Collectors.toList())
            );
        }

        HashMap<String, List<IAutoLocDesc>> sortedMap = new HashMap<>();
        for (Map.Entry<IAutoLocName.AutoLocGroup, List<IAutoLocDesc>> entry : map.entrySet()) {
            List<IAutoLocDesc> sortedlist = new ArrayList<>(entry.getValue());
            CreateLangFileUtils.sortDesc(sortedlist);
            if (sortedlist.size() > 0) {
                sortedMap.put(entry.getValue()
                        .get(0)
                        .getDescGroupName(), sortedlist);
            }
        }

        return sortedMap;

    }

}
