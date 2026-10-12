/*
 * Copyright (c) bdew, 2013 - 2015 https://github.com/bdew/neiaddons This mod is distributed under the terms of the
 * Minecraft Mod Public License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.neiaddons.forestry.requirements;

public enum RequirementKind {

    MUTATION_RESOURCE("bdew.neiaddons.requirement.blocks"),

    FLOWERS("bdew.neiaddons.requirement.flowers"),

    POLLEN("bdew.neiaddons.requirement.pollen");

    private final String labelKey;

    RequirementKind(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }
}
