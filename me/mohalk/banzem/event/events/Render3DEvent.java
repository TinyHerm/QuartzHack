/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ 
/*    */ public class Render3DEvent
/*    */   extends EventStage {
/*    */   private final float partialTicks;
/*    */   
/*    */   public Render3DEvent(float partialTicks) {
/* 10 */     this.partialTicks = partialTicks;
/*    */   }
/*    */   
/*    */   public float getPartialTicks() {
/* 14 */     return this.partialTicks;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\Render3DEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */