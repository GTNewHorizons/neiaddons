/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.butterflies;

import java.util.Collection;

import net.bdew.neiaddons.Utils;
import net.bdew.neiaddons.forestry.AddonForestry;
import net.bdew.neiaddons.forestry.GeneticsUtils;

import codechicken.nei.api.API;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.HandlerInfo;
import forestry.api.genetics.AlleleManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.core.config.Constants;

public class ButterflyHelper {

    public static Collection<IAlleleButterflySpecies> allSpecies;

    public static IButterflyRoot root;

    public static void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
        if (AddonForestry.showButterflyMutations) {
            event.registerHandlerInfo(
                    new HandlerInfo.Builder(ButterflyBreedingHandler.class, Constants.MOD, Constants.ID)
                            .setShowOverlayButton(false).setShowFavoritesButton(false)
                            .setDisplayStack("Forestry:butterflyGE", null).build());
        }
    }

    public static void setup() {
        root = (IButterflyRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies");

        if (root == null) {
            AddonForestry.instance
                    .logWarning("Butterfly Species Root not found, some functionality will be unavailable");
            return;
        }

        allSpecies = GeneticsUtils.getAllButterflySpecies(AddonForestry.loadBlacklisted);

        if (AddonForestry.showButterflyMutations) {
            ButterflyBreedingHandler breedingRecipeHandler = new ButterflyBreedingHandler();
            API.registerRecipeHandler(breedingRecipeHandler);
            API.registerUsageHandler(breedingRecipeHandler);
            AddonForestry.instance.registerWithNEIPlugins(
                    breedingRecipeHandler.getRecipeName(),
                    breedingRecipeHandler.getRecipeIdent());
        }

        // productsCache = new HashMap<Integer, Collection<IAlleleSpecies>>();

        for (IAlleleButterflySpecies species : allSpecies) {
            if (AddonForestry.addBees) {
                Utils.safeAddNBTItem(GeneticsUtils.stackFromSpecies(species, EnumFlutterType.BUTTERFLY.ordinal()));
                Utils.safeAddNBTItem(GeneticsUtils.stackFromSpecies(species, EnumFlutterType.CATERPILLAR.ordinal()));
                Utils.safeAddNBTItem(GeneticsUtils.stackFromSpecies(species, EnumFlutterType.SERUM.ordinal()));
            }
        }
    }
}
