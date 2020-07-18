package com.alcatrazescapee.suckeggs.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EggItem.class)
public abstract class EggItemMixin extends Item
{
    private final FoodComponent foodComponent = new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().build();

    public EggItemMixin(Settings settings)
    {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci)
    {
        if (!user.isSneaking())
        {
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(getFoodComponent().isAlwaysEdible()))
            {
                user.setCurrentHand(hand);
                ci.setReturnValue(TypedActionResult.consume(itemStack));
            }
            else
            {
                ci.setReturnValue(TypedActionResult.fail(itemStack));
            }
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        return this.isFood() ? user.eatFood(world, stack.copy()) : stack;
    }

    @Override
    public boolean isFood()
    {
        return true;
    }

    @Override
    public FoodComponent getFoodComponent()
    {
        return foodComponent;
    }
}
