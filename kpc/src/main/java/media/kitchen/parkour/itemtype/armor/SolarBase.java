package media.kitchen.parkour.itemtype.armor;

import media.kitchen.parkour.Parkour;
import media.kitchen.parkour.itemtype.parkour.AmbigSoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SolarBase extends ArmorBase {

    public SolarBase(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        BlockPos bp = new BlockPos(entityIn);
        boolean seeSky = worldIn.canBlockSeeSky(bp);
        int lightValue = worldIn.getLightFor(LightType.SKY, bp);
        boolean isDay = worldIn.isDaytime();
        System.out.println(" isDay = " + isDay);
        System.out.println(" skyLight = " + worldIn.getSkylightSubtracted());
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
            solarEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
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

    protected void slowSolarEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                               boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( solarEffect ) {
            if ( isArmorWorn ) {
                player.heal(3);
            }
            repairItem(stack, player, lightValue);
        }
    }

    protected void solarEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                               boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        // timer
        int cooldown = getNBTInt(stack, cooldownTag);
        if (cooldown == -1) cooldown = maxCooldown;
        if (cooldown > 0) {
            --cooldown;
        } else {
            cooldown = maxCooldown;
            slowSolarEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
        }
        // !timer
        // potions
        applyPotions(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
        // !potions
    }

    protected void applyPotions(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                               boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( isArmorWorn && ( worldIn.isDaytime() || solarEffect ) ) {
            boolean regenFlag = !player.isPotionActive(Effects.REGENERATION) ||
                    ( player.isPotionActive(Effects.REGENERATION) && player.getActivePotionEffect(Effects.REGENERATION).getDuration() < 20 );
            if ( regenFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 20, 1));
            }

            boolean strengthFlag = !player.isPotionActive(Effects.STRENGTH) ||
                    ( player.isPotionActive(Effects.STRENGTH) && player.getActivePotionEffect(Effects.STRENGTH).getDuration() < 20 );
            if ( strengthFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 20, 1));
            }

            boolean hasteFlag = !player.isPotionActive(Effects.HASTE) ||
                    ( player.isPotionActive(Effects.HASTE) && player.getActivePotionEffect(Effects.HASTE).getDuration() < 20 );
            if ( hasteFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.HASTE, 20, 1));
            }
        }

    }

    protected void repairItem(ItemStack stack, LivingEntity entityLiving, int lightValue) {
        stack.damageItem((int) ( -1 - .2 * lightValue ), entityLiving, (p_220044_0_) -> {
            p_220044_0_.sendBreakAnimation(this.slot);
        });
    }

}
