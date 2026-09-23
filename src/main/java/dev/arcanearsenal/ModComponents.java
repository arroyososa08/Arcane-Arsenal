package dev.arcanearsenal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;

public class ModComponents {

    public static final DataComponentType<Integer> LOADED_AMMO =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Identifier.fromNamespaceAndPath(
                            ArcaneArsenal.MOD_ID,
                            "loaded_ammo"
                    ),
                    DataComponentType.<Integer>builder()
                            .persistent(net.minecraft.util.ExtraCodecs.NON_NEGATIVE_INT)
                            .build()
            );

    public static void initialize() {
        ArcaneArsenal.LOGGER.info(
                "Registering Arcane Arsenal data components..."
        );
    }
}
