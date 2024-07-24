package com.robertx22.age_of_exile.gui.screens.skill_tree.connections;

import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkPointPair;

public class PerkConnectionRenderer {

    public PerkPointPair pair;
    public Perk.Connection connection;

    public PerkConnectionRenderer(PerkPointPair pair, Perk.Connection connection) {
        this.pair = pair;
        this.connection = connection;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof PerkConnectionRenderer p) {
            return this.pair.equals(p.pair);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    public int hashCodeWithConnection(){
        long result = 17;
        result = 29 * result + pair.hashCode() | connection.hashCode();

        return (int) result;
    }

    @Override
    public String toString() {
        return "PerkConnectionRender2{" +
                "pair=" + pair +
                ", connection=" + connection +
                '}';
    }
}
