/*
 * Part of the Suck Eggs mod by AlcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.suckeggs;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SuckEggs.MOD_ID, version = SuckEggs.VERSION, dependencies = SuckEggs.DEPENDENCIES)
@Mod.EventBusSubscriber(modid = SuckEggs.MOD_ID)
public class SuckEggs
{
    public static final String MOD_ID = "suckeggs";
    public static final String VERSION = "0.1";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2768,15.0.0.0);";

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemNewEgg().setRegistryName(new ResourceLocation("minecraft", "egg")));
    }

    @ParametersAreNonnullByDefault
    static class ItemNewEgg extends ItemFood
    {

        ItemNewEgg()
        {
            super(0, 0.01f, false);
            setAlwaysEdible();
            setMaxStackSize(16);
            setCreativeTab(CreativeTabs.MATERIALS);
            setTranslationKey("egg");
        }

        @Nonnull
        public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
        {
            if (entityLiving instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entityLiving;
                player.getFoodStats().addStats(this, stack);
                worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                onFoodEaten(stack, worldIn, player);
                //noinspection ConstantConditions
                player.addStat(StatList.getObjectUseStats(this));

                if (player instanceof EntityPlayerMP)
                {
                    CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
                }
            }
            return stack;
        }

        @Override
        @Nonnull
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            if (playerIn.isSneaking())
            {
                ItemStack stack = playerIn.getHeldItem(handIn);
                if (!playerIn.capabilities.isCreativeMode)
                {
                    stack.shrink(1);
                }

                worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

                if (!worldIn.isRemote)
                {
                    EntityEgg entityegg = new EntityEgg(worldIn, playerIn);
                    entityegg.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
                    worldIn.spawnEntity(entityegg);
                }

                //noinspection ConstantConditions
                playerIn.addStat(StatList.getObjectUseStats(this));
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }
    }

}
