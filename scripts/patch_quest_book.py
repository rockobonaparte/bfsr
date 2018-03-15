import re
from hqm_helpers import load_legacy_chapters, dump_chapters
from migration_map import load_remap_as_dict


class IdRules(object):
    def __init__(self, sub_rules=[]):
        uncompiled = sub_rules
        self.sub_rules = []
        for regex, sub in uncompiled:
            self.sub_rules.append((re.compile(regex), sub))

    def apply_rules(self, in_id):
        transformed = in_id
        for subexpr in self.sub_rules:
            transformed = subexpr[0].sub(subexpr[1], transformed)
        return transformed


# Let's keep a tally of all the stuff we muck with!
change_count = 0


def iterate_all_ids(json_obj, id_rules, meta_remaps, remap_dict):
    global change_count
    if type(json_obj) is dict:
        found_remap = False

        # First let's see if we have a 1:1 map for it.
        if "id" in json_obj:
            val = json_obj["id"]
            remap_key = val
            if "damage" in json_obj:
                remap_key = "%s:%s" % (val, json_obj["damage"])

            if remap_key in remap_dict:

                remapped = remap_dict[remap_key]
                new_name = remapped.name
                json_obj["id"] = new_name
                if hasattr(remapped, "metadata"):
                    add_damage = remapped.metadata
                    new_name += " metadata=%s" % remapped.metadata
                else:
                    if "damage" in json_obj:
                        # We can't delete it here because we're actively iterate json_obj
                        del_damage = True
                print("Remap %s => %s" % (val, new_name))
                found_remap = True
                change_count += 1

        # Try metadata remap rules
        if not found_remap:
            for meta_remap in meta_remaps:
                if meta_remap.matches_json_dict(json_obj):
                    meta_remap.remap(json_obj)
                    found_remap = True
                    change_count += 1
                    break

        if not found_remap:
            for key, val in json_obj.items():
                # Try the regular expressions and other sacks full of tricks.
                if type(val) is str and key.lower() == "id":
                    original = val
                    transformed = id_rules.apply_rules(val)
                    print("Regex %s => %s" % (original, transformed))
                    change_count += 1

                    json_obj[key] = transformed
                elif type(val) is dict or type(val) is list:
                    iterate_all_ids(val, id_rules, meta_remaps, remap_dict)

    elif type(json_obj) is list:
        for list_item in json_obj:
            iterate_all_ids(list_item, id_rules, meta_remaps, remap_dict)


class MetaBlockRemap(object):

    def __init__(self, original_id, original_damage, new_id, new_damage=None):
        self.original_id = original_id
        self.original_damage = original_damage
        self.new_id = new_id
        self.new_damage = new_damage

    def matches_json_dict(self, json_dict):
        if "id" in json_dict and "damage" in json_dict:
            if json_dict["id"] == self.original_id and json_dict["damage"] == self.original_damage:
                return True
        return False

    def remap(self, json_dict):
        json_dict["id"] = self.new_id
        if self.new_damage is not None:
            json_dict["damage"] = self.new_damage
        else:
            del json_dict["damage"]

if __name__ == "__main__":
    sets = load_legacy_chapters()

    rules = [
        ("GalacticraftCore:tile\.", "galacticraftcore:"),
        ("GalacticraftCore:item\.", "galacticraftcore:"),
        ("galacticraftcore:oxygenCollector", "galacticraftcore:collector"),
        ("galacticraftcore:oxygenPipe", "galacticraftcore:fluid_pipe"),
        ("galacticraftcore:rocketWorkbench", "galacticraftcore:rocket_workbench"),
        ("galacticraftcore:oxygenDetector", "galacticraftcore:oxygen_detector"),
        ("galacticraftcore:glowstoneTorch", "galacticraftcore:glowstone_torch"),
        ("galacticraftcore:item.itemTier1Rocket", "galacticraftcore:rocket_t1"),
        ("GalacticraftMars:item.itemTier2Rocket", "galacticraftplanets:rocket_t2"),
        ("GalacticraftMars:item.itemTier3Rocket", "galacticraftplanets:rocket_t3"),
        ("GalacticraftMars:item.itemAstroMiner", "galacticraftplanets:astro_miner"),
        ("GalacticraftMars:tile.mars", "galacticraftplanets:mars"),
        ("galacticraftcore:landingPad", "galacticraftcore:landing_pad"),
        ("galacticraftcore:oxygenGear", "galacticraftcore:oxygen_gear"),
        ("galacticraftcore:rocketWorkbench", "galacticraftcore:rocket_workbench"),
        ("galacticraftcore:spinThruster", "galacticraftcore:spin_thruster"),
        ("galacticraftcore:airLockFrame", "galacticraftcore:air_lock_frame"),
        ("galacticraftcore:meteoricIronRaw", "galacticraftcore:meteoric_iron_raw"),
        ("appliedenergistics2:item\.", "appliedenergistics2:"),
        ("appliedenergistics2:tile\.", "appliedenergistics2:"),
        ("appliedenergistics2:BlockCharger", "appliedenergistics2:charger"),
        ("appliedenergistics2:BlockSecurity", "appliedenergistics2:security_station"),
        ("appliedenergistics2:BlockEnergyAcceptor", "appliedenergistics2:energy_acceptor"),
        ("appliedenergistics2:BlockMolecularAssembler", "appliedenergistics2:molecular_assembler"),
        ("appliedenergistics2:BlockController", "appliedenergistics2:controller"),
        ("appliedenergistics2:BlockSkyCompass", "appliedenergistics2:sky_compass"),
        ("appliedenergistics2:BlockCraftingStorage", "appliedenergistics2:crafting_storage_1k"),
        ("appliedenergistics2:BlockChest", "appliedenergistics2:chest"),
        ("appliedenergistics2:BlockDrive", "appliedenergistics2:drive"),
        ("appliedenergistics2:ToolWirelessTerminal", "appliedenergistics2:wireless_terminal"),
        ("appliedenergistics2:BlockQuartzGrowthAccelerator", "appliedenergistics2:quartz_growth_accelerator"),
        ("appliedenergistics2:ItemBasicStorageCell.1k", "appliedenergistics2:storage_cell_1k"),
        ("appliedenergistics2:ItemEncodedPattern", "appliedenergistics2:encoded_pattern"),
        ("appliedenergistics2:BlockQuantumLinkChamber", "appliedenergistics2:quantum_link_chamber"),
        ("appliedenergistics2:BlockSpatialPylon", "appliedenergistics2:spatial_pylon"),
        ("appliedenergistics2:ItemMultiPart", "appliedenergistics2:part"),
        ("appliedenergistics2:ItemMultiMaterial", "appliedenergistics2:material"),
        ("BiblioCraft:item\.", "bibliocraft:"),
        ("TConstruct:Smeltery", "tconstruct:seared"),
        ("TConstruct:GlassBlock", "tconstruct:clear_glass"),
        ("TConstruct:SearedBrick", "tconstruct:materials"),
        ("TConstruct:ToolStationBlock", "tconstruct:tooltables"),
        ("TConstruct:ToolForgeBlock", "tconstruct:toolforge"),
        ("TConstruct:Crossbow", "tconstruct:crossbow"),
        ("TConstruct:jerky", "tconstruct:edible"),
        ("TConstruct:metalPattern", "tconstruct:pattern"),
        ("ExtraUtilities", "extrautils2"),
        ("ExtraUtilities:dark_portal", "extrautils2:teleporter"),
        ("ExtraUtilities:watering_can", "extrautils2:wateringcan"),
        ("ExtraUtilities:", "extrautils2:"),
        ("ExtraUtilities:", "extrautils2:"),
        ("ExtraUtilities:", "extrautils2:"),
        ("MineFactoryReloaded:machine.0", "minefactoryreloaded:machine_0"),
        ("MineFactoryReloaded:machine.1", "minefactoryreloaded:machine_1"),
        ("MineFactoryReloaded:machine.2", "minefactoryreloaded:machine_2"),
        ("MineFactoryReloaded:laserfocus", "minefactoryreloaded:laser_focus"),
        ("MineFactoryReloaded:plastic.bag", "minefactoryreloaded:plastic_bag"),
        ("MineFactoryReloaded:plastic.cup", "minefactoryreloaded:plastic_cup"),
        ("MineFactoryReloaded:upgrade.radius", "minefactoryreloaded:upgrade_radius"),
        ("MineFactoryReloaded:rubber\.", "minefactoryreloaded:rubber_"),
        ("chisel:factoryblock", "chisel:factory"),
        ("chisel:paperwall", "chisel:paper"),
        ("BigReactors:BROre", "bigreactors:minerals"),
        ("BigReactors:BRIngot", "bigreactors:ingotmetals"),
        ("BigReactors:BRMetalBlock", "bigreactors:blockmetals"),
        ("BigReactors:YelloriumFuelRod", "bigreactors:reactorfuelrod"),
        ("BigReactors:BRReactorRedstonePort", "bigreactors:reactorredstoneport"),
        ("BigReactors:BRDevice", "bigreactors:reactorredstoneport"),
        ("BigReactors:BRReactorPart", "bigreactors:reactorcasing"),      # Sometimes BRReactorPart shows up without metadata. It looks like it's indicating reactor casings.
        ("Natura:natura.axe.bloodwood", "natura:bloodwood_axe"),
        ("Natura:NetherFurnace", "natura:netherrack_furnace"),
        ("Natura:berryMedley", "natura:soups"),
        ("Natura:berry", "natura:edibles"),
        ("Natura:barley", "natura:materials"),
        ("Natura:barleyFood", "natura:barley_crop"),
        ("rftools:remoteStorageBlock", "rftools:remote_storage"),
        ("rftools:dimensionEnscriberBlock", "rftools:dimension_enscriber"),
        ("rftools:dialingDeviceBlock", "rftools:dialing_device"),
        ("rftools:matterTransmitterBlock", "rftools:matter_transmitter"),
        ("rftools:remoteStorageBlock", "rftools:remote_storage"),
        ("rftools:modularStorageBlock", "rftools:modular_storage"),
        ("rftools:unknownDimlet", "rftools:unknown_dimlet"),
        ("Mekanism:PartTransmitter", "mekanism:MultipartTransmitter"),
        ("OpenBlocks:paintmixer", "openblocks:paint_mixer"),
        ("OpenBlocks:hangglider", "openblocks:hang_glider"),
        ("ThermalFoundation:FluidEnder", "thermalfoundation:fluid_ender"),
        ("ThermalFoundation:", "thermalfoundation:"),
        ("ThermalExpansion:", "thermalexpansion:"),
        ("CompactMachines:machine", "cm2:machine"),
        ("CompactMachines:innerwallcreative", "cm2:wall"),
        ("StorageDrawers:halfDrawers4", "storagedrawers:basicDrawers")
        #("", ""),
    ]

    id_rules = IdRules(rules)

    id_damage_remaps = [MetaBlockRemap("BigReactors:BRReactorPart", 0, "bigreactors:reactorcasing"),
                        MetaBlockRemap("BigReactors:BRReactorPart", 1, "bigreactors:reactorcontroller"),
                        MetaBlockRemap("BigReactors:BRReactorPart", 2, "bigreactors:reactorcontrolrod"),
                        MetaBlockRemap("BigReactors:BRReactorPart", 3, "bigreactors:reactorpowertaprf"),
                        MetaBlockRemap("BigReactors:BRReactorPart", 4, "bigreactors:reactoraccessport"),
                        MetaBlockRemap("BigReactors:BRReactorPart", 5, "bigreactors:reactorcoolantport"),
                        MetaBlockRemap("BigReactors:BRReactorPart", 6, "bigreactors:reactorrednetport"),
                        MetaBlockRemap("BigReactors:BRReactorPart", 7, "bigreactors:reactorcomputerport"),
                        MetaBlockRemap("BigReactors:BRMultiblockGlass", 0, "bigreactors:reactorglass"),
                        MetaBlockRemap("BigReactors:BRMultiblockGlass", 1, "bigreactors:turbineglass"),
                        MetaBlockRemap("BigReactors:BRTurbinePart", 0, "bigreactors:turbinehousing"),
                        MetaBlockRemap("BigReactors:BRTurbinePart", 1, "bigreactors:turbinecontroller"),
                        MetaBlockRemap("BigReactors:BRTurbinePart", 2, "bigreactors:turbinepowertaprf"),
                        MetaBlockRemap("BigReactors:BRTurbinePart", 3, "bigreactors:turbinefluidport"),
                        MetaBlockRemap("BigReactors:BRTurbinePart", 4, "bigreactors:turbinebearing"),
                        MetaBlockRemap("BigReactors:BRTurbinePart", 5, "bigreactors:turbinecomputerport"),
                        MetaBlockRemap("BigReactors:BRTurbineRotorPart", 0, "bigreactors:turbinerotorshaft"),
                        MetaBlockRemap("BigReactors:BRTurbineRotorPart", 1, "bigreactors:turbinerotorblade"),
                        ]

    remap_dict = load_remap_as_dict(r"C:\temp\20170906\bfsr_ids_map.json")

    iterate_all_ids(sets, id_rules, id_damage_remaps, remap_dict)

    dump_chapters(sets)

    print("Performend %s changes" % change_count)