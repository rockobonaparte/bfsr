package com.bfsr.spawn;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;


/**
 * Created by Adam on 2/13/2016.
 */
public class BfsrRattle extends Item {
    public BfsrRattle() {
        setUnlocalizedName("bfsr_safetyRattle");
        setTextureName(BFSR.MODID + ":bfsr_safetyRattle");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(player instanceof EntityPlayerMP)
        {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            BfsrGotoNurseryCommand.commandNursery(playerMP);
        }
        return stack;
    }
}
