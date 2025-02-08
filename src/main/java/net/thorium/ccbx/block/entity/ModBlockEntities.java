package net.thorium.ccbx.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.thorium.ccbx.CCBX;
import net.thorium.ccbx.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CCBX.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CCBXTileEntity>> CC_TILEENTITY = 
        BLOCK_ENTITIES.register("ccbx_block",
            () -> BlockEntityType.Builder.of(CCBXTileEntity::new, ModBlocks.CCBX_BLOCK.get())
                .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
