/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RequirementStacks {

    public static List<ItemStack> fromBlock(Block block, int meta) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        if (block == null || block == Blocks.air) return result;

        Item item = Item.getItemFromBlock(block);
        if (item == null) return result;

        if (meta == OreDictionary.WILDCARD_VALUE) {
            List<ItemStack> variants = new ArrayList<ItemStack>();
            try {
                item.getSubItems(item, null, variants);
            } catch (Throwable ignored) {}
            for (ItemStack variant : variants) {
                add(result, variant);
            }
        }

        if (result.isEmpty()) {
            add(result, new ItemStack(item, 1, meta == OreDictionary.WILDCARD_VALUE ? 0 : meta));
        }
        return result;
    }

    public static List<ItemStack> sanitize(List<ItemStack> stacks) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        if (stacks != null) {
            for (ItemStack stack : stacks) {
                add(result, stack);
            }
        }
        return result;
    }

    public static void add(List<ItemStack> target, ItemStack stack) {
        if (!isRenderable(stack)) return;
        for (ItemStack existing : target) {
            if (existing.getItem() == stack.getItem() && existing.getItemDamage() == stack.getItemDamage()) return;
        }
        target.add(stack.copy());
    }

    public static boolean isRenderable(ItemStack stack) {
        return stack != null && stack.getItem() != null && stack.getItemDamage() != OreDictionary.WILDCARD_VALUE;
    }
}
