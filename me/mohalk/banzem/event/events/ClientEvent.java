/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ import me.mohalk.banzem.features.Feature;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraftforge.fml.common.eventhandler.Cancelable;
/*    */ 
/*    */ @Cancelable
/*    */ public class ClientEvent
/*    */   extends EventStage {
/*    */   private Feature feature;
/*    */   private Setting setting;
/*    */   
/*    */   public ClientEvent(int stage, Feature feature) {
/* 15 */     super(stage);
/* 16 */     this.feature = feature;
/*    */   }
/*    */   
/*    */   public ClientEvent(Setting setting) {
/* 20 */     super(2);
/* 21 */     this.setting = setting;
/*    */   }
/*    */   
/*    */   public Feature getFeature() {
/* 25 */     return this.feature;
/*    */   }
/*    */   
/*    */   public Setting getSetting() {
/* 29 */     return this.setting;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\ClientEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */