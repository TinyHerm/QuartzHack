/*     */ package me.mohalk.banzem.features.modules.misc;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.Managers;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.TextUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.network.play.client.CPacketChatMessage;
/*     */ import net.minecraft.network.play.server.SPacketChat;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.TextComponentString;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class ChatModifier
/*     */   extends Module {
/*  22 */   private static ChatModifier INSTANCE = new ChatModifier();
/*  23 */   private final Timer timer = new Timer();
/*  24 */   public Setting<Suffix> suffix = register(new Setting("Suffix", Suffix.NONE, "Your Suffix."));
/*  25 */   public Setting<Boolean> clean = register(new Setting("CleanChat", Boolean.valueOf(false), "Cleans your chat"));
/*  26 */   public Setting<Boolean> infinite = register(new Setting("Infinite", Boolean.valueOf(false), "Makes your chat infinite."));
/*  27 */   public Setting<Boolean> autoQMain = register(new Setting("AutoQMain", Boolean.valueOf(false), "Spams AutoQMain"));
/*  28 */   public Setting<Boolean> qNotification = register(new Setting("QNotification", Boolean.valueOf(false), v -> ((Boolean)this.autoQMain.getValue()).booleanValue()));
/*  29 */   public Setting<Integer> qDelay = register(new Setting("QDelay", Integer.valueOf(9), Integer.valueOf(1), Integer.valueOf(90), v -> ((Boolean)this.autoQMain.getValue()).booleanValue()));
/*  30 */   public Setting<TextUtil.Color> timeStamps = register(new Setting("Time", TextUtil.Color.NONE));
/*  31 */   public Setting<Boolean> rainbowTimeStamps = register(new Setting("RainbowTimeStamps", Boolean.valueOf(false), v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
/*  32 */   public Setting<TextUtil.Color> bracket = register(new Setting("Bracket", TextUtil.Color.WHITE, v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
/*  33 */   public Setting<Boolean> space = register(new Setting("Space", Boolean.valueOf(true), v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
/*  34 */   public Setting<Boolean> all = register(new Setting("All", Boolean.valueOf(false), v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
/*  35 */   public Setting<Boolean> shrug = register(new Setting("Shrug", Boolean.valueOf(false)));
/*     */   
/*     */   public ChatModifier() {
/*  38 */     super("ChatModifier", "Modifies your chat", Module.Category.MISC, true, false, false);
/*  39 */     setInstance();
/*     */   }
/*     */   
/*     */   public static ChatModifier getInstance() {
/*  43 */     if (INSTANCE == null) {
/*  44 */       INSTANCE = new ChatModifier();
/*     */     }
/*  46 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  50 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  55 */     if (((Boolean)this.shrug.getValue()).booleanValue()) {
/*  56 */       mc.field_71439_g.func_71165_d(TextUtil.shrug);
/*  57 */       this.shrug.setValue(Boolean.valueOf(false));
/*     */     } 
/*  59 */     if (((Boolean)this.autoQMain.getValue()).booleanValue()) {
/*  60 */       if (!shouldSendMessage((EntityPlayer)mc.field_71439_g)) {
/*     */         return;
/*     */       }
/*  63 */       if (((Boolean)this.qNotification.getValue()).booleanValue()) {
/*  64 */         Command.sendMessage("<AutoQueueMain> Sending message: /queue main");
/*     */       }
/*  66 */       mc.field_71439_g.func_71165_d("/queue main");
/*  67 */       this.timer.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketSend(PacketEvent.Send event) {
/*  73 */     if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
/*  74 */       CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
/*  75 */       String s = packet.func_149439_c();
/*  76 */       if (s.startsWith("/") || s.startsWith("!")) {
/*     */         return;
/*     */       }
/*  79 */       switch ((Suffix)this.suffix.getValue()) {
/*     */         case Banzem:
/*  81 */           s = s + " ⏐ QuartzHack";
/*     */           break;
/*     */       } 
/*     */ 
/*     */       
/*  86 */       if (s.length() >= 256) {
/*  87 */         s = s.substring(0, 256);
/*     */       }
/*  89 */       packet.field_149440_a = s;
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onChatPacketReceive(PacketEvent.Receive event) {
/*  95 */     if (event.getStage() != 0 || event.getPacket() instanceof SPacketChat);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceive(PacketEvent.Receive event) {
/* 102 */     if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
/* 103 */       if (!((SPacketChat)event.getPacket()).func_148916_d()) {
/*     */         return;
/*     */       }
/* 106 */       String originalMessage = ((SPacketChat)event.getPacket()).field_148919_a.func_150254_d();
/* 107 */       String message = getTimeString(originalMessage) + originalMessage;
/* 108 */       ((SPacketChat)event.getPacket()).field_148919_a = (ITextComponent)new TextComponentString(message);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getTimeString(String message) {
/* 113 */     String date = (new SimpleDateFormat("k:mm")).format(new Date());
/* 114 */     if (((Boolean)this.rainbowTimeStamps.getValue()).booleanValue()) {
/* 115 */       String timeString = "<" + date + ">" + (((Boolean)this.space.getValue()).booleanValue() ? " " : "");
/* 116 */       StringBuilder builder = new StringBuilder(timeString);
/* 117 */       builder.insert(0, "§+");
/* 118 */       if (!message.contains(Managers.getInstance().getRainbowCommandMessage())) {
/* 119 */         builder.append("§r");
/*     */       }
/* 121 */       return builder.toString();
/*     */     } 
/* 123 */     return ((this.bracket.getValue() == TextUtil.Color.NONE) ? "" : TextUtil.coloredString("<", (TextUtil.Color)this.bracket.getValue())) + TextUtil.coloredString(date, (TextUtil.Color)this.timeStamps.getValue()) + ((this.bracket.getValue() == TextUtil.Color.NONE) ? "" : TextUtil.coloredString(">", (TextUtil.Color)this.bracket.getValue())) + (((Boolean)this.space.getValue()).booleanValue() ? " " : "") + "§r";
/*     */   }
/*     */   
/*     */   private boolean shouldSendMessage(EntityPlayer player) {
/* 127 */     if (player.field_71093_bK != 1) {
/* 128 */       return false;
/*     */     }
/* 130 */     if (!this.timer.passedS(((Integer)this.qDelay.getValue()).intValue())) {
/* 131 */       return false;
/*     */     }
/* 133 */     return player.func_180425_c().equals(new Vec3i(0, 240, 0));
/*     */   }
/*     */   
/*     */   public enum Suffix {
/* 137 */     NONE,
/* 138 */     Banzem;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\ChatModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */