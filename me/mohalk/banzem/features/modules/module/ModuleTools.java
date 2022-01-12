/*    */ package me.mohalk.banzem.features.modules.module;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ 
/*    */ public class ModuleTools
/*    */   extends Module
/*    */ {
/*    */   private static ModuleTools INSTANCE;
/* 10 */   public Setting<Notifier> notifier = register(new Setting("ModuleNotifier", Notifier.FUTURE));
/* 11 */   public Setting<PopNotifier> popNotifier = register(new Setting("PopNotifier", PopNotifier.FUTURE));
/*    */   
/*    */   public ModuleTools() {
/* 14 */     super("ModuleTools", "Change settings", Module.Category.CLIENT, true, false, false);
/* 15 */     INSTANCE = this;
/*    */   }
/*    */ 
/*    */   
/*    */   public static ModuleTools getInstance() {
/* 20 */     if (INSTANCE == null) {
/* 21 */       INSTANCE = new ModuleTools();
/*    */     }
/* 23 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public enum Notifier
/*    */   {
/* 28 */     PHOBOS,
/* 29 */     FUTURE,
/* 30 */     DOTGOD;
/*    */   }
/*    */   
/*    */   public enum PopNotifier {
/* 34 */     PHOBOS,
/* 35 */     FUTURE,
/* 36 */     DOTGOD,
/* 37 */     NONE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\module\ModuleTools.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */