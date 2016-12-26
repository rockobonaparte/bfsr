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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

// Transports a player to the overworld spawn. If run from a non-player entity--like a command block--it will
// transport the nearest player to the overworld spawn. This was written to help with the tutorial village. Players
// would use a button a command block after spawning in the tutorial village to get whisked to the real overworld spawn,
// which would be generated according to their original intentions.

public class BfsrGotoNurseryCommand implements ICommand {

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "bfsr_nursery";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "Teleports player to the nursery dimension.";


    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList(new String[] { });
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] params) {

        if (icommandsender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) icommandsender;
            commandNursery(player);
        }
    }

    public static void commandNursery(EntityPlayerMP player)
    {
        BfsrTeleportCommand.teleportToDimension(player, 2, 13.0d, 66.0d, 30.0d);
        // Used when doing in-development tests. Very funny to accidentally distribute! :(
        //BfsrTeleportCommand.teleportToDimension(player, 0, 0.0d, 66.0d, 0.0d);
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
        return false;
    }

}
