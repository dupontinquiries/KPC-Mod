import os
import codecs
itemName = input("item name: ").strip()

text = """{
	"variants": {
		"": {
			"model": "kitchenparkour:block/custom_block_name"
		}
	}
}""".replace("custom_block_name", itemName)
with codecs.open(itemName + ".json", 'w+', encoding='utf8') as f:
    f.write(text)
