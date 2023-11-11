package fr.sparkandrotation.createwearandtear.events;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearCompability;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearImp;
import fr.sparkandrotation.createwearandtear.logic.WearAndTearLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WearAndTearCommonEventListener {

    public WearAndTearCommonEventListener(){

        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlace(BlockEvent.EntityPlaceEvent blockEvent){

        if (blockEvent.getLevel().isClientSide())return;

        if (!((ServerLevel) blockEvent.getLevel()).getGameRules().getBoolean(CreateWearAndTear.RULE_ACTIVE)){
            return;
        }

        AtomicBoolean isWearAndTearItem = new AtomicBoolean(false);
        AtomicReference<ItemStack> stackAtomicReference = new AtomicReference<>(null);

        blockEvent.getEntity().getHandSlots().forEach(stack -> {
            if (stack.getCapability(WearAndTearCompability.WEAR_AND_TEAR).isPresent()){
                isWearAndTearItem.set(true);
                stackAtomicReference.set(stack);
            }
        });

        if (isWearAndTearItem.get()){

            stackAtomicReference.get().getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {

                BlockEntity entity = blockEvent.getLevel().getBlockEntity(blockEvent.getPos());

                assert entity != null;
                entity.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTearBlock -> {

                    iWearAndTearBlock.setUsage(iWearAndTear.getUsage());
                    iWearAndTearBlock.setStatus(iWearAndTear.getStatus());


                });


            });


        }

    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent.RightClickBlock blockEvent){

        if (blockEvent.getLevel().isClientSide())return;

        if (!((ServerLevel) blockEvent.getLevel()).getGameRules().getBoolean(CreateWearAndTear.RULE_ACTIVE)){
            return;
        }

        BlockEntity entity = blockEvent.getLevel().getBlockEntity(blockEvent.getPos());

        if(entity instanceof KineticBlockEntity kineticBlock){

            kineticBlock.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {

                WearAndTearLogic logic = CreateWearAndTear.WEAR_AND_TEAR.getLogic(new WearAndTearImp(entity));

                logic.onRightClick(blockEvent,iWearAndTear,entity);

            });

        }


    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent.LeftClickBlock blockEvent){

        if (blockEvent.getLevel().isClientSide())return;

        if (!((ServerLevel) blockEvent.getLevel()).getGameRules().getBoolean(CreateWearAndTear.RULE_ACTIVE)){
            return;
        }

        BlockEntity entity = blockEvent.getLevel().getBlockEntity(blockEvent.getPos());

        if(entity instanceof KineticBlockEntity kineticBlock){

            kineticBlock.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {

                WearAndTearLogic logic = CreateWearAndTear.WEAR_AND_TEAR.getLogic(new WearAndTearImp(entity));

                logic.onLeftClick(blockEvent,iWearAndTear,entity);

            });

        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBreak(BlockEvent.BreakEvent blockEvent){

        if (blockEvent.getLevel().isClientSide())return;

        if (!((ServerLevel) blockEvent.getLevel()).getGameRules().getBoolean(CreateWearAndTear.RULE_ACTIVE)){
            return;
        }


        if(blockEvent.getPlayer().isCreative()){
            return;
        }

        BlockEntity entity = blockEvent.getLevel().getBlockEntity(blockEvent.getPos());

        if(entity instanceof KineticBlockEntity kineticBlock){

            kineticBlock.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {

                WearAndTearLogic logic = CreateWearAndTear.WEAR_AND_TEAR.getLogic(new WearAndTearImp(entity));

                logic.onBreak(blockEvent,iWearAndTear,entity);

            });


        }

    }

}
