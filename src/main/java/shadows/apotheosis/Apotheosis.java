package shadows.apotheosis;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import shadows.apotheosis.advancements.AdvancementTriggers;
import shadows.apotheosis.deadly.DeadlyModule;
import shadows.apotheosis.ench.EnchModule;
import shadows.apotheosis.garden.GardenModule;
import shadows.apotheosis.potion.PotionModule;
import shadows.apotheosis.spawn.SpawnerModule;
import shadows.apotheosis.util.ModuleCondition;
import shadows.apotheosis.util.ParticleMessage;
import shadows.apotheosis.village.VillageModule;
import shadows.placebo.config.Configuration;
import shadows.placebo.recipe.NBTIngredient;
import shadows.placebo.recipe.RecipeHelper;
import shadows.placebo.util.NetworkUtils;

@Mod(Apotheosis.MODID)
public class Apotheosis {

	public static final String MODID = "apotheosis";
	//Formatter::off
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, MODID))
            .clientAcceptedVersions(s->true)
            .serverAcceptedVersions(s->true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();
    //Formatter::on

	public static final RecipeHelper HELPER = new RecipeHelper(Apotheosis.MODID);

	public static File configDir;
	public static Configuration config;
	public static boolean enableSpawner = true;
	public static boolean enableGarden = true;
	public static boolean enableDeadly = true;
	public static boolean enableEnch = true;
	public static boolean enablePotion = true;
	public static boolean enableVillager = true;
	public static boolean enchTooltips = true;

	public Apotheosis() {
		configDir = new File(FMLPaths.CONFIGDIR.get().toFile(), MODID);
		config = new Configuration(new File(configDir, MODID + ".cfg"));

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		enableEnch = config.getBoolean("Enable Enchantment Module", "general", true, "If the enchantment module is enabled.");
		if (enableEnch) bus.register(new EnchModule());

		enableSpawner = config.getBoolean("Enable Spawner Module", "general", true, "If the spawner module is enabled.");
		if (enableSpawner) bus.register(new SpawnerModule());

		enableGarden = config.getBoolean("Enable Garden Module", "general", true, "If the garden module is loaded.");
		if (enableGarden) bus.register(new GardenModule());

		enableDeadly = config.getBoolean("Enable Deadly Module", "general", true, "If the deadly module is loaded.");
		if (enableDeadly) bus.register(new DeadlyModule());

		enablePotion = config.getBoolean("Enable Potion Module", "general", true, "If the potion module is loaded.");
		if (enablePotion) bus.register(new PotionModule());

		enableVillager = config.getBoolean("Enable Village Module", "general", enableVillager, "If the village module is loaded.");
		if (enableVillager) bus.register(new VillageModule());

		enchTooltips = config.getBoolean("Enchantment Tooltips", "client", true, "If apotheosis enchantments have tooltips on books.");

		if (config.hasChanged()) config.save();
		bus.post(new ApotheosisConstruction());
		bus.addListener(this::init);
	}

	public void init(FMLCommonSetupEvent e) {
		NetworkUtils.registerMessage(CHANNEL, 0, new ParticleMessage());
		FMLJavaModLoadingContext.get().getModEventBus().post(new ApotheosisSetup());
		DeferredWorkQueue.runLater(AdvancementTriggers::init);
		CraftingHelper.register(new ModuleCondition.Serializer());
	}

	public static Ingredient potionIngredient(Potion type) {
		return new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), type));
	}

	public static class ApotheosisConstruction extends Event {
		public ApotheosisConstruction() {
		}
	}

	public static class ApotheosisSetup extends Event {
		public ApotheosisSetup() {
		}
	}

}
