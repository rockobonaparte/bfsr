package com.bfsr.spawn;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class BfsrRegenTutorialDimension  implements ICommand {

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "bfsr_regen";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "Resets and regenerates the tutorial dimension.";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList(new String[]{});
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] params) {
        EntityPlayer player = null;
        EntityPlayer targetPlayer;

        // skyboy tells me CommandBase.getPlayer(sender, args[i]) will parse stuff like @p,
        // so I may eventually refactor all this to process arguments. Then I can give a
        // command block "/bfsr_spawn @p" and it would be able to target an appropriate player.
        // It would make for a more robust command.

        if(icommandsender instanceof EntityPlayer) {
            player = (EntityPlayer) icommandsender;
            targetPlayer = player;
        } else {
            World world = icommandsender.getEntityWorld();

            ChunkCoordinates senderCoords = icommandsender.getPlayerCoordinates();

            targetPlayer = world.getClosestPlayer(senderCoords.posX + 0.5D, senderCoords.posY + 0.5D, senderCoords.posZ + 0.5D, -1.0D);
            if(targetPlayer == null) {
                return;
            }
        }

        commandRegenTutorialWorld(targetPlayer);
    }

    private static void commandRegenTutorialWorld(EntityPlayer player) {
        World world = player.worldObj;
        if ( world.isRemote ) { return; }

        // get player's location at start of this teleport request
        double  px = Math.round(player.posX - .5); // player's coordinates rounded down
        double  py = Math.round(player.posY - .5);
        double  pz = Math.round(player.posZ - .5);
        Float   pyaw = player.rotationYaw;
        Float   ppitch = player.rotationPitch;

        player.travelToDimension(0);

        // Insert code to overwrite DIM2 in the save with the one from our template

        player.travelToDimension(2);        // Tutorial world dimension

        // Set ourselves back up roughly where we started
        ((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(px + .5d, py, pz + .5d, pyaw, ppitch);

    } // end commandRegenTutorialWorld

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] astring, int i) {
        return false;
    }

}
