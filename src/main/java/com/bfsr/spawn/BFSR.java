package com.bfsr.spawn;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;

@Mod(modid = BFSR.MODID, version = BFSR.VERSION)
public class BFSR {
    public static final String MODID = "bfsr";
    public static final String VERSION = "1.1";

    public static Item safetyRattle;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        safetyRattle = new BfsrRattle();

        // The second parameter is an unique registry identifier (not the displayed name)
        // Please don't use genericItem.getUnlocalizedName(), or you will make Lex sad
        GameRegistry.registerItem(safetyRattle, "genericItem");

        ItemStack gravelStack = new ItemStack(Blocks.gravel);
        ItemStack dirtStack = new ItemStack(Blocks.dirt);
        ItemStack sandStack = new ItemStack(Blocks.sand);

        GameRegistry.addRecipe(new ItemStack(BFSR.safetyRattle),
                "A",
                "A",
                'A',gravelStack);

        GameRegistry.addRecipe(new ItemStack(BFSR.safetyRattle),
                "A",
                "A",
                'A',dirtStack);

        GameRegistry.addRecipe(new ItemStack(BFSR.safetyRattle),
                "A",
                "A",
                'A',sandStack);

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        System.out.println("Initialized BFSR spawn helpers.");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Need to wait until init to register this because we need to make sure Natura's loaded first!
        Block taintedSoilBlock = GameRegistry.findBlock("Natura", "soil.tainted");
        ItemStack taintedStack = new ItemStack(taintedSoilBlock);
        GameRegistry.addRecipe(new ItemStack(BFSR.safetyRattle),
                "A",
                "A",
                'A',taintedStack);
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        ServerCommandManager manager = (ServerCommandManager) event.getServer().getCommandManager();
        manager.registerCommand(new BfsrSpawnCommand());
        manager.registerCommand(new BfsrGotoNurseryCommand());
        manager.registerCommand(new BfsrRegenTutorialDimension());
        manager.registerCommand(new BfsrTeleportCommand());
    }
}