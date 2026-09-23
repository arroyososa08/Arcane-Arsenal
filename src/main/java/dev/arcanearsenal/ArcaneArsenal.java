package dev.arcanearsenal;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneArsenal implements ModInitializer {
    public static final String MOD_ID = "arcane_arsenal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Arcane Arsenal loaded.");
    }
}
