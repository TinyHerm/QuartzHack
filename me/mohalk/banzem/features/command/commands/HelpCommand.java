/*    */ package me.mohalk.banzem.features.command.commands;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ 
/*    */ public class HelpCommand
/*    */   extends Command {
/*    */   public HelpCommand() {
/*  9 */     super("commands");
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(String[] commands) {
/* 14 */     sendMessage("You can use following commands: ");
/* 15 */     for (Command command : Banzem.commandManager.getCommands())
/* 16 */       sendMessage(Banzem.commandManager.getPrefix() + command.getName()); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\command\commands\HelpCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */