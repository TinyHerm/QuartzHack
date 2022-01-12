/*    */ package me.mohalk.banzem.features.modules.module;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ 
/*    */ public class FriendSettings
/*    */   extends Module {
/*    */   private static FriendSettings INSTANCE;
/*  9 */   public Setting<Boolean> notify = register(new Setting("Notify", Boolean.valueOf(false)));
/*    */ 
/*    */   
/*    */   public FriendSettings() {
/* 13 */     super("FriendSettings", "Change aspects of friends", Module.Category.MODULE, true, false, false);
/* 14 */     INSTANCE = this;
/*    */   }
/*    */   
/*    */   public static FriendSettings getInstance() {
/* 18 */     if (INSTANCE == null) {
/* 19 */       INSTANCE = new FriendSettings();
/*    */     }
/* 21 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\module\FriendSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */