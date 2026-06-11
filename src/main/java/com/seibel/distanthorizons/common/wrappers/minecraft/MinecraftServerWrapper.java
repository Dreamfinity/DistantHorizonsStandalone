package com.seibel.distanthorizons.common.wrappers.minecraft;

import java.io.File;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.jetbrains.annotations.Nullable;

import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;

// @Environment(EnvType.SERVER)
public class MinecraftServerWrapper extends AbstractMinecraftSharedWrapper implements IMinecraftSharedWrapper {

    public static final MinecraftServerWrapper INSTANCE = new MinecraftServerWrapper();

    public DedicatedServer dedicatedServer = null;

    // =============//
    // constructor //
    // =============//

    private MinecraftServerWrapper() {}

    // =========//
    // methods //
    // =========//

    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @Override
    public File getInstallationDirectory() {
        if (this.dedicatedServer == null) {
            throw new IllegalStateException(
                "Trying to get Installation Direction before Dedicated server completed initialization!");
        }

        return this.dedicatedServer.getFile("test")
            .getParentFile();
    }

    @Override
    public int getPlayerCount() {
        return this.dedicatedServer.getCurrentPlayerCount();
    }

    @Override
    public @Nullable IServerLevelWrapper getLevelWrapper(String dimensionResourceLocation) {
        if (this.dedicatedServer == null) {
            throw new IllegalStateException(
                "Trying to get the server mcLevel before dedicated server completed initialization!");
        }

        Integer dimensionKey = this.deserializeDimensionResourceKey(dimensionResourceLocation);
        if (dimensionKey == null) {
            return null;
        }
        WorldServer mcLevel = DimensionManager.getWorld(dimensionKey);
        return ServerLevelWrapper.getWrapper(mcLevel);
    }

}
