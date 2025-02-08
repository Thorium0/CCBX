package net.thorium.ccbx.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.thorium.ccbx.block.ModBlocks;
import net.thorium.ccbx.block.entity.CCBXTileEntity;
import net.thorium.ccbx.block.entity.ModBlockEntities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CCBXBlock extends Block implements EntityBlock {

    public CCBXBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).explosionResistance(5));
    }

    /**
     * This is the method from {@link EntityBlock} to create a new block entity for our block
     *
     * @return A new block entity from our registry object
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return ModBlockEntities.CC_TILEENTITY.get().create(pos, state);
    }

    /**
     * Allows the block entity to tick.
     *
     * @param level The level
     * @param state The state
     * @param type  The type
     * @param <T>   The type
     * @return The ticker
     */
    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NotNull Level level,
            @NotNull BlockState state,
            @NotNull BlockEntityType<T> type
    ) {
        return type == ModBlockEntities.CC_TILEENTITY.get() && !level.isClientSide
                ? (l, p, s, t) -> ((CCBXTileEntity) t).tick()
                : null;
    }
}