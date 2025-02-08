package net.thorium.ccbx.item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thorium.ccbx.CCBX;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CCBX.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
