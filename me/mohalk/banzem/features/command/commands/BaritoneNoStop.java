/*    */ package me.mohalk.banzem.features.command.commands;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ 
/*    */ public class BaritoneNoStop
/*    */   extends Command {
/*    */   public BaritoneNoStop() {
/*  9 */     super("noStop", new String[] { "<prefix>", "<x>", "<y>", "<z>" });
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(String[] commands) {
/* 14 */     if (commands.length == 5) {
/* 15 */       Banzem.baritoneManager.setPrefix(commands[0]);
/* 16 */       int x = 0;
/* 17 */       int y = 0;
/* 18 */       int z = 0;
/*    */       try {
/* 20 */         x = Integer.parseInt(commands[1]);
/* 21 */         y = Integer.parseInt(commands[2]);
/* 22 */         z = Integer.parseInt(commands[3]);
/* 23 */       } catch (NumberFormatException e) {
/* 24 */         sendMessage("Invalid Input for x, y or z!");
/* 25 */         Banzem.baritoneManager.stop();
/*    */         return;
/*    */       } 
/* 28 */       Banzem.baritoneManager.start(x, y, z);
/*    */       return;
/*    */     } 
/* 31 */     sendMessage("Stoping Baritone-Nostop.");
/* 32 */     Banzem.baritoneManager.stop();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\command\commands\BaritoneNoStop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */