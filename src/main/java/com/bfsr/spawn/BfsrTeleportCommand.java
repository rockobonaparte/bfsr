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
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

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
            teleportToDimension(targetPlayer, dimension, x, y, z);
            FMLLog.info("Teleported player");
        } catch(Exception e) {
            FMLLog.bigWarning("BFSR's /bfsr_tp command imploded!");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            FMLLog.warning(exceptionAsString);
        }
    }

    // Shamelessly stolen from RFTools.
    // Thanks McJty!
    // I discovered that the reason nether portals are spawning everywhere is because the player teleportation command
    // has logic to cause one to generate. Well, RFTool doesn't do that, and this is ground zero for executing the
    // actual cross-dimension movement.
    public static void teleportToDimension(EntityPlayerMP player, int dimension, double x, double y, double z) {
        int oldDimension = player.worldObj.provider.dimensionId;
        WorldServer worldServer = MinecraftServer.getServer().worldServerForDimension(dimension);
        player.addExperienceLevel(0);
        MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, dimension, new NullTeleporter(worldServer, x, y, z));
        if (oldDimension == 1) {
            // For some reason teleporting out of the end does weird things.
            player.setPositionAndUpdate(x, y, z);
            worldServer.spawnEntityInWorld(player);
            worldServer.updateEntityWithOptionalForce(player, false);
        }
    }

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
