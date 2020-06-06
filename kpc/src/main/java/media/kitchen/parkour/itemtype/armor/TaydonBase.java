package media.kitchen.parkour.itemtype.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class TaydonBase extends ArmorBase {

    public TaydonBase(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        BlockPos bp = new BlockPos(entityIn);
        boolean seeSky = worldIn.canBlockSeeSky(bp);
        int lightValue = worldIn.getLightFor(LightType.BLOCK, bp);
        boolean isDay = worldIn.isDaytime();
        boolean living = entityIn instanceof LivingEntity;
        LivingEntity livingEntity = null;
        if ( living ) {
            livingEntity = (LivingEntity) entityIn;
        }
        boolean solarEffect = seeSky
                && !worldIn.isRaining() && !worldIn.isThundering()
                && isDay;

        if ( livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            boolean isArmorWorn = player.inventory.armorItemInSlot(0) == stack ||
                    player.inventory.armorItemInSlot(1) == stack ||
                    player.inventory.armorItemInSlot(2) == stack ||
                    player.inventory.armorItemInSlot(3) == stack;
            taydonEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
        }
    }

    protected String cooldownTag = "kcoo";

    protected int maxCooldown = 40;

    protected int getNBTInt(ItemStack stack, final String query) {
        CompoundNBT tags = stack.getOrCreateTag();

        if (tags.contains(query)) {
            return tags.getInt(query);
        } else {
            tags.putInt(query, -1);
            stack.setTag(tags);
            return -1;
        }
    }

    protected void slowTaydonEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                                    boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( !solarEffect ) {
            if ( isArmorWorn ) {
                player.heal(3);
            }
            repairItem(stack, player, lightValue);
        }
    }

    protected void taydonEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                                boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        // timer
        int cooldown = getNBTInt(stack, cooldownTag);
        if (cooldown == -1) cooldown = maxCooldown;
        if (cooldown > 0) {
            --cooldown;
        } else {
            cooldown = maxCooldown;
            slowTaydonEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
        }
        // !timer
        // potions
        applyPotions(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
        // !potions
    }

    protected void applyPotions(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                               boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( isArmorWorn && ( worldIn.isNightTime() || !solarEffect ) ) {
            boolean invisFlag = !player.isPotionActive(Effects.INVISIBILITY) ||
                    ( player.isPotionActive(Effects.INVISIBILITY) && player.getActivePotionEffect(Effects.INVISIBILITY).getDuration() < 20 );
            if ( invisFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 20, 1));
            }

            boolean speedFlag = !player.isPotionActive(Effects.SPEED) ||
                    ( player.isPotionActive(Effects.SPEED) && player.getActivePotionEffect(Effects.SPEED).getDuration() < 20 );
            if ( speedFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 20, 1));
            }

            boolean jumpFlag = !player.isPotionActive(Effects.JUMP_BOOST) ||
                    ( player.isPotionActive(Effects.JUMP_BOOST) && player.getActivePotionEffect(Effects.JUMP_BOOST).getDuration() < 20 );
            if ( jumpFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 20, 1));
            }
        }

    }

    protected void repairItem(ItemStack stack, LivingEntity entityLiving, int lightValue) {
        stack.damageItem((int) ( -1 - .2 * lightValue ), entityLiving, (p_220044_0_) -> {
            p_220044_0_.sendBreakAnimation(this.slot);
        });
    }

}
