package com.grim3212.assorted.decor.common.sounds;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class DecorSounds {

    public static final RegistryProvider<SoundEvent> SOUNDS = RegistryProvider.create(Registries.SOUND_EVENT, Constants.MOD_ID);

    public static final IRegistryObject<SoundEvent> GATE_TRUMPET = registerSound("gate_trumpet");
    public static final IRegistryObject<SoundEvent> GARAGE_REMOTE = registerSound("garage_remote");

    private static IRegistryObject<SoundEvent> registerSound(String name) {
        Identifier loc = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(loc));
    }

    public static void init() {
    }
}
