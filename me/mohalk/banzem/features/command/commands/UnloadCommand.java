/*    */ package me.mohalk.banzem.features.command.commands;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ 
/*    */ public class UnloadCommand
/*    */   extends Command {
/*    */   public UnloadCommand() {
/*  9 */     super("unload", new String[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(String[] commands) {
/* 14 */     Banzem.unload(true);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\command\commands\UnloadCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */