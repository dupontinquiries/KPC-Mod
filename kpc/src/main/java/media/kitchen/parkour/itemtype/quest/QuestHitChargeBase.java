package media.kitchen.parkour.itemtype.quest;

import media.kitchen.parkour.Parkour;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class QuestHitChargeBase<T extends Item> extends QuestItemBase {

    protected ArrayList<Entity> filter;
    protected float f;

    public QuestHitChargeBase( T item ) {
        super(item);
        filter = new ArrayList<Entity>();
        f = 1;
    }

    public QuestHitChargeBase( T item, float f ) {
        super(item);
        filter = new ArrayList<Entity>();
        this.f = f;
    }

    @Override
    protected void activateQuest(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            if (getNBTBoolean(stack, actTag) == false) {
                stack.damageItem((int) (stack.getMaxDamage() * f) - 1, living, (p_220045_0_) -> {
                    p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
                });
                world.playSound(null, new BlockPos(entity), Parkour.PARKOUR_GRIPPER_READY.get(), SoundCategory.AMBIENT, 1F, 1F + ( Item.random.nextFloat() * 0.4F ) - 0.2F );
            }
        }
        setNBTBoolean(stack, actTag, true);
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    //@Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            boolean flag = false;

            for (Entity e : filter) {

            }

            if (rechargeOnePoint(stack, player)) {
                yieldItem(stack, player);
            }

        }
        return false;
    }

}
