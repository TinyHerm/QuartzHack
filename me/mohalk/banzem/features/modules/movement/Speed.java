/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import java.util.Random;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.ClientEvent;
/*     */ import me.mohalk.banzem.event.events.MoveEvent;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.init.MobEffects;
/*     */ import net.minecraft.potion.PotionEffect;
/*     */ import net.minecraft.util.MovementInput;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Speed extends Module {
/*  21 */   private static Speed INSTANCE = new Speed();
/*  22 */   public Setting<Mode> mode = register(new Setting("Mode", Mode.INSTANT));
/*  23 */   public Setting<Boolean> strafeJump = register(new Setting("Jump", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.INSTANT)));
/*  24 */   public Setting<Boolean> noShake = register(new Setting("NoShake", Boolean.valueOf(true), v -> (this.mode.getValue() != Mode.INSTANT)));
/*  25 */   public Setting<Boolean> useTimer = register(new Setting("UseTimer", Boolean.valueOf(false), v -> (this.mode.getValue() != Mode.INSTANT)));
/*  26 */   public Setting<Double> zeroSpeed = register(new Setting("0-Speed", Double.valueOf(0.0D), Double.valueOf(0.0D), Double.valueOf(100.0D), v -> (this.mode.getValue() == Mode.VANILLA)));
/*  27 */   public Setting<Double> speed = register(new Setting("Speed", Double.valueOf(10.0D), Double.valueOf(0.1D), Double.valueOf(100.0D), v -> (this.mode.getValue() == Mode.VANILLA)));
/*  28 */   public Setting<Double> blocked = register(new Setting("Blocked", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(100.0D), v -> (this.mode.getValue() == Mode.VANILLA)));
/*  29 */   public Setting<Double> unblocked = register(new Setting("Unblocked", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(100.0D), v -> (this.mode.getValue() == Mode.VANILLA)));
/*  30 */   public double startY = 0.0D;
/*     */   public boolean antiShake = false;
/*  32 */   public double minY = 0.0D;
/*     */   public boolean changeY = false;
/*  34 */   private double highChainVal = 0.0D;
/*  35 */   private double lowChainVal = 0.0D;
/*     */   private boolean oneTime = false;
/*  37 */   private double bounceHeight = 0.4D;
/*  38 */   private float move = 0.26F;
/*  39 */   private int vanillaCounter = 0;
/*     */   
/*     */   public Speed() {
/*  42 */     super("Speed", "Makes you faster", Module.Category.MOVEMENT, true, false, false);
/*  43 */     setInstance();
/*     */   }
/*     */   
/*     */   public static Speed getInstance() {
/*  47 */     if (INSTANCE == null) {
/*  48 */       INSTANCE = new Speed();
/*     */     }
/*  50 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  54 */     INSTANCE = this;
/*     */   }
/*     */   
/*     */   private boolean shouldReturn() {
/*  58 */     return (Banzem.moduleManager.isModuleEnabled("Freecam") || Banzem.moduleManager.isModuleEnabled("Phase") || Banzem.moduleManager.isModuleEnabled("ElytraFlight") || Banzem.moduleManager.isModuleEnabled("Strafe") || Banzem.moduleManager.isModuleEnabled("Flight"));
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  63 */     if (shouldReturn() || mc.field_71439_g.func_70093_af() || mc.field_71439_g.func_70090_H() || mc.field_71439_g.func_180799_ab()) {
/*     */       return;
/*     */     }
/*  66 */     switch ((Mode)this.mode.getValue()) {
/*     */       case BOOST:
/*  68 */         doBoost();
/*     */         break;
/*     */       
/*     */       case ACCEL:
/*  72 */         doAccel();
/*     */         break;
/*     */       
/*     */       case ONGROUND:
/*  76 */         doOnground();
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/*  84 */     if (this.mode.getValue() != Mode.VANILLA || nullCheck()) {
/*     */       return;
/*     */     }
/*  87 */     switch (event.getStage()) {
/*     */       case 0:
/*  89 */         this.vanillaCounter = vanilla() ? ++this.vanillaCounter : 0;
/*  90 */         if (this.vanillaCounter != 4)
/*  91 */           break;  this.changeY = true;
/*  92 */         this.minY = (mc.field_71439_g.func_174813_aQ()).field_72338_b + (mc.field_71441_e.func_180495_p(mc.field_71439_g.func_180425_c()).func_185904_a().func_76230_c() ? (-((Double)this.blocked.getValue()).doubleValue() / 10.0D) : (((Double)this.unblocked.getValue()).doubleValue() / 10.0D)) + getJumpBoostModifier();
/*     */         return;
/*     */       
/*     */       case 1:
/*  96 */         if (this.vanillaCounter == 3) {
/*  97 */           mc.field_71439_g.field_70159_w *= ((Double)this.zeroSpeed.getValue()).doubleValue() / 10.0D;
/*  98 */           mc.field_71439_g.field_70179_y *= ((Double)this.zeroSpeed.getValue()).doubleValue() / 10.0D;
/*     */           break;
/*     */         } 
/* 101 */         if (this.vanillaCounter != 4)
/* 102 */           break;  mc.field_71439_g.field_70159_w /= ((Double)this.speed.getValue()).doubleValue() / 10.0D;
/* 103 */         mc.field_71439_g.field_70179_y /= ((Double)this.speed.getValue()).doubleValue() / 10.0D;
/* 104 */         this.vanillaCounter = 2;
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   private double getJumpBoostModifier() {
/* 110 */     double boost = 0.0D;
/* 111 */     if (mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
/* 112 */       int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76430_j))).func_76458_c();
/* 113 */       boost *= 1.0D + 0.2D * amplifier;
/*     */     } 
/* 115 */     return boost;
/*     */   }
/*     */   
/*     */   private boolean vanillaCheck() {
/* 119 */     if (mc.field_71439_g.field_70122_E);
/*     */ 
/*     */     
/* 122 */     return false;
/*     */   }
/*     */   
/*     */   private boolean vanilla() {
/* 126 */     return mc.field_71439_g.field_70122_E;
/*     */   }
/*     */   
/*     */   private void doBoost() {
/* 130 */     this.bounceHeight = 0.4D;
/* 131 */     this.move = 0.26F;
/* 132 */     if (mc.field_71439_g.field_70122_E) {
/* 133 */       this.startY = mc.field_71439_g.field_70163_u;
/*     */     }
/* 135 */     if (EntityUtil.getEntitySpeed((Entity)mc.field_71439_g) <= 1.0D) {
/* 136 */       this.lowChainVal = 1.0D;
/* 137 */       this.highChainVal = 1.0D;
/*     */     } 
/* 139 */     if (EntityUtil.isEntityMoving((Entity)mc.field_71439_g) && !mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid((Entity)mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid((Entity)mc.field_71439_g)) {
/* 140 */       this.oneTime = true;
/* 141 */       this.antiShake = (((Boolean)this.noShake.getValue()).booleanValue() && mc.field_71439_g.func_184187_bx() == null);
/* 142 */       Random random = new Random();
/* 143 */       boolean rnd = random.nextBoolean();
/* 144 */       if (mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
/* 145 */         mc.field_71439_g.field_70181_x = -this.bounceHeight;
/* 146 */         this.lowChainVal++;
/* 147 */         if (this.lowChainVal == 1.0D) {
/* 148 */           this.move = 0.075F;
/*     */         }
/* 150 */         if (this.lowChainVal == 2.0D) {
/* 151 */           this.move = 0.15F;
/*     */         }
/* 153 */         if (this.lowChainVal == 3.0D) {
/* 154 */           this.move = 0.175F;
/*     */         }
/* 156 */         if (this.lowChainVal == 4.0D) {
/* 157 */           this.move = 0.2F;
/*     */         }
/* 159 */         if (this.lowChainVal == 5.0D) {
/* 160 */           this.move = 0.225F;
/*     */         }
/* 162 */         if (this.lowChainVal == 6.0D) {
/* 163 */           this.move = 0.25F;
/*     */         }
/* 165 */         if (this.lowChainVal >= 7.0D) {
/* 166 */           this.move = 0.27895F;
/*     */         }
/* 168 */         if (((Boolean)this.useTimer.getValue()).booleanValue()) {
/* 169 */           Banzem.timerManager.setTimer(1.0F);
/*     */         }
/*     */       } 
/* 172 */       if (mc.field_71439_g.field_70163_u == this.startY) {
/* 173 */         mc.field_71439_g.field_70181_x = this.bounceHeight;
/* 174 */         this.highChainVal++;
/* 175 */         if (this.highChainVal == 1.0D) {
/* 176 */           this.move = 0.075F;
/*     */         }
/* 178 */         if (this.highChainVal == 2.0D) {
/* 179 */           this.move = 0.175F;
/*     */         }
/* 181 */         if (this.highChainVal == 3.0D) {
/* 182 */           this.move = 0.325F;
/*     */         }
/* 184 */         if (this.highChainVal == 4.0D) {
/* 185 */           this.move = 0.375F;
/*     */         }
/* 187 */         if (this.highChainVal == 5.0D) {
/* 188 */           this.move = 0.4F;
/*     */         }
/* 190 */         if (this.highChainVal >= 6.0D) {
/* 191 */           this.move = 0.43395F;
/*     */         }
/* 193 */         if (((Boolean)this.useTimer.getValue()).booleanValue()) {
/* 194 */           if (rnd) {
/* 195 */             Banzem.timerManager.setTimer(1.3F);
/*     */           } else {
/* 197 */             Banzem.timerManager.setTimer(1.0F);
/*     */           } 
/*     */         }
/*     */       } 
/* 201 */       EntityUtil.moveEntityStrafe(this.move, (Entity)mc.field_71439_g);
/*     */     } else {
/* 203 */       if (this.oneTime) {
/* 204 */         mc.field_71439_g.field_70181_x = -0.1D;
/* 205 */         this.oneTime = false;
/*     */       } 
/* 207 */       this.highChainVal = 0.0D;
/* 208 */       this.lowChainVal = 0.0D;
/* 209 */       this.antiShake = false;
/* 210 */       speedOff();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doAccel() {
/* 215 */     this.bounceHeight = 0.4D;
/* 216 */     this.move = 0.26F;
/* 217 */     if (mc.field_71439_g.field_70122_E) {
/* 218 */       this.startY = mc.field_71439_g.field_70163_u;
/*     */     }
/* 220 */     if (EntityUtil.getEntitySpeed((Entity)mc.field_71439_g) <= 1.0D) {
/* 221 */       this.lowChainVal = 1.0D;
/* 222 */       this.highChainVal = 1.0D;
/*     */     } 
/* 224 */     if (EntityUtil.isEntityMoving((Entity)mc.field_71439_g) && !mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid((Entity)mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid((Entity)mc.field_71439_g)) {
/* 225 */       this.oneTime = true;
/* 226 */       this.antiShake = (((Boolean)this.noShake.getValue()).booleanValue() && mc.field_71439_g.func_184187_bx() == null);
/* 227 */       Random random = new Random();
/* 228 */       boolean rnd = random.nextBoolean();
/* 229 */       if (mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
/* 230 */         mc.field_71439_g.field_70181_x = -this.bounceHeight;
/* 231 */         this.lowChainVal++;
/* 232 */         if (this.lowChainVal == 1.0D) {
/* 233 */           this.move = 0.075F;
/*     */         }
/* 235 */         if (this.lowChainVal == 2.0D) {
/* 236 */           this.move = 0.175F;
/*     */         }
/* 238 */         if (this.lowChainVal == 3.0D) {
/* 239 */           this.move = 0.275F;
/*     */         }
/* 241 */         if (this.lowChainVal == 4.0D) {
/* 242 */           this.move = 0.35F;
/*     */         }
/* 244 */         if (this.lowChainVal == 5.0D) {
/* 245 */           this.move = 0.375F;
/*     */         }
/* 247 */         if (this.lowChainVal == 6.0D) {
/* 248 */           this.move = 0.4F;
/*     */         }
/* 250 */         if (this.lowChainVal == 7.0D) {
/* 251 */           this.move = 0.425F;
/*     */         }
/* 253 */         if (this.lowChainVal == 8.0D) {
/* 254 */           this.move = 0.45F;
/*     */         }
/* 256 */         if (this.lowChainVal == 9.0D) {
/* 257 */           this.move = 0.475F;
/*     */         }
/* 259 */         if (this.lowChainVal == 10.0D) {
/* 260 */           this.move = 0.5F;
/*     */         }
/* 262 */         if (this.lowChainVal == 11.0D) {
/* 263 */           this.move = 0.5F;
/*     */         }
/* 265 */         if (this.lowChainVal == 12.0D) {
/* 266 */           this.move = 0.525F;
/*     */         }
/* 268 */         if (this.lowChainVal == 13.0D) {
/* 269 */           this.move = 0.525F;
/*     */         }
/* 271 */         if (this.lowChainVal == 14.0D) {
/* 272 */           this.move = 0.535F;
/*     */         }
/* 274 */         if (this.lowChainVal == 15.0D) {
/* 275 */           this.move = 0.535F;
/*     */         }
/* 277 */         if (this.lowChainVal == 16.0D) {
/* 278 */           this.move = 0.545F;
/*     */         }
/* 280 */         if (this.lowChainVal >= 17.0D) {
/* 281 */           this.move = 0.545F;
/*     */         }
/* 283 */         if (((Boolean)this.useTimer.getValue()).booleanValue()) {
/* 284 */           Banzem.timerManager.setTimer(1.0F);
/*     */         }
/*     */       } 
/* 287 */       if (mc.field_71439_g.field_70163_u == this.startY) {
/* 288 */         mc.field_71439_g.field_70181_x = this.bounceHeight;
/* 289 */         this.highChainVal++;
/* 290 */         if (this.highChainVal == 1.0D) {
/* 291 */           this.move = 0.075F;
/*     */         }
/* 293 */         if (this.highChainVal == 2.0D) {
/* 294 */           this.move = 0.175F;
/*     */         }
/* 296 */         if (this.highChainVal == 3.0D) {
/* 297 */           this.move = 0.375F;
/*     */         }
/* 299 */         if (this.highChainVal == 4.0D) {
/* 300 */           this.move = 0.6F;
/*     */         }
/* 302 */         if (this.highChainVal == 5.0D) {
/* 303 */           this.move = 0.775F;
/*     */         }
/* 305 */         if (this.highChainVal == 6.0D) {
/* 306 */           this.move = 0.825F;
/*     */         }
/* 308 */         if (this.highChainVal == 7.0D) {
/* 309 */           this.move = 0.875F;
/*     */         }
/* 311 */         if (this.highChainVal == 8.0D) {
/* 312 */           this.move = 0.925F;
/*     */         }
/* 314 */         if (this.highChainVal == 9.0D) {
/* 315 */           this.move = 0.975F;
/*     */         }
/* 317 */         if (this.highChainVal == 10.0D) {
/* 318 */           this.move = 1.05F;
/*     */         }
/* 320 */         if (this.highChainVal == 11.0D) {
/* 321 */           this.move = 1.1F;
/*     */         }
/* 323 */         if (this.highChainVal == 12.0D) {
/* 324 */           this.move = 1.1F;
/*     */         }
/* 326 */         if (this.highChainVal == 13.0D) {
/* 327 */           this.move = 1.15F;
/*     */         }
/* 329 */         if (this.highChainVal == 14.0D) {
/* 330 */           this.move = 1.15F;
/*     */         }
/* 332 */         if (this.highChainVal == 15.0D) {
/* 333 */           this.move = 1.175F;
/*     */         }
/* 335 */         if (this.highChainVal == 16.0D) {
/* 336 */           this.move = 1.175F;
/*     */         }
/* 338 */         if (this.highChainVal >= 17.0D) {
/* 339 */           this.move = 1.175F;
/*     */         }
/* 341 */         if (((Boolean)this.useTimer.getValue()).booleanValue()) {
/* 342 */           if (rnd) {
/* 343 */             Banzem.timerManager.setTimer(1.3F);
/*     */           } else {
/* 345 */             Banzem.timerManager.setTimer(1.0F);
/*     */           } 
/*     */         }
/*     */       } 
/* 349 */       EntityUtil.moveEntityStrafe(this.move, (Entity)mc.field_71439_g);
/*     */     } else {
/* 351 */       if (this.oneTime) {
/* 352 */         mc.field_71439_g.field_70181_x = -0.1D;
/* 353 */         this.oneTime = false;
/*     */       } 
/* 355 */       this.antiShake = false;
/* 356 */       this.highChainVal = 0.0D;
/* 357 */       this.lowChainVal = 0.0D;
/* 358 */       speedOff();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doOnground() {
/* 363 */     this.bounceHeight = 0.4D;
/* 364 */     this.move = 0.26F;
/* 365 */     if (mc.field_71439_g.field_70122_E) {
/* 366 */       this.startY = mc.field_71439_g.field_70163_u;
/*     */     }
/* 368 */     if (EntityUtil.getEntitySpeed((Entity)mc.field_71439_g) <= 1.0D) {
/* 369 */       this.lowChainVal = 1.0D;
/* 370 */       this.highChainVal = 1.0D;
/*     */     } 
/* 372 */     if (EntityUtil.isEntityMoving((Entity)mc.field_71439_g) && !mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid((Entity)mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid((Entity)mc.field_71439_g)) {
/* 373 */       this.oneTime = true;
/* 374 */       this.antiShake = (((Boolean)this.noShake.getValue()).booleanValue() && mc.field_71439_g.func_184187_bx() == null);
/* 375 */       Random random = new Random();
/* 376 */       boolean rnd = random.nextBoolean();
/* 377 */       if (mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
/* 378 */         mc.field_71439_g.field_70181_x = -this.bounceHeight;
/* 379 */         this.lowChainVal++;
/* 380 */         if (this.lowChainVal == 1.0D) {
/* 381 */           this.move = 0.075F;
/*     */         }
/* 383 */         if (this.lowChainVal == 2.0D) {
/* 384 */           this.move = 0.175F;
/*     */         }
/* 386 */         if (this.lowChainVal == 3.0D) {
/* 387 */           this.move = 0.275F;
/*     */         }
/* 389 */         if (this.lowChainVal == 4.0D) {
/* 390 */           this.move = 0.35F;
/*     */         }
/* 392 */         if (this.lowChainVal == 5.0D) {
/* 393 */           this.move = 0.375F;
/*     */         }
/* 395 */         if (this.lowChainVal == 6.0D) {
/* 396 */           this.move = 0.4F;
/*     */         }
/* 398 */         if (this.lowChainVal == 7.0D) {
/* 399 */           this.move = 0.425F;
/*     */         }
/* 401 */         if (this.lowChainVal == 8.0D) {
/* 402 */           this.move = 0.45F;
/*     */         }
/* 404 */         if (this.lowChainVal == 9.0D) {
/* 405 */           this.move = 0.475F;
/*     */         }
/* 407 */         if (this.lowChainVal == 10.0D) {
/* 408 */           this.move = 0.5F;
/*     */         }
/* 410 */         if (this.lowChainVal == 11.0D) {
/* 411 */           this.move = 0.5F;
/*     */         }
/* 413 */         if (this.lowChainVal == 12.0D) {
/* 414 */           this.move = 0.525F;
/*     */         }
/* 416 */         if (this.lowChainVal == 13.0D) {
/* 417 */           this.move = 0.525F;
/*     */         }
/* 419 */         if (this.lowChainVal == 14.0D) {
/* 420 */           this.move = 0.535F;
/*     */         }
/* 422 */         if (this.lowChainVal == 15.0D) {
/* 423 */           this.move = 0.535F;
/*     */         }
/* 425 */         if (this.lowChainVal == 16.0D) {
/* 426 */           this.move = 0.545F;
/*     */         }
/* 428 */         if (this.lowChainVal >= 17.0D) {
/* 429 */           this.move = 0.545F;
/*     */         }
/* 431 */         if (((Boolean)this.useTimer.getValue()).booleanValue()) {
/* 432 */           Banzem.timerManager.setTimer(1.0F);
/*     */         }
/*     */       } 
/* 435 */       if (mc.field_71439_g.field_70163_u == this.startY) {
/* 436 */         mc.field_71439_g.field_70181_x = this.bounceHeight;
/* 437 */         this.highChainVal++;
/* 438 */         if (this.highChainVal == 1.0D) {
/* 439 */           this.move = 0.075F;
/*     */         }
/* 441 */         if (this.highChainVal == 2.0D) {
/* 442 */           this.move = 0.175F;
/*     */         }
/* 444 */         if (this.highChainVal == 3.0D) {
/* 445 */           this.move = 0.375F;
/*     */         }
/* 447 */         if (this.highChainVal == 4.0D) {
/* 448 */           this.move = 0.6F;
/*     */         }
/* 450 */         if (this.highChainVal == 5.0D) {
/* 451 */           this.move = 0.775F;
/*     */         }
/* 453 */         if (this.highChainVal == 6.0D) {
/* 454 */           this.move = 0.825F;
/*     */         }
/* 456 */         if (this.highChainVal == 7.0D) {
/* 457 */           this.move = 0.875F;
/*     */         }
/* 459 */         if (this.highChainVal == 8.0D) {
/* 460 */           this.move = 0.925F;
/*     */         }
/* 462 */         if (this.highChainVal == 9.0D) {
/* 463 */           this.move = 0.975F;
/*     */         }
/* 465 */         if (this.highChainVal == 10.0D) {
/* 466 */           this.move = 1.05F;
/*     */         }
/* 468 */         if (this.highChainVal == 11.0D) {
/* 469 */           this.move = 1.1F;
/*     */         }
/* 471 */         if (this.highChainVal == 12.0D) {
/* 472 */           this.move = 1.1F;
/*     */         }
/* 474 */         if (this.highChainVal == 13.0D) {
/* 475 */           this.move = 1.15F;
/*     */         }
/* 477 */         if (this.highChainVal == 14.0D) {
/* 478 */           this.move = 1.15F;
/*     */         }
/* 480 */         if (this.highChainVal == 15.0D) {
/* 481 */           this.move = 1.175F;
/*     */         }
/* 483 */         if (this.highChainVal == 16.0D) {
/* 484 */           this.move = 1.175F;
/*     */         }
/* 486 */         if (this.highChainVal >= 17.0D) {
/* 487 */           this.move = 1.2F;
/*     */         }
/* 489 */         if (((Boolean)this.useTimer.getValue()).booleanValue()) {
/* 490 */           if (rnd) {
/* 491 */             Banzem.timerManager.setTimer(1.3F);
/*     */           } else {
/* 493 */             Banzem.timerManager.setTimer(1.0F);
/*     */           } 
/*     */         }
/*     */       } 
/* 497 */       EntityUtil.moveEntityStrafe(this.move, (Entity)mc.field_71439_g);
/*     */     } else {
/* 499 */       if (this.oneTime) {
/* 500 */         mc.field_71439_g.field_70181_x = -0.1D;
/* 501 */         this.oneTime = false;
/*     */       } 
/* 503 */       this.antiShake = false;
/* 504 */       this.highChainVal = 0.0D;
/* 505 */       this.lowChainVal = 0.0D;
/* 506 */       speedOff();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 512 */     if (this.mode.getValue() == Mode.ONGROUND || this.mode.getValue() == Mode.BOOST) {
/* 513 */       mc.field_71439_g.field_70181_x = -0.1D;
/*     */     }
/* 515 */     this.changeY = false;
/* 516 */     Banzem.timerManager.setTimer(1.0F);
/* 517 */     this.highChainVal = 0.0D;
/* 518 */     this.lowChainVal = 0.0D;
/* 519 */     this.antiShake = false;
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSettingChange(ClientEvent event) {
/* 524 */     if (event.getStage() == 2 && event.getSetting().equals(this.mode) && this.mode.getPlannedValue() == Mode.INSTANT) {
/* 525 */       mc.field_71439_g.field_70181_x = -0.1D;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/* 531 */     return this.mode.currentEnumName();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onMode(MoveEvent event) {
/* 536 */     if (!shouldReturn() && event.getStage() == 0 && this.mode.getValue() == Mode.INSTANT && !nullCheck() && !mc.field_71439_g.func_70093_af() && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab() && (mc.field_71439_g.field_71158_b.field_192832_b != 0.0F || mc.field_71439_g.field_71158_b.field_78902_a != 0.0F)) {
/* 537 */       if (mc.field_71439_g.field_70122_E && ((Boolean)this.strafeJump.getValue()).booleanValue()) {
/* 538 */         mc.field_71439_g.field_70181_x = 0.4D;
/* 539 */         event.setY(0.4D);
/*     */       } 
/* 541 */       MovementInput movementInput = mc.field_71439_g.field_71158_b;
/* 542 */       float moveForward = movementInput.field_192832_b;
/* 543 */       float moveStrafe = movementInput.field_78902_a;
/* 544 */       float rotationYaw = mc.field_71439_g.field_70177_z;
/* 545 */       if (moveForward == 0.0D && moveStrafe == 0.0D) {
/* 546 */         event.setX(0.0D);
/* 547 */         event.setZ(0.0D);
/*     */       } else {
/* 549 */         if (moveForward != 0.0D) {
/* 550 */           if (moveStrafe > 0.0D) {
/* 551 */             rotationYaw += ((moveForward > 0.0D) ? -45 : 45);
/* 552 */           } else if (moveStrafe < 0.0D) {
/* 553 */             rotationYaw += ((moveForward > 0.0D) ? 45 : -45);
/*     */           } 
/* 555 */           moveStrafe = 0.0F;
/* 556 */           float f = (moveForward == 0.0F) ? moveForward : (moveForward = (moveForward > 0.0D) ? 1.0F : -1.0F);
/*     */         } 
/* 558 */         moveStrafe = (moveStrafe == 0.0F) ? moveStrafe : ((moveStrafe > 0.0D) ? 1.0F : -1.0F);
/* 559 */         event.setX(moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians((rotationYaw + 90.0F))) + moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians((rotationYaw + 90.0F))));
/* 560 */         event.setZ(moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians((rotationYaw + 90.0F))) - moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians((rotationYaw + 90.0F))));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void speedOff() {
/* 566 */     float yaw = (float)Math.toRadians(mc.field_71439_g.field_70177_z);
/* 567 */     if (BlockUtil.isBlockAboveEntitySolid((Entity)mc.field_71439_g)) {
/* 568 */       if (mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && mc.field_71439_g.field_70122_E) {
/* 569 */         mc.field_71439_g.field_70159_w -= MathUtil.sin(yaw) * 0.15D;
/* 570 */         mc.field_71439_g.field_70179_y += MathUtil.cos(yaw) * 0.15D;
/*     */       } 
/* 572 */     } else if (mc.field_71439_g.field_70123_F) {
/* 573 */       if (mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && mc.field_71439_g.field_70122_E) {
/* 574 */         mc.field_71439_g.field_70159_w -= MathUtil.sin(yaw) * 0.03D;
/* 575 */         mc.field_71439_g.field_70179_y += MathUtil.cos(yaw) * 0.03D;
/*     */       } 
/* 577 */     } else if (!BlockUtil.isBlockBelowEntitySolid((Entity)mc.field_71439_g)) {
/* 578 */       if (mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && mc.field_71439_g.field_70122_E) {
/* 579 */         mc.field_71439_g.field_70159_w -= MathUtil.sin(yaw) * 0.03D;
/* 580 */         mc.field_71439_g.field_70179_y += MathUtil.cos(yaw) * 0.03D;
/*     */       } 
/*     */     } else {
/* 583 */       mc.field_71439_g.field_70159_w = 0.0D;
/* 584 */       mc.field_71439_g.field_70179_y = 0.0D;
/*     */     } 
/*     */   }
/*     */   
/*     */   public enum Mode {
/* 589 */     INSTANT,
/* 590 */     ONGROUND,
/* 591 */     ACCEL,
/* 592 */     BOOST,
/* 593 */     VANILLA;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\Speed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */