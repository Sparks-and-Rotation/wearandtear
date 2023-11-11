package fr.sparkandrotation.createwearandtear.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearCompability;
import fr.sparkandrotation.createwearandtear.compability.WearAndTearImp;
import fr.sparkandrotation.createwearandtear.logic.WearAndTearLogic;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KineticBlockEntity.class,remap = false)
public class MixinKineticBlockEntity {

    private WearAndTearLogic getLogic(){
        return CreateWearAndTear.WEAR_AND_TEAR.getLogic(new WearAndTearImp(getEntity()));
    }
    private boolean hasLogic(){
        return CreateWearAndTear.WEAR_AND_TEAR.hasLogic(new WearAndTearImp(getEntity()));
    }
    private KineticBlockEntity getEntity(){
        KineticBlockEntity kineticBlockEntity = (KineticBlockEntity) ((Object) this);;
        return kineticBlockEntity;
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void tick(CallbackInfo info) {
        if (!getEntity().getLevel().getGameRules().getBoolean(CreateWearAndTear.RULE_ACTIVE)){
            return;
        }
        if (hasLogic()){
            if (getEntity().getLevel().isClientSide){
                getLogic().onClientTick(getEntity());
            }else{
                getLogic().onServerTick(getEntity());
            }
        }
    }
    @Inject(at = @At("RETURN"), method = "remove")
    public void remove(CallbackInfo info) {
        if (!getEntity().getLevel().getGameRules().getBoolean(CreateWearAndTear.RULE_ACTIVE)){
            return;
        }
        if (hasLogic()){
            getLogic().onRemove(getEntity());
        }
    }

}
