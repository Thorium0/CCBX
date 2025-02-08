package net.thorium.ccbx.block;

import com.google.common.collect.Sets;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thorium.ccbx.CCBX;
import net.thorium.ccbx.block.custom.CCBXBlock;
import net.thorium.ccbx.block.entity.CCBXTileEntity;
import net.thorium.ccbx.item.ModItems;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CCBX.MOD_ID);

    public static final DeferredBlock<Block> CC_BLOCK = registerBlock("cc_block", CCBXBlock::new);


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private enum blockItemOverride {
        appendHoverText,
        appendShiftHoverText
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, List<blockItemOverride> blockItemOverrides) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, blockItemOverrides);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block,  List<blockItemOverride> blockItemOverrides) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                if (blockItemOverrides.contains(blockItemOverride.appendShiftHoverText)) {
                    if (Screen.hasShiftDown())
                        tooltipComponents.add(Component.translatable("tooltip.quantumtech."+ name +".shift_down"));
                    else
                        tooltipComponents.add(Component.translatable("tooltip.quantumtech." + name));

                } else if (blockItemOverrides.contains(blockItemOverride.appendHoverText))
                    tooltipComponents.add(Component.translatable("tooltip.quantumtech." + name));

            }
        });
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
