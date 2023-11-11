package fr.sparkandrotation.createwearandtear.compability;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.network.WearAndTearNetwork;
import fr.sparkandrotation.createwearandtear.network.WearAndTearSyncPacket;
import fr.sparkandrotation.createwearandtear.repair.WearAndTearStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class WearAndTearImp implements IWearAndTear, INBTSerializable<CompoundTag> {

    private final Block stack;
    private long usage = 0;
    private long last_usage_send = 0;


    private WearAndTearStatus status = WearAndTearStatus.OK;


    public WearAndTearImp(ItemStack stack){
        this.stack = Block.byItem(stack.getItem());
    }
    public WearAndTearImp(Block stack){
        this.stack = stack;
    }
    public WearAndTearImp(BlockEntity stack){
        this.stack = stack.getBlockState().getBlock();
    }

    public Class<?> id(){
        return this.stack.getClass();
    }

    public Block block(){
        return this.stack;
    }

    public boolean isOk(){
        return block() instanceof KineticBlock;
    }

    @Override
    public long getUsage() {
        return usage;
    }

    @Override
    public void setUsage(long NewUsage) {
        this.usage = NewUsage;
    }


    @Override
    public void addUsage(long addUsage) {
        this.usage = this.usage + addUsage;
    }

    public void removeUsage(long usage){
        this.usage = this.usage - usage;
        if( this.usage<=0){
            this.usage = 0;
        }
    }
    @Override
    public void setDestroy() {
        this.status = WearAndTearStatus.DESTROY;
    }

    @Override
    public WearAndTearStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(WearAndTearStatus status) {
        this.status = status;
    }

    public void setUsageForDestroy(){
        this.usage = CreateWearAndTear.WEAR_AND_TEAR.getLogic(this).options.max_durability+1;
    }

    @Override
    public void sendPacket(Supplier<ResourceKey<Level>> key, BlockPos pos) {
        if (this.last_usage_send != this.usage){
            this.last_usage_send = this.usage;
            WearAndTearNetwork.INSTANCE.send(PacketDistributor.DIMENSION.with(key), new WearAndTearSyncPacket(pos,getUsage(),getStatus()));
        }

    }

    @Override
    public boolean isDestroyWithUsage() {
        return CreateWearAndTear.WEAR_AND_TEAR.isDestroy(this);
    }
    @Override
    public boolean isDestroyWithStatus() {
        return this.status.isDestroy();
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("usage",usage);
        tag.putString("status",status.name());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.usage = nbt.getLong("usage");
        this.status = WearAndTearStatus.valueOf(nbt.getString("status"));
    }
}
