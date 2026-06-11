package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractMinecraftSharedWrapper implements IMinecraftSharedWrapper
{

    @Nullable
    protected Integer deserializeDimensionResourceKey(String dimensionResourceLocation)
{
    try
    {
        return Integer.parseInt(dimensionResourceLocation.substring(dimensionResourceLocation.indexOf(":")+1));
    }
    catch (NumberFormatException ignored)
    {
        return null;
    }

}

}
