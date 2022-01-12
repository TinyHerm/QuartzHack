/*    */ package me.mohalk.banzem.features.command.commands;
/*    */ 
/*    */ import com.mojang.realmsclient.gui.ChatFormatting;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ import me.mohalk.banzem.features.modules.module.FriendSettings;
/*    */ import me.mohalk.banzem.util.Util;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraft.network.play.client.CPacketChatMessage;
/*    */ 
/*    */ public class FriendCommand
/*    */   extends Command {
/*    */   public FriendCommand() {
/* 16 */     super("friend", new String[] { "<add/del/name/clear>", "<name>" });
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(String[] commands) {
/* 21 */     if (commands.length == 1) {
/* 22 */       if (Banzem.friendManager.getFriends().isEmpty()) {
/* 23 */         sendMessage("You currently dont have any friends added.");
/*    */       } else {
/* 25 */         sendMessage("Friends: ");
/* 26 */         for (Map.Entry<String, UUID> entry : (Iterable<Map.Entry<String, UUID>>)Banzem.friendManager.getFriends().entrySet()) {
/* 27 */           sendMessage(entry.getKey());
/*    */         }
/*    */       } 
/*    */       return;
/*    */     } 
/* 32 */     if (commands.length == 2) {
/* 33 */       switch (commands[0]) {
/*    */         case "reset":
/* 35 */           Banzem.friendManager.onLoad();
/* 36 */           sendMessage("Friends got reset.");
/*    */           return;
/*    */       } 
/*    */       
/* 40 */       sendMessage(commands[0] + (Banzem.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
/*    */       return;
/*    */     } 
/* 43 */     if (commands.length >= 2) {
/* 44 */       switch (commands[0]) {
/*    */         case "add":
/* 46 */           Banzem.friendManager.addFriend(commands[1]);
/* 47 */           sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
/* 48 */           if (((Boolean)(FriendSettings.getInstance()).notify.getValue()).booleanValue()) {
/* 49 */             Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("/w " + commands[1] + " I just added you to my friends list on Charlie dana hack!"));
/*    */           }
/*    */           return;
/*    */         
/*    */         case "del":
/* 54 */           Banzem.friendManager.removeFriend(commands[1]);
/* 55 */           if (((Boolean)(FriendSettings.getInstance()).notify.getValue()).booleanValue()) {
/* 56 */             Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("/w " + commands[1] + " I just removed you from my friends list on Charlie dana hack!"));
/*    */           }
/* 58 */           sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
/*    */           return;
/*    */       } 
/*    */       
/* 62 */       sendMessage("Unknown Command, try friend add/del (name)");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\command\commands\FriendCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */