/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ 
/*    */ public class ValueChangeEvent
/*    */   extends EventStage {
/*    */   public Setting setting;
/*    */   public Object value;
/*    */   
/*    */   public ValueChangeEvent(Setting setting, Object value) {
/* 12 */     this.setting = setting;
/* 13 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\ValueChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */