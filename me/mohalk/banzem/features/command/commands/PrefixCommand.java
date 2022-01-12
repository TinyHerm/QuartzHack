/*    */ package me.mohalk.banzem.features.command.commands;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*    */ 
/*    */ public class PrefixCommand
/*    */   extends Command {
/*    */   public PrefixCommand() {
/* 10 */     super("prefix", new String[] { "<char>" });
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(String[] commands) {
/* 15 */     if (commands.length == 1) {
/* 16 */       Command.sendMessage("§cSpecify a new prefix.");
/*    */       return;
/*    */     } 
/* 19 */     ((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).prefix.setValue(commands[0]);
/* 20 */     Command.sendMessage("Prefix set to §a" + Banzem.commandManager.getPrefix());
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\command\commands\PrefixCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */