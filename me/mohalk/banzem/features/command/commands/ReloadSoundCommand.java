/*    */ package me.mohalk.banzem.features.command.commands;
/*    */ 
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ import me.mohalk.banzem.util.Util;
/*    */ import net.minecraft.client.audio.SoundHandler;
/*    */ import net.minecraft.client.audio.SoundManager;
/*    */ import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
/*    */ 
/*    */ public class ReloadSoundCommand
/*    */   extends Command {
/*    */   public ReloadSoundCommand() {
/* 12 */     super("sound", new String[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(String[] commands) {
/*    */     try {
/* 18 */       SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, Util.mc.func_147118_V(), new String[] { "sndManager", "field_147694_f" });
/* 19 */       sndManager.func_148596_a();
/* 20 */       sendMessage("§aReloaded Sound System.");
/* 21 */     } catch (Exception e) {
/* 22 */       System.out.println("Could not restart sound manager: " + e.toString());
/* 23 */       e.printStackTrace();
/* 24 */       sendMessage("§cCouldnt Reload Sound System!");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\command\commands\ReloadSoundCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */