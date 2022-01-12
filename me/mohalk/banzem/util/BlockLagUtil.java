/*     */ package me.mohalk.banzem.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.item.ItemBlock;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketHeldItemChange;
/*     */ import net.minecraft.network.play.client.CPacketPlayer;
/*     */ import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlockLagUtil
/*     */   implements Util
/*     */ {
/*     */   public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
/*  30 */     boolean sneaking = false;
/*  31 */     EnumFacing side = getFirstFacing(pos);
/*  32 */     if (side == null) {
/*  33 */       return isSneaking;
/*     */     }
/*  35 */     BlockPos neighbour = pos.func_177972_a(side);
/*  36 */     EnumFacing opposite = side.func_176734_d();
/*  37 */     Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
/*  38 */     Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
/*  39 */     if (!mc.field_71439_g.func_70093_af()) {
/*  40 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/*  41 */       mc.field_71439_g.func_70095_a(true);
/*  42 */       sneaking = true;
/*     */     } 
/*  44 */     if (rotate) {
/*  45 */       faceVector(hitVec, true);
/*     */     }
/*  47 */     rightClickBlock(neighbour, hitVec, hand, opposite, packet);
/*  48 */     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/*  49 */     mc.field_71467_ac = 4;
/*  50 */     return (sneaking || isSneaking);
/*     */   }
/*     */   
/*     */   public static List<EnumFacing> getPossibleSides(BlockPos pos) {
/*  54 */     List<EnumFacing> facings = new ArrayList<>();
/*  55 */     for (EnumFacing side : EnumFacing.values()) {
/*  56 */       BlockPos neighbour = pos.func_177972_a(side);
/*  57 */       if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false)) {
/*  58 */         IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
/*  59 */         if (!blockState.func_185904_a().func_76222_j()) {
/*  60 */           facings.add(side);
/*     */         }
/*     */       } 
/*     */     } 
/*  64 */     return facings;
/*     */   }
/*     */   
/*     */   public static EnumFacing getFirstFacing(BlockPos pos) {
/*  68 */     Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
/*  69 */     if (iterator.hasNext()) {
/*  70 */       EnumFacing facing = iterator.next();
/*  71 */       return facing;
/*     */     } 
/*  73 */     return null;
/*     */   }
/*     */   
/*     */   public static Vec3d getEyesPos() {
/*  77 */     return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
/*     */   }
/*     */   
/*     */   public static float[] getLegitRotations(Vec3d vec) {
/*  81 */     Vec3d eyesPos = getEyesPos();
/*  82 */     double diffX = vec.field_72450_a - eyesPos.field_72450_a;
/*  83 */     double diffY = vec.field_72448_b - eyesPos.field_72448_b;
/*  84 */     double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
/*  85 */     double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
/*  86 */     float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
/*  87 */     float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
/*  88 */     return new float[] { mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - mc.field_71439_g.field_70177_z), mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - mc.field_71439_g.field_70125_A) };
/*     */   }
/*     */   
/*     */   public static void faceVector(Vec3d vec, boolean normalizeAngle) {
/*  92 */     float[] rotations = getLegitRotations(vec);
/*  93 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.func_180184_b((int)rotations[1], 360) : rotations[1], mc.field_71439_g.field_70122_E));
/*     */   }
/*     */   
/*     */   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
/*  97 */     if (packet) {
/*  98 */       float f = (float)(vec.field_72450_a - pos.func_177958_n());
/*  99 */       float f2 = (float)(vec.field_72448_b - pos.func_177956_o());
/* 100 */       float f3 = (float)(vec.field_72449_c - pos.func_177952_p());
/* 101 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
/*     */     } else {
/*     */       
/* 104 */       mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, direction, vec, hand);
/*     */     } 
/* 106 */     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 107 */     mc.field_71467_ac = 4;
/*     */   }
/*     */   
/*     */   public static int findHotbarBlock(Class clazz) {
/* 111 */     for (int i = 0; i < 9; i++) {
/* 112 */       ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
/* 113 */       if (stack != ItemStack.field_190927_a) {
/* 114 */         if (clazz.isInstance(stack.func_77973_b())) {
/* 115 */           return i;
/*     */         }
/* 117 */         if (stack.func_77973_b() instanceof ItemBlock) {
/* 118 */           Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
/* 119 */           if (clazz.isInstance(block)) {
/* 120 */             return i;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 125 */     return -1;
/*     */   }
/*     */   
/*     */   public static void switchToSlot(int slot) {
/* 129 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
/* 130 */     mc.field_71439_g.field_71071_by.field_70461_c = slot;
/* 131 */     mc.field_71442_b.func_78765_e();
/*     */   }
/*     */ 
/*     */   
/* 135 */   public static final Minecraft mc = Minecraft.func_71410_x();
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\BlockLagUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */