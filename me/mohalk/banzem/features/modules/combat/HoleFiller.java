/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*     */ import me.mohalk.banzem.features.modules.client.ServerModule;
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
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketChatMessage;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import net.minecraftforge.fml.common.gameevent.InputEvent;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ public class HoleFiller extends Module {
/*  29 */   private static HoleFiller INSTANCE = new HoleFiller();
/*  30 */   private final Setting<Boolean> server = register(new Setting("Server", Boolean.valueOf(false)));
/*  31 */   private final Setting<Double> range = register(new Setting("PlaceRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D)));
/*  32 */   private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
/*  33 */   private final Setting<Integer> blocksPerTick = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
/*  34 */   private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
/*  35 */   private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
/*  36 */   private final Setting<Boolean> disable = register(new Setting("Disable", Boolean.valueOf(true)));
/*  37 */   private final Setting<Integer> disableTime = register(new Setting("Ms/Disable", Integer.valueOf(200), Integer.valueOf(1), Integer.valueOf(250)));
/*  38 */   private final Setting<Boolean> offhand = register(new Setting("OffHand", Boolean.valueOf(true)));
/*  39 */   private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
/*  40 */   private final Setting<Boolean> onlySafe = register(new Setting("OnlySafe", Boolean.valueOf(true), v -> ((Boolean)this.offhand.getValue()).booleanValue()));
/*  41 */   private final Setting<Boolean> webSelf = register(new Setting("SelfWeb", Boolean.valueOf(false)));
/*  42 */   private final Setting<Boolean> highWeb = register(new Setting("HighWeb", Boolean.valueOf(false)));
/*  43 */   private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false)));
/*  44 */   private final Setting<Boolean> midSafeHoles = register(new Setting("MidSafe", Boolean.valueOf(false)));
/*  45 */   private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
/*  46 */   private final Setting<Boolean> onGroundCheck = register(new Setting("OnGroundCheck", Boolean.valueOf(false)));
/*  47 */   private final Timer offTimer = new Timer();
/*  48 */   private final Timer timer = new Timer();
/*  49 */   private final Map<BlockPos, Integer> retries = new HashMap<>();
/*  50 */   private final Timer retryTimer = new Timer();
/*  51 */   public Setting<Mode> mode = register(new Setting("Mode", Mode.OBSIDIAN));
/*  52 */   public Setting<PlaceMode> placeMode = register(new Setting("PlaceMode", PlaceMode.ALL));
/*  53 */   private final Setting<Double> smartRange = register(new Setting("SmartRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D), v -> (this.placeMode.getValue() == PlaceMode.SMART)));
/*  54 */   public Setting<Bind> obbyBind = register(new Setting("Obsidian", new Bind(-1)));
/*  55 */   public Setting<Bind> webBind = register(new Setting("Webs", new Bind(-1)));
/*  56 */   public Mode currentMode = Mode.OBSIDIAN;
/*     */   private boolean accessedViaBind = false;
/*  58 */   private int targetSlot = -1;
/*  59 */   private int blocksThisTick = 0;
/*  60 */   private Offhand.Mode offhandMode = Offhand.Mode.CRYSTALS;
/*  61 */   private Offhand.Mode2 offhandMode2 = Offhand.Mode2.CRYSTALS;
/*     */   private boolean isSneaking;
/*     */   private boolean hasOffhand = false;
/*     */   private boolean placeHighWeb = false;
/*  65 */   private int lastHotbarSlot = -1;
/*     */   private boolean switchedItem = false;
/*     */   
/*     */   public HoleFiller() {
/*  69 */     super("HoleFiller", "Fills holes around you.", Module.Category.COMBAT, true, false, true);
/*  70 */     setInstance();
/*     */   }
/*     */   
/*     */   public static HoleFiller getInstance() {
/*  74 */     if (INSTANCE == null) {
/*  75 */       INSTANCE = new HoleFiller();
/*     */     }
/*  77 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  81 */     INSTANCE = this;
/*     */   }
/*     */   
/*     */   private boolean shouldServer() {
/*  85 */     return (ServerModule.getInstance().isConnected() && ((Boolean)this.server.getValue()).booleanValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  90 */     if (fullNullCheck()) {
/*  91 */       disable();
/*     */     }
/*  93 */     if (!mc.field_71439_g.field_70122_E && ((Boolean)this.onGroundCheck.getValue()).booleanValue()) {
/*     */       return;
/*     */     }
/*  96 */     if (shouldServer()) {
/*  97 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Serverprefix" + (String)(ClickGui.getInstance()).prefix.getValue()));
/*  98 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Server" + (String)(ClickGui.getInstance()).prefix.getValue() + "module HoleFiller set Enabled true"));
/*     */       return;
/*     */     } 
/* 101 */     this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/* 102 */     if (!this.accessedViaBind) {
/* 103 */       this.currentMode = (Mode)this.mode.getValue();
/*     */     }
/* 105 */     Offhand module = (Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class);
/* 106 */     this.offhandMode = module.mode;
/* 107 */     this.offhandMode2 = module.currentMode;
/* 108 */     if (((Boolean)this.offhand.getValue()).booleanValue() && (EntityUtil.isSafe((Entity)mc.field_71439_g) || !((Boolean)this.onlySafe.getValue()).booleanValue())) {
/* 109 */       if (module.type.getValue() == Offhand.Type.NEW) {
/* 110 */         if (this.currentMode == Mode.WEBS) {
/* 111 */           module.setSwapToTotem(false);
/* 112 */           module.setMode(Offhand.Mode.WEBS);
/*     */         } else {
/* 114 */           module.setSwapToTotem(false);
/* 115 */           module.setMode(Offhand.Mode.OBSIDIAN);
/*     */         } 
/*     */       } else {
/* 118 */         if (this.currentMode == Mode.WEBS) {
/* 119 */           module.setMode(Offhand.Mode2.WEBS);
/*     */         } else {
/* 121 */           module.setMode(Offhand.Mode2.OBSIDIAN);
/*     */         } 
/* 123 */         if (!module.didSwitchThisTick) {
/* 124 */           module.doOffhand();
/*     */         }
/*     */       } 
/*     */     }
/* 128 */     Banzem.holeManager.update();
/* 129 */     this.offTimer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/* 134 */     if (isOn() && (((Integer)this.blocksPerTick.getValue()).intValue() != 1 || !((Boolean)this.rotate.getValue()).booleanValue())) {
/* 135 */       doHoleFill();
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/* 141 */     if (isOn() && event.getStage() == 0 && ((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue()) {
/* 142 */       doHoleFill();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 148 */     if (((Boolean)this.offhand.getValue()).booleanValue()) {
/* 149 */       ((Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode);
/* 150 */       ((Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode2);
/*     */     } 
/* 152 */     switchItem(true);
/* 153 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 154 */     this.retries.clear();
/* 155 */     this.accessedViaBind = false;
/* 156 */     this.hasOffhand = false;
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
/*     */   public void onKeyInput(InputEvent.KeyInputEvent event) {
/* 161 */     if (Keyboard.getEventKeyState()) {
/* 162 */       if (((Bind)this.obbyBind.getValue()).getKey() == Keyboard.getEventKey()) {
/* 163 */         this.accessedViaBind = true;
/* 164 */         this.currentMode = Mode.OBSIDIAN;
/* 165 */         toggle();
/*     */       } 
/* 167 */       if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
/* 168 */         this.accessedViaBind = true;
/* 169 */         this.currentMode = Mode.WEBS;
/* 170 */         toggle();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doHoleFill() {
/*     */     ArrayList<BlockPos> targets;
/* 181 */     if (check()) {
/*     */       return;
/*     */     }
/* 184 */     if (this.placeHighWeb) {
/* 185 */       BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v);
/* 186 */       placeBlock(pos);
/* 187 */       this.placeHighWeb = false;
/*     */     } 
/* 189 */     if (((Boolean)this.midSafeHoles.getValue()).booleanValue()) {
/* 190 */       Object object1 = Banzem.holeManager.getMidSafety();
/* 191 */       synchronized (object1) {
/* 192 */         targets = new ArrayList<>(Banzem.holeManager.getMidSafety());
/*     */       } 
/*     */     } 
/* 195 */     Object object = Banzem.holeManager.getHoles();
/* 196 */     synchronized (object) {
/* 197 */       targets = new ArrayList<>(Banzem.holeManager.getHoles());
/*     */     } 
/* 199 */     for (BlockPos position : targets) {
/*     */       
/* 201 */       if (mc.field_71439_g.func_174818_b(position) > MathUtil.square(((Double)this.range.getValue()).doubleValue()) || (this.placeMode.getValue() == PlaceMode.SMART && !isPlayerInRange(position)))
/*     */         continue; 
/* 203 */       if (position.equals(new BlockPos(mc.field_71439_g.func_174791_d()))) {
/* 204 */         if (this.currentMode != Mode.WEBS || !((Boolean)this.webSelf.getValue()).booleanValue())
/* 205 */           continue;  if (((Boolean)this.highWeb.getValue()).booleanValue())
/* 206 */           this.placeHighWeb = true; 
/*     */       } 
/*     */       int placeability;
/* 209 */       if ((placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue())) == 1 && (this.currentMode == Mode.WEBS || (this.switchMode.getValue() == InventoryUtil.Switch.SILENT && (this.currentMode == Mode.WEBS || this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < 4)))) {
/* 210 */         placeBlock(position);
/* 211 */         if (this.currentMode == Mode.WEBS)
/* 212 */           continue;  this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
/*     */         continue;
/*     */       } 
/* 215 */       if (placeability != 3)
/* 216 */         continue;  placeBlock(position);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void placeBlock(BlockPos pos) {
/* 221 */     if (this.blocksThisTick < ((Integer)this.blocksPerTick.getValue()).intValue() && switchItem(false)) {
/*     */       
/* 223 */       boolean smartRotate = (((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue()), bl = smartRotate;
/* 224 */       this.isSneaking = smartRotate ? BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
/* 225 */       this.timer.reset();
/* 226 */       this.blocksThisTick++;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isPlayerInRange(BlockPos pos) {
/* 231 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 232 */       if (EntityUtil.isntValid((Entity)player, ((Double)this.smartRange.getValue()).doubleValue()))
/* 233 */         continue;  return true;
/*     */     } 
/* 235 */     return false;
/*     */   }
/*     */   
/*     */   private boolean check() {
/* 239 */     if (fullNullCheck() || (((Boolean)this.disable.getValue()).booleanValue() && this.offTimer.passedMs(((Integer)this.disableTime.getValue()).intValue()))) {
/* 240 */       disable();
/* 241 */       return true;
/*     */     } 
/* 243 */     if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock((this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class)) {
/* 244 */       this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*     */     }
/* 246 */     switchItem(true);
/* 247 */     if (!((Boolean)this.freecam.getValue()).booleanValue() && Banzem.moduleManager.isModuleEnabled(Freecam.class)) {
/* 248 */       return true;
/*     */     }
/* 250 */     this.blocksThisTick = 0;
/* 251 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 252 */     if (this.retryTimer.passedMs(2000L)) {
/* 253 */       this.retries.clear();
/* 254 */       this.retryTimer.reset();
/*     */     } 
/* 256 */     switch (this.currentMode) {
/*     */       case WEBS:
/* 258 */         this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockWeb.class);
/* 259 */         this.targetSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
/*     */         break;
/*     */       
/*     */       case OBSIDIAN:
/* 263 */         this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
/* 264 */         this.targetSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
/*     */         break;
/*     */     } 
/*     */     
/* 268 */     if (((Boolean)this.onlySafe.getValue()).booleanValue() && !EntityUtil.isSafe((Entity)mc.field_71439_g)) {
/* 269 */       disable();
/* 270 */       return true;
/*     */     } 
/* 272 */     if (!this.hasOffhand && this.targetSlot == -1 && (!((Boolean)this.offhand.getValue()).booleanValue() || (!EntityUtil.isSafe((Entity)mc.field_71439_g) && ((Boolean)this.onlySafe.getValue()).booleanValue()))) {
/* 273 */       return true;
/*     */     }
/* 275 */     if (((Boolean)this.offhand.getValue()).booleanValue() && !this.hasOffhand) {
/* 276 */       return true;
/*     */     }
/* 278 */     return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
/*     */   }
/*     */   
/*     */   private boolean switchItem(boolean back) {
/* 282 */     if (((Boolean)this.offhand.getValue()).booleanValue()) {
/* 283 */       return true;
/*     */     }
/* 285 */     boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), (this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class);
/* 286 */     this.switchedItem = value[0];
/* 287 */     return value[1];
/*     */   }
/*     */   
/*     */   public enum PlaceMode {
/* 291 */     SMART,
/* 292 */     ALL;
/*     */   }
/*     */   
/*     */   public enum Mode
/*     */   {
/* 297 */     WEBS,
/* 298 */     OBSIDIAN;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\HoleFiller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */