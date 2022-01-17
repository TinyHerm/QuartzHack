/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.network.handshake.client.C00Handshake
 */
package me.mohalk.banzem.mixin.mixins;

import me.mohalk.banzem.features.modules.client.ServerModule;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={C00Handshake.class})
public abstract class MixinC00Handshake {
    @Redirect(method={"writePacketData"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/PacketBuffer;writeString(Ljava/lang/String;)Lnet/minecraft/network/PacketBuffer;"))
    public PacketBuffer writePacketDataHook(PacketBuffer packetBuffer, String string) {
        if (ServerModule.getInstance().noFML.getValue().booleanValue()) {
            String ipNoFML = string.substring(0, string.length() - "\u0000FML\u0000".length());
            return packetBuffer.func_180714_a(ipNoFML);
        }
        return packetBuffer.func_180714_a(string);
    }
}

