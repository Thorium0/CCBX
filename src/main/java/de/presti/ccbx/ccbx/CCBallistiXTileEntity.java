package de.presti.ccbx.ccbx;

import dan200.computercraft.api.peripheral.IPeripheral;
import electrodynamics.prefab.tile.components.ComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class CCBallistiXTileEntity extends BlockEntity {

    /**
     * A counter to do operations every few ticks.
     */
    private int tickCounter = 0;

    /**
     * The inventory of the block.
     */
    private final ItemStackHandler inventory = new CCBXItemStackHandler(this);

    /**
     * The capability of the inventory.
     */
    private final LazyOptional<ItemStackHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public CCBallistiXTileEntity(BlockPos pos, BlockState state) {
        super(Registration.CC_TILEENTITY.get(), pos, state);
    }

    /**
     * Our peripheral, we create a new peripheral for each new tile entity
     */
    protected CCBallistiXPeripheral peripheral = new CCBallistiXPeripheral(this);
    private LazyOptional<IPeripheral> peripheralCap;

    /**
     * Loads the inventory.
     *
     * @param nbt The tag to load from
     */
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        try {
            var data = nbt.getCompound("Buffer");
            inventory.deserializeNBT(data.getCompound("Inventory"));
        } catch (Exception e) {
            // Ignore, just log
            e.printStackTrace();
        }
    }

    /**
     * Saves the inventory.
     *
     * @param nbt The tag to save to
     */
    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        var data = new CompoundTag();
        data.put("Inventory", inventory.serializeNBT());
        nbt.put("Buffer", data);
    }

    /**
     * When a computer modem tries to wrap our block, the modem will call getCapability to receive our peripheral.
     * Then we just simply return a {@link LazyOptional} with our Peripheral
     */
    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheralCap == null) {
                peripheralCap = LazyOptional.of(() -> peripheral);
            }
            return peripheralCap.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }

        return super.getCapability(cap, direction);
    }

    /**
     * Moves the items from our inventory to the missile silo above us every 10 ticks.
     */
    public void tick() {
        tickCounter++;

        // Only move items every 10 ticks and if there is something to move
        if (tickCounter % 10 != 0 || (inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty())) {
            return;
        }

        var silo = BallistixUtil.getMissileSilo(getLevel(), getBlockPos().above());
        if (silo == null) {
            return;
        }

        var inventoryComponent = silo.getComponent(ComponentType.Inventory);
        if (inventoryComponent == null) {
            return;
        }

        inventoryComponent.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(wrapper -> {
            // Move the items from our inventory to the missile silo above us
            var ourMissileStack = inventory.getStackInSlot(0);
            var ourExplosiveStack = inventory.getStackInSlot(1);
            var theirMissileStack = wrapper.getStackInSlot(0);
            var theirExplosiveStack = wrapper.getStackInSlot(1);

            if (theirMissileStack.isEmpty() || ourMissileStack.getItem() == theirMissileStack.getItem()) {
                var newStack = wrapper.insertItem(0, ourMissileStack, false);
                inventory.setStackInSlot(0, newStack);
            }
            if (theirExplosiveStack.isEmpty() || ourExplosiveStack.getItem() == theirExplosiveStack.getItem()) {
                var newStack = wrapper.insertItem(1, ourExplosiveStack, false);
                inventory.setStackInSlot(1, newStack);
            }
        });
    }
}
