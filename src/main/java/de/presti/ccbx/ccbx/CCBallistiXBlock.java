package de.presti.ccbx.ccbx;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * This is our block. To tell minecraft that this block has a block entity, we need to implement {@link EntityBlock}
 */
public class CCBallistiXBlock extends Block implements EntityBlock {

    public CCBallistiXBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(0.5F)
                .sound(SoundType.METAL)
                .noOcclusion());
    }

    /**
     * This is the method from {@link EntityBlock} to create a new block entity for our block
     *
     * @return A new block entity from our registry object
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return Registration.CC_TILEENTITY.get().create(pos, state);
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
        return type == Registration.CC_TILEENTITY.get() && !level.isClientSide
                ? (l, p, s, t) -> ((CCBallistiXTileEntity) t).tick()
                : null;
    }

    /**
     * Called when the block is broken to determine what items it drops.
     * We want the block to drop itself as an item.
     *
     * @param state   The BlockState of the broken block.
     * @param builder The LootContext.Builder used to build the loot context.
     * @return A list of ItemStacks to drop.
     */
    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}
