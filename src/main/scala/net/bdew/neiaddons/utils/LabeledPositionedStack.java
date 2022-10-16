/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/neiaddons
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.utils;

import codechicken.nei.PositionedStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bdew.neiaddons.Utils;

public class LabeledPositionedStack extends PositionedStack {

    private final String label;
    private final int yoffs;
    private final ArrayList<String> tooltip = new ArrayList<>();

    public LabeledPositionedStack(Object stack, int x, int y, String label, int yoffs) {
        super(stack, x, y);
        this.label = label;
        this.yoffs = yoffs;
    }

    public LabeledPositionedStack(Object stack, int x, int y, String label, int yoffs, String... tooltip) {
        super(stack, x, y);
        this.label = label;
        this.yoffs = yoffs;
        this.tooltip.addAll(Arrays.asList(tooltip));
    }

    public void drawLabel() {
        if (label.contains(" ")) {
            String[] parts = label.split(" ");
            for (int i = 0; i < parts.length; i++) {
                Utils.drawCenteredString(parts[i], relx + 8, rely + 8 + yoffs + 9 * i, 0xFFFFFF);
            }
        } else {
            Utils.drawCenteredString(label, relx + 8, rely + 8 + yoffs, 0xFFFFFF);
        }
    }

    public List<String> getTooltip() {
        return tooltip;
    }
}
