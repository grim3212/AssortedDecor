package com.grim3212.assorted.decor.common.crafting;

import com.grim3212.assorted.decor.DecorConfig;
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
        Services.CONDITIONS.registerPartCondition(Parts.COLORIZER, () -> DecorConfig.Common.partColorizerEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.NEON_SIGN, () -> DecorConfig.Common.partNeonSignEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.HANGEABLES, () -> DecorConfig.Common.partHangeablesEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.FLURO, () -> DecorConfig.Common.partFluroEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.ROADWAYS, () -> DecorConfig.Common.partRoadwaysEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.PAINTING, () -> DecorConfig.Common.partPaintingEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.DECORATIONS, () -> DecorConfig.Common.partDecorationsEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.CAGE, () -> DecorConfig.Common.partCageEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.PLANTER_POT, () -> DecorConfig.Common.partPlanterPotEnabled.getValue());
        Services.CONDITIONS.registerPartCondition(Parts.EXTRAS, () -> DecorConfig.Common.partExtrasEnabled.getValue());
    }
}
