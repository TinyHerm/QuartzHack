/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ 
/*    */ public class TotemPopEvent
/*    */   extends EventStage {
/*    */   private final EntityPlayer entity;
/*    */   
/*    */   public TotemPopEvent(EntityPlayer entity) {
/* 11 */     this.entity = entity;
/*    */   }
/*    */   
/*    */   public EntityPlayer getEntity() {
/* 15 */     return this.entity;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\TotemPopEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */