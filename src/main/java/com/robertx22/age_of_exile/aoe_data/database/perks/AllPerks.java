package com.robertx22.age_of_exile.aoe_data.database.perks;

import com.robertx22.age_of_exile.aoe_data.database.perks.asc.*;
import com.robertx22.library_of_exile.registry.ExileRegistryInit;

public class AllPerks implements ExileRegistryInit {

    @Override
    public void registerAll() {

        new RangerPerks().registerAll();
        new PaladinPerks().registerAll();
        new SummonerPerks().registerAll();
        new WarlockPerks().registerAll();
        new ElementalistPerks().registerAll();

        new Perks().registerAll();
        //  PerksAddtl().registerAll();
        new GameChangerPerks().registerAll();
        //new GameChangerPerksAddtl().registerAll();
        new StartPerks().registerAll();
        //new StartPerksAddtl().registerAll();
      
        new SpellPassives().registerAll();
    }
}
