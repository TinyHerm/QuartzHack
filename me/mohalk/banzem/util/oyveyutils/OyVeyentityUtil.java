/*     */ package me.mohalk.banzem.util.oyveyutils;
/*     */ 
/*     */ import com.mojang.realmsclient.gui.ChatFormatting;
/*     */ import java.awt.Color;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.EnumCreatureType;
/*     */ import net.minecraft.entity.monster.EntityEnderman;
/*     */ import net.minecraft.entity.monster.EntityIronGolem;
/*     */ import net.minecraft.entity.monster.EntityPigZombie;
/*     */ import net.minecraft.entity.passive.EntityWolf;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.Enchantments;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketUseEntity;
/*     */ import net.minecraft.potion.Potion;
/*     */ import net.minecraft.potion.PotionEffect;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.MovementInput;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OyVeyentityUtil
/*     */   implements Util
/*     */ {
/*  50 */   public static final Vec3d[] antiDropOffsetList = new Vec3d[] { new Vec3d(0.0D, -2.0D, 0.0D) };
/*  51 */   public static final Vec3d[] platformOffsetList = new Vec3d[] { new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D) };
/*  52 */   public static final Vec3d[] legOffsetList = new Vec3d[] { new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D) };
/*  53 */   public static final Vec3d[] OffsetList = new Vec3d[] { new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D) };
/*  54 */   public static final Vec3d[] antiStepOffsetList = new Vec3d[] { new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D) };
/*  55 */   public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[] { new Vec3d(0.0D, 3.0D, 0.0D) };
/*     */   
/*     */   public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
/*  58 */     if (packet) {
/*  59 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(entity));
/*     */     } else {
/*  61 */       mc.field_71442_b.func_78764_a((EntityPlayer)mc.field_71439_g, entity);
/*     */     } 
/*  63 */     if (swingArm) {
/*  64 */       mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Vec3d interpolateEntity(Entity entity, float time) {
/*  69 */     return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * time);
/*     */   }
/*     */   
/*     */   public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
/*  73 */     return (new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).func_178787_e(getInterpolatedAmount(entity, partialTicks));
/*     */   }
/*     */   
/*     */   public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
/*  77 */     return getInterpolatedPos(entity, partialTicks).func_178786_a((mc.func_175598_ae()).field_78725_b, (mc.func_175598_ae()).field_78726_c, (mc.func_175598_ae()).field_78723_d);
/*     */   }
/*     */   
/*     */   public static Vec3d getInterpolatedRenderPos(Vec3d vec) {
/*  81 */     return (new Vec3d(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c)).func_178786_a((mc.func_175598_ae()).field_78725_b, (mc.func_175598_ae()).field_78726_c, (mc.func_175598_ae()).field_78723_d);
/*     */   }
/*     */   
/*     */   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
/*  85 */     return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
/*     */   }
/*     */   
/*     */   public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
/*  89 */     return getInterpolatedAmount(entity, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
/*     */   }
/*     */   
/*     */   public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
/*  93 */     return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
/*     */   }
/*     */   
/*     */   public static boolean isPassive(Entity entity) {
/*  97 */     if (entity instanceof EntityWolf && ((EntityWolf)entity).func_70919_bu()) {
/*  98 */       return false;
/*     */     }
/* 100 */     if (entity instanceof net.minecraft.entity.EntityAgeable || entity instanceof net.minecraft.entity.passive.EntityAmbientCreature || entity instanceof net.minecraft.entity.passive.EntitySquid) {
/* 101 */       return true;
/*     */     }
/* 103 */     return (entity instanceof EntityIronGolem && ((EntityIronGolem)entity).func_70643_av() == null);
/*     */   }
/*     */   
/*     */   public static boolean isSafe(Entity entity, int height, boolean floor) {
/* 107 */     return (getUnsafeBlocks(entity, height, floor).size() == 0);
/*     */   }
/*     */   
/*     */   public static boolean stopSneaking(boolean isSneaking) {
/* 111 */     if (isSneaking && mc.field_71439_g != null) {
/* 112 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
/*     */     }
/* 114 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isSafe(Entity entity) {
/* 118 */     return isSafe(entity, 0, false);
/*     */   }
/*     */   
/*     */   public static BlockPos getPlayerPos(EntityPlayer player) {
/* 122 */     return new BlockPos(Math.floor(player.field_70165_t), Math.floor(player.field_70163_u), Math.floor(player.field_70161_v));
/*     */   }
/*     */   
/*     */   public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
/* 126 */     return getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
/*     */   }
/*     */   
/*     */   public static boolean isMobAggressive(Entity entity) {
/* 130 */     if (entity instanceof EntityPigZombie) {
/* 131 */       if (((EntityPigZombie)entity).func_184734_db() || ((EntityPigZombie)entity).func_175457_ck()) {
/* 132 */         return true;
/*     */       }
/*     */     } else {
/* 135 */       if (entity instanceof EntityWolf) {
/* 136 */         return (((EntityWolf)entity).func_70919_bu() && !mc.field_71439_g.equals(((EntityWolf)entity).func_70902_q()));
/*     */       }
/* 138 */       if (entity instanceof EntityEnderman) {
/* 139 */         return ((EntityEnderman)entity).func_70823_r();
/*     */       }
/*     */     } 
/* 142 */     return isHostileMob(entity);
/*     */   }
/*     */   
/*     */   public static boolean isNeutralMob(Entity entity) {
/* 146 */     return (entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman);
/*     */   }
/*     */   
/*     */   public static boolean isProjectile(Entity entity) {
/* 150 */     return (entity instanceof net.minecraft.entity.projectile.EntityShulkerBullet || entity instanceof net.minecraft.entity.projectile.EntityFireball);
/*     */   }
/*     */   
/*     */   public static boolean isVehicle(Entity entity) {
/* 154 */     return (entity instanceof net.minecraft.entity.item.EntityBoat || entity instanceof net.minecraft.entity.item.EntityMinecart);
/*     */   }
/*     */   
/*     */   public static boolean isFriendlyMob(Entity entity) {
/* 158 */     return ((entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof net.minecraft.entity.passive.EntityVillager || entity instanceof EntityIronGolem || (isNeutralMob(entity) && !isMobAggressive(entity)));
/*     */   }
/*     */   
/*     */   public static boolean isHostileMob(Entity entity) {
/* 162 */     return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity));
/*     */   }
/*     */   
/*     */   public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
/* 166 */     ArrayList<Vec3d> vec3ds = new ArrayList<>();
/* 167 */     for (Vec3d vector : getOffsets(height, floor)) {
/* 168 */       BlockPos targetPos = (new BlockPos(pos)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
/* 169 */       Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
/* 170 */       if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow)
/*     */       {
/* 172 */         vec3ds.add(vector); } 
/*     */     } 
/* 174 */     return vec3ds;
/*     */   }
/*     */   
/*     */   public static boolean isInHole(Entity entity) {
/* 178 */     return isBlockValid(new BlockPos(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
/*     */   }
/*     */   
/*     */   public static boolean isBlockValid(BlockPos blockPos) {
/* 182 */     return (isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos));
/*     */   } public static boolean isObbyHole(BlockPos blockPos) { BlockPos[] touchingBlocks;
/*     */     BlockPos[] arrayOfBlockPos1;
/*     */     int i;
/*     */     byte b;
/* 187 */     for (arrayOfBlockPos1 = touchingBlocks = new BlockPos[] { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() }, i = arrayOfBlockPos1.length, b = 0; b < i; ) { BlockPos pos = arrayOfBlockPos1[b];
/* 188 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 189 */       if (touchingState.func_177230_c() != Blocks.field_150350_a && touchingState.func_177230_c() == Blocks.field_150343_Z) { b++; continue; }
/* 190 */        return false; }
/*     */     
/* 192 */     return true; }
/*     */   public static boolean isBedrockHole(BlockPos blockPos) { BlockPos[] touchingBlocks;
/*     */     BlockPos[] arrayOfBlockPos1;
/*     */     int i;
/*     */     byte b;
/* 197 */     for (arrayOfBlockPos1 = touchingBlocks = new BlockPos[] { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() }, i = arrayOfBlockPos1.length, b = 0; b < i; ) { BlockPos pos = arrayOfBlockPos1[b];
/* 198 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 199 */       if (touchingState.func_177230_c() != Blocks.field_150350_a && touchingState.func_177230_c() == Blocks.field_150357_h) { b++; continue; }
/* 200 */        return false; }
/*     */     
/* 202 */     return true; } public static boolean isBothHole(BlockPos blockPos) {
/*     */     BlockPos[] touchingBlocks;
/*     */     BlockPos[] arrayOfBlockPos1;
/*     */     int i;
/*     */     byte b;
/* 207 */     for (arrayOfBlockPos1 = touchingBlocks = new BlockPos[] { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() }, i = arrayOfBlockPos1.length, b = 0; b < i; ) { BlockPos pos = arrayOfBlockPos1[b];
/* 208 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 209 */       if (touchingState.func_177230_c() != Blocks.field_150350_a && (touchingState.func_177230_c() == Blocks.field_150357_h || touchingState.func_177230_c() == Blocks.field_150343_Z)) {
/*     */         b++; continue;
/* 211 */       }  return false; }
/*     */     
/* 213 */     return true;
/*     */   }
/*     */   
/*     */   public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
/* 217 */     List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
/* 218 */     Vec3d[] array = new Vec3d[list.size()];
/* 219 */     return list.<Vec3d>toArray(array);
/*     */   }
/*     */   
/*     */   public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
/* 223 */     List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
/* 224 */     Vec3d[] array = new Vec3d[list.size()];
/* 225 */     return list.<Vec3d>toArray(array);
/*     */   }
/*     */   
/*     */   public static double getDst(Vec3d vec) {
/* 229 */     return mc.field_71439_g.func_174791_d().func_72438_d(vec);
/*     */   }
/*     */   
/*     */   public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
/* 233 */     return (getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0);
/*     */   }
/*     */   
/*     */   public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
/* 237 */     return (getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0);
/*     */   }
/*     */   
/*     */   public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
/* 241 */     ArrayList<Vec3d> vec3ds = new ArrayList<>();
/* 242 */     if (!antiStep && getUnsafeBlocks((Entity)player, 2, false).size() == 4) {
/* 243 */       vec3ds.addAll(getUnsafeBlocks((Entity)player, 2, false));
/*     */     }
/* 245 */     for (int i = 0; i < (getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)).length; i++) {
/* 246 */       Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
/* 247 */       BlockPos targetPos = (new BlockPos(player.func_174791_d())).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
/* 248 */       Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
/* 249 */       if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow)
/*     */       {
/* 251 */         vec3ds.add(vector); } 
/*     */     } 
/* 253 */     return vec3ds;
/*     */   }
/*     */   
/*     */   public static boolean isInWater(Entity entity) {
/* 257 */     if (entity == null) {
/* 258 */       return false;
/*     */     }
/* 260 */     double y = entity.field_70163_u + 0.01D;
/* 261 */     for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); x++) {
/* 262 */       for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ) {
/* 263 */         BlockPos pos = new BlockPos(x, (int)y, z);
/* 264 */         if (!(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof net.minecraft.block.BlockLiquid)) { z++; continue; }
/* 265 */          return true;
/*     */       } 
/*     */     } 
/* 268 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isDrivenByPlayer(Entity entityIn) {
/* 272 */     return (mc.field_71439_g != null && entityIn != null && entityIn.equals(mc.field_71439_g.func_184187_bx()));
/*     */   }
/*     */   
/*     */   public static boolean isPlayer(Entity entity) {
/* 276 */     return entity instanceof EntityPlayer;
/*     */   }
/*     */   
/*     */   public static boolean isAboveWater(Entity entity) {
/* 280 */     return isAboveWater(entity, false);
/*     */   }
/*     */   
/*     */   public static boolean isAboveWater(Entity entity, boolean packet) {
/* 284 */     if (entity == null) {
/* 285 */       return false;
/*     */     }
/* 287 */     double y = entity.field_70163_u - (packet ? 0.03D : (isPlayer(entity) ? 0.2D : 0.5D));
/* 288 */     for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); x++) {
/* 289 */       for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ) {
/* 290 */         BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
/* 291 */         if (!(mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof net.minecraft.block.BlockLiquid)) { z++; continue; }
/* 292 */          return true;
/*     */       } 
/*     */     } 
/* 295 */     return false;
/*     */   }
/*     */   
/*     */   public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
/* 299 */     ArrayList<Vec3d> placeTargets = new ArrayList<>();
/* 300 */     if (extension == 1) {
/* 301 */       placeTargets.addAll(targets(player.func_174791_d(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
/*     */     } else {
/* 303 */       int extend = 1;
/* 304 */       for (Vec3d vec3d : MathUtil.getBlockBlocks((Entity)player)) {
/* 305 */         if (extend > extension)
/* 306 */           break;  placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
/* 307 */         extend++;
/*     */       } 
/*     */     } 
/* 310 */     ArrayList<Vec3d> removeList = new ArrayList<>();
/* 311 */     for (Vec3d vec3d : placeTargets) {
/* 312 */       BlockPos pos = new BlockPos(vec3d);
/* 313 */       if (BlockUtil.isPositionPlaceable(pos, raytrace) != -1)
/* 314 */         continue;  removeList.add(vec3d);
/*     */     } 
/* 316 */     for (Vec3d vec3d : removeList) {
/* 317 */       placeTargets.remove(vec3d);
/*     */     }
/* 319 */     return placeTargets;
/*     */   }
/*     */   
/*     */   public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
/* 323 */     ArrayList<Vec3d> placeTargets = new ArrayList<>();
/* 324 */     if (antiDrop) {
/* 325 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
/*     */     }
/* 327 */     if (platform) {
/* 328 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
/*     */     }
/* 330 */     if (legs) {
/* 331 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
/*     */     }
/* 333 */     Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
/* 334 */     if (antiStep) {
/* 335 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
/*     */     } else {
/* 337 */       List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
/* 338 */       if (vec3ds.size() == 4)
/*     */       {
/* 340 */         for (Vec3d vector : vec3ds) {
/* 341 */           BlockPos position = (new BlockPos(vec3d)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
/* 342 */           switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
/*     */             case 0:
/*     */               break;
/*     */             
/*     */             case -1:
/*     */             case 1:
/*     */             case 2:
/*     */               continue;
/*     */             
/*     */             case 3:
/* 352 */               placeTargets.add(vec3d.func_178787_e(vector)); break;
/*     */             default:
/*     */               // Byte code: goto -> 234
/*     */           } 
/* 356 */           if (antiScaffold) {
/* 357 */             Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
/*     */           }
/* 359 */           return placeTargets;
/*     */         } 
/*     */       }
/*     */     } 
/* 363 */     if (antiScaffold) {
/* 364 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
/*     */     }
/* 366 */     return placeTargets;
/*     */   }
/*     */   
/*     */   public static List<Vec3d> getOffsetList(int y, boolean floor) {
/* 370 */     ArrayList<Vec3d> offsets = new ArrayList<>();
/* 371 */     offsets.add(new Vec3d(-1.0D, y, 0.0D));
/* 372 */     offsets.add(new Vec3d(1.0D, y, 0.0D));
/* 373 */     offsets.add(new Vec3d(0.0D, y, -1.0D));
/* 374 */     offsets.add(new Vec3d(0.0D, y, 1.0D));
/* 375 */     if (floor) {
/* 376 */       offsets.add(new Vec3d(0.0D, (y - 1), 0.0D));
/*     */     }
/* 378 */     return offsets;
/*     */   }
/*     */   
/*     */   public static Vec3d[] getOffsets(int y, boolean floor) {
/* 382 */     List<Vec3d> offsets = getOffsetList(y, floor);
/* 383 */     Vec3d[] array = new Vec3d[offsets.size()];
/* 384 */     return offsets.<Vec3d>toArray(array);
/*     */   }
/*     */   
/*     */   public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
/* 388 */     List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
/* 389 */     Vec3d[] array = new Vec3d[offsets.size()];
/* 390 */     return offsets.<Vec3d>toArray(array);
/*     */   }
/*     */   
/*     */   public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
/* 394 */     ArrayList<Vec3d> offsets = new ArrayList<>(getOffsetList(1, false));
/* 395 */     offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
/* 396 */     if (antiScaffold) {
/* 397 */       offsets.add(new Vec3d(0.0D, 3.0D, 0.0D));
/*     */     }
/* 399 */     if (antiStep) {
/* 400 */       offsets.addAll(getOffsetList(2, false));
/*     */     }
/* 402 */     if (legs) {
/* 403 */       offsets.addAll(getOffsetList(0, false));
/*     */     }
/* 405 */     if (platform) {
/* 406 */       offsets.addAll(getOffsetList(-1, false));
/* 407 */       offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
/*     */     } 
/* 409 */     if (antiDrop) {
/* 410 */       offsets.add(new Vec3d(0.0D, -2.0D, 0.0D));
/*     */     }
/* 412 */     return offsets;
/*     */   }
/*     */   
/*     */   public static Vec3d[] getHeightOffsets(int min, int max) {
/* 416 */     ArrayList<Vec3d> offsets = new ArrayList<>();
/* 417 */     for (int i = min; i <= max; i++) {
/* 418 */       offsets.add(new Vec3d(0.0D, i, 0.0D));
/*     */     }
/* 420 */     Vec3d[] array = new Vec3d[offsets.size()];
/* 421 */     return offsets.<Vec3d>toArray(array);
/*     */   }
/*     */   
/*     */   public static BlockPos getRoundedBlockPos(Entity entity) {
/* 425 */     return new BlockPos(MathUtil.roundVec(entity.func_174791_d(), 0));
/*     */   }
/*     */   
/*     */   public static boolean isLiving(Entity entity) {
/* 429 */     return entity instanceof EntityLivingBase;
/*     */   }
/*     */   
/*     */   public static boolean isAlive(Entity entity) {
/* 433 */     return (isLiving(entity) && !entity.field_70128_L && ((EntityLivingBase)entity).func_110143_aJ() > 0.0F);
/*     */   }
/*     */   
/*     */   public static boolean isDead(Entity entity) {
/* 437 */     return !isAlive(entity);
/*     */   }
/*     */   
/*     */   public static float getHealth(Entity entity) {
/* 441 */     if (isLiving(entity)) {
/* 442 */       EntityLivingBase livingBase = (EntityLivingBase)entity;
/* 443 */       return livingBase.func_110143_aJ() + livingBase.func_110139_bj();
/*     */     } 
/* 445 */     return 0.0F;
/*     */   }
/*     */   
/*     */   public static float getHealth(Entity entity, boolean absorption) {
/* 449 */     if (isLiving(entity)) {
/* 450 */       EntityLivingBase livingBase = (EntityLivingBase)entity;
/* 451 */       return livingBase.func_110143_aJ() + (absorption ? livingBase.func_110139_bj() : 0.0F);
/*     */     } 
/* 453 */     return 0.0F;
/*     */   }
/*     */   
/*     */   public static boolean canEntityFeetBeSeen(Entity entityIn) {
/* 457 */     return (mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70165_t + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(entityIn.field_70165_t, entityIn.field_70163_u, entityIn.field_70161_v), false, true, false) == null);
/*     */   }
/*     */   
/*     */   public static boolean isntValid(Entity entity, double range) {
/* 461 */     return (entity == null || isDead(entity) || entity.equals(mc.field_71439_g) || (entity instanceof EntityPlayer && Banzem.friendManager.isFriend(entity.func_70005_c_())) || mc.field_71439_g.func_70068_e(entity) > MathUtil.square(range));
/*     */   }
/*     */   
/*     */   public static boolean isValid(Entity entity, double range) {
/* 465 */     return !isntValid(entity, range);
/*     */   }
/*     */   
/*     */   public static boolean holdingWeapon(EntityPlayer player) {
/* 469 */     return (player.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemSword || player.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemAxe);
/*     */   }
/*     */   
/*     */   public static double getMaxSpeed() {
/* 473 */     double maxModifier = 0.2873D;
/* 474 */     if (mc.field_71439_g.func_70644_a(Objects.<Potion>requireNonNull(Potion.func_188412_a(1)))) {
/* 475 */       maxModifier *= 1.0D + 0.2D * (((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.field_71439_g.func_70660_b(Objects.<Potion>requireNonNull(Potion.func_188412_a(1))))).func_76458_c() + 1);
/*     */     }
/* 477 */     return maxModifier;
/*     */   }
/*     */   
/*     */   public static void mutliplyEntitySpeed(Entity entity, double multiplier) {
/* 481 */     if (entity != null) {
/* 482 */       entity.field_70159_w *= multiplier;
/* 483 */       entity.field_70179_y *= multiplier;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean isEntityMoving(Entity entity) {
/* 488 */     if (entity == null) {
/* 489 */       return false;
/*     */     }
/* 491 */     if (entity instanceof EntityPlayer) {
/* 492 */       return (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d());
/*     */     }
/* 494 */     return (entity.field_70159_w != 0.0D || entity.field_70181_x != 0.0D || entity.field_70179_y != 0.0D);
/*     */   }
/*     */   
/*     */   public static double getEntitySpeed(Entity entity) {
/* 498 */     if (entity != null) {
/* 499 */       double distTraveledLastTickX = entity.field_70165_t - entity.field_70169_q;
/* 500 */       double distTraveledLastTickZ = entity.field_70161_v - entity.field_70166_s;
/* 501 */       double speed = MathHelper.func_76133_a(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ);
/* 502 */       return speed * 20.0D;
/*     */     } 
/* 504 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public static boolean is32k(ItemStack stack) {
/* 508 */     return (EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, stack) >= 1000);
/*     */   }
/*     */   
/*     */   public static void moveEntityStrafe(double speed, Entity entity) {
/* 512 */     if (entity != null) {
/* 513 */       MovementInput movementInput = mc.field_71439_g.field_71158_b;
/* 514 */       double forward = movementInput.field_192832_b;
/* 515 */       double strafe = movementInput.field_78902_a;
/* 516 */       float yaw = mc.field_71439_g.field_70177_z;
/* 517 */       if (forward == 0.0D && strafe == 0.0D) {
/* 518 */         entity.field_70159_w = 0.0D;
/* 519 */         entity.field_70179_y = 0.0D;
/*     */       } else {
/* 521 */         if (forward != 0.0D) {
/* 522 */           if (strafe > 0.0D) {
/* 523 */             yaw += ((forward > 0.0D) ? -45 : 45);
/* 524 */           } else if (strafe < 0.0D) {
/* 525 */             yaw += ((forward > 0.0D) ? 45 : -45);
/*     */           } 
/* 527 */           strafe = 0.0D;
/* 528 */           if (forward > 0.0D) {
/* 529 */             forward = 1.0D;
/* 530 */           } else if (forward < 0.0D) {
/* 531 */             forward = -1.0D;
/*     */           } 
/*     */         } 
/* 534 */         entity.field_70159_w = forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F)));
/* 535 */         entity.field_70179_y = forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F)));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean rayTraceHitCheck(Entity entity, boolean shouldCheck) {
/* 541 */     return (!shouldCheck || mc.field_71439_g.func_70685_l(entity));
/*     */   }
/*     */   
/*     */   public static Color getColor(Entity entity, int red, int green, int blue, int alpha, boolean colorFriends) {
/* 545 */     Color color = new Color(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
/* 546 */     if (entity instanceof EntityPlayer && colorFriends && Banzem.friendManager.isFriend((EntityPlayer)entity)) {
/* 547 */       color = new Color(0.33333334F, 1.0F, 1.0F, alpha / 255.0F);
/*     */     }
/* 549 */     return color;
/*     */   }
/*     */   
/*     */   public static boolean isMoving() {
/* 553 */     return (mc.field_71439_g.field_191988_bg != 0.0D || mc.field_71439_g.field_70702_br != 0.0D);
/*     */   }
/*     */   
/*     */   public static EntityPlayer getClosestEnemy(double distance) {
/* 557 */     EntityPlayer closest = null;
/* 558 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 559 */       if (isntValid((Entity)player, distance))
/* 560 */         continue;  if (closest == null) {
/* 561 */         closest = player;
/*     */         continue;
/*     */       } 
/* 564 */       if (mc.field_71439_g.func_70068_e((Entity)player) >= mc.field_71439_g.func_70068_e((Entity)closest))
/*     */         continue; 
/* 566 */       closest = player;
/*     */     } 
/* 568 */     return closest;
/*     */   }
/*     */   
/*     */   public static boolean checkCollide() {
/* 572 */     if (mc.field_71439_g.func_70093_af()) {
/* 573 */       return false;
/*     */     }
/* 575 */     if (mc.field_71439_g.func_184187_bx() != null && (mc.field_71439_g.func_184187_bx()).field_70143_R >= 3.0F) {
/* 576 */       return false;
/*     */     }
/* 578 */     return (mc.field_71439_g.field_70143_R < 3.0F);
/*     */   }
/*     */   
/*     */   public static BlockPos getPlayerPosWithEntity() {
/* 582 */     return new BlockPos((mc.field_71439_g.func_184187_bx() != null) ? (mc.field_71439_g.func_184187_bx()).field_70165_t : mc.field_71439_g.field_70165_t, (mc.field_71439_g.func_184187_bx() != null) ? (mc.field_71439_g.func_184187_bx()).field_70163_u : mc.field_71439_g.field_70163_u, (mc.field_71439_g.func_184187_bx() != null) ? (mc.field_71439_g.func_184187_bx()).field_70161_v : mc.field_71439_g.field_70161_v);
/*     */   }
/*     */   
/*     */   public static double[] forward(double speed) {
/* 586 */     float forward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 587 */     float side = mc.field_71439_g.field_71158_b.field_78902_a;
/* 588 */     float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
/* 589 */     if (forward != 0.0F) {
/* 590 */       if (side > 0.0F) {
/* 591 */         yaw += ((forward > 0.0F) ? -45 : 45);
/* 592 */       } else if (side < 0.0F) {
/* 593 */         yaw += ((forward > 0.0F) ? 45 : -45);
/*     */       } 
/* 595 */       side = 0.0F;
/* 596 */       if (forward > 0.0F) {
/* 597 */         forward = 1.0F;
/* 598 */       } else if (forward < 0.0F) {
/* 599 */         forward = -1.0F;
/*     */       } 
/*     */     } 
/* 602 */     double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
/* 603 */     double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
/* 604 */     double posX = forward * speed * cos + side * speed * sin;
/* 605 */     double posZ = forward * speed * sin - side * speed * cos;
/* 606 */     return new double[] { posX, posZ };
/*     */   }
/*     */   
/*     */   public static Map<String, Integer> getTextRadarPlayers() {
/* 610 */     Map<String, Integer> output = new HashMap<>();
/* 611 */     DecimalFormat dfHealth = new DecimalFormat("#.#");
/* 612 */     dfHealth.setRoundingMode(RoundingMode.CEILING);
/* 613 */     DecimalFormat dfDistance = new DecimalFormat("#.#");
/* 614 */     dfDistance.setRoundingMode(RoundingMode.CEILING);
/* 615 */     StringBuilder healthSB = new StringBuilder();
/* 616 */     StringBuilder distanceSB = new StringBuilder();
/* 617 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 618 */       if (player.func_82150_aj() || player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()))
/* 619 */         continue;  int hpRaw = (int)getHealth((Entity)player);
/* 620 */       String hp = dfHealth.format(hpRaw);
/* 621 */       healthSB.append("Â§");
/* 622 */       if (hpRaw >= 20) {
/* 623 */         healthSB.append("a");
/* 624 */       } else if (hpRaw >= 10) {
/* 625 */         healthSB.append("e");
/* 626 */       } else if (hpRaw >= 5) {
/* 627 */         healthSB.append("6");
/*     */       } else {
/* 629 */         healthSB.append("c");
/*     */       } 
/* 631 */       healthSB.append(hp);
/* 632 */       int distanceInt = (int)mc.field_71439_g.func_70032_d((Entity)player);
/* 633 */       String distance = dfDistance.format(distanceInt);
/* 634 */       distanceSB.append("Â§");
/* 635 */       if (distanceInt >= 25) {
/* 636 */         distanceSB.append("a");
/* 637 */       } else if (distanceInt > 10) {
/* 638 */         distanceSB.append("6");
/*     */       } else {
/* 640 */         distanceSB.append("c");
/*     */       } 
/* 642 */       distanceSB.append(distance);
/* 643 */       output.put(healthSB.toString() + " " + (Banzem.friendManager.isFriend(player) ? (String)ChatFormatting.AQUA : (String)ChatFormatting.RED) + player.func_70005_c_() + " " + distanceSB.toString() + " Â§f0", Integer.valueOf((int)mc.field_71439_g.func_70032_d((Entity)player)));
/* 644 */       healthSB.setLength(0);
/* 645 */       distanceSB.setLength(0);
/*     */     } 
/* 647 */     if (!output.isEmpty()) {
/* 648 */       output = MathUtil.sortByValue(output, false);
/*     */     }
/* 650 */     return output;
/*     */   }
/*     */   
/*     */   public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
/* 654 */     return (entity.field_70163_u >= blockPos.func_177956_o());
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\oyveyutils\OyVeyentityUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */