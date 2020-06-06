package media.kitchen.parkour.entity;

import media.kitchen.parkour.itemtype.tools.supertrident.SuperTridentGoal;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class SuperDrowned extends DrownedEntity {
    public SuperDrowned(EntityType<? extends DrownedEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void applyEntityAI() {
        this.goalSelector.addGoal(2, new SuperTridentGoal(this, 1.0D, 40, 10.0F));
        super.applyEntityAI();
    }

}
