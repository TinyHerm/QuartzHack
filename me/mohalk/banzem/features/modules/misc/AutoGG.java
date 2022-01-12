/*     */ package me.mohalk.banzem.features.modules.misc;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.DeathEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.combat.AutoCrystal;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.manager.FileManager;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketChatMessage;
/*     */ import net.minecraft.network.play.client.CPacketUseEntity;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.event.entity.player.AttackEntityEvent;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class AutoGG extends Module {
/*     */   private static final String path = "banzem/autogg.txt";
/*  29 */   private final Setting<Boolean> onOwnDeath = register(new Setting("OwnDeath", Boolean.valueOf(false)));
/*  30 */   private final Setting<Boolean> greentext = register(new Setting("Greentext", Boolean.valueOf(false)));
/*  31 */   private final Setting<Boolean> loadFiles = register(new Setting("LoadFiles", Boolean.valueOf(false)));
/*  32 */   private final Setting<Integer> targetResetTimer = register(new Setting("Reset", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(90)));
/*  33 */   private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(30)));
/*  34 */   private final Setting<Boolean> test = register(new Setting("Test", Boolean.valueOf(false)));
/*  35 */   public Map<EntityPlayer, Integer> targets = new ConcurrentHashMap<>();
/*  36 */   public List<String> messages = new ArrayList<>();
/*     */   public EntityPlayer cauraTarget;
/*  38 */   private final Timer timer = new Timer();
/*  39 */   private final Timer cooldownTimer = new Timer();
/*     */   private boolean cooldown;
/*     */   
/*     */   public AutoGG() {
/*  43 */     super("AutoGG", "Automatically GGs", Module.Category.MISC, true, false, false);
/*  44 */     File file = new File("QuartzHack/autogg.txt");
/*  45 */     if (!file.exists()) {
/*     */       try {
/*  47 */         file.createNewFile();
/*  48 */       } catch (Exception e) {
/*  49 */         e.printStackTrace();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  56 */     loadMessages();
/*  57 */     this.timer.reset();
/*  58 */     this.cooldownTimer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  63 */     if (((Boolean)this.loadFiles.getValue()).booleanValue()) {
/*  64 */       loadMessages();
/*  65 */       Command.sendMessage("<AutoGG> Loaded messages.");
/*  66 */       this.loadFiles.setValue(Boolean.valueOf(false));
/*     */     } 
/*  68 */     if (AutoCrystal.target != null && this.cauraTarget != AutoCrystal.target) {
/*  69 */       this.cauraTarget = AutoCrystal.target;
/*     */     }
/*  71 */     if (((Boolean)this.test.getValue()).booleanValue()) {
/*  72 */       announceDeath((EntityPlayer)mc.field_71439_g);
/*  73 */       this.test.setValue(Boolean.valueOf(false));
/*     */     } 
/*  75 */     if (!this.cooldown) {
/*  76 */       this.cooldownTimer.reset();
/*     */     }
/*  78 */     if (this.cooldownTimer.passedS(((Integer)this.delay.getValue()).intValue()) && this.cooldown) {
/*  79 */       this.cooldown = false;
/*  80 */       this.cooldownTimer.reset();
/*     */     } 
/*  82 */     if (AutoCrystal.target != null) {
/*  83 */       this.targets.put(AutoCrystal.target, Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L)));
/*     */     }
/*  85 */     this.targets.replaceAll((p, v) -> Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L)));
/*  86 */     for (EntityPlayer player : this.targets.keySet()) {
/*  87 */       if (((Integer)this.targets.get(player)).intValue() <= ((Integer)this.targetResetTimer.getValue()).intValue())
/*  88 */         continue;  this.targets.remove(player);
/*  89 */       this.timer.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onEntityDeath(DeathEvent event) {
/*  95 */     if (this.targets.containsKey(event.player) && !this.cooldown) {
/*  96 */       announceDeath(event.player);
/*  97 */       this.cooldown = true;
/*  98 */       this.targets.remove(event.player);
/*     */     } 
/* 100 */     if (event.player == this.cauraTarget && !this.cooldown) {
/* 101 */       announceDeath(event.player);
/* 102 */       this.cooldown = true;
/*     */     } 
/* 104 */     if (event.player == mc.field_71439_g && ((Boolean)this.onOwnDeath.getValue()).booleanValue()) {
/* 105 */       announceDeath(event.player);
/* 106 */       this.cooldown = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onAttackEntity(AttackEntityEvent event) {
/* 112 */     if (event.getTarget() instanceof EntityPlayer && !Banzem.friendManager.isFriend(event.getEntityPlayer())) {
/* 113 */       this.targets.put((EntityPlayer)event.getTarget(), Integer.valueOf(0));
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSendAttackPacket(PacketEvent.Send event) {
/*     */     CPacketUseEntity packet;
/* 120 */     if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && packet.func_149564_a((World)mc.field_71441_e) instanceof EntityPlayer && !Banzem.friendManager.isFriend((EntityPlayer)packet.func_149564_a((World)mc.field_71441_e))) {
/* 121 */       this.targets.put((EntityPlayer)packet.func_149564_a((World)mc.field_71441_e), Integer.valueOf(0));
/*     */     }
/*     */   }
/*     */   
/*     */   public void loadMessages() {
/* 126 */     this.messages = FileManager.readTextFileAllLines("QuartzHack/autogg.txt");
/*     */   }
/*     */   
/*     */   public String getRandomMessage() {
/* 130 */     loadMessages();
/* 131 */     Random rand = new Random();
/* 132 */     if (this.messages.size() == 0) {
/* 133 */       return "<player> is a noob hahaha fobus on tope";
/*     */     }
/* 135 */     if (this.messages.size() == 1) {
/* 136 */       return this.messages.get(0);
/*     */     }
/* 138 */     return this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1));
/*     */   }
/*     */   
/*     */   public void announceDeath(EntityPlayer target) {
/* 142 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage((((Boolean)this.greentext.getValue()).booleanValue() ? ">" : "") + getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\AutoGG.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */