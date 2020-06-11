package media.kitchen.parkour.blocktype.tileentity;

import media.kitchen.parkour.Parkour;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Random;

public class ChargableTile extends TileEntity implements ITickableTileEntity {

    protected int x, y, z, counter, lmin, lmax;
    protected BlockState blockStateIn;

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getZ() {
        return z;
    }
    public int getLMin() {
        return lmin;
    }
    public int getLMax() {
        return lmax;
    }

    public int getCounter() {
        return counter;
    }

    public ChargableTile() {
        super(Parkour.CHARGABLE_TE.get());
        this.blockStateIn = Parkour.CHARGED_RUBY_BLOCK.get().getDefaultState();
        this.lmin = 3;
        this.lmax = 12;
    }

    public ChargableTile(BlockState blockStateIn, int lmin, int lmax) {
        super(Parkour.CHARGABLE_TE.get());
        this.blockStateIn = blockStateIn;
        this.lmin = lmin;
        this.lmax = lmax;
    }

    @Override
    public void tick() {
        this.updateCharge( getBlockState(), world, pos, world.rand );
    }

    protected void updateCharge(BlockState state, World world, BlockPos pos, Random rand) {
        //System.out.println("updateCharge");
        boolean seeSky = world.canBlockSeeSky(pos);
        int lightValue = world.getLightFor(LightType.SKY, pos);
        boolean isDay = world.isDaytime();
        System.out.println(" isDay = " + isDay);
        System.out.println(" skyLight = " + world.getSkylightSubtracted());

        if (lmin <= lightValue && lightValue <= lmax) {
            if ( ++counter > 400) {
                world.setBlockState(pos, blockStateIn);
            }
        }

        markDirty();

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("initvalues", NBTHelper.toNBT(this));
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        CompoundNBT initValues = compound.getCompound("initvalues");
        if (initValues != null) {

            this.x = initValues.getInt("x");
            this.y = initValues.getInt("y");
            this.z = initValues.getInt("z");
            this.lmin = initValues.getInt("lmin");
            this.lmax = initValues.getInt("lmax");
            this.counter = initValues.getInt("counter");
            return;
        }
        postRead();
    }

    private void postRead() {
    }

}
