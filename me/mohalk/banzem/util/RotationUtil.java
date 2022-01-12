/*     */ package me.mohalk.banzem.util;
/*     */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketPlayer;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ 
/*     */ public class RotationUtil implements Util {
/*     */   public static Vec3d getEyesPos() {
/*  15 */     return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
/*     */   }
/*     */   
/*     */   public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
/*  19 */     double dirx = me.field_70165_t - px;
/*  20 */     double diry = me.field_70163_u - py;
/*  21 */     double dirz = me.field_70161_v - pz;
/*  22 */     double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
/*  23 */     double pitch = Math.asin(diry /= len);
/*  24 */     double yaw = Math.atan2(dirz /= len, dirx /= len);
/*  25 */     pitch = pitch * 180.0D / Math.PI;
/*  26 */     yaw = yaw * 180.0D / Math.PI;
/*  27 */     return new double[] { yaw += 90.0D, pitch };
/*     */   }
/*     */   
/*     */   public static float[] getLegitRotations(Vec3d vec) {
/*  31 */     Vec3d eyesPos = getEyesPos();
/*  32 */     double diffX = vec.field_72450_a - eyesPos.field_72450_a;
/*  33 */     double diffY = vec.field_72448_b - eyesPos.field_72448_b;
/*  34 */     double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
/*  35 */     double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
/*  36 */     float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
/*  37 */     float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
/*  38 */     return new float[] { mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - mc.field_71439_g.field_70177_z), mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - mc.field_71439_g.field_70125_A) };
/*     */   }
/*     */   
/*     */   public static float[] simpleFacing(EnumFacing facing) {
/*  42 */     switch (facing) {
/*     */       case DOWN:
/*  44 */         return new float[] { mc.field_71439_g.field_70177_z, 90.0F };
/*     */       
/*     */       case UP:
/*  47 */         return new float[] { mc.field_71439_g.field_70177_z, -90.0F };
/*     */       
/*     */       case NORTH:
/*  50 */         return new float[] { 180.0F, 0.0F };
/*     */       
/*     */       case SOUTH:
/*  53 */         return new float[] { 0.0F, 0.0F };
/*     */       
/*     */       case WEST:
/*  56 */         return new float[] { 90.0F, 0.0F };
/*     */     } 
/*     */     
/*  59 */     return new float[] { 270.0F, 0.0F };
/*     */   }
/*     */   
/*     */   public static void faceYawAndPitch(float yaw, float pitch) {
/*  63 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(yaw, pitch, mc.field_71439_g.field_70122_E));
/*     */   }
/*     */   
/*     */   public static void faceVector(Vec3d vec, boolean normalizeAngle) {
/*  67 */     float[] rotations = getLegitRotations(vec);
/*  68 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.func_180184_b((int)rotations[1], 360) : rotations[1], mc.field_71439_g.field_70122_E));
/*     */   }
/*     */   
/*     */   public static void faceEntity(Entity entity) {
/*  72 */     float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), entity.func_174824_e(mc.func_184121_ak()));
/*  73 */     faceYawAndPitch(angle[0], angle[1]);
/*     */   }
/*     */   
/*     */   public static float[] getAngle(Entity entity) {
/*  77 */     return MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), entity.func_174824_e(mc.func_184121_ak()));
/*     */   }
/*     */   
/*     */   public static float transformYaw() {
/*  81 */     float yaw = mc.field_71439_g.field_70177_z % 360.0F;
/*  82 */     if (mc.field_71439_g.field_70177_z > 0.0F) {
/*  83 */       if (yaw > 180.0F) {
/*  84 */         yaw = -180.0F + yaw - 180.0F;
/*     */       }
/*  86 */     } else if (yaw < -180.0F) {
/*  87 */       yaw = 180.0F + yaw + 180.0F;
/*     */     } 
/*  89 */     if (yaw < 0.0F) {
/*  90 */       return 180.0F + yaw;
/*     */     }
/*  92 */     return -180.0F + yaw;
/*     */   }
/*     */   
/*     */   public static boolean isInFov(BlockPos pos) {
/*  96 */     return (pos != null && (mc.field_71439_g.func_174818_b(pos) < 4.0D || yawDist(pos) < (getHalvedfov() + 2.0F)));
/*     */   }
/*     */   
/*     */   public static boolean isInFov(Entity entity) {
/* 100 */     return (entity != null && (mc.field_71439_g.func_70068_e(entity) < 4.0D || yawDist(entity) < (getHalvedfov() + 2.0F)));
/*     */   }
/*     */   
/*     */   public static double yawDist(BlockPos pos) {
/* 104 */     if (pos != null) {
/* 105 */       Vec3d difference = (new Vec3d((Vec3i)pos)).func_178788_d(mc.field_71439_g.func_174824_e(mc.func_184121_ak()));
/* 106 */       double d = Math.abs(mc.field_71439_g.field_70177_z - Math.toDegrees(Math.atan2(difference.field_72449_c, difference.field_72450_a)) - 90.0D) % 360.0D;
/* 107 */       return (d > 180.0D) ? (360.0D - d) : d;
/*     */     } 
/* 109 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public static double yawDist(Entity e) {
/* 113 */     if (e != null) {
/* 114 */       Vec3d difference = e.func_174791_d().func_72441_c(0.0D, (e.func_70047_e() / 2.0F), 0.0D).func_178788_d(mc.field_71439_g.func_174824_e(mc.func_184121_ak()));
/* 115 */       double d = Math.abs(mc.field_71439_g.field_70177_z - Math.toDegrees(Math.atan2(difference.field_72449_c, difference.field_72450_a)) - 90.0D) % 360.0D;
/* 116 */       return (d > 180.0D) ? (360.0D - d) : d;
/*     */     } 
/* 118 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public static boolean isInFov(Vec3d vec3d, Vec3d other) {
/* 122 */     if ((mc.field_71439_g.field_70125_A > 30.0F) ? (other.field_72448_b > mc.field_71439_g.field_70163_u) : (mc.field_71439_g.field_70125_A < -30.0F && other.field_72448_b < mc.field_71439_g.field_70163_u)) {
/* 123 */       return true;
/*     */     }
/* 125 */     float angle = MathUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
/* 126 */     if (angle < -270.0F) {
/* 127 */       return true;
/*     */     }
/* 129 */     float fov = (((Boolean)(ClickGui.getInstance()).customFov.getValue()).booleanValue() ? ((Float)(ClickGui.getInstance()).fov.getValue()).floatValue() : mc.field_71474_y.field_74334_X) / 2.0F;
/* 130 */     return (angle < fov + 10.0F && angle > -fov - 10.0F);
/*     */   }
/*     */   
/*     */   public static float getFov() {
/* 134 */     return ((Boolean)(ClickGui.getInstance()).customFov.getValue()).booleanValue() ? ((Float)(ClickGui.getInstance()).fov.getValue()).floatValue() : mc.field_71474_y.field_74334_X;
/*     */   }
/*     */   
/*     */   public static float getHalvedfov() {
/* 138 */     return getFov() / 2.0F;
/*     */   }
/*     */   
/*     */   public static int getDirection4D() {
/* 142 */     return MathHelper.func_76128_c((mc.field_71439_g.field_70177_z * 4.0F / 360.0F) + 0.5D) & 0x3;
/*     */   }
/*     */   
/*     */   public static String getDirection4D(boolean northRed) {
/* 146 */     int dirnumber = getDirection4D();
/* 147 */     if (dirnumber == 0) {
/* 148 */       return "South (+Z)";
/*     */     }
/* 150 */     if (dirnumber == 1) {
/* 151 */       return "West (-X)";
/*     */     }
/* 153 */     if (dirnumber == 2) {
/* 154 */       return (northRed ? "§c" : "") + "North (-Z)";
/*     */     }
/* 156 */     if (dirnumber == 3) {
/* 157 */       return "East (+X)";
/*     */     }
/* 159 */     return "Loading...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\RotationUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */