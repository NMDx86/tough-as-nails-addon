package com.nmdx86.tanaddon.utils;

import net.minecraft.core.BlockPos;
import java.util.Objects;

public final class CacheKey {
    private final int entityId;
    private final BlockPos pos;
    private final long gameTick;

    public CacheKey(int entityId, BlockPos pos, long gameTick) {
        this.entityId = entityId;
        this.pos = pos;
        this.gameTick = gameTick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheKey cacheKey)) return false;
        return entityId == cacheKey.entityId &&
                gameTick == cacheKey.gameTick &&
                pos.equals(cacheKey.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, pos, gameTick);
    }
}