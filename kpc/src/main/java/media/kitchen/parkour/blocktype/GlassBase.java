package media.kitchen.parkour.blocktype;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class GlassBase extends GlassBlock {

    public GlassBase(Block.Properties props) {
        super(props);
        RenderTypeLookup.setRenderLayer(this, RenderType.getCutout());
    }

}
