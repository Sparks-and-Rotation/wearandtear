package fr.sparkandrotation.createwearandtear.client;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import fr.sparkandrotation.createwearandtear.compability.IWearAndTear;
import fr.sparkandrotation.createwearandtear.logic.WearAndTearLogic;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.Random;

public class AllStyleRender {

    public static void onWrenchUsage(PlayerInteractEvent.LeftClickBlock blockEvent, IWearAndTear iWearAndTear) {
        ServerLevel serverLevel = (ServerLevel) blockEvent.getLevel();
        if (iWearAndTear.getUsage()<=0){
            serverLevel.playSound(null,blockEvent.getPos(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS,1000F,0F);
            ( (ServerPlayer) blockEvent.getEntity()).connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("wrench.block_is_full")));
        }else{
            serverLevel.playSound(null,blockEvent.getPos(),SoundEvents.ANVIL_USE,SoundSource.BLOCKS,1000F,1F);
        }
    }

    public static void onItemStackName(ItemStack stack, IWearAndTear iWearAndTear) {
        switch (iWearAndTear.getStatus()){
            case OK -> {
            }case DESTROY -> {
                stack.setHoverName(Component.literal("Destroyed ").withStyle(ChatFormatting.DARK_GRAY).append(stack.getDisplayName()));
            }
        }
    }

    public static void onRenderClient(KineticBlockEntity block, WearAndTearLogic logic, IWearAndTear iWearAndTear) {

        Level level = block.getLevel();
        BlockPos pos = block.getBlockPos();
        Vec3 center = pos.getCenter();
        Random random = new Random();

        /**
         * progress / 11
         * usage / max_durability
         */

        int progress = (int) (( iWearAndTear.getUsage() * 11 ) / logic.options.max_durability);

        int idProgress = block.getBlockPos().getX() + block.getBlockPos().getY() + block.getBlockPos().getZ();

        if (idProgress>=0){
            idProgress = -idProgress;
        }

        if (logic.isDestroy(iWearAndTear)) {
            if (random.nextInt(0,100)>80){
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, center.x(), center.y()+0.5, center.z(), 0.0, 0.1, 0.0);
            }
            Minecraft.getInstance().levelRenderer.destroyBlockProgress(idProgress,block.getBlockPos(),9);
        }else{
            Minecraft.getInstance().levelRenderer.destroyBlockProgress(idProgress,block.getBlockPos(),progress-1);
        }


    }


    private static void renderDurability(ItemTooltipEvent tooltipEvent, IWearAndTear iWearAndTear, WearAndTearLogic logic){
        MutableComponent component_durability =  Component.literal("");

        int barMax = 50;
        /**
         *  barMax / max_durability
         *  ? / max_durability-usage
         */

        long usage = iWearAndTear.getUsage();
        if (usage>=logic.options.max_durability){
            usage = logic.options.max_durability;
        }

        long reset_durability = logic.options.max_durability - usage;
        long croix = ( barMax * reset_durability) / logic.options.max_durability;

        int start = 0;

        while (start<barMax){
            start=start+1;
            if (start<=croix){
                component_durability.append(Component.literal("|").withStyle(ChatFormatting.GREEN));
            }else{
                component_durability.append(Component.literal("|").withStyle(ChatFormatting.RED));
            }

        }
        tooltipEvent.getToolTip().add(component_durability);
    }

    public static void onRenderToolTip(ItemTooltipEvent blockEvent, IWearAndTear iWearAndTear, WearAndTearLogic logic) {

        blockEvent.getToolTip().add(Component.translatable("tooltip.head").withStyle(ChatFormatting.GRAY));
        switch (iWearAndTear.getStatus()){
            case DESTROY -> {
                blockEvent.getToolTip().add(Component.translatable("tooltip.destroy.txt").withStyle(ChatFormatting.RED,ChatFormatting.BOLD));
            }
            case OK -> {

            }
        }
        renderDurability(blockEvent,iWearAndTear, logic);
    }
}
