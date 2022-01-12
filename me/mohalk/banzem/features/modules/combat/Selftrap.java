/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.player.Freecam;
/*     */ import me.mohalk.banzem.features.setting.Bind;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.block.BlockObsidian;
/*     */ import net.minecraft.block.BlockWeb;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import net.minecraftforge.fml.common.gameevent.InputEvent;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ public class Selftrap extends Module {
/*  28 */   private final Setting<Boolean> smart = register(new Setting("Smart", Boolean.valueOf(false)));
/*  29 */   private final Setting<Double> smartRange = register(new Setting("SmartRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D)));
/*  30 */   private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
/*  31 */   private final Setting<Integer> blocksPerTick = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
/*  32 */   private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
/*  33 */   private final Setting<Boolean> disable = register(new Setting("Disable", Boolean.valueOf(true)));
/*  34 */   private final Setting<Integer> disableTime = register(new Setting("Ms/Disable", Integer.valueOf(200), Integer.valueOf(1), Integer.valueOf(250)));
/*  35 */   private final Setting<Boolean> offhand = register(new Setting("OffHand", Boolean.valueOf(true)));
/*  36 */   private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
/*  37 */   private final Setting<Boolean> onlySafe = register(new Setting("OnlySafe", Boolean.valueOf(true), v -> ((Boolean)this.offhand.getValue()).booleanValue()));
/*  38 */   private final Setting<Boolean> highWeb = register(new Setting("HighWeb", Boolean.valueOf(false)));
/*  39 */   private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false)));
/*  40 */   private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
/*  41 */   private final Timer offTimer = new Timer();
/*  42 */   private final Timer timer = new Timer();
/*  43 */   private final Map<BlockPos, Integer> retries = new HashMap<>();
/*  44 */   private final Timer retryTimer = new Timer();
/*  45 */   public Setting<Mode> mode = register(new Setting("Mode", Mode.OBSIDIAN));
/*  46 */   public Setting<PlaceMode> placeMode = register(new Setting("PlaceMode", PlaceMode.NORMAL, v -> (this.mode.getValue() == Mode.OBSIDIAN)));
/*  47 */   public Setting<Bind> obbyBind = register(new Setting("Obsidian", new Bind(-1)));
/*  48 */   public Setting<Bind> webBind = register(new Setting("Webs", new Bind(-1)));
/*  49 */   public Mode currentMode = Mode.OBSIDIAN;
/*     */   private boolean accessedViaBind = false;
/*  51 */   private int blocksThisTick = 0;
/*  52 */   private Offhand.Mode offhandMode = Offhand.Mode.CRYSTALS;
/*  53 */   private Offhand.Mode2 offhandMode2 = Offhand.Mode2.CRYSTALS;
/*     */   private boolean isSneaking;
/*     */   private boolean hasOffhand = false;
/*     */   private boolean placeHighWeb = false;
/*  57 */   private int lastHotbarSlot = -1;
/*     */   private boolean switchedItem = false;
/*     */   
/*     */   public Selftrap() {
/*  61 */     super("Selftrap", "Lure your enemies in!", Module.Category.COMBAT, true, false, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  66 */     if (fullNullCheck()) {
/*  67 */       disable();
/*     */     }
/*  69 */     this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*  70 */     if (!this.accessedViaBind) {
/*  71 */       this.currentMode = (Mode)this.mode.getValue();
/*     */     }
/*  73 */     Offhand module = (Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class);
/*  74 */     this.offhandMode = module.mode;
/*  75 */     this.offhandMode2 = module.currentMode;
/*  76 */     if (((Boolean)this.offhand.getValue()).booleanValue() && (EntityUtil.isSafe((Entity)mc.field_71439_g) || !((Boolean)this.onlySafe.getValue()).booleanValue())) {
/*  77 */       if (module.type.getValue() == Offhand.Type.OLD) {
/*  78 */         if (this.currentMode == Mode.WEBS) {
/*  79 */           module.setMode(Offhand.Mode2.WEBS);
/*     */         } else {
/*  81 */           module.setMode(Offhand.Mode2.OBSIDIAN);
/*     */         } 
/*  83 */       } else if (this.currentMode == Mode.WEBS) {
/*  84 */         module.setSwapToTotem(false);
/*  85 */         module.setMode(Offhand.Mode.WEBS);
/*     */       } else {
/*  87 */         module.setSwapToTotem(false);
/*  88 */         module.setMode(Offhand.Mode.OBSIDIAN);
/*     */       } 
/*     */     }
/*  91 */     Banzem.holeManager.update();
/*  92 */     this.offTimer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  97 */     if (isOn() && (((Integer)this.blocksPerTick.getValue()).intValue() != 1 || !((Boolean)this.rotate.getValue()).booleanValue())) {
/*  98 */       doHoleFill();
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/* 104 */     if (isOn() && event.getStage() == 0 && ((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue()) {
/* 105 */       doHoleFill();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 111 */     if (((Boolean)this.offhand.getValue()).booleanValue()) {
/* 112 */       ((Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode);
/* 113 */       ((Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode2);
/*     */     } 
/* 115 */     switchItem(true);
/* 116 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 117 */     this.retries.clear();
/* 118 */     this.accessedViaBind = false;
/* 119 */     this.hasOffhand = false;
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
/*     */   public void onKeyInput(InputEvent.KeyInputEvent event) {
/* 124 */     if (Keyboard.getEventKeyState()) {
/* 125 */       if (((Bind)this.obbyBind.getValue()).getKey() == Keyboard.getEventKey()) {
/* 126 */         this.accessedViaBind = true;
/* 127 */         this.currentMode = Mode.OBSIDIAN;
/* 128 */         toggle();
/*     */       } 
/* 130 */       if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
/* 131 */         this.accessedViaBind = true;
/* 132 */         this.currentMode = Mode.WEBS;
/* 133 */         toggle();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doHoleFill() {
/* 139 */     if (check()) {
/*     */       return;
/*     */     }
/* 142 */     if (this.placeHighWeb) {
/* 143 */       BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v);
/* 144 */       placeBlock(pos);
/* 145 */       this.placeHighWeb = false;
/*     */     } 
/* 147 */     for (BlockPos position : getPositions()) {
/* 148 */       if (((Boolean)this.smart.getValue()).booleanValue() && !isPlayerInRange())
/* 149 */         continue;  int placeability = BlockUtil.isPositionPlaceable(position, false);
/* 150 */       if (placeability == 1) {
/* 151 */         switch (this.currentMode) {
/*     */           case WEBS:
/* 153 */             placeBlock(position);
/*     */             break;
/*     */           
/*     */           case OBSIDIAN:
/* 157 */             if (this.switchMode.getValue() != InventoryUtil.Switch.SILENT || (this.retries.get(position) != null && ((Integer)this.retries.get(position)).intValue() >= 4))
/*     */               break; 
/* 159 */             placeBlock(position);
/* 160 */             this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
/*     */             break;
/*     */         } 
/*     */       }
/* 164 */       if (placeability != 3)
/* 165 */         continue;  placeBlock(position);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isPlayerInRange() {
/* 170 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 171 */       if (EntityUtil.isntValid((Entity)player, ((Double)this.smartRange.getValue()).doubleValue()))
/* 172 */         continue;  return true;
/*     */     } 
/* 174 */     return false;
/*     */   }
/*     */   private List<BlockPos> getPositions() {
/*     */     int placeability;
/* 178 */     ArrayList<BlockPos> positions = new ArrayList<>();
/*     */     
/* 180 */     switch (this.currentMode) {
/*     */       case WEBS:
/* 182 */         positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v));
/* 183 */         if (!((Boolean)this.highWeb.getValue()).booleanValue())
/* 184 */           break;  positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v));
/*     */         break;
/*     */       
/*     */       case OBSIDIAN:
/* 188 */         if (this.placeMode.getValue() == PlaceMode.NORMAL) {
/* 189 */           positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v));
/* 190 */           int i = BlockUtil.isPositionPlaceable(positions.get(0), false);
/* 191 */           switch (i) {
/*     */             case 0:
/* 193 */               return new ArrayList<>();
/*     */             
/*     */             case 3:
/* 196 */               return positions;
/*     */             
/*     */             case 1:
/* 199 */               if (BlockUtil.isPositionPlaceable(positions.get(0), false, false) == 3) {
/* 200 */                 return positions;
/*     */               }
/*     */             
/*     */             case 2:
/* 204 */               positions.add(new BlockPos(mc.field_71439_g.field_70165_t + 1.0D, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v));
/* 205 */               positions.add(new BlockPos(mc.field_71439_g.field_70165_t + 1.0D, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v));
/*     */               break;
/*     */           } 
/*     */           
/*     */           break;
/*     */         } 
/* 211 */         positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v));
/* 212 */         if (this.placeMode.getValue() == PlaceMode.SELFHIGH) {
/* 213 */           positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v));
/*     */         }
/* 215 */         placeability = BlockUtil.isPositionPlaceable(positions.get(0), false);
/* 216 */         switch (placeability) {
/*     */           case 0:
/* 218 */             return new ArrayList<>();
/*     */           
/*     */           case 3:
/* 221 */             return positions;
/*     */           
/*     */           case 1:
/* 224 */             if (BlockUtil.isPositionPlaceable(positions.get(0), false, false) == 3) {
/* 225 */               return positions;
/*     */             }
/*     */             break;
/*     */         } 
/*     */ 
/*     */         
/*     */         break;
/*     */     } 
/*     */     
/* 234 */     positions.sort(Comparator.comparingDouble(Vec3i::func_177956_o));
/* 235 */     return positions;
/*     */   }
/*     */   
/*     */   private void placeBlock(BlockPos pos) {
/* 239 */     if (this.blocksThisTick < ((Integer)this.blocksPerTick.getValue()).intValue() && switchItem(false)) {
/*     */       
/* 241 */       boolean smartRotate = (((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue()), bl = smartRotate;
/* 242 */       this.isSneaking = smartRotate ? BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
/* 243 */       this.timer.reset();
/* 244 */       this.blocksThisTick++;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean check() {
/* 249 */     if (fullNullCheck() || (((Boolean)this.disable.getValue()).booleanValue() && this.offTimer.passedMs(((Integer)this.disableTime.getValue()).intValue()))) {
/* 250 */       disable();
/* 251 */       return true;
/*     */     } 
/* 253 */     if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock((this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class)) {
/* 254 */       this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*     */     }
/* 256 */     switchItem(true);
/* 257 */     if (!((Boolean)this.freecam.getValue()).booleanValue() && Banzem.moduleManager.isModuleEnabled(Freecam.class)) {
/* 258 */       return true;
/*     */     }
/* 260 */     this.blocksThisTick = 0;
/* 261 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 262 */     if (this.retryTimer.passedMs(2000L)) {
/* 263 */       this.retries.clear();
/* 264 */       this.retryTimer.reset();
/*     */     } 
/* 266 */     int targetSlot = -1;
/* 267 */     switch (this.currentMode) {
/*     */       case WEBS:
/* 269 */         this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockWeb.class);
/* 270 */         targetSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
/*     */         break;
/*     */       
/*     */       case OBSIDIAN:
/* 274 */         this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
/* 275 */         targetSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/*     */         break;
/*     */     } 
/*     */     
/* 279 */     if (((Boolean)this.onlySafe.getValue()).booleanValue() && !EntityUtil.isSafe((Entity)mc.field_71439_g)) {
/* 280 */       disable();
/* 281 */       return true;
/*     */     } 
/* 283 */     if (!this.hasOffhand && targetSlot == -1 && (!((Boolean)this.offhand.getValue()).booleanValue() || (!EntityUtil.isSafe((Entity)mc.field_71439_g) && ((Boolean)this.onlySafe.getValue()).booleanValue()))) {
/* 284 */       return true;
/*     */     }
/* 286 */     if (((Boolean)this.offhand.getValue()).booleanValue() && !this.hasOffhand) {
/* 287 */       return true;
/*     */     }
/* 289 */     return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
/*     */   }
/*     */   
/*     */   private boolean switchItem(boolean back) {
/* 293 */     if (((Boolean)this.offhand.getValue()).booleanValue()) {
/* 294 */       return true;
/*     */     }
/* 296 */     boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), (this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class);
/* 297 */     this.switchedItem = value[0];
/* 298 */     return value[1];
/*     */   }
/*     */   
/*     */   public enum PlaceMode {
/* 302 */     NORMAL,
/* 303 */     SELF,
/* 304 */     SELFHIGH;
/*     */   }
/*     */   
/*     */   public enum Mode
/*     */   {
/* 309 */     WEBS,
/* 310 */     OBSIDIAN;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\Selftrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */