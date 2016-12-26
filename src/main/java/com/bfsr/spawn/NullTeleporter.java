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
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Created by Adam on 7/20/2016.
 */
public class NullTeleporter  extends Teleporter {

    final WorldServer worldServer;
//    private static Logger logger = LogManager.getLogger(BFSR.MODID);

    private double x, y, z;

    public NullTeleporter(WorldServer world, double x, double y, double z) {
        super(world);

        this.worldServer = world;
        this.x = x;
        this.y = y;
        this.z = z;

//        logger.info("Creating a BFSR NullTeleporter");

    }

    @Override
    public void placeInPortal(Entity pEntity, double p2, double p3, double p4, float p5) {
        this.worldServer.getBlock((int) this.x, (int) this.y, (int) this.z);   //dummy load to maybe gen chunk

        pEntity.setPosition(this.x, this.y, this.z);
        pEntity.motionX = 0.0f;
        pEntity.motionY = 0.0f;
        pEntity.motionZ = 0.0f;

//        logger.info("Placing player in the BFSR NullTeleporter");

    }
}
