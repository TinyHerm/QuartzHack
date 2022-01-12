/*    */ package me.mohalk.banzem;
/*    */ 
/*    */ import club.minnced.discord.rpc.DiscordEventHandlers;
/*    */ import club.minnced.discord.rpc.DiscordRPC;
/*    */ import club.minnced.discord.rpc.DiscordRichPresence;
/*    */ import me.mohalk.banzem.features.modules.misc.RPC;
/*    */ import net.minecraft.client.Minecraft;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiscordPresence
/*    */ {
/*    */   public static DiscordRichPresence presence;
/*    */   
/*    */   public static void start() {
/* 17 */     DiscordEventHandlers handlers = new DiscordEventHandlers();
/* 18 */     rpc.Discord_Initialize("929453675682889778", handlers, true, "");
/* 19 */     presence.startTimestamp = System.currentTimeMillis() / 1000L;
/* 20 */     presence.details = ((Minecraft.func_71410_x()).field_71462_r instanceof net.minecraft.client.gui.GuiMainMenu) ? "In the main menu." : ("Playing " + ((Minecraft.func_71410_x().func_147104_D() != null) ? (((Boolean)RPC.INSTANCE.showIP.getValue()).booleanValue() ? ("on " + (Minecraft.func_71410_x().func_147104_D()).field_78845_b + ".") : " multiplayer.") : " singleplayer."));
/* 21 */     presence.state = (String)RPC.INSTANCE.state.getValue();
/* 22 */     presence.largeImageText = (String)RPC.INSTANCE.largeImageText.getValue();
/* 23 */     presence.largeImageKey = "quartzhackrpcicon";
/* 24 */     presence.smallImageText = (String)RPC.INSTANCE.smallImageText.getValue();
/* 25 */     presence.partyId = "quartzhackrpcicon";
/* 26 */     presence.partyMax = 50;
/* 27 */     presence.partySize = 1;
/* 28 */     presence.joinSecret = "QuartzHack - 0.3.0-rewrite";
/* 29 */     rpc.Discord_UpdatePresence(presence);
/* 30 */     (thread = new Thread(() -> {
/*    */           while (!Thread.currentThread().isInterrupted()) {
/*    */             rpc.Discord_RunCallbacks();
/*    */             
/*    */             String string = "";
/*    */             
/*    */             StringBuilder sb = new StringBuilder();
/*    */             
/*    */             DiscordRichPresence presence = DiscordPresence.presence;
/*    */             
/*    */             (new StringBuilder()).append("Playing ");
/*    */             if (Minecraft.func_71410_x().func_147104_D() != null) {
/*    */               if (((Boolean)RPC.INSTANCE.showIP.getValue()).booleanValue()) {
/*    */                 string = "on " + (Minecraft.func_71410_x().func_147104_D()).field_78845_b + ".";
/*    */               } else {
/*    */                 string = " multiplayer.";
/*    */               } 
/*    */             } else {
/*    */               string = " not multiplayer.";
/*    */             } 
/*    */             presence.details = sb.append(string).toString();
/*    */             DiscordPresence.presence.state = (String)RPC.INSTANCE.state.getValue();
/*    */             rpc.Discord_UpdatePresence(DiscordPresence.presence);
/*    */             try {
/*    */               Thread.sleep(2000L);
/* 55 */             } catch (InterruptedException interruptedException) {}
/*    */           } 
/* 57 */         }"RPC-Callback-Handler")).start();
/*    */   }
/*    */   
/*    */   public static void stop() {
/* 61 */     if (thread != null && !thread.isInterrupted()) {
/* 62 */       thread.interrupt();
/*    */     }
/* 64 */     rpc.Discord_Shutdown();
/*    */   }
/*    */ 
/*    */   
/* 68 */   private static final DiscordRPC rpc = DiscordRPC.INSTANCE; static {
/* 69 */     presence = new DiscordRichPresence();
/*    */   }
/*    */   
/*    */   private static Thread thread;
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\DiscordPresence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */