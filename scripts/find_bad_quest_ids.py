import re
from hqm_helpers import load_legacy_chapters


class LintFactory(object):
    def __init__(self):
        self.null_quest_icon_re = re.compile(r"Set: \'(.+?)\' Quest \'(.+?)\'")
        self.null_quest_icons = {}

    def load_from_file(self, path=r"C:\temp\20170727\bfsr_lint_log.txt"):
        with open(path, 'r') as lint_errs:
            for lint_line in lint_errs:
                null_quest_match = self.null_quest_icon_re.search(lint_line)
                if null_quest_match:
                    set = null_quest_match.group(1)
                    quest = null_quest_match.group(2)
                    if set not in self.null_quest_icons:
                        self.null_quest_icons[set] = [quest]
                    else:
                        self.null_quest_icons[set].append(quest)


def find_quest(json_set, quest_name):
    quests = json_set["quests"]
    for quest in quests:
        if quest["name"] == quest_name:
            return quest
    return None

if __name__ == "__main__":
    json_sets = load_legacy_chapters()
    factory = LintFactory()
    factory.load_from_file()
    for (set, quests) in factory.null_quest_icons.items():
        for quest in quests:
            quest_json = find_quest(json_sets[set], quest)
            if quest_json is None:
                print("Unrecognized set/quest %s . %s" % (set, quest))
            else:
                print(quest_json["icon"])