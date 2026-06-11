package com.seibel.distanthorizons.common.wrappers.level;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.multiplayer.WorldClient;

import org.jetbrains.annotations.Nullable;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import com.seibel.distanthorizons.core.level.IKeyedClientLevelManager;
import com.seibel.distanthorizons.core.level.IServerKeyedClientLevel;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;

public class KeyedClientLevelManager implements IKeyedClientLevelManager {

    public static final KeyedClientLevelManager INSTANCE = new KeyedClientLevelManager();

    /** Stores server-provided keys by dimension name. */
    private final Map<String, KeyInfo> keysByDimensionName = new ConcurrentHashMap<>();

    /** Cache keyed wrappers by MC client level to preserve wrapper identity. */
    private final Map<WorldClient, IServerKeyedClientLevel> keyedLevelsCache = Collections
        .synchronizedMap(new WeakHashMap<>());

    /** Allows to keep level manager enabled between loading different keyed levels */
    private volatile boolean enabled = false;

    // =============//
    // constructor //
    // =============//

    private KeyedClientLevelManager() {}

    // ======================//
    // level override logic //
    // ======================//

    @Override
    @Nullable
    public IServerKeyedClientLevel getServerKeyedLevel(IClientLevelWrapper levelWrapper) {
        if (levelWrapper == null) {
            return null;
        }

        WorldClient level = (WorldClient) levelWrapper.getWrappedMcObject();
        synchronized (this.keyedLevelsCache) {
            IServerKeyedClientLevel cached = this.keyedLevelsCache.get(level);
            if (cached != null) {
                return cached;
            }

            IClientLevelWrapper wrappedLevel = ClientLevelWrapper.getWrapper(level, true);
            if (wrappedLevel == null) {
                return null;
            }

            KeyInfo info = this.keysByDimensionName.get(wrappedLevel.getDimensionName());
            if (info == null) {
                return null;
            }

            IServerKeyedClientLevel keyedLevel = new ServerKeyedClientLevel(level, info.serverKey, info.levelKey);
            this.keyedLevelsCache.put(level, keyedLevel);
            return keyedLevel;
        }
    }

    @Override
    public IServerKeyedClientLevel setServerKeyedLevel(IClientLevelWrapper clientLevel, String dimensionResource,
        String serverKey, String levelKey) {
        this.keysByDimensionName.put(dimensionResource, new KeyInfo(serverKey, levelKey));
        this.enabled = true;

        synchronized (this.keyedLevelsCache) {
            this.keyedLevelsCache.keySet()
                .removeIf(
                    level -> ClientLevelWrapper.getWrapper(level, true)
                        .getDimensionName()
                        .equals(dimensionResource));
        }

        if (clientLevel == null || !clientLevel.getDimensionName()
            .equals(dimensionResource)) {
            return null;
        }

        return this.getServerKeyedLevel(clientLevel);
    }

    @Override
    public void clearKeyedLevel() {
        synchronized (this.keyedLevelsCache) {
            this.keyedLevelsCache.clear();
            this.keysByDimensionName.clear();
        }
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void disable() {
        this.enabled = false;
        this.clearKeyedLevel();
    }

    private static class KeyInfo {

        public final String serverKey;
        public final String levelKey;

        public KeyInfo(String serverKey, String levelKey) {
            this.serverKey = serverKey;
            this.levelKey = levelKey;
        }

    }

}
