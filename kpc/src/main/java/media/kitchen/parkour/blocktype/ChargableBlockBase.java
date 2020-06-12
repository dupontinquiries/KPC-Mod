package media.kitchen.parkour.blocktype;

import media.kitchen.parkour.blocktype.tileentity.ChargableTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ChargableBlockBase extends BlockBase implements ITileEntityProvider {

    protected Block blockIn = Blocks.DIRT;
    public ChargableBlockBase(Properties props) {
        super(props);
    }
    public ChargableBlockBase(Properties props, Block blockIn) {
        this(props);
        this.blockIn = blockIn;
    }

    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = new ChargableTile();//worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }

}