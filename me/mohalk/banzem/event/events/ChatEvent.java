/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ import net.minecraftforge.fml.common.eventhandler.Cancelable;
/*    */ 
/*    */ @Cancelable
/*    */ public class ChatEvent
/*    */   extends EventStage {
/*    */   private final String msg;
/*    */   
/*    */   public ChatEvent(String msg) {
/* 12 */     this.msg = msg;
/*    */   }
/*    */   
/*    */   public String getMsg() {
/* 16 */     return this.msg;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\ChatEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */