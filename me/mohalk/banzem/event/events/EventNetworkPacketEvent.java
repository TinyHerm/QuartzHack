/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ import net.minecraft.network.Packet;
/*    */ 
/*    */ 
/*    */ public class EventNetworkPacketEvent
/*    */   extends EventStage
/*    */ {
/*    */   public Packet m_Packet;
/*    */   
/*    */   public EventNetworkPacketEvent(Packet p_Packet) {
/* 13 */     this.m_Packet = p_Packet;
/*    */   }
/*    */ 
/*    */   
/*    */   public Packet GetPacket() {
/* 18 */     return this.m_Packet;
/*    */   }
/*    */ 
/*    */   
/*    */   public Packet getPacket() {
/* 23 */     return this.m_Packet;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\EventNetworkPacketEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */