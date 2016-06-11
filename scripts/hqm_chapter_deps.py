import json
import re
import glob

class ChapterDependency(object):
    def __init__(self, other_chapter, other_quest):
        self.other_chapter = other_chapter
        self.other_quest = other_quest


class DependencyGraph(object):
    def __init__(self):
        self.chapters = []
        self.dependencies = {}

    def add_dependency(self, dependent, dependency):
        if dependent not in self.chapters:
            self.add_chapter(dependent)

        if dependency not in self.dependencies[dependent]:
            self.dependencies[dependent].append(dependency)

    def add_chapter(self, chapter):
        self.chapters.append(chapter)
        self.dependencies[chapter] = []

    def print_dependencies(self):
        for chapter in self.chapters:
            print(chapter)
            for dependency in self.dependencies[chapter]:
                print("\t" + dependency)

if __name__ == "__main__":

    graph = DependencyGraph()
    preq_chapter_quest = re.compile("\{(.+)\}\[(.+)\]")

    jsons_file_names = glob.glob(r"C:\space_race\bfsr_github\bfsr\config\hqm\QuestFiles\*.json")

    for json_file_name in jsons_file_names:

        if "reputations.json" in json_file_name:
            continue

        with open(json_file_name) as json_file:

            try:
                chapter = json.load(json_file)
                graph.add_chapter(chapter["name"])
                for quest in chapter["quests"]:
                    if "prerequisites" in quest:
                        for preq in quest["prerequisites"]:
                            matching = preq_chapter_quest.search(str(preq))
                            if matching:
                                dependency = matching.group(1)
                                graph.add_dependency(chapter["name"], dependency)

            except Exception as e:
                print("Couldn't parse %s" % json_file_name)
                print(e)

    graph.print_dependencies()