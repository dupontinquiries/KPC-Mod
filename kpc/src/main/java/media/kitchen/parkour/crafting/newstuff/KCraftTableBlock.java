package media.kitchen.parkour.crafting.newstuff;

import media.kitchen.parkour.crafting.kpctable.KPCContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class KCraftTableBlock extends Block {
    private static final ITextComponent field_220271_a = new TranslationTextComponent("container.kpccrafting");

    protected KCraftTableBlock(Block.Properties properties) {
        super(properties);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(state.getContainer(worldIn, pos));
            player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return ActionResultType.SUCCESS;
        }
    }

    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((p_220270_2_, p_220270_3_, p_220270_4_) -> {
            return new KPCContainer(p_220270_2_, p_220270_3_, IWorldPosCallable.of(worldIn, pos));
        }, field_220271_a);
    }
}