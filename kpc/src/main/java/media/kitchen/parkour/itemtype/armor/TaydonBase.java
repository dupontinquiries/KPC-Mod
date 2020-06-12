package media.kitchen.parkour.itemtype.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
                taydonEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
            }


        }
    }

    protected String cooldownTag = "kcoo";

    protected int maxCooldown = 80;

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

    protected void slowTaydonEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                                    boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( !solarEffect ) {
            if ( isArmorWorn ) {
                player.heal(1);
            }
            repairItem(stack, player, lightValue);
            player.fallDistance -= 0.005;

            ListNBT list = stack.getEnchantmentTagList();
            int stealth = 0;
            boolean fa = false;
            for(int i = 0; i < list.size() && !fa; ++i) {
                CompoundNBT element = list.getCompound(i);
                if (element.getString("id").toLowerCase().contains("stealth")) {
                    stealth = element.getInt("lvl");
                    fa = true;
                }
            }
            if ( fa ) {
                if ( player.getAttackingEntity() != null ) {
                    boolean strengthFlag = !player.isPotionActive(Effects.STRENGTH) ||
                            ( player.isPotionActive(Effects.STRENGTH) && player.getActivePotionEffect(Effects.STRENGTH).getDuration() < 20 );
                    if ( strengthFlag ) {
                        player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 60, stealth));
                    }
                }
            }
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
        setNBTInt(stack, cooldownTag, cooldown);
    }

    protected void applyPotions(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                               boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( isArmorWorn && ( !solarEffect ) ) {
            /*
            boolean resisFlag = !player.isPotionActive(Effects.RESISTANCE) ||
                    ( player.isPotionActive(Effects.RESISTANCE) && player.getActivePotionEffect(Effects.RESISTANCE).getDuration() < 20 );
            if ( resisFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 60, 1));
            }
            */

            boolean speedFlag = !player.isPotionActive(Effects.SPEED) ||
                    ( player.isPotionActive(Effects.SPEED) && player.getActivePotionEffect(Effects.SPEED).getDuration() < 20 );
            if ( speedFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 60, 1));
            }

            boolean jumpFlag = !player.isPotionActive(Effects.JUMP_BOOST) ||
                    ( player.isPotionActive(Effects.JUMP_BOOST) && player.getActivePotionEffect(Effects.JUMP_BOOST).getDuration() < 20 );
            if ( jumpFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 60, 1));
            }
        }

    }

    protected void repairItem(ItemStack stack, LivingEntity entityLiving, int lightValue) {
        stack.damageItem((int) ( -1 - .2 * lightValue ), entityLiving, (p_220044_0_) -> {
            p_220044_0_.sendBreakAnimation(this.slot);
        });
    }

}
