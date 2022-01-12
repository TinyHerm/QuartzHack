/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import com.mojang.realmsclient.gui.ChatFormatting;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import me.mohalk.banzem.util.oyveyutils.OyVeyentityUtil;
/*     */ import net.minecraft.block.BlockEnderChest;
/*     */ import net.minecraft.block.BlockObsidian;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ 
/*     */ public class AutoTrap extends Module {
/*  25 */   private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250))); public static boolean isPlacing = false;
/*  26 */   private final Setting<Integer> blocksPerPlace = register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
/*  27 */   private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
/*  28 */   private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
/*  29 */   private final Setting<Boolean> antiScaffold = register(new Setting("AntiScaffold", Boolean.valueOf(false)));
/*  30 */   private final Setting<Boolean> antiStep = register(new Setting("AntiStep", Boolean.valueOf(false)));
/*  31 */   private final Timer timer = new Timer();
/*  32 */   private final Map<BlockPos, Integer> retries = new HashMap<>();
/*  33 */   private final Timer retryTimer = new Timer();
/*     */   public EntityPlayer target;
/*     */   private boolean didPlace = false;
/*     */   private boolean switchedItem;
/*     */   private boolean isSneaking;
/*     */   private int lastHotbarSlot;
/*  39 */   private int placements = 0;
/*     */   private boolean smartRotate = false;
/*  41 */   private BlockPos startPos = null;
/*     */   
/*     */   public AutoTrap() {
/*  44 */     super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  49 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/*  52 */     this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.field_71439_g);
/*  53 */     this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*  54 */     this.retries.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  59 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/*  62 */     this.smartRotate = false;
/*  63 */     doTrap();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/*  68 */     if (this.target != null) {
/*  69 */       return this.target.func_70005_c_();
/*     */     }
/*  71 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  76 */     isPlacing = false;
/*  77 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/*     */   }
/*     */   
/*     */   private void doTrap() {
/*  81 */     if (check()) {
/*     */       return;
/*     */     }
/*  84 */     doStaticTrap();
/*  85 */     if (this.didPlace) {
/*  86 */       this.timer.reset();
/*     */     }
/*     */   }
/*     */   
/*     */   private void doStaticTrap() {
/*  91 */     List<Vec3d> placeTargets = OyVeyentityUtil.targets(this.target.func_174791_d(), ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), false, false, false, ((Boolean)this.raytrace.getValue()).booleanValue());
/*  92 */     placeList(placeTargets);
/*     */   }
/*     */   
/*     */   private void placeList(List<Vec3d> list) {
/*  96 */     list.sort((vec3d, vec3d2) -> Double.compare(mc.field_71439_g.func_70092_e(vec3d2.field_72450_a, vec3d2.field_72448_b, vec3d2.field_72449_c), mc.field_71439_g.func_70092_e(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c)));
/*  97 */     list.sort(Comparator.comparingDouble(vec3d -> vec3d.field_72448_b));
/*  98 */     for (Vec3d vec3d3 : list) {
/*  99 */       BlockPos position = new BlockPos(vec3d3);
/* 100 */       int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
/* 101 */       if (placeability == 1 && (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < 4)) {
/* 102 */         placeBlock(position);
/* 103 */         this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
/* 104 */         this.retryTimer.reset();
/*     */         continue;
/*     */       } 
/* 107 */       if (placeability != 3)
/* 108 */         continue;  placeBlock(position);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean check() {
/* 113 */     isPlacing = false;
/* 114 */     this.didPlace = false;
/* 115 */     this.placements = 0;
/* 116 */     int obbySlot2 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/* 117 */     if (obbySlot2 == -1) {
/* 118 */       toggle();
/*     */     }
/* 120 */     int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/* 121 */     if (isOff()) {
/* 122 */       return true;
/*     */     }
/* 124 */     if (!this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.field_71439_g))) {
/* 125 */       disable();
/* 126 */       return true;
/*     */     } 
/* 128 */     if (this.retryTimer.passedMs(2000L)) {
/* 129 */       this.retries.clear();
/* 130 */       this.retryTimer.reset();
/*     */     } 
/* 132 */     if (obbySlot == -1) {
/* 133 */       Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
/* 134 */       disable();
/* 135 */       return true;
/*     */     } 
/* 137 */     if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != obbySlot) {
/* 138 */       this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*     */     }
/* 140 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 141 */     this.target = getTarget(10.0D, true);
/* 142 */     return (this.target == null || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()));
/*     */   }
/*     */   
/*     */   private EntityPlayer getTarget(double range, boolean trapped) {
/* 146 */     EntityPlayer target = null;
/* 147 */     double distance = Math.pow(range, 2.0D) + 1.0D;
/* 148 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 149 */       if (EntityUtil.isntValid((Entity)player, range) || (trapped && OyVeyentityUtil.isTrapped(player, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), false, false, false)) || Banzem.speedManager.getPlayerSpeed(player) > 10.0D)
/*     */         continue; 
/* 151 */       if (target == null) {
/* 152 */         target = player;
/* 153 */         distance = mc.field_71439_g.func_70068_e((Entity)player);
/*     */         continue;
/*     */       } 
/* 156 */       if (mc.field_71439_g.func_70068_e((Entity)player) >= distance)
/* 157 */         continue;  target = player;
/* 158 */       distance = mc.field_71439_g.func_70068_e((Entity)player);
/*     */     } 
/* 160 */     return target;
/*     */   }
/*     */   
/*     */   private void placeBlock(BlockPos pos) {
/* 164 */     if (this.placements < ((Integer)this.blocksPerPlace.getValue()).intValue() && mc.field_71439_g.func_174818_b(pos) <= MathUtil.square(5.0D)) {
/* 165 */       isPlacing = true;
/* 166 */       int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/* 167 */       int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/* 168 */       int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
/* 169 */       if (obbySlot == -1 && eChestSot == -1) {
/* 170 */         toggle();
/*     */       }
/* 172 */       if (this.smartRotate) {
/* 173 */         mc.field_71439_g.field_71071_by.field_70461_c = (obbySlot == -1) ? eChestSot : obbySlot;
/* 174 */         mc.field_71442_b.func_78765_e();
/* 175 */         this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, true, this.isSneaking);
/* 176 */         mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
/* 177 */         mc.field_71442_b.func_78765_e();
/*     */       } else {
/* 179 */         mc.field_71439_g.field_71071_by.field_70461_c = (obbySlot == -1) ? eChestSot : obbySlot;
/* 180 */         mc.field_71442_b.func_78765_e();
/* 181 */         this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), true, this.isSneaking);
/* 182 */         mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
/* 183 */         mc.field_71442_b.func_78765_e();
/*     */       } 
/* 185 */       this.didPlace = true;
/* 186 */       this.placements++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\AutoTrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */