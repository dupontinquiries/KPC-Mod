package media.kitchen.parkour.world.ore;

import media.kitchen.parkour.Parkour;
import media.kitchen.parkour.world.structure.ForgePiece;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class RubyOreGen {
    public static void generateOre() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            if ( biome.getPrecipitation() != Biome.RainType.SNOW ) {
                ConfiguredPlacement rubyConfig = Placement.COUNT_RANGE.configure(new CountRangeConfig(4, 5, 5, 35));
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                        Feature.ORE.withConfiguration(
                                new OreFeatureConfig( OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                                        Parkour.RUBY_ORE.get().getDefaultState(), 9 ))
                                .withPlacement(rubyConfig));
            }
        }
    }
}
