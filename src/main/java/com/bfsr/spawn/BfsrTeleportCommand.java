package com.bfsr.spawn;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.util.ChatComponentText;
import net.minecraft.command.CommandBase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

// Transports a player to the overworld spawn. If run from a non-player entity--like a command block--it will
// transport the nearest player to the overworld spawn. This was written to help with the tutorial village. Players
// would use a button a command block after spawning in the tutorial village to get whisked to the real overworld spawn,
// which would be generated according to their original intentions.

public class BfsrTeleportCommand extends CommandBase {
    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "bfsr_tp";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "Teleports a given player: /bfsr_tp [player name] [dimension] [x] [y] [z]";


    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList(new String[] { });
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] params) {
        if(params.length < 5) {
            icommandsender.addChatMessage(new ChatComponentText("Insufficient arguments: /bfsr_tp [player name] [dimension] [x] [y] [z]"));
            FMLLog.warning("Insufficient arguments to /bfsr_tp");
            return;
        }

        World world = icommandsender.getEntityWorld();
        EntityPlayerMP targetPlayer = getPlayer(icommandsender, params[0]);

        if(targetPlayer == null) {
            icommandsender.addChatMessage(new ChatComponentText("Cannot find player: " + params[0]));
            FMLLog.warning("/bfsr_tp given invalid player: " + params[0]);
            return;
        }

        int dimension = Integer.parseInt(params[1]);
        float x = Float.parseFloat(params[2]);
        float y = Float.parseFloat(params[3]);
        float z = Float.parseFloat(params[4]);
        try {
            FMLLog.info("About to teleport player");
            commandTeleport(world, targetPlayer, dimension, x, y, z);
            FMLLog.info("Teleported player");
        } catch(Exception e) {
            FMLLog.bigWarning("BFSR's /bfsr_tp command imploded!");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            FMLLog.warning(exceptionAsString);
        }
    }

    public static void commandTeleport(World world, EntityPlayerMP player, int pdim, float x, float y, float z) {
        if ( world.isRemote ) { return; }

        Integer originDim = world.provider.dimensionId;

        if ( originDim != pdim ) { // if changing dimensions
            // The following is a hack fix that overcomes a problem when directly leaving the end to any other dimension.
            // The problem: when you leave the end to another dimension the world will NOT load.
            // The solution: when you go to another dimension then go to a third one it WILL load, so we go to another one on the way.
            if ((originDim ==1 )) {
                player.travelToDimension(0);
            }

            FMLLog.info("Player is about to be sent to dimension #2");
            player.travelToDimension(pdim); // officially change dimension now
        } // end if changing dimensions

        // actually go there now
        FMLLog.info("Player being moved to origin in dimension.");
        player.playerNetServerHandler.setPlayerLocation(x, y, z, 0.0f, 0.0f);
    } // end commandSpawn

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
        return i == 0;
    }

}
