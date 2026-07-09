/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry;

import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.bdew.neiaddons.Utils;
import net.bdew.neiaddons.utils.LabeledPositionedStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import forestry.api.apiculture.IAlleleBeeSpeciesCustom;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;

public abstract class BaseProduceRecipeHandler extends TemplateRecipeHandler {

    private static final int ROW_HEIGHT = 27;
    private static final DecimalFormat productChance = new DecimalFormat("0.#");
    private final ISpeciesRoot speciesRoot;
    private final Map<Item, Collection<IAlleleSpecies>> cache;

    public BaseProduceRecipeHandler(ISpeciesRoot root) {
        this.speciesRoot = root;
        cache = getProduceCache();
    }

    public class CachedProduceRecipe extends CachedRecipe {

        private LabeledPositionedStack producer;
        private final ArrayList<LabeledPositionedStack> products;
        private final int numProductRows, numSpecRows, height;

        public CachedProduceRecipe(IAlleleSpecies species) {
            ItemStack producerStack = GeneticsUtils.stackFromSpecies(species, GeneticsUtils.RecipePosition.Producer);
            if (producerStack == null) {
                AddonForestry.instance.logWarning("Producer is null... wtf? species = %s", species.getUID());
            } else {
                producer = new LabeledPositionedStack(producerStack, 22, 19, species.getName(), 13);
            }

            products = new ArrayList<>();

            int i = 0;
            for (Entry<ItemStack, Float> product : Utils.mergeStacks(GeneticsUtils.getProduceFromSpecies(species))
                    .entrySet()) {
                String label = productChance.format(product.getValue() * 100f) + "%";
                int x = 96 + 22 * (i % 3);
                int y = 8 + (i / 3) * ROW_HEIGHT;
                products.add(new LabeledPositionedStack(product.getKey(), x, y, label, 10));
                i++;
            }
            numProductRows = Math.max((int) Math.ceil(i / 3.0), 1);

            String jubilance = null;
            if (species instanceof IAlleleBeeSpeciesCustom) {
                IJubilanceProvider provider = ((IAlleleBeeSpeciesCustom) species).getJubilanceProvider();
                if (provider != null) jubilance = provider.getDescription();
            }

            i = 0;
            for (Entry<ItemStack, Float> product : Utils.mergeStacks(GeneticsUtils.getSpecialtyFromSpecies(species))
                    .entrySet()) {
                String label = productChance.format(product.getValue() * 100f) + "%";
                int x = 96 + 22 * (i % 3);
                int y = 36 + ((i / 3) + (numProductRows - 1)) * ROW_HEIGHT;
                if (jubilance != null) products.add(
                        new LabeledPositionedStack(
                                product.getKey(),
                                x,
                                y,
                                label,
                                10,
                                EnumChatFormatting.GRAY + jubilance));
                else products.add(new LabeledPositionedStack(product.getKey(), x, y, label, 10));
                i++;
            }
            numSpecRows = Math.max((int) Math.ceil(i / 3.0), 1);

            height = (numProductRows + numSpecRows) * ROW_HEIGHT + 10;
        }

        public boolean isNoOutput() {
            return products.isEmpty();
        }

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> list = new ArrayList<>();
            list.add(producer);
            return list;
        }

        @Override
        public ArrayList<PositionedStack> getOtherStacks() {
            ArrayList<PositionedStack> list = new ArrayList<>();
            if (products.size() > 1) {
                for (int i = 1; i < products.size(); i++) {
                    list.add(products.get(i));
                }
            }
            return list;
        }

        @Override
        public PositionedStack getResult() {
            if (!products.isEmpty()) {
                return products.get(0);
            } else {
                return null;
            }
        }

        public ArrayList<LabeledPositionedStack> getProducts() {
            return products;
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("item")) {
            loadCraftingRecipes((ItemStack) results[0]);
            return;
        }

        if (!outputId.equals(getRecipeIdent())) {
            return;
        }

        for (IAlleleSpecies species : getAllSpecies()) {
            CachedProduceRecipe rec = new CachedProduceRecipe(species);
            if (!rec.isNoOutput()) {
                arecipes.add(rec);
            }
        }
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe<?> gui, ItemStack stack, List<String> currenttip, int recipe) {
        CachedProduceRecipe crecipe = (CachedProduceRecipe) this.arecipes.get(recipe);
        for (LabeledPositionedStack positionedStack : crecipe.getProducts()) {
            if (positionedStack.containsWithNBT(stack)) {
                currenttip.addAll(positionedStack.getTooltip());
                break;
            }
        }
        return currenttip;
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (cache == null) return;
        if (result == null) {
            AddonForestry.instance.logWarning("loadCraftingRecipes() called with null, something is FUBAR.");
            return;
        }
        if (!cache.containsKey(result.getItem())) {
            return;
        }
        for (IAlleleSpecies species : cache.get(result.getItem())) {
            CachedProduceRecipe recipe = new CachedProduceRecipe(species);
            for (LabeledPositionedStack stack : recipe.products) {
                if (NEIClientUtils.areStacksSameTypeCrafting(stack.item, result)) {
                    arecipes.add(recipe);
                    break;
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (!speciesRoot.isMember(ingredient)) {
            return;
        }
        IIndividual member = speciesRoot.getMember(ingredient);
        if (member == null || member.getGenome() == null || member.getGenome().getPrimary() == null) {
            AddonForestry.instance
                    .logWarning("Individual or genome is null searching recipe for %s", ingredient.toString());
            return;
        }
        arecipes.add(new CachedProduceRecipe(member.getGenome().getPrimary()));
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(48, 22, 21, 15), getRecipeIdent()));
    }

    @Override
    public void drawBackground(int recipe) {
        CachedProduceRecipe rec = (CachedProduceRecipe) arecipes.get(recipe);
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());

        int y = 7;
        // Top
        GuiDraw.drawTexturedModalRect(4, 4, 0, 0, 160, 3);
        // Prod
        for (int i = 0; i < rec.numProductRows; i++) {
            GuiDraw.drawTexturedModalRect(4, y, 0, 3, 160, ROW_HEIGHT);
            y += ROW_HEIGHT;
        }
        // Spec
        for (int i = 0; i < rec.numSpecRows; i++) {
            GuiDraw.drawTexturedModalRect(4, y, 0, 3 + ROW_HEIGHT, 160, ROW_HEIGHT);
            y += ROW_HEIGHT;
        }
        // Bottom
        GuiDraw.drawTexturedModalRect(4, y, 0, 58, 160, 2);

        // Input BG
        GuiDraw.drawTexturedModalRect(19, 16, 160, 0, 51, 22);
    }

    @Override
    public void drawExtras(int recipe) {
        CachedProduceRecipe rec = (CachedProduceRecipe) arecipes.get(recipe);
        rec.producer.drawLabel();
        for (LabeledPositionedStack stack : rec.products) {
            stack.drawLabel();
        }
        FontRenderer f = Minecraft.getMinecraft().fontRenderer;
        f.drawString("Prod:", 65, 8 + 4, 0xFFFFFF);
        f.drawString("Spec:", 65, 36 + 4 + (rec.numProductRows - 1) * ROW_HEIGHT, 0xFFF200);
    }

    public abstract String getRecipeIdent();

    public abstract Collection<? extends IAlleleSpecies> getAllSpecies();

    public abstract Map<Item, Collection<IAlleleSpecies>> getProduceCache();

    @Override
    public String getGuiTexture() {
        return "neiaddons:textures/gui/products_dynamic.png";
    }

    @Override
    public final String getRecipeName() {
        return I18n.format("bdew.neiaddons.produce." + getRecipeIdent());
    }

    @Override
    public int getRecipeHeight(int recipe) {
        CachedProduceRecipe rec = (CachedProduceRecipe) arecipes.get(recipe);
        return rec.height;
    }
}
