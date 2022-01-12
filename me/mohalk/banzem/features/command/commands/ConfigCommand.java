/*    */ package me.mohalk.banzem.features.command.commands;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ import java.util.stream.Collectors;
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ 
/*    */ public class ConfigCommand
/*    */   extends Command
/*    */ {
/*    */   public ConfigCommand() {
/* 14 */     super("config", new String[] { "<save/load>" });
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(String[] commands) {
/* 19 */     if (commands.length == 1) {
/* 20 */       sendMessage("You`ll find the config files in your gameProfile directory under QuartzHack/config");
/*    */       return;
/*    */     } 
/* 23 */     if (commands.length == 2) {
/* 24 */       if ("list".equals(commands[0])) {
/* 25 */         String configs = "Configs: ";
/* 26 */         File file = new File("QuartzHack/");
/* 27 */         List<File> directories = (List<File>)Arrays.<File>stream(file.listFiles()).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());
/* 28 */         StringBuilder builder = new StringBuilder(configs);
/* 29 */         for (File file1 : directories) {
/* 30 */           builder.append(file1.getName() + ", ");
/*    */         }
/* 32 */         configs = builder.toString();
/* 33 */         sendMessage("§a" + configs);
/*    */       } else {
/* 35 */         sendMessage("§cNot a valid command... Possible usage: <list>");
/*    */       } 
/*    */     }
/* 38 */     if (commands.length >= 3) {
/* 39 */       switch (commands[0]) {
/*    */         case "save":
/* 41 */           Banzem.configManager.saveConfig(commands[1]);
/* 42 */           sendMessage("§aConfig has been saved.");
/*    */           return;
/*    */         
/*    */         case "load":
/* 46 */           Banzem.moduleManager.onUnload();
/* 47 */           Banzem.configManager.loadConfig(commands[1]);
/* 48 */           Banzem.moduleManager.onLoad();
/* 49 */           sendMessage("§aConfig has been loaded.");
/*    */           return;
/*    */       } 
/*    */       
/* 53 */       sendMessage("§cNot a valid command... Possible usage: <save/load>");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\command\commands\ConfigCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */