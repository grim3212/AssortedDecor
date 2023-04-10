package com.grim3212.assorted.decor.common.crafting;

import com.grim3212.assorted.decor.DecorCommonMod;
import com.grim3212.assorted.lib.platform.Services;

public class DecorConditions {

    public static class Parts {
        public static final String COLORIZER = "colorizer";
        public static final String NEON_SIGN = "neon_sign";
        public static final String HANGEABLES = "hangeables";
        public static final String FLURO = "fluro";
        public static final String ROADWAYS = "roadways";
        public static final String PAINTING = "painting";
        public static final String DECORATIONS = "decorations";
        public static final String CAGE = "cage";
        public static final String PLANTER_POT = "planter_pot";
        public static final String EXTRAS = "extras";
    }


    public static void init() {
        Services.CONDITIONS.registerPartCondition(Parts.COLORIZER, () -> DecorCommonMod.COMMON_CONFIG.colorizerEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.NEON_SIGN, () -> DecorCommonMod.COMMON_CONFIG.neonSignEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.HANGEABLES, () -> DecorCommonMod.COMMON_CONFIG.hangeablesEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.FLURO, () -> DecorCommonMod.COMMON_CONFIG.fluroEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.ROADWAYS, () -> DecorCommonMod.COMMON_CONFIG.roadwaysEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.PAINTING, () -> DecorCommonMod.COMMON_CONFIG.paintingEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.DECORATIONS, () -> DecorCommonMod.COMMON_CONFIG.decorationsEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.CAGE, () -> DecorCommonMod.COMMON_CONFIG.cageEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.PLANTER_POT, () -> DecorCommonMod.COMMON_CONFIG.planterPotEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.EXTRAS, () -> DecorCommonMod.COMMON_CONFIG.extrasEnabled.get());
    }
}
