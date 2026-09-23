package dev.arcanearsenal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.entity.projectile.ProjectileUtil;

public class ArcaneRifleItem extends Item {

    public static final int MAGAZINE_SIZE = 30;
    public static final int MAX_RANGE = 100;

    // Starting damage for the Arcane Rifle.
    // We can balance this later.
    public static final float DAMAGE = 8.0F;

    public ArcaneRifleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand
    ) {

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

        // Consume one round.
        rifle.set(
                ModComponents.LOADED_AMMO,
                loadedAmmo - 1
        );

        // Start of the shot: player's eye position.
        Vec3 start = player.getEyePosition();

        // Direction the player is looking.
        Vec3 direction = player.getLookAngle();

        // End of the shot: 100 blocks away.
        Vec3 end = start.add(
                direction.scale(MAX_RANGE)
        );

        // Search for an entity along the shot path.
        EntityHitResult entityHit =
                ProjectileUtil.getEntityHitResult(
                        player,
                        start,
                        end,
                        player.getBoundingBox()
                                .expandTowards(
                                        direction.scale(MAX_RANGE)
                                )
                                .inflate(1.0D),
                        entity -> entity.isPickable()
                                && entity != player,
                        MAX_RANGE * MAX_RANGE
                );

        // Also check for blocks so we cannot shoot
        // an entity through a wall.
        HitResult blockHit = level.clip(
                new net.minecraft.world.level.ClipContext(
                        start,
                        end,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        player
                )
        );

        if (entityHit != null) {

            double entityDistance =
                    start.distanceToSqr(
                            entityHit.getLocation()
                    );

            double blockDistance =
                    start.distanceToSqr(
                            blockHit.getLocation()
                    );

            if (entityDistance <= blockDistance) {

                Entity target = entityHit.getEntity();

                target.hurt(
                        player.damageSources().playerAttack(player),
                        DAMAGE
                );

                ArcaneArsenal.LOGGER.info(
                        "{} hit {} with the Arcane Rifle.",
                        player.getName().getString(),
                        target.getName().getString()
                );
            }
        }

        ArcaneArsenal.LOGGER.info(
                "{} fired the Arcane Rifle! Ammo: {}/{}",
                player.getName().getString(),
                loadedAmmo - 1,
                MAGAZINE_SIZE
        );

        return InteractionResult.SUCCESS;
    }

    public void reload(
            Player player,
            ItemStack rifle
    ) {

        int loadedAmmo = rifle.getOrDefault(
                ModComponents.LOADED_AMMO,
                0
        );

        if (loadedAmmo >= MAGAZINE_SIZE) {
            return;
        }

        int neededAmmo =
                MAGAZINE_SIZE - loadedAmmo;

        int ammoLoaded = 0;

        for (ItemStack stack :
                player.getInventory().getNonEquipmentItems()) {

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
