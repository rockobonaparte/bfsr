# Load two bfsr dumps. The first is regarded as the original and the second is the target. If the same ID/metadata
# from the source cannot be found in the second, it will attempt to translate it by name into a remap. If it cannot
# be found at all, it will go into the not found list.

import json
import argparse
import sys


class MinecraftThing(object):
    def __init__(self, game_name, name, mc_id):
        self.game_name = game_name
        self.name = name
        self.mc_id = mc_id

    def __hash__(self):
        return self.name.__hash__() + self.mc_id.__hash__()

    def __eq__(self, other):
        if not isinstance(MinecraftThing, other):
            return False
        else:
            return self.game_name == other.game_name and self.name == other.name and self.mc_id == other.mc_id

    def as_json_dict(self):
        return {
            "name": self.game_name,
            "internal_name": self.name,
            "id": self.mc_id
        }

    def as_remap_dict_key(self):
        return self.name


class MinecraftMetadataThing(MinecraftThing):
    def __init__(self, game_name, name, mc_id, metadata):
        MinecraftThing.__init__(self, game_name, name, mc_id)
        self.metadata = metadata

    def __hash__(self):
        return MinecraftThing.__hash__(self) + self.metadata.__hash__()

    def __eq__(self, other):
        if not isinstance(MinecraftMetadataThing, other):
            return False
        else:
            return MinecraftThing.__eq__(self) and self.metadata == other.metadata

    def as_json_dict(self):
        the_dict = MinecraftThing.as_json_dict(self)
        the_dict["metadata"] = self.metadata
        return the_dict

    def as_remap_dict_key(self):
        return "%s:%s" % (self.name, self.metadata)


def build_from_json_obj(json_obj):
    if "metadata" in json_obj:
        return MinecraftMetadataThing(json_obj["name"], json_obj["internal_name"], json_obj["id"], json_obj["metadata"])
    else:
        return MinecraftThing(json_obj["name"], json_obj["internal_name"], json_obj["id"])


def find_by_name(item_list, name):
    return [item for item in item_list if item["name"] == name]


def load_remap_as_dict(remap_path):
    with open(remap_path, "r") as in_file:
        remap_list = json.load(in_file)

    remap_dict = {}
    for pair in remap_list:
        old = build_from_json_obj(pair[0])
        new = build_from_json_obj(pair[1])

        remap_dict[old.as_remap_dict_key()] = new

    return remap_dict


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--old", help="Source bfsr_dump.json", type=str)
    parser.add_argument("--new", help="New bfsr_dump.json", type=str)
    parser.add_argument("--undefined", "-u", help="Undefined (not found) items list output file (JSON)")
    parser.add_argument("--out", help="Output json translation map", type=str)

    args = parser.parse_args()

    with open(args.old) as old_file:
        old_json = json.load(old_file)

    with open(args.new) as new_file:
        new_json = json.load(new_file)

    collide_map = {}

    # {
    #     "metadata": 0,
    #     "name": "Oak Wood Planks",
    #     "id": 5,
    #     "internal_name": "tile.wood"
    # },
    remaps = []
    for record in old_json:
        matches = find_by_name(new_json, record["name"])

        # if len(matches) == 0:
        #     print("Not found: %s" % record["name"])
        # elif len(matches) > 1:
        #     print("%s matches: %s" % (len(matches), record["name"]))
        # else:
        #     singles += 1

        if len(matches) == 1:
            remaps.append([build_from_json_obj(record).as_json_dict(), build_from_json_obj(matches[0]).as_json_dict()])

    with open(args.out, "w") as out_file:
        out_file.write(json.dumps(remaps, indent=4))
