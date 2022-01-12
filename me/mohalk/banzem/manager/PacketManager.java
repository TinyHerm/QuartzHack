/*    */ package me.mohalk.banzem.manager;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import me.mohalk.banzem.features.Feature;
/*    */ import net.minecraft.network.Packet;
/*    */ 
/*    */ public class PacketManager
/*    */   extends Feature
/*    */ {
/* 11 */   private final List<Packet<?>> noEventPackets = new ArrayList<>();
/*    */   
/*    */   public void sendPacketNoEvent(Packet<?> packet) {
/* 14 */     if (packet != null && !nullCheck()) {
/* 15 */       this.noEventPackets.add(packet);
/* 16 */       mc.field_71439_g.field_71174_a.func_147297_a(packet);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean shouldSendPacket(Packet<?> packet) {
/* 21 */     if (this.noEventPackets.contains(packet)) {
/* 22 */       this.noEventPackets.remove(packet);
/* 23 */       return false;
/*    */     } 
/* 25 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\PacketManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */