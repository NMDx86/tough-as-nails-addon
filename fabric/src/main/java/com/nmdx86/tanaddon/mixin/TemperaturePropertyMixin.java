package com.nmdx86.tanaddon.mixin;

import com.nmdx86.tanaddon.utils.CacheKey;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.client.item.TemperatureProperty;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = TemperatureProperty.class, remap = false)
public class TemperaturePropertyMixin
{
    @Unique
    private static final Map<CacheKey, Float> temperatureCache = new HashMap<>();

    @Unique
    private static final int MAX_CACHE_SIZE = 1000;

    @Inject(
            method = "get(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/ItemOwner;I)F",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void optimizedGet(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int i,
            CallbackInfoReturnable<Float> cir
    ) {
        if (owner == null) {
            cir.setReturnValue(0.5F);
            return;
        }

        if (!(owner instanceof Entity holder)) {
            cir.setReturnValue(0.5F);
            return;
        }

        Level holderLevel = holder.level();

        if (!holderLevel.isClientSide()) {
            cir.setReturnValue(0.5F);
            return;
        }

        if (level == null && holderLevel instanceof ClientLevel clientLevel) {
            level = clientLevel;
        }

        if (level == null) {
            cir.setReturnValue(0.5F);
            return;
        }

        BlockPos pos = holder.blockPosition();
        long gameTick = level.getGameTime();
        CacheKey key = new CacheKey(holder.getId(), pos, gameTick);

        Float cached = temperatureCache.get(key);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "get(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/ItemOwner;I)F",
            at = @At("RETURN"),
            remap = false
    )
    private void cacheResult(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int i,
            CallbackInfoReturnable<Float> cir
    ) {
        if (owner == null || level == null) {
            return;
        }

        if (!(owner instanceof Entity holder)) {
            return;
        }

        BlockPos pos = holder.blockPosition();
        long gameTick = level.getGameTime();
        CacheKey key = new CacheKey(holder.getId(), pos, gameTick);

        if (temperatureCache.size() > MAX_CACHE_SIZE) {
            temperatureCache.clear();
        }
        temperatureCache.put(key, cir.getReturnValue());
    }
}