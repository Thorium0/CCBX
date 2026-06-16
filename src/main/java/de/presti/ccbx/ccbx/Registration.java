package de.presti.ccbx.ccbx;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CCBX.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CCBX.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CCBX.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CCBX.MODID);

    // Blocks
    public static final RegistryObject<Block> CC_BLOCK = register("cc_ballistix_block", CCBallistiXBlock::new);

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> registryObject = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(registryObject.get(), new Item.Properties()));
        return registryObject;
    }

    // Creative tab
    public static final RegistryObject<CreativeModeTab> CCBX_TAB = CREATIVE_MODE_TABS.register("ccbx_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(CC_BLOCK.get()))
                    .title(Component.translatable("creativetab.ccbx"))
                    .displayItems((parameters, output) -> output.accept(CC_BLOCK.get()))
                    .build());

    // Tile Entities
    public static final RegistryObject<BlockEntityType<CCBallistiXTileEntity>> CC_TILEENTITY = Registration.BLOCK_ENTITIES.register("cc_ballistix_block", () -> new BlockEntityType<>(CCBallistiXTileEntity::new, Sets.newHashSet(CC_BLOCK.get()), null));

    // Register our stuff
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
    }
}