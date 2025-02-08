package net.thorium.ccbx.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thorium.ccbx.CCBX;
import net.thorium.ccbx.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS  = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CCBX.MOD_ID);

    public static final Supplier<CreativeModeTab> CCBX_TAB = CREATIVE_MODE_TABS.register("ccbx_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.CC_BLOCK))
                    .title(Component.translatable("creativetab.ccbx"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.CC_BLOCK);

                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
