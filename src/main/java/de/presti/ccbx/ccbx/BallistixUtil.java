package de.presti.ccbx.ccbx;

import ballistix.common.tile.TileMissileSilo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BallistixUtil {

    /**
     * Get the missile silo from the Block position.
     *
     * @return The missile silo or null if there is none
     */
    public static TileMissileSilo getMissileSilo(Level level, BlockPos pos) {
        if (level == null) return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity == null) return null;

        if (blockEntity instanceof TileMissileSilo tileMissileSilo) {
            return tileMissileSilo;
        }

        return null;
    }

}
