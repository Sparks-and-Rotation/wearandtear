package fr.sparkandrotation.createwearandtear.compability;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.repair.WearAndTearStatus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.checkerframework.checker.units.qual.C;

public class WearAndTearCompability {

    public static Capability<IWearAndTear> WEAR_AND_TEAR = CapabilityManager.get(new CapabilityToken<>() {});

    public WearAndTearCompability() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegister(final RegisterCapabilitiesEvent event) {
        event.register(IWearAndTear.class);
    }

    @SubscribeEvent
    public void onAttachingCapabilitiesBlockEntity(final AttachCapabilitiesEvent<BlockEntity> event) {

        WearAndTearImp imp = new WearAndTearImp(event.getObject());

        if (!imp.isOk()){
            return;
        }
        if (!CreateWearAndTear.WEAR_AND_TEAR.hasLogic(imp)){
            return;
        }

        WearAndTearProvider provider = new WearAndTearProvider(imp,WEAR_AND_TEAR);

        event.addCapability(CreateWearAndTear.asRessource("wear_and_tear"),provider);
    }

    @SubscribeEvent
    public void onAttachingCapabilitiesItemStack(final AttachCapabilitiesEvent<ItemStack> event) {

        WearAndTearImp imp = new WearAndTearImp(event.getObject());


        if (!imp.isOk()){
            return;
        }
        if (!CreateWearAndTear.WEAR_AND_TEAR.hasLogic(imp)){
            return;
        }

        CompoundTag tag = event.getObject().getOrCreateTag();

        if (tag.contains("wear_and_tear")){
            imp.deserializeNBT(tag.getCompound("wear_and_tear"));
        }

        WearAndTearProvider provider = new WearAndTearProvider(imp,WEAR_AND_TEAR);

        event.addCapability(CreateWearAndTear.asRessource("wear_and_tear"),provider);
    }
}
