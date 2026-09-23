package dev.arcanearsenal;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item ARCANE_RIFLE = register(
            "arcane_rifle",
            new Item(new Item.Settings())
    );

    private static Item register(String name, Item item) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(ArcaneArsenal.MOD_ID, name),
                item
        );
    }

    public static void initialize() {
        ArcaneArsenal.LOGGER.info("Registering Arcane Arsenal items...");
    }
}
