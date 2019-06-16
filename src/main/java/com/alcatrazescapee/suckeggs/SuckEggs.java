/*
 * Part of the Suck Eggs mod by AlcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.suckeggs;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(SuckEggs.MOD_ID)
@Mod.EventBusSubscriber(modid = SuckEggs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SuckEggs
{
    static final String MOD_ID = "suckeggs";

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new NewEggItem().setRegistryName(new ResourceLocation("minecraft", "egg")));
    }

    @ParametersAreNonnullByDefault
    static class NewEggItem extends EggItem
    {
        NewEggItem()
        {
            super(new Item.Properties().maxStackSize(16).group(ItemGroup.MATERIALS).food(new Food.Builder().hunger(0).saturation(0.01f).setAlwaysEdible().build()));
        }

        @Override
        @Nonnull
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
        {
            if (playerIn.isSneaking())
            {
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }
            else
            {
                ItemStack itemstack = playerIn.getHeldItem(handIn);
                //noinspection ConstantConditions
                if (playerIn.canEat(getFood().canEatWhenFull()))
                {
                    playerIn.setActiveHand(handIn);
                    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
                }
                else
                {
                    return new ActionResult<>(ActionResultType.FAIL, itemstack);
                }
            }
        }

        @Nonnull
        public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving)
        {
            entityLiving.onFoodEaten(worldIn, stack.copy());
            return stack;
        }
    }

}
