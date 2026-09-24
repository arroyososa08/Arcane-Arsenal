package dev.arcanearsenal;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneArsenal implements ModInitializer {

    public static final String MOD_ID = "arcane_arsenal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Arcane Arsenal weapon system starting...");

        ModComponents.initialize();
        ModItems.initialize();

        // Register reload packet.
        PayloadTypeRegistry.serverboundPlay().register(
                ReloadRiflePayload.TYPE,
                ReloadRiflePayload.CODEC
        );

        // Register shoot packet.
        PayloadTypeRegistry.serverboundPlay().register(
                ShootRiflePayload.TYPE,
                ShootRiflePayload.CODEC
        );

        // Reload packet receiver.
        ServerPlayNetworking.registerGlobalReceiver(
                ReloadRiflePayload.TYPE,
                (payload, context) -> {

                    var player = context.player();

                    ItemStack rifle =
                            player.getItemInHand(
                                    InteractionHand.MAIN_HAND
                            );

                    if (rifle.getItem()
                            instanceof ArcaneRifleItem arcaneRifle) {

                        arcaneRifle.reload(
                                player,
                                rifle
                        );
                    }
                }
        );

        // Shooting receiver will be connected
        // after the rifle shooting method is moved.
        ServerPlayNetworking.registerGlobalReceiver(
                ShootRiflePayload.TYPE,
                (payload, context) -> {

                    var player = context.player();

                    ItemStack rifle =
                            player.getItemInHand(
                                    InteractionHand.MAIN_HAND
                            );

                    if (rifle.getItem()
                            instanceof ArcaneRifleItem) {

                        LOGGER.debug(
                                "{} requested an Arcane Rifle shot.",
                                player.getName().getString()
                        );
                    }
                }
        );
    }
}
