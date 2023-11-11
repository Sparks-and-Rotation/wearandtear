package fr.sparkandrotation.createwearandtear.network;

import fr.sparkandrotation.createwearandtear.CreateWearAndTear;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class WearAndTearNetwork {

    public static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            CreateWearAndTear.asRessource("network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void initMessage(){
        INSTANCE.registerMessage(0, WearAndTearSyncPacket.class,
                WearAndTearSyncPacket::encode, WearAndTearSyncPacket::decode,WearAndTearSyncPacket::handle);

    }
}
