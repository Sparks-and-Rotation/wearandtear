package fr.sparkandrotation.createwearandtear.events;

import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.client.AllStyleRender;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearCompability;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearImp;
import fr.sparkandrotation.createwearandtear.logic.WearAndTearLogic;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WearAndTearClientEventListener {

    public WearAndTearClientEventListener(){

        MinecraftForge.EVENT_BUS.register(this);

    }



    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onToolTip(ItemTooltipEvent blockEvent){

        if (blockEvent.getEntity().level()==null){
            return;
        }
        if (!blockEvent.getEntity().level().getGameRules().getBoolean(CreateWearAndTear.RULE_ACTIVE)){
            return;
        }

        ItemStack stack = blockEvent.getItemStack();

        if ( CreateWearAndTear.WEAR_AND_TEAR.hasLogic(new WearAndTearImp(stack))){

            WearAndTearLogic logic = CreateWearAndTear.WEAR_AND_TEAR.getLogic(new WearAndTearImp(blockEvent.getItemStack()));

            blockEvent.getItemStack().getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {
                AllStyleRender.onRenderToolTip(blockEvent,iWearAndTear,logic);
            });
        }

    }


}
