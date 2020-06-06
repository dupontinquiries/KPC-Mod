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
import net.minecraft.world.World;

public class RubyBase extends ArmorBase {
    public RubyBase(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }


    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        BlockPos bp = new BlockPos(entityIn);
        boolean seeSky = worldIn.canBlockSeeSky(bp);
        int lightValue = worldIn.getLightValue(bp);
        boolean living = entityIn instanceof LivingEntity;
        LivingEntity livingEntity = null;
        if ( living ) {
            livingEntity = (LivingEntity) entityIn;
        }
        boolean solarEffect = seeSky
                && !worldIn.isRaining() && !worldIn.isThundering()
                && lightValue > 3;

        if ( livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            boolean isArmorWorn = player.inventory.armorItemInSlot(0) == stack ||
                    player.inventory.armorItemInSlot(1) == stack ||
                    player.inventory.armorItemInSlot(2) == stack ||
                    player.inventory.armorItemInSlot(3) == stack;
            rubyEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
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

    protected void slowRubyEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                                  boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( player.getMaxHealth() - player.getHealth() < 5 ) {
            player.heal(3);
        }
    }

    protected void rubyEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                              boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        // timer
        int cooldown = getNBTInt(stack, cooldownTag);
        if (cooldown == -1) cooldown = maxCooldown;
        if (cooldown > 0) {
            --cooldown;
        } else {
            cooldown = maxCooldown;
            slowRubyEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
        }
        // !timer
        // potions
        applyPotions(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
        // !potions
    }

    protected void applyPotions(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                                boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( isArmorWorn && player.getMaxHealth() - player.getHealth() > 6 ) {
            boolean speedFlag = !player.isPotionActive(Effects.SPEED) ||
                    ( player.isPotionActive(Effects.SPEED) && player.getActivePotionEffect(Effects.SPEED).getDuration() < 20 );
            if ( speedFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 20, 1));
            }
        }

    }

}
