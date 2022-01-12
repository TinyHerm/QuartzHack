/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.client.entity.EntityPlayerSP;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.MobEffects;
/*     */ import net.minecraft.inventory.ClickType;
/*     */ import net.minecraft.inventory.Container;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemBlock;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketAnimation;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketHeldItemChange;
/*     */ import net.minecraft.network.play.client.CPacketPlayer;
/*     */ import net.minecraft.util.EnumActionResult;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Scaffold
/*     */   extends Module
/*     */ {
/*     */   private final Setting<Mode> mode;
/*     */   private final Setting<Boolean> swing;
/*     */   private final Setting<Boolean> bSwitch;
/*     */   private final Setting<Boolean> center;
/*     */   private final Setting<Boolean> keepY;
/*     */   private final Setting<Boolean> replenishBlocks;
/*     */   private final Setting<Boolean> down;
/*     */   private final Setting<Float> expand;
/*     */   
/*     */   public Scaffold() {
/*  57 */     super("Scaffold", "Places Blocks underneath you.", Module.Category.MOVEMENT, true, false, false);
/*  58 */     this.mode = register(new Setting("Mode", Mode.Legit));
/*  59 */     this.rotation = register(new Setting("Rotate", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Fast)));
/*  60 */     this.swing = register(new Setting("Swing Arm", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Legit)));
/*  61 */     this.bSwitch = register(new Setting("Switch", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Legit)));
/*  62 */     this.center = register(new Setting("Center", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
/*  63 */     this.keepY = register(new Setting("KeepYLevel", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
/*  64 */     this.replenishBlocks = register(new Setting("ReplenishBlocks", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Legit)));
/*  65 */     this.down = register(new Setting("Down", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
/*  66 */     this.expand = register(new Setting("Expand", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.mode.getValue() == Mode.Legit)));
/*  67 */     this.invalid = Arrays.asList(new Block[] { Blocks.field_150381_bn, Blocks.field_150460_al, Blocks.field_150404_cg, Blocks.field_150462_ai, Blocks.field_150447_bR, (Block)Blocks.field_150486_ae, Blocks.field_150367_z, Blocks.field_150350_a, (Block)Blocks.field_150355_j, (Block)Blocks.field_150353_l, (Block)Blocks.field_150358_i, (Block)Blocks.field_150356_k, Blocks.field_150431_aC, Blocks.field_150478_aa, Blocks.field_150467_bQ, Blocks.field_150421_aI, Blocks.field_150430_aB, Blocks.field_150471_bO, Blocks.field_150442_at, Blocks.field_150323_B, Blocks.field_150456_au, Blocks.field_150445_bS, Blocks.field_150452_aw, Blocks.field_150443_bT, (Block)Blocks.field_150337_Q, (Block)Blocks.field_150338_P, (Block)Blocks.field_150327_N, (Block)Blocks.field_150328_O, Blocks.field_150467_bQ, (Block)Blocks.field_150434_aF, Blocks.field_150468_ap, Blocks.field_150477_bB });
/*  68 */     this.timerMotion = new Timer();
/*  69 */     this.itemTimer = new Timer();
/*  70 */     this.timer = new Timer();
/*     */   }
/*     */   private final List<Block> invalid; private final Timer timerMotion; private final Timer itemTimer; private final Timer timer; public Setting<Boolean> rotation; private int lastY; private BlockPos pos; private boolean teleported;
/*     */   
/*     */   public static void swap(int slot, int hotbarNum) {
/*  75 */     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*  76 */     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, hotbarNum, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*  77 */     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*  78 */     mc.field_71442_b.func_78765_e();
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getItemSlot(Container container, Item item) {
/*  83 */     int slot = 0;
/*  84 */     for (int i = 9; i < 45; i++) {
/*  85 */       if (container.func_75139_a(i).func_75216_d()) {
/*  86 */         ItemStack is = container.func_75139_a(i).func_75211_c();
/*  87 */         if (is.func_77973_b() == item) {
/*  88 */           slot = i;
/*     */         }
/*     */       } 
/*     */     } 
/*  92 */     return slot;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isMoving(EntityLivingBase entity) {
/*  97 */     return (entity.field_191988_bg != 0.0F || entity.field_70702_br != 0.0F);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 103 */     this.timer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent event) {
/* 109 */     if (this.mode.getValue() == Mode.Fast) {
/* 110 */       if (isOff() || Feature.fullNullCheck() || event.getStage() == 0) {
/*     */         return;
/*     */       }
/* 113 */       if (!mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 114 */         this.timer.reset();
/*     */       }
/*     */       BlockPos playerBlock;
/* 117 */       if (BlockUtil.isScaffoldPos((playerBlock = EntityUtil.getPlayerPosWithEntity()).func_177982_a(0, -1, 0))) {
/* 118 */         if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -2, 0))) {
/* 119 */           place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.UP);
/* 120 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
/* 121 */           place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.EAST);
/* 122 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 0))) {
/* 123 */           place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.WEST);
/* 124 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, -1))) {
/* 125 */           place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.SOUTH);
/* 126 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
/* 127 */           place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.NORTH);
/* 128 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
/* 129 */           if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
/* 130 */             place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.NORTH);
/*     */           }
/* 132 */           place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.EAST);
/* 133 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 1))) {
/* 134 */           if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
/* 135 */             place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.WEST);
/*     */           }
/* 137 */           place(playerBlock.func_177982_a(-1, -1, 1), EnumFacing.SOUTH);
/* 138 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
/* 139 */           if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
/* 140 */             place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.SOUTH);
/*     */           }
/* 142 */           place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.WEST);
/* 143 */         } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
/* 144 */           if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
/* 145 */             place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.EAST);
/*     */           }
/* 147 */           place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.NORTH);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/* 156 */     if (this.mode.getValue() == Mode.Legit) {
/* 157 */       if (((Boolean)this.replenishBlocks.getValue()).booleanValue() && !(mc.field_71439_g.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemBlock) && getBlockCountHotbar() <= 0) {
/* 158 */         for (int i = 9; i < 45; i++) {
/* 159 */           if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
/* 160 */             ItemStack is = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
/* 161 */             if (is.func_77973_b() instanceof ItemBlock && !this.invalid.contains(Block.func_149634_a(is.func_77973_b())) && i < 36) {
/* 162 */               swap(getItemSlot(mc.field_71439_g.field_71069_bz, is.func_77973_b()), 44);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/* 167 */       if (((Boolean)this.keepY.getValue()).booleanValue()) {
/* 168 */         if ((!isMoving((EntityLivingBase)mc.field_71439_g) && mc.field_71474_y.field_74314_A.func_151470_d()) || mc.field_71439_g.field_70124_G || mc.field_71439_g.field_70122_E) {
/* 169 */           this.lastY = MathHelper.func_76128_c(mc.field_71439_g.field_70163_u);
/*     */         }
/*     */       } else {
/* 172 */         this.lastY = MathHelper.func_76128_c(mc.field_71439_g.field_70163_u);
/*     */       } 
/* 174 */       BlockData blockData = null;
/* 175 */       double x = mc.field_71439_g.field_70165_t;
/* 176 */       double z = mc.field_71439_g.field_70161_v;
/* 177 */       double y = ((Boolean)this.keepY.getValue()).booleanValue() ? this.lastY : mc.field_71439_g.field_70163_u;
/* 178 */       double forward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 179 */       double strafe = mc.field_71439_g.field_71158_b.field_78902_a;
/* 180 */       float yaw = mc.field_71439_g.field_70177_z;
/* 181 */       if (!mc.field_71439_g.field_70123_F) {
/* 182 */         double[] coords = getExpandCoords(x, z, forward, strafe, yaw);
/* 183 */         x = coords[0];
/* 184 */         z = coords[1];
/*     */       } 
/* 186 */       if (canPlace(mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - ((mc.field_71474_y.field_74311_E.func_151470_d() && ((Boolean)this.down.getValue()).booleanValue()) ? 2 : true), mc.field_71439_g.field_70161_v)).func_177230_c())) {
/* 187 */         x = mc.field_71439_g.field_70165_t;
/* 188 */         z = mc.field_71439_g.field_70161_v;
/*     */       } 
/* 190 */       BlockPos blockBelow = new BlockPos(x, y - 1.0D, z);
/* 191 */       if (mc.field_71474_y.field_74311_E.func_151470_d() && ((Boolean)this.down.getValue()).booleanValue()) {
/* 192 */         blockBelow = new BlockPos(x, y - 2.0D, z);
/*     */       }
/* 194 */       this.pos = blockBelow;
/* 195 */       if (mc.field_71441_e.func_180495_p(blockBelow).func_177230_c() == Blocks.field_150350_a) {
/* 196 */         blockData = getBlockData2(blockBelow);
/*     */       }
/* 198 */       if (blockData != null) {
/* 199 */         if (getBlockCountHotbar() <= 0 || (!((Boolean)this.bSwitch.getValue()).booleanValue() && !(mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock))) {
/*     */           return;
/*     */         }
/* 202 */         int heldItem = mc.field_71439_g.field_71071_by.field_70461_c;
/* 203 */         if (((Boolean)this.bSwitch.getValue()).booleanValue()) {
/* 204 */           for (int j = 0; j < 9; j++) {
/* 205 */             mc.field_71439_g.field_71071_by.func_70301_a(j);
/* 206 */             if (mc.field_71439_g.field_71071_by.func_70301_a(j).func_190916_E() != 0 && mc.field_71439_g.field_71071_by.func_70301_a(j).func_77973_b() instanceof ItemBlock && !this.invalid.contains(((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(j).func_77973_b()).func_179223_d())) {
/* 207 */               mc.field_71439_g.field_71071_by.field_70461_c = j;
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         }
/* 212 */         if (this.mode.getValue() == Mode.Legit) {
/* 213 */           if (mc.field_71474_y.field_74314_A.func_151470_d() && mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F && !mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
/* 214 */             if (!this.teleported && ((Boolean)this.center.getValue()).booleanValue()) {
/* 215 */               this.teleported = true;
/* 216 */               BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
/* 217 */               mc.field_71439_g.func_70107_b(pos.func_177958_n() + 0.5D, pos.func_177956_o(), pos.func_177952_p() + 0.5D);
/*     */             } 
/* 219 */             if (((Boolean)this.center.getValue()).booleanValue() && !this.teleported) {
/*     */               return;
/*     */             }
/* 222 */             mc.field_71439_g.field_70181_x = 0.41999998688697815D;
/* 223 */             mc.field_71439_g.field_70179_y = 0.0D;
/* 224 */             mc.field_71439_g.field_70159_w = 0.0D;
/*     */             
/* 226 */             mc.field_71439_g.field_70181_x = -0.28D;
/*     */           } else {
/*     */             
/* 229 */             this.timerMotion.reset();
/* 230 */             if (this.teleported && ((Boolean)this.center.getValue()).booleanValue()) {
/* 231 */               this.teleported = false;
/*     */             }
/*     */           } 
/*     */         }
/* 235 */         if (mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, blockData.position, blockData.face, new Vec3d(blockData.position.func_177958_n() + Math.random(), blockData.position.func_177956_o() + Math.random(), blockData.position.func_177952_p() + Math.random()), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
/* 236 */           if (((Boolean)this.swing.getValue()).booleanValue()) {
/* 237 */             mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/*     */           } else {
/* 239 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
/*     */           } 
/*     */         }
/* 242 */         mc.field_71439_g.field_71071_by.field_70461_c = heldItem;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW) {
/* 249 */     BlockPos underPos = new BlockPos(x, mc.field_71439_g.field_70163_u - ((mc.field_71474_y.field_74311_E.func_151470_d() && ((Boolean)this.down.getValue()).booleanValue()) ? 2 : true), z);
/* 250 */     Block underBlock = mc.field_71441_e.func_180495_p(underPos).func_177230_c();
/* 251 */     double xCalc = -999.0D;
/* 252 */     double zCalc = -999.0D;
/* 253 */     double dist = 0.0D;
/* 254 */     double expandDist = (((Float)this.expand.getValue()).floatValue() * 2.0F);
/* 255 */     while (!canPlace(underBlock)) {
/* 256 */       xCalc = x;
/* 257 */       zCalc = z;
/* 258 */       dist++;
/* 259 */       if (dist > expandDist) {
/* 260 */         dist = expandDist;
/*     */       }
/* 262 */       xCalc += (forward * 0.45D * Math.cos(Math.toRadians((YAW + 90.0F))) + strafe * 0.45D * Math.sin(Math.toRadians((YAW + 90.0F)))) * dist;
/* 263 */       zCalc += (forward * 0.45D * Math.sin(Math.toRadians((YAW + 90.0F))) - strafe * 0.45D * Math.cos(Math.toRadians((YAW + 90.0F)))) * dist;
/* 264 */       if (dist == expandDist) {
/*     */         break;
/*     */       }
/* 267 */       underPos = new BlockPos(xCalc, mc.field_71439_g.field_70163_u - ((mc.field_71474_y.field_74311_E.func_151470_d() && ((Boolean)this.down.getValue()).booleanValue()) ? 2 : true), zCalc);
/* 268 */       underBlock = mc.field_71441_e.func_180495_p(underPos).func_177230_c();
/*     */     } 
/* 270 */     return new double[] { xCalc, zCalc };
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canPlace(Block block) {
/* 275 */     return ((block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid) && mc.field_71441_e != null && mc.field_71439_g != null && this.pos != null && mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(this.pos)).isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   private int getBlockCountHotbar() {
/* 280 */     int blockCount = 0;
/* 281 */     for (int i = 36; i < 45; i++) {
/* 282 */       if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
/* 283 */         ItemStack is = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
/* 284 */         Item item = is.func_77973_b();
/* 285 */         if (is.func_77973_b() instanceof ItemBlock && !this.invalid.contains(((ItemBlock)item).func_179223_d())) {
/* 286 */           blockCount += is.func_190916_E();
/*     */         }
/*     */       } 
/*     */     } 
/* 290 */     return blockCount;
/*     */   }
/*     */ 
/*     */   
/*     */   private BlockData getBlockData2(BlockPos pos) {
/* 295 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 296 */       return new BlockData(pos.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 298 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 299 */       return new BlockData(pos.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 301 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 302 */       return new BlockData(pos.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 304 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 305 */       return new BlockData(pos.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 307 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 308 */       return new BlockData(pos.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 310 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 311 */       return new BlockData(pos.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 313 */     BlockPos pos2 = pos.func_177982_a(-1, 0, 0);
/* 314 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 315 */       return new BlockData(pos2.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 317 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 318 */       return new BlockData(pos2.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 320 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 321 */       return new BlockData(pos2.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 323 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 324 */       return new BlockData(pos2.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 326 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 327 */       return new BlockData(pos2.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 329 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 330 */       return new BlockData(pos2.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 332 */     BlockPos pos3 = pos.func_177982_a(1, 0, 0);
/* 333 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 334 */       return new BlockData(pos3.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 336 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 337 */       return new BlockData(pos3.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 339 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 340 */       return new BlockData(pos3.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 342 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 343 */       return new BlockData(pos3.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 345 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 346 */       return new BlockData(pos3.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 348 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 349 */       return new BlockData(pos3.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 351 */     BlockPos pos4 = pos.func_177982_a(0, 0, 1);
/* 352 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 353 */       return new BlockData(pos4.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 355 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 356 */       return new BlockData(pos4.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 358 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 359 */       return new BlockData(pos4.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 361 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 362 */       return new BlockData(pos4.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 364 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 365 */       return new BlockData(pos4.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 367 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 368 */       return new BlockData(pos4.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 370 */     BlockPos pos5 = pos.func_177982_a(0, 0, -1);
/* 371 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 372 */       return new BlockData(pos5.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 374 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 375 */       return new BlockData(pos5.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 377 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 378 */       return new BlockData(pos5.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 380 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 381 */       return new BlockData(pos5.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 383 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 384 */       return new BlockData(pos5.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 386 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 387 */       return new BlockData(pos5.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 389 */     pos.func_177982_a(-2, 0, 0);
/* 390 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 391 */       return new BlockData(pos2.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 393 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 394 */       return new BlockData(pos2.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 396 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 397 */       return new BlockData(pos2.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 399 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 400 */       return new BlockData(pos2.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 402 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 403 */       return new BlockData(pos2.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 405 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 406 */       return new BlockData(pos2.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 408 */     pos.func_177982_a(2, 0, 0);
/* 409 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 410 */       return new BlockData(pos3.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 412 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 413 */       return new BlockData(pos3.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 415 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 416 */       return new BlockData(pos3.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 418 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 419 */       return new BlockData(pos3.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 421 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 422 */       return new BlockData(pos3.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 424 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos3.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 425 */       return new BlockData(pos3.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 427 */     pos.func_177982_a(0, 0, 2);
/* 428 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 429 */       return new BlockData(pos4.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 431 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 432 */       return new BlockData(pos4.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 434 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 435 */       return new BlockData(pos4.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 437 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 438 */       return new BlockData(pos4.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 440 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 441 */       return new BlockData(pos4.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 443 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos4.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 444 */       return new BlockData(pos4.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 446 */     pos.func_177982_a(0, 0, -2);
/* 447 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 448 */       return new BlockData(pos5.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 450 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 451 */       return new BlockData(pos5.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 453 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 454 */       return new BlockData(pos5.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 456 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 457 */       return new BlockData(pos5.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 459 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 460 */       return new BlockData(pos5.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 462 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos5.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 463 */       return new BlockData(pos5.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 465 */     BlockPos pos10 = pos.func_177982_a(0, -1, 0);
/* 466 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos10.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 467 */       return new BlockData(pos10.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 469 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos10.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 470 */       return new BlockData(pos10.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 472 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos10.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 473 */       return new BlockData(pos10.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 475 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos10.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 476 */       return new BlockData(pos10.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 478 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos10.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 479 */       return new BlockData(pos10.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 481 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos10.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 482 */       return new BlockData(pos10.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 484 */     BlockPos pos11 = pos10.func_177982_a(1, 0, 0);
/* 485 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos11.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 486 */       return new BlockData(pos11.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 488 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos11.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 489 */       return new BlockData(pos11.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 491 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos11.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 492 */       return new BlockData(pos11.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 494 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos11.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 495 */       return new BlockData(pos11.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 497 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos11.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 498 */       return new BlockData(pos11.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 500 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos11.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 501 */       return new BlockData(pos11.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 503 */     BlockPos pos12 = pos10.func_177982_a(-1, 0, 0);
/* 504 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos12.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 505 */       return new BlockData(pos12.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 507 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos12.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 508 */       return new BlockData(pos12.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 510 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos12.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 511 */       return new BlockData(pos12.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 513 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos12.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 514 */       return new BlockData(pos12.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 516 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos12.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 517 */       return new BlockData(pos12.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 519 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos12.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 520 */       return new BlockData(pos12.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 522 */     BlockPos pos13 = pos10.func_177982_a(0, 0, 1);
/* 523 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos13.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 524 */       return new BlockData(pos13.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 526 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos13.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 527 */       return new BlockData(pos13.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 529 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos13.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 530 */       return new BlockData(pos13.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 532 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos13.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 533 */       return new BlockData(pos13.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 535 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos13.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 536 */       return new BlockData(pos13.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 538 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos13.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 539 */       return new BlockData(pos13.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 541 */     BlockPos pos14 = pos10.func_177982_a(0, 0, -1);
/* 542 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos14.func_177982_a(0, -1, 0)).func_177230_c())) {
/* 543 */       return new BlockData(pos14.func_177982_a(0, -1, 0), EnumFacing.UP);
/*     */     }
/* 545 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos14.func_177982_a(0, 1, 0)).func_177230_c())) {
/* 546 */       return new BlockData(pos14.func_177982_a(0, 1, 0), EnumFacing.DOWN);
/*     */     }
/* 548 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos14.func_177982_a(-1, 0, 0)).func_177230_c())) {
/* 549 */       return new BlockData(pos14.func_177982_a(-1, 0, 0), EnumFacing.EAST);
/*     */     }
/* 551 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos14.func_177982_a(1, 0, 0)).func_177230_c())) {
/* 552 */       return new BlockData(pos14.func_177982_a(1, 0, 0), EnumFacing.WEST);
/*     */     }
/* 554 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos14.func_177982_a(0, 0, 1)).func_177230_c())) {
/* 555 */       return new BlockData(pos14.func_177982_a(0, 0, 1), EnumFacing.NORTH);
/*     */     }
/* 557 */     if (!this.invalid.contains(mc.field_71441_e.func_180495_p(pos14.func_177982_a(0, 0, -1)).func_177230_c())) {
/* 558 */       return new BlockData(pos14.func_177982_a(0, 0, -1), EnumFacing.SOUTH);
/*     */     }
/* 560 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void place(BlockPos posI, EnumFacing face) {
/* 565 */     BlockPos pos = posI;
/* 566 */     if (face == EnumFacing.UP) {
/* 567 */       pos = pos.func_177982_a(0, -1, 0);
/* 568 */     } else if (face == EnumFacing.NORTH) {
/* 569 */       pos = pos.func_177982_a(0, 0, 1);
/* 570 */     } else if (face == EnumFacing.SOUTH) {
/* 571 */       pos = pos.func_177982_a(0, 0, -1);
/* 572 */     } else if (face == EnumFacing.EAST) {
/* 573 */       pos = pos.func_177982_a(-1, 0, 0);
/* 574 */     } else if (face == EnumFacing.WEST) {
/* 575 */       pos = pos.func_177982_a(1, 0, 0);
/*     */     } 
/* 577 */     int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/* 578 */     int newSlot = -1;
/* 579 */     for (int i = 0; i < 9; i++) {
/* 580 */       ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
/* 581 */       if (!InventoryUtil.isNull(stack) && stack.func_77973_b() instanceof ItemBlock && Block.func_149634_a(stack.func_77973_b()).func_176223_P().func_185913_b()) {
/* 582 */         newSlot = i;
/*     */         break;
/*     */       } 
/*     */     } 
/* 586 */     if (newSlot == -1) {
/*     */       return;
/*     */     }
/* 589 */     boolean crouched = false;
/* 590 */     if (!mc.field_71439_g.func_70093_af() && BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(pos).func_177230_c())) {
/* 591 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/* 592 */       crouched = true;
/*     */     } 
/* 594 */     if (!(mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock)) {
/* 595 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(newSlot));
/* 596 */       mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
/* 597 */       mc.field_71442_b.func_78765_e();
/*     */     } 
/* 599 */     if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 600 */       EntityPlayerSP player = mc.field_71439_g;
/* 601 */       player.field_70159_w *= 0.3D;
/* 602 */       EntityPlayerSP player2 = mc.field_71439_g;
/* 603 */       player2.field_70179_y *= 0.3D;
/* 604 */       mc.field_71439_g.func_70664_aZ();
/*     */       
/* 606 */       mc.field_71439_g.field_70181_x = -0.28D;
/* 607 */       this.timer.reset();
/*     */     } 
/*     */     
/* 610 */     if (((Boolean)this.rotation.getValue()).booleanValue()) {
/* 611 */       float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((pos.func_177958_n() + 0.5F), (pos.func_177956_o() - 0.5F), (pos.func_177952_p() + 0.5F)));
/* 612 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(angle[0], MathHelper.func_180184_b((int)angle[1], 360), mc.field_71439_g.field_70122_E));
/*     */     } 
/* 614 */     mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
/* 615 */     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 616 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
/* 617 */     mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
/* 618 */     mc.field_71442_b.func_78765_e();
/* 619 */     if (crouched) {
/* 620 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
/*     */     }
/*     */   }
/*     */   
/*     */   public enum Mode
/*     */   {
/* 626 */     Legit,
/* 627 */     Fast;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class BlockData
/*     */   {
/*     */     public BlockPos position;
/*     */     public EnumFacing face;
/*     */     
/*     */     public BlockData(BlockPos position, EnumFacing face) {
/* 637 */       this.position = position;
/* 638 */       this.face = face;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\Scaffold.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */