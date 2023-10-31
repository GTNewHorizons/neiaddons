/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.exnihilo;

import net.bdew.neiaddons.Utils;
import net.bdew.neiaddons.exnihilo.waila.BarrelHandler;
import net.bdew.neiaddons.exnihilo.waila.BeeTrapHandler;
import net.bdew.neiaddons.exnihilo.waila.CrucibleHandler;
import net.minecraft.tileentity.TileEntity;

import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaHandler {

    public static Class<? extends TileEntity> clsTeBarrel;
    public static Class<? extends TileEntity> clsTeCrucible;
    public static Class<? extends TileEntity> clsTeBeeTrap;

    private static void loadClasses() throws ClassNotFoundException {
        clsTeBarrel = Utils.getAndCheckClass("exnihilo.blocks.tileentities.TileEntityBarrel", TileEntity.class);
        clsTeCrucible = Utils.getAndCheckClass("exnihilo.blocks.tileentities.TileEntityCrucible", TileEntity.class);
        clsTeBeeTrap = Utils.getAndCheckClass("exnihilo.blocks.tileentities.TileEntityBeeTrap", TileEntity.class);
    }

    public static void loadCallback(IWailaRegistrar reg) {
        try {
            loadClasses();
            reg.registerBodyProvider(new CrucibleHandler(), clsTeCrucible);
            reg.registerBodyProvider(new BarrelHandler(), clsTeBarrel);
            reg.registerBodyProvider(new BeeTrapHandler(), clsTeBeeTrap);
        } catch (Throwable t) {
            AddonExnihilo.instance.logWarning("WAILA support load failed: %s", t.toString());
        }
    }
}
