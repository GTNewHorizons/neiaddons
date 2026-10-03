/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.ArrayList;
import java.util.List;

import net.bdew.neiaddons.forestry.AddonForestry;

import forestry.api.genetics.IAlleleSpecies;

public class RequirementResolvers {

    public static final List<IBlockRequirementProvider> PROVIDERS = new ArrayList<IBlockRequirementProvider>();

    public static void register(IBlockRequirementProvider provider) {
        if (provider != null && !PROVIDERS.contains(provider)) {
            PROVIDERS.add(provider);
        }
    }

    public static List<BlockRequirement> resolve(Object subject, IAlleleSpecies species, RequirementKind... kinds) {
        List<BlockRequirement> result = new ArrayList<BlockRequirement>();
        if (subject == null) return result;

        for (IBlockRequirementProvider provider : PROVIDERS) {
            if (!accepts(provider, kinds)) continue;
            try {
                List<BlockRequirement> contributed = provider.getRequirements(subject, species);
                if (contributed == null) continue;
                for (BlockRequirement requirement : contributed) {
                    if (requirement != null && !requirement.isEmpty()) {
                        result.add(requirement);
                    }
                }
            } catch (Throwable t) {
                AddonForestry.instance.logWarningExc(
                        t,
                        "Requirement provider %s failed for %s",
                        provider.getClass().getName(),
                        subject);
            }
        }

        return result;
    }

    public static List<BlockRequirement> resolve(Object subject, RequirementKind... kinds) {
        return resolve(subject, subject instanceof IAlleleSpecies ? (IAlleleSpecies) subject : null, kinds);
    }

    private static boolean accepts(IBlockRequirementProvider provider, RequirementKind[] kinds) {
        if (kinds == null || kinds.length == 0) return true;
        for (RequirementKind kind : kinds) {
            if (kind == provider.getKind()) return true;
        }
        return false;
    }
}
