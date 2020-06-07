package media.kitchen.parkour;

import media.kitchen.parkour.blocktype.BlockBase;
import media.kitchen.parkour.blocktype.GlassBase;
import media.kitchen.parkour.blocktype.SauberiteBlock;
import media.kitchen.parkour.crafting.KPCShapedRecipe;
import media.kitchen.parkour.crafting.KShapelessRecipe;
import media.kitchen.parkour.crafting.kpctable.KPCTable;
import media.kitchen.parkour.itemtype.*;
import media.kitchen.parkour.itemtype.armor.*;
import media.kitchen.parkour.itemtype.parkour.AquaParkour;
import media.kitchen.parkour.itemtype.parkour.ExplosiveParkour;
import media.kitchen.parkour.itemtype.parkour.ParkourBase;
import media.kitchen.parkour.itemtype.parkour.TeleParkour;
import media.kitchen.parkour.itemtype.quest.QuestHitChargeBase;
import media.kitchen.parkour.itemtype.token.TokenBase;
import media.kitchen.parkour.itemtype.token.TokenType;
import media.kitchen.parkour.itemtype.tools.AreaPickaxeBase;
import media.kitchen.parkour.itemtype.tools.supertrident.SuperTrident;
import media.kitchen.parkour.itemtype.tools.SwordBase;
import media.kitchen.parkour.world.structure.ForgeStructure;
import media.kitchen.parkour.world.structure.KPCForgeBlob;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Parkour.MOD_ID)
public class Parkour
{

    public static final float QUEST_MODIFIER = 12F;

    public static final ItemGroup KPC_TAB = new ItemGroup("kpc_tab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Parkour.BLUE_FINS.get());
        }
    };

    public static final ItemGroup KPC_ARMOR = new ItemGroup("kpc_armor") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Parkour.SOLAR_HELM.get());
        }
    };

    public static final ItemGroup KPC_TOKENS = new ItemGroup("kpc_tokens") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Parkour.TOKEN_TANK.get());
        }
    };

    static final class ClientConfig {

        final ForgeConfigSpec.BooleanValue clientBoolean;
        final ForgeConfigSpec.ConfigValue<List<String>> clientStringList;
        final ForgeConfigSpec.EnumValue<DyeColor> clientDyeColorEnum;

        final ForgeConfigSpec.BooleanValue modelTranslucency;
        final ForgeConfigSpec.DoubleValue modelScale;

        ClientConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("general");
            clientBoolean = builder
                    .comment("An example boolean in the client config")
                    .translation(Parkour.MOD_ID + ".config.clientBoolean")
                    .define("clientBoolean", true);
            clientStringList = builder
                    .comment("An example list of Strings in the client config")
                    .translation(Parkour.MOD_ID + ".config.clientStringList")
                    .define("clientStringList", new ArrayList<>());
            clientDyeColorEnum = builder
                    .comment("An example DyeColor enum in the client config")
                    .translation(Parkour.MOD_ID + ".config.clientDyeColorEnum")
                    .defineEnum("clientDyeColorEnum", DyeColor.WHITE);

            modelTranslucency = builder
                    .comment("If the model should be rendered translucent")
                    .translation(Parkour.MOD_ID + ".config.modelTranslucency")
                    .define("modelTranslucency", true);
            modelScale = builder
                    .comment("The scale to render the model at")
                    .translation(Parkour.MOD_ID + ".config.modelScale")
                    .defineInRange("modelScale", 0.0625F, 0.0001F, 100F);
            builder.pop();
        }

        /*
        ClientConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("general");
            clientBoolean = builder
                    .comment("An example boolean in the client config")
                    .translation(Parkour.MODID + ".config.clientBoolean")
                    .define("clientBoolean", true);
            clientStringList = builder
                    .comment("An example list of Strings in the client config")
                    .translation(Parkour.MODID + ".config.clientStringList")
                    .define("clientStringList", new ArrayList<>());
            clientDyeColorEnum = builder
                    .comment("An example DyeColor enum in the client config")
                    .translation(Parkour.MODID + ".config.clientDyeColorEnum")
                    .defineEnum("clientDyeColorEnum", DyeColor.WHITE);

            modelTranslucency = builder
                    .comment("If the model should be rendered translucent")
                    .translation(Parkour.MODID + ".config.modelTranslucency")
                    .define("modelTranslucency", true);
            modelScale = builder
                    .comment("The scale to render the model at")
                    .translation(Parkour.MODID + ".config.modelScale")
                    .defineInRange("modelScale", 0.0625F, 0.0001F, 100F);
            builder.pop();
        }
        */

    }
    public static final String MOD_ID = "kitchenparkour";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    //

    //

    // Registries

    //

    //

    // Register Blocks
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Parkour.MOD_ID);
    // Custom Blocks

    // Working Example
    public static final RegistryObject<Block> CUSTOM_BLOCK = BLOCKS.register("custom_block",
            () -> new Block(Block.Properties.create(Material.IRON, MaterialColor.IRON)
                    .hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));

    // Ruby Blocks
    public static final RegistryObject<Block> RUBY_ORE = BLOCKS.register("ruby_ore",
            () -> new BlockBase(Block.Properties.create(Material.IRON, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(5.0F, 7.0F)
                    .sound(SoundType.STONE)
                    .harvestLevel(3).harvestTool(ToolType.PICKAXE))); // 0-3 = vanilla, 4 & 5 = upgrades to ruby pick

    public static final RegistryObject<Block> RUBY_BLOCK = BLOCKS.register("ruby_block",
            () -> new BlockBase(Block.Properties.create(Material.IRON, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(6.0F, 13.0F)
                    .sound(SoundType.METAL)
                    .harvestLevel(3).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Block> CHARGED_RUBY_BLOCK = BLOCKS.register("charged_ruby_block",
            () -> new BlockBase(Block.Properties.create(Material.IRON, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(7.0F, 16.0F)
                    .sound(SoundType.GLASS)
                    .harvestLevel(3).harvestTool(ToolType.PICKAXE)));

    // Taydon Blocks
    public static final RegistryObject<Block> TAYDON_ORE = BLOCKS.register("taydon_ore",
            () -> new BlockBase(Block.Properties.create(Material.IRON, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(6.0F, 8.0F)
                    .sound(SoundType.STONE)
                    .harvestLevel(4).harvestTool(ToolType.PICKAXE))); // 0-3 = vanilla, 4 & 5 = upgrades to ruby pick

    public static final RegistryObject<Block> TAYDON_BLOCK = BLOCKS.register("taydon_block",
            () -> new BlockBase(Block.Properties.create(Material.IRON, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(7.0F, 14.0F)
                    .sound(SoundType.METAL)
                    .harvestLevel(4).harvestTool(ToolType.PICKAXE)));

    public static final RegistryObject<Block> CHARGED_TAYDON_BLOCK = BLOCKS.register("charged_taydon_block",
            () -> new BlockBase(Block.Properties.create(Material.IRON, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(8.0F, 17.0F)
                    .sound(SoundType.GLASS)
                    .harvestLevel(4).harvestTool(ToolType.PICKAXE)));

    // Crafting Table
    public static final RegistryObject<Block> KPC_TABLE = BLOCKS.register("kpc_table",
            () -> new KPCTable(Block.Properties.from(Blocks.OBSIDIAN)));

    // Sauberite Block
    public static final RegistryObject<Block> SAUBERITE_BLOCK = BLOCKS.register("sauberite_block",
            () -> new SauberiteBlock(Block.Properties.from(Blocks.BEDROCK)));

    // Oberite Block
    public static final RegistryObject<Block> OBERITE_BLOCK = BLOCKS.register("oberite_block",
            () -> new BlockBase(Block.Properties.from(Blocks.OBSIDIAN)));

    // Hard Glass Block
    public static final RegistryObject<Block> HARD_GLASS = BLOCKS.register("hard_glass",
            () -> new GlassBase(Block.Properties.create(Material.GLASS).hardnessAndResistance(-1.0F, 3600000.0F).sound(SoundType.GLASS).notSolid()));//GlassBase(Block.Properties.create(Material.GLASS).notSolid().hardnessAndResistance(-1.0F, 3600000.0F))); Block sand = Blocks.GLASS;

    /*
    public static final RegistryObject<Block> KPCT = BLOCKS.register("kpct",
            () -> new KPCT(Block.Properties.create(Material.IRON, MaterialColor.OBSIDIAN)
                    .hardnessAndResistance(5.0F, 16.0F).sound(SoundType.ANVIL)));
     */

    // !Custom Blocks

    //

    //

    // Register Items
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Parkour.MOD_ID);
    // Custom Items

    // Crafting
    public static final RegistryObject<Item> KPC_TABLE_ITEM = ITEMS.register("kpc_table",
            () -> new ItemBlockBase(KPC_TABLE.get()));

    // Ruby
    public static final RegistryObject<Item> RUBY_ORE_ITEM = ITEMS.register("ruby_ore",
            () -> new ItemBlockBase(RUBY_ORE.get()));

    public static final RegistryObject<Item> RUBY_BLOCK_ITEM = ITEMS.register("ruby_block",
            () -> new ItemBlockBase(RUBY_BLOCK.get()));

    public static final RegistryObject<Item> CHARGED_RUBY_BLOCK_ITEM = ITEMS.register("charged_ruby_block",
            () -> new ItemBlockBase(CHARGED_RUBY_BLOCK.get()));

    // Taydon
    public static final RegistryObject<Item> TAYDON_ORE_ITEM = ITEMS.register("taydon_ore",
            () -> new ItemBlockBase(TAYDON_ORE.get()));

    public static final RegistryObject<Item> TAYDON_BLOCK_ITEM = ITEMS.register("taydon_block",
            () -> new ItemBlockBase(TAYDON_BLOCK.get()));

    public static final RegistryObject<Item> CHARGED_TAYDON_BLOCK_ITEM = ITEMS.register("charged_taydon_block",
            () -> new ItemBlockBase(CHARGED_TAYDON_BLOCK.get()));

    // Sauberite
    public static final RegistryObject<Item> SAUBERITE_BLOCK_ITEM = ITEMS.register("sauberite_block",
            () -> new ItemBlockBase(SAUBERITE_BLOCK.get()));

    public static final RegistryObject<Item> OBERITE_BLOCK_ITEM = ITEMS.register("oberite_block",
            () -> new ItemBlockBase(OBERITE_BLOCK.get()));

    public static final RegistryObject<Item> HARD_GLASS_ITEM = ITEMS.register("hard_glass",
            () -> new ItemBlockBase(HARD_GLASS.get()));

    public static final RegistryObject<Item> RUBY_POWDER = ITEMS.register("ruby_powder",
            () -> new MineralBase(new Item.Properties().maxStackSize(64)));

    public static final RegistryObject<Item> RUBY_GEM = ITEMS.register("ruby_gem",
            () -> new MineralBase(new Item.Properties().maxStackSize(64)));

    public static final RegistryObject<Item> CHARGED_RUBY_GEM = ITEMS.register("charged_ruby_gem",
            () -> new MineralBase(new Item.Properties().maxStackSize(9)));


    public static final RegistryObject<Item> CHARGED_TAYDON_GEM = ITEMS.register("charged_taydon_gem",
            () -> new MineralBase(new Item.Properties().maxStackSize(9)));

    public static final RegistryObject<Item> TAYDON_GEM = ITEMS.register("taydon_gem",
            () -> new QuestHitChargeBase<>(CHARGED_TAYDON_GEM.get(), 0.1F));

    // Ruby Tools
    public static final RegistryObject<Item> RUBY_PICK = ITEMS.register("ruby_pick",
            () -> AreaPickaxeBase.setSize( new AreaPickaxeBase(ItemTier.DIAMOND,
                            7, 1, new Item.Properties()
                            .addToolType(ToolType.PICKAXE, 4).maxStackSize(1).maxDamage(224000)),
                    3 ) );

    public static final RegistryObject<Item> UNLIT_SPARK = ITEMS.register("unlit_spark",
            () -> new QuestHitChargeBase(RUBY_PICK.get(), 0.2F));
    // Solar Tools
    public static final RegistryObject<Item> SOLAR_PICK = ITEMS.register("solar_pick",
            () -> AreaPickaxeBase.setSize( new AreaPickaxeBase(ItemTier.DIAMOND,
                        7, 1, new Item.Properties()
                        .addToolType(ToolType.PICKAXE, 5).maxStackSize(1).maxDamage(57500)),
                    6 ) );
    public static final RegistryObject<Item> UNLIT_STAR = ITEMS.register("unlit_star",
            () -> new QuestHitChargeBase(SOLAR_PICK.get(), 0.3F));

    // Parkour Grippers
    public static final RegistryObject<Item> PARKOUR_GRIPPER = ITEMS.register("parkour_gripper",
            () -> SwordBase.setAttackAndSpeed(new ParkourBase(0.7D),8, 0.85F)
                    .setCooldownTime(25).setWallLeapTime(10).setChargeTime(20));
    public static final RegistryObject<Item> EAGER_ARTIFACT = ITEMS.register("eager_artifact",
            () -> new QuestHitChargeBase(PARKOUR_GRIPPER.get(), 0.5F));
    // ultimate parkour gripper
    public static final RegistryObject<Item> ULTIMATE_PARKOUR_GRIPPER = ITEMS.register("ultimate_parkour_gripper",
            () -> SwordBase.setAttackAndSpeed(new ParkourBase(1.3D, 1.1D),12, 0.75F)
                    .setCooldownTime(30).setWallLeapTime(10).setChargeTime(20));
    public static final RegistryObject<Item> HUNGRY_STICK = ITEMS.register("hungry_stick",
            () -> new QuestHitChargeBase(ULTIMATE_PARKOUR_GRIPPER.get(), 0.7F));
    // ultimate parkour gripper v2
    public static final RegistryObject<Item> ULTIMATE_PARKOUR_GRIPPER_V2 = ITEMS.register("ultimate_parkour_gripper_v2",
            () -> SwordBase.setAttackAndSpeed(new ParkourBase(1.42D, 1.2D),16, 0.7F)
                    .setCooldownTime(25).setWallLeapTime(25).setChargeTime(25));
    public static final RegistryObject<Item> RAVENOUS_ROD = ITEMS.register("ravenous_rod",
            () -> new QuestHitChargeBase(ULTIMATE_PARKOUR_GRIPPER_V2.get()));
    // wings of order
    public static final RegistryObject<Item> GODS_WINGS = ITEMS.register("gods_wings",
            () -> SwordBase.setAttackAndSpeed(new ParkourBase(1.89, 0.945D),18, 0.9F)
                    .setCooldownTime(50).setWallLeapTime(45).setChargeTime(25).setElytraFlyer());
    // blue fins
    public static final RegistryObject<Item> BLUE_FINS = ITEMS.register("blue_fins",
            () -> SwordBase.setAttackAndSpeed(new AquaParkour(1.45),13, 0.8F)
                    .setCooldownTime(65).setWallLeapTime(0).setChargeTime(0)); //2.3
    public static final RegistryObject<Item> MYSTICAL_FISH_BONE = ITEMS.register("mystical_fish_bone",
            () -> new QuestHitChargeBase(BLUE_FINS.get()));

    // blue fins
    public static final RegistryObject<Item> ENDER_ROD = ITEMS.register("ender_rod",
            () -> SwordBase.setAttackAndSpeed(new ExplosiveParkour(3.0),13, 0.8F)
                    .setCooldownTime(65).setWallLeapTime(0).setChargeTime(0));

    // super trident
    public static final RegistryObject<Item> SUPER_TRIDENT = ITEMS.register("super_trident",
            () -> new SuperTrident(new Item.Properties().maxStackSize(1).maxDamage(400).group(ItemGroup.COMBAT)));

    // Tokens of Valor
    public static final RegistryObject<Item> TOKEN_WARRIOR = ITEMS.register("token_warrior",
            () -> new TokenBase(20, TokenType.WARRIOR));
    //.withTradeoff(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, 0.05D)

    public static final RegistryObject<Item> TOKEN_TANK = ITEMS.register("token_tank",
            () -> new TokenBase(20, TokenType.TANK));
    //.withTradeoff(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, 0.05D)

    public static final RegistryObject<Item> TOKEN_SCOUT = ITEMS.register("token_scout",
            () -> new TokenBase(20, TokenType.SCOUT));
    //.withTradeoff(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, 0.05D)
    // Ruby Armor

    public static RubyMaterial RUBY_MATERIAL = new RubyMaterial();

    public static final RegistryObject<Item> RUBY_HELM = ITEMS.register("ruby_helm",
            () -> new RubyBase(RUBY_MATERIAL,
                    EquipmentSlotType.HEAD,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> RUBY_CHEST = ITEMS.register("ruby_chest",
            () -> new RubyBase(RUBY_MATERIAL,
                    EquipmentSlotType.CHEST,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> RUBY_LEGS = ITEMS.register("ruby_legs",
            () -> new RubyBase(RUBY_MATERIAL,
                    EquipmentSlotType.LEGS,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> RUBY_BOOTS = ITEMS.register("ruby_boots",
            () -> new RubyBase(RUBY_MATERIAL,
                    EquipmentSlotType.FEET,
                    new Item.Properties().maxDamage(200)));

    // Solar Armor

    public static SolarMaterial SOLAR_MATERIAL = new SolarMaterial();

    public static final RegistryObject<Item> SOLAR_HELM = ITEMS.register("solar_helm",
            () -> new SolarBase(SOLAR_MATERIAL,
                    EquipmentSlotType.HEAD,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> SOLAR_CHEST = ITEMS.register("solar_chest",
            () -> new SolarBase(SOLAR_MATERIAL,
                    EquipmentSlotType.CHEST,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> SOLAR_LEGS = ITEMS.register("solar_legs",
            () -> new SolarBase(SOLAR_MATERIAL,
                    EquipmentSlotType.LEGS,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> SOLAR_BOOTS = ITEMS.register("solar_boots",
            () -> new SolarBase(SOLAR_MATERIAL,
                    EquipmentSlotType.FEET,
                    new Item.Properties().maxDamage(200)));

    // Taydon Armor

    public static TaydonMaterial TAYDON_MATERIAL = new TaydonMaterial();

    public static final RegistryObject<Item> TAYDON_HELM = ITEMS.register("taydon_helm",
            () -> new TaydonBase(TAYDON_MATERIAL,
                    EquipmentSlotType.HEAD,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> TAYDON_CHEST = ITEMS.register("taydon_chest",
            () -> new TaydonBase(TAYDON_MATERIAL,
                    EquipmentSlotType.CHEST,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> TAYDON_LEGS = ITEMS.register("taydon_legs",
            () -> new TaydonBase(TAYDON_MATERIAL,
                    EquipmentSlotType.LEGS,
                    new Item.Properties().maxDamage(200)));

    public static final RegistryObject<Item> TAYDON_BOOTS = ITEMS.register("taydon_boots",
            () -> new TaydonBase(TAYDON_MATERIAL,
                    EquipmentSlotType.FEET,
                    new Item.Properties().maxDamage(200)));

    // !Solar Armor

    // !Custom Items

    //

    //

    // Register Sounds
    public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, Parkour.MOD_ID);

    // Custom Sounds
    public static final RegistryObject<SoundEvent> PARKOUR_GRIPPER_JUMP = SOUNDS.register("item.parkour_gripper_jump",
            () -> new SoundEvent(new ResourceLocation(MOD_ID, "item.parkour_gripper_jump")));
    public static final RegistryObject<SoundEvent> PARKOUR_GRIPPER_READY = SOUNDS.register("item.parkour_gripper_ready",
            () -> new SoundEvent(new ResourceLocation(MOD_ID, "item.parkour_gripper_ready")));
    public static final RegistryObject<SoundEvent> SWORD_ATTACK = SOUNDS.register("item.sword_attack",
            () -> new SoundEvent(new ResourceLocation(MOD_ID, "item.sword_attack")));
    public static final RegistryObject<SoundEvent> AQUA_PARKOUR_DASH = SOUNDS.register("item.aqua_parkour_dash",
            () -> new SoundEvent(new ResourceLocation(MOD_ID, "item.aqua_parkour_dash")));

    public static final RegistryObject<SoundEvent> KPC_TABLE_CRAFT = SOUNDS.register("block.kpc_table_craft",
            () -> new SoundEvent(new ResourceLocation(MOD_ID, "block.kpc_table_craft")));
    // !Custom Sounds

    // Recipes

    public static final DeferredRegister<ContainerType<?>> MOD_CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Parkour.MOD_ID);
    //public static final RegistryObject<ContainerType<?>> KPC_CONTAINER = MOD_CONTAINERS.register("kpc_table", () -> new KPCContainer().getType());


    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, Parkour.MOD_ID);

    public static final RegistryObject<KPCShapedRecipe.Serializer> KPC_SHAPED = RECIPE_SERIALIZERS.register("kpc_crafting_shaped",
            () -> new KPCShapedRecipe.Serializer());

    public static final RegistryObject<KShapelessRecipe.Serializer> KPC_SHAPELESS = RECIPE_SERIALIZERS.register("kpc_crafting_shapeless",
            () -> new KShapelessRecipe.Serializer());

    //public static final RegistryObject<ShapedRecipe.Serializer> KPC_SHAPED = RECIPE_TYPES.register("kpc_shaped", () -> new ShapedRecipe.Serializer());

    // !Recipes

    //

    //

    // Enchantments

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = new DeferredRegister<>(ForgeRegistries.ENCHANTMENTS, Parkour.MOD_ID);


    /*
    public static final RegistryObject<KPCShaped.Serializer> SUN_BLESSING = ENCHANTMENTS.register("sun_blessing",
            () -> new SunBlessing(Enchantment.Rarity.UNCOMMON, EnchantmentType.create("carrot", item -> item.equals(Items.IRON_CHESTPLATE))));
     */
    /*
    public static final RegistryObject<Enchantment> SUN_BLESSING = ENCHANTMENTS.register("sun_blessing",
            () -> new SunBlessing(Enchantment.Rarity.COMMON,
                    EnchantmentType.create("solar_enchantment", Predicate.isEqual(SolarBase.class) ),
                    new EquipmentSlotType[] { EquipmentSlotType.LEGS, EquipmentSlotType.CHEST } ));
     */
    //working single player
    /*
    public static final RegistryObject<Enchantment> SUN_BLESSING = ENCHANTMENTS.register("sun_blessing",
            () -> new SunBlessing(Enchantment.Rarity.RARE,
                    EnchantmentType.create("solar", item -> item.equals(Items.DIAMOND_CHESTPLATE)),
                    new EquipmentSlotType[] { EquipmentSlotType.FEET } ));

    public static final RegistryObject<Enchantment> STEALTH = ENCHANTMENTS.register("stealth",
            () -> new Stealth(Enchantment.Rarity.VERY_RARE,
                    EnchantmentType.create("night_shade", item -> item.equals(Items.DIAMOND_BOOTS)),
                    new EquipmentSlotType[] { EquipmentSlotType.FEET } ));
     */

    /*
    public static final RegistryObject<Enchantment> STEALTH = ENCHANTMENTS.register("sun_blessing",
            () -> new SunBlessing(Enchantment.Rarity.COMMON,
                    EnchantmentType.create("stealth_enchantment", Predicate.isEqual(NSBase.class) ),
                    new EquipmentSlotType[] { EquipmentSlotType.FEET } ));
     */

    // !Enchantments

    // !Registries

    //

    //

    public static final ForgeConfigSpec CLIENT_SPEC;
    static final ClientConfig CLIENT;
    static {
        {
            final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
    }

    public Parkour() {

        //ShapedRecipe.setCraftingSize(5, 5);

        // Register Armor Materials

        RubyMaterial.doInitStuff();
        SolarMaterial.doInitStuff();
        TaydonMaterial.doInitStuff();

        // !Register Armor Materials

        LOGGER.debug("Its about to get waaay more OP...");

        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Configs (Does not need to be after Deferred Registers)
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);


        // Register Deferred Registers (Does not need to be before Configs)
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        // Sound Register
        SOUNDS.register(modEventBus);

        RECIPE_SERIALIZERS.register(modEventBus);

        //ENCHANTMENTS.register(modEventBus);

        modEventBus.addListener(this::commonSetup); // for structures
    }

    // Structures

    public static final ResourceLocation KPC_FORGE_LOC = new ResourceLocation(MOD_ID, "kpc_forge");

    public static IStructurePieceType KPC_FORGE_PIECE = null;

    @ObjectHolder(MOD_ID + ":kpc_forge")
    public static Structure<NoFeatureConfig> KPC_FORGE; // = new KPCForgeBlob(NoFeatureConfig::deserialize); // Structure<NoFeatureConfig>

    public void commonSetup(FMLCommonSetupEvent args) {
        DeferredWorkQueue.runLater(() -> {
            Iterator<Biome> biomes = ForgeRegistries.BIOMES.iterator();
            biomes.forEachRemaining((biome) -> {
                // kpc forge
                if (biome == Biomes.DEEP_WARM_OCEAN || biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_OCEAN) {
                    biome.addStructure(KPC_FORGE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
                    biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,
                            KPC_FORGE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
                                    .withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
                }
            });
        });
    }

    // !Structures


    // entities

    public static Item SUPER_DROWNED_ENTITY_EGG = null;

    // !entities
}
