package com.yourname.tanaddon.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.item.FilledCanteenItem;

@Mixin(FilledCanteenItem.class)
public abstract class FilledCanteenMixin
{
    /**
     * Fixes a crash/duplication bug in the original inventoryTick implementation.
     * The original code used slot.getIndex() which doesn't work for all slot types.
     * This implementation searches for the exact stack instance in the inventory instead.
     */
    @Inject(
        method = "inventoryTick",
        at = @At("HEAD"),
        cancellable = true
    )
    private void fixInventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot, CallbackInfo ci)
    {
        FilledCanteenItem self = (FilledCanteenItem)(Object)this;
        
        // Basic checks: must be a player, must not already be purified, must have enchantments
        if (!(entity instanceof Player player) || stack.getItem() == self.getPurifiedWaterCanteen() || stack.getEnchantments().isEmpty())
        {
            ci.cancel();
            return;
        }

        // Create a new stack with the same damage value, except purified
        ItemStack newStack = new ItemStack(self.getPurifiedWaterCanteen());
        newStack.setDamageValue(stack.getDamageValue());
        stack.getEnchantments().entrySet().forEach(e -> newStack.enchant(e.getKey(), e.getIntValue()));

        // FIX: Find the exact slot index of THIS stack instance to prevent crash and duplication
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            // Check if the item in this slot is the exact same object as the one ticking
            if (inv.getItem(i) == stack)
            {
                inv.setItem(i, newStack);
                ci.cancel();
                return; // Stop looking once we found and replaced it
            }
        }
        
        ci.cancel();
    }
}