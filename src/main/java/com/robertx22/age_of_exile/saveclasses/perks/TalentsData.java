package com.robertx22.age_of_exile.saveclasses.perks;

import com.robertx22.age_of_exile.capability.entity.EntityData;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.database.registry.ExileDB;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkButton;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.SelfCheckTask;
import com.robertx22.age_of_exile.saveclasses.ExactStatData;
import com.robertx22.age_of_exile.saveclasses.PointData;
import com.robertx22.age_of_exile.saveclasses.gearitem.gear_bases.IStatCtx;
import com.robertx22.age_of_exile.saveclasses.unit.stat_ctx.SimpleStatCtx;
import com.robertx22.age_of_exile.saveclasses.unit.stat_ctx.StatContext;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.utilityclasses.ClientOnly;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class TalentsData implements IStatCtx {


    public int reset_points = 5;
    HashMap<TalentTree.SchoolType, SchoolData> perks = new HashMap<>();

    public int getAllocatedPoints(TalentTree.SchoolType type) {
        return getSchool(type).getAllocatedPointsInSchool();
    }


    public SchoolData getSchool(TalentTree.SchoolType type) {
        return perks.computeIfAbsent(type, x -> new SchoolData()); // todo check if this works how i think it should
    }

    public HashMap<TalentTree.SchoolType, SchoolData> getPerks() {
        return perks;
    }

    public void allocate(Player player, TalentTree school, PointData point) {
        getSchool(school.getSchool_type()).allocate(point);
    }

    public void remove(TalentTree.SchoolType school, PointData point) {
        getSchool(school).unAllocate(point);
    }

    public boolean hasFreePoints(EntityData data, TalentTree.SchoolType type) {
        return type.getFreePoints(data, this) > 0;

    }

    public boolean canAllocate(TalentTree school, PointData point, EntityData data, Player player) {
        if (!hasFreePoints(data, school.getSchool_type())) {
            return false;
        }

        Perk perk = school.calcData.getPerk(point);


        if (perk.one_kind != null && !perk.one_kind.isEmpty()) {
            return getAllAllocatedPerks(school.getSchool_type()).values().stream().noneMatch(x -> x.one_kind != null && x.one_kind.equals(perk.one_kind));
        }

        // todo you can take multiple starts??

        if (!perk.is_entry) {
            Set<PointData> con = school.calcData.connections.get(point);

            if (con == null || !con.stream()
                    .anyMatch(x -> getSchool(school.getSchool_type())
                            .isAllocated(x))) {
                return false;
            }
        }

        return true;

    }

    public boolean canRemove(TalentTree school, PointData point) {

        if (!getSchool(school.getSchool_type()).isAllocated(point)) {
            return false;
        }

        if (reset_points < 1) {
            return false;
        }

        for (PointData con : school.calcData.connections.get(point)) {
            if (getSchool(school.getSchool_type()).isAllocated(con)) {
                Perk perk = school.calcData.getPerk(con);
                if (perk.is_entry) {
                    continue;
                }
                if (!hasPathToStart(school, con, point)) {
                    return false;
                }
            }
        }

        return true;

    }

    private boolean hasPathToStart(TalentTree school, PointData check, PointData toRemove) {
        Queue<PointData> openSet = new ArrayDeque<>();

        openSet.addAll(school.calcData.connections.get(check));

        Set<PointData> closedSet = new HashSet<>();

        while (!openSet.isEmpty()) {
            PointData current = openSet.poll();

            Perk perk = school.calcData.getPerk(current);

            if (current.equals(toRemove) || !getSchool(school.getSchool_type()).isAllocated(current)) {
                continue; // skip exploring this path
            }

            if (perk.is_entry) {
                return true;
            }

            if (!closedSet.add(current)) {
                continue; // we already visited it
            }

            openSet.addAll(school.calcData.connections.get(current));

        }

        return false;
    }

    public void clearAllTalents() {

        this.perks = new HashMap<>();


    }

    public HashMap<PointData, Perk> getAllAllocatedPerks(TalentTree.SchoolType type) {

        HashMap<PointData, Perk> perks = new HashMap<>();

        ExileDB.TalentTrees().getFilterWrapped(x -> x.getSchool_type() == type).list.stream().findFirst().ifPresent(x -> {
            for (PointData p : this.getSchool(type).getAllocatedPoints(x)) {
                perks.put(p, x.calcData.getPerk(p));
            }
        });

        return perks;
    }

    public PerkStatus getStatus(Player player, TalentTree school, PointData point) {
        if (isAllocated(school, point)) {
            return PerkStatus.CONNECTED;
        }
        if (canAllocate(school, point, Load.Unit(player), player)) {
            return PerkStatus.POSSIBLE;
        } else {
            return PerkStatus.BLOCKED;
        }
    }

    // use getStatus in a per-tick situation will affect performance a lot, this is a lazy version replacement.
    // invoke this when clicking a button
    public void updateRelatedButtonStatus(PerkButton button) {
        // no need to check free point here, move this check to self check.
        //if (!hasFreePoints(data, school.getSchool_type())) return;


        CompletableFuture.runAsync(() ->
        {
            TalentTree school = button.school;
            TalentTree.SchoolType schoolType = school.getSchool_type();
            Perk perk = school.calcData.getPerk(button.point);

            //wait for the playerData update.
            SelfCheckTask waitingStatusUpdate = new SelfCheckTask(x -> {
                System.out.println("lazy is: " + x.lazyStatus);
                System.out.println("current is: " + getStatus(ClientOnly.getPlayer(), x.school, x.point));
                return x.lazyStatus != getStatus(ClientOnly.getPlayer(), x.school, x.point);
            }, x -> {
                x.lazyStatus = getStatus(ClientOnly.getPlayer(), x.school, x.point);
                x.updateTexture();
                System.out.println("wait done!");
            }, 1000);

            //if this perk is a one-kind perk, send the self-check task to their checklist.
            if (perk.one_kind != null && !perk.one_kind.isEmpty()) {

                Set<String> qualifiedPerk = getAllAllocatedPerks(schoolType).values().stream().filter(x -> x.one_kind != null && x.one_kind.equals(perk.one_kind)).map(x -> x.id).collect(Collectors.toSet());
                Iterator<PerkButton> iterator = new ArrayList<>(button.getScreen().pointPerkButtonMap.values()).iterator();
                while (iterator.hasNext()) {
                    PerkButton next = iterator.next();
                    if (qualifiedPerk.contains(next.perk.id)) {
                        waitingStatusUpdate.sendTo(next);
                    }
                }

            }

            Set<PointData> con = school.calcData.connections.get(button.point);
            // todo if player don't have free points, and it closes the skill screen, this task will lose forever, it means that if a player closes the screen, and reopen it with no free point, and they get a new free point when this reopened screen is on, the button's status won't change at all, player must reopen the screen again to get the right status.

            con.stream().filter(x -> x != null && !getSchool(schoolType).isAllocated(x)).forEach(x -> waitingStatusUpdate.sendTo(button.getScreen().pointPerkButtonMap.get(x)));
            waitingStatusUpdate.sendTo(button);

        });
    }

    public Perk.Connection getConnection(TalentTree school, PointData one, PointData two) {

        if (isAllocated(school, one)) {
            if (isAllocated(school, two)) {
                return Perk.Connection.LINKED;
            }
            return Perk.Connection.POSSIBLE;
        } else if (isAllocated(school, two)) {
            return Perk.Connection.POSSIBLE;
        }

        return Perk.Connection.BLOCKED;
    }


    public boolean isAllocated(TalentTree school, PointData point) {
        return getSchool(school.getSchool_type()).isAllocated(point);
    }

    @Override
    public List<StatContext> getStatAndContext(LivingEntity en) {
        List<StatContext> ctx = new ArrayList<>();

        for (TalentTree.SchoolType type : TalentTree.SchoolType.values()) {

            HashMap<PointData, Perk> map = getAllAllocatedPerks(type);

            int lvl = Load.Unit(en).getLevel();

            List<ExactStatData> stats = new ArrayList<>();

            map.forEach((key, value) -> {
                value.stats.forEach(s -> stats.add(s.toExactStat(lvl)));
            });

            ctx.add(new SimpleStatCtx(StatContext.StatCtxType.TALENT, stats));
        }
        return ctx;
    }

}
