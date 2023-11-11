package fr.sparkandrotation.createwearandtear;

import com.mojang.logging.LogUtils;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearCompability;
import fr.sparkandrotation.createwearandtear.config.CustomBlocConfig;
import fr.sparkandrotation.createwearandtear.config.WearAndTearCommonConfigs;
import fr.sparkandrotation.createwearandtear.events.WearAndTearClientEventListener;
import fr.sparkandrotation.createwearandtear.events.WearAndTearCommonEventListener;
import fr.sparkandrotation.createwearandtear.logic.WearAndTearManager;
import fr.sparkandrotation.createwearandtear.network.WearAndTearNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.File;

import static net.minecraft.world.level.GameRules.register;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateWearAndTear.MODID)
public class CreateWearAndTear {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_wearandtear";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean AUTO_LOAD_WORLD = true;
    // Create a Deferred Register to hold Blocks which will all be registered under the "create_wearandtear" namespace

    public static WearAndTearManager WEAR_AND_TEAR = null;

    public static final GameRules.Key<GameRules.BooleanValue> RULE_ACTIVE = register("wearandtear", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static ResourceLocation asRessource(String name){
        return new ResourceLocation(MODID,name);
    }
    public CreateWearAndTear() {



        // Register Logic
        WEAR_AND_TEAR = new WearAndTearManager();

        CustomBlocConfig.init(new File(FMLPaths.CONFIGDIR.get().toString(), "wearandtear-custom-block.json"));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WearAndTearCommonConfigs.SPEC, "wearandtear-common.toml");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::common);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);

    }
    private void common(final FMLCommonSetupEvent e)
    {
        WEAR_AND_TEAR.load();
        // Register Capability
        new WearAndTearCompability();
        // init system sync client
        WearAndTearNetwork.initMessage();
        new WearAndTearCommonEventListener();
    }
    private void client(final FMLClientSetupEvent e)
    {
        new WearAndTearClientEventListener();
    }



}
