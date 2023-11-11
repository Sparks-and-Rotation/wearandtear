package fr.sparkandrotation.createwearandtear.logic;

import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.compability.IWearAndTear;
import fr.sparkandrotation.createwearandtear.config.WearAndTearCommonConfigs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

public class WearAndTearLogicOptions {

    public final WearAndTearLogicType type;
    private final Random random;

    public long max_durability = WearAndTearCommonConfigs.DEFAULT_DURABILITY.get();
    public long[] remove_durability_per_chance = new long[]{0,5};

    public long chance_for_remove_durability = 15;



    public WearAndTearLogicOptions(){
        this.type = WearAndTearLogicType.REPAIR_WRENCH;
        this.random = new Random();
    }
    public WearAndTearLogicOptions(WearAndTearLogicType type){
        this.type = type;
        this.random = new Random();
    }
    public WearAndTearLogicOptions wrench_cooldowns(int wrench_cooldowns){
        this.max_durability = wrench_cooldowns;
        return this;
    }


    public WearAndTearLogicOptions max_durability(int max_durability){
        this.max_durability = max_durability;
        return this;
    }
    public WearAndTearLogicOptions remove_durability_per_chance(int min,int max){
        long realMax = Math.max(min,max);
        long realMin = Math.min(min,max);
        this.remove_durability_per_chance = new long[]{realMin,realMax};
        return this;
    }
    public WearAndTearLogicOptions chance_for_remove_durability(int chance){
        this.chance_for_remove_durability = chance;
        return this;
    }

    public void onRemove(IWearAndTear wearAndTear){

        long min = Math.min(remove_durability_per_chance[0],remove_durability_per_chance[1]);
        long max = Math.max(remove_durability_per_chance[0],remove_durability_per_chance[1]);

        if (random.nextInt(0,100)> ( 100-chance_for_remove_durability ) ){
            wearAndTear.addUsage(random.nextLong(min,max));
        }
    }

    public void onRepairWrench(WearAndTearLogic logic, ItemStack tool, PlayerInteractEvent.LeftClickBlock blockEvent, IWearAndTear iWearAndTear) {
        blockEvent.getEntity().getCooldowns().addCooldown(tool.getItem(), WearAndTearCommonConfigs.WRENCH_COOLDOWN.get());

        long max_durability = logic.options.max_durability;
        float percent_to_repair = ((float)WearAndTearCommonConfigs.WRENCH_REPAIR.get() / (float)100);
        long repair = (long) Math.floor((max_durability * percent_to_repair));




        CreateWearAndTear.LOGGER.info(" repair "+repair+" / "+max_durability);
        iWearAndTear.removeUsage(repair);
    }
}
