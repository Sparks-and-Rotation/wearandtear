package fr.sparkandrotation.createwearandtear.logic;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.client.AllStyleRender;
import fr.sparkandrotation.createwearandtear.compability.IWearAndTear;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearCompability;
import fr.sparkandrotation.createwearandtear.repair.WearAndTearStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WearAndTearLogic {

    public WearAndTearLogicOptions options;


    public WearAndTearLogic(WearAndTearLogicOptions options){
        this.options = options;
    }




    public void onClientTick(KineticBlockEntity block){

        block.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {;
            AllStyleRender.onRenderClient(block,this,iWearAndTear);
        });

    }

    public boolean isDestroy(IWearAndTear imp){
        return imp.isDestroyWithUsage();
    }

    public void breakBlockWithStatus(BlockPos pos,ServerPlayer player,ServerLevel level,IWearAndTear imp,boolean drop){
        ItemStack tool = player.getItemInHand(InteractionHand.MAIN_HAND);

        LootParams.Builder builder = new LootParams.Builder((ServerLevel) level).withParameter(LootContextParams.ORIGIN,pos.getCenter()).withParameter(LootContextParams.TOOL,tool);

        List<ItemStack> stackList = level.getBlockState(pos).getDrops(builder);

        level.setBlock(pos,Blocks.AIR.defaultBlockState(),0);

        stackList.forEach(stack -> {



            if (stack.getCapability(WearAndTearCompability.WEAR_AND_TEAR).isPresent()){
                stack.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {


                    AllStyleRender.onItemStackName(stack,iWearAndTear);

                    iWearAndTear.setStatus(imp.getStatus());
                    iWearAndTear.setUsage(imp.getUsage());

                    stack.getOrCreateTag().put("wear_and_tear",iWearAndTear.serializeNBT());

                    if (drop){
                        Block.popResource(level,pos,stack);
                    }else{
                        player.getInventory().add(stack);
                    }

                });
            }else{
                Block.popResource((Level) level,pos,stack);
            }
        });
    }



    public void onServerTick(KineticBlockEntity block){

        block.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {
            ServerLevel serverLevel = (ServerLevel) block.getLevel();

            if( block.getSpeed()!=0){

                if (isDestroy(iWearAndTear)){
                    iWearAndTear.setStatus(WearAndTearStatus.DESTROY);
                    destroy(block);
                }else{
                    if (iWearAndTear.isDestroyWithUsage()){
                        iWearAndTear.setStatus(WearAndTearStatus.DESTROY);
                        iWearAndTear.setUsageForDestroy();
                    }else{
                        this.options.onRemove(iWearAndTear);
                    }
                }
            }
            if (!iWearAndTear.isDestroyWithUsage()){
                if (iWearAndTear.getStatus().equals(WearAndTearStatus.DESTROY)){
                    iWearAndTear.setStatus(WearAndTearStatus.OK);
                    onRepairOk(iWearAndTear,block);
                }
            }

            assert serverLevel != null;
            // Send sync packet to all client in same dimension
            iWearAndTear.sendPacket(serverLevel::dimension,block.getBlockPos());
        });

        block.setChanged();

    }

    public static int DAMAGE_STRESS = 10000;

    HashMap<String,BlockPos> sources = new HashMap<>();

    public String id_string(BlockPos pos){
        return pos.getX()+";"+pos.getY()+";"+pos.getZ();
    }

    public void destroy(KineticBlockEntity block){
        block.detachKinetics();
        block.setSpeed(0);
        block.setChanged();
    }
    public void onRemove(KineticBlockEntity block){
        block.detachKinetics();
        block.setSpeed(0);
        block.setChanged();
    }

    public void onRepairOk(IWearAndTear base,KineticBlockEntity block){
        BlockState state = block.getBlockState();
        BlockPos pos = block.getBlockPos();
        Level level = block.getLevel();

        block.attachKinetics();
        block.setChanged();


    }

    public void onBreak(BlockEvent.BreakEvent blockEvent, IWearAndTear imp, BlockEntity entity) {

        blockEvent.setCanceled(true);

        breakBlockWithStatus(blockEvent.getPos(),(ServerPlayer) blockEvent.getPlayer(),(ServerLevel) blockEvent.getLevel(),imp,true);

    }

    public WearAndTearLogicType getType() {
        return this.options.type;
    }

    public void onLeftClick(PlayerInteractEvent.LeftClickBlock blockEvent, IWearAndTear iWearAndTear, BlockEntity entity) {

        ItemStack tool = blockEvent.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
        if (tool.is(AllTags.AllItemTags.WRENCH.tag)){

            blockEvent.setCanceled(true);
            if(!blockEvent.getEntity().getCooldowns().isOnCooldown(tool.getItem())){
                AllStyleRender.onWrenchUsage(blockEvent,iWearAndTear);
                this.options.onRepairWrench(this,tool,blockEvent,iWearAndTear);
            }
        }

    }

    public void onRightClick(PlayerInteractEvent.RightClickBlock blockEvent, IWearAndTear iWearAndTear, BlockEntity entity) {

        ItemStack tool = blockEvent.getEntity().getItemInHand(InteractionHand.MAIN_HAND);

        if (tool.is(AllTags.AllItemTags.WRENCH.tag) && blockEvent.getEntity().isCrouching()){

            ServerLevel serverLevel = (ServerLevel) blockEvent.getLevel();

            blockEvent.setCanceled(true);
            serverLevel.playSound(null,blockEvent.getPos(), AllSoundEvents.WRENCH_REMOVE.getMainEvent(),SoundSource.BLOCKS,100F,0F);

            breakBlockWithStatus(blockEvent.getPos(),(ServerPlayer) blockEvent.getEntity(),(ServerLevel) blockEvent.getLevel(),iWearAndTear,false);


        }
    }
}
