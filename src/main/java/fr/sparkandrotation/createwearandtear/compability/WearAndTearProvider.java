package fr.sparkandrotation.createwearandtear.compability;

import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.logic.WearAndTearLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WearAndTearProvider  implements ICapabilitySerializable<CompoundTag> {

    private final WearAndTearImp backend;
    private final LazyOptional<WearAndTearImp> optionalStorage;
    private final Capability<IWearAndTear> capability;

    public WearAndTearProvider(WearAndTearImp backend, Capability<IWearAndTear> capability){
        this.backend =backend;
        this.capability =capability;
        this.optionalStorage = LazyOptional.of(() -> backend);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == capability){
            return this.optionalStorage.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.deserializeNBT(nbt);
    }
}
