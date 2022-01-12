/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Objects;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.MoveEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.player.Freecam;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.init.MobEffects;
/*     */ import net.minecraft.potion.PotionEffect;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Strafe
/*     */   extends Module {
/*     */   private static Strafe INSTANCE;
/*  23 */   private final Setting<Mode> mode = register(new Setting("Mode", Mode.NCP));
/*  24 */   private final Setting<Boolean> limiter = register(new Setting("SetGround", Boolean.valueOf(true)));
/*  25 */   private final Setting<Boolean> bhop2 = register(new Setting("Hop", Boolean.valueOf(true)));
/*  26 */   private final Setting<Boolean> limiter2 = register(new Setting("Bhop", Boolean.valueOf(false)));
/*  27 */   private final Setting<Boolean> noLag = register(new Setting("NoLag", Boolean.valueOf(false)));
/*  28 */   private final Setting<Integer> specialMoveSpeed = register(new Setting("Speed", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(150)));
/*  29 */   private final Setting<Integer> potionSpeed = register(new Setting("Speed1", Integer.valueOf(130), Integer.valueOf(0), Integer.valueOf(150)));
/*  30 */   private final Setting<Integer> potionSpeed2 = register(new Setting("Speed2", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(150)));
/*  31 */   private final Setting<Integer> dFactor = register(new Setting("DFactor", Integer.valueOf(159), Integer.valueOf(100), Integer.valueOf(200)));
/*  32 */   private final Setting<Integer> acceleration = register(new Setting("Accel", Integer.valueOf(2149), Integer.valueOf(1000), Integer.valueOf(2500)));
/*  33 */   private final Setting<Float> speedLimit = register(new Setting("SpeedLimit", Float.valueOf(35.0F), Float.valueOf(20.0F), Float.valueOf(60.0F)));
/*  34 */   private final Setting<Float> speedLimit2 = register(new Setting("SpeedLimit2", Float.valueOf(60.0F), Float.valueOf(20.0F), Float.valueOf(60.0F)));
/*  35 */   private final Setting<Integer> yOffset = register(new Setting("YOffset", Integer.valueOf(400), Integer.valueOf(350), Integer.valueOf(500)));
/*  36 */   private final Setting<Boolean> potion = register(new Setting("Potion", Boolean.valueOf(false)));
/*  37 */   private final Setting<Boolean> wait = register(new Setting("Wait", Boolean.valueOf(true)));
/*  38 */   private final Setting<Boolean> hopWait = register(new Setting("HopWait", Boolean.valueOf(true)));
/*  39 */   private final Setting<Integer> startStage = register(new Setting("Stage", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(4)));
/*  40 */   private final Setting<Boolean> setPos = register(new Setting("SetPos", Boolean.valueOf(true)));
/*  41 */   private final Setting<Boolean> setNull = register(new Setting("SetNull", Boolean.valueOf(false)));
/*  42 */   private final Setting<Integer> setGroundLimit = register(new Setting("GroundLimit", Integer.valueOf(138), Integer.valueOf(0), Integer.valueOf(1000)));
/*  43 */   private final Setting<Integer> groundFactor = register(new Setting("GroundFactor", Integer.valueOf(13), Integer.valueOf(0), Integer.valueOf(50)));
/*  44 */   private final Setting<Integer> step = register(new Setting("SetStep", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(2), v -> (this.mode.getValue() == Mode.BHOP)));
/*  45 */   private final Setting<Boolean> setGroundNoLag = register(new Setting("NoGroundLag", Boolean.valueOf(true)));
/*  46 */   private int stage = 1;
/*     */   private double moveSpeed;
/*     */   private double lastDist;
/*  49 */   private int cooldownHops = 0;
/*     */   private boolean waitForGround = false;
/*  51 */   private final Timer timer = new Timer();
/*  52 */   private int hops = 0;
/*     */   
/*     */   public Strafe() {
/*  55 */     super("Strafe", "AirControl etc.", Module.Category.MOVEMENT, true, false, false);
/*  56 */     INSTANCE = this;
/*     */   }
/*     */   
/*     */   public static Strafe getInstance() {
/*  60 */     if (INSTANCE == null) {
/*  61 */       INSTANCE = new Strafe();
/*     */     }
/*  63 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   public static double getBaseMoveSpeed() {
/*  67 */     double baseSpeed = 0.272D;
/*  68 */     if (mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
/*  69 */       int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c();
/*  70 */       baseSpeed *= 1.0D + 0.2D * amplifier;
/*     */     } 
/*  72 */     return baseSpeed;
/*     */   }
/*     */   
/*     */   public static double round(double value, int places) {
/*  76 */     if (places < 0) {
/*  77 */       throw new IllegalArgumentException();
/*     */     }
/*  79 */     BigDecimal bigDecimal = (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP);
/*  80 */     return bigDecimal.doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  85 */     if (!mc.field_71439_g.field_70122_E) {
/*  86 */       this.waitForGround = true;
/*     */     }
/*  88 */     this.hops = 0;
/*  89 */     this.timer.reset();
/*  90 */     this.moveSpeed = getBaseMoveSpeed();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  95 */     this.hops = 0;
/*  96 */     this.moveSpeed = 0.0D;
/*  97 */     this.stage = ((Integer)this.startStage.getValue()).intValue();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/* 102 */     if (event.getStage() == 0) {
/* 103 */       this.lastDist = Math.sqrt((mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s));
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onMove(MoveEvent event) {
/* 109 */     if (event.getStage() != 0 || shouldReturn()) {
/*     */       return;
/*     */     }
/* 112 */     if (!mc.field_71439_g.field_70122_E) {
/* 113 */       if (((Boolean)this.wait.getValue()).booleanValue() && this.waitForGround) {
/*     */         return;
/*     */       }
/*     */     } else {
/* 117 */       this.waitForGround = false;
/*     */     } 
/* 119 */     if (this.mode.getValue() == Mode.NCP) {
/* 120 */       doNCP(event);
/* 121 */     } else if (this.mode.getValue() == Mode.BHOP) {
/* 122 */       float moveForward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 123 */       float moveStrafe = mc.field_71439_g.field_71158_b.field_78902_a;
/* 124 */       float rotationYaw = mc.field_71439_g.field_70177_z;
/* 125 */       if (((Integer)this.step.getValue()).intValue() == 1) {
/* 126 */         mc.field_71439_g.field_70138_W = 0.6F;
/*     */       }
/* 128 */       if (((Boolean)this.limiter2.getValue()).booleanValue() && mc.field_71439_g.field_70122_E && Banzem.speedManager.getSpeedKpH() < ((Float)this.speedLimit2.getValue()).floatValue()) {
/* 129 */         this.stage = 2;
/*     */       }
/* 131 */       if (((Boolean)this.limiter.getValue()).booleanValue() && round(mc.field_71439_g.field_70163_u - (int)mc.field_71439_g.field_70163_u, 3) == round(((Integer)this.setGroundLimit.getValue()).intValue() / 1000.0D, 3) && (!((Boolean)this.setGroundNoLag.getValue()).booleanValue() || EntityUtil.isEntityMoving((Entity)mc.field_71439_g))) {
/* 132 */         if (((Boolean)this.setNull.getValue()).booleanValue()) {
/* 133 */           mc.field_71439_g.field_70181_x = 0.0D;
/*     */         } else {
/* 135 */           mc.field_71439_g.field_70181_x -= ((Integer)this.groundFactor.getValue()).intValue() / 100.0D;
/* 136 */           event.setY(event.getY() - ((Integer)this.groundFactor.getValue()).intValue() / 100.0D);
/* 137 */           if (((Boolean)this.setPos.getValue()).booleanValue()) {
/* 138 */             mc.field_71439_g.field_70163_u -= ((Integer)this.groundFactor.getValue()).intValue() / 100.0D;
/*     */           }
/*     */         } 
/*     */       }
/* 142 */       if (this.stage == 1 && EntityUtil.isMoving()) {
/* 143 */         this.stage = 2;
/* 144 */         this.moveSpeed = getMultiplier() * getBaseMoveSpeed() - 0.01D;
/* 145 */       } else if (this.stage == 2 && EntityUtil.isMoving()) {
/* 146 */         this.stage = 3;
/* 147 */         mc.field_71439_g.field_70181_x = ((Integer)this.yOffset.getValue()).intValue() / 1000.0D;
/* 148 */         event.setY(((Integer)this.yOffset.getValue()).intValue() / 1000.0D);
/* 149 */         if (this.cooldownHops > 0) {
/* 150 */           this.cooldownHops--;
/*     */         }
/* 152 */         this.hops++;
/* 153 */         double accel = (((Integer)this.acceleration.getValue()).intValue() == 2149) ? 2.149802D : (((Integer)this.acceleration.getValue()).intValue() / 1000.0D);
/* 154 */         this.moveSpeed *= accel;
/* 155 */       } else if (this.stage == 3) {
/* 156 */         this.stage = 4;
/* 157 */         double difference = 0.66D * (this.lastDist - getBaseMoveSpeed());
/* 158 */         this.moveSpeed = this.lastDist - difference;
/*     */       } else {
/* 160 */         if (mc.field_71441_e.func_184144_a((Entity)mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).size() > 0 || (mc.field_71439_g.field_70124_G && this.stage > 0)) {
/* 161 */           this.stage = (((Boolean)this.bhop2.getValue()).booleanValue() && Banzem.speedManager.getSpeedKpH() >= ((Float)this.speedLimit.getValue()).floatValue()) ? 0 : ((mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F) ? 1 : 0);
/*     */         }
/* 163 */         this.moveSpeed = this.lastDist - this.lastDist / ((Integer)this.dFactor.getValue()).intValue();
/*     */       } 
/* 165 */       this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
/* 166 */       if (((Boolean)this.hopWait.getValue()).booleanValue() && ((Boolean)this.limiter2.getValue()).booleanValue() && this.hops < 2) {
/* 167 */         this.moveSpeed = EntityUtil.getMaxSpeed();
/*     */       }
/* 169 */       if (moveForward == 0.0F && moveStrafe == 0.0F) {
/* 170 */         event.setX(0.0D);
/* 171 */         event.setZ(0.0D);
/* 172 */         this.moveSpeed = 0.0D;
/* 173 */       } else if (moveForward != 0.0F) {
/* 174 */         if (moveStrafe >= 1.0F) {
/* 175 */           rotationYaw += (moveForward > 0.0F) ? -45.0F : 45.0F;
/* 176 */           moveStrafe = 0.0F;
/* 177 */         } else if (moveStrafe <= -1.0F) {
/* 178 */           rotationYaw += (moveForward > 0.0F) ? 45.0F : -45.0F;
/* 179 */           moveStrafe = 0.0F;
/*     */         } 
/* 181 */         if (moveForward > 0.0F) {
/* 182 */           moveForward = 1.0F;
/* 183 */         } else if (moveForward < 0.0F) {
/* 184 */           moveForward = -1.0F;
/*     */         } 
/*     */       } 
/* 187 */       double motionX = Math.cos(Math.toRadians((rotationYaw + 90.0F)));
/* 188 */       double motionZ = Math.sin(Math.toRadians((rotationYaw + 90.0F)));
/* 189 */       if (this.cooldownHops == 0) {
/* 190 */         event.setX(moveForward * this.moveSpeed * motionX + moveStrafe * this.moveSpeed * motionZ);
/* 191 */         event.setZ(moveForward * this.moveSpeed * motionZ - moveStrafe * this.moveSpeed * motionX);
/*     */       } 
/* 193 */       if (((Integer)this.step.getValue()).intValue() == 2) {
/* 194 */         mc.field_71439_g.field_70138_W = 0.6F;
/*     */       }
/* 196 */       if (moveForward == 0.0F && moveStrafe == 0.0F) {
/* 197 */         this.timer.reset();
/* 198 */         event.setX(0.0D);
/* 199 */         event.setZ(0.0D);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private void doNCP(MoveEvent event) {
/*     */     double motionY;
/* 205 */     if (!((Boolean)this.limiter.getValue()).booleanValue() && mc.field_71439_g.field_70122_E) {
/* 206 */       this.stage = 2;
/*     */     }
/* 208 */     switch (this.stage) {
/*     */       case 0:
/* 210 */         this.stage++;
/* 211 */         this.lastDist = 0.0D;
/*     */         break;
/*     */       
/*     */       case 2:
/* 215 */         motionY = 0.40123128D;
/* 216 */         if ((mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F) || !mc.field_71439_g.field_70122_E)
/*     */           break; 
/* 218 */         if (mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
/* 219 */           motionY += ((mc.field_71439_g.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1F);
/*     */         }
/* 221 */         mc.field_71439_g.field_70181_x = motionY;
/* 222 */         event.setY(mc.field_71439_g.field_70181_x);
/* 223 */         this.moveSpeed *= 2.149D;
/*     */         break;
/*     */       
/*     */       case 3:
/* 227 */         this.moveSpeed = this.lastDist - 0.76D * (this.lastDist - getBaseMoveSpeed());
/*     */         break;
/*     */       
/*     */       default:
/* 231 */         if (mc.field_71441_e.func_184144_a((Entity)mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).size() > 0 || (mc.field_71439_g.field_70124_G && this.stage > 0)) {
/* 232 */           this.stage = (((Boolean)this.bhop2.getValue()).booleanValue() && Banzem.speedManager.getSpeedKpH() >= ((Float)this.speedLimit.getValue()).floatValue()) ? 0 : ((mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F) ? 1 : 0);
/*     */         }
/* 234 */         this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
/*     */         break;
/*     */     } 
/* 237 */     this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
/* 238 */     double forward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 239 */     double strafe = mc.field_71439_g.field_71158_b.field_78902_a;
/* 240 */     double yaw = mc.field_71439_g.field_70177_z;
/* 241 */     if (forward == 0.0D && strafe == 0.0D) {
/* 242 */       event.setX(0.0D);
/* 243 */       event.setZ(0.0D);
/* 244 */     } else if (forward != 0.0D && strafe != 0.0D) {
/* 245 */       forward *= Math.sin(0.7853981633974483D);
/* 246 */       strafe *= Math.cos(0.7853981633974483D);
/*     */     } 
/* 248 */     event.setX((forward * this.moveSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99D);
/* 249 */     event.setZ((forward * this.moveSpeed * Math.cos(Math.toRadians(yaw)) - strafe * this.moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99D);
/* 250 */     this.stage++;
/*     */   }
/*     */   
/*     */   private float getMultiplier() {
/* 254 */     float baseSpeed = ((Integer)this.specialMoveSpeed.getValue()).intValue();
/* 255 */     if (((Boolean)this.potion.getValue()).booleanValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
/* 256 */       int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c() + 1;
/* 257 */       baseSpeed = (amplifier >= 2) ? ((Integer)this.potionSpeed2.getValue()).intValue() : ((Integer)this.potionSpeed.getValue()).intValue();
/*     */     } 
/* 259 */     return baseSpeed / 100.0F;
/*     */   }
/*     */   
/*     */   private boolean shouldReturn() {
/* 263 */     return (Banzem.moduleManager.isModuleEnabled(Freecam.class) || Banzem.moduleManager.isModuleEnabled(ElytraFlight.class));
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceive(PacketEvent.Receive event) {
/* 268 */     if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlayerPosLook && ((Boolean)this.noLag.getValue()).booleanValue()) {
/* 269 */       this.stage = (this.mode.getValue() == Mode.BHOP && (((Boolean)this.limiter2.getValue()).booleanValue() || ((Boolean)this.bhop2.getValue()).booleanValue())) ? 1 : 4;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/* 275 */     if (this.mode.getValue() != Mode.NONE) {
/* 276 */       if (this.mode.getValue() == Mode.NCP) {
/* 277 */         return this.mode.currentEnumName().toUpperCase();
/*     */       }
/* 279 */       return this.mode.currentEnumName();
/*     */     } 
/* 281 */     return null;
/*     */   }
/*     */   
/*     */   public enum Mode {
/* 285 */     NONE,
/* 286 */     NCP,
/* 287 */     BHOP;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\Strafe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */