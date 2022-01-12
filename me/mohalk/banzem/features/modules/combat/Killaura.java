/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.DamageUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.inventory.ClickType;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Killaura extends Module {
/*  21 */   private final Timer timer = new Timer(); public static Entity target;
/*  22 */   public Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
/*  23 */   public Setting<Boolean> autoSwitch = register(new Setting("AutoSwitch", Boolean.valueOf(false)));
/*  24 */   public Setting<Boolean> delay = register(new Setting("Delay", Boolean.valueOf(true)));
/*  25 */   public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
/*  26 */   public Setting<Boolean> stay = register(new Setting("Stay", Boolean.valueOf(true), v -> ((Boolean)this.rotate.getValue()).booleanValue()));
/*  27 */   public Setting<Boolean> armorBreak = register(new Setting("ArmorBreak", Boolean.valueOf(false)));
/*  28 */   public Setting<Boolean> eating = register(new Setting("Eating", Boolean.valueOf(true)));
/*  29 */   public Setting<Boolean> onlySharp = register(new Setting("Axe/Sword", Boolean.valueOf(true)));
/*  30 */   public Setting<Boolean> teleport = register(new Setting("Teleport", Boolean.valueOf(false)));
/*  31 */   public Setting<Float> raytrace = register(new Setting("Raytrace", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F), v -> !((Boolean)this.teleport.getValue()).booleanValue(), "Wall Range."));
/*  32 */   public Setting<Float> teleportRange = register(new Setting("TpRange", Float.valueOf(15.0F), Float.valueOf(0.1F), Float.valueOf(50.0F), v -> ((Boolean)this.teleport.getValue()).booleanValue(), "Teleport Range."));
/*  33 */   public Setting<Boolean> lagBack = register(new Setting("LagBack", Boolean.valueOf(true), v -> ((Boolean)this.teleport.getValue()).booleanValue()));
/*  34 */   public Setting<Boolean> teekaydelay = register(new Setting("32kDelay", Boolean.valueOf(false)));
/*  35 */   public Setting<Integer> time32k = register(new Setting("32kTime", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(50)));
/*  36 */   public Setting<Integer> multi = register(new Setting("32kPackets", Integer.valueOf(2), v -> !((Boolean)this.teekaydelay.getValue()).booleanValue()));
/*  37 */   public Setting<Boolean> multi32k = register(new Setting("Multi32k", Boolean.valueOf(false)));
/*  38 */   public Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true)));
/*  39 */   public Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false)));
/*  40 */   public Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false)));
/*  41 */   public Setting<Boolean> vehicles = register(new Setting("Entities", Boolean.valueOf(false)));
/*  42 */   public Setting<Boolean> projectiles = register(new Setting("Projectiles", Boolean.valueOf(false)));
/*  43 */   public Setting<Boolean> tps = register(new Setting("TpsSync", Boolean.valueOf(true)));
/*  44 */   public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
/*  45 */   public Setting<Boolean> swing = register(new Setting("Swing", Boolean.valueOf(true)));
/*  46 */   public Setting<Boolean> sneak = register(new Setting("State", Boolean.valueOf(false)));
/*  47 */   public Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(true)));
/*  48 */   private final Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.CLOSEST));
/*  49 */   public Setting<Float> health = register(new Setting("Health", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.targetMode.getValue() == TargetMode.SMART)));
/*     */   
/*     */   public Killaura() {
/*  52 */     super("Killaura", "Kills aura.", Module.Category.COMBAT, true, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  57 */     if (!((Boolean)this.rotate.getValue()).booleanValue()) {
/*  58 */       doKillaura();
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
/*  64 */     if (event.getStage() == 0 && ((Boolean)this.rotate.getValue()).booleanValue()) {
/*  65 */       if (((Boolean)this.stay.getValue()).booleanValue() && target != null) {
/*  66 */         Banzem.rotationManager.lookAtEntity(target);
/*     */       }
/*  68 */       doKillaura();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void doKillaura() {
/*  74 */     if (((Boolean)this.onlySharp.getValue()).booleanValue() && !EntityUtil.holdingWeapon((EntityPlayer)mc.field_71439_g)) {
/*  75 */       target = null;
/*     */       return;
/*     */     } 
/*  78 */     int i, wait = (!((Boolean)this.delay.getValue()).booleanValue() || (EntityUtil.holding32k((EntityPlayer)mc.field_71439_g) && !((Boolean)this.teekaydelay.getValue()).booleanValue())) ? 0 : (i = (int)(DamageUtil.getCooldownByWeapon((EntityPlayer)mc.field_71439_g) * (((Boolean)this.tps.getValue()).booleanValue() ? Banzem.serverManager.getTpsFactor() : 1.0F)));
/*  79 */     if (!this.timer.passedMs(wait) || (!((Boolean)this.eating.getValue()).booleanValue() && mc.field_71439_g.func_184587_cr() && (!mc.field_71439_g.func_184592_cb().func_77973_b().equals(Items.field_185159_cQ) || mc.field_71439_g.func_184600_cs() != EnumHand.OFF_HAND))) {
/*     */       return;
/*     */     }
/*  82 */     if (this.targetMode.getValue() != TargetMode.FOCUS || target == null || (mc.field_71439_g.func_70068_e(target) >= MathUtil.square(((Float)this.range.getValue()).floatValue()) && (!((Boolean)this.teleport.getValue()).booleanValue() || mc.field_71439_g.func_70068_e(target) >= MathUtil.square(((Float)this.teleportRange.getValue()).floatValue()))) || (!mc.field_71439_g.func_70685_l(target) && !EntityUtil.canEntityFeetBeSeen(target) && mc.field_71439_g.func_70068_e(target) >= MathUtil.square(((Float)this.raytrace.getValue()).floatValue()) && !((Boolean)this.teleport.getValue()).booleanValue())) {
/*  83 */       target = getTarget();
/*     */     }
/*  85 */     if (target == null)
/*     */       return; 
/*     */     int sword;
/*  88 */     if (((Boolean)this.autoSwitch.getValue()).booleanValue() && (sword = InventoryUtil.findHotbarBlock(ItemSword.class)) != -1) {
/*  89 */       InventoryUtil.switchToHotbarSlot(sword, false);
/*     */     }
/*  91 */     if (((Boolean)this.rotate.getValue()).booleanValue()) {
/*  92 */       Banzem.rotationManager.lookAtEntity(target);
/*     */     }
/*  94 */     if (((Boolean)this.teleport.getValue()).booleanValue()) {
/*  95 */       Banzem.positionManager.setPositionPacket(target.field_70165_t, EntityUtil.canEntityFeetBeSeen(target) ? target.field_70163_u : (target.field_70163_u + target.func_70047_e()), target.field_70161_v, true, true, !((Boolean)this.lagBack.getValue()).booleanValue());
/*     */     }
/*  97 */     if (EntityUtil.holding32k((EntityPlayer)mc.field_71439_g) && !((Boolean)this.teekaydelay.getValue()).booleanValue()) {
/*  98 */       if (((Boolean)this.multi32k.getValue()).booleanValue()) {
/*  99 */         for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 100 */           if (!EntityUtil.isValid((Entity)player, ((Float)this.range.getValue()).floatValue()))
/* 101 */             continue;  teekayAttack((Entity)player);
/*     */         } 
/*     */       } else {
/* 104 */         teekayAttack(target);
/*     */       } 
/* 106 */       this.timer.reset();
/*     */       return;
/*     */     } 
/* 109 */     if (((Boolean)this.armorBreak.getValue()).booleanValue()) {
/* 110 */       mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 9, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, (EntityPlayer)mc.field_71439_g);
/* 111 */       EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), ((Boolean)this.swing.getValue()).booleanValue());
/* 112 */       mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 9, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, (EntityPlayer)mc.field_71439_g);
/* 113 */       EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), ((Boolean)this.swing.getValue()).booleanValue());
/*     */     } else {
/* 115 */       boolean sneaking = mc.field_71439_g.func_70093_af();
/* 116 */       boolean sprint = mc.field_71439_g.func_70051_ag();
/* 117 */       if (((Boolean)this.sneak.getValue()).booleanValue()) {
/* 118 */         if (sneaking) {
/* 119 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
/*     */         }
/* 121 */         if (sprint) {
/* 122 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SPRINTING));
/*     */         }
/*     */       } 
/* 125 */       EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), ((Boolean)this.swing.getValue()).booleanValue());
/* 126 */       if (((Boolean)this.sneak.getValue()).booleanValue()) {
/* 127 */         if (sprint) {
/* 128 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SPRINTING));
/*     */         }
/* 130 */         if (sneaking) {
/* 131 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/*     */         }
/*     */       } 
/*     */     } 
/* 135 */     this.timer.reset();
/*     */   }
/*     */   
/*     */   private void teekayAttack(Entity entity) {
/* 139 */     for (int i = 0; i < ((Integer)this.multi.getValue()).intValue(); i++) {
/* 140 */       startEntityAttackThread(entity, i * ((Integer)this.time32k.getValue()).intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private void startEntityAttackThread(Entity entity, int time) {
/* 145 */     (new Thread(() -> {
/*     */           Timer timer = new Timer();
/*     */           timer.reset();
/*     */           try {
/*     */             Thread.sleep(time);
/* 150 */           } catch (InterruptedException ex) {
/*     */             Thread.currentThread().interrupt();
/*     */           } 
/*     */           EntityUtil.attackEntity(entity, true, ((Boolean)this.swing.getValue()).booleanValue());
/* 154 */         })).start();
/*     */   }
/*     */   
/*     */   private Entity getTarget() {
/* 158 */     Entity target = null;
/* 159 */     double distance = ((Boolean)this.teleport.getValue()).booleanValue() ? ((Float)this.teleportRange.getValue()).floatValue() : ((Float)this.range.getValue()).floatValue();
/* 160 */     double maxHealth = 36.0D;
/* 161 */     for (Entity entity : mc.field_71441_e.field_72996_f) {
/* 162 */       if (((!((Boolean)this.players.getValue()).booleanValue() || !(entity instanceof EntityPlayer)) && (!((Boolean)this.animals.getValue()).booleanValue() || !EntityUtil.isPassive(entity)) && (!((Boolean)this.mobs.getValue()).booleanValue() || !EntityUtil.isMobAggressive(entity)) && (!((Boolean)this.vehicles.getValue()).booleanValue() || !EntityUtil.isVehicle(entity)) && (!((Boolean)this.projectiles.getValue()).booleanValue() || !EntityUtil.isProjectile(entity))) || (entity instanceof net.minecraft.entity.EntityLivingBase && EntityUtil.isntValid(entity, distance)) || (!((Boolean)this.teleport.getValue()).booleanValue() && !mc.field_71439_g.func_70685_l(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && mc.field_71439_g.func_70068_e(entity) > MathUtil.square(((Float)this.raytrace.getValue()).floatValue())))
/*     */         continue; 
/* 164 */       if (target == null) {
/* 165 */         target = entity;
/* 166 */         distance = mc.field_71439_g.func_70068_e(entity);
/* 167 */         maxHealth = EntityUtil.getHealth(entity);
/*     */         continue;
/*     */       } 
/* 170 */       if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer)entity, 18)) {
/* 171 */         target = entity;
/*     */         break;
/*     */       } 
/* 174 */       if (this.targetMode.getValue() == TargetMode.SMART && EntityUtil.getHealth(entity) < ((Float)this.health.getValue()).floatValue()) {
/* 175 */         target = entity;
/*     */         break;
/*     */       } 
/* 178 */       if (this.targetMode.getValue() != TargetMode.HEALTH && mc.field_71439_g.func_70068_e(entity) < distance) {
/* 179 */         target = entity;
/* 180 */         distance = mc.field_71439_g.func_70068_e(entity);
/* 181 */         maxHealth = EntityUtil.getHealth(entity);
/*     */       } 
/* 183 */       if (this.targetMode.getValue() != TargetMode.HEALTH || EntityUtil.getHealth(entity) >= maxHealth)
/*     */         continue; 
/* 185 */       target = entity;
/* 186 */       distance = mc.field_71439_g.func_70068_e(entity);
/* 187 */       maxHealth = EntityUtil.getHealth(entity);
/*     */     } 
/* 189 */     return target;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/* 194 */     if (((Boolean)this.info.getValue()).booleanValue() && target instanceof EntityPlayer) {
/* 195 */       return target.func_70005_c_();
/*     */     }
/* 197 */     return null;
/*     */   }
/*     */   
/*     */   public enum TargetMode {
/* 201 */     FOCUS,
/* 202 */     CLOSEST,
/* 203 */     HEALTH,
/* 204 */     SMART;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\Killaura.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */