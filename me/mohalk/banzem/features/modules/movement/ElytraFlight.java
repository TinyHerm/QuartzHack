/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.MoveEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.inventory.EntityEquipmentSlot;
/*     */ import net.minecraft.item.ItemElytra;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketPlayer;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class ElytraFlight extends Module {
/*  24 */   private static ElytraFlight INSTANCE = new ElytraFlight();
/*  25 */   private final Timer timer = new Timer();
/*  26 */   private final Timer bypassTimer = new Timer();
/*  27 */   public Setting<Mode> mode = register(new Setting("Mode", Mode.FLY));
/*  28 */   public Setting<Integer> devMode = register(new Setting("Type", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(3), v -> (this.mode.getValue() == Mode.BYPASS || this.mode.getValue() == Mode.BETTER), "EventMode"));
/*  29 */   public Setting<Float> speed = register(new Setting("Speed", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.mode.getValue() != Mode.FLY && this.mode.getValue() != Mode.BOOST && this.mode.getValue() != Mode.BETTER && this.mode.getValue() != Mode.OHARE), "The Speed."));
/*  30 */   public Setting<Float> vSpeed = register(new Setting("VSpeed", Float.valueOf(0.3F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.mode.getValue() == Mode.BETTER || this.mode.getValue() == Mode.OHARE), "Vertical Speed"));
/*  31 */   public Setting<Float> hSpeed = register(new Setting("HSpeed", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.mode.getValue() == Mode.BETTER || this.mode.getValue() == Mode.OHARE), "Horizontal Speed"));
/*  32 */   public Setting<Float> glide = register(new Setting("Glide", Float.valueOf(1.0E-4F), Float.valueOf(0.0F), Float.valueOf(0.2F), v -> (this.mode.getValue() == Mode.BETTER), "Glide Speed"));
/*  33 */   public Setting<Float> tooBeeSpeed = register(new Setting("TooBeeSpeed", Float.valueOf(1.8000001F), Float.valueOf(1.0F), Float.valueOf(2.0F), v -> (this.mode.getValue() == Mode.TOOBEE), "Speed for flight on 2b2t"));
/*  34 */   public Setting<Boolean> autoStart = register(new Setting("AutoStart", Boolean.valueOf(true)));
/*  35 */   public Setting<Boolean> disableInLiquid = register(new Setting("NoLiquid", Boolean.valueOf(true)));
/*  36 */   public Setting<Boolean> infiniteDura = register(new Setting("InfiniteDura", Boolean.valueOf(false)));
/*  37 */   public Setting<Boolean> noKick = register(new Setting("NoKick", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKET)));
/*  38 */   public Setting<Boolean> allowUp = register(new Setting("AllowUp", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.BETTER)));
/*  39 */   public Setting<Boolean> lockPitch = register(new Setting("LockPitch", Boolean.valueOf(false)));
/*     */   private boolean vertical;
/*     */   private Double posX;
/*     */   private Double flyHeight;
/*     */   private Double posZ;
/*     */   
/*     */   public ElytraFlight() {
/*  46 */     super("ElytraFlight", "Makes Elytra Flight better.", Module.Category.MOVEMENT, true, false, false);
/*  47 */     setInstance();
/*     */   }
/*     */   
/*     */   public static ElytraFlight getInstance() {
/*  51 */     if (INSTANCE == null) {
/*  52 */       INSTANCE = new ElytraFlight();
/*     */     }
/*  54 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  58 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  63 */     if (this.mode.getValue() == Mode.BETTER && !((Boolean)this.autoStart.getValue()).booleanValue() && ((Integer)this.devMode.getValue()).intValue() == 1) {
/*  64 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/*     */     }
/*  66 */     this.flyHeight = null;
/*  67 */     this.posX = null;
/*  68 */     this.posZ = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/*  73 */     return this.mode.currentEnumName();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  78 */     if (this.mode.getValue() == Mode.BYPASS && ((Integer)this.devMode.getValue()).intValue() == 1 && mc.field_71439_g.func_184613_cA()) {
/*  79 */       mc.field_71439_g.field_70159_w = 0.0D;
/*  80 */       mc.field_71439_g.field_70181_x = -1.0E-4D;
/*  81 */       mc.field_71439_g.field_70179_y = 0.0D;
/*  82 */       double forwardInput = mc.field_71439_g.field_71158_b.field_192832_b;
/*  83 */       double strafeInput = mc.field_71439_g.field_71158_b.field_78902_a;
/*  84 */       double[] result = forwardStrafeYaw(forwardInput, strafeInput, mc.field_71439_g.field_70177_z);
/*  85 */       double forward = result[0];
/*  86 */       double strafe = result[1];
/*  87 */       double yaw = result[2];
/*  88 */       if (forwardInput != 0.0D || strafeInput != 0.0D) {
/*  89 */         mc.field_71439_g.field_70159_w = forward * ((Float)this.speed.getValue()).floatValue() * Math.cos(Math.toRadians(yaw + 90.0D)) + strafe * ((Float)this.speed.getValue()).floatValue() * Math.sin(Math.toRadians(yaw + 90.0D));
/*  90 */         mc.field_71439_g.field_70179_y = forward * ((Float)this.speed.getValue()).floatValue() * Math.sin(Math.toRadians(yaw + 90.0D)) - strafe * ((Float)this.speed.getValue()).floatValue() * Math.cos(Math.toRadians(yaw + 90.0D));
/*     */       } 
/*  92 */       if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/*  93 */         mc.field_71439_g.field_70181_x = -1.0D;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSendPacket(PacketEvent.Send event) {
/* 101 */     if (event.getPacket() instanceof CPacketPlayer && this.mode.getValue() == Mode.TOOBEE) {
/* 102 */       CPacketPlayer packet = (CPacketPlayer)event.getPacket();
/* 103 */       if (mc.field_71439_g.func_184613_cA());
/*     */     } 
/*     */ 
/*     */     
/* 107 */     if (event.getPacket() instanceof CPacketPlayer && this.mode.getValue() == Mode.TOOBEEBYPASS) {
/* 108 */       CPacketPlayer packet = (CPacketPlayer)event.getPacket();
/* 109 */       if (mc.field_71439_g.func_184613_cA());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onMove(MoveEvent event) {
/* 117 */     if (this.mode.getValue() == Mode.OHARE) {
/* 118 */       ItemStack itemstack = mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST);
/* 119 */       if (itemstack.func_77973_b() == Items.field_185160_cR && ItemElytra.func_185069_d(itemstack) && mc.field_71439_g.func_184613_cA()) {
/* 120 */         event.setY(mc.field_71474_y.field_74314_A.func_151470_d() ? ((Float)this.vSpeed.getValue()).floatValue() : (mc.field_71474_y.field_74311_E.func_151470_d() ? -((Float)this.vSpeed.getValue()).floatValue() : 0.0D));
/* 121 */         mc.field_71439_g.func_70024_g(0.0D, mc.field_71474_y.field_74314_A.func_151470_d() ? ((Float)this.vSpeed.getValue()).floatValue() : (mc.field_71474_y.field_74311_E.func_151470_d() ? -((Float)this.vSpeed.getValue()).floatValue() : 0.0D), 0.0D);
/* 122 */         mc.field_71439_g.field_184835_a = 0.0F;
/* 123 */         mc.field_71439_g.field_184836_b = 0.0F;
/* 124 */         mc.field_71439_g.field_184837_c = 0.0F;
/* 125 */         mc.field_71439_g.field_70701_bs = mc.field_71474_y.field_74314_A.func_151470_d() ? ((Float)this.vSpeed.getValue()).floatValue() : (mc.field_71474_y.field_74311_E.func_151470_d() ? -((Float)this.vSpeed.getValue()).floatValue() : 0.0F);
/* 126 */         double forward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 127 */         double strafe = mc.field_71439_g.field_71158_b.field_78902_a;
/* 128 */         float yaw = mc.field_71439_g.field_70177_z;
/* 129 */         if (forward == 0.0D && strafe == 0.0D) {
/* 130 */           event.setX(0.0D);
/* 131 */           event.setZ(0.0D);
/*     */         } else {
/* 133 */           if (forward != 0.0D) {
/* 134 */             if (strafe > 0.0D) {
/* 135 */               yaw += ((forward > 0.0D) ? -45 : 45);
/* 136 */             } else if (strafe < 0.0D) {
/* 137 */               yaw += ((forward > 0.0D) ? 45 : -45);
/*     */             } 
/* 139 */             strafe = 0.0D;
/* 140 */             if (forward > 0.0D) {
/* 141 */               forward = 1.0D;
/* 142 */             } else if (forward < 0.0D) {
/* 143 */               forward = -1.0D;
/*     */             } 
/*     */           } 
/* 146 */           double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
/* 147 */           double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
/* 148 */           event.setX(forward * ((Float)this.hSpeed.getValue()).floatValue() * cos + strafe * ((Float)this.hSpeed.getValue()).floatValue() * sin);
/* 149 */           event.setZ(forward * ((Float)this.hSpeed.getValue()).floatValue() * sin - strafe * ((Float)this.hSpeed.getValue()).floatValue() * cos);
/*     */         } 
/*     */       } 
/* 152 */     } else if (event.getStage() == 0 && this.mode.getValue() == Mode.BYPASS && ((Integer)this.devMode.getValue()).intValue() == 3) {
/* 153 */       if (mc.field_71439_g.func_184613_cA()) {
/* 154 */         event.setX(0.0D);
/* 155 */         event.setY(-1.0E-4D);
/* 156 */         event.setZ(0.0D);
/* 157 */         double forwardInput = mc.field_71439_g.field_71158_b.field_192832_b;
/* 158 */         double strafeInput = mc.field_71439_g.field_71158_b.field_78902_a;
/* 159 */         double[] result = forwardStrafeYaw(forwardInput, strafeInput, mc.field_71439_g.field_70177_z);
/* 160 */         double forward = result[0];
/* 161 */         double strafe = result[1];
/* 162 */         double yaw = result[2];
/* 163 */         if (forwardInput != 0.0D || strafeInput != 0.0D) {
/* 164 */           event.setX(forward * ((Float)this.speed.getValue()).floatValue() * Math.cos(Math.toRadians(yaw + 90.0D)) + strafe * ((Float)this.speed.getValue()).floatValue() * Math.sin(Math.toRadians(yaw + 90.0D)));
/* 165 */           event.setY(forward * ((Float)this.speed.getValue()).floatValue() * Math.sin(Math.toRadians(yaw + 90.0D)) - strafe * ((Float)this.speed.getValue()).floatValue() * Math.cos(Math.toRadians(yaw + 90.0D)));
/*     */         } 
/* 167 */         if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 168 */           event.setY(-1.0D);
/*     */         }
/*     */       } 
/* 171 */     } else if (this.mode.getValue() == Mode.TOOBEE) {
/* 172 */       if (!mc.field_71439_g.func_184613_cA()) {
/*     */         return;
/*     */       }
/* 175 */       if (!mc.field_71439_g.field_71158_b.field_78901_c) {
/* 176 */         if (mc.field_71439_g.field_71158_b.field_78899_d) {
/* 177 */           mc.field_71439_g.field_70181_x = -(((Float)this.tooBeeSpeed.getValue()).floatValue() / 2.0F);
/* 178 */           event.setY(-(((Float)this.speed.getValue()).floatValue() / 2.0F));
/* 179 */         } else if (event.getY() != -1.01E-4D) {
/* 180 */           event.setY(-1.01E-4D);
/* 181 */           mc.field_71439_g.field_70181_x = -1.01E-4D;
/*     */         } 
/*     */       } else {
/*     */         return;
/*     */       } 
/* 186 */       setMoveSpeed(event, ((Float)this.tooBeeSpeed.getValue()).floatValue());
/* 187 */     } else if (this.mode.getValue() == Mode.TOOBEEBYPASS) {
/* 188 */       if (!mc.field_71439_g.func_184613_cA()) {
/*     */         return;
/*     */       }
/* 191 */       if (!mc.field_71439_g.field_71158_b.field_78901_c) {
/* 192 */         if (((Boolean)this.lockPitch.getValue()).booleanValue()) {
/* 193 */           mc.field_71439_g.field_70125_A = 4.0F;
/*     */         }
/*     */       } else {
/*     */         return;
/*     */       } 
/* 198 */       if (Banzem.speedManager.getSpeedKpH() > 180.0D) {
/*     */         return;
/*     */       }
/* 201 */       double yaw = Math.toRadians(mc.field_71439_g.field_70177_z);
/* 202 */       mc.field_71439_g.field_70159_w -= mc.field_71439_g.field_71158_b.field_192832_b * Math.sin(yaw) * 0.04D;
/* 203 */       mc.field_71439_g.field_70179_y += mc.field_71439_g.field_71158_b.field_192832_b * Math.cos(yaw) * 0.04D;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setMoveSpeed(MoveEvent event, double speed) {
/* 208 */     double forward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 209 */     double strafe = mc.field_71439_g.field_71158_b.field_78902_a;
/* 210 */     float yaw = mc.field_71439_g.field_70177_z;
/* 211 */     if (forward == 0.0D && strafe == 0.0D) {
/* 212 */       event.setX(0.0D);
/* 213 */       event.setZ(0.0D);
/* 214 */       mc.field_71439_g.field_70159_w = 0.0D;
/* 215 */       mc.field_71439_g.field_70179_y = 0.0D;
/*     */     } else {
/* 217 */       if (forward != 0.0D) {
/* 218 */         if (strafe > 0.0D) {
/* 219 */           yaw += ((forward > 0.0D) ? -45 : 45);
/* 220 */         } else if (strafe < 0.0D) {
/* 221 */           yaw += ((forward > 0.0D) ? 45 : -45);
/*     */         } 
/* 223 */         strafe = 0.0D;
/* 224 */         if (forward > 0.0D) {
/* 225 */           forward = 1.0D;
/* 226 */         } else if (forward < 0.0D) {
/* 227 */           forward = -1.0D;
/*     */         } 
/*     */       } 
/* 230 */       double x = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
/* 231 */       double z = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
/* 232 */       event.setX(x);
/* 233 */       event.setZ(z);
/* 234 */       mc.field_71439_g.field_70159_w = x;
/* 235 */       mc.field_71439_g.field_70179_y = z;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void onTick() {
/*     */     float yaw;
/* 241 */     if (!mc.field_71439_g.func_184613_cA()) {
/*     */       return;
/*     */     }
/* 244 */     switch ((Mode)this.mode.getValue()) {
/*     */       case BOOST:
/* 246 */         if (mc.field_71439_g.func_70090_H()) {
/* 247 */           Util.mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/*     */           return;
/*     */         } 
/* 250 */         if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 251 */           mc.field_71439_g.field_70181_x += 0.08D;
/* 252 */         } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 253 */           mc.field_71439_g.field_70181_x -= 0.04D;
/*     */         } 
/* 255 */         if (mc.field_71474_y.field_74351_w.func_151470_d()) {
/* 256 */           float f = (float)Math.toRadians(mc.field_71439_g.field_70177_z);
/* 257 */           mc.field_71439_g.field_70159_w -= (MathHelper.func_76126_a(f) * 0.05F);
/* 258 */           mc.field_71439_g.field_70179_y += (MathHelper.func_76134_b(f) * 0.05F);
/*     */           break;
/*     */         } 
/* 261 */         if (!mc.field_71474_y.field_74368_y.func_151470_d())
/* 262 */           break;  yaw = (float)Math.toRadians(mc.field_71439_g.field_70177_z);
/* 263 */         mc.field_71439_g.field_70159_w += (MathHelper.func_76126_a(yaw) * 0.05F);
/* 264 */         mc.field_71439_g.field_70179_y -= (MathHelper.func_76134_b(yaw) * 0.05F);
/*     */         break;
/*     */       
/*     */       case FLY:
/* 268 */         mc.field_71439_g.field_71075_bZ.field_75100_b = true;
/*     */         break;
/*     */     } 
/*     */   }
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/*     */     double rotationYaw;
/* 275 */     if (mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() != Items.field_185160_cR) {
/*     */       return;
/*     */     }
/* 278 */     switch (event.getStage()) {
/*     */       case 0:
/* 280 */         if (((Boolean)this.disableInLiquid.getValue()).booleanValue() && (mc.field_71439_g.func_70090_H() || mc.field_71439_g.func_180799_ab())) {
/* 281 */           if (mc.field_71439_g.func_184613_cA()) {
/* 282 */             Util.mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/*     */           }
/*     */           return;
/*     */         } 
/* 286 */         if (((Boolean)this.autoStart.getValue()).booleanValue() && mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71439_g.func_184613_cA() && mc.field_71439_g.field_70181_x < 0.0D && this.timer.passedMs(250L)) {
/* 287 */           Util.mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/* 288 */           this.timer.reset();
/*     */         } 
/* 290 */         if (this.mode.getValue() == Mode.BETTER) {
/* 291 */           double[] dir = MathUtil.directionSpeed((((Integer)this.devMode.getValue()).intValue() == 1) ? ((Float)this.speed.getValue()).floatValue() : ((Float)this.hSpeed.getValue()).floatValue());
/* 292 */           switch (((Integer)this.devMode.getValue()).intValue()) {
/*     */             case 1:
/* 294 */               mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
/* 295 */               mc.field_71439_g.field_70747_aH = ((Float)this.speed.getValue()).floatValue();
/* 296 */               if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 297 */                 mc.field_71439_g.field_70181_x += ((Float)this.speed.getValue()).floatValue();
/*     */               }
/* 299 */               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 300 */                 mc.field_71439_g.field_70181_x -= ((Float)this.speed.getValue()).floatValue();
/*     */               }
/* 302 */               if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
/* 303 */                 mc.field_71439_g.field_70159_w = dir[0];
/* 304 */                 mc.field_71439_g.field_70179_y = dir[1];
/*     */                 break;
/*     */               } 
/* 307 */               mc.field_71439_g.field_70159_w = 0.0D;
/* 308 */               mc.field_71439_g.field_70179_y = 0.0D;
/*     */               break;
/*     */             
/*     */             case 2:
/* 312 */               if (mc.field_71439_g.func_184613_cA()) {
/* 313 */                 if (this.flyHeight == null) {
/* 314 */                   this.flyHeight = Double.valueOf(mc.field_71439_g.field_70163_u);
/*     */                 }
/*     */               } else {
/* 317 */                 this.flyHeight = null;
/*     */                 return;
/*     */               } 
/* 320 */               if (((Boolean)this.noKick.getValue()).booleanValue()) {
/* 321 */                 this.flyHeight = Double.valueOf(this.flyHeight.doubleValue() - ((Float)this.glide.getValue()).floatValue());
/*     */               }
/* 323 */               this.posX = Double.valueOf(0.0D);
/* 324 */               this.posZ = Double.valueOf(0.0D);
/* 325 */               if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
/* 326 */                 this.posX = Double.valueOf(dir[0]);
/* 327 */                 this.posZ = Double.valueOf(dir[1]);
/*     */               } 
/* 329 */               if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 330 */                 this.flyHeight = Double.valueOf(mc.field_71439_g.field_70163_u + ((Float)this.vSpeed.getValue()).floatValue());
/*     */               }
/* 332 */               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 333 */                 this.flyHeight = Double.valueOf(mc.field_71439_g.field_70163_u - ((Float)this.vSpeed.getValue()).floatValue());
/*     */               }
/* 335 */               mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + this.posX.doubleValue(), this.flyHeight.doubleValue(), mc.field_71439_g.field_70161_v + this.posZ.doubleValue());
/* 336 */               mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
/*     */               break;
/*     */             
/*     */             case 3:
/* 340 */               if (mc.field_71439_g.func_184613_cA()) {
/* 341 */                 if (this.flyHeight == null || this.posX == null || this.posX.doubleValue() == 0.0D || this.posZ == null || this.posZ.doubleValue() == 0.0D) {
/* 342 */                   this.flyHeight = Double.valueOf(mc.field_71439_g.field_70163_u);
/* 343 */                   this.posX = Double.valueOf(mc.field_71439_g.field_70165_t);
/* 344 */                   this.posZ = Double.valueOf(mc.field_71439_g.field_70161_v);
/*     */                 } 
/*     */               } else {
/* 347 */                 this.flyHeight = null;
/* 348 */                 this.posX = null;
/* 349 */                 this.posZ = null;
/*     */                 return;
/*     */               } 
/* 352 */               if (((Boolean)this.noKick.getValue()).booleanValue()) {
/* 353 */                 this.flyHeight = Double.valueOf(this.flyHeight.doubleValue() - ((Float)this.glide.getValue()).floatValue());
/*     */               }
/* 355 */               if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
/* 356 */                 this.posX = Double.valueOf(this.posX.doubleValue() + dir[0]);
/* 357 */                 this.posZ = Double.valueOf(this.posZ.doubleValue() + dir[1]);
/*     */               } 
/* 359 */               if (((Boolean)this.allowUp.getValue()).booleanValue() && mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 360 */                 this.flyHeight = Double.valueOf(mc.field_71439_g.field_70163_u + (((Float)this.vSpeed.getValue()).floatValue() / 10.0F));
/*     */               }
/* 362 */               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 363 */                 this.flyHeight = Double.valueOf(mc.field_71439_g.field_70163_u - (((Float)this.vSpeed.getValue()).floatValue() / 10.0F));
/*     */               }
/* 365 */               mc.field_71439_g.func_70107_b(this.posX.doubleValue(), this.flyHeight.doubleValue(), this.posZ.doubleValue());
/* 366 */               mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
/*     */               break;
/*     */           } 
/*     */         } 
/* 370 */         rotationYaw = Math.toRadians(mc.field_71439_g.field_70177_z);
/* 371 */         if (mc.field_71439_g.func_184613_cA()) {
/* 372 */           float speedScaled; double[] directionSpeedPacket; double[] directionSpeedBypass; switch ((Mode)this.mode.getValue()) {
/*     */             case VANILLA:
/* 374 */               speedScaled = ((Float)this.speed.getValue()).floatValue() * 0.05F;
/* 375 */               if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 376 */                 mc.field_71439_g.field_70181_x += speedScaled;
/*     */               }
/* 378 */               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 379 */                 mc.field_71439_g.field_70181_x -= speedScaled;
/*     */               }
/* 381 */               if (mc.field_71474_y.field_74351_w.func_151470_d()) {
/* 382 */                 mc.field_71439_g.field_70159_w -= Math.sin(rotationYaw) * speedScaled;
/* 383 */                 mc.field_71439_g.field_70179_y += Math.cos(rotationYaw) * speedScaled;
/*     */               } 
/* 385 */               if (!mc.field_71474_y.field_74368_y.func_151470_d())
/* 386 */                 break;  mc.field_71439_g.field_70159_w += Math.sin(rotationYaw) * speedScaled;
/* 387 */               mc.field_71439_g.field_70179_y -= Math.cos(rotationYaw) * speedScaled;
/*     */               break;
/*     */             
/*     */             case PACKET:
/* 391 */               freezePlayer((EntityPlayer)mc.field_71439_g);
/* 392 */               runNoKick((EntityPlayer)mc.field_71439_g);
/* 393 */               directionSpeedPacket = MathUtil.directionSpeed(((Float)this.speed.getValue()).floatValue());
/* 394 */               if (mc.field_71439_g.field_71158_b.field_78901_c) {
/* 395 */                 mc.field_71439_g.field_70181_x = ((Float)this.speed.getValue()).floatValue();
/*     */               }
/* 397 */               if (mc.field_71439_g.field_71158_b.field_78899_d) {
/* 398 */                 mc.field_71439_g.field_70181_x = -((Float)this.speed.getValue()).floatValue();
/*     */               }
/* 400 */               if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
/* 401 */                 mc.field_71439_g.field_70159_w = directionSpeedPacket[0];
/* 402 */                 mc.field_71439_g.field_70179_y = directionSpeedPacket[1];
/*     */               } 
/* 404 */               Util.mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/* 405 */               Util.mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/*     */               break;
/*     */             
/*     */             case BYPASS:
/* 409 */               if (((Integer)this.devMode.getValue()).intValue() != 3)
/* 410 */                 break;  if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 411 */                 mc.field_71439_g.field_70181_x = 0.019999999552965164D;
/*     */               }
/* 413 */               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 414 */                 mc.field_71439_g.field_70181_x = -0.20000000298023224D;
/*     */               }
/* 416 */               if (mc.field_71439_g.field_70173_aa % 8 == 0 && mc.field_71439_g.field_70163_u <= 240.0D) {
/* 417 */                 mc.field_71439_g.field_70181_x = 0.019999999552965164D;
/*     */               }
/* 419 */               mc.field_71439_g.field_71075_bZ.field_75100_b = true;
/* 420 */               mc.field_71439_g.field_71075_bZ.func_75092_a(0.025F);
/* 421 */               directionSpeedBypass = MathUtil.directionSpeed(0.5199999809265137D);
/* 422 */               if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
/* 423 */                 mc.field_71439_g.field_70159_w = directionSpeedBypass[0];
/* 424 */                 mc.field_71439_g.field_70179_y = directionSpeedBypass[1];
/*     */                 break;
/*     */               } 
/* 427 */               mc.field_71439_g.field_70159_w = 0.0D;
/* 428 */               mc.field_71439_g.field_70179_y = 0.0D;
/*     */               break;
/*     */           } 
/*     */         } 
/* 432 */         if (!((Boolean)this.infiniteDura.getValue()).booleanValue())
/* 433 */           break;  mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/*     */         break;
/*     */       
/*     */       case 1:
/* 437 */         if (!((Boolean)this.infiniteDura.getValue()).booleanValue())
/* 438 */           break;  mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   private double[] forwardStrafeYaw(double forward, double strafe, double yaw) {
/* 444 */     double[] result = { forward, strafe, yaw };
/* 445 */     if ((forward != 0.0D || strafe != 0.0D) && forward != 0.0D) {
/* 446 */       if (strafe > 0.0D) {
/* 447 */         result[2] = result[2] + ((forward > 0.0D) ? -45 : 45);
/* 448 */       } else if (strafe < 0.0D) {
/* 449 */         result[2] = result[2] + ((forward > 0.0D) ? 45 : -45);
/*     */       } 
/* 451 */       result[1] = 0.0D;
/* 452 */       if (forward > 0.0D) {
/* 453 */         result[0] = 1.0D;
/* 454 */       } else if (forward < 0.0D) {
/* 455 */         result[0] = -1.0D;
/*     */       } 
/*     */     } 
/* 458 */     return result;
/*     */   }
/*     */   
/*     */   private void freezePlayer(EntityPlayer player) {
/* 462 */     player.field_70159_w = 0.0D;
/* 463 */     player.field_70181_x = 0.0D;
/* 464 */     player.field_70179_y = 0.0D;
/*     */   }
/*     */   
/*     */   private void runNoKick(EntityPlayer player) {
/* 468 */     if (((Boolean)this.noKick.getValue()).booleanValue() && !player.func_184613_cA() && player.field_70173_aa % 4 == 0) {
/* 469 */       player.field_70181_x = -0.03999999910593033D;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 475 */     if (fullNullCheck() || mc.field_71439_g.field_71075_bZ.field_75098_d) {
/*     */       return;
/*     */     }
/* 478 */     mc.field_71439_g.field_71075_bZ.field_75100_b = false;
/*     */   }
/*     */   
/*     */   public enum Mode {
/* 482 */     VANILLA,
/* 483 */     PACKET,
/* 484 */     BOOST,
/* 485 */     FLY,
/* 486 */     BYPASS,
/* 487 */     BETTER,
/* 488 */     OHARE,
/* 489 */     TOOBEE,
/* 490 */     TOOBEEBYPASS;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\ElytraFlight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */