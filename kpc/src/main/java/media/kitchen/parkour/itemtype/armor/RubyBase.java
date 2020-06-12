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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RubyBase extends ArmorBase {
    public RubyBase(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

        BlockPos bp = new BlockPos(entityIn);
        boolean seeSky = worldIn.canBlockSeeSky(bp);
        if (worldIn.dimension.hasSkyLight()) {
            int lightValue = worldIn.getLightFor(LightType.SKY, bp) - worldIn.getSkylightSubtracted();
            float f = worldIn.getCelestialAngleRadians(1.0F);
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            lightValue = Math.round((float)lightValue * MathHelper.cos(f));

            lightValue = MathHelper.clamp(lightValue, 0, 15);

            lightValue += worldIn.getLightFor(LightType.BLOCK, bp) / 2;


            boolean isDay = worldIn.isDaytime();
            boolean living = entityIn instanceof LivingEntity;
            LivingEntity livingEntity = null;
            if ( living ) {
                livingEntity = (LivingEntity) entityIn;
            }
            boolean solarEffect = ( lightValue > 6 );

            if ( livingEntity instanceof PlayerEntity ) {
                PlayerEntity player = (PlayerEntity) livingEntity;
                boolean isArmorWorn = player.inventory.armorItemInSlot(0) == stack ||
                        player.inventory.armorItemInSlot(1) == stack ||
                        player.inventory.armorItemInSlot(2) == stack ||
                        player.inventory.armorItemInSlot(3) == stack;
                rubyEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
            }


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

    protected void setNBTInt(ItemStack stack, final String query, final int value) {
        CompoundNBT tags = stack.getOrCreateTag();

        tags.putInt(query, value);

        stack.setTag(tags);
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
        setNBTInt(stack, cooldownTag, cooldown);
    }

    protected void applyPotions(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                                boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( isArmorWorn && player.getHealth() < 7 ) {
            boolean speedFlag = !player.isPotionActive(Effects.SPEED) ||
                    ( player.isPotionActive(Effects.SPEED) && player.getActivePotionEffect(Effects.SPEED).getDuration() < 20 );
            if ( speedFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 20, 1));
            }
        }

    }

}
