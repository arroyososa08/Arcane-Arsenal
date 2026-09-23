package dev.arcanearsenal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ArcaneRifleItem extends Item {

    public ArcaneRifleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ArcaneArsenal.LOGGER.info(
                "{} fired the Arcane Rifle!",
                player.getName().getString()
        );

        return InteractionResult.SUCCESS;
    }
}
