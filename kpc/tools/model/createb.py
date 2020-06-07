import os
import codecs
itemName = input("item name: ").strip()
text = """{
	"parent": "kitchenparkour:block/""" + itemName + "\"" + """
}"""
with codecs.open(itemName + ".json", 'w+', encoding='utf8') as f:
    f.write(text)
