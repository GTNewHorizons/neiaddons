/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IMutationCondition;
import forestry.core.genetics.mutations.Mutation;
import forestry.core.genetics.mutations.MutationConditionRequiresResource;
import forestry.core.genetics.mutations.MutationConditionRequiresResourceOreDict;

public class MutationConditionRequirementProvider implements IBlockRequirementProvider {

    public static final String LABEL_KEY = "bdew.neiaddons.requirement.blocks";

    @Override
    public RequirementKind getKind() {
        return RequirementKind.MUTATION_RESOURCE;
    }

    @Override
    public List<BlockRequirement> getRequirements(Object subject) {
        return getRequirements(subject, null);
    }

    @Override
    public List<BlockRequirement> getRequirements(Object subject, IAlleleSpecies species) {
        List<BlockRequirement> result = new ArrayList<BlockRequirement>();
        for (IMutationCondition condition : getResourceConditions(subject)) {
            List<ItemStack> candidates = getCandidates(condition);
            if (!candidates.isEmpty()) {
                result.add(new BlockRequirement(candidates, LABEL_KEY));
            }
        }
        return result;
    }

    private List<IMutationCondition> getResourceConditions(Object subject) {
        if (!(subject instanceof Mutation)) return Collections.emptyList();

        List<IMutationCondition> result = new ArrayList<IMutationCondition>();
        for (IMutationCondition condition : ((Mutation) subject).getMutationConditions()) {
            if (condition == null) continue;
            if (condition instanceof MutationConditionRequiresResource
                    || condition instanceof MutationConditionRequiresResourceOreDict) {
                result.add(condition);
            }
        }
        return result;
    }

    private List<ItemStack> getCandidates(IMutationCondition condition) {
        if (condition instanceof MutationConditionRequiresResourceOreDict) {
            return getOreDictCandidates(condition);
        }
        return getBlockCandidates(condition);
    }

    private List<ItemStack> getBlockCandidates(IMutationCondition condition) {
        if (!(condition instanceof MutationConditionRequiresResource)) return Collections.emptyList();
        ItemStack stack = ((MutationConditionRequiresResource) condition).getBlockRequired();
        return RequirementStacks.fromBlock(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
    }

    private List<ItemStack> getOreDictCandidates(IMutationCondition condition) {
        if (!(condition instanceof MutationConditionRequiresResourceOreDict)) return Collections.emptyList();
        String oreName = OreDictionary
                .getOreName(((MutationConditionRequiresResourceOreDict) condition).getOreDictId());
        if (oreName == null || oreName.isEmpty()) return Collections.emptyList();

        List<ItemStack> result = new ArrayList<ItemStack>();
        for (ItemStack ore : OreDictionary.getOres(oreName)) {
            RequirementStacks.add(result, ore);
        }
        return result;
    }

}
