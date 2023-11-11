package fr.sparkandrotation.createwearandtear.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WearAndTearPacketClientHandlerClass {
    public static void handlePacket(WearAndTearSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {

        if (Minecraft.getInstance().level != null){
            msg.updateClient(Minecraft.getInstance().level);
        }
    }
}
