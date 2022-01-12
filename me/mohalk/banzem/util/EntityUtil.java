/*     */ package me.mohalk.banzem.util;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Collectors;
/*     */ import javax.annotation.Nullable;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.modules.client.Managers;
/*     */ import me.mohalk.banzem.features.modules.combat.Killaura;
/*     */ import me.mohalk.banzem.features.modules.player.Blink;
/*     */ import me.mohalk.banzem.features.modules.player.FakePlayer;
/*     */ import me.mohalk.banzem.features.modules.player.Freecam;
/*     */ import me.mohalk.banzem.mixin.mixins.accessors.IEntityLivingBase;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.EnumCreatureType;
/*     */ import net.minecraft.entity.item.EntityEnderCrystal;
/*     */ import net.minecraft.entity.monster.EntityEnderman;
/*     */ import net.minecraft.entity.monster.EntityIronGolem;
/*     */ import net.minecraft.entity.monster.EntityPigZombie;
/*     */ import net.minecraft.entity.passive.EntityWolf;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.Enchantments;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.NBTTagCompound;
/*     */ import net.minecraft.nbt.NBTTagList;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketUseEntity;
/*     */ import net.minecraft.potion.Potion;
/*     */ import net.minecraft.potion.PotionEffect;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.MovementInput;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EntityUtil
/*     */   implements Util
/*     */ {
/*  68 */   public static final Vec3d[] antiDropOffsetList = new Vec3d[] { new Vec3d(0.0D, -2.0D, 0.0D) };
/*  69 */   public static final Vec3d[] platformOffsetList = new Vec3d[] { new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D) };
/*  70 */   public static final Vec3d[] legOffsetList = new Vec3d[] { new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D) };
/*  71 */   public static final Vec3d[] doubleLegOffsetList = new Vec3d[] { new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -2.0D), new Vec3d(0.0D, 0.0D, 2.0D) };
/*  72 */   public static final Vec3d[] OffsetList = new Vec3d[] { new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D) };
/*  73 */   public static final Vec3d[] headpiece = new Vec3d[] { new Vec3d(0.0D, 2.0D, 0.0D) };
/*  74 */   public static final Vec3d[] offsetsNoHead = new Vec3d[] { new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D) };
/*  75 */   public static final Vec3d[] antiStepOffsetList = new Vec3d[] { new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D) };
/*  76 */   public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[] { new Vec3d(0.0D, 3.0D, 0.0D) };
/*     */ 
/*     */ 
/*     */   
/*     */   public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
/*  81 */     if (packet) {
/*  82 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(entity));
/*     */     } else {
/*  84 */       mc.field_71442_b.func_78764_a((EntityPlayer)mc.field_71439_g, entity);
/*     */     } 
/*  86 */     if (swingArm) {
/*  87 */       mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void OffhandAttack(Entity entity, boolean packet, boolean swingArm) {
/*  93 */     if (packet) {
/*  94 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(entity));
/*     */     } else {
/*  96 */       mc.field_71442_b.func_78764_a((EntityPlayer)mc.field_71439_g, entity);
/*     */     } 
/*  98 */     if (swingArm) {
/*  99 */       mc.field_71439_g.func_184609_a(EnumHand.OFF_HAND);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d interpolateEntity(Entity entity, float time) {
/* 105 */     return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * time);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
/* 110 */     return (new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).func_178787_e(getInterpolatedAmount(entity, partialTicks));
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
/* 115 */     return getInterpolatedPos(entity, partialTicks).func_178786_a((mc.func_175598_ae()).field_78725_b, (mc.func_175598_ae()).field_78726_c, (mc.func_175598_ae()).field_78723_d);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d getInterpolatedRenderPos(Vec3d vec) {
/* 120 */     return (new Vec3d(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c)).func_178786_a((mc.func_175598_ae()).field_78725_b, (mc.func_175598_ae()).field_78726_c, (mc.func_175598_ae()).field_78723_d);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
/* 125 */     return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
/* 130 */     return getInterpolatedAmount(entity, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
/* 135 */     return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isPassive(Entity entity) {
/* 140 */     return ((!(entity instanceof EntityWolf) || !((EntityWolf)entity).func_70919_bu()) && (entity instanceof net.minecraft.entity.EntityAgeable || entity instanceof net.minecraft.entity.passive.EntityAmbientCreature || entity instanceof net.minecraft.entity.passive.EntitySquid || (entity instanceof EntityIronGolem && ((EntityIronGolem)entity).func_70643_av() == null)));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isSafe(Entity entity, int height, boolean floor, boolean face) {
/* 145 */     return (getUnsafeBlocks(entity, height, floor, face).size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean stopSneaking(boolean isSneaking) {
/* 150 */     if (isSneaking && mc.field_71439_g != null) {
/* 151 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
/*     */     }
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isSafe(Entity entity) {
/* 158 */     return isSafe(entity, 0, false, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos getPlayerPos(EntityPlayer player) {
/* 163 */     return new BlockPos(Math.floor(player.field_70165_t), Math.floor(player.field_70163_u), Math.floor(player.field_70161_v));
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor, boolean face) {
/* 168 */     return getUnsafeBlocksFromVec3d(entity.func_174791_d().func_72441_c(0.0D, 0.125D, 0.0D), height, floor, face);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isMobAggressive(Entity entity) {
/* 173 */     if (entity instanceof EntityPigZombie) {
/* 174 */       if (((EntityPigZombie)entity).func_184734_db() || ((EntityPigZombie)entity).func_175457_ck()) {
/* 175 */         return true;
/*     */       }
/*     */     } else {
/* 178 */       if (entity instanceof EntityWolf) {
/* 179 */         return (((EntityWolf)entity).func_70919_bu() && !mc.field_71439_g.equals(((EntityWolf)entity).func_70902_q()));
/*     */       }
/* 181 */       if (entity instanceof EntityEnderman) {
/* 182 */         return ((EntityEnderman)entity).func_70823_r();
/*     */       }
/*     */     } 
/* 185 */     return isHostileMob(entity);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isNeutralMob(Entity entity) {
/* 190 */     return (entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isProjectile(Entity entity) {
/* 195 */     return (entity instanceof net.minecraft.entity.projectile.EntityShulkerBullet || entity instanceof net.minecraft.entity.projectile.EntityFireball);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isVehicle(Entity entity) {
/* 200 */     return (entity instanceof net.minecraft.entity.item.EntityBoat || entity instanceof net.minecraft.entity.item.EntityMinecart);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isFriendlyMob(Entity entity) {
/* 205 */     return ((entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof net.minecraft.entity.passive.EntityVillager || entity instanceof EntityIronGolem || (isNeutralMob(entity) && !isMobAggressive(entity)));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isHostileMob(Entity entity) {
/* 210 */     return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity));
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor, boolean face) {
/* 215 */     List<Vec3d> vec3ds = new ArrayList<>();
/* 216 */     for (Vec3d vector : getOffsets(height, floor, face)) {
/* 217 */       BlockPos targetPos = (new BlockPos(pos)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
/* 218 */       Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
/* 219 */       if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow) {
/* 220 */         vec3ds.add(vector);
/*     */       }
/*     */     } 
/* 223 */     return vec3ds;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isInHole(Entity entity) {
/* 228 */     return isBlockValid(new BlockPos(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isBlockValid(BlockPos blockPos) {
/* 233 */     return (isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isCrystalAtFeet(EntityEnderCrystal crystal, double range) {
/* 238 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 239 */       if (mc.field_71439_g.func_70068_e((Entity)player) > range * range) {
/*     */         continue;
/*     */       }
/* 242 */       if (Banzem.friendManager.isFriend(player)) {
/*     */         continue;
/*     */       }
/* 245 */       for (Vec3d vec : doubleLegOffsetList) {
/* 246 */         if ((new BlockPos(player.func_174791_d())).func_177963_a(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c) == crystal.func_180425_c()) {
/* 247 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 251 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isObbyHole(BlockPos blockPos) {
/* 257 */     BlockPos[] array = { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() };
/* 258 */     for (BlockPos pos : array) {
/* 259 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 260 */       if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150343_Z) {
/* 261 */         return false;
/*     */       }
/*     */     } 
/* 264 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBedrockHole(BlockPos blockPos) {
/* 270 */     BlockPos[] array = { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() };
/* 271 */     for (BlockPos pos : array) {
/* 272 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 273 */       if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h) {
/* 274 */         return false;
/*     */       }
/*     */     } 
/* 277 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBothHole(BlockPos blockPos) {
/* 283 */     BlockPos[] array = { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() };
/* 284 */     for (BlockPos pos : array) {
/* 285 */       IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
/* 286 */       if (touchingState.func_177230_c() == Blocks.field_150350_a || (touchingState.func_177230_c() != Blocks.field_150357_h && touchingState.func_177230_c() != Blocks.field_150343_Z)) {
/* 287 */         return false;
/*     */       }
/*     */     } 
/* 290 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor, boolean face) {
/* 295 */     List<Vec3d> list = getUnsafeBlocks(entity, height, floor, face);
/* 296 */     Vec3d[] array = new Vec3d[list.size()];
/* 297 */     return list.<Vec3d>toArray(array);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor, boolean face) {
/* 302 */     List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor, face);
/* 303 */     Vec3d[] array = new Vec3d[list.size()];
/* 304 */     return list.<Vec3d>toArray(array);
/*     */   }
/*     */ 
/*     */   
/*     */   public static double getDst(Vec3d vec) {
/* 309 */     return mc.field_71439_g.func_174791_d().func_72438_d(vec);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
/* 314 */     return (getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop, face).size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean noScaffoldExtend, boolean face) {
/* 319 */     return (getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace, noScaffoldExtend, face).size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
/* 324 */     List<Vec3d> vec3ds = new ArrayList<>();
/* 325 */     if (!antiStep && getUnsafeBlocks((Entity)player, 2, false, face).size() == 4) {
/* 326 */       vec3ds.addAll(getUnsafeBlocks((Entity)player, 2, false, face));
/*     */     }
/* 328 */     for (int i = 0; i < (getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop, face)).length; i++) {
/* 329 */       Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop, face)[i];
/* 330 */       BlockPos targetPos = (new BlockPos(player.func_174791_d())).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
/* 331 */       Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
/* 332 */       if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow) {
/* 333 */         vec3ds.add(vector);
/*     */       }
/*     */     } 
/* 336 */     return vec3ds;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isInWater(Entity entity) {
/* 341 */     if (entity == null) {
/* 342 */       return false;
/*     */     }
/* 344 */     double y = entity.field_70163_u + 0.01D;
/* 345 */     for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); x++) {
/* 346 */       for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); z++) {
/* 347 */         BlockPos pos = new BlockPos(x, (int)y, z);
/* 348 */         if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof net.minecraft.block.BlockLiquid) {
/* 349 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 353 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isDrivenByPlayer(Entity entityIn) {
/* 358 */     return (mc.field_71439_g != null && entityIn != null && entityIn.equals(mc.field_71439_g.func_184187_bx()));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isPlayer(Entity entity) {
/* 363 */     return entity instanceof EntityPlayer;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAboveWater(Entity entity) {
/* 368 */     return isAboveWater(entity, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAboveWater(Entity entity, boolean packet) {
/* 373 */     if (entity == null) {
/* 374 */       return false;
/*     */     }
/* 376 */     double y = entity.field_70163_u - (packet ? 0.03D : (isPlayer(entity) ? 0.2D : 0.5D));
/* 377 */     for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); x++) {
/* 378 */       for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); z++) {
/* 379 */         BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
/* 380 */         if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof net.minecraft.block.BlockLiquid) {
/* 381 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 385 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean noScaffoldExtend, boolean face) {
/* 390 */     List<Vec3d> placeTargets = new ArrayList<>();
/* 391 */     if (extension == 1) {
/* 392 */       placeTargets.addAll(targets(player.func_174791_d(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace, face));
/*     */     } else {
/* 394 */       int extend = 1;
/* 395 */       for (Vec3d vec3d : MathUtil.getBlockBlocks((Entity)player)) {
/* 396 */         if (extend > extension) {
/*     */           break;
/*     */         }
/* 399 */         placeTargets.addAll(targets(vec3d, !noScaffoldExtend, antiStep, legs, platform, antiDrop, raytrace, face));
/* 400 */         extend++;
/*     */       } 
/*     */     } 
/* 403 */     List<Vec3d> removeList = new ArrayList<>();
/* 404 */     for (Vec3d vec3d : placeTargets) {
/* 405 */       BlockPos pos = new BlockPos(vec3d);
/* 406 */       if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
/* 407 */         removeList.add(vec3d);
/*     */       }
/*     */     } 
/* 410 */     for (Vec3d vec3d : removeList) {
/* 411 */       placeTargets.remove(vec3d);
/*     */     }
/* 413 */     return placeTargets;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean face) {
/* 418 */     List<Vec3d> placeTargets = new ArrayList<>();
/* 419 */     if (antiDrop) {
/* 420 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
/*     */     }
/* 422 */     if (platform) {
/* 423 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
/*     */     }
/* 425 */     if (legs) {
/* 426 */       Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
/*     */     }
/* 428 */     Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
/* 429 */     if (antiStep)
/* 430 */     { Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList)); }
/*     */     else
/* 432 */     { List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false, face);
/* 433 */       if (vec3ds.size() == 4)
/* 434 */       { Iterator<Vec3d> iterator = vec3ds.iterator(); while (true) { if (iterator.hasNext()) { Vec3d vector = iterator.next();
/* 435 */             BlockPos position = (new BlockPos(vec3d)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
/* 436 */             switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
/*     */               case -1:
/*     */               case 1:
/*     */               case 2:
/*     */                 continue;
/*     */               
/*     */               case 3:
/* 443 */                 placeTargets.add(vec3d.func_178787_e(vector));
/*     */                 break;
/*     */               default:
/*     */                 break;
/*     */             }  }
/*     */           else
/*     */           { break; }
/*     */           
/* 451 */           if (antiScaffold) {
/* 452 */             Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
/*     */           }
/* 454 */           if (!face) {
/* 455 */             List<Vec3d> offsets = new ArrayList<>();
/* 456 */             offsets.add(new Vec3d(1.0D, 1.0D, 0.0D));
/* 457 */             offsets.add(new Vec3d(0.0D, 1.0D, -1.0D));
/* 458 */             offsets.add(new Vec3d(0.0D, 1.0D, 1.0D));
/* 459 */             Vec3d[] array = new Vec3d[offsets.size()];
/* 460 */             placeTargets.removeAll(Arrays.asList((Object[])BlockUtil.convertVec3ds(vec3d, offsets.<Vec3d>toArray(array))));
/*     */           } 
/* 462 */           return placeTargets; }  }  }  if (antiScaffold) Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));  if (!face) { ArrayList<Vec3d> arrayList = new ArrayList(); arrayList.add(new Vec3d(1.0D, 1.0D, 0.0D)); arrayList.add(new Vec3d(0.0D, 1.0D, -1.0D)); arrayList.add(new Vec3d(0.0D, 1.0D, 1.0D)); Vec3d[] arrayOfVec3d = new Vec3d[arrayList.size()]; placeTargets.removeAll(Arrays.asList((Object[])BlockUtil.convertVec3ds(vec3d, arrayList.<Vec3d>toArray(arrayOfVec3d)))); }  return placeTargets;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Vec3d> getOffsetList(int y, boolean floor, boolean face) {
/* 467 */     List<Vec3d> offsets = new ArrayList<>();
/* 468 */     if (face) {
/* 469 */       offsets.add(new Vec3d(-1.0D, y, 0.0D));
/* 470 */       offsets.add(new Vec3d(1.0D, y, 0.0D));
/* 471 */       offsets.add(new Vec3d(0.0D, y, -1.0D));
/* 472 */       offsets.add(new Vec3d(0.0D, y, 1.0D));
/*     */     } else {
/* 474 */       offsets.add(new Vec3d(-1.0D, y, 0.0D));
/*     */     } 
/* 476 */     if (floor) {
/* 477 */       offsets.add(new Vec3d(0.0D, (y - 1), 0.0D));
/*     */     }
/* 479 */     return offsets;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] getOffsets(int y, boolean floor, boolean face) {
/* 484 */     List<Vec3d> offsets = getOffsetList(y, floor, face);
/* 485 */     Vec3d[] array = new Vec3d[offsets.size()];
/* 486 */     return offsets.<Vec3d>toArray(array);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
/* 491 */     List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop, face);
/* 492 */     Vec3d[] array = new Vec3d[offsets.size()];
/* 493 */     return offsets.<Vec3d>toArray(array);
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
/* 498 */     List<Vec3d> offsets = new ArrayList<>(getOffsetList(1, false, face));
/* 499 */     offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
/* 500 */     if (antiScaffold) {
/* 501 */       offsets.add(new Vec3d(0.0D, 3.0D, 0.0D));
/*     */     }
/* 503 */     if (antiStep) {
/* 504 */       offsets.addAll(getOffsetList(2, false, face));
/*     */     }
/* 506 */     if (legs) {
/* 507 */       offsets.addAll(getOffsetList(0, false, face));
/*     */     }
/* 509 */     if (platform) {
/* 510 */       offsets.addAll(getOffsetList(-1, false, face));
/* 511 */       offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
/*     */     } 
/* 513 */     if (antiDrop) {
/* 514 */       offsets.add(new Vec3d(0.0D, -2.0D, 0.0D));
/*     */     }
/* 516 */     return offsets;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vec3d[] getHeightOffsets(int min, int max) {
/* 521 */     List<Vec3d> offsets = new ArrayList<>();
/* 522 */     for (int i = min; i <= max; i++) {
/* 523 */       offsets.add(new Vec3d(0.0D, i, 0.0D));
/*     */     }
/* 525 */     Vec3d[] array = new Vec3d[offsets.size()];
/* 526 */     return offsets.<Vec3d>toArray(array);
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos getRoundedBlockPos(Entity entity) {
/* 531 */     return new BlockPos(MathUtil.roundVec(entity.func_174791_d(), 0));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isLiving(Entity entity) {
/* 536 */     return entity instanceof EntityLivingBase;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAlive(Entity entity) {
/* 541 */     return (isLiving(entity) && !entity.field_70128_L && ((EntityLivingBase)entity).func_110143_aJ() > 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isDead(Entity entity) {
/* 546 */     return !isAlive(entity);
/*     */   }
/*     */ 
/*     */   
/*     */   public static float getHealth(Entity entity) {
/* 551 */     if (isLiving(entity)) {
/* 552 */       EntityLivingBase livingBase = (EntityLivingBase)entity;
/* 553 */       return livingBase.func_110143_aJ() + livingBase.func_110139_bj();
/*     */     } 
/* 555 */     return 0.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<EntityPlayer> getNearbyPlayers(double d) {
/* 560 */     if (mc.field_71441_e.func_72910_y().size() == 0) {
/* 561 */       return null;
/*     */     }
/* 563 */     List<EntityPlayer> list = (List<EntityPlayer>)mc.field_71441_e.field_73010_i.stream().filter(entityPlayer -> (mc.field_71439_g != entityPlayer)).filter(entityPlayer -> (mc.field_71439_g.func_70032_d((Entity)entityPlayer) <= d)).filter(entityPlayer -> (getHealth((Entity)entityPlayer) >= 0.0F)).collect(Collectors.toList());
/* 564 */     list.removeIf(entityPlayer -> Banzem.friendManager.isFriend(entityPlayer.func_70005_c_()));
/* 565 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos GetPositionVectorBlockPos(Entity entity, @Nullable BlockPos blockPos) {
/* 570 */     Vec3d vec3d = entity.func_174791_d();
/* 571 */     if (blockPos == null) {
/* 572 */       return new BlockPos(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
/*     */     }
/* 574 */     return (new BlockPos(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c)).func_177971_a((Vec3i)blockPos);
/*     */   }
/*     */ 
/*     */   
/*     */   public static float getHealth(Entity entity, boolean absorption) {
/* 579 */     if (isLiving(entity)) {
/* 580 */       EntityLivingBase livingBase = (EntityLivingBase)entity;
/* 581 */       return livingBase.func_110143_aJ() + (absorption ? livingBase.func_110139_bj() : 0.0F);
/*     */     } 
/* 583 */     return 0.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean canEntityFeetBeSeen(Entity entityIn) {
/* 588 */     return (mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70165_t + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(entityIn.field_70165_t, entityIn.field_70163_u, entityIn.field_70161_v), false, true, false) == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isntValid(Entity entity, double range) {
/* 593 */     return (entity == null || isDead(entity) || entity.equals(mc.field_71439_g) || (entity instanceof EntityPlayer && Banzem.friendManager.isFriend(entity.func_70005_c_())) || mc.field_71439_g.func_70068_e(entity) > MathUtil.square(range));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isValid(Entity entity, double range) {
/* 598 */     return !isntValid(entity, range);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean holdingWeapon(EntityPlayer player) {
/* 603 */     return (player.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemSword || player.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemAxe);
/*     */   }
/*     */ 
/*     */   
/*     */   public static double getMaxSpeed() {
/* 608 */     double maxModifier = 0.2873D;
/* 609 */     if (mc.field_71439_g.func_70644_a(Objects.<Potion>requireNonNull(Potion.func_188412_a(1)))) {
/* 610 */       maxModifier *= 1.0D + 0.2D * (((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.field_71439_g.func_70660_b(Objects.<Potion>requireNonNull(Potion.func_188412_a(1))))).func_76458_c() + 1);
/*     */     }
/* 612 */     return maxModifier;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void mutliplyEntitySpeed(Entity entity, double multiplier) {
/* 617 */     if (entity != null) {
/* 618 */       entity.field_70159_w *= multiplier;
/* 619 */       entity.field_70179_y *= multiplier;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isEntityMoving(Entity entity) {
/* 625 */     if (entity == null) {
/* 626 */       return false;
/*     */     }
/* 628 */     if (entity instanceof EntityPlayer) {
/* 629 */       return (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d());
/*     */     }
/* 631 */     return (entity.field_70159_w != 0.0D || entity.field_70181_x != 0.0D || entity.field_70179_y != 0.0D);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean movementKey() {
/* 636 */     return (mc.field_71439_g.field_71158_b.field_187255_c || mc.field_71439_g.field_71158_b.field_187258_f || mc.field_71439_g.field_71158_b.field_187257_e || mc.field_71439_g.field_71158_b.field_187256_d || mc.field_71439_g.field_71158_b.field_78901_c || mc.field_71439_g.field_71158_b.field_78899_d);
/*     */   }
/*     */ 
/*     */   
/*     */   public static double getEntitySpeed(Entity entity) {
/* 641 */     if (entity != null) {
/* 642 */       double distTraveledLastTickX = entity.field_70165_t - entity.field_70169_q;
/* 643 */       double distTraveledLastTickZ = entity.field_70161_v - entity.field_70166_s;
/* 644 */       double speed = MathHelper.func_76133_a(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ);
/* 645 */       return speed * 20.0D;
/*     */     } 
/* 647 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean holding32k(EntityPlayer player) {
/* 652 */     return is32k(player.func_184614_ca());
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean is32k(ItemStack stack) {
/* 657 */     if (stack == null) {
/* 658 */       return false;
/*     */     }
/* 660 */     if (stack.func_77978_p() == null) {
/* 661 */       return false;
/*     */     }
/* 663 */     NBTTagList enchants = (NBTTagList)stack.func_77978_p().func_74781_a("ench");
/* 664 */     int i = 0;
/* 665 */     while (i < enchants.func_74745_c()) {
/* 666 */       NBTTagCompound enchant = enchants.func_150305_b(i);
/* 667 */       if (enchant.func_74762_e("id") == 16) {
/* 668 */         int lvl = enchant.func_74762_e("lvl");
/* 669 */         if (lvl >= 42) {
/* 670 */           return true;
/*     */         }
/*     */         break;
/*     */       } 
/* 674 */       i++;
/*     */     } 
/*     */     
/* 677 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean simpleIs32k(ItemStack stack) {
/* 682 */     return (EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, stack) >= 1000);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void moveEntityStrafe(double speed, Entity entity) {
/* 687 */     if (entity != null) {
/* 688 */       MovementInput movementInput = mc.field_71439_g.field_71158_b;
/* 689 */       double forward = movementInput.field_192832_b;
/* 690 */       double strafe = movementInput.field_78902_a;
/* 691 */       float yaw = mc.field_71439_g.field_70177_z;
/* 692 */       if (forward == 0.0D && strafe == 0.0D) {
/* 693 */         entity.field_70159_w = 0.0D;
/* 694 */         entity.field_70179_y = 0.0D;
/*     */       } else {
/* 696 */         if (forward != 0.0D) {
/* 697 */           if (strafe > 0.0D) {
/* 698 */             yaw += ((forward > 0.0D) ? -45 : 45);
/* 699 */           } else if (strafe < 0.0D) {
/* 700 */             yaw += ((forward > 0.0D) ? 45 : -45);
/*     */           } 
/* 702 */           strafe = 0.0D;
/* 703 */           if (forward > 0.0D) {
/* 704 */             forward = 1.0D;
/* 705 */           } else if (forward < 0.0D) {
/* 706 */             forward = -1.0D;
/*     */           } 
/*     */         } 
/* 709 */         entity.field_70159_w = forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F)));
/* 710 */         entity.field_70179_y = forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F)));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean rayTraceHitCheck(Entity entity, boolean shouldCheck) {
/* 717 */     return (!shouldCheck || mc.field_71439_g.func_70685_l(entity));
/*     */   }
/*     */ 
/*     */   
/*     */   public static Color getColor(Entity entity, int red, int green, int blue, int alpha, boolean colorFriends) {
/* 722 */     Color color = new Color(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
/* 723 */     if (entity instanceof EntityPlayer) {
/* 724 */       if (colorFriends && Banzem.friendManager.isFriend((EntityPlayer)entity)) {
/* 725 */         color = new Color(0.33333334F, 1.0F, 1.0F, alpha / 255.0F);
/*     */       }
/* 727 */       Killaura killaura = (Killaura)Banzem.moduleManager.getModuleByClass(Killaura.class);
/* 728 */       if (((Boolean)killaura.info.getValue()).booleanValue() && Killaura.target != null && Killaura.target.equals(entity)) {
/* 729 */         color = new Color(1.0F, 0.0F, 0.0F, alpha / 255.0F);
/*     */       }
/*     */     } 
/* 732 */     return color;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isFakePlayer(EntityPlayer player) {
/* 737 */     Freecam freecam = Freecam.getInstance();
/* 738 */     FakePlayer fakePlayer = FakePlayer.getInstance();
/* 739 */     Blink blink = Blink.getInstance();
/* 740 */     int playerID = player.func_145782_y();
/* 741 */     if (freecam.isOn() && playerID == 69420) {
/* 742 */       return true;
/*     */     }
/* 744 */     if (fakePlayer.isOn()) {
/* 745 */       for (Iterator<Integer> iterator = fakePlayer.fakePlayerIdList.iterator(); iterator.hasNext(); ) { int id = ((Integer)iterator.next()).intValue();
/* 746 */         if (id == playerID) {
/* 747 */           return true;
/*     */         } }
/*     */     
/*     */     }
/* 751 */     return (blink.isOn() && playerID == 6942069);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isMoving() {
/* 756 */     return (mc.field_71439_g.field_191988_bg != 0.0D || mc.field_71439_g.field_70702_br != 0.0D);
/*     */   }
/*     */ 
/*     */   
/*     */   public static EntityPlayer getClosestEnemy(double distance) {
/* 761 */     EntityPlayer closest = null;
/* 762 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 763 */       if (isntValid((Entity)player, distance)) {
/*     */         continue;
/*     */       }
/* 766 */       if (closest == null) {
/* 767 */         closest = player; continue;
/*     */       } 
/* 769 */       if (mc.field_71439_g.func_70068_e((Entity)player) >= mc.field_71439_g.func_70068_e((Entity)closest)) {
/*     */         continue;
/*     */       }
/* 772 */       closest = player;
/*     */     } 
/*     */     
/* 775 */     return closest;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean checkCollide() {
/* 780 */     return (!mc.field_71439_g.func_70093_af() && (mc.field_71439_g.func_184187_bx() == null || (mc.field_71439_g.func_184187_bx()).field_70143_R < 3.0F) && mc.field_71439_g.field_70143_R < 3.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isInLiquid() {
/* 785 */     if (mc.field_71439_g.field_70143_R >= 3.0F) {
/* 786 */       return false;
/*     */     }
/* 788 */     boolean inLiquid = false;
/* 789 */     AxisAlignedBB bb = (mc.field_71439_g.func_184187_bx() != null) ? mc.field_71439_g.func_184187_bx().func_174813_aQ() : mc.field_71439_g.func_174813_aQ();
/* 790 */     int y = (int)bb.field_72338_b;
/* 791 */     for (int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; x++) {
/* 792 */       for (int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; z++) {
/* 793 */         Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
/* 794 */         if (!(block instanceof net.minecraft.block.BlockAir)) {
/* 795 */           if (!(block instanceof net.minecraft.block.BlockLiquid)) {
/* 796 */             return false;
/*     */           }
/* 798 */           inLiquid = true;
/*     */         } 
/*     */       } 
/*     */     } 
/* 802 */     return inLiquid;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isOnLiquid(double offset) {
/* 807 */     if (mc.field_71439_g.field_70143_R >= 3.0F) {
/* 808 */       return false;
/*     */     }
/* 810 */     AxisAlignedBB bb = (mc.field_71439_g.func_184187_bx() != null) ? mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(0.0D, -offset, 0.0D) : mc.field_71439_g.func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(0.0D, -offset, 0.0D);
/* 811 */     boolean onLiquid = false;
/* 812 */     int y = (int)bb.field_72338_b;
/* 813 */     for (int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d + 1.0D); x++) {
/* 814 */       for (int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f + 1.0D); z++) {
/* 815 */         Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
/* 816 */         if (block != Blocks.field_150350_a) {
/* 817 */           if (!(block instanceof net.minecraft.block.BlockLiquid)) {
/* 818 */             return false;
/*     */           }
/* 820 */           onLiquid = true;
/*     */         } 
/*     */       } 
/*     */     } 
/* 824 */     return onLiquid;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAboveLiquid(Entity entity) {
/* 829 */     if (entity == null) {
/* 830 */       return false;
/*     */     }
/* 832 */     double n = entity.field_70163_u + 0.01D;
/* 833 */     for (int i = MathHelper.func_76128_c(entity.field_70165_t); i < MathHelper.func_76143_f(entity.field_70165_t); i++) {
/* 834 */       for (int j = MathHelper.func_76128_c(entity.field_70161_v); j < MathHelper.func_76143_f(entity.field_70161_v); j++) {
/* 835 */         if (mc.field_71441_e.func_180495_p(new BlockPos(i, (int)n, j)).func_177230_c() instanceof net.minecraft.block.BlockLiquid) {
/* 836 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 840 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BlockPos getPlayerPosWithEntity() {
/* 845 */     return new BlockPos((mc.field_71439_g.func_184187_bx() != null) ? (mc.field_71439_g.func_184187_bx()).field_70165_t : mc.field_71439_g.field_70165_t, (mc.field_71439_g.func_184187_bx() != null) ? (mc.field_71439_g.func_184187_bx()).field_70163_u : mc.field_71439_g.field_70163_u, (mc.field_71439_g.func_184187_bx() != null) ? (mc.field_71439_g.func_184187_bx()).field_70161_v : mc.field_71439_g.field_70161_v);
/*     */   }
/*     */   
/*     */   public static boolean checkForLiquid(Entity entity, boolean b) {
/*     */     double n;
/* 850 */     if (entity == null) {
/* 851 */       return false;
/*     */     }
/* 853 */     double posY = entity.field_70163_u;
/*     */     
/* 855 */     if (b) {
/* 856 */       n = 0.03D;
/* 857 */     } else if (entity instanceof EntityPlayer) {
/* 858 */       n = 0.2D;
/*     */     } else {
/* 860 */       n = 0.5D;
/*     */     } 
/* 862 */     double n2 = posY - n;
/* 863 */     for (int i = MathHelper.func_76128_c(entity.field_70165_t); i < MathHelper.func_76143_f(entity.field_70165_t); i++) {
/* 864 */       for (int j = MathHelper.func_76128_c(entity.field_70161_v); j < MathHelper.func_76143_f(entity.field_70161_v); j++) {
/* 865 */         if (mc.field_71441_e.func_180495_p(new BlockPos(i, MathHelper.func_76128_c(n2), j)).func_177230_c() instanceof net.minecraft.block.BlockLiquid) {
/* 866 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 870 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isOnLiquid() {
/* 875 */     double y = mc.field_71439_g.field_70163_u - 0.03D;
/* 876 */     for (int x = MathHelper.func_76128_c(mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f(mc.field_71439_g.field_70165_t); x++) {
/* 877 */       for (int z = MathHelper.func_76128_c(mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f(mc.field_71439_g.field_70161_v); z++) {
/* 878 */         BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
/* 879 */         if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof net.minecraft.block.BlockLiquid) {
/* 880 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 884 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double[] forward(double speed) {
/* 889 */     float forward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 890 */     float side = mc.field_71439_g.field_71158_b.field_78902_a;
/* 891 */     float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
/* 892 */     if (forward != 0.0F) {
/* 893 */       if (side > 0.0F) {
/* 894 */         yaw += ((forward > 0.0F) ? -45 : 45);
/* 895 */       } else if (side < 0.0F) {
/* 896 */         yaw += ((forward > 0.0F) ? 45 : -45);
/*     */       } 
/* 898 */       side = 0.0F;
/* 899 */       if (forward > 0.0F) {
/* 900 */         forward = 1.0F;
/* 901 */       } else if (forward < 0.0F) {
/* 902 */         forward = -1.0F;
/*     */       } 
/*     */     } 
/* 905 */     double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
/* 906 */     double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
/* 907 */     double posX = forward * speed * cos + side * speed * sin;
/* 908 */     double posZ = forward * speed * sin - side * speed * cos;
/* 909 */     return new double[] { posX, posZ };
/*     */   }
/*     */ 
/*     */   
/*     */   public static Map<String, Integer> getTextRadarPlayers() {
/* 914 */     Map<String, Integer> output = new HashMap<>();
/* 915 */     DecimalFormat dfHealth = new DecimalFormat("#.#");
/* 916 */     dfHealth.setRoundingMode(RoundingMode.CEILING);
/* 917 */     DecimalFormat dfDistance = new DecimalFormat("#.#");
/* 918 */     dfDistance.setRoundingMode(RoundingMode.CEILING);
/* 919 */     StringBuilder healthSB = new StringBuilder();
/* 920 */     StringBuilder distanceSB = new StringBuilder();
/* 921 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 922 */       if (player.func_82150_aj() && !((Boolean)(Managers.getInstance()).tRadarInv.getValue()).booleanValue()) {
/*     */         continue;
/*     */       }
/* 925 */       if (player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_())) {
/*     */         continue;
/*     */       }
/* 928 */       int hpRaw = (int)getHealth((Entity)player);
/* 929 */       String hp = dfHealth.format(hpRaw);
/* 930 */       healthSB.append("§");
/* 931 */       if (hpRaw >= 20) {
/* 932 */         healthSB.append("a");
/* 933 */       } else if (hpRaw >= 10) {
/* 934 */         healthSB.append("e");
/* 935 */       } else if (hpRaw >= 5) {
/* 936 */         healthSB.append("6");
/*     */       } else {
/* 938 */         healthSB.append("c");
/*     */       } 
/* 940 */       healthSB.append(hp);
/* 941 */       int distanceInt = (int)mc.field_71439_g.func_70032_d((Entity)player);
/* 942 */       String distance = dfDistance.format(distanceInt);
/* 943 */       distanceSB.append("§");
/* 944 */       if (distanceInt >= 25) {
/* 945 */         distanceSB.append("a");
/* 946 */       } else if (distanceInt > 10) {
/* 947 */         distanceSB.append("6");
/*     */       } else {
/* 949 */         distanceSB.append("c");
/*     */       } 
/* 951 */       distanceSB.append(distance);
/* 952 */       output.put(healthSB + " " + (Banzem.friendManager.isFriend(player) ? "§b" : "§r") + player.func_70005_c_() + " " + distanceSB + " §f" + Banzem.totemPopManager.getTotemPopString(player) + Banzem.potionManager.getTextRadarPotion(player), Integer.valueOf((int)mc.field_71439_g.func_70032_d((Entity)player)));
/* 953 */       healthSB.setLength(0);
/* 954 */       distanceSB.setLength(0);
/*     */     } 
/* 956 */     if (!output.isEmpty()) {
/* 957 */       output = MathUtil.sortByValue(output, false);
/*     */     }
/* 959 */     return output;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void swingArmNoPacket(EnumHand hand, EntityLivingBase entity) {
/* 964 */     ItemStack stack = entity.func_184586_b(hand);
/* 965 */     if (!stack.func_190926_b() && stack.func_77973_b().onEntitySwing(entity, stack)) {
/*     */       return;
/*     */     }
/* 968 */     if (!entity.field_82175_bq || entity.field_110158_av >= ((IEntityLivingBase)entity).getArmSwingAnimationEnd() / 2 || entity.field_110158_av < 0) {
/* 969 */       entity.field_110158_av = -1;
/* 970 */       entity.field_82175_bq = true;
/* 971 */       entity.field_184622_au = hand;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
/* 977 */     return (entity.field_70163_u >= blockPos.func_177956_o());
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\EntityUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */