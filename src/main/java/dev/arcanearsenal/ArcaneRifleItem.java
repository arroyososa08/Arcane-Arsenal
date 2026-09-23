package dev.arcanearsenal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArcaneRifleItem extends Item {

    public static final int MAGAZINE_SIZE = 30;
    public static final int MAX_RANGE = 100;

    public ArcaneRifleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        ItemStack rifle = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        int loadedAmmo = rifle.getOrDefault(
                ModComponents.LOADED_AMMO,
                0
        );

        if (loadedAmmo <= 0) {
            ArcaneArsenal.LOGGER.info(
                    "{} tried to fire an empty Arcane Rifle.",
                    player.getName().getString()
            );

            return InteractionResult.FAIL;
        }

        rifle.set(
                ModComponents.LOADED_AMMO,
                loadedAmmo - 1
        );

        ArcaneArsenal.LOGGER.info(
                "{} fired the Arcane Rifle! Ammo: {}/{}",
                player.getName().getString(),
                loadedAmmo - 1,
                MAGAZINE_SIZE
        );

        return InteractionResult.SUCCESS;
    }

    public void reload(Player player, ItemStack rifle) {

        int loadedAmmo = rifle.getOrDefault(
                ModComponents.LOADED_AMMO,
                0
        );

        if (loadedAmmo >= MAGAZINE_SIZE) {
            return;
        }

        int neededAmmo = MAGAZINE_SIZE - loadedAmmo;
        int ammoLoaded = 0;

        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {

            if (stack.is(ModItems.ARCANE_RIFLE_AMMO)
                    && ammoLoaded < neededAmmo) {

                int amountToTake = Math.min(
                        stack.getCount(),
                        neededAmmo - ammoLoaded
                );

                stack.shrink(amountToTake);
                ammoLoaded += amountToTake;
            }
        }

        if (ammoLoaded > 0) {
            rifle.set(
                    ModComponents.LOADED_AMMO,
                    loadedAmmo + ammoLoaded
            );

            ArcaneArsenal.LOGGER.info(
                    "{} reloaded the Arcane Rifle! Ammo: {}/{}",
                    player.getName().getString(),
                    loadedAmmo + ammoLoaded,
                    MAGAZINE_SIZE
            );
        }
    }
}
