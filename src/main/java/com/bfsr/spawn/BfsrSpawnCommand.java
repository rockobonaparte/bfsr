package com.bfsr.spawn;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

// Transports a player to the overworld spawn. If run from a non-player entity--like a command block--it will
// transport the nearest player to the overworld spawn. This was written to help with the tutorial village. Players
// would use a button a command block after spawning in the tutorial village to get whisked to the real overworld spawn,
// which would be generated according to their original intentions.

public class BfsrSpawnCommand implements ICommand {

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "bfsr_spawn";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "Teleports player to the default overworld spawn.";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList(new String[] { });
    }

    private static EntityPlayerMP getPlayerMP(EntityPlayer playerSP, World world) {
        Iterator iterator = world.playerEntities.iterator();

        while (iterator.hasNext())
        {
            Object obj = iterator.next();
            if ((obj instanceof EntityPlayer) && ((EntityPlayerMP)obj).getDisplayName().equalsIgnoreCase(playerSP.getDisplayName()))
            {
                return (EntityPlayerMP) obj;
            }
        }
        return null;
    }


    @Override
    public void processCommand(ICommandSender icommandsender, String[] params) {
        EntityPlayerMP player = null;
        EntityPlayerMP targetPlayer;

        // skyboy tells me CommandBase.getPlayer(sender, args[i]) will parse stuff like @p,
        // so I may eventually refactor all this to process arguments. Then I can give a
        // command block "/bfsr_spawn @p" and it would be able to target an appropriate player.
        // It would make for a more robust command.

        if(icommandsender instanceof EntityPlayerMP) {
            player = (EntityPlayerMP) icommandsender;
            targetPlayer = player;
        } else {
            World world = icommandsender.getEntityWorld();
            ChunkCoordinates senderCoords = icommandsender.getPlayerCoordinates();

            EntityPlayer nearestPlayerSP = world.getClosestPlayer(senderCoords.posX + 0.5D, senderCoords.posY + 0.5D, senderCoords.posZ + 0.5D, -1.0D);
            targetPlayer = getPlayerMP(nearestPlayerSP, world);

            if(targetPlayer == null) {
                return;
            }
        }

        commandSpawn(targetPlayer);
    }

//    private static void commandSpawn(EntityPlayer player) {
//        World world = player.worldObj;
//        if ( world.isRemote ) { return; }
//
//        // get player's location at start of this teleport request
//        double  px = Math.round(player.posX - .5); // player's coordinates rounded down
//        double  py = Math.round(player.posY - .5);
//        double  pz = Math.round(player.posZ - .5);
//        Float   pyaw = player.rotationYaw;
//        Float   ppitch = player.rotationPitch;
//        //Integer pdim = world.provider.getDimensionId(); // mc1.8
//        Integer pdim = world.provider.dimensionId; // mc1.7.10
//        Integer originDim = pdim;
//        String newGoBackLocation = px +","+ py +","+ pz +","+ pyaw +","+ ppitch +","+ pdim;
//
//        boolean toMainSpawn = false;
//        boolean isSharedName = false;
//        String theName = "";
//
//
//        // params.length == 0 destination is main spawn
//        toMainSpawn = true;
//        // x y z defined at time of dimensional change below
//        // pitch and yaw are still defined from the player's position above
//        pdim = 0;
//
//        if ( originDim != pdim ) { // if changing dimensions
//            // The following is a hack fix that overcomes a problem when directly leaving the end to any other dimension.
//            // The problem: when you leave the end to another dimension the world will NOT load.
//            // The solution: when you go to another dimension then go to a third one it WILL load, so we go to another one on the way.
//            if ( ( originDim == 1 ) && ( pdim != 1 ) ) {
//                if ( pdim == 0 ) {
//                    player.travelToDimension(-1);
//                } else {
//                    player.travelToDimension(0);
//                }
//            }
//            player.travelToDimension(pdim); // officially change dimension now
//        } // end if changing dimensions
//
//        // write the location of the next go back command
//        if ( toMainSpawn ) { // this is here because we have to already be in the main dimension to get the main spawn coordinates
//            // mc1.8
//            //BlockPos pxyz = world.getSpawnPoint();
//            //px = pxyz.getX();
//            //py = pxyz.getY();
//            //pz = pxyz.getZ();
//            // mc1.7.10
//            ChunkCoordinates pxyz = world.getSpawnPoint();
//            px = pxyz.posX;
//            py = pxyz.posY;
//            pz = pxyz.posZ;
//        }
//
//        // Don't spawn inside blocks check
//        while ( ( py < player.worldObj.getActualHeight() )
//                &&  ( (!canSpawnInsideBlock(player, px, py, pz)) || (!canSpawnInsideBlock(player, px, py + 1, pz)) )
//                )
//        { py++; }
//        // falling check
//        while ( ( py > 1 )
//                &&  ( (canSpawnInsideBlock(player, px, py - 1, pz)) && (canSpawnInsideBlock(player, px, py, pz)) )
//                )
//        { py--; }
//
//        // lava under feet check
//        // mc1.8
//        //BlockPos posBelowFeet = new BlockPos(px, (py-1), pz);
//        //if ((py>1) && ( ( player.worldObj.getBlockState(posBelowFeet).getBlock() == Blocks.lava) || ( player.worldObj.getBlockState(posBelowFeet).getBlock() == Blocks.flowing_lava) )) {
//        //  player.worldObj.setBlockState(posBelowFeet, Blocks.cobblestone.getDefaultState());
//        //}
//        // mc1.7.10
//        if ((py>1) && ( ( player.worldObj.getBlock((int)px, (int)(py-1), (int)pz) == Blocks.lava) || ( player.worldObj.getBlock((int)px, (int)(py-1), (int)pz) == Blocks.flowing_lava) )) {
//            player.worldObj.setBlock((int)px, (int)(py-1), (int)pz, Blocks.cobblestone);
//        }
//
//        // actually go there now
//        ((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(px + .5d, py, pz + .5d, pyaw, ppitch);
//
//    } // end commandSpawn

    private static void commandSpawn(EntityPlayerMP player) {
        World world = player.worldObj;
        if ( world.isRemote ) {
            return;
        }

        // Switch dimensions. Let's reuse our teleportation command so we don't crap out random nether portals.
        // We can move the player to the spawn point after we move to the Overworld.
        BfsrTeleportCommand.teleportToDimension(player, 0, 0, 64, 0);

        // mc1.8
        //BlockPos pxyz = world.getSpawnPoint();
        //px = pxyz.getX();
        //py = pxyz.getY();
        //pz = pxyz.getZ();
        // mc1.7.10
        ChunkCoordinates pxyz = world.getSpawnPoint();
        double px = pxyz.posX;
        double py = pxyz.posY;
        double pz = pxyz.posZ;

        // Don't spawn inside blocks check
        while ( ( py < player.worldObj.getActualHeight() )
                &&  ( (!canSpawnInsideBlock(player, px, py, pz)) || (!canSpawnInsideBlock(player, px, py + 1, pz)) )
                )
        { py++; }
        // falling check
        while ( ( py > 1 )
                &&  ( (canSpawnInsideBlock(player, px, py - 1, pz)) && (canSpawnInsideBlock(player, px, py, pz)) )
                )
        { py--; }

        // lava under feet check
        // mc1.8
        //BlockPos posBelowFeet = new BlockPos(px, (py-1), pz);
        //if ((py>1) && ( ( player.worldObj.getBlockState(posBelowFeet).getBlock() == Blocks.lava) || ( player.worldObj.getBlockState(posBelowFeet).getBlock() == Blocks.flowing_lava) )) {
        //  player.worldObj.setBlockState(posBelowFeet, Blocks.cobblestone.getDefaultState());
        //}
        // mc1.7.10
        if ((py>1) && ( ( player.worldObj.getBlock((int)px, (int)(py-1), (int)pz) == Blocks.lava) || ( player.worldObj.getBlock((int)px, (int)(py-1), (int)pz) == Blocks.flowing_lava) )) {
            player.worldObj.setBlock((int)px, (int)(py-1), (int)pz, Blocks.cobblestone);
        }

        // actually go there now
        ((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(px + .5d, py + .5d, pz + .5d, 0.0f, 0.0f);

    } // end commandSpawn



    public static boolean canSpawnInsideBlock(EntityPlayerMP player, double x, double y, double z) {
        return canSpawnInsideBlock(player,(int)x,(int)y,(int)z);
    }

    public static boolean canSpawnInsideBlock(EntityPlayerMP player, int x, int y, int z) {
        World world = player.worldObj;
        String[] spawnInBlocksLines = null;
        if ( y > world.getActualHeight() ) { return true; }
        if ( y < 1 ) { return false; }
        Block blockID = player.worldObj.getBlock(x, y, z);
        int blockMetaData = player.worldObj.getBlockMetadata(x,y,z);
        // if standard IDs then its ok
        return (blockID == Blocks.air) || // air
                (blockID == Blocks.bed) || // yes it is ok to spawn inside a bed.
                (blockID == Blocks.tallgrass) ||
                (blockID == Blocks.red_flower) ||
                (blockID == Blocks.yellow_flower) ||
                (blockID == Blocks.deadbush) ||
                (blockID == Blocks.vine) ||
                (blockID == Blocks.snow_layer) ||
                (blockID == Blocks.torch);
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
