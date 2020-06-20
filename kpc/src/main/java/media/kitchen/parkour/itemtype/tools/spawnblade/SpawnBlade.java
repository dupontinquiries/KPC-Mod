package media.kitchen.parkour.itemtype.tools.spawnblade;

import media.kitchen.parkour.Parkour;
import media.kitchen.parkour.itemtype.nbthandles.ItemData;
import media.kitchen.parkour.itemtype.parkour.AmbigSoundType;
import media.kitchen.parkour.itemtype.tools.SwordBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class SpawnBlade<E extends CreatureEntity> extends SwordBase {

    protected EntityType eType = null;
    protected ArrayList<E> activeUnits = new ArrayList<>();

    protected AmbigSoundType callSound = new AmbigSoundType(Parkour.SWORD_ATTACK);
    protected final String
            COOLDOWN_TAG = "spawncooltag",
            LIFE_TAG     = "spawnlifetimetag",
            ID_TAG       = "summonTarget";

    protected int
            maxLife = 700,
            maxCool = 300;

    private int maxUnits = 1;

    public SpawnBlade(IItemTier tier) {
        super(tier);
    }

    public SpawnBlade(IItemTier tier, Item.Properties props) {
        super(tier, props);
    }

    public SpawnBlade(IItemTier tier, int attackDamageIn, float attackSpeedIn, Item.Properties props) {
        super(tier, attackDamageIn, attackSpeedIn, props);
    }

    public SpawnBlade(IItemTier tier, int attackDamageIn, float attackSpeedIn, Item.Properties props, EntityType eType) {
        super(tier, attackDamageIn, attackSpeedIn, props);
        this.eType = eType;
    }

    public SpawnBlade(IItemTier tier, int attackDamageIn, float attackSpeedIn, Item.Properties props, EntityType eType, int maxLife, int maxCool) {
        this(tier, attackDamageIn, attackSpeedIn, props, eType);
        this.maxCool = maxCool;
        this.maxLife = maxLife;
    }

    public SpawnBlade(IItemTier tier, int attackDamageIn, float attackSpeedIn, Item.Properties props, EntityType eType, int maxLife, int maxCool, int maxUnits) {
        this(tier, attackDamageIn, attackSpeedIn, props, eType);
        this.maxCool = maxCool;
        this.maxLife = maxLife;
        this.maxUnits = maxUnits;
    }

    public SpawnBlade setCount(int n) {
        this.maxUnits = n;
        return this;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        //
        int cool = ItemData.getNBTInt(stack, this.COOLDOWN_TAG),
            life = ItemData.getNBTInt(stack,     this.LIFE_TAG);

        if ( cool == 0 && eType != null ) {
            attacker.world.playSound(null, attacker.getPosition(), callSound.getSound(), SoundCategory.AMBIENT, 0.8F, 1F );
            cool = this.maxCool;
            life = maxLife;
            for ( int g = 0; g < maxUnits - this.activeUnits.size(); ++g ) {
                Entity entity = eType.create(attacker.world);
                if ( entity instanceof CreatureEntity) {
                    E l = (E) entity;
                    l.setHealth(l.getMaxHealth());
                    l.clearActivePotions();
                    l.setPosition(attacker.getPosX() + random.nextInt(2)  - 1, attacker.getPosY(), attacker.getPosZ() + random.nextInt(2)  - 1);

                    //l.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier());

                    NearestExcludingCaster<LivingEntity, LivingEntity> goal = new NearestExcludingCaster<>(l, LivingEntity.class,
                            10, true, false, this::shouldSummonedAttack);
                    goal.setCaster(attacker);

                    l.targetSelector.addGoal(0, goal);

                    if ( !attacker.world.isRemote() ) {
                        attacker.world.addEntity(l);
                    }
                    //l.setLastAttackedEntity(target);
                    l.setRevengeTarget(target);
                    activeUnits.add(l);
                }
            }
            if ( target != null && target instanceof LivingEntity ) {
                ItemData.setNBTInt(stack, ID_TAG, target.getEntityId());
            }
        }

        ItemData.setNBTInt(stack, this.COOLDOWN_TAG, cool);
        ItemData.setNBTInt(stack, this.LIFE_TAG, life);
        //
        return super.hitEntity(stack, target, attacker);
    }

    public boolean shouldSummonedAttack(@Nullable LivingEntity living) {
        if (living != null) {
            for ( E li : activeUnits ) {
                if ( living.getUniqueID() == li.getUniqueID() ) return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        int cool = ItemData.getNBTInt(stack, this.COOLDOWN_TAG),
            life = ItemData.getNBTInt(stack,     this.LIFE_TAG);

        //UUID targetId = ItemData.getUUID(stack, this.idTag);
        int entityId = ItemData.getNBTInt(stack, ID_TAG);

        // get target
        LivingEntity li = null;
        Entity en = null;
        if ( entityId != -1 ) {
            en = worldIn.getEntityByID(entityId);
        }
        if ( en != null && en instanceof LivingEntity ) {
            li = (LivingEntity) en;
            if ( !li.isAlive() ) {
                li = null;
            }
        }
        if ( li != null && li.getUniqueID() == entityIn.getUniqueID() ) {
            li = null;
        }

        if ( !activeUnits.isEmpty() ) {
            --life;
            if ( life <= 0 ) {
                for ( E l : activeUnits ) {
                    l.remove();
                }
                activeUnits.clear();
            } else {
                for ( int g = 0; g < activeUnits.size(); ++g ) {
                    E l = activeUnits.get(g);
                    // set attack target

                    if ( l.getRevengeTarget() != li ) {
                        l.setRevengeTarget(li);
                    }
                }
            }
            ItemData.setNBTInt(stack, this.LIFE_TAG, life);
        } else {
            if ( cool == -1 ) {
                cool = maxCool;
            } else if ( cool > 0 ) {
                --cool;
            }
            ItemData.setNBTInt(stack, this.COOLDOWN_TAG, cool);
        }
    }

}
