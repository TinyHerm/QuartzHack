/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.PushEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.projectile.EntityFishHook;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.network.play.server.SPacketEntityStatus;
/*     */ import net.minecraft.network.play.server.SPacketEntityVelocity;
/*     */ import net.minecraft.network.play.server.SPacketExplosion;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Velocity extends Module {
/*  17 */   private static Velocity INSTANCE = new Velocity();
/*  18 */   public Setting<Boolean> knockBack = register(new Setting("KnockBack", Boolean.valueOf(true)));
/*  19 */   public Setting<Boolean> noPush = register(new Setting("NoPush", Boolean.valueOf(true)));
/*  20 */   public Setting<Float> horizontal = register(new Setting("Horizontal", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(100.0F)));
/*  21 */   public Setting<Float> vertical = register(new Setting("Vertical", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(100.0F)));
/*  22 */   public Setting<Boolean> explosions = register(new Setting("Explosions", Boolean.valueOf(true)));
/*  23 */   public Setting<Boolean> bobbers = register(new Setting("Bobbers", Boolean.valueOf(true)));
/*  24 */   public Setting<Boolean> water = register(new Setting("Water", Boolean.valueOf(false)));
/*  25 */   public Setting<Boolean> blocks = register(new Setting("Blocks", Boolean.valueOf(false)));
/*  26 */   public Setting<Boolean> ice = register(new Setting("Ice", Boolean.valueOf(false)));
/*     */   
/*     */   public Velocity() {
/*  29 */     super("Velocity", "Allows you to control your velocity", Module.Category.MOVEMENT, true, false, false);
/*  30 */     setInstance();
/*     */   }
/*     */   
/*     */   public static Velocity getINSTANCE() {
/*  34 */     if (INSTANCE == null) {
/*  35 */       INSTANCE = new Velocity();
/*     */     }
/*  37 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  41 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  46 */     if (((Boolean)this.ice.getValue()).booleanValue()) {
/*  47 */       Blocks.field_150432_aD.field_149765_K = 0.6F;
/*  48 */       Blocks.field_150403_cj.field_149765_K = 0.6F;
/*  49 */       Blocks.field_185778_de.field_149765_K = 0.6F;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  55 */     Blocks.field_150432_aD.field_149765_K = 0.98F;
/*  56 */     Blocks.field_150403_cj.field_149765_K = 0.98F;
/*  57 */     Blocks.field_185778_de.field_149765_K = 0.98F;
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceived(PacketEvent.Receive event) {
/*  62 */     if (event.getStage() == 0 && mc.field_71439_g != null) {
/*     */       SPacketEntityVelocity velocity;
/*     */ 
/*     */       
/*  66 */       if (((Boolean)this.knockBack.getValue()).booleanValue() && event.getPacket() instanceof SPacketEntityVelocity && (velocity = (SPacketEntityVelocity)event.getPacket()).func_149412_c() == mc.field_71439_g.field_145783_c) {
/*  67 */         if (((Float)this.horizontal.getValue()).floatValue() == 0.0F && ((Float)this.vertical.getValue()).floatValue() == 0.0F) {
/*  68 */           event.setCanceled(true);
/*     */           return;
/*     */         } 
/*  71 */         velocity.field_149415_b = (int)(velocity.field_149415_b * ((Float)this.horizontal.getValue()).floatValue());
/*  72 */         velocity.field_149416_c = (int)(velocity.field_149416_c * ((Float)this.vertical.getValue()).floatValue());
/*  73 */         velocity.field_149414_d = (int)(velocity.field_149414_d * ((Float)this.horizontal.getValue()).floatValue());
/*     */       }  Entity entity; SPacketEntityStatus packet;
/*  75 */       if (event.getPacket() instanceof SPacketEntityStatus && ((Boolean)this.bobbers.getValue()).booleanValue() && (packet = (SPacketEntityStatus)event.getPacket()).func_149160_c() == 31 && entity = packet.func_149161_a((World)mc.field_71441_e) instanceof EntityFishHook) {
/*  76 */         EntityFishHook fishHook = (EntityFishHook)entity;
/*  77 */         if (fishHook.field_146043_c == mc.field_71439_g) {
/*  78 */           event.setCanceled(true);
/*     */         }
/*     */       } 
/*  81 */       if (((Boolean)this.explosions.getValue()).booleanValue() && event.getPacket() instanceof SPacketExplosion) {
/*     */         
/*  83 */         SPacketExplosion velocity_ = (SPacketExplosion)event.getPacket();
/*  84 */         velocity_.field_149152_f *= ((Float)this.horizontal.getValue()).floatValue();
/*  85 */         velocity_.field_149153_g *= ((Float)this.vertical.getValue()).floatValue();
/*  86 */         velocity_.field_149159_h *= ((Float)this.horizontal.getValue()).floatValue();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPush(PushEvent event) {
/*  93 */     if (event.getStage() == 0 && ((Boolean)this.noPush.getValue()).booleanValue() && event.entity.equals(mc.field_71439_g)) {
/*  94 */       if (((Float)this.horizontal.getValue()).floatValue() == 0.0F && ((Float)this.vertical.getValue()).floatValue() == 0.0F) {
/*  95 */         event.setCanceled(true);
/*     */         return;
/*     */       } 
/*  98 */       event.x = -event.x * ((Float)this.horizontal.getValue()).floatValue();
/*  99 */       event.y = -event.y * ((Float)this.vertical.getValue()).floatValue();
/* 100 */       event.z = -event.z * ((Float)this.horizontal.getValue()).floatValue();
/* 101 */     } else if (event.getStage() == 1 && ((Boolean)this.blocks.getValue()).booleanValue()) {
/* 102 */       event.setCanceled(true);
/* 103 */     } else if (event.getStage() == 2 && ((Boolean)this.water.getValue()).booleanValue() && mc.field_71439_g != null && mc.field_71439_g.equals(event.entity)) {
/* 104 */       event.setCanceled(true);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\Velocity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */