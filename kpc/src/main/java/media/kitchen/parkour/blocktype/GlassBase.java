package media.kitchen.parkour.blocktype;

import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class GlassBase extends GlassBlock {

    public GlassBase(Block.Properties props) {
        super(props);
        RenderTypeLookup.setRenderLayer(this, RenderType.getCutout());
    }

}
