/*    */ package me.mohalk.banzem.features.modules.misc;
/*    */ 
/*    */ import io.netty.buffer.Unpooled;
/*    */ import me.mohalk.banzem.event.events.PacketEvent;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.network.play.client.CPacketCustomPayload;
/*    */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoHandShake
/*    */   extends Module
/*    */ {
/*    */   public NoHandShake() {
/* 16 */     super("NoHandshake", "Doesn't send your mod list to the server.", Module.Category.MISC, true, false, false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @SubscribeEvent
/*    */   public void onPacketSend(PacketEvent.Send event) {
/* 23 */     if (event.getPacket() instanceof net.minecraftforge.fml.common.network.internal.FMLProxyPacket && !mc.func_71356_B())
/* 24 */       event.setCanceled(true); 
/*    */     CPacketCustomPayload packet;
/* 26 */     if (event.getPacket() instanceof CPacketCustomPayload && (packet = (CPacketCustomPayload)event.getPacket()).func_149559_c().equals("MC|Brand"))
/* 27 */       packet.field_149561_c = (new PacketBuffer(Unpooled.buffer())).func_180714_a("vanilla"); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\NoHandShake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */