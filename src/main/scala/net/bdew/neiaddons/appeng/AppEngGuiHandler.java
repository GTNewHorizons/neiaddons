/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/neiaddons
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.appeng;

import codechicken.nei.api.INEIGuiAdapter;
import net.bdew.neiaddons.network.ClientHandler;
import net.bdew.neiaddons.network.PacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class AppEngGuiHandler extends INEIGuiAdapter {
    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mouseX, int mouseY, ItemStack draggedStack, int button) {
        if (AddonAppeng.clsBaseGui.isInstance(gui) && !checkBlacklist(gui)) {
            final Slot currentSlot = getSlotAtPosition(gui, mouseX, mouseY);

            if (AddonAppeng.clsSlotFake.isInstance(currentSlot) && SlotHelper.isSlotEnabled(currentSlot)) {

                if (ClientHandler.enabledCommands.contains(AddonAppeng.setWorkbenchCommand)) {
                    final ItemStack stack = draggedStack.copy();

                    stack.stackSize = Math.min(stack.stackSize, 127);

                    if (button == 0) { // left
                        setWorkbenchCommand(currentSlot.slotNumber, stack, true);
                    } else if (button == 1) { // right
                        stack.stackSize = 1;
                        setWorkbenchCommand(currentSlot.slotNumber, stack, false);
                    } else if (button == 2) { // middle
                        setWorkbenchCommand(currentSlot.slotNumber, stack, true);
                        draggedStack.stackSize -= stack.stackSize;
                    }

                    return true;
                } else {
                    Minecraft.getMinecraft()
                            .thePlayer
                            .addChatMessage(new ChatComponentTranslation("bdew.neiaddons.noserver")
                                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                }
            }
        }

        return super.handleDragNDrop(gui, mouseX, mouseY, draggedStack, button);
    }

    private boolean checkBlacklist(GuiContainer gui) {
        String name = gui.getClass().getName();
        for (String blacklist : AddonAppeng.blackListGuiName) {
            if (blacklist.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMouseOverSlot(GuiContainer gui, Slot slot, int mouseX, int mouseY) {
        int slotX = slot.xDisplayPosition;
        int slotY = slot.yDisplayPosition;
        int slotW = 16;
        int slotH = 16;
        mouseX -= gui.guiLeft;
        mouseY -= gui.guiTop;
        return mouseX >= slotX - 1 && mouseX < slotX + slotW + 1 && mouseY >= slotY - 1 && mouseY < slotY + slotH + 1;
    }

    private void setWorkbenchCommand(int slotNum, ItemStack stack, boolean replace) {
        NBTTagCompound message = new NBTTagCompound();
        message.setInteger("slot", slotNum);
        message.setBoolean("replace", replace);

        NBTTagCompound item = new NBTTagCompound();
        stack.writeToNBT(item);
        message.setTag("item", item);

        PacketHelper.sendToServer(AddonAppeng.setWorkbenchCommand, message);
    }

    private Slot getSlotAtPosition(GuiContainer gui, int x, int y) {
        for (int k = 0; k < gui.inventorySlots.inventorySlots.size(); ++k) {
            Slot slot = (Slot) gui.inventorySlots.inventorySlots.get(k);

            if (isMouseOverSlot(gui, slot, x, y)) {
                return slot;
            }
        }

        return null;
    }
}
