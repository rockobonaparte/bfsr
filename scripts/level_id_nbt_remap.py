# This takes a remap file and uses it to migrate a player.dat ID table. It requires the NBT Python module in order to
# open up and manipulate the NBT files

from nbt import nbt
import argparse
from migration_map import load_remap_as_dict, MinecraftThing, MinecraftMetadataThing

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--map", help="The remap json created by migration_map.py", type=str)
    parser.add_argument("--nbt", help="The level.nbt to remap (back it up!)", type=str)
    args = parser.parse_args()

    remap = load_remap_as_dict(args.map)
    nbtfile = nbt.NBTFile(args.nbt, 'rb')
    for pair in nbtfile["FML"]["ItemData"]:
        item_name_goofy_frontchar = pair.tags[1]
        item_name = item_name_goofy_frontchar[1:]
        item_id = pair.tags[0]

        if item_name in remap:
            new_item = remap[item_name]
            if type(new_item) is MinecraftThing:
                print("%s: %s -> %s" % (item_id, item_name, new_item.name))
            elif type(new_item) is MinecraftMetadataThing:
                print("%s: %s -> %s:%s" % (item_id, item_name, new_item.name, new_item.metadata))
            else:
                raise Exception("Unrecognized type of Minecraft item record: %s" % new_item.__class__.__name__)
        else:
            print("%s: %s" % (item_id, item_name))


