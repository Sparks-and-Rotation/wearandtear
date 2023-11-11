package fr.sparkandrotation.createwearandtear.network;

import fr.sparkandrotation.createwearandtear.compability.WearAndTearCompability;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearImp;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearProvider;
import fr.sparkandrotation.createwearandtear.repair.WearAndTearStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WearAndTearSyncPacket {


    public WearAndTearStatus repair_etape;
    public BlockPos pos;

    public long usage = 0;


    public WearAndTearSyncPacket(BlockPos pos, long usage, WearAndTearStatus repair_etape){
        this.pos = pos;
        this.usage = usage;
        this.repair_etape = repair_etape;
    }

    public WearAndTearSyncPacket(FriendlyByteBuf friendlyByteBuf){
        this.pos = friendlyByteBuf.readBlockPos();
        this.usage = friendlyByteBuf.readLong();
        this.repair_etape = friendlyByteBuf.readEnum(WearAndTearStatus.class);
    }

    public void updateClient(ClientLevel level){
        BlockEntity entity = level.getBlockEntity(this.pos);
        if( entity!= null){
            entity.getCapability(WearAndTearCompability.WEAR_AND_TEAR).ifPresent(iWearAndTear -> {
                iWearAndTear.setUsage(this.usage);
                iWearAndTear.setStatus(this.repair_etape);
            });
        }
    }
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeLong(this.usage);
        friendlyByteBuf.writeEnum(this.repair_etape);
    }
    public static WearAndTearSyncPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new WearAndTearSyncPacket(friendlyByteBuf);
    }

    public static void handle(WearAndTearSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> WearAndTearPacketClientHandlerClass.handlePacket(msg, ctx))
        );
        ctx.get().setPacketHandled(true);
    }

}
