package media.kitchen.parkour.crafting.kpctable;

import media.kitchen.parkour.crafting.newstuff.KWorkBenchCont;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class KPCTable extends Block {

    public KPCTable(Block.Properties props) {
        super(props);
    }

    /**
     * Looks like this is called when fecthing the crafting interface
     * @param state
     * @param worldIn
     * @param pos
     * @return
     */
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((a, p_220270_3_, p_220270_4_) -> {
            return new KPCContainer(a, p_220270_3_, IWorldPosCallable.of(worldIn, pos));
        }, label);
    }
}
