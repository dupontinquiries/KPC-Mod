package media.kitchen.parkour.world.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import media.kitchen.parkour.Parkour;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.function.Function;

public class KPCForgeBlob extends ScatteredStructure<NoFeatureConfig> {

    public KPCForgeBlob(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    protected int getSeedModifier() {
        return 165745296;
    }

    @Override
    public IStartFactory getStartFactory() {
        return KPCForgeBlob.Start::new;
    }

    @Override
    public String getStructureName() {
        return Parkour.KPC_FORGE_LOC.toString();
    }

    @Override
    public int getSize() {
        return 1;
    }

    public class Start extends StructureStart {

        public Start(Structure<?> structIn, int int_1, int int_2, MutableBoundingBox mutableBB, int int_3, long long_1) {
            super(structIn, int_1, int_2, mutableBB, int_3, long_1);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            int worldX = chunkX * 16;
            int worldZ = chunkZ * 16;
            BlockPos blockpos = new BlockPos(worldX, 0, worldZ);
            this.components.add(new KPCForgeBlobPiece.Piece(templateManagerIn, Parkour.KPC_FORGE_LOC, blockpos,
                    Rotation.randomRotation(rand), 1 + rand.nextInt( 6)));
            this.recalculateStructureSize();
        }

    }

}