package de.presti.ccbx.ccbx;

import ballistix.common.block.BlockMissileSilo;
import ballistix.common.tile.TileMissileSilo;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Our peripheral class, this is the class where we will register functions for our block.
 */
public class CCBallistiXPeripheral implements IPeripheral {

    /**
     * A list of all our connected computers. We need this for event usages.
     */
    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    /**
     * This is our tile entity, we set the tile entity when we create a new peripheral. We use this tile entity to access the block or the world
     */
    private final CCBallistiXTileEntity tileEntity;

    /**
     * @param tileEntity the tile entity of this peripheral
     */
    public CCBallistiXPeripheral(CCBallistiXTileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    /**
     * We use getType to set the name for our peripheral. A modem would wrap our block as "test_n"
     *
     * @return the name of our peripheral
     */
    @Nonnull
    @Override
    public String getType() {
        return "silocontroller";
    }

    /**
     * CC use this method to check, if the peripheral in front of the modem is our peripheral
     */
    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return this == iPeripheral;
    }

    /**
     * Will be called when a computer disconnects from our block
     */
    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    /**
     * Will be called when a computer connects to our block
     */
    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    public CCBallistiXTileEntity getTileEntity() {
        return tileEntity;
    }

    private TileMissileSilo getMissileSilo() {
        Level level = getTileEntity().getLevel();

        if (level == null) return null;

        BlockEntity blockEntity = level.getBlockEntity(getTileEntity().getBlockPos().above());

        if (blockEntity == null) return null;

        if (blockEntity instanceof TileMissileSilo tileMissileSilo) {
            return tileMissileSilo;
        }

        return null;
    }

    @LuaFunction(mainThread = true)
    public final boolean launch() {

        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return false;

        tileMissileSilo.shouldLaunch = true;
        return true;
    }

    @LuaFunction(mainThread = true)
    public final boolean launchWithPosition(int x, int y, int z) {
        setPosition(x, y, z);
        return launch();
    }

    @LuaFunction(mainThread = true)
    public final String getExplosionTyp() {
        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return "";

        ComponentInventory inv = tileMissileSilo.getComponent(ComponentType.Inventory);
        ItemStack explosive = inv.getItem(1);

        if (explosive == null) return "";

        return explosive.getItem().getDefaultInstance().getDisplayName().getString()
                .replace("[", "").replace("]", "");
    }

    @LuaFunction(mainThread = true)
    public final int getExplosionAmount() {
        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return 0;

        ComponentInventory inv = tileMissileSilo.getComponent(ComponentType.Inventory);
        ItemStack explosive = inv.getItem(1);

        if (explosive == null) return 0;

        return explosive.getCount();
    }

    @LuaFunction(mainThread = true)
    public final String getMissileTyp() {
        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return "";

        ComponentInventory inv = tileMissileSilo.getComponent(ComponentType.Inventory);
        ItemStack missile = inv.getItem(0);

        if (missile == null) return "";

        return missile.getItem().getDefaultInstance().getDisplayName().getString()
                .replace("[", "").replace("]", "");
    }

    @LuaFunction(mainThread = true)
    public final int getMissileAmount() {
        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return 0;

        ComponentInventory inv = tileMissileSilo.getComponent(ComponentType.Inventory);
        ItemStack missile = inv.getItem(0);

        if (missile == null) return 0;

        return missile.getCount();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getPosition() {
        Map<String, Object> info = new HashMap<>();

        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return info;

        if (tileMissileSilo.target == null) return info;

        BlockPos position = tileMissileSilo.target.get();

        info.put("x", position.getX());
        info.put("y", position.getY());
        info.put("z", position.getZ());

        return info;
    }

    @LuaFunction(mainThread = true)
    public final void setPosition(int x, int y, int z) {
        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return;

        tileMissileSilo.target.set(new BlockPos(x, y, z));
    }

    @LuaFunction(mainThread = true)
    public final int getFrequency() {
        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return 0;

        if (tileMissileSilo.frequency == null) return 0;

        return tileMissileSilo.frequency.get();
    }

    @LuaFunction(mainThread = true)
    public final void setFrequency(int freq) {
        TileMissileSilo tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return;

        tileMissileSilo.frequency.set(freq);
    }
}
