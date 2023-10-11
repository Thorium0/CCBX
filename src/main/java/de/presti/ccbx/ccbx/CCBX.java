package de.presti.ccbx.ccbx;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("ccbx")
public class CCBX {

    // Our mod id
    public static final String MODID = "ccbx";

    public CCBX() {
        Registration.register();
        // Register ourselves for server and other game events we are interested in. Currently, we do not use any events
        MinecraftForge.EVENT_BUS.register(this);
    }
}
