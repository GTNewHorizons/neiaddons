/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bdew.neiaddons.forestry.GeneticsUtils;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;

public class FlowerRequirementProvider implements IBlockRequirementProvider {

    public final Map<String, List<ItemStack>> flowerCache = new HashMap<String, List<ItemStack>>();

    @Override
    public RequirementKind getKind() {
        return RequirementKind.FLOWERS;
    }

    @Override
    public List<BlockRequirement> getRequirements(Object subject) {
        return getRequirements(subject, subject instanceof IAlleleSpecies ? (IAlleleSpecies) subject : null);
    }

    @Override
    public List<BlockRequirement> getRequirements(Object subject, IAlleleSpecies species) {
        String flowerType = getFlowerType(species);
        if (flowerType == null) return Collections.emptyList();

        List<ItemStack> flowers = resolveFlowers(flowerType);
        if (flowers.isEmpty()) return Collections.emptyList();

        List<BlockRequirement> result = new ArrayList<BlockRequirement>();
        result.add(new BlockRequirement(flowers, "bdew.neiaddons.requirement.flowers"));
        return result;
    }

    private String getFlowerType(IAlleleSpecies species) {
        if (species == null) return null;

        try {
            IGenome genome = getGenome(species);
            if (genome == null) return null;

            // The flower chromosome is the only one holding an IAlleleFlowers allele.
            for (IChromosome chromosome : genome.getChromosomes()) {
                if (chromosome == null) continue;
                String flowerType = flowerTypeOf(chromosome.getActiveAllele());
                if (flowerType != null) return flowerType;
            }
            return null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private IGenome getGenome(IAlleleSpecies species) {
        ItemStack stack = GeneticsUtils.stackFromSpecies(species, GeneticsUtils.RecipePosition.Producer);
        if (stack == null) return null;

        IIndividual individual = species.getRoot().getMember(stack);
        return individual == null ? null : individual.getGenome();
    }

    private String flowerTypeOf(Object allele) {
        if (!(allele instanceof IAlleleFlowers)) return null;

        IFlowerProvider provider = ((IAlleleFlowers) allele).getProvider();
        if (provider == null) return null;

        String flowerType = provider.getFlowerType();
        // The end flower type has no block of its own, any end block counts.
        if (flowerType == null || flowerType.equals(FlowerManager.FlowerTypeEnd)) return null;
        return flowerType;
    }

    private List<ItemStack> resolveFlowers(String flowerType) {
        List<ItemStack> cached = flowerCache.get(flowerType);
        if (cached != null) return cached;

        List<ItemStack> result = new ArrayList<ItemStack>();
        IFlowerRegistry registry = FlowerManager.flowerRegistry;
        if (registry != null) {
            try {
                for (IFlower flower : registry.getAcceptableFlowers(flowerType)) {
                    if (flower == null) continue;
                    for (ItemStack stack : RequirementStacks.fromBlock(flower.getBlock(), flower.getMeta())) {
                        RequirementStacks.add(result, stack);
                    }
                }
            } catch (Throwable ignored) {}
        }

        result = Collections.unmodifiableList(result);
        flowerCache.put(flowerType, result);
        return result;
    }
}
