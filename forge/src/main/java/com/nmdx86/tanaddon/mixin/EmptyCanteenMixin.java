package com.nmdx86.tanaddon.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import toughasnails.item.EmptyCanteenItem;

@Mixin(EmptyCanteenItem.class)
public class EmptyCanteenMixin {

    private static final ThreadLocal<Integer> capturedTier = new ThreadLocal<>();

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static int captureTier(int tier) {
        capturedTier.set(tier);
        return tier;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Properties modifyProperties(Properties properties) {
        Integer tier = capturedTier.get();
        capturedTier.remove();

        if (tier != null && tier == 5) {
            return properties.fireResistant();
        }
        return properties;
    }
}