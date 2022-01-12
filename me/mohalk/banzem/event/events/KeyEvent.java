/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ 
/*    */ public class KeyEvent
/*    */   extends EventStage {
/*    */   public boolean info;
/*    */   public boolean pressed;
/*    */   
/*    */   public KeyEvent(int stage, boolean info, boolean pressed) {
/* 11 */     super(stage);
/* 12 */     this.info = info;
/* 13 */     this.pressed = pressed;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\KeyEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */