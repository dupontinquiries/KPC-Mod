package media.kitchen.parkour.blocktype;

import media.kitchen.parkour.blocktype.tileentity.ChargableTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class ChargableBlockSunlight extends BlockBase implements ITileEntityProvider {

    protected int lmin, lmax;

    protected BlockState blockStateIn = Blocks.DIRT.getDefaultState();

    //public static final IntegerProperty CHARGE_COUNT = IntegerProperty.create("CHARGE_COUNT", 0, 100);

    public ChargableBlockSunlight(Properties props) {
        super(props);
    }

    public ChargableBlockSunlight(Properties props, BlockState blockStateIn) {
        this(props);
        this.blockStateIn = blockStateIn;
    }

    public ChargableBlockSunlight(Properties props, BlockState blockStateIn, int lmin, int lmax) {
        this(props, blockStateIn);
        this.lmin = lmin;
        this.lmax = lmax;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        TileEntity tileentity = new ChargableTile(blockStateIn, lmin, lmax);
        return tileentity;
    }

    /*
    @Override
    protected void updateCharge(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        BlockState bs = world.getBlockState(pos);
        int curr = world.getLight(pos), val = bs.get(CHARGE_COUNT);
        if ( val < 100 && curr > lmin && curr < lmax ) {
            world.setBlockState(pos, bs.with(CHARGE_COUNT, val + 1));
        }
        System.out.println(val);
    }
    */

}
