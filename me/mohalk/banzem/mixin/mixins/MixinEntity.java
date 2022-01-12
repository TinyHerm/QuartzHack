/*     */ package me.mohalk.banzem.mixin.mixins;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import me.mohalk.banzem.event.events.PushEvent;
/*     */ import me.mohalk.banzem.event.events.StepEvent;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.crash.CrashReport;
/*     */ import net.minecraft.crash.CrashReportCategory;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.MoverType;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.SoundEvents;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.ReportedException;
/*     */ import net.minecraft.util.SoundEvent;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.common.MinecraftForge;
/*     */ import net.minecraftforge.fml.common.eventhandler.Event;
/*     */ import org.spongepowered.asm.mixin.Final;
/*     */ import org.spongepowered.asm.mixin.Mixin;
/*     */ import org.spongepowered.asm.mixin.Overwrite;
/*     */ import org.spongepowered.asm.mixin.Shadow;
/*     */ import org.spongepowered.asm.mixin.injection.At;
/*     */ import org.spongepowered.asm.mixin.injection.Redirect;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Mixin({Entity.class})
/*     */ public abstract class MixinEntity
/*     */ {
/*     */   @Shadow
/*     */   public double field_70165_t;
/*     */   @Shadow
/*     */   public double field_70163_u;
/*     */   @Shadow
/*     */   public double field_70161_v;
/*     */   @Shadow
/*     */   public double field_70159_w;
/*     */   @Shadow
/*     */   public double field_70181_x;
/*     */   @Shadow
/*     */   public double field_70179_y;
/*     */   @Shadow
/*     */   public float field_70177_z;
/*     */   @Shadow
/*     */   public float field_70125_A;
/*     */   @Shadow
/*     */   public boolean field_70122_E;
/*     */   @Shadow
/*     */   public boolean field_70145_X;
/*     */   @Shadow
/*     */   public float field_70141_P;
/*     */   @Shadow
/*     */   public World field_70170_p;
/*     */   @Shadow
/*     */   @Final
/*     */   private double[] field_191505_aI;
/*     */   @Shadow
/*     */   private long field_191506_aJ;
/*     */   @Shadow
/*     */   protected boolean field_70134_J;
/*     */   @Shadow
/*     */   public float field_70138_W;
/*     */   @Shadow
/*     */   public boolean field_70123_F;
/*     */   @Shadow
/*     */   public boolean field_70124_G;
/*     */   @Shadow
/*     */   public boolean field_70132_H;
/*     */   @Shadow
/*     */   public float field_70140_Q;
/*     */   @Shadow
/*     */   public float field_82151_R;
/*     */   @Shadow
/*     */   private int field_190534_ay;
/*     */   @Shadow
/*     */   private int field_70150_b;
/*     */   @Shadow
/*     */   private float field_191959_ay;
/*     */   @Shadow
/*     */   protected Random field_70146_Z;
/*     */   
/*     */   @Shadow
/*     */   public abstract boolean func_70051_ag();
/*     */   
/*     */   @Shadow
/*     */   public abstract boolean func_184218_aH();
/*     */   
/*     */   @Shadow
/*     */   public abstract boolean func_70093_af();
/*     */   
/*     */   @Shadow
/*     */   public abstract void func_174826_a(AxisAlignedBB paramAxisAlignedBB);
/*     */   
/*     */   @Shadow
/*     */   public abstract AxisAlignedBB func_174813_aQ();
/*     */   
/*     */   @Shadow
/*     */   public abstract void func_174829_m();
/*     */   
/*     */   @Shadow
/*     */   protected abstract void func_184231_a(double paramDouble, boolean paramBoolean, IBlockState paramIBlockState, BlockPos paramBlockPos);
/*     */   
/*     */   @Shadow
/*     */   protected abstract boolean func_70041_e_();
/*     */   
/*     */   @Shadow
/*     */   public abstract boolean func_70090_H();
/*     */   
/*     */   @Shadow
/*     */   public abstract boolean func_184207_aI();
/*     */   
/*     */   @Shadow
/*     */   public abstract Entity func_184179_bs();
/*     */   
/*     */   @Shadow
/*     */   public abstract void func_184185_a(SoundEvent paramSoundEvent, float paramFloat1, float paramFloat2);
/*     */   
/*     */   @Shadow
/*     */   protected abstract void func_145775_I();
/*     */   
/*     */   @Shadow
/*     */   public abstract boolean func_70026_G();
/*     */   
/*     */   @Shadow
/*     */   protected abstract void func_180429_a(BlockPos paramBlockPos, Block paramBlock);
/*     */   
/*     */   @Shadow
/*     */   protected abstract SoundEvent func_184184_Z();
/*     */   
/*     */   @Shadow
/*     */   protected abstract float func_191954_d(float paramFloat);
/*     */   
/*     */   @Shadow
/*     */   protected abstract boolean func_191957_ae();
/*     */   
/*     */   @Shadow
/*     */   public abstract void func_85029_a(CrashReportCategory paramCrashReportCategory);
/*     */   
/*     */   @Shadow
/*     */   protected abstract void func_70081_e(int paramInt);
/*     */   
/*     */   @Shadow
/*     */   public abstract void func_70015_d(int paramInt);
/*     */   
/*     */   @Shadow
/*     */   protected abstract int func_190531_bD();
/*     */   
/*     */   @Shadow
/*     */   public abstract boolean func_70027_ad();
/*     */   
/*     */   @Shadow
/*     */   public abstract int func_82145_z();
/*     */   
/*     */   @Overwrite
/*     */   public void func_70091_d(MoverType type, double x, double y, double z) {
/* 166 */     Entity _this = (Entity)this;
/* 167 */     if (this.field_70145_X) {
/* 168 */       func_174826_a(func_174813_aQ().func_72317_d(x, y, z));
/* 169 */       func_174829_m();
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 175 */       if (type == MoverType.PISTON) {
/* 176 */         long i = this.field_70170_p.func_82737_E();
/* 177 */         if (i != this.field_191506_aJ) {
/* 178 */           Arrays.fill(this.field_191505_aI, 0.0D);
/* 179 */           this.field_191506_aJ = i;
/*     */         } 
/* 181 */         if (x != 0.0D) {
/* 182 */           int j = EnumFacing.Axis.X.ordinal();
/* 183 */           double d0 = MathHelper.func_151237_a(x + this.field_191505_aI[j], -0.51D, 0.51D);
/* 184 */           x = d0 - this.field_191505_aI[j];
/* 185 */           this.field_191505_aI[j] = d0;
/* 186 */           if (Math.abs(x) <= 9.999999747378752E-6D) {
/*     */             return;
/*     */           }
/* 189 */         } else if (y != 0.0D) {
/* 190 */           int l4 = EnumFacing.Axis.Y.ordinal();
/* 191 */           double d12 = MathHelper.func_151237_a(y + this.field_191505_aI[l4], -0.51D, 0.51D);
/* 192 */           y = d12 - this.field_191505_aI[l4];
/* 193 */           this.field_191505_aI[l4] = d12;
/* 194 */           if (Math.abs(y) <= 9.999999747378752E-6D) {
/*     */             return;
/*     */           }
/*     */         } else {
/* 198 */           if (z == 0.0D) {
/*     */             return;
/*     */           }
/* 201 */           int i5 = EnumFacing.Axis.Z.ordinal();
/* 202 */           double d13 = MathHelper.func_151237_a(z + this.field_191505_aI[i5], -0.51D, 0.51D);
/* 203 */           z = d13 - this.field_191505_aI[i5];
/* 204 */           this.field_191505_aI[i5] = d13;
/* 205 */           if (Math.abs(z) <= 9.999999747378752E-6D) {
/*     */             return;
/*     */           }
/*     */         } 
/*     */       } 
/* 210 */       this.field_70170_p.field_72984_F.func_76320_a("move");
/* 211 */       double d10 = this.field_70165_t;
/* 212 */       double d11 = this.field_70163_u;
/* 213 */       double d1 = this.field_70161_v;
/* 214 */       if (this.field_70134_J) {
/* 215 */         this.field_70134_J = false;
/* 216 */         x *= 0.25D;
/* 217 */         y *= 0.05000000074505806D;
/* 218 */         z *= 0.25D;
/* 219 */         this.field_70159_w = 0.0D;
/* 220 */         this.field_70181_x = 0.0D;
/* 221 */         this.field_70179_y = 0.0D;
/*     */       } 
/* 223 */       double d2 = x;
/* 224 */       double d3 = y;
/* 225 */       double d4 = z;
/* 226 */       if ((type == MoverType.SELF || type == MoverType.PLAYER) && this.field_70122_E && func_70093_af() && _this instanceof net.minecraft.entity.player.EntityPlayer) {
/* 227 */         double d5 = 0.05D;
/* 228 */         while (x != 0.0D && this.field_70170_p.func_184144_a(_this, func_174813_aQ().func_72317_d(x, -this.field_70138_W, 0.0D)).isEmpty()) {
/* 229 */           x = (x < 0.05D && x >= -0.05D) ? 0.0D : ((x > 0.0D) ? (x -= 0.05D) : (x += 0.05D));
/* 230 */           d2 = x;
/*     */         } 
/* 232 */         while (z != 0.0D && this.field_70170_p.func_184144_a(_this, func_174813_aQ().func_72317_d(0.0D, -this.field_70138_W, z)).isEmpty()) {
/* 233 */           z = (z < 0.05D && z >= -0.05D) ? 0.0D : ((z > 0.0D) ? (z -= 0.05D) : (z += 0.05D));
/* 234 */           d4 = z;
/*     */         } 
/* 236 */         while (x != 0.0D && z != 0.0D && this.field_70170_p.func_184144_a(_this, func_174813_aQ().func_72317_d(x, -this.field_70138_W, z)).isEmpty()) {
/* 237 */           x = (x < 0.05D && x >= -0.05D) ? 0.0D : ((x > 0.0D) ? (x -= 0.05D) : (x += 0.05D));
/* 238 */           d2 = x;
/* 239 */           z = (z < 0.05D && z >= -0.05D) ? 0.0D : ((z > 0.0D) ? (z -= 0.05D) : (z += 0.05D));
/* 240 */           d4 = z;
/*     */         } 
/*     */       } 
/* 243 */       List<AxisAlignedBB> list1 = this.field_70170_p.func_184144_a(_this, func_174813_aQ().func_72321_a(x, y, z));
/* 244 */       AxisAlignedBB axisalignedbb = func_174813_aQ();
/* 245 */       if (y != 0.0D) {
/* 246 */         int l = list1.size();
/* 247 */         for (int k = 0; k < l; k++) {
/* 248 */           y = ((AxisAlignedBB)list1.get(k)).func_72323_b(func_174813_aQ(), y);
/*     */         }
/* 250 */         func_174826_a(func_174813_aQ().func_72317_d(0.0D, y, 0.0D));
/*     */       } 
/* 252 */       if (x != 0.0D) {
/* 253 */         int l5 = list1.size();
/* 254 */         for (int j5 = 0; j5 < l5; j5++) {
/* 255 */           x = ((AxisAlignedBB)list1.get(j5)).func_72316_a(func_174813_aQ(), x);
/*     */         }
/* 257 */         if (x != 0.0D) {
/* 258 */           func_174826_a(func_174813_aQ().func_72317_d(x, 0.0D, 0.0D));
/*     */         }
/*     */       } 
/* 261 */       if (z != 0.0D) {
/* 262 */         int i6 = list1.size();
/* 263 */         for (int k5 = 0; k5 < i6; k5++) {
/* 264 */           z = ((AxisAlignedBB)list1.get(k5)).func_72322_c(func_174813_aQ(), z);
/*     */         }
/* 266 */         if (z != 0.0D) {
/* 267 */           func_174826_a(func_174813_aQ().func_72317_d(0.0D, 0.0D, z));
/*     */         }
/*     */       } 
/* 270 */       boolean flag = (this.field_70122_E || (d3 != y && d3 < 0.0D)), bl = flag;
/* 271 */       if (this.field_70138_W > 0.0F && flag && (d2 != x || d4 != z)) {
/* 272 */         StepEvent preEvent = new StepEvent(0, _this);
/* 273 */         MinecraftForge.EVENT_BUS.post((Event)preEvent);
/* 274 */         double d14 = x;
/* 275 */         double d6 = y;
/* 276 */         double d7 = z;
/* 277 */         AxisAlignedBB axisalignedbb1 = func_174813_aQ();
/* 278 */         func_174826_a(axisalignedbb);
/* 279 */         y = preEvent.getHeight();
/* 280 */         List<AxisAlignedBB> list = this.field_70170_p.func_184144_a(_this, func_174813_aQ().func_72321_a(d2, y, d4));
/* 281 */         AxisAlignedBB axisalignedbb2 = func_174813_aQ();
/* 282 */         AxisAlignedBB axisalignedbb3 = axisalignedbb2.func_72321_a(d2, 0.0D, d4);
/* 283 */         double d8 = y;
/* 284 */         int k1 = list.size();
/* 285 */         for (int j1 = 0; j1 < k1; j1++) {
/* 286 */           d8 = ((AxisAlignedBB)list.get(j1)).func_72323_b(axisalignedbb3, d8);
/*     */         }
/* 288 */         axisalignedbb2 = axisalignedbb2.func_72317_d(0.0D, d8, 0.0D);
/* 289 */         double d18 = d2;
/* 290 */         int i2 = list.size();
/* 291 */         for (int l1 = 0; l1 < i2; l1++) {
/* 292 */           d18 = ((AxisAlignedBB)list.get(l1)).func_72316_a(axisalignedbb2, d18);
/*     */         }
/* 294 */         axisalignedbb2 = axisalignedbb2.func_72317_d(d18, 0.0D, 0.0D);
/* 295 */         double d19 = d4;
/* 296 */         int k2 = list.size();
/* 297 */         for (int j2 = 0; j2 < k2; j2++) {
/* 298 */           d19 = ((AxisAlignedBB)list.get(j2)).func_72322_c(axisalignedbb2, d19);
/*     */         }
/* 300 */         axisalignedbb2 = axisalignedbb2.func_72317_d(0.0D, 0.0D, d19);
/* 301 */         AxisAlignedBB axisalignedbb4 = func_174813_aQ();
/* 302 */         double d20 = y;
/* 303 */         int i3 = list.size();
/* 304 */         for (int l2 = 0; l2 < i3; l2++) {
/* 305 */           d20 = ((AxisAlignedBB)list.get(l2)).func_72323_b(axisalignedbb4, d20);
/*     */         }
/* 307 */         axisalignedbb4 = axisalignedbb4.func_72317_d(0.0D, d20, 0.0D);
/* 308 */         double d21 = d2;
/* 309 */         int k3 = list.size();
/* 310 */         for (int j3 = 0; j3 < k3; j3++) {
/* 311 */           d21 = ((AxisAlignedBB)list.get(j3)).func_72316_a(axisalignedbb4, d21);
/*     */         }
/* 313 */         axisalignedbb4 = axisalignedbb4.func_72317_d(d21, 0.0D, 0.0D);
/* 314 */         double d22 = d4;
/* 315 */         int i4 = list.size();
/* 316 */         for (int l3 = 0; l3 < i4; l3++) {
/* 317 */           d22 = ((AxisAlignedBB)list.get(l3)).func_72322_c(axisalignedbb4, d22);
/*     */         }
/* 319 */         axisalignedbb4 = axisalignedbb4.func_72317_d(0.0D, 0.0D, d22);
/* 320 */         double d23 = d18 * d18 + d19 * d19;
/* 321 */         double d9 = d21 * d21 + d22 * d22;
/* 322 */         if (d23 > d9) {
/* 323 */           x = d18;
/* 324 */           z = d19;
/* 325 */           y = -d8;
/* 326 */           func_174826_a(axisalignedbb2);
/*     */         } else {
/* 328 */           x = d21;
/* 329 */           z = d22;
/* 330 */           y = -d20;
/* 331 */           func_174826_a(axisalignedbb4);
/*     */         } 
/* 333 */         int k4 = list.size();
/* 334 */         for (int j4 = 0; j4 < k4; j4++) {
/* 335 */           y = ((AxisAlignedBB)list.get(j4)).func_72323_b(func_174813_aQ(), y);
/*     */         }
/* 337 */         func_174826_a(func_174813_aQ().func_72317_d(0.0D, y, 0.0D));
/* 338 */         if (d14 * d14 + d7 * d7 >= x * x + z * z) {
/* 339 */           x = d14;
/* 340 */           y = d6;
/* 341 */           z = d7;
/* 342 */           func_174826_a(axisalignedbb1);
/*     */         } else {
/* 344 */           StepEvent postEvent = new StepEvent(1, _this);
/* 345 */           MinecraftForge.EVENT_BUS.post((Event)postEvent);
/*     */         } 
/*     */       } 
/* 348 */       this.field_70170_p.field_72984_F.func_76319_b();
/* 349 */       this.field_70170_p.field_72984_F.func_76320_a("rest");
/* 350 */       func_174829_m();
/* 351 */       this.field_70123_F = (d2 != x || d4 != z);
/* 352 */       this.field_70124_G = (d3 != y);
/* 353 */       this.field_70122_E = (this.field_70124_G && d3 < 0.0D);
/* 354 */       this.field_70132_H = (this.field_70123_F || this.field_70124_G);
/* 355 */       int j6 = MathHelper.func_76128_c(this.field_70165_t);
/* 356 */       int i1 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
/* 357 */       int k6 = MathHelper.func_76128_c(this.field_70161_v);
/* 358 */       BlockPos blockpos = new BlockPos(j6, i1, k6);
/* 359 */       IBlockState iblockstate = this.field_70170_p.func_180495_p(blockpos); BlockPos blockpos1; IBlockState iblockstate1; Block block1;
/* 360 */       if (iblockstate.func_185904_a() == Material.field_151579_a && (block1 = (iblockstate1 = this.field_70170_p.func_180495_p(blockpos1 = blockpos.func_177977_b())).func_177230_c() instanceof net.minecraft.block.BlockFence || block1 instanceof net.minecraft.block.BlockWall || block1 instanceof net.minecraft.block.BlockFenceGate)) {
/* 361 */         iblockstate = iblockstate1;
/* 362 */         blockpos = blockpos1;
/*     */       } 
/* 364 */       func_184231_a(y, this.field_70122_E, iblockstate, blockpos);
/* 365 */       if (d2 != x) {
/* 366 */         this.field_70159_w = 0.0D;
/*     */       }
/* 368 */       if (d4 != z) {
/* 369 */         this.field_70179_y = 0.0D;
/*     */       }
/* 371 */       Block block = iblockstate.func_177230_c();
/* 372 */       if (d3 != y) {
/* 373 */         block.func_176216_a(this.field_70170_p, _this);
/*     */       }
/* 375 */       if (func_70041_e_() && (!this.field_70122_E || !func_70093_af() || !(_this instanceof net.minecraft.entity.player.EntityPlayer)) && !func_184218_aH()) {
/* 376 */         double d15 = this.field_70165_t - d10;
/* 377 */         double d16 = this.field_70163_u - d11;
/* 378 */         double d17 = this.field_70161_v - d1;
/* 379 */         if (block != Blocks.field_150468_ap) {
/* 380 */           d16 = 0.0D;
/*     */         }
/* 382 */         if (block != null && this.field_70122_E) {
/* 383 */           block.func_176199_a(this.field_70170_p, blockpos, _this);
/*     */         }
/* 385 */         this.field_70140_Q = (float)(this.field_70140_Q + MathHelper.func_76133_a(d15 * d15 + d17 * d17) * 0.6D);
/* 386 */         this.field_82151_R = (float)(this.field_82151_R + MathHelper.func_76133_a(d15 * d15 + d16 * d16 + d17 * d17) * 0.6D);
/* 387 */         if (this.field_82151_R > this.field_70150_b && iblockstate.func_185904_a() != Material.field_151579_a) {
/* 388 */           this.field_70150_b = (int)this.field_82151_R + 1;
/* 389 */           if (func_70090_H()) {
/* 390 */             Entity entity = (func_184207_aI() && func_184179_bs() != null) ? func_184179_bs() : _this;
/* 391 */             float f = (entity == _this) ? 0.35F : 0.4F;
/* 392 */             float f1 = MathHelper.func_76133_a(entity.field_70159_w * entity.field_70159_w * 0.20000000298023224D + entity.field_70181_x * entity.field_70181_x + entity.field_70179_y * entity.field_70179_y * 0.20000000298023224D) * f;
/* 393 */             if (f1 > 1.0F) {
/* 394 */               f1 = 1.0F;
/*     */             }
/* 396 */             func_184185_a(func_184184_Z(), f1, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
/*     */           } else {
/* 398 */             func_180429_a(blockpos, block);
/*     */           } 
/* 400 */         } else if (this.field_82151_R > this.field_191959_ay && func_191957_ae() && iblockstate.func_185904_a() == Material.field_151579_a) {
/* 401 */           this.field_191959_ay = func_191954_d(this.field_82151_R);
/*     */         } 
/*     */       } 
/*     */       try {
/* 405 */         func_145775_I();
/*     */       }
/* 407 */       catch (Throwable throwable) {
/* 408 */         CrashReport crashreport = CrashReport.func_85055_a(throwable, "Checking entity block collision");
/* 409 */         CrashReportCategory crashreportcategory = crashreport.func_85058_a("Entity being checked for collision");
/* 410 */         func_85029_a(crashreportcategory);
/* 411 */         throw new ReportedException(crashreport);
/*     */       } 
/* 413 */       boolean flag1 = func_70026_G();
/* 414 */       if (this.field_70170_p.func_147470_e(func_174813_aQ().func_186664_h(0.001D))) {
/* 415 */         func_70081_e(1);
/* 416 */         if (!flag1) {
/* 417 */           this.field_190534_ay++;
/* 418 */           if (this.field_190534_ay == 0) {
/* 419 */             func_70015_d(8);
/*     */           }
/*     */         } 
/* 422 */       } else if (this.field_190534_ay <= 0) {
/* 423 */         this.field_190534_ay = -func_190531_bD();
/*     */       } 
/* 425 */       if (flag1 && func_70027_ad()) {
/* 426 */         func_184185_a(SoundEvents.field_187541_bC, 0.7F, 1.6F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
/* 427 */         this.field_190534_ay = -func_190531_bD();
/*     */       } 
/* 429 */       this.field_70170_p.field_72984_F.func_76319_b();
/*     */     } 
/*     */   }
/*     */   
/*     */   @Redirect(method = {"applyEntityCollision"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
/*     */   public void addVelocityHook(Entity entity, double x, double y, double z) {
/* 435 */     PushEvent event = new PushEvent(entity, x, y, z, true);
/* 436 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 437 */     if (!event.isCanceled()) {
/* 438 */       entity.field_70159_w += event.x;
/* 439 */       entity.field_70181_x += event.y;
/* 440 */       entity.field_70179_y += event.z;
/* 441 */       entity.field_70160_al = event.airbone;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */