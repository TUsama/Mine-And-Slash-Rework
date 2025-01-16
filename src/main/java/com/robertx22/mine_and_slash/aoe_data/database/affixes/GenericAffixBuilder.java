package com.robertx22.mine_and_slash.aoe_data.database.affixes;

import com.robertx22.mine_and_slash.database.data.StatMod;
import com.robertx22.mine_and_slash.database.data.affixes.Affix;
import com.robertx22.mine_and_slash.database.data.requirements.Requirements;
import com.robertx22.mine_and_slash.database.data.requirements.TagRequirement;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.tags.ModTag;
import com.robertx22.mine_and_slash.tags.TagType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericAffixBuilder<T> {

    List<T> elements = new ArrayList<>();

    int weight = 1000;
    Affix.AffixSlot type;

    TagRequirement tagRequirement = new TagRequirement(TagType.GearSlot, new ArrayList<>(), new ArrayList<>());

    Function<T, String> guid;
    Function<T, List<StatMod>> stats;

    HashMap<T, String> nameMap = new HashMap<>();

    public GenericAffixBuilder<T> guid(Function<T, String> guid) {
        this.guid = guid;
        return this;
    }

    public GenericAffixBuilder<T> add(T element, String name) {
        this.nameMap.put(element, name);
        this.elements.add(element);
        return this;
    }

    public GenericAffixBuilder<T> stats(Function<T, List<StatMod>> mods) {
        this.stats = mods;
        return this;
    }

    public GenericAffixBuilder<T> includesTags(ModTag... tags) {
        this.tagRequirement.included.addAll(Arrays.stream(tags)
                .map(x -> x.GUID())
                .collect(Collectors.toList()));
        return this;
    }

    public GenericAffixBuilder<T> excludesTags(ModTag... tags) {
        this.tagRequirement.excluded.addAll(Arrays.stream(tags)
                .map(x -> x.GUID())
                .collect(Collectors.toList()));
        return this;
    }

    public GenericAffixBuilder<T> Weight(int weight) {
        this.weight = weight;
        return this;
    }


    public GenericAffixBuilder<T> Prefix() {
        type = Affix.AffixSlot.prefix;
        return this;
    }

    public GenericAffixBuilder<T> Suffix() {
        type = Affix.AffixSlot.suffix;
        return this;
    }

    public GenericAffixBuilder<T> Jewel() {
        type = Affix.AffixSlot.jewel;
        return this;
    }


    public GenericAffixBuilder<T> Implicit() {
        type = Affix.AffixSlot.implicit;
        return this;
    }

    public GenericAffixBuilder<T> craftedUniqueJewel() {
        type = Affix.AffixSlot.crafted_jewel_unique;
        return this;
    }


    public void Build() {

        for (T element : elements) {

            Affix affix = new Affix();
            affix.guid = guid.apply(element);
            affix.requirements = new Requirements(this.tagRequirement);

            affix.stats = stats.apply(element);

            affix.type = type;
            affix.weight = weight;
            affix.loc_name = nameMap.get(element);

            affix.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        }

    }

}
