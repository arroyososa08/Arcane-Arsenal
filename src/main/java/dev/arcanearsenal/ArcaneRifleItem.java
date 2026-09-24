package dev.arcanearsenal;

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

    // Temporary damage.
    // Head/body damage will replace this next.
    public static final float DAMAGE = 8.0F;

    // 20 ticks = 1 second.
    // 4 ticks = about 5 shots per second.
    public static final int FIRE_COOLDOWN_TICKS = 4;

    public ArcaneRifleItem(Properties properties) {
        super(properties);
    }

    /*
     * Shooting is now a separate method.
     * Right-click no longer fires the rifle.
     */
    public void shoot(
            Level level,
            Player player,
            ItemStack rifle
    ) {

        if (level.isClientSide()) {
            return;
        }

        // Do not fire during the cooldown.
        if (player.getCooldowns().isOnCooldown(rifle)) {
            return;
        }

        int loadedAmmo = rifle.getOrDefault(
                ModComponents.LOADED_AMMO,
                0
        );

        // Empty rifle.
        if (loadedAmmo <= 0) {

            ArcaneArsenal.LOGGER.info(
                    "{} tried to fire an empty Arcane Rifle.",
                    player.getName().getString()
            );

            return;
        }

        // Consume one round.
        rifle.set(
                ModComponents.LOADED_AMMO,
                loadedAmmo - 1
        );

        // Apply fire-rate cooldown.
        player.getCooldowns().addCooldown(
                rifle,
                FIRE_COOLDOWN_TICKS
        );

        // Start shot at player's eyes.
        Vec3 start = player.getEyePosition();

        // Shoot where the player is looking.
        Vec3 direction = player.getLookAngle();

        // Maximum range.
        Vec3 end = start.add(
                direction.scale(MAX_RANGE)
        );

        // Look for an entity along the shot.
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

        // Check blocks so bullets cannot hit
        // entities through walls.
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

                Entity target =
                        entityHit.getEntity();

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
