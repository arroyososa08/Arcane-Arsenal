package dev.arcanearsenal;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ArcaneArsenalClient implements ClientModInitializer {

    private static final KeyMapping.Category ARCANE_ARSENAL_CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            ArcaneArsenal.MOD_ID,
                            "controls"
                    )
            );

    private static final KeyMapping RELOAD_KEY =
            KeyMappingHelper.registerKeyMapping(
                    new KeyMapping(
                            "key.arcane_arsenal.reload",
                            InputConstants.Type.KEYSYM,
                            InputConstants.KEY_R,
                            ARCANE_ARSENAL_CATEGORY
                    )
            );

    private static boolean attackWasDown = false;

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null) {
                attackWasDown = false;
                return;
            }

            // -------------------------
            // RELOAD
            // -------------------------

            while (RELOAD_KEY.consumeClick()) {

                ClientPlayNetworking.send(
                        new ReloadRiflePayload()
                );

                ArcaneArsenal.LOGGER.info(
                        "Arcane Rifle reload requested."
                );
            }

            // -------------------------
            // LEFT-CLICK SHOOTING
            // -------------------------

            boolean attackDown =
                    client.options.keyAttack.isDown();

            boolean holdingArcaneRifle =
                    client.player.getMainHandItem().getItem()
                            instanceof ArcaneRifleItem;

            /*
             * Fire once when the attack button changes
             * from released -> pressed.
             *
             * The server still controls ammo,
             * cooldown and damage.
             */
            if (holdingArcaneRifle
                    && attackDown
                    && !attackWasDown) {

                ClientPlayNetworking.send(
                        new ShootRiflePayload()
                );

                ArcaneArsenal.LOGGER.info(
                        "Arcane Rifle shot requested."
                );
            }

            attackWasDown = attackDown;
        });
    }
}
