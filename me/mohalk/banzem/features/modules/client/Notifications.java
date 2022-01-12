/*     */ package me.mohalk.banzem.features.modules.client;
/*     */ 
/*     */ import com.mojang.realmsclient.gui.ChatFormatting;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.ClientEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.module.ModuleTools;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.manager.FileManager;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.SoundEvents;
/*     */ import net.minecraft.network.play.server.SPacketSpawnObject;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.TextComponentString;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Notifications
/*     */   extends Module {
/*     */   private static final String fileName = "phobos/util/ModuleMessage_List.txt";
/*  26 */   private static final List<String> modules = new ArrayList<>();
/*  27 */   private static Notifications INSTANCE = new Notifications();
/*  28 */   private final Timer timer = new Timer();
/*  29 */   public Setting<Boolean> totemPops = register(new Setting("TotemPops", Boolean.valueOf(false)));
/*  30 */   public Setting<Boolean> totemNoti = register(new Setting("TotemNoti", Boolean.valueOf(true), v -> ((Boolean)this.totemPops.getValue()).booleanValue()));
/*  31 */   public Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(2000), Integer.valueOf(0), Integer.valueOf(5000), v -> ((Boolean)this.totemPops.getValue()).booleanValue(), "Delays messages."));
/*  32 */   public Setting<Boolean> clearOnLogout = register(new Setting("LogoutClear", Boolean.valueOf(false)));
/*  33 */   public Setting<Boolean> moduleMessage = register(new Setting("ModuleMessage", Boolean.valueOf(false)));
/*  34 */   public Setting<Boolean> list = register(new Setting("List", Boolean.valueOf(false), v -> ((Boolean)this.moduleMessage.getValue()).booleanValue()));
/*  35 */   public Setting<Boolean> watermark = register(new Setting("Watermark", Boolean.valueOf(true), v -> ((Boolean)this.moduleMessage.getValue()).booleanValue()));
/*  36 */   public Setting<Boolean> visualRange = register(new Setting("VisualRange", Boolean.valueOf(false)));
/*  37 */   public Setting<Boolean> VisualRangeSound = register(new Setting("VisualRangeSound", Boolean.valueOf(false)));
/*  38 */   public Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(true), v -> ((Boolean)this.visualRange.getValue()).booleanValue()));
/*  39 */   public Setting<Boolean> leaving = register(new Setting("Leaving", Boolean.valueOf(false), v -> ((Boolean)this.visualRange.getValue()).booleanValue()));
/*  40 */   public Setting<Boolean> pearls = register(new Setting("PearlNotifs", Boolean.valueOf(false)));
/*  41 */   public Setting<Boolean> crash = register(new Setting("Crash", Boolean.valueOf(false)));
/*  42 */   public Setting<Boolean> popUp = register(new Setting("PopUpVisualRange", Boolean.valueOf(false)));
/*  43 */   public Timer totemAnnounce = new Timer();
/*  44 */   private final Setting<Boolean> readfile = register(new Setting("LoadFile", Boolean.valueOf(false), v -> ((Boolean)this.moduleMessage.getValue()).booleanValue()));
/*  45 */   private List<EntityPlayer> knownPlayers = new ArrayList<>();
/*     */   private boolean check;
/*     */   
/*     */   public Notifications() {
/*  49 */     super("Notifications", "Sends Messages.", Module.Category.CLIENT, true, false, false);
/*  50 */     setInstance();
/*     */   }
/*     */   
/*     */   public static Notifications getInstance() {
/*  54 */     if (INSTANCE == null) {
/*  55 */       INSTANCE = new Notifications();
/*     */     }
/*  57 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   public static void displayCrash(Exception e) {
/*  61 */     Command.sendMessage("§cException caught: " + e.getMessage());
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  65 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLoad() {
/*  70 */     this.check = true;
/*  71 */     loadFile();
/*  72 */     this.check = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  77 */     this.knownPlayers = new ArrayList<>();
/*  78 */     if (!this.check) {
/*  79 */       loadFile();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  85 */     if (((Boolean)this.readfile.getValue()).booleanValue()) {
/*  86 */       if (!this.check) {
/*  87 */         Command.sendMessage("Loading File...");
/*  88 */         this.timer.reset();
/*  89 */         loadFile();
/*     */       } 
/*  91 */       this.check = true;
/*     */     } 
/*  93 */     if (this.check && this.timer.passedMs(750L)) {
/*  94 */       this.readfile.setValue(Boolean.valueOf(false));
/*  95 */       this.check = false;
/*     */     } 
/*  97 */     if (((Boolean)this.visualRange.getValue()).booleanValue()) {
/*  98 */       ArrayList<EntityPlayer> tickPlayerList = new ArrayList<>(mc.field_71441_e.field_73010_i);
/*  99 */       if (tickPlayerList.size() > 0) {
/* 100 */         for (EntityPlayer player : tickPlayerList) {
/* 101 */           if (player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) || this.knownPlayers.contains(player))
/*     */             continue; 
/* 103 */           this.knownPlayers.add(player);
/* 104 */           if (Banzem.friendManager.isFriend(player)) {
/* 105 */             Command.sendMessage("Player §a" + player.func_70005_c_() + "§r entered your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!") : "!"), ((Boolean)this.popUp.getValue()).booleanValue());
/*     */           } else {
/* 107 */             Command.sendMessage("Player §c" + player.func_70005_c_() + "§r entered your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!") : "!"), ((Boolean)this.popUp.getValue()).booleanValue());
/*     */           } 
/* 109 */           if (((Boolean)this.VisualRangeSound.getValue()).booleanValue()) {
/* 110 */             mc.field_71439_g.func_184185_a(SoundEvents.field_187689_f, 1.0F, 1.0F);
/*     */           }
/*     */           return;
/*     */         } 
/*     */       }
/* 115 */       if (this.knownPlayers.size() > 0) {
/* 116 */         for (EntityPlayer player : this.knownPlayers) {
/* 117 */           if (tickPlayerList.contains(player))
/* 118 */             continue;  this.knownPlayers.remove(player);
/* 119 */           if (((Boolean)this.leaving.getValue()).booleanValue()) {
/* 120 */             if (Banzem.friendManager.isFriend(player)) {
/* 121 */               Command.sendMessage("Player §a" + player.func_70005_c_() + "§r left your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!") : "!"), ((Boolean)this.popUp.getValue()).booleanValue());
/*     */             } else {
/* 123 */               Command.sendMessage("Player §c" + player.func_70005_c_() + "§r left your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!") : "!"), ((Boolean)this.popUp.getValue()).booleanValue());
/*     */             } 
/*     */           }
/*     */           return;
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void loadFile() {
/* 133 */     List<String> fileInput = FileManager.readTextFileAllLines("phobos/util/ModuleMessage_List.txt");
/* 134 */     Iterator<String> i = fileInput.iterator();
/* 135 */     modules.clear();
/* 136 */     while (i.hasNext()) {
/* 137 */       String s = i.next();
/* 138 */       if (s.replaceAll("\\s", "").isEmpty())
/* 139 */         continue;  modules.add(s);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onReceivePacket(PacketEvent.Receive event) {
/* 145 */     if (event.getPacket() instanceof SPacketSpawnObject && ((Boolean)this.pearls.getValue()).booleanValue()) {
/* 146 */       SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
/* 147 */       EntityPlayer player = mc.field_71441_e.func_184137_a(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e(), 1.0D, false);
/* 148 */       if (player == null) {
/*     */         return;
/*     */       }
/* 151 */       if (packet.func_149001_c() == 85) {
/* 152 */         Command.sendMessage("§cPearl thrown by " + player.func_70005_c_() + " at X:" + (int)packet.func_186880_c() + " Y:" + (int)packet.func_186882_d() + " Z:" + (int)packet.func_186881_e());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public TextComponentString getNotifierOn(Module module) {
/* 159 */     if (ModuleTools.getInstance().isEnabled()) {
/* 160 */       TextComponentString textComponentString; switch ((ModuleTools.Notifier)(ModuleTools.getInstance()).notifier.getValue()) {
/*     */         case FUTURE:
/* 162 */           textComponentString = new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + module.getDisplayName() + " toggled " + ChatFormatting.GREEN + "on" + ChatFormatting.GRAY + ".");
/* 163 */           return textComponentString;
/*     */         
/*     */         case DOTGOD:
/* 166 */           textComponentString = new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + module.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.GREEN + "enabled.");
/* 167 */           return textComponentString;
/*     */ 
/*     */         
/*     */         case PHOBOS:
/* 171 */           textComponentString = new TextComponentString(Banzem.commandManager.getClientMessage() + ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + " enabled.");
/* 172 */           return textComponentString;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 177 */     TextComponentString text = new TextComponentString(Banzem.commandManager.getClientMessage() + ChatFormatting.GREEN + module.getDisplayName() + " toggled on.");
/* 178 */     return text;
/*     */   }
/*     */   
/*     */   public TextComponentString getNotifierOff(Module module) {
/* 182 */     if (ModuleTools.getInstance().isEnabled()) {
/* 183 */       TextComponentString textComponentString; switch ((ModuleTools.Notifier)(ModuleTools.getInstance()).notifier.getValue()) {
/*     */         case FUTURE:
/* 185 */           textComponentString = new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + module.getDisplayName() + " toggled " + ChatFormatting.RED + "off" + ChatFormatting.GRAY + ".");
/* 186 */           return textComponentString;
/*     */         
/*     */         case DOTGOD:
/* 189 */           textComponentString = new TextComponentString(ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.DARK_AQUA + module.getDisplayName() + ChatFormatting.LIGHT_PURPLE + " was " + ChatFormatting.RED + "disabled.");
/* 190 */           return textComponentString;
/*     */ 
/*     */         
/*     */         case PHOBOS:
/* 194 */           textComponentString = new TextComponentString(Banzem.commandManager.getClientMessage() + ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + " disabled.");
/* 195 */           return textComponentString;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 200 */     TextComponentString text = new TextComponentString(Banzem.commandManager.getClientMessage() + ChatFormatting.RED + module.getDisplayName() + " toggled off.");
/* 201 */     return text;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onToggleModule(ClientEvent event) {
/* 208 */     if (!((Boolean)this.moduleMessage.getValue()).booleanValue())
/*     */       return; 
/*     */     Module module;
/* 211 */     if (event.getStage() == 0 && !(module = (Module)event.getFeature()).equals(this) && (modules.contains(module.getDisplayName()) || !((Boolean)this.list.getValue()).booleanValue())) {
/* 212 */       int moduleNumber = 0;
/* 213 */       for (char character : module.getDisplayName().toCharArray()) {
/* 214 */         moduleNumber += character;
/* 215 */         moduleNumber *= 10;
/*     */       } 
/* 217 */       mc.field_71456_v.func_146158_b().func_146234_a((ITextComponent)getNotifierOff(module), moduleNumber);
/*     */     } 
/*     */     
/* 220 */     if (event.getStage() == 1 && (modules.contains((module = (Module)event.getFeature()).getDisplayName()) || !((Boolean)this.list.getValue()).booleanValue())) {
/* 221 */       int moduleNumber = 0;
/* 222 */       for (char character : module.getDisplayName().toCharArray()) {
/* 223 */         moduleNumber += character;
/* 224 */         moduleNumber *= 10;
/*     */       } 
/* 226 */       mc.field_71456_v.func_146158_b().func_146234_a((ITextComponent)getNotifierOn(module), moduleNumber);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\Notifications.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */