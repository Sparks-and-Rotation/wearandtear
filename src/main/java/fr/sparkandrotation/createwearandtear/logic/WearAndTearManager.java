package fr.sparkandrotation.createwearandtear.logic;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearImp;
import fr.sparkandrotation.createwearandtear.config.CustomBlocConfig;
import fr.sparkandrotation.createwearandtear.config.WearAndTearCommonConfigs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class WearAndTearManager {


    private HashMap<String,WearAndTearLogic> logics = new HashMap<>();


    public WearAndTearManager(){}


    public void registerBlock(String name,WearAndTearLogicOptions options){
        boolean has_ressource = ForgeRegistries.BLOCKS.containsKey(ResourceLocation.tryParse(name));

        if (has_ressource){

            @NotNull Optional<Holder<Block>> blockHolder = ForgeRegistries.BLOCKS.getHolder(ResourceLocation.tryParse(name));

            if (blockHolder.isPresent()){

                Class<?> clazz = blockHolder.get().get().getClass();
                register_system(clazz,new WearAndTearLogic(options));

            }else{
                CreateWearAndTear.LOGGER.error("The resource " + name + " does not exists (blockHolder)");
            }
        } else {
            CreateWearAndTear.LOGGER.error("The resource " + name + " does not exists");
        }
    }

    public void load(){

        ArrayList<String> list = new ArrayList<>();

        CustomBlocConfig.registeredBlocks.forEach((name, config) -> {
            list.add(name);
            boolean has_ressource = ForgeRegistries.BLOCKS.containsKey(ResourceLocation.tryParse(name));

            WearAndTearLogicOptions options = new WearAndTearLogicOptions(config.type);
            options = options.max_durability(config.durability).remove_durability_per_chance(config.min,config.max).chance_for_remove_durability(config.chance);

            registerBlock(name,options);

        });

        WearAndTearCommonConfigs.WEAR_WHITELIST.get().forEach(s -> {
            if(!list.contains(s)){
                registerBlock(s,new WearAndTearLogicOptions());
            }
        });

    }
    public void register_system(Class<?> block,WearAndTearLogic logic){
        CreateWearAndTear.LOGGER.info("[Manager] register "+block.getName());
        this.logics.put(id_string(block),logic);
    }

    public String id_string(Class<?> clazz){
        return clazz.getSimpleName();
    }

    public boolean hasLogic(WearAndTearImp wearAndTearImp){
        return this.logics.containsKey(id_string(wearAndTearImp.id()));
    }
    public WearAndTearLogic getLogic(WearAndTearImp wearAndTearImp){
        return this.logics.get(id_string(wearAndTearImp.id()));
    }
    public boolean isDestroy(WearAndTearImp wearAndTearImp) {

        if (hasLogic(wearAndTearImp)){
            WearAndTearLogic logic = getLogic(wearAndTearImp);
            return wearAndTearImp.getUsage() > logic.options.max_durability;
        }

        return true;
    }
}
