/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import codechicken.nei.PositionedStack;

public class RequirementRenderer {

    public static final int SLOT_PITCH = 20;

    private final List<PositionedStack> slots;

    public RequirementRenderer(List<BlockRequirement> requirements, int originX, int originY) {
        List<PositionedStack> built = new ArrayList<PositionedStack>();
        for (BlockRequirement requirement : requirements) {
            if (requirement == null || requirement.isEmpty()) continue;
            PositionedStack slot = new PositionedStack(
                    requirement.getCandidates(),
                    originX + built.size() * SLOT_PITCH,
                    originY);
            slot.setTooltip(Collections.singletonList(StatCollector.translateToLocal(requirement.getLabelKey())));
            built.add(slot);
        }
        this.slots = Collections.unmodifiableList(built);
    }

    public static RequirementRenderer empty() {
        return new RequirementRenderer(Collections.<BlockRequirement>emptyList(), 0, 0);
    }

    public List<PositionedStack> asStacks() {
        return slots;
    }

    public boolean isEmpty() {
        return slots.isEmpty();
    }

    public boolean contains(ItemStack stack) {
        if (stack == null) return false;
        for (PositionedStack slot : slots) {
            if (slot.containsWithNBT(stack)) return true;
        }
        return false;
    }

}
