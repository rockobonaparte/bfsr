import json
import glob
import os


def load_legacy_chapters(in_glob=r"C:\space_race\bfsr_github\bfsr\config\hqm\QuestFiles\*.json"):
    sets = {}
    for json_file in glob.glob(in_glob):
        if not json_file.endswith("reputations.json"):
            with open(json_file, "r") as in_json_file:
                set_json = json.load(in_json_file)
                sets[set_json["name"]] = set_json
    return sets


def dump_chapters(set_map, out_path=r"C:\temp\20170727\quests_out"):
    for set_name, set in set_map.items():
        with open(os.path.join(out_path, set_name + ".json"), "w") as json_out_fp:
            json.dump(set, json_out_fp, indent=2)
