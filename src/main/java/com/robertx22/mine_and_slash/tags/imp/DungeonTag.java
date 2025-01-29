package com.robertx22.mine_and_slash.tags.imp;

import com.robertx22.mine_and_slash.tags.ModTag;
import com.robertx22.mine_and_slash.tags.NormalModTag;
import com.robertx22.mine_and_slash.tags.TagType;

import java.util.List;
import java.util.stream.Collectors;

public class DungeonTag extends NormalModTag {
    public static DungeonTag SERIALIZER = new DungeonTag("");

    public static DungeonTag of(String id) {
        return (DungeonTag) register(TagType.Dungeon, new DungeonTag(id));
    }

    public static List<DungeonTag> getAll() {
        return ModTag.MAP.get(TagType.Dungeon).stream().map(x -> (DungeonTag) x).collect(Collectors.toList());
    }

    public DungeonTag(String id) {
        super(id);
    }

    @Override
    public DungeonTag fromString(String s) {
        return new DungeonTag(s);
    }

    @Override
    public String getTagType() {
        return TagType.Dungeon.id;
    }
}
