package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.client.blockentity.NeonSignBlockEntityRenderer;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class DecorSpriteSourceProvider extends SpriteSourceProvider {

    public DecorSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, Constants.MOD_ID);
    }

    @Override
    protected void addSources() {
        atlas(SpriteSourceProvider.SIGNS_ATLAS).addSource(new SingleFile(NeonSignBlockEntityRenderer.NEON_SIGN_TEXTURE, Optional.empty()));
        atlas(SpriteSourceProvider.SIGNS_ATLAS).addSource(new SingleFile(NeonSignBlockEntityRenderer.NEON_SIGN_CLEAR_TEXTURE, Optional.empty()));
        atlas(SpriteSourceProvider.SIGNS_ATLAS).addSource(new SingleFile(NeonSignBlockEntityRenderer.NEON_SIGN_WHITE_TEXTURE, Optional.empty()));
    }

}
