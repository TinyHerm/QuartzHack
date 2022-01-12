/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import com.mojang.realmsclient.gui.ChatFormatting;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import me.mohalk.banzem.util.oyveyutils.OyVeyentityUtil;
/*     */ import net.minecraft.block.BlockEnderChest;
/*     */ import net.minecraft.block.BlockObsidian;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ 
/*     */ public class Surround extends Module {
/*  24 */   private final Setting<Integer> blocksPerTick = register(new Setting("BlocksPerTick", Integer.valueOf(12), Integer.valueOf(1), Integer.valueOf(20))); public static boolean isPlacing = false;
/*  25 */   private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250)));
/*  26 */   private final Setting<Boolean> noGhost = register(new Setting("PacketPlace", Boolean.valueOf(false)));
/*  27 */   private final Setting<Boolean> center = register(new Setting("TPCenter", Boolean.valueOf(false)));
/*  28 */   private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
/*  29 */   private final Timer timer = new Timer();
/*  30 */   private final Timer retryTimer = new Timer();
/*  31 */   private final Set<Vec3d> extendingBlocks = new HashSet<>();
/*  32 */   private final Map<BlockPos, Integer> retries = new HashMap<>();
/*     */   private int isSafe;
/*     */   private BlockPos startPos;
/*     */   private boolean didPlace = false;
/*     */   private boolean switchedItem;
/*     */   private int lastHotbarSlot;
/*     */   private boolean isSneaking;
/*  39 */   private int placements = 0;
/*  40 */   private int extenders = 1;
/*  41 */   private int obbySlot = -1;
/*     */   private boolean offHand = false;
/*     */   
/*     */   public Surround() {
/*  45 */     super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  50 */     if (fullNullCheck()) {
/*  51 */       disable();
/*     */     }
/*  53 */     this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*  54 */     this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.field_71439_g);
/*  55 */     if (((Boolean)this.center.getValue()).booleanValue()) {
/*  56 */       Banzem.positionManager.setPositionPacket(this.startPos.func_177958_n() + 0.5D, this.startPos.func_177956_o(), this.startPos.func_177952_p() + 0.5D, true, true, true);
/*     */     }
/*  58 */     this.retries.clear();
/*  59 */     this.retryTimer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  64 */     doFeetPlace();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  69 */     if (nullCheck()) {
/*     */       return;
/*     */     }
/*  72 */     isPlacing = false;
/*  73 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/*  78 */     switch (this.isSafe) {
/*     */       case 0:
/*  80 */         return ChatFormatting.RED + "Unsafe";
/*     */       
/*     */       case 1:
/*  83 */         return ChatFormatting.YELLOW + "Safe";
/*     */     } 
/*     */     
/*  86 */     return ChatFormatting.GREEN + "Safe";
/*     */   }
/*     */   
/*     */   private void doFeetPlace() {
/*  90 */     if (check()) {
/*     */       return;
/*     */     }
/*  93 */     if (!OyVeyentityUtil.isSafe((Entity)mc.field_71439_g, 0, true)) {
/*  94 */       this.isSafe = 0;
/*  95 */       placeBlocks(mc.field_71439_g.func_174791_d(), OyVeyentityUtil.getUnsafeBlockArray((Entity)mc.field_71439_g, 0, true), true, false, false);
/*     */     }
/*  97 */     else if (!OyVeyentityUtil.isSafe((Entity)mc.field_71439_g, -1, false)) {
/*  98 */       this.isSafe = 1;
/*  99 */       placeBlocks(mc.field_71439_g.func_174791_d(), OyVeyentityUtil.getUnsafeBlockArray((Entity)mc.field_71439_g, -1, false), false, false, true);
/*     */     } else {
/*     */       
/* 102 */       this.isSafe = 3;
/* 103 */       if (Util.mc.field_71441_e.func_180495_p(EntityUtil.getRoundedBlockPos((Entity)Util.mc.field_71439_g)).func_177230_c().equals(Blocks.field_150477_bB) && Util.mc.field_71439_g.field_70163_u != EntityUtil.getRoundedBlockPos((Entity)Util.mc.field_71439_g).func_177956_o()) {
/* 104 */         placeBlocks(mc.field_71439_g.func_174791_d(), OyVeyentityUtil.getUnsafeBlockArray((Entity)mc.field_71439_g, 1, false), false, false, true);
/*     */       } else {
/* 106 */         this.isSafe = 4;
/*     */       } 
/*     */     } 
/* 109 */     processExtendingBlocks();
/* 110 */     if (this.didPlace) {
/* 111 */       this.timer.reset();
/*     */     }
/*     */   }
/*     */   
/*     */   private void processExtendingBlocks() {
/* 116 */     if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
/* 117 */       Vec3d[] array = new Vec3d[2];
/* 118 */       int i = 0;
/* 119 */       Iterator<Vec3d> iterator = this.extendingBlocks.iterator();
/* 120 */       while (iterator.hasNext()) {
/*     */         
/* 122 */         Vec3d vec3d = iterator.next();
/* 123 */         i++;
/*     */       } 
/* 125 */       int placementsBefore = this.placements;
/* 126 */       if (areClose(array) != null) {
/* 127 */         placeBlocks(areClose(array), OyVeyentityUtil.getUnsafeBlockArrayFromVec3d(areClose(array), 0, true), true, false, true);
/*     */       }
/* 129 */       if (placementsBefore < this.placements) {
/* 130 */         this.extendingBlocks.clear();
/*     */       }
/* 132 */     } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
/* 133 */       this.extendingBlocks.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   private Vec3d areClose(Vec3d[] vec3ds) {
/* 138 */     int matches = 0;
/* 139 */     for (Vec3d vec3d : vec3ds) {
/* 140 */       for (Vec3d pos : OyVeyentityUtil.getUnsafeBlockArray((Entity)mc.field_71439_g, 0, true)) {
/* 141 */         if (vec3d.equals(pos))
/* 142 */           matches++; 
/*     */       } 
/*     */     } 
/* 145 */     if (matches == 2) {
/* 146 */       return mc.field_71439_g.func_174791_d().func_178787_e(vec3ds[0].func_178787_e(vec3ds[1]));
/*     */     }
/* 148 */     return null;
/*     */   }
/*     */   
/*     */   private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
/* 152 */     boolean gotHelp = true;
/*     */     
/* 154 */     for (Vec3d vec3d : vec3ds) {
/* 155 */       gotHelp = true;
/* 156 */       BlockPos position = (new BlockPos(pos)).func_177963_a(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
/* 157 */       switch (BlockUtil.isPositionPlaceable(position, false)) {
/*     */         case 1:
/* 159 */           if (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < 4) {
/* 160 */             placeBlock(position);
/* 161 */             this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
/* 162 */             this.retryTimer.reset();
/*     */             break;
/*     */           } 
/* 165 */           if (Banzem.speedManager.getSpeedKpH() != 0.0D || isExtending || this.extenders >= 1)
/* 166 */             break;  placeBlocks(mc.field_71439_g.func_174791_d().func_178787_e(vec3d), OyVeyentityUtil.getUnsafeBlockArrayFromVec3d(mc.field_71439_g.func_174791_d().func_178787_e(vec3d), 0, true), hasHelpingBlocks, false, true);
/* 167 */           this.extendingBlocks.add(vec3d);
/* 168 */           this.extenders++;
/*     */           break;
/*     */         
/*     */         case 2:
/* 172 */           if (!hasHelpingBlocks)
/* 173 */             break;  gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
/*     */         
/*     */         case 3:
/* 176 */           if (gotHelp) {
/* 177 */             placeBlock(position);
/*     */           }
/* 179 */           if (!isHelping)
/* 180 */             break;  return true;
/*     */       } 
/*     */     
/*     */     } 
/* 184 */     return false;
/*     */   }
/*     */   
/*     */   private boolean check() {
/* 188 */     if (nullCheck()) {
/* 189 */       return true;
/*     */     }
/* 191 */     int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/* 192 */     int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
/* 193 */     if (obbySlot == -1 && eChestSot == -1) {
/* 194 */       toggle();
/*     */     }
/* 196 */     this.offHand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
/* 197 */     isPlacing = false;
/* 198 */     this.didPlace = false;
/* 199 */     this.extenders = 1;
/* 200 */     this.placements = 0;
/* 201 */     this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/* 202 */     int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
/* 203 */     if (isOff()) {
/* 204 */       return true;
/*     */     }
/* 206 */     if (this.retryTimer.passedMs(2500L)) {
/* 207 */       this.retries.clear();
/* 208 */       this.retryTimer.reset();
/*     */     } 
/* 210 */     if (this.obbySlot == -1 && !this.offHand && echestSlot == -1) {
/* 211 */       Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
/* 212 */       disable();
/* 213 */       return true;
/*     */     } 
/* 215 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 216 */     if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != this.obbySlot && mc.field_71439_g.field_71071_by.field_70461_c != echestSlot) {
/* 217 */       this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*     */     }
/* 219 */     if (!this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.field_71439_g))) {
/* 220 */       disable();
/* 221 */       return true;
/*     */     } 
/* 223 */     return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
/*     */   }
/*     */   
/*     */   private void placeBlock(BlockPos pos) {
/* 227 */     if (this.placements < ((Integer)this.blocksPerTick.getValue()).intValue()) {
/* 228 */       int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/* 229 */       int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/* 230 */       int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
/* 231 */       if (obbySlot == -1 && eChestSot == -1) {
/* 232 */         toggle();
/*     */       }
/* 234 */       isPlacing = true;
/* 235 */       mc.field_71439_g.field_71071_by.field_70461_c = (obbySlot == -1) ? eChestSot : obbySlot;
/* 236 */       mc.field_71442_b.func_78765_e();
/* 237 */       this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.noGhost.getValue()).booleanValue(), this.isSneaking);
/* 238 */       mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
/* 239 */       mc.field_71442_b.func_78765_e();
/* 240 */       this.didPlace = true;
/* 241 */       this.placements++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\Surround.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */