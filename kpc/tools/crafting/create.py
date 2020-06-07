import os
import codecs

def unique(text):
    ret = list()
    while len(text) > 0:
        letter = text[0]
        if letter == " ":
            text = text[1:]
        else:
            if letter in ret:
                text = text[1:]
            else:
                ret.append(letter)
                text = text[1:]
                text = text.replace(letter, "")
    return ret

print("Enter name of item to be crafted")
itemName = input("item name: ").strip()
print("""To enter the shape of the recipe as a string (9 Characters)
i.e. "aa b bc c" would yield
"aa",
"b b",
"c c"
""")

recipe = input("recipe: ")
while len(recipe) != 9:
    recipe = input("recipe: ")

#make pattern
"""
make pattern ie.
"pattern": [
  "RDR",
  " S ",
  " S "
],
"""
#construct pattern
patternText = """  "pattern": [
    """ + "\"" + recipe[0:3] + "\"" + """,
    """ + "\"" + recipe[3:6] + "\"" + """,
    """ + "\"" + recipe[6:9] + "\"" + """
],"""

#make tags
"""
make tags ie.
"R": {
  "item": "kitchenparkour:ruby_gem"
},"""
#construct tags
tags = unique(recipe)
components = list()
for tag in tags:
    inp = input("item name [" + tag + "]: ")
    if ":" in inp:
        components.append(inp)
    else:
        components.append("kitchenparkour:" + inp)
tagsText = ""
i = 0
letters = list("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
for tag in tags:
    tagsText += """    """ + "\"" + tag + """": {
      "item": """ + "\"" + components[i] + "\"" + """
    }"""
    if i != len(tags) - 1:
        tagsText += ","
        tagsText += """
"""
    i += 1

body = """{
  "type": "kitchenparkour:kpc_crafting_shaped",
""" + patternText + """
  "key": {
""" + tagsText + """
  },
  "result": {
    "item": "kitchenparkour:""" + itemName + """"
  }
}"""
with codecs.open(itemName + "_recipe.json", 'w+', encoding='utf8') as f:
    f.write(body)
