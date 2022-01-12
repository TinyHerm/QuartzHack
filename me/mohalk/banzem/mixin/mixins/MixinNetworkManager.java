/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import io.netty.channel.ChannelHandlerContext;
/*    */ import me.mohalk.banzem.event.events.EventNetworkPacketEvent;
/*    */ import me.mohalk.banzem.event.events.PacketEvent;
/*    */ import net.minecraft.network.NetworkManager;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.Event;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({NetworkManager.class})
/*    */ public class MixinNetworkManager {
/*    */   @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
/* 19 */     PacketEvent.Send event = new PacketEvent.Send(0, packet);
/* 20 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 21 */     if (event.isCanceled()) {
/* 22 */       info.cancel();
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At("RETURN")}, cancellable = true)
/*    */   private void onSendPacketPost(Packet<?> packet, CallbackInfo info) {
/* 28 */     PacketEvent.Send event = new PacketEvent.Send(1, packet);
/* 29 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 30 */     if (event.isCanceled()) {
/* 31 */       info.cancel();
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"channelRead0"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
/* 37 */     PacketEvent.Receive event = new PacketEvent.Receive(0, packet);
/* 38 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 39 */     if (event.isCanceled()) {
/* 40 */       info.cancel();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void onSendPacket(Packet<?> p_Packet, CallbackInfo callbackInfo) {
/* 47 */     EventNetworkPacketEvent l_Event = new EventNetworkPacketEvent(p_Packet);
/* 48 */     MinecraftForge.EVENT_BUS.post((Event)l_Event);
/*    */     
/* 50 */     if (l_Event.isCanceled())
/*    */     {
/* 52 */       callbackInfo.cancel();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   @Inject(method = {"channelRead0"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void onChannelRead(ChannelHandlerContext context, Packet<?> p_Packet, CallbackInfo callbackInfo) {
/* 59 */     EventNetworkPacketEvent l_Event = new EventNetworkPacketEvent(p_Packet);
/* 60 */     MinecraftForge.EVENT_BUS.post((Event)l_Event);
/*    */     
/* 62 */     if (l_Event.isCanceled())
/*    */     {
/*    */       
/* 65 */       callbackInfo.cancel();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinNetworkManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */