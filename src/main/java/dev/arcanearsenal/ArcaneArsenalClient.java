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

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (RELOAD_KEY.consumeClick()) {

                if (client.player == null) {
                    return;
                }

                ClientPlayNetworking.send(
                        new ReloadRiflePayload()
                );

                ArcaneArsenal.LOGGER.info(
                        "Arcane Rifle reload requested."
                );
            }
        });
    }
}
