package net.bdew.neiaddons.utils;

import com.gtnewhorizon.gtnhlib.color.ColorResource;

public class ColorUtils {

    private static final ColorResource.Factory color = new ColorResource.Factory("neiaddons");

    public static final ColorResource
    // spotless:off
        neiChanceTextNormal = color.rgb("neiChanceTextNormal",  "0xFFFFFF"),
        neiChanceTextRed    = color.rgb("neiChanceTextRed",     "0xFFFFFF"),
        neiLabel            = color.rgb("neiLabel",             "0xFFFFFF"),
        neiProd             = color.rgb("neiProd",              "0xFFFFFF"),
        neiSpec             = color.rgb("neiSpec",              "0xFFF200");
    // spotless:on
}
