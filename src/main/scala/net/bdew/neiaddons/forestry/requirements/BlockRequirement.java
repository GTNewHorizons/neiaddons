/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

public class BlockRequirement {

    public final List<ItemStack> candidates;
    public final String labelKey;

    public BlockRequirement(ItemStack candidate, String labelKey) {
        this(Collections.singletonList(candidate), labelKey);
    }

    public BlockRequirement(List<ItemStack> candidates, String labelKey) {
        this.candidates = Collections.unmodifiableList(RequirementStacks.sanitize(candidates));
        this.labelKey = labelKey == null ? "bdew.neiaddons.requirement.block" : labelKey;
    }

    public List<ItemStack> getCandidates() {
        return candidates;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public boolean isEmpty() {
        return candidates.isEmpty();
    }
}
