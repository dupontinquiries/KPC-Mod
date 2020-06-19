package media.kitchen.parkour.itemtype.nbthandles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

public class ItemData {

    // int

    public static int getNBTInt(ItemStack stack, final String query) {
        CompoundNBT tags = stack.getOrCreateTag();

        if (tags.contains(query)) {
            return tags.getInt(query);
        } else {
            tags.putInt(query, -1);
            stack.setTag(tags);
            return -1;
        }
    }

    public static void setNBTInt(ItemStack stack, final String query, final int value) {
        CompoundNBT tags = stack.getOrCreateTag();

        tags.putInt(query, value);

        stack.setTag(tags);
    }

    // !int

    // uuid

    public static UUID getUUID(ItemStack stack, final String query) {
        CompoundNBT tags = stack.getOrCreateTag();

        if (tags.contains(query)) {
            return tags.getUniqueId(query);
        } else {
            tags.putUniqueId(query, null);
            stack.setTag(tags);
            return null;
        }
    }

    public static void setUUID(ItemStack stack, final String query, final UUID value) {
        CompoundNBT tags = stack.getOrCreateTag();

        tags.putUniqueId(query, value);

        stack.setTag(tags);
    }

    // ! uuid

}
