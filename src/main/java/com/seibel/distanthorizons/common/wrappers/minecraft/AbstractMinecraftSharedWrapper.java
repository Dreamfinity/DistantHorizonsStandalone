package com.seibel.distanthorizons.common.wrappers.minecraft;

import org.jetbrains.annotations.Nullable;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;

public abstract class AbstractMinecraftSharedWrapper implements IMinecraftSharedWrapper {

    @Nullable
    protected Integer deserializeDimensionResourceKey(String dimensionResourceLocation) {
        try {
            return Integer.parseInt(dimensionResourceLocation.substring(dimensionResourceLocation.indexOf(":") + 1));
        } catch (NumberFormatException ignored) {
            return null;
        }

    }

}
