package com.robertx22.mine_and_slash.tags.imp;

import com.robertx22.mine_and_slash.tags.ModTag;
import com.robertx22.mine_and_slash.tags.NormalModTag;
import com.robertx22.mine_and_slash.tags.TagType;

import java.util.List;
import java.util.stream.Collectors;

public class SpellTag extends NormalModTag {
    public static SpellTag SERIALIZER = new SpellTag("");

    public static SpellTag of(String id) {
        return (SpellTag) register(TagType.Spell, new SpellTag(id));
    }

    public static List<SpellTag> getAll() {
        return ModTag.MAP.get(TagType.Spell).stream().map(x -> (SpellTag) x).collect(Collectors.toList());
    }

    public SpellTag(String id) {
        super(id);
    }

    @Override
    public ModTag fromString(String s) {
        return new SpellTag(s);
    }

    @Override
    public String getTagType() {
        return TagType.Spell.id;
    }
}
