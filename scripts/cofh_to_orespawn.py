import json

if __name__ == "__main__":
    block_map = {
        "stone": "minecraft:stone",
        "dirt": "minecraft:dirt",
        "gravel": "minecraft:gravel",
        "coal_ore": "minecraft:coal_ore",
        "iron_ore": "minecraft:iron_ore",
        "gold_ore": "minecraft:gold_ore",
        "redstone_ore": "minecraft:redstone_ore",
        "diamond_ore": "minecraft:diamond_ore",
        "emerald_ore": "minecraft:emerald_ore",
        "lapis_ore": "minecraft:lapis_ore",
    }

    orespawn_root = {}
    dim_array = []
    dim0 = {}
    dim_array.append(dim0)
    orespawn_root["dimensions"] = dim_array
    dim0["dimension"] = "+"
    ores_array = []
    dim0["ores"] = ores_array

    in_file = open(r"C:\Users\Adam\AppData\Roaming\.technic\modpacks\babys-first-space-race\config\cofh\world\Vanilla.json", "r")
    cofh_json = json.load(in_file)

    for (label, gen_data) in cofh_json.items():
        new_ore = {}
        new_ore["blockID"] = block_map[gen_data["block"]]
        new_ore["size"] = gen_data["clusterSize"]
        new_ore["variation"] = int(gen_data["clusterSize"] / 2)
        new_ore["frequency"] = gen_data["numClusters"]

        if not "minHeight" in gen_data:
            new_ore["minHeight"] = 0
        else:
            new_ore["minHeight"] = gen_data["minHeight"]

        if not "maxHeight" in gen_data:
            new_ore["maxHeight"] = 64
        else:
            new_ore["maxHeight"] = gen_data["maxHeight"]

        new_ore["biomes"] = []
        ores_array.append(new_ore)

    print(json.dumps(orespawn_root, indent=4, sort_keys=True))
