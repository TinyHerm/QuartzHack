/*    */ package me.mohalk.banzem.features.modules.client;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.Util;
/*    */ 
/*    */ public class Media
/*    */   extends Module {
/*    */   private static Media instance;
/* 10 */   public final Setting<Boolean> changeOwn = register(new Setting("MyName", Boolean.valueOf(true)));
/* 11 */   public final Setting<String> ownName = register(new Setting("Name", "Name here...", v -> ((Boolean)this.changeOwn.getValue()).booleanValue()));
/*    */   
/*    */   public Media() {
/* 14 */     super("NickChanger", "Helps with creating Media", Module.Category.CLIENT, false, false, false);
/* 15 */     instance = this;
/*    */   }
/*    */   
/*    */   public static Media getInstance() {
/* 19 */     if (instance == null) {
/* 20 */       instance = new Media();
/*    */     }
/* 22 */     return instance;
/*    */   }
/*    */   
/*    */   public static String getPlayerName() {
/* 26 */     if (fullNullCheck() || !ServerModule.getInstance().isConnected()) {
/* 27 */       return Util.mc.func_110432_I().func_111285_a();
/*    */     }
/* 29 */     String name = ServerModule.getInstance().getPlayerName();
/* 30 */     if (name == null || name.isEmpty()) {
/* 31 */       return Util.mc.func_110432_I().func_111285_a();
/*    */     }
/* 33 */     return name;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\Media.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */