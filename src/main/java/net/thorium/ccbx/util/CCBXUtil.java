package net.thorium.ccbx.util;

import ballistix.api.silo.ILauncherControlPanel;
import ballistix.common.tile.TileVerticalLaunchSilo;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.common.tile.silo.TileLauncherControlPanelT2;
import ballistix.common.tile.silo.TileLauncherControlPanelT3;
import ballistix.common.tile.silo.TileLauncherPlatformT1;
import ballistix.common.tile.silo.TileLauncherPlatformT2;
import ballistix.common.tile.silo.TileLauncherPlatformT3;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentInventory;

public class CCBXUtil {

    /**
     * Get the missile silo from the Block position.
     * Now supports all new Ballistix tile types: TileVerticalLaunchSilo, TileLauncherPlatformT1-T3
     *
     * @return The missile silo or null if there is none
     */
    public static Object getMissileSilo(Level level, BlockPos pos) {
        if (level == null) return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity == null) return null;

        // Check for all tile types
        if (blockEntity instanceof TileVerticalLaunchSilo tileSilo) {
            return tileSilo;
        }
        if (blockEntity instanceof TileLauncherPlatformT1 tileSilo) {
            return tileSilo;
        }
        if (blockEntity instanceof TileLauncherPlatformT2 tileSilo) {
            return tileSilo;
        }
        if (blockEntity instanceof TileLauncherPlatformT3 tileSilo) {
            return tileSilo;
        }

        return null;
    }

    /**
     * Set the launch state for any missile silo type
     */
    public static boolean setLaunchState(
        Object tileSilo,
        boolean shouldLaunch
    ) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            if (shouldLaunch) {
                // Validate power and range before launching
                if (!validateVerticalLaunchSilo(silo)) {
                    return false;
                }
            }
            silo.shouldLaunch.setValue(shouldLaunch);
            return true;
        }

        // For launcher platforms, trigger launch immediately if shouldLaunch is true
        if (
            shouldLaunch &&
            (tileSilo instanceof TileLauncherPlatformT1 ||
                tileSilo instanceof TileLauncherPlatformT2 ||
                tileSilo instanceof TileLauncherPlatformT3)
        ) {
            ILauncherControlPanel controlPanel = findControlPanel(tileSilo);
            if (controlPanel != null) {
                return launchPlatform(tileSilo, controlPanel);
            }
        }

        return false;
    }

    /**
     * Get the launch state for any missile silo type
     * Note: Launcher platforms don't have a persistent launch state
     */
    public static boolean getLaunchState(Object tileSilo) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            return silo.shouldLaunch.getValue();
        }
        // Launcher platforms (T1-T3) don't have a persistent launch state
        // They launch immediately when triggered, so always return false
        return false;
    }

    /**
     * Get the range for any missile silo type
     */
    public static int getRange(Object tileSilo) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            return silo.getRange();
        }
        if (tileSilo instanceof TileLauncherPlatformT1 silo) {
            return silo.getRange();
        }
        if (tileSilo instanceof TileLauncherPlatformT2 silo) {
            return silo.getRange();
        }
        if (tileSilo instanceof TileLauncherPlatformT3 silo) {
            return silo.getRange();
        }
        return 0;
    }

    /**
     * Get the target position for any missile silo type
     */
    public static BlockPos getTarget(Object tileSilo) {
        if (
            tileSilo instanceof TileVerticalLaunchSilo silo &&
            silo.target != null
        ) {
            return silo.target.getValue();
        }

        // For launcher platforms, get target from control panel
        if (
            tileSilo instanceof TileLauncherPlatformT1 ||
            tileSilo instanceof TileLauncherPlatformT2 ||
            tileSilo instanceof TileLauncherPlatformT3
        ) {
            ILauncherControlPanel controlPanel = findControlPanel(tileSilo);
            if (controlPanel != null) {
                return controlPanel.getTarget();
            }
        }
        return null;
    }

    /**
     * Set the target position for any missile silo type
     */
    public static boolean setTarget(Object tileSilo, BlockPos target) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            silo.target.setValue(target);
            return true;
        }

        // For launcher platforms, set target on control panel
        if (
            tileSilo instanceof TileLauncherPlatformT1 ||
            tileSilo instanceof TileLauncherPlatformT2 ||
            tileSilo instanceof TileLauncherPlatformT3
        ) {
            ILauncherControlPanel controlPanel = findControlPanel(tileSilo);
            if (controlPanel != null) {
                controlPanel.setTarget(target);
                return true;
            }
        }
        return false;
    }

    /**
     * Get the frequency for any missile silo type
     */
    public static int getFrequency(Object tileSilo) {
        if (
            tileSilo instanceof TileVerticalLaunchSilo silo &&
            silo.frequency != null
        ) {
            return silo.frequency.getValue();
        }

        // For launcher platforms, get frequency from control panel
        if (
            tileSilo instanceof TileLauncherPlatformT1 ||
            tileSilo instanceof TileLauncherPlatformT2 ||
            tileSilo instanceof TileLauncherPlatformT3
        ) {
            ILauncherControlPanel controlPanel = findControlPanel(tileSilo);
            if (controlPanel != null) {
                return controlPanel.getFrequency();
            }
        }
        return 0;
    }

    /**
     * Set the frequency for any missile silo type
     */
    public static boolean setFrequency(Object tileSilo, int frequency) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            silo.frequency.setValue(frequency);
            return true;
        }

        // For launcher platforms, set frequency on control panel
        if (
            tileSilo instanceof TileLauncherPlatformT1 ||
            tileSilo instanceof TileLauncherPlatformT2 ||
            tileSilo instanceof TileLauncherPlatformT3
        ) {
            ILauncherControlPanel controlPanel = findControlPanel(tileSilo);
            if (controlPanel != null) {
                // Control panel extends GenericTile and has frequency field like TileVerticalLaunchSilo
                if (controlPanel instanceof TileLauncherControlPanelT1 panel) {
                    panel.frequency.setValue(frequency);
                    return true;
                } else if (
                    controlPanel instanceof TileLauncherControlPanelT2 panel
                ) {
                    panel.frequency.setValue(frequency);
                    return true;
                } else if (
                    controlPanel instanceof TileLauncherControlPanelT3 panel
                ) {
                    panel.frequency.setValue(frequency);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the inventory component for any missile silo type
     */
    public static ComponentInventory getInventory(Object tileSilo) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            return silo.getComponent(IComponentType.Inventory);
        }
        if (tileSilo instanceof TileLauncherPlatformT1 silo) {
            return silo.getComponent(IComponentType.Inventory);
        }
        if (tileSilo instanceof TileLauncherPlatformT2 silo) {
            return silo.getComponent(IComponentType.Inventory);
        }
        if (tileSilo instanceof TileLauncherPlatformT3 silo) {
            return silo.getComponent(IComponentType.Inventory);
        }
        return null;
    }

    /**
     * Get the current power/energy level for any missile silo type
     * @return Current energy in joules, or -1 if power system not available
     */
    public static double getPower(Object tileSilo) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            var electro = (ComponentElectrodynamic) silo.getComponent(
                IComponentType.Electrodynamic
            );
            if (electro != null) {
                return electro.getJoulesStored();
            }
        }

        // For launcher platforms, get power from control panel
        if (
            tileSilo instanceof TileLauncherPlatformT1 ||
            tileSilo instanceof TileLauncherPlatformT2 ||
            tileSilo instanceof TileLauncherPlatformT3
        ) {
            ILauncherControlPanel controlPanel = findControlPanel(tileSilo);
            if (controlPanel instanceof TileLauncherControlPanelT1 panel) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro != null) {
                    return electro.getJoulesStored();
                }
            } else if (
                controlPanel instanceof TileLauncherControlPanelT2 panel
            ) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro != null) {
                    return electro.getJoulesStored();
                }
            } else if (
                controlPanel instanceof TileLauncherControlPanelT3 panel
            ) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro != null) {
                    return electro.getJoulesStored();
                }
            }
        }
        return -1; // Power system not available
    }

    /**
     * Get the maximum power capacity for any missile silo type
     * @return Maximum energy capacity in joules, or -1 if power system not available
     */
    public static double getMaxPower(Object tileSilo) {
        if (tileSilo instanceof TileVerticalLaunchSilo silo) {
            var electro = (ComponentElectrodynamic) silo.getComponent(
                IComponentType.Electrodynamic
            );
            if (electro != null) {
                return electro.getMaxJoulesStored();
            }
        }

        // For launcher platforms, get max power from control panel
        if (
            tileSilo instanceof TileLauncherPlatformT1 ||
            tileSilo instanceof TileLauncherPlatformT2 ||
            tileSilo instanceof TileLauncherPlatformT3
        ) {
            ILauncherControlPanel controlPanel = findControlPanel(tileSilo);
            if (controlPanel instanceof TileLauncherControlPanelT1 panel) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro != null) {
                    return electro.getMaxJoulesStored();
                }
            } else if (
                controlPanel instanceof TileLauncherControlPanelT2 panel
            ) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro != null) {
                    return electro.getMaxJoulesStored();
                }
            } else if (
                controlPanel instanceof TileLauncherControlPanelT3 panel
            ) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro != null) {
                    return electro.getMaxJoulesStored();
                }
            }
        }
        return -1; // Power system not available
    }

    /**
     * Find the control panel associated with a launcher platform
     * Searches nearby blocks for ILauncherControlPanel implementations
     */
    private static ILauncherControlPanel findControlPanel(Object tileSilo) {
        if (
            !(tileSilo instanceof TileLauncherPlatformT1) &&
            !(tileSilo instanceof TileLauncherPlatformT2) &&
            !(tileSilo instanceof TileLauncherPlatformT3)
        ) {
            return null;
        }

        // Get the platform's position and level
        BlockPos platformPos;
        Level level;

        if (tileSilo instanceof TileLauncherPlatformT1 platform) {
            platformPos = platform.getBlockPos();
            level = platform.getLevel();
        } else if (tileSilo instanceof TileLauncherPlatformT2 platform) {
            platformPos = platform.getBlockPos();
            level = platform.getLevel();
        } else if (tileSilo instanceof TileLauncherPlatformT3 platform) {
            platformPos = platform.getBlockPos();
            level = platform.getLevel();
        } else {
            return null;
        }

        if (level == null) return null;

        // Search only horizontal adjacent blocks where the controller can connect
        // Control panels must be horizontally adjacent and facing toward the platform
        BlockPos[] searchPositions = {
            platformPos.north(),
            platformPos.south(),
            platformPos.east(),
            platformPos.west(),
        };

        for (BlockPos searchPos : searchPositions) {
            BlockEntity blockEntity = level.getBlockEntity(searchPos);
            if (blockEntity instanceof ILauncherControlPanel controlPanel) {
                return controlPanel;
            }
        }

        return null;
    }

    /**
     * Launch a launcher platform using its control panel
     * Validates range and power requirements before launching
     */
    public static boolean launchPlatform(
        Object tileSilo,
        ILauncherControlPanel controlPanel
    ) {
        try {
            if (tileSilo instanceof TileLauncherPlatformT3 platform) {
                // Check range and power before launching
                if (!validateLaunchConditions(platform, controlPanel)) {
                    return false;
                }
                int cooldown = platform.launch(controlPanel, true, 0);
                return cooldown != -1;
            } else if (tileSilo instanceof TileLauncherPlatformT2 platform) {
                if (!validateLaunchConditions(platform, controlPanel)) {
                    return false;
                }
                int cooldown = platform.launch(controlPanel, true, 0);
                return cooldown != -1;
            } else if (tileSilo instanceof TileLauncherPlatformT1 platform) {
                if (!validateLaunchConditions(platform, controlPanel)) {
                    return false;
                }
                int cooldown = platform.launch(controlPanel, true, 0);
                return cooldown != -1;
            }
        } catch (Exception e) {
            // Log the error or handle it appropriately
            return false;
        }
        return false;
    }

    /**
     * Validate launch conditions (range and power) before attempting launch
     */
    public static boolean validateLaunchConditions(
        Object platform,
        ILauncherControlPanel controlPanel
    ) {
        try {
            // Check if control panel is actually a tile entity to access power
            if (controlPanel instanceof TileLauncherControlPanelT1 panel) {
                // Check power requirements
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro == null) {
                    return false; // No power system
                }
                double powerRequired =
                    getMissileSiloUsage() * (double) panel.getTier();
                if (electro.getJoulesStored() < powerRequired) {
                    return false; // Insufficient power
                }

                // Check range
                if (platform instanceof TileLauncherPlatformT1 plat) {
                    double distance =
                        TileLauncherControlPanelT1.calculateDistance(
                            panel.getBlockPos(),
                            controlPanel.getTarget()
                        );
                    return plat.getRange() >= distance;
                }
                return false; // Platform type mismatch
            } else if (
                controlPanel instanceof TileLauncherControlPanelT2 panel
            ) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro == null) {
                    return false; // No power system
                }
                double powerRequired =
                    getMissileSiloUsage() * panel.getTier();
                if (electro.getJoulesStored() < powerRequired) {
                    return false;
                }
                if (platform instanceof TileLauncherPlatformT2 plat) {
                    double distance =
                        TileLauncherControlPanelT1.calculateDistance(
                            panel.getBlockPos(),
                            controlPanel.getTarget()
                        );
                    return plat.getRange() >= distance;
                }
                return false; // Platform type mismatch
            } else if (
                controlPanel instanceof TileLauncherControlPanelT3 panel
            ) {
                var electro = (ComponentElectrodynamic) panel.getComponent(
                    IComponentType.Electrodynamic
                );
                if (electro == null) {
                    return false; // No power system
                }
                double powerRequired =
                    getMissileSiloUsage() * panel.getTier();
                if (electro.getJoulesStored() < powerRequired) {
                    return false;
                }
                if (platform instanceof TileLauncherPlatformT3 plat) {
                    double distance =
                        TileLauncherControlPanelT1.calculateDistance(
                            panel.getBlockPos(),
                            controlPanel.getTarget()
                        );
                    return plat.getRange() >= distance;
                }
                return false; // Platform type mismatch
            }
        } catch (Exception e) {
            // If validation fails, assume conditions are not met
            return false;
        }
        return false; // Unknown control panel type
    }

    /**
     * Validate launch conditions for TileVerticalLaunchSilo
     */
    public static boolean validateVerticalLaunchSilo(
        TileVerticalLaunchSilo silo
    ) {
        try {
            // Check power requirements
            var electro = (ComponentElectrodynamic) silo.getComponent(
                IComponentType.Electrodynamic
            );
            if (electro == null) {
                return false; // No power system
            }

            // Vertical launch silo is tier 1
            double powerRequired = getMissileSiloUsage();
            if (electro.getJoulesStored() < powerRequired) {
                return false; // Insufficient power
            }

            // Check range - target must be within silo range
            if (silo.target != null) {
                BlockPos target = silo.target.getValue();
                BlockPos siloPos = silo.getBlockPos();
                if (target != null && siloPos != null) {
                    double distance =
                        TileLauncherControlPanelT1.calculateDistance(
                            siloPos,
                            target
                        );
                    return silo.getRange() >= distance;
                }
            }

            return true; // No target set or validation passed
        } catch (Exception e) {
            return false; // Validation failed
        }
    }

    private static double getMissileSiloUsage() {
        // Ballistix 0.9 style: BallistixConstants.MISSILESILO_USAGE
        try {
            Class<?> constantsClass = Class.forName(
                "ballistix.common.settings.BallistixConstants"
            );
            Field usageField = constantsClass.getField("MISSILESILO_USAGE");
            Object value = usageField.get(null);
            if (value instanceof Number number) {
                return number.doubleValue();
            }
        } catch (Throwable ignored) {}

        // Ballistix 1.0 style: BallistixConfig.INSTANCE.MISSILESILO_USAGE.get()
        try {
            Class<?> configClass = Class.forName(
                "ballistix.common.settings.BallistixConfig"
            );
            Field instanceField = configClass.getField("INSTANCE");
            Object instance = instanceField.get(null);
            if (instance != null) {
                Field usageField = configClass.getField("MISSILESILO_USAGE");
                Object usageConfigValue = usageField.get(instance);
                if (usageConfigValue instanceof Number number) {
                    return number.doubleValue();
                }
                if (usageConfigValue != null) {
                    Method getMethod = usageConfigValue
                        .getClass()
                        .getMethod("get");
                    Object value = getMethod.invoke(usageConfigValue);
                    if (value instanceof Number number) {
                        return number.doubleValue();
                    }
                }
            }
        } catch (Throwable ignored) {}

        return 10000; // Default fallback value if reflection fails
    }
}
