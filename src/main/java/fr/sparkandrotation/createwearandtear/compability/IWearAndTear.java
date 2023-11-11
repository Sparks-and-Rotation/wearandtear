package fr.sparkandrotation.createwearandtear.compability;

import fr.sparkandrotation.createwearandtear.repair.WearAndTearStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface IWearAndTear {

    long getUsage();

    void setUsage(long NewUsage);

    void addUsage(long addUsage);

    boolean isDestroyWithUsage();

    boolean isDestroyWithStatus();

    void setDestroy();

    void setUsageForDestroy();

    WearAndTearStatus getStatus();

    void setStatus(WearAndTearStatus status);

    void sendPacket(Supplier<ResourceKey<Level>> key, BlockPos pos);

    Class<?> id();

    void removeUsage(long i);

    CompoundTag serializeNBT();

}
