/*     */ package me.mohalk.banzem.util;
/*     */ import com.google.common.util.concurrent.AtomicDouble;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.stream.Collectors;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.BlockWorkbench;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketAnimation;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketHeldItemChange;
/*     */ import net.minecraft.network.play.client.CPacketPlayerDigging;
/*     */ import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.NonNullList;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.RayTraceResult;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ import net.minecraft.world.IBlockAccess;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class BlockUtil implements Util {
/*  37 */   public static final List<Block> blackList = Arrays.asList(new Block[] { Blocks.field_150477_bB, (Block)Blocks.field_150486_ae, Blocks.field_150447_bR, Blocks.field_150462_ai, Blocks.field_150467_bQ, Blocks.field_150382_bo, (Block)Blocks.field_150438_bZ, Blocks.field_150409_cd, Blocks.field_150367_z, Blocks.field_150415_aT, Blocks.field_150381_bn });
/*  38 */   public static final List<Block> shulkerList = Arrays.asList(new Block[] { Blocks.field_190977_dl, Blocks.field_190978_dm, Blocks.field_190979_dn, Blocks.field_190980_do, Blocks.field_190981_dp, Blocks.field_190982_dq, Blocks.field_190983_dr, Blocks.field_190984_ds, Blocks.field_190985_dt, Blocks.field_190986_du, Blocks.field_190987_dv, Blocks.field_190988_dw, Blocks.field_190989_dx, Blocks.field_190990_dy, Blocks.field_190991_dz, Blocks.field_190975_dA });
/*  39 */   public static List<Block> unSolidBlocks = Arrays.asList(new Block[] { (Block)Blocks.field_150356_k, Blocks.field_150457_bL, Blocks.field_150433_aE, Blocks.field_150404_cg, Blocks.field_185764_cQ, (Block)Blocks.field_150465_bP, Blocks.field_150457_bL, Blocks.field_150473_bD, (Block)Blocks.field_150479_bC, Blocks.field_150471_bO, Blocks.field_150442_at, Blocks.field_150430_aB, Blocks.field_150468_ap, (Block)Blocks.field_150441_bU, (Block)Blocks.field_150455_bV, (Block)Blocks.field_150413_aR, (Block)Blocks.field_150416_aS, Blocks.field_150437_az, Blocks.field_150429_aA, (Block)Blocks.field_150488_af, Blocks.field_150350_a, (Block)Blocks.field_150427_aO, Blocks.field_150384_bq, (Block)Blocks.field_150355_j, (Block)Blocks.field_150358_i, (Block)Blocks.field_150353_l, (Block)Blocks.field_150356_k, Blocks.field_150345_g, (Block)Blocks.field_150328_O, (Block)Blocks.field_150327_N, (Block)Blocks.field_150338_P, (Block)Blocks.field_150337_Q, Blocks.field_150464_aj, Blocks.field_150459_bM, Blocks.field_150469_bN, Blocks.field_185773_cZ, (Block)Blocks.field_150436_aH, Blocks.field_150393_bb, Blocks.field_150394_bc, Blocks.field_150392_bi, Blocks.field_150388_bm, Blocks.field_150375_by, Blocks.field_185766_cS, Blocks.field_185765_cR, (Block)Blocks.field_150329_H, (Block)Blocks.field_150330_I, Blocks.field_150395_bd, (Block)Blocks.field_150480_ab, Blocks.field_150448_aq, Blocks.field_150408_cc, Blocks.field_150319_E, Blocks.field_150318_D, Blocks.field_150478_aa });
/*     */   
/*     */   private static BlockPos _currBlock;
/*     */   private static boolean _started;
/*     */   
/*     */   public static List<BlockPos> getBlockSphere(float breakRange, Class<BlockWorkbench> clazz) {
/*  45 */     NonNullList positions = NonNullList.func_191196_a();
/*  46 */     positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.field_71439_g), breakRange, (int)breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(mc.field_71441_e.func_180495_p(pos).func_177230_c())).collect(Collectors.toList()));
/*  47 */     return (List<BlockPos>)positions;
/*     */   } public static EnumFacing getFacing(BlockPos pos) {
/*     */     EnumFacing[] arrayOfEnumFacing;
/*     */     int i;
/*     */     byte b;
/*  52 */     for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) { EnumFacing facing = arrayOfEnumFacing[b];
/*  53 */       RayTraceResult rayTraceResult = mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(pos.func_177958_n() + 0.5D + facing.func_176730_m().func_177958_n() / 2.0D, pos.func_177956_o() + 0.5D + facing.func_176730_m().func_177956_o() / 2.0D, pos.func_177952_p() + 0.5D + facing.func_176730_m().func_177952_p() / 2.0D), false, true, false);
/*  54 */       if (rayTraceResult != null && (rayTraceResult.field_72313_a != RayTraceResult.Type.BLOCK || !rayTraceResult.func_178782_a().equals(pos))) {
/*     */         b++; continue;
/*  56 */       }  return facing; }
/*     */     
/*  58 */     if (pos.func_177956_o() > mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e()) {
/*  59 */       return EnumFacing.DOWN;
/*     */     }
/*  61 */     return EnumFacing.UP;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<EnumFacing> getPossibleSides(BlockPos pos) {
/*  66 */     ArrayList<EnumFacing> facings = new ArrayList<>();
/*  67 */     if (mc.field_71441_e == null || pos == null) {
/*  68 */       return facings;
/*     */     }
/*  70 */     for (EnumFacing side : EnumFacing.values()) {
/*  71 */       BlockPos neighbour = pos.func_177972_a(side);
/*  72 */       IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
/*  73 */       if (blockState.func_177230_c().func_176209_a(blockState, false) && !blockState.func_185904_a().func_76222_j())
/*     */       {
/*  75 */         facings.add(side); } 
/*     */     } 
/*  77 */     return facings;
/*     */   }
/*     */ 
/*     */   
/*     */   public static EnumFacing getFirstFacing(BlockPos pos) {
/*  82 */     Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
/*  83 */     if (iterator.hasNext()) {
/*  84 */       return iterator.next();
/*     */     }
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static EnumFacing getRayTraceFacing(BlockPos pos) {
/*  91 */     RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(pos.func_177958_n() + 0.5D, pos.func_177958_n() - 0.5D, pos.func_177958_n() + 0.5D));
/*  92 */     if (result == null || result.field_178784_b == null) {
/*  93 */       return EnumFacing.UP;
/*     */     }
/*  95 */     return result.field_178784_b;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
/* 100 */     return isPositionPlaceable(pos, rayTrace, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
/* 105 */     Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
/* 106 */     if (!(block instanceof net.minecraft.block.BlockAir) && !(block instanceof net.minecraft.block.BlockLiquid) && !(block instanceof net.minecraft.block.BlockTallGrass) && !(block instanceof net.minecraft.block.BlockFire) && !(block instanceof net.minecraft.block.BlockDeadBush) && !(block instanceof net.minecraft.block.BlockSnow)) {
/* 107 */       return 0;
/*     */     }
/* 109 */     if (!rayTracePlaceCheck(pos, rayTrace, 0.0F)) {
/* 110 */       return -1;
/*     */     }
/* 112 */     if (entityCheck) {
/* 113 */       for (Entity entity : mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos))) {
/* 114 */         if (entity instanceof net.minecraft.entity.item.EntityItem || entity instanceof net.minecraft.entity.item.EntityXPOrb || entity instanceof net.minecraft.entity.projectile.EntityArrow || entity instanceof net.minecraft.entity.item.EntityExpBottle)
/*     */           continue; 
/* 116 */         return 1;
/*     */       } 
/*     */     }
/* 119 */     for (EnumFacing side : getPossibleSides(pos)) {
/* 120 */       if (!canBeClicked(pos.func_177972_a(side)))
/* 121 */         continue;  return 3;
/*     */     } 
/* 123 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
/* 128 */     if (packet) {
/* 129 */       float f = (float)(vec.field_72450_a - pos.func_177958_n());
/* 130 */       float f1 = (float)(vec.field_72448_b - pos.func_177956_o());
/* 131 */       float f2 = (float)(vec.field_72449_c - pos.func_177952_p());
/* 132 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
/*     */     } else {
/* 134 */       mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, direction, vec, hand);
/*     */     } 
/* 136 */     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 137 */     mc.field_71467_ac = 4;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void rightClickBed(BlockPos pos, float range, boolean rotate, EnumHand hand, AtomicDouble yaw, AtomicDouble pitch, AtomicBoolean rotating, boolean packet) {
/* 142 */     Vec3d posVec = (new Vec3d((Vec3i)pos)).func_72441_c(0.5D, 0.5D, 0.5D);
/* 143 */     RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), posVec);
/* 144 */     EnumFacing face = (result == null || result.field_178784_b == null) ? EnumFacing.UP : result.field_178784_b;
/* 145 */     RotationUtil.getEyesPos();
/* 146 */     if (rotate) {
/* 147 */       float[] rotations = RotationUtil.getLegitRotations(posVec);
/* 148 */       yaw.set(rotations[0]);
/* 149 */       pitch.set(rotations[1]);
/* 150 */       rotating.set(true);
/*     */     } 
/* 152 */     rightClickBlock(pos, posVec, hand, face, packet);
/* 153 */     mc.field_71439_g.func_184609_a(hand);
/* 154 */     mc.field_71467_ac = 4;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void rightClickBlockLegit(BlockPos pos, float range, boolean rotate, EnumHand hand, AtomicDouble Yaw2, AtomicDouble Pitch, AtomicBoolean rotating, boolean packet) {
/* 159 */     Vec3d eyesPos = RotationUtil.getEyesPos();
/* 160 */     Vec3d posVec = (new Vec3d((Vec3i)pos)).func_72441_c(0.5D, 0.5D, 0.5D);
/* 161 */     double distanceSqPosVec = eyesPos.func_72436_e(posVec); EnumFacing[] arrayOfEnumFacing; int i; byte b;
/* 162 */     for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) { EnumFacing side = arrayOfEnumFacing[b];
/* 163 */       Vec3d hitVec = posVec.func_178787_e((new Vec3d(side.func_176730_m())).func_186678_a(0.5D));
/* 164 */       double distanceSqHitVec = eyesPos.func_72436_e(hitVec);
/* 165 */       if (distanceSqHitVec > MathUtil.square(range) || distanceSqHitVec >= distanceSqPosVec || mc.field_71441_e.func_147447_a(eyesPos, hitVec, false, true, false) != null) {
/*     */         b++; continue;
/* 167 */       }  if (rotate) {
/* 168 */         float[] rotations = RotationUtil.getLegitRotations(hitVec);
/* 169 */         Yaw2.set(rotations[0]);
/* 170 */         Pitch.set(rotations[1]);
/* 171 */         rotating.set(true);
/*     */       } 
/* 173 */       rightClickBlock(pos, hitVec, hand, side, packet);
/* 174 */       mc.field_71439_g.func_184609_a(hand);
/* 175 */       mc.field_71467_ac = 4; }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
/* 182 */     boolean sneaking = false;
/* 183 */     EnumFacing side = getFirstFacing(pos);
/* 184 */     if (side == null) {
/* 185 */       return isSneaking;
/*     */     }
/* 187 */     BlockPos neighbour = pos.func_177972_a(side);
/* 188 */     EnumFacing opposite = side.func_176734_d();
/* 189 */     Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
/* 190 */     Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
/* 191 */     if (!mc.field_71439_g.func_70093_af() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
/* 192 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/* 193 */       mc.field_71439_g.func_70095_a(true);
/* 194 */       sneaking = true;
/*     */     } 
/* 196 */     if (rotate) {
/* 197 */       RotationUtil.faceVector(hitVec, true);
/*     */     }
/* 199 */     rightClickBlock(neighbour, hitVec, hand, opposite, packet);
/* 200 */     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 201 */     mc.field_71467_ac = 4;
/* 202 */     return (sneaking || isSneaking);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean placeBlockNotRetarded(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean altRotate) {
/* 207 */     EnumFacing side = getFirstFacing(pos);
/* 208 */     if (side == null) return false; 
/* 209 */     BlockPos neighbour = pos.func_177972_a(side);
/* 210 */     EnumFacing opposite = side.func_176734_d();
/* 211 */     Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
/* 212 */     Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
/* 213 */     if (!mc.field_71439_g.func_70093_af() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
/* 214 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/* 215 */       mc.field_71439_g.func_70095_a(true);
/*     */     } 
/* 217 */     if (rotate) RotationUtil.faceVector(altRotate ? new Vec3d((Vec3i)pos) : hitVec, true); 
/* 218 */     rightClickBlock(neighbour, hitVec, hand, opposite, packet);
/* 219 */     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 220 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
/* 231 */     boolean sneaking = false;
/* 232 */     EnumFacing side = getFirstFacing(pos);
/* 233 */     if (side == null) {
/* 234 */       return isSneaking;
/*     */     }
/* 236 */     BlockPos neighbour = pos.func_177972_a(side);
/* 237 */     EnumFacing opposite = side.func_176734_d();
/* 238 */     Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
/* 239 */     Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
/* 240 */     if (!mc.field_71439_g.func_70093_af() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
/* 241 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/* 242 */       sneaking = true;
/*     */     } 
/* 244 */     if (rotate) {
/* 245 */       Banzem.rotationManager.lookAtVec3d(hitVec);
/*     */     }
/* 247 */     rightClickBlock(neighbour, hitVec, hand, opposite, packet);
/* 248 */     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 249 */     mc.field_71467_ac = 4;
/* 250 */     return (sneaking || isSneaking);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void placeBlockStopSneaking(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
/* 255 */     boolean sneaking = placeBlockSmartRotate(pos, hand, rotate, packet, isSneaking);
/* 256 */     if (!isSneaking && sneaking) {
/* 257 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
/* 263 */     return new Vec3d[] { new Vec3d(vec3d.field_72450_a, vec3d.field_72448_b - 1.0D, vec3d.field_72449_c), new Vec3d((vec3d.field_72450_a != 0.0D) ? (vec3d.field_72450_a * 2.0D) : vec3d.field_72450_a, vec3d.field_72448_b, (vec3d.field_72450_a != 0.0D) ? vec3d.field_72449_c : (vec3d.field_72449_c * 2.0D)), new Vec3d((vec3d.field_72450_a == 0.0D) ? (vec3d.field_72450_a + 1.0D) : vec3d.field_72450_a, vec3d.field_72448_b, (vec3d.field_72450_a == 0.0D) ? vec3d.field_72449_c : (vec3d.field_72449_c + 1.0D)), new Vec3d((vec3d.field_72450_a == 0.0D) ? (vec3d.field_72450_a - 1.0D) : vec3d.field_72450_a, vec3d.field_72448_b, (vec3d.field_72450_a == 0.0D) ? vec3d.field_72449_c : (vec3d.field_72449_c - 1.0D)), new Vec3d(vec3d.field_72450_a, vec3d.field_72448_b + 1.0D, vec3d.field_72449_c) };
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<BlockPos> possiblePlacePositions(float placeRange) {
/* 268 */     NonNullList positions = NonNullList.func_191196_a();
/* 269 */     positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.field_71439_g), placeRange, (int)placeRange, false, true, 0).stream().filter(BlockUtil::canPlaceCrystal).collect(Collectors.toList()));
/* 270 */     return (List<BlockPos>)positions;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
/* 275 */     ArrayList<BlockPos> circleblocks = new ArrayList<>();
/* 276 */     int cx = pos.func_177958_n();
/* 277 */     int cy = pos.func_177956_o();
/* 278 */     int cz = pos.func_177952_p();
/* 279 */     int x = cx - (int)r;
/* 280 */     while (x <= cx + r) {
/* 281 */       int z = cz - (int)r;
/* 282 */       while (z <= cz + r) {
/* 283 */         int y = sphere ? (cy - (int)r) : cy;
/*     */         while (true) {
/* 285 */           float f = y;
/* 286 */           float f2 = sphere ? (cy + r) : (cy + h);
/* 287 */           if (f >= f2)
/* 288 */             break;  double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
/* 289 */           if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
/* 290 */             BlockPos l = new BlockPos(x, y + plus_y, z);
/* 291 */             circleblocks.add(l);
/*     */           } 
/* 293 */           y++;
/*     */         } 
/* 295 */         z++;
/*     */       } 
/* 297 */       x++;
/*     */     } 
/* 299 */     return circleblocks;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<BlockPos> getDisc(BlockPos pos, float r) {
/* 304 */     ArrayList<BlockPos> circleblocks = new ArrayList<>();
/* 305 */     int cx = pos.func_177958_n();
/* 306 */     int cy = pos.func_177956_o();
/* 307 */     int cz = pos.func_177952_p();
/* 308 */     int x = cx - (int)r;
/* 309 */     while (x <= cx + r) {
/* 310 */       int z = cz - (int)r;
/* 311 */       while (z <= cz + r) {
/* 312 */         double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z));
/* 313 */         if (dist < (r * r)) {
/* 314 */           BlockPos position = new BlockPos(x, cy, z);
/* 315 */           circleblocks.add(position);
/*     */         } 
/* 317 */         z++;
/*     */       } 
/* 319 */       x++;
/*     */     } 
/* 321 */     return circleblocks;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean canPlaceCrystal(BlockPos blockPos) {
/* 326 */     BlockPos boost = blockPos.func_177982_a(0, 1, 0);
/* 327 */     BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);
/*     */     try {
/* 329 */       return ((mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150357_h || mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150343_Z) && mc.field_71441_e.func_180495_p(boost).func_177230_c() == Blocks.field_150350_a && mc.field_71441_e.func_180495_p(boost2).func_177230_c() == Blocks.field_150350_a && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).isEmpty());
/* 330 */     } catch (Exception e) {
/* 331 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck, boolean oneDot15) {
/* 337 */     NonNullList positions = NonNullList.func_191196_a();
/* 338 */     positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.field_71439_g), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, specialEntityCheck, oneDot15)).collect(Collectors.toList()));
/* 339 */     return (List<BlockPos>)positions;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
/* 344 */     BlockPos boost = blockPos.func_177982_a(0, 1, 0);
/* 345 */     BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);
/*     */     try {
/* 347 */       if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
/* 348 */         return false;
/*     */       }
/* 350 */       if ((!oneDot15 && mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150350_a) || mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a) {
/* 351 */         return false;
/*     */       }
/* 353 */       for (Entity entity : mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost))) {
/* 354 */         if (entity.field_70128_L || (specialEntityCheck && entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
/* 355 */           continue;  return false;
/*     */       } 
/* 357 */       if (!oneDot15) {
/* 358 */         for (Entity entity : mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2))) {
/* 359 */           if (entity.field_70128_L || (specialEntityCheck && entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
/* 360 */             continue;  return false;
/*     */         } 
/*     */       }
/* 363 */     } catch (Exception ignored) {
/* 364 */       return false;
/*     */     } 
/* 366 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean canBeClicked(BlockPos pos) {
/* 371 */     return getBlock(pos).func_176209_a(getState(pos), false);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Block getBlock(BlockPos pos) {
/* 376 */     return getState(pos).func_177230_c();
/*     */   }
/*     */ 
/*     */   
/*     */   private static IBlockState getState(BlockPos pos) {
/* 381 */     return mc.field_71441_e.func_180495_p(pos);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isBlockAboveEntitySolid(Entity entity) {
/* 386 */     if (entity != null) {
/* 387 */       BlockPos pos = new BlockPos(entity.field_70165_t, entity.field_70163_u + 2.0D, entity.field_70161_v);
/* 388 */       return isBlockSolid(pos);
/*     */     } 
/* 390 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void debugPos(String message, BlockPos pos) {
/* 395 */     Command.sendMessage(message + pos.func_177958_n() + "x, " + pos.func_177956_o() + "y, " + pos.func_177952_p() + "z");
/*     */   }
/*     */ 
/*     */   
/*     */   public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand, boolean silent) {
/* 400 */     RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(pos.func_177958_n() + 0.5D, pos.func_177956_o() - 0.5D, pos.func_177952_p() + 0.5D));
/* 401 */     EnumFacing facing = (result == null || result.field_178784_b == null) ? EnumFacing.UP : result.field_178784_b;
/* 402 */     int old = mc.field_71439_g.field_71071_by.field_70461_c;
/* 403 */     int crystal = InventoryUtil.getItemHotbar(Items.field_185158_cP);
/* 404 */     if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.field_71439_g.field_71071_by.field_70461_c)
/* 405 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(crystal)); 
/* 406 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0F, 0.0F, 0.0F));
/* 407 */     if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.field_71439_g.field_71071_by.field_70461_c)
/* 408 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(old)); 
/* 409 */     if (swing)
/* 410 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND)); 
/*     */   }
/*     */   
/*     */   public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand) {
/* 414 */     RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(pos.func_177958_n() + 0.5D, pos.func_177956_o() - 0.5D, pos.func_177952_p() + 0.5D));
/* 415 */     EnumFacing facing = (result == null || result.field_178784_b == null) ? EnumFacing.UP : result.field_178784_b;
/* 416 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0F, 0.0F, 0.0F));
/* 417 */     if (swing) {
/* 418 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
/* 424 */     BlockPos[] list = new BlockPos[vec3ds.length];
/* 425 */     for (int i = 0; i < vec3ds.length; i++) {
/* 426 */       list[i] = new BlockPos(vec3ds[i]);
/*     */     }
/* 428 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d posToVec3d(BlockPos pos) {
/* 433 */     return new Vec3d((Vec3i)pos);
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos vec3dToPos(Vec3d vec3d) {
/* 438 */     return new BlockPos(vec3d);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Boolean isPosInFov(BlockPos pos) {
/* 443 */     int dirnumber = RotationUtil.getDirection4D();
/* 444 */     if (dirnumber == 0 && pos.func_177952_p() - (mc.field_71439_g.func_174791_d()).field_72449_c < 0.0D) {
/* 445 */       return Boolean.valueOf(false);
/*     */     }
/* 447 */     if (dirnumber == 1 && pos.func_177958_n() - (mc.field_71439_g.func_174791_d()).field_72450_a > 0.0D) {
/* 448 */       return Boolean.valueOf(false);
/*     */     }
/* 450 */     if (dirnumber == 2 && pos.func_177952_p() - (mc.field_71439_g.func_174791_d()).field_72449_c > 0.0D) {
/* 451 */       return Boolean.valueOf(false);
/*     */     }
/* 453 */     return Boolean.valueOf((dirnumber != 3 || pos.func_177958_n() - (mc.field_71439_g.func_174791_d()).field_72450_a >= 0.0D));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isBlockBelowEntitySolid(Entity entity) {
/* 458 */     if (entity != null) {
/* 459 */       BlockPos pos = new BlockPos(entity.field_70165_t, entity.field_70163_u - 1.0D, entity.field_70161_v);
/* 460 */       return isBlockSolid(pos);
/*     */     } 
/* 462 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isBlockSolid(BlockPos pos) {
/* 467 */     return !isBlockUnSolid(pos);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isBlockUnSolid(BlockPos pos) {
/* 472 */     return isBlockUnSolid(mc.field_71441_e.func_180495_p(pos).func_177230_c());
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isBlockUnSolid(Block block) {
/* 477 */     return unSolidBlocks.contains(block);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
/* 482 */     Vec3d[] output = new Vec3d[input.length];
/* 483 */     for (int i = 0; i < input.length; i++) {
/* 484 */       output[i] = vec3d.func_178787_e(input[i]);
/*     */     }
/* 486 */     return output;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
/* 491 */     return convertVec3ds(entity.func_174791_d(), input);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean canBreak(BlockPos pos) {
/* 496 */     IBlockState blockState = mc.field_71441_e.func_180495_p(pos);
/* 497 */     Block block = blockState.func_177230_c();
/* 498 */     return (block.func_176195_g(blockState, (World)mc.field_71441_e, pos) != -1.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isValidBlock(BlockPos pos) {
/* 503 */     Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
/* 504 */     return (!(block instanceof net.minecraft.block.BlockLiquid) && block.func_149688_o(null) != Material.field_151579_a);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isScaffoldPos(BlockPos pos) {
/* 509 */     return (mc.field_71441_e.func_175623_d(pos) || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150431_aC || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150329_H || mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof net.minecraft.block.BlockLiquid);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
/* 514 */     return (!shouldCheck || mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(pos.func_177958_n(), (pos.func_177956_o() + height), pos.func_177952_p()), false, true, false) == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
/* 519 */     return rayTracePlaceCheck(pos, shouldCheck, 1.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean rayTracePlaceCheck(BlockPos pos) {
/* 524 */     return rayTracePlaceCheck(pos, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isInHole() {
/* 529 */     BlockPos blockPos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
/* 530 */     IBlockState blockState = mc.field_71441_e.func_180495_p(blockPos);
/* 531 */     return isBlockValid(blockState, blockPos);
/*     */   }
/*     */ 
/*     */   
/*     */   public static double getNearestBlockBelow() {
/* 536 */     for (double y = mc.field_71439_g.field_70163_u; y > 0.0D; ) {
/* 537 */       if (mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, y, mc.field_71439_g.field_70161_v)).func_177230_c() instanceof net.minecraft.block.BlockSlab || mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, y, mc.field_71439_g.field_70161_v)).func_177230_c().func_176223_P().func_185890_d((IBlockAccess)mc.field_71441_e, new BlockPos(0, 0, 0)) == null) {
/*     */         y -= 0.001D; continue;
/* 539 */       }  return y;
/*     */     } 
/* 541 */     return -1.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
/* 546 */     if (blockState.func_177230_c() != Blocks.field_150350_a) {
/* 547 */       return false;
/*     */     }
/* 549 */     if (mc.field_71439_g.func_174818_b(blockPos) < 1.0D) {
/* 550 */       return false;
/*     */     }
/* 552 */     if (mc.field_71441_e.func_180495_p(blockPos.func_177984_a()).func_177230_c() != Blocks.field_150350_a) {
/* 553 */       return false;
/*     */     }
/* 555 */     if (mc.field_71441_e.func_180495_p(blockPos.func_177981_b(2)).func_177230_c() != Blocks.field_150350_a) {
/* 556 */       return false;
/*     */     }
/* 558 */     return (isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos) || isElseHole(blockPos));
/*     */   } public static boolean isObbyHole(BlockPos blockPos) {
/*     */     BlockPos[] arrayOfBlockPos;
/*     */     int i;
/*     */     byte b;
/* 563 */     for (arrayOfBlockPos = getTouchingBlocks(blockPos), i = arrayOfBlockPos.length, b = 0; b < i; ) { BlockPos pos = arrayOfBlockPos[b];
/* 564 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 565 */       if (touchingState.func_177230_c() != Blocks.field_150350_a && touchingState.func_177230_c() == Blocks.field_150343_Z) { b++; continue; }
/* 566 */        return false; }
/*     */     
/* 568 */     return true;
/*     */   } public static boolean isBedrockHole(BlockPos blockPos) {
/*     */     BlockPos[] arrayOfBlockPos;
/*     */     int i;
/*     */     byte b;
/* 573 */     for (arrayOfBlockPos = getTouchingBlocks(blockPos), i = arrayOfBlockPos.length, b = 0; b < i; ) { BlockPos pos = arrayOfBlockPos[b];
/* 574 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 575 */       if (touchingState.func_177230_c() != Blocks.field_150350_a && touchingState.func_177230_c() == Blocks.field_150357_h) { b++; continue; }
/* 576 */        return false; }
/*     */     
/* 578 */     return true;
/*     */   } public static boolean isBothHole(BlockPos blockPos) {
/*     */     BlockPos[] arrayOfBlockPos;
/*     */     int i;
/*     */     byte b;
/* 583 */     for (arrayOfBlockPos = getTouchingBlocks(blockPos), i = arrayOfBlockPos.length, b = 0; b < i; ) { BlockPos pos = arrayOfBlockPos[b];
/* 584 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 585 */       if (touchingState.func_177230_c() != Blocks.field_150350_a && (touchingState.func_177230_c() == Blocks.field_150357_h || touchingState.func_177230_c() == Blocks.field_150343_Z)) {
/*     */         b++; continue;
/* 587 */       }  return false; }
/*     */     
/* 589 */     return true;
/*     */   } public static boolean isElseHole(BlockPos blockPos) {
/*     */     BlockPos[] arrayOfBlockPos;
/*     */     int i;
/*     */     byte b;
/* 594 */     for (arrayOfBlockPos = getTouchingBlocks(blockPos), i = arrayOfBlockPos.length, b = 0; b < i; ) { BlockPos pos = arrayOfBlockPos[b];
/* 595 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 596 */       if (touchingState.func_177230_c() != Blocks.field_150350_a && touchingState.func_185913_b()) { b++; continue; }
/* 597 */        return false; }
/*     */     
/* 599 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos[] getTouchingBlocks(BlockPos blockPos) {
/* 604 */     return new BlockPos[] { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() };
/*     */   }
/*     */ 
/*     */   
/*     */   public static void SetCurrentBlock(BlockPos block) {
/* 609 */     _currBlock = block;
/* 610 */     _started = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos GetCurrBlock() {
/* 615 */     return _currBlock;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean GetState() {
/* 620 */     return (_currBlock != null && IsDoneBreaking(mc.field_71441_e.func_180495_p(_currBlock)));
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean IsDoneBreaking(IBlockState blockState) {
/* 625 */     return (blockState.func_177230_c() == Blocks.field_150357_h || blockState.func_177230_c() == Blocks.field_150350_a || blockState.func_177230_c() instanceof net.minecraft.block.BlockLiquid);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean Update(float range, boolean rayTrace) {
/* 630 */     if (_currBlock == null) {
/* 631 */       return false;
/*     */     }
/* 633 */     IBlockState state = mc.field_71441_e.func_180495_p(_currBlock);
/* 634 */     if (!IsDoneBreaking(state) && mc.field_71439_g.func_174818_b(_currBlock) <= Math.pow(range, range)) {
/* 635 */       mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 636 */       EnumFacing facing = EnumFacing.UP;
/* 637 */       if (rayTrace) {
/* 638 */         RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(_currBlock.func_177958_n() + 0.5D, _currBlock.func_177956_o() - 0.5D, _currBlock.func_177952_p() + 0.5D));
/* 639 */         if (result != null && result.field_178784_b != null) {
/* 640 */           facing = result.field_178784_b;
/*     */         }
/*     */       } 
/*     */       
/* 644 */       if (!_started) {
/* 645 */         _started = true;
/* 646 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, _currBlock, facing));
/* 647 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, _currBlock, facing));
/*     */       } else {
/* 649 */         mc.field_71442_b.func_180512_c(_currBlock, facing);
/*     */       } 
/*     */       
/* 652 */       return true;
/*     */     } 
/* 654 */     _currBlock = null;
/* 655 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void placeBlock(BlockPos pos, EnumFacing side, boolean packet) {
/* 662 */     BlockPos neighbour = pos.func_177972_a(side);
/* 663 */     EnumFacing opposite = side.func_176734_d();
/* 664 */     if (!Util.mc.field_71439_g.func_70093_af()) {
/* 665 */       Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Util.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/*     */     }
/* 667 */     Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
/* 668 */     if (packet) {
/* 669 */       Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, side, EnumHand.MAIN_HAND, (float)hitVec.field_72450_a - pos.func_177958_n(), (float)hitVec.field_72448_b - pos.func_177956_o(), (float)hitVec.field_72449_c - pos.func_177952_p()));
/*     */     } else {
/* 671 */       Util.mc.field_71442_b.func_187099_a(Util.mc.field_71439_g, Util.mc.field_71441_e, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
/* 672 */     }  Util.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\BlockUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */