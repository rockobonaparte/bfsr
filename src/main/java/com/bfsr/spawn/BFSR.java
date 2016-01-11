package com.bfsr.spawn;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;

@Mod(modid = BFSR.MODID, version = BFSR.VERSION)
public class BFSR {
    public static final String MODID = "bfsr";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        System.out.println("Initialized BFSR spawn helpers.");
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        ServerCommandManager manager = (ServerCommandManager) event.getServer().getCommandManager();
        manager.registerCommand(new BfsrSpawnCommand());
        manager.registerCommand(new BfsrGotoNurseryCommand());
    }
}