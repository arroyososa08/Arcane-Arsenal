package dev.arcanearsenal;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item ARCANE_RIFLE = register(
            "arcane_rifle",
            ArcaneRifleItem::new,
            new Item.Properties().stacksTo(1)
    );
    public static final Item ARCANE_RIFLE_AMMO = register(
        "arcane_rifle_ammo",
        ArcaneRifleAmmoItem::new,
        new Item.Properties().stacksTo(64)
    );

    public static <T extends Item> T register(
            String name,
            Function<Item.Properties, T> itemFactory,
            Item.Properties properties
    ) {
        ResourceKey<Item> itemKey = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(ArcaneArsenal.MOD_ID, name)
        );

        T item = itemFactory.apply(properties.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        ArcaneArsenal.LOGGER.info("Registering Arcane Arsenal items...");
    }
}
