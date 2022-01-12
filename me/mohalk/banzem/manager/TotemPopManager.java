/*     */ package me.mohalk.banzem.manager;
/*     */ 
/*     */ import com.mojang.realmsclient.gui.ChatFormatting;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.client.Notifications;
/*     */ import me.mohalk.banzem.features.modules.module.ModuleTools;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ 
/*     */ public class TotemPopManager
/*     */   extends Feature
/*     */ {
/*     */   private Notifications notifications;
/*  19 */   private Map<EntityPlayer, Integer> poplist = new ConcurrentHashMap<>();
/*  20 */   private final Set<EntityPlayer> toAnnounce = new HashSet<>();
/*     */   
/*     */   public void onUpdate() {
/*  23 */     if (this.notifications.totemAnnounce.passedMs(((Integer)this.notifications.delay.getValue()).intValue()) && this.notifications.isOn() && ((Boolean)this.notifications.totemPops.getValue()).booleanValue()) {
/*  24 */       for (EntityPlayer player : this.toAnnounce) {
/*  25 */         if (player == null)
/*  26 */           continue;  int playerNumber = 0;
/*  27 */         for (char character : player.func_70005_c_().toCharArray()) {
/*  28 */           playerNumber += character;
/*  29 */           playerNumber *= 10;
/*     */         } 
/*  31 */         Command.sendOverwriteMessage(pop(player), playerNumber, ((Boolean)this.notifications.totemNoti.getValue()).booleanValue());
/*  32 */         this.toAnnounce.remove(player);
/*  33 */         this.notifications.totemAnnounce.reset();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String pop(EntityPlayer player) {
/*  40 */     if (getTotemPops(player) == 1) {
/*  41 */       if (ModuleTools.getInstance().isEnabled()) {
/*  42 */         String text; switch ((ModuleTools.PopNotifier)(ModuleTools.getInstance()).popNotifier.getValue()) {
/*     */           case FUTURE:
/*  44 */             text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.GRAY + " totem.";
/*  45 */             return text;
/*     */           
/*     */           case PHOBOS:
/*  48 */             text = ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + getTotemPops(player) + ChatFormatting.RED + " totem.";
/*  49 */             return text;
/*     */           
/*     */           case DOTGOD:
/*  52 */             text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " has popped " + ChatFormatting.RED + getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " time in total!";
/*  53 */             return text;
/*     */           
/*     */           case NONE:
/*  56 */             return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totem.";
/*     */         } 
/*     */       
/*     */       } else {
/*  60 */         return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totem.";
/*     */       }
/*     */     
/*  63 */     } else if (ModuleTools.getInstance().isEnabled()) {
/*  64 */       String text; switch ((ModuleTools.PopNotifier)(ModuleTools.getInstance()).popNotifier.getValue()) {
/*     */         case FUTURE:
/*  66 */           text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.GRAY + " totems.";
/*  67 */           return text;
/*     */         
/*     */         case PHOBOS:
/*  70 */           text = ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + getTotemPops(player) + ChatFormatting.RED + " totems.";
/*  71 */           return text;
/*     */         
/*     */         case DOTGOD:
/*  74 */           text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " has popped " + ChatFormatting.RED + getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " times in total!";
/*  75 */           return text;
/*     */         
/*     */         case NONE:
/*  78 */           return ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totems.";
/*     */       } 
/*     */     
/*     */     } else {
/*  82 */       return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totems.";
/*     */     } 
/*     */     
/*  85 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLogout() {
/*  90 */     onOwnLogout(((Boolean)this.notifications.clearOnLogout.getValue()).booleanValue());
/*     */   }
/*     */   
/*     */   public void init() {
/*  94 */     this.notifications = Banzem.moduleManager.<Notifications>getModuleByClass(Notifications.class);
/*     */   }
/*     */   
/*     */   public void onTotemPop(EntityPlayer player) {
/*  98 */     popTotem(player);
/*  99 */     if (!player.equals(mc.field_71439_g)) {
/* 100 */       this.toAnnounce.add(player);
/* 101 */       this.notifications.totemAnnounce.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   public String death1(EntityPlayer player) {
/* 106 */     if (getTotemPops(player) == 1) {
/* 107 */       if (ModuleTools.getInstance().isEnabled()) {
/* 108 */         String text; switch ((ModuleTools.PopNotifier)(ModuleTools.getInstance()).popNotifier.getValue()) {
/*     */           case FUTURE:
/* 110 */             text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.GRAY + " totem.";
/* 111 */             return text;
/*     */           
/*     */           case PHOBOS:
/* 114 */             text = ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + getTotemPops(player) + ChatFormatting.RED + " totem.";
/* 115 */             return text;
/*     */           
/*     */           case DOTGOD:
/* 118 */             text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " time!";
/* 119 */             return text;
/*     */           
/*     */           case NONE:
/* 122 */             return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totem!";
/*     */         } 
/*     */ 
/*     */       
/*     */       } else {
/* 127 */         return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totem!";
/*     */       }
/*     */     
/*     */     }
/* 131 */     else if (ModuleTools.getInstance().isEnabled()) {
/* 132 */       String text; switch ((ModuleTools.PopNotifier)(ModuleTools.getInstance()).popNotifier.getValue()) {
/*     */         case FUTURE:
/* 134 */           text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.GRAY + " totems.";
/* 135 */           return text;
/*     */         
/*     */         case PHOBOS:
/* 138 */           text = ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + getTotemPops(player) + ChatFormatting.RED + " totems.";
/* 139 */           return text;
/*     */         
/*     */         case DOTGOD:
/* 142 */           text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " times!";
/* 143 */           return text;
/*     */         
/*     */         case NONE:
/* 146 */           return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totems!";
/*     */       } 
/*     */ 
/*     */     
/*     */     } else {
/* 151 */       return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + getTotemPops(player) + ChatFormatting.WHITE + " Totems!";
/*     */     } 
/*     */     
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDeath(EntityPlayer player) {
/* 159 */     if (getTotemPops(player) != 0 && !player.equals(mc.field_71439_g) && this.notifications.isOn() && ((Boolean)this.notifications.totemPops.getValue()).booleanValue()) {
/* 160 */       int playerNumber = 0;
/* 161 */       for (char character : player.func_70005_c_().toCharArray()) {
/* 162 */         playerNumber += character;
/* 163 */         playerNumber *= 10;
/*     */       } 
/* 165 */       Command.sendOverwriteMessage(death1(player), playerNumber, ((Boolean)this.notifications.totemNoti.getValue()).booleanValue());
/* 166 */       this.toAnnounce.remove(player);
/*     */     } 
/* 168 */     resetPops(player);
/*     */   }
/*     */   
/*     */   public void onLogout(EntityPlayer player, boolean clearOnLogout) {
/* 172 */     if (clearOnLogout) {
/* 173 */       resetPops(player);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onOwnLogout(boolean clearOnLogout) {
/* 178 */     if (clearOnLogout) {
/* 179 */       clearList();
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearList() {
/* 184 */     this.poplist = new ConcurrentHashMap<>();
/*     */   }
/*     */   
/*     */   public void resetPops(EntityPlayer player) {
/* 188 */     setTotemPops(player, 0);
/*     */   }
/*     */   
/*     */   public void popTotem(EntityPlayer player) {
/* 192 */     this.poplist.merge(player, Integer.valueOf(1), Integer::sum);
/*     */   }
/*     */   
/*     */   public void setTotemPops(EntityPlayer player, int amount) {
/* 196 */     this.poplist.put(player, Integer.valueOf(amount));
/*     */   }
/*     */   
/*     */   public int getTotemPops(EntityPlayer player) {
/* 200 */     Integer pops = this.poplist.get(player);
/* 201 */     if (pops == null) {
/* 202 */       return 0;
/*     */     }
/* 204 */     return pops.intValue();
/*     */   }
/*     */   
/*     */   public String getTotemPopString(EntityPlayer player) {
/* 208 */     return "§f" + ((getTotemPops(player) <= 0) ? "" : ("-" + getTotemPops(player) + " "));
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\TotemPopManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */