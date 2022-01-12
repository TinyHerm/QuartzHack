/*    */ package me.mohalk.banzem.features.modules.module;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ 
/*    */ public class Display
/*    */   extends Module {
/*  8 */   private static Display INSTANCE = new Display();
/*  9 */   public Setting<String> gang = register(new Setting("Title", "QuartzHack - 0.3.0-rewrite"));
/* 10 */   public Setting<Boolean> version = register(new Setting("Version", Boolean.valueOf(true)));
/*    */   
/*    */   public Display() {
/* 13 */     super("Display", "Sets the title of your game", Module.Category.MODULE, true, false, false);
/* 14 */     setInstance();
/*    */   }
/*    */   public static Display getInstance() {
/* 17 */     if (INSTANCE == null) {
/* 18 */       INSTANCE = new Display();
/*    */     }
/* 20 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   private void setInstance() {
/* 24 */     INSTANCE = this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 29 */     org.lwjgl.opengl.Display.setTitle((String)this.gang.getValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\module\Display.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */