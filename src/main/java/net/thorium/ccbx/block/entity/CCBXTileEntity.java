package net.thorium.ccbx.block.entity;

import dan200.computercraft.api.peripheral.IPeripheral;

import electrodynamics.prefab.tile.components.CapabilityInputType;
import electrodynamics.prefab.tile.components.IComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.thorium.ccbx.CC.CCBXPeripheral;
import net.thorium.ccbx.block.entity.ModBlockEntities;
import net.thorium.ccbx.item.CCBXItemStackHandler;
import net.thorium.ccbx.util.CCBXUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CCBXTileEntity extends BlockEntity {
    private int tickCounter = 0;
    private final CCBXItemStackHandler inventory = new CCBXItemStackHandler(this);
    private final CCBXPeripheral peripheral;

    public CCBXTileEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CC_TILEENTITY.get(), pos, state);
        this.peripheral = new CCBXPeripheral(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        try {
            CompoundTag data = tag.getCompound("Buffer");
            if (data.contains("Inventory")) {
                inventory.deserializeNBT(registries, data.getCompound("Inventory"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag data = new CompoundTag();
        data.put("Inventory", inventory.serializeNBT(registries));
        tag.put("Buffer", data);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    public IPeripheral getPeripheral(Direction side) {
        return peripheral;
    }

    public void tick() {
        tickCounter++;

        if (tickCounter % 10 != 0 || (inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty())) {
            return;
        }

        var silo = CCBXUtil.getMissileSilo(getLevel(), getBlockPos().above());
        if (silo == null) {
            return;
        }

        var inventoryComponent = silo.getComponent(IComponentType.Inventory);
        if (inventoryComponent == null) {
            return;
        }

        if (inventoryComponent instanceof IItemHandler handler) {
            ItemStack ourMissileStack = inventory.getStackInSlot(0);
            ItemStack ourExplosiveStack = inventory.getStackInSlot(1);
            ItemStack theirMissileStack = handler.getStackInSlot(0);
            ItemStack theirExplosiveStack = handler.getStackInSlot(1);

            if (theirMissileStack.isEmpty() || ourMissileStack.getItem() == theirMissileStack.getItem()) {
                ItemStack newStack = handler.insertItem(0, ourMissileStack, false);
                inventory.setStackInSlot(0, newStack);
            }
            if (theirExplosiveStack.isEmpty() || ourExplosiveStack.getItem() == theirExplosiveStack.getItem()) {
                ItemStack newStack = handler.insertItem(1, ourExplosiveStack, false);
                inventory.setStackInSlot(1, newStack);
            }
        }
    }
}