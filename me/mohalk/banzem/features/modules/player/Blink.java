/*     */ package me.mohalk.banzem.features.modules.player;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.client.entity.EntityOtherPlayerMP;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Blink
/*     */   extends Module
/*     */ {
/*  20 */   private static Blink INSTANCE = new Blink();
/*  21 */   private final Timer timer = new Timer();
/*  22 */   private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
/*  23 */   public Setting<Boolean> cPacketPlayer = register(new Setting("CPacketPlayer", Boolean.valueOf(true)));
/*  24 */   public Setting<Mode> autoOff = register(new Setting("AutoOff", Mode.MANUAL));
/*  25 */   public Setting<Integer> timeLimit = register(new Setting("Time", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> (this.autoOff.getValue() == Mode.TIME)));
/*  26 */   public Setting<Integer> packetLimit = register(new Setting("Packets", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> (this.autoOff.getValue() == Mode.PACKETS)));
/*  27 */   public Setting<Float> distance = register(new Setting("Distance", Float.valueOf(10.0F), Float.valueOf(1.0F), Float.valueOf(100.0F), v -> (this.autoOff.getValue() == Mode.DISTANCE)));
/*     */   
/*     */   private EntityOtherPlayerMP entity;
/*     */   private int packetsCanceled;
/*     */   private BlockPos startPos;
/*     */   
/*     */   public Blink() {
/*  34 */     super("Blink", "Fakelag.", Module.Category.PLAYER, true, false, false);
/*  35 */     setInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   public static Blink getInstance() {
/*  40 */     if (INSTANCE == null) {
/*  41 */       INSTANCE = new Blink();
/*     */     }
/*  43 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   private void setInstance() {
/*  48 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  54 */     if (!fullNullCheck()) {
/*  55 */       this.entity = new EntityOtherPlayerMP((World)mc.field_71441_e, mc.field_71449_j.func_148256_e());
/*  56 */       this.entity.func_82149_j((Entity)mc.field_71439_g);
/*  57 */       this.entity.field_70177_z = mc.field_71439_g.field_70177_z;
/*  58 */       this.entity.field_70759_as = mc.field_71439_g.field_70759_as;
/*  59 */       this.entity.field_71071_by.func_70455_b(mc.field_71439_g.field_71071_by);
/*  60 */       mc.field_71441_e.func_73027_a(6942069, (Entity)this.entity);
/*  61 */       this.startPos = mc.field_71439_g.func_180425_c();
/*     */     } else {
/*  63 */       disable();
/*     */     } 
/*  65 */     this.packetsCanceled = 0;
/*  66 */     this.timer.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  72 */     if (nullCheck() || (this.autoOff.getValue() == Mode.TIME && this.timer.passedS(((Integer)this.timeLimit.getValue()).intValue())) || (this.autoOff.getValue() == Mode.DISTANCE && this.startPos != null && mc.field_71439_g.func_174818_b(this.startPos) >= MathUtil.square(((Float)this.distance.getValue()).floatValue())) || (this.autoOff.getValue() == Mode.PACKETS && this.packetsCanceled >= ((Integer)this.packetLimit.getValue()).intValue())) {
/*  73 */       disable();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onLogout() {
/*  80 */     if (isOn()) {
/*  81 */       disable();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSendPacket(PacketEvent.Send event) {
/*  88 */     if (event.getStage() == 0 && mc.field_71441_e != null && !mc.func_71356_B()) {
/*  89 */       Object packet = event.getPacket();
/*  90 */       if (((Boolean)this.cPacketPlayer.getValue()).booleanValue() && packet instanceof net.minecraft.network.play.client.CPacketPlayer) {
/*  91 */         event.setCanceled(true);
/*  92 */         this.packets.add((Packet)packet);
/*  93 */         this.packetsCanceled++;
/*     */       } 
/*  95 */       if (!((Boolean)this.cPacketPlayer.getValue()).booleanValue()) {
/*  96 */         if (packet instanceof net.minecraft.network.play.client.CPacketChatMessage || packet instanceof net.minecraft.network.play.client.CPacketConfirmTeleport || packet instanceof net.minecraft.network.play.client.CPacketKeepAlive || packet instanceof net.minecraft.network.play.client.CPacketTabComplete || packet instanceof net.minecraft.network.play.client.CPacketClientStatus) {
/*     */           return;
/*     */         }
/*  99 */         this.packets.add((Packet)packet);
/* 100 */         event.setCanceled(true);
/* 101 */         this.packetsCanceled++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 109 */     if (!fullNullCheck()) {
/* 110 */       mc.field_71441_e.func_72900_e((Entity)this.entity);
/* 111 */       while (!this.packets.isEmpty()) {
/* 112 */         mc.field_71439_g.field_71174_a.func_147297_a(this.packets.poll());
/*     */       }
/*     */     } 
/* 115 */     this.startPos = null;
/*     */   }
/*     */   
/*     */   public enum Mode
/*     */   {
/* 120 */     MANUAL,
/* 121 */     TIME,
/* 122 */     DISTANCE,
/* 123 */     PACKETS;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\Blink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */