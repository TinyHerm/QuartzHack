/*    */ package me.mohalk.banzem.features.modules.misc;
/*    */ 
/*    */ import me.mohalk.banzem.DiscordPresence;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ 
/*    */ public class RPC extends Module {
/*    */   public static RPC INSTANCE;
/*  9 */   public Setting<Boolean> showIP = register(new Setting("ShowIP", Boolean.valueOf(true), "Shows the server IP in your discord presence."));
/* 10 */   public Setting<String> state = register(new Setting("State", "v0.3.0", "Sets the state of the DiscordRPC."));
/* 11 */   public Setting<String> largeImageText = register(new Setting("LargeImageText", "v0.3.0", "Sets the large image text of the DiscordRPC."));
/* 12 */   public Setting<String> smallImageText = register(new Setting("SmallImageText", "QuartzHack - 0.3.0-rewrite", "Sets the small image text of the DiscordRPC."));
/*    */ 
/*    */   
/*    */   public RPC() {
/* 16 */     super("RPC", "Discord rich presence", Module.Category.MISC, false, false, false);
/*    */     
/* 18 */     INSTANCE = this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 23 */     DiscordPresence.start();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 29 */     DiscordPresence.stop();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\RPC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */