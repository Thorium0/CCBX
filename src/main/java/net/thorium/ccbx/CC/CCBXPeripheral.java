package net.thorium.ccbx.CC;

import ballistix.common.block.BlockExplosive;
import ballistix.common.item.ItemMissile;
import ballistix.common.tile.TileVerticalLaunchSilo;
import ballistix.common.tile.silo.TileLauncherPlatformT1;
import ballistix.common.tile.silo.TileLauncherPlatformT2;
import ballistix.common.tile.silo.TileLauncherPlatformT3;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.thorium.ccbx.block.entity.CCBXTileEntity;
import net.thorium.ccbx.util.CCBXUtil;
import voltaic.common.blockitem.BlockItemDescriptable;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;

/**
 * Our peripheral class, this is the class where we will register functions for our block.
 */
public class CCBXPeripheral implements IPeripheral {

    /**
     * A list of all our connected computers. We need this for event usages.
     */
    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    /**
     * This is our tile entity, we set the tile entity when we create a new peripheral. We use this tile entity to access the block or the world
     */
    private final CCBXTileEntity tileEntity;

    /**
     * @param tileEntity the tile entity of this peripheral
     */
    public CCBXPeripheral(CCBXTileEntity tileEntity) {
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
        return "siloController";
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

    public CCBXTileEntity getTileEntity() {
        return tileEntity;
    }

    @LuaFunction(mainThread = true)
    public final boolean launch() {
        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return false;

        if (!CCBXUtil.setLaunchState(tileSilo, true)) {
            return false;
        }

        BlockPos targetPosition = CCBXUtil.getTarget(tileSilo);

        if (targetPosition != null) {
            for (IComputerAccess computerAccess : connectedComputers) {
                computerAccess.queueEvent(
                    "ccbx_launch",
                    targetPosition.getX(),
                    targetPosition.getY(),
                    targetPosition.getZ()
                );
            }
        }

        return true;
    }

    @LuaFunction(mainThread = true)
    public final boolean launchWithPosition(int x, int y, int z) {
        setPosition(x, y, z);
        return launch();
    }

    @LuaFunction(mainThread = true)
    public boolean getLaunchState() {
        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return false;

        return CCBXUtil.getLaunchState(tileSilo);
    }

    @LuaFunction(mainThread = true)
    public int getRange() {
        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return 0;

        return CCBXUtil.getRange(tileSilo);
    }

    @LuaFunction(mainThread = true)
    public final String getExplosiveType() {
        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return "";

        ComponentInventory inv = CCBXUtil.getInventory(tileSilo);

        if (inv == null) return "";

        ItemStack explosive = inv.getItem(1);

        if (explosive == null) return "";
        if (
            explosive.getItem() instanceof BlockItemDescriptable des &&
            des.getBlock() instanceof BlockExplosive blockExplosive
        ) {
            return blockExplosive.explosive.getExplosiveItem().get().toString();
        }

        return null;
    }

    @LuaFunction(mainThread = true)
    public final int getExplosiveAmount() {
        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return 0;

        ComponentInventory inv = null;

        if (tileSilo instanceof TileVerticalLaunchSilo vls) {
            inv = vls.getComponent(IComponentType.Inventory);
        } else if (tileSilo instanceof TileLauncherPlatformT1 t1) {
            inv = t1.getComponent(IComponentType.Inventory);
        } else if (tileSilo instanceof TileLauncherPlatformT2 t2) {
            inv = t2.getComponent(IComponentType.Inventory);
        } else if (tileSilo instanceof TileLauncherPlatformT3 t3) {
            inv = t3.getComponent(IComponentType.Inventory);
        }

        if (inv == null) return 0;

        ItemStack explosive = inv.getItem(1);

        if (explosive == null) return 0;

        return explosive.getCount();
    }

    @LuaFunction(mainThread = true)
    public final String getMissileType() {
        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return null;

        ComponentInventory inv = CCBXUtil.getInventory(tileSilo);

        if (inv == null) return null;

        ItemStack missileItem = inv.getItem(0);

        if (missileItem.getItem() instanceof ItemMissile missile) {
            return (
                missile.getCreatorModId(missileItem) +
                ":" +
                missile.missile.tag()
            );
        }

        return null;
    }

    @LuaFunction(mainThread = true)
    public final int getMissileAmount() {
        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return 0;

        ComponentInventory inv = CCBXUtil.getInventory(tileSilo);

        if (inv == null) return 0;

        ItemStack missile = inv.getItem(0);

        if (missile == null) return 0;

        return missile.getCount();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getPosition() {
        Map<String, Object> info = new HashMap<>();

        Object tileSilo = getMissileSilo();

        if (tileSilo == null) return info;

        BlockPos position = CCBXUtil.getTarget(tileSilo);

        if (position != null) {
            info.put("x", position.getX());
            info.put("y", position.getY());
            info.put("z", position.getZ());
        }

        return info;
    }

    @LuaFunction(mainThread = true)
    public final void setPosition(int x, int y, int z) {
        Object tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return;

        CCBXUtil.setTarget(tileMissileSilo, new BlockPos(x, y, z));

        for (IComputerAccess computerAccess : connectedComputers) {
            computerAccess.queueEvent("ccbx_update_position", x, y, z);
        }
    }

    @LuaFunction(mainThread = true)
    public final int getFrequency() {
        Object tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return 0;

        return CCBXUtil.getFrequency(tileMissileSilo);
    }

    @LuaFunction(mainThread = true)
    public final void setFrequency(int freq) {
        Object tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return;

        for (IComputerAccess computerAccess : connectedComputers) {
            computerAccess.queueEvent("ccbx_update_frequency", freq);
        }

        CCBXUtil.setFrequency(tileMissileSilo, freq);
    }

    @LuaFunction(mainThread = true)
    public final double getPower() {
        Object tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return -1;

        return CCBXUtil.getPower(tileMissileSilo);
    }

    @LuaFunction(mainThread = true)
    public final double getMaxPower() {
        Object tileMissileSilo = getMissileSilo();

        if (tileMissileSilo == null) return -1;

        return CCBXUtil.getMaxPower(tileMissileSilo);
    }

    public Object getMissileSilo() {
        return CCBXUtil.getMissileSilo(
            getTileEntity().getLevel(),
            getTileEntity().getBlockPos().above()
        );
    }
}
