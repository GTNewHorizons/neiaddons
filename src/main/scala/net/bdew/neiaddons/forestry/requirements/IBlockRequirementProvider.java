/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.List;

import forestry.api.genetics.IAlleleSpecies;

public interface IBlockRequirementProvider {

    RequirementKind getKind();

    List<BlockRequirement> getRequirements(Object subject);

    /**
     * species is null when only the mutation is known.
     */
    List<BlockRequirement> getRequirements(Object subject, IAlleleSpecies species);
}
