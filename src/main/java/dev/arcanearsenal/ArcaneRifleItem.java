package dev.arcanearsenal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ArcaneRifleItem extends Item {

    public static final int MAGAZINE_SIZE = 30;
    public static final int MAX_RANGE = 100;

    public ArcaneRifleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        var rifle = player.getItemInHand(hand);

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
}
