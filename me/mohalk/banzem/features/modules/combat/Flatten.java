/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import net.minecraft.block.BlockObsidian;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Flatten
/*     */   extends Module
/*     */ {
/*  24 */   private final Setting<Float> placerange = register(new Setting("PlaceRange", Float.valueOf(6.0F), Float.valueOf(1.0F), Float.valueOf(10.0F)));
/*  25 */   private final Setting<Integer> blocksPerTick = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
/*  26 */   private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
/*  27 */   private final Setting<Boolean> packet = register(new Setting("PacketPlace", Boolean.valueOf(false)));
/*  28 */   private final Setting<Boolean> autoDisable = register(new Setting("AutoDisable", Boolean.valueOf(true)));
/*  29 */   private final Vec3d[] offsetsDefault = new Vec3d[] { new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D) };
/*     */   
/*     */   private int offsetStep;
/*     */   private int oldSlot;
/*     */   
/*     */   public Flatten() {
/*  35 */     super("Flatten", "Flatter then zprestiges 9 yr gf.", Module.Category.COMBAT, true, false, false);
/*  36 */     this.offsetStep = 0;
/*  37 */     this.oldSlot = -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  42 */     this.oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  47 */     this.oldSlot = -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  52 */     EntityPlayer closest_target = findClosestTarget();
/*  53 */     int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/*  54 */     if (closest_target == null) {
/*  55 */       disable();
/*     */       return;
/*     */     } 
/*  58 */     List<Vec3d> place_targets = new ArrayList<>();
/*  59 */     Collections.addAll(place_targets, this.offsetsDefault);
/*  60 */     int blocks_placed = 0;
/*  61 */     while (blocks_placed < ((Integer)this.blocksPerTick.getValue()).intValue()) {
/*  62 */       if (this.offsetStep >= place_targets.size()) {
/*  63 */         this.offsetStep = 0;
/*     */         break;
/*     */       } 
/*  66 */       BlockPos offset_pos = new BlockPos(place_targets.get(this.offsetStep));
/*  67 */       BlockPos target_pos = (new BlockPos(closest_target.func_174791_d())).func_177977_b().func_177982_a(offset_pos.func_177958_n(), offset_pos.func_177956_o(), offset_pos.func_177952_p());
/*  68 */       boolean should_try_place = mc.field_71441_e.func_180495_p(target_pos).func_185904_a().func_76222_j();
/*  69 */       for (Entity entity : mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(target_pos))) {
/*  70 */         if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb)) {
/*  71 */           should_try_place = false;
/*     */           break;
/*     */         } 
/*     */       } 
/*  75 */       if (should_try_place) {
/*  76 */         place(target_pos, obbySlot, this.oldSlot);
/*  77 */         blocks_placed++;
/*     */       } 
/*  79 */       this.offsetStep++;
/*     */     } 
/*  81 */     if (((Boolean)this.autoDisable.getValue()).booleanValue()) {
/*  82 */       disable();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void place(BlockPos pos, int slot, int oldSlot) {
/*  88 */     mc.field_71439_g.field_71071_by.field_70461_c = slot;
/*  89 */     mc.field_71442_b.func_78765_e();
/*  90 */     BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), mc.field_71439_g.func_70093_af());
/*  91 */     mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
/*  92 */     mc.field_71442_b.func_78765_e();
/*     */   }
/*     */ 
/*     */   
/*     */   private EntityPlayer findClosestTarget() {
/*  97 */     if (mc.field_71441_e.field_73010_i.isEmpty()) {
/*  98 */       return null;
/*     */     }
/* 100 */     EntityPlayer closestTarget = null;
/* 101 */     for (EntityPlayer target : mc.field_71441_e.field_73010_i) {
/* 102 */       if (target == mc.field_71439_g || 
/* 103 */         !target.func_70089_S()) {
/*     */         continue;
/*     */       }
/* 106 */       if (Banzem.friendManager.isFriend(target.func_70005_c_())) {
/*     */         continue;
/*     */       }
/* 109 */       if (target.func_110143_aJ() <= 0.0F) {
/*     */         continue;
/*     */       }
/* 112 */       if (mc.field_71439_g.func_70032_d((Entity)target) > ((Float)this.placerange.getValue()).floatValue()) {
/*     */         continue;
/*     */       }
/* 115 */       if (closestTarget != null && mc.field_71439_g.func_70032_d((Entity)target) > mc.field_71439_g.func_70032_d((Entity)closestTarget)) {
/*     */         continue;
/*     */       }
/* 118 */       closestTarget = target;
/*     */     } 
/*     */     
/* 121 */     return closestTarget;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\Flatten.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */