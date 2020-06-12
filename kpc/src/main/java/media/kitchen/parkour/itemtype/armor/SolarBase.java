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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SolarBase extends ArmorBase {

    public SolarBase(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
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
                solarEffect(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
            }


        }

    }

    protected String cooldownTag = "kcooldown";

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

    protected void slowSolarEffect(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                               boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {
        if ( solarEffect ) {
            if ( isArmorWorn ) {
                player.heal(1);
            }
            repairItem(stack, player, lightValue);

            ListNBT list = stack.getEnchantmentTagList();
            int blessing = 0;
            boolean fa = false;
            for(int i = 0; i < list.size() && !fa; ++i) {
                CompoundNBT element = list.getCompound(i);
                if (element.getString("id").toLowerCase().contains("sun_blessing")) {
                    blessing = element.getInt("lvl");
                    fa = true;
                }
            }
            if ( fa ) {
                ItemStack handStack = player.getHeldItem(Hand.MAIN_HAND);
                if (handStack.isRepairable()) {
                    repairItem( handStack, player, -1 * (lightValue + blessing * 2) / 3 );
                }
                ItemStack chest = player.inventory.armorItemInSlot(EquipmentSlotType.CHEST.getIndex());
                if (chest.isRepairable()) {
                    repairItem( chest, player, -1 * (lightValue + blessing * 2) / 3 );
                }
            }

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
        if (solarEffect) {
            // float
            if ( itemSlot == EquipmentSlotType.FEET.getIndex() && isArmorWorn
                    && worldIn.getFluidState(player.getPosition()).isEmpty() && !player.isSwimming()
                    && !player.abilities.isFlying && player.getTicksElytraFlying() < 30) {
                player.addVelocity(0, .03 + (lightValue * .001625), 0);
                if (player.fallDistance > 1) player.fallDistance -= 0.4;
            }
            // !float

            // potions
            applyPotions(stack, worldIn, player, itemSlot, isSelected, seeSky, lightValue, solarEffect, isArmorWorn);
            // !potions

        }

        setNBTInt(stack, cooldownTag, cooldown);

    }

    protected void applyPotions(ItemStack stack, World worldIn, PlayerEntity player, int itemSlot, boolean isSelected,
                               boolean seeSky, int lightValue, boolean solarEffect, boolean isArmorWorn) {


        if ( isArmorWorn && ( worldIn.isDaytime() || solarEffect ) ) {
            boolean regenFlag = !player.isPotionActive(Effects.REGENERATION) ||
                    ( player.isPotionActive(Effects.REGENERATION) && player.getActivePotionEffect(Effects.REGENERATION).getDuration() < 20 );
            if ( regenFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 60, 1));
            }

            boolean strengthFlag = !player.isPotionActive(Effects.STRENGTH) ||
                    ( player.isPotionActive(Effects.STRENGTH) && player.getActivePotionEffect(Effects.STRENGTH).getDuration() < 20 );
            if ( strengthFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 60, 1));
            }

            boolean hasteFlag = !player.isPotionActive(Effects.HASTE) ||
                    ( player.isPotionActive(Effects.HASTE) && player.getActivePotionEffect(Effects.HASTE).getDuration() < 20 );
            if ( hasteFlag ) {
                player.addPotionEffect(new EffectInstance(Effects.HASTE, 60, 1));
            }
        }

    }

    protected void repairItem(ItemStack stack, LivingEntity entityLiving, int lightValue) {
        stack.damageItem((int) ( -1 - .2 * ( 15 - lightValue ) ), entityLiving, (p_220044_0_) -> {
            p_220044_0_.sendBreakAnimation(this.slot);
        });
    }

}
