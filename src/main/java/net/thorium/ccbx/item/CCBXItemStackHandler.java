package net.thorium.ccbx.item;

import ballistix.common.block.BlockExplosive;
import ballistix.common.item.ItemMissile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.thorium.ccbx.block.entity.CCBXTileEntity;
import org.jetbrains.annotations.NotNull;
import voltaic.common.blockitem.BlockItemDescriptable;

/**
 * Item stack handler of a silo controller.
 */
public class CCBXItemStackHandler extends ItemStackHandler {
    /**
     * The tile entity of the silo controller.
     */
    private final CCBXTileEntity tileEntity;

    /**
     * Creates a new item stack handler for a silo controller.
     *
     * @param tileEntity The tile entity of the silo controller
     */
    public CCBXItemStackHandler(CCBXTileEntity tileEntity) {
        super(2);
        this.tileEntity = tileEntity;
    }

    /**
     * Called when the contents of the inventory have changed.
     *
     * @param slot The slot that changed
     */
    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (tileEntity != null) {
            this.tileEntity.setChanged();
        }
    }

    /**
     * Inserts an ItemStack into the given slot and returns the remainder.
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining stack that was not inserted (if the entire stack is accepted, then return ItemStack.EMPTY).
     */
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        var item = stack.getItem();
        if (item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive) {
            slot = 1;
        }

        if (item instanceof ItemMissile) {
            slot = 0;
        }

        return super.insertItem(slot, stack, simulate);
    }

    /**
     * Checks if the given stack is valid for the given slot.
     *
     * @param slot  Slot to query for validity
     * @param stack Stack to test with for validity
     * @return If the stack is valid for the slot
     */
    @Override
    public boolean isItemValid(@NotNull int slot, @NotNull ItemStack stack) {
        var item = stack.getItem();

        if (slot == 0) {
            return item instanceof ItemMissile;
        }
        if (slot == 1) {
            return item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive;
        }

        return false;
    }
}