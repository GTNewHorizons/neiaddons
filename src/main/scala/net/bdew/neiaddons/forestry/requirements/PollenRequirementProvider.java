/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.Collections;
import java.util.List;

import net.bdew.neiaddons.forestry.GeneticsUtils;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.IAlleleSpecies;

public class PollenRequirementProvider implements IBlockRequirementProvider {

    @Override
    public RequirementKind getKind() {
        return RequirementKind.POLLEN;
    }

    @Override
    public List<BlockRequirement> getRequirements(Object subject) {
        return getRequirements(subject, subject instanceof IAlleleSpecies ? (IAlleleSpecies) subject : null);
    }

    @Override
    public List<BlockRequirement> getRequirements(Object subject, IAlleleSpecies species) {
        if (!(species instanceof IAlleleTreeSpecies)) return Collections.emptyList();

        ItemStack pollen = GeneticsUtils.stackFromSpecies(species, GeneticsUtils.RecipePosition.Parent2);
        if (!RequirementStacks.isRenderable(pollen)) return Collections.emptyList();

        return Collections.singletonList(new BlockRequirement(pollen, "bdew.neiaddons.requirement.pollen"));
    }
}
