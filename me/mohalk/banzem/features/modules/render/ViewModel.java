/*    */ package me.mohalk.banzem.features.modules.render;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ViewModel
/*    */   extends Module
/*    */ {
/* 12 */   public final Setting<Integer> translateX = register(new Setting("TranslateX", Integer.valueOf(0), Integer.valueOf(-200), Integer.valueOf(200)));
/* 13 */   public final Setting<Integer> translateY = register(new Setting("TranslateY", Integer.valueOf(0), Integer.valueOf(-200), Integer.valueOf(200)));
/* 14 */   public final Setting<Integer> translateZ = register(new Setting("TranslateZ", Integer.valueOf(0), Integer.valueOf(-200), Integer.valueOf(200)));
/*    */   
/* 16 */   public final Setting<Integer> rotateX = register(new Setting("RotateX", Integer.valueOf(0), Integer.valueOf(-200), Integer.valueOf(200)));
/* 17 */   public final Setting<Integer> rotateY = register(new Setting("RotateY", Integer.valueOf(0), Integer.valueOf(-200), Integer.valueOf(200)));
/* 18 */   public final Setting<Integer> rotateZ = register(new Setting("RotateZ", Integer.valueOf(0), Integer.valueOf(-200), Integer.valueOf(200)));
/*    */   
/* 20 */   public final Setting<Integer> scaleX = register(new Setting("ScaleX", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(200)));
/* 21 */   public final Setting<Integer> scaleY = register(new Setting("ScaleY", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(200)));
/* 22 */   public final Setting<Integer> scaleZ = register(new Setting("ScaleZ", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(200)));
/*    */   
/*    */   public static ViewModel INSTANCE;
/*    */ 
/*    */   
/*    */   public ViewModel() {
/* 28 */     super("ViewModel", "Cool", Module.Category.RENDER, true, false, false);
/*    */ 
/*    */     
/* 31 */     INSTANCE = this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\render\ViewModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */