/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.ProcessRightClickBlockEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.ServerModule;
/*     */ import me.mohalk.banzem.features.setting.Bind;
/*     */ import me.mohalk.banzem.features.setting.EnumConverter;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.mixin.mixins.accessors.IContainer;
/*     */ import me.mohalk.banzem.mixin.mixins.accessors.ISPacketSetSlot;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.block.BlockObsidian;
/*     */ import net.minecraft.block.BlockWeb;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.inventory.ClickType;
/*     */ import net.minecraft.inventory.EntityEquipmentSlot;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemBlock;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
/*     */ import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
/*     */ import net.minecraft.network.play.server.SPacketSetSlot;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.fml.common.eventhandler.EventPriority;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import net.minecraftforge.fml.common.gameevent.InputEvent;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ import org.lwjgl.input.Mouse;
/*     */ 
/*     */ public class Offhand
/*     */   extends Module
/*     */ {
/*     */   private static Offhand instance;
/*  49 */   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
/*  50 */   private final Timer timer = new Timer();
/*  51 */   private final Timer secondTimer = new Timer();
/*  52 */   private final Timer serverTimer = new Timer();
/*  53 */   public Setting<Type> type = register(new Setting("Mode", Type.NEW));
/*  54 */   public Setting<Boolean> cycle = register(new Setting("Cycle", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
/*  55 */   public Setting<Bind> cycleKey = register(new Setting("Key", new Bind(-1), v -> (((Boolean)this.cycle.getValue()).booleanValue() && this.type.getValue() == Type.OLD)));
/*  56 */   public Setting<Bind> offHandGapple = register(new Setting("Gapple", new Bind(-1)));
/*  57 */   public Setting<Float> gappleHealth = register(new Setting("G-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  58 */   public Setting<Float> gappleHoleHealth = register(new Setting("G-H-Health", Float.valueOf(3.5F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  59 */   public Setting<Bind> offHandCrystal = register(new Setting("Crystal", new Bind(-1)));
/*  60 */   public Setting<Float> crystalHealth = register(new Setting("C-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  61 */   public Setting<Float> crystalHoleHealth = register(new Setting("C-H-Health", Float.valueOf(3.5F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  62 */   public Setting<Float> cTargetDistance = register(new Setting("C-Distance", Float.valueOf(10.0F), Float.valueOf(1.0F), Float.valueOf(20.0F)));
/*  63 */   public Setting<Bind> obsidian = register(new Setting("Obsidian", new Bind(-1)));
/*  64 */   public Setting<Float> obsidianHealth = register(new Setting("O-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  65 */   public Setting<Float> obsidianHoleHealth = register(new Setting("O-H-Health", Float.valueOf(8.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  66 */   public Setting<Bind> webBind = register(new Setting("Webs", new Bind(-1)));
/*  67 */   public Setting<Float> webHealth = register(new Setting("W-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  68 */   public Setting<Float> webHoleHealth = register(new Setting("W-H-Health", Float.valueOf(8.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  69 */   public Setting<Boolean> holeCheck = register(new Setting("Hole-Check", Boolean.valueOf(true)));
/*  70 */   public Setting<Boolean> crystalCheck = register(new Setting("Crystal-Check", Boolean.valueOf(false)));
/*  71 */   public Setting<Boolean> gapSwap = register(new Setting("Gap-Swap", Boolean.valueOf(true)));
/*  72 */   public Setting<Integer> updates = register(new Setting("Updates", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(2)));
/*  73 */   public Setting<Boolean> cycleObby = register(new Setting("CycleObby", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
/*  74 */   public Setting<Boolean> cycleWebs = register(new Setting("CycleWebs", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
/*  75 */   public Setting<Boolean> crystalToTotem = register(new Setting("Crystal-Totem", Boolean.valueOf(true), v -> (this.type.getValue() == Type.OLD)));
/*  76 */   public Setting<Boolean> absorption = register(new Setting("Absorption", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
/*  77 */   public Setting<Boolean> autoGapple = register(new Setting("AutoGapple", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
/*  78 */   public Setting<Boolean> onlyWTotem = register(new Setting("OnlyWTotem", Boolean.valueOf(true), v -> (((Boolean)this.autoGapple.getValue()).booleanValue() && this.type.getValue() == Type.OLD)));
/*  79 */   public Setting<Boolean> unDrawTotem = register(new Setting("DrawTotems", Boolean.valueOf(true), v -> (this.type.getValue() == Type.OLD)));
/*  80 */   public Setting<Boolean> noOffhandGC = register(new Setting("NoOGC", Boolean.valueOf(false)));
/*  81 */   public Setting<Boolean> retardOGC = register(new Setting("RetardOGC", Boolean.valueOf(false)));
/*  82 */   public Setting<Boolean> returnToCrystal = register(new Setting("RecoverySwitch", Boolean.valueOf(false)));
/*  83 */   public Setting<Integer> timeout = register(new Setting("Timeout", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
/*  84 */   public Setting<Integer> timeout2 = register(new Setting("Timeout2", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
/*  85 */   public Setting<Integer> actions = register(new Setting("Actions", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4), v -> (this.type.getValue() == Type.OLD)));
/*  86 */   public Setting<NameMode> displayNameChange = register(new Setting("Name", NameMode.TOTEM, v -> (this.type.getValue() == Type.OLD)));
/*  87 */   public Setting<Boolean> guis = register(new Setting("Guis", Boolean.valueOf(false)));
/*  88 */   public Setting<Integer> serverTimeOut = register(new Setting("S-Timeout", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(5000)));
/*  89 */   public Setting<Boolean> bedcheck = register(new Setting("BedCheck", Boolean.valueOf(false)));
/*  90 */   public Mode mode = Mode.CRYSTALS;
/*  91 */   public Mode oldMode = Mode.CRYSTALS;
/*  92 */   public Mode2 currentMode = Mode2.TOTEMS;
/*  93 */   public int totems = 0;
/*  94 */   public int crystals = 0;
/*  95 */   public int gapples = 0;
/*  96 */   public int obby = 0;
/*  97 */   public int webs = 0;
/*  98 */   public int lastTotemSlot = -1;
/*  99 */   public int lastGappleSlot = -1;
/* 100 */   public int lastCrystalSlot = -1;
/* 101 */   public int lastObbySlot = -1;
/* 102 */   public int lastWebSlot = -1;
/*     */   public boolean holdingCrystal = false;
/*     */   public boolean holdingTotem = false;
/*     */   public boolean holdingGapple = false;
/*     */   public boolean holdingObby = false;
/*     */   public boolean holdingWeb = false;
/*     */   public boolean didSwitchThisTick = false;
/* 109 */   private int oldSlot = -1;
/*     */   private boolean swapToTotem = false;
/*     */   private boolean eatingApple = false;
/*     */   private boolean oldSwapToTotem = false;
/*     */   private boolean autoGappleSwitch = false;
/*     */   private boolean second = false;
/*     */   private boolean switchedForHealthReason = false;
/*     */   
/*     */   public Offhand() {
/* 118 */     super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
/* 119 */     instance = this;
/*     */   }
/*     */   
/*     */   public static Offhand getInstance() {
/* 123 */     if (instance == null) {
/* 124 */       instance = new Offhand();
/*     */     }
/* 126 */     return instance;
/*     */   }
/*     */   
/*     */   public void onItemFinish(ItemStack stack, EntityLivingBase base) {
/* 130 */     if (((Boolean)this.noOffhandGC.getValue()).booleanValue() && base.equals(mc.field_71439_g) && stack.func_77973_b() == mc.field_71439_g.func_184592_cb().func_77973_b()) {
/* 131 */       this.secondTimer.reset();
/* 132 */       this.second = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/* 138 */     if (nullCheck() || ((Integer)this.updates.getValue()).intValue() == 1) {
/*     */       return;
/*     */     }
/* 141 */     doOffhand();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
/* 146 */     if (((Boolean)this.noOffhandGC.getValue()).booleanValue() && event.hand == EnumHand.MAIN_HAND && event.stack.func_77973_b() == Items.field_185158_cP && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71476_x != null && event.pos == mc.field_71476_x.func_178782_a()) {
/* 147 */       event.setCanceled(true);
/* 148 */       mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
/* 149 */       mc.field_71442_b.func_187101_a((EntityPlayer)mc.field_71439_g, (World)mc.field_71441_e, EnumHand.OFF_HAND);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/* 155 */     if (((Boolean)this.noOffhandGC.getValue()).booleanValue() && ((Boolean)this.retardOGC.getValue()).booleanValue()) {
/* 156 */       if (this.timer.passedMs(((Integer)this.timeout.getValue()).intValue())) {
/* 157 */         if (mc.field_71439_g != null && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && Mouse.isButtonDown(1)) {
/* 158 */           mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
/* 159 */           mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown(1);
/*     */         } 
/* 161 */       } else if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
/* 162 */         mc.field_71474_y.field_74313_G.field_74513_e = false;
/*     */       } 
/*     */     }
/* 165 */     if (nullCheck() || ((Integer)this.updates.getValue()).intValue() == 2) {
/*     */       return;
/*     */     }
/* 168 */     doOffhand();
/* 169 */     if (this.secondTimer.passedMs(((Integer)this.timeout2.getValue()).intValue()) && this.second) {
/* 170 */       this.second = false;
/* 171 */       this.timer.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
/*     */   public void onKeyInput(InputEvent.KeyInputEvent event) {
/* 177 */     if (Keyboard.getEventKeyState()) {
/* 178 */       if (this.type.getValue() == Type.NEW) {
/* 179 */         if (((Bind)this.offHandCrystal.getValue()).getKey() == Keyboard.getEventKey()) {
/* 180 */           if (this.mode == Mode.CRYSTALS) {
/* 181 */             setSwapToTotem(!isSwapToTotem());
/*     */           } else {
/* 183 */             setSwapToTotem(false);
/*     */           } 
/* 185 */           setMode(Mode.CRYSTALS);
/*     */         } 
/* 187 */         if (((Bind)this.offHandGapple.getValue()).getKey() == Keyboard.getEventKey()) {
/* 188 */           if (this.mode == Mode.GAPPLES) {
/* 189 */             setSwapToTotem(!isSwapToTotem());
/*     */           } else {
/* 191 */             setSwapToTotem(false);
/*     */           } 
/* 193 */           setMode(Mode.GAPPLES);
/*     */         } 
/* 195 */         if (((Bind)this.obsidian.getValue()).getKey() == Keyboard.getEventKey()) {
/* 196 */           if (this.mode == Mode.OBSIDIAN) {
/* 197 */             setSwapToTotem(!isSwapToTotem());
/*     */           } else {
/* 199 */             setSwapToTotem(false);
/*     */           } 
/* 201 */           setMode(Mode.OBSIDIAN);
/*     */         } 
/* 203 */         if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
/* 204 */           if (this.mode == Mode.WEBS) {
/* 205 */             setSwapToTotem(!isSwapToTotem());
/*     */           } else {
/* 207 */             setSwapToTotem(false);
/*     */           } 
/* 209 */           setMode(Mode.WEBS);
/*     */         } 
/* 211 */       } else if (((Boolean)this.cycle.getValue()).booleanValue()) {
/* 212 */         if (((Bind)this.cycleKey.getValue()).getKey() == Keyboard.getEventKey()) {
/* 213 */           Mode2 newMode = (Mode2)EnumConverter.increaseEnum(this.currentMode);
/* 214 */           if ((newMode == Mode2.OBSIDIAN && !((Boolean)this.cycleObby.getValue()).booleanValue()) || (newMode == Mode2.WEBS && !((Boolean)this.cycleWebs.getValue()).booleanValue())) {
/* 215 */             newMode = Mode2.TOTEMS;
/*     */           }
/* 217 */           setMode(newMode);
/*     */         } 
/*     */       } else {
/* 220 */         if (((Bind)this.offHandCrystal.getValue()).getKey() == Keyboard.getEventKey()) {
/* 221 */           setMode(Mode2.CRYSTALS);
/*     */         }
/* 223 */         if (((Bind)this.offHandGapple.getValue()).getKey() == Keyboard.getEventKey()) {
/* 224 */           setMode(Mode2.GAPPLES);
/*     */         }
/* 226 */         if (((Bind)this.obsidian.getValue()).getKey() == Keyboard.getEventKey()) {
/* 227 */           setMode(Mode2.OBSIDIAN);
/*     */         }
/* 229 */         if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
/* 230 */           setMode(Mode2.WEBS);
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketSend(PacketEvent.Send event) {
/* 238 */     if (((Boolean)this.noOffhandGC.getValue()).booleanValue() && !fullNullCheck() && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && mc.field_71474_y.field_74313_G.func_151470_d())
/*     */     {
/* 240 */       if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
/* 241 */       { CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
/* 242 */         if (packet2.func_187022_c() == EnumHand.MAIN_HAND && !AutoCrystal.placedPos.contains(packet2.func_187023_a())) {
/* 243 */           if (this.timer.passedMs(((Integer)this.timeout.getValue()).intValue())) {
/* 244 */             mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
/* 245 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
/*     */           } 
/* 247 */           event.setCanceled(true);
/*     */         }  }
/* 249 */       else { CPacketPlayerTryUseItem packet; if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet = (CPacketPlayerTryUseItem)event.getPacket()).func_187028_a() == EnumHand.OFF_HAND && !this.timer.passedMs(((Integer)this.timeout.getValue()).intValue()))
/* 250 */           event.setCanceled(true);  }
/*     */     
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceive(PacketEvent.Receive event) {
/*     */     SPacketSetSlot packet;
/* 258 */     if (ServerModule.getInstance().isConnected() && event.getPacket() instanceof SPacketSetSlot && (packet = (SPacketSetSlot)event.getPacket()).func_149173_d() == -1 && packet.func_149175_c() != -1) {
/* 259 */       ((IContainer)mc.field_71439_g.field_71070_bA).setTransactionID((short)packet.func_149175_c());
/* 260 */       ((ISPacketSetSlot)packet).setWindowId(-1);
/* 261 */       this.serverTimer.reset();
/* 262 */       this.switchedForHealthReason = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/* 268 */     if (this.type.getValue() == Type.NEW) {
/* 269 */       return String.valueOf(getStackSize());
/*     */     }
/* 271 */     switch ((NameMode)this.displayNameChange.getValue()) {
/*     */       case GAPPLES:
/* 273 */         return EnumConverter.getProperName(this.currentMode);
/*     */       
/*     */       case WEBS:
/* 276 */         if (this.currentMode == Mode2.TOTEMS) {
/* 277 */           return this.totems + "";
/*     */         }
/* 279 */         return EnumConverter.getProperName(this.currentMode);
/*     */     } 
/*     */     
/* 282 */     switch (this.currentMode) {
/*     */       case GAPPLES:
/* 284 */         return this.totems + "";
/*     */       
/*     */       case WEBS:
/* 287 */         return this.gapples + "";
/*     */     } 
/*     */     
/* 290 */     return this.crystals + "";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayName() {
/* 295 */     if (this.type.getValue() == Type.NEW) {
/* 296 */       if (!shouldTotem()) {
/* 297 */         switch (this.mode) {
/*     */           case GAPPLES:
/* 299 */             return "OffhandGapple";
/*     */           
/*     */           case WEBS:
/* 302 */             return "OffhandWebs";
/*     */           
/*     */           case OBSIDIAN:
/* 305 */             return "OffhandObby";
/*     */         } 
/*     */         
/* 308 */         return "OffhandCrystal";
/*     */       } 
/* 310 */       return "AutoTotem" + (!isSwapToTotem() ? ("-" + getModeStr()) : "");
/*     */     } 
/* 312 */     switch ((NameMode)this.displayNameChange.getValue()) {
/*     */       case GAPPLES:
/* 314 */         return (String)this.displayName.getValue();
/*     */       
/*     */       case WEBS:
/* 317 */         if (this.currentMode == Mode2.TOTEMS) {
/* 318 */           return "AutoTotem";
/*     */         }
/* 320 */         return (String)this.displayName.getValue();
/*     */     } 
/*     */     
/* 323 */     switch (this.currentMode) {
/*     */       case GAPPLES:
/* 325 */         return "AutoTotem";
/*     */       
/*     */       case WEBS:
/* 328 */         return "OffhandGapple";
/*     */       
/*     */       case OBSIDIAN:
/* 331 */         return "OffhandWebs";
/*     */       
/*     */       case CRYSTALS:
/* 334 */         return "OffhandObby";
/*     */     } 
/*     */     
/* 337 */     return "OffhandCrystal";
/*     */   }
/*     */   
/*     */   public void doOffhand() {
/* 341 */     if (!this.serverTimer.passedMs(((Integer)this.serverTimeOut.getValue()).intValue())) {
/*     */       return;
/*     */     }
/* 344 */     if (this.type.getValue() == Type.NEW) {
/* 345 */       if (mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiContainer && !((Boolean)this.guis.getValue()).booleanValue() && !(mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiInventory)) {
/*     */         return;
/*     */       }
/* 348 */       if (((Boolean)this.gapSwap.getValue()).booleanValue()) {
/* 349 */         if ((getSlot(Mode.GAPPLES) != -1 || mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao) && mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_151153_ao && mc.field_71474_y.field_74313_G.func_151470_d()) {
/* 350 */           setMode(Mode.GAPPLES);
/* 351 */           this.eatingApple = true;
/* 352 */           this.swapToTotem = false;
/* 353 */         } else if (this.eatingApple) {
/* 354 */           setMode(this.oldMode);
/* 355 */           this.swapToTotem = this.oldSwapToTotem;
/* 356 */           this.eatingApple = false;
/*     */         } else {
/* 358 */           this.oldMode = this.mode;
/* 359 */           this.oldSwapToTotem = this.swapToTotem;
/*     */         } 
/*     */       }
/* 362 */       if (!shouldTotem()) {
/* 363 */         if (mc.field_71439_g.func_184592_cb() == ItemStack.field_190927_a || !isItemInOffhand()) {
/*     */           
/* 365 */           int slot = (getSlot(this.mode) < 9) ? (getSlot(this.mode) + 36) : getSlot(this.mode), n = slot;
/* 366 */           if (getSlot(this.mode) != -1) {
/* 367 */             if (this.oldSlot != -1) {
/* 368 */               mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 369 */               mc.field_71442_b.func_187098_a(0, this.oldSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*     */             } 
/* 371 */             this.oldSlot = slot;
/* 372 */             mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 373 */             mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 374 */             mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*     */           } 
/*     */         } 
/* 377 */       } else if (!this.eatingApple && (mc.field_71439_g.func_184592_cb() == ItemStack.field_190927_a || mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_190929_cY)) {
/*     */         
/* 379 */         int slot = (getTotemSlot() < 9) ? (getTotemSlot() + 36) : getTotemSlot(), n = slot;
/* 380 */         if (getTotemSlot() != -1) {
/* 381 */           mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 382 */           mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 383 */           mc.field_71442_b.func_187098_a(0, this.oldSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 384 */           this.oldSlot = -1;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 388 */       if (!((Boolean)this.unDrawTotem.getValue()).booleanValue()) {
/* 389 */         manageDrawn();
/*     */       }
/* 391 */       this.didSwitchThisTick = false;
/* 392 */       this.holdingCrystal = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP);
/* 393 */       this.holdingTotem = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY);
/* 394 */       this.holdingGapple = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao);
/* 395 */       this.holdingObby = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
/* 396 */       this.holdingWeb = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockWeb.class);
/* 397 */       this.totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_190929_cY)).mapToInt(ItemStack::func_190916_E).sum();
/* 398 */       if (this.holdingTotem) {
/* 399 */         this.totems += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_190929_cY)).mapToInt(ItemStack::func_190916_E).sum();
/*     */       }
/* 401 */       this.crystals = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_185158_cP)).mapToInt(ItemStack::func_190916_E).sum();
/* 402 */       if (this.holdingCrystal) {
/* 403 */         this.crystals += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_185158_cP)).mapToInt(ItemStack::func_190916_E).sum();
/*     */       }
/* 405 */       this.gapples = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_151153_ao)).mapToInt(ItemStack::func_190916_E).sum();
/* 406 */       if (this.holdingGapple) {
/* 407 */         this.gapples += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_151153_ao)).mapToInt(ItemStack::func_190916_E).sum();
/*     */       }
/* 409 */       if (this.currentMode == Mode2.WEBS || this.currentMode == Mode2.OBSIDIAN) {
/* 410 */         this.obby = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.func_77973_b(), BlockObsidian.class)).mapToInt(ItemStack::func_190916_E).sum();
/* 411 */         if (this.holdingObby) {
/* 412 */           this.obby += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.func_77973_b(), BlockObsidian.class)).mapToInt(ItemStack::func_190916_E).sum();
/*     */         }
/* 414 */         this.webs = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.func_77973_b(), BlockWeb.class)).mapToInt(ItemStack::func_190916_E).sum();
/* 415 */         if (this.holdingWeb) {
/* 416 */           this.webs += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.func_77973_b(), BlockWeb.class)).mapToInt(ItemStack::func_190916_E).sum();
/*     */         }
/*     */       } 
/* 419 */       doSwitch();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void manageDrawn() {
/* 424 */     if (this.currentMode == Mode2.TOTEMS && ((Boolean)this.drawn.getValue()).booleanValue()) {
/* 425 */       this.drawn.setValue(Boolean.valueOf(false));
/*     */     }
/* 427 */     if (this.currentMode != Mode2.TOTEMS && !((Boolean)this.drawn.getValue()).booleanValue())
/* 428 */       this.drawn.setValue(Boolean.valueOf(true)); 
/*     */   }
/*     */   
/*     */   public void doSwitch() {
/*     */     int lastSlot;
/* 433 */     if (((Boolean)this.autoGapple.getValue()).booleanValue()) {
/* 434 */       if (mc.field_71474_y.field_74313_G.func_151470_d()) {
/* 435 */         if (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemSword && (!((Boolean)this.onlyWTotem.getValue()).booleanValue() || mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY)) {
/* 436 */           setMode(Mode.GAPPLES);
/* 437 */           this.autoGappleSwitch = true;
/*     */         } 
/* 439 */       } else if (this.autoGappleSwitch) {
/* 440 */         setMode(Mode2.TOTEMS);
/* 441 */         this.autoGappleSwitch = false;
/*     */       } 
/*     */     }
/* 444 */     if ((this.currentMode == Mode2.GAPPLES && (((!EntityUtil.isSafe((Entity)mc.field_71439_g) || bedPlaceable()) && EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.gappleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.gappleHoleHealth.getValue()).floatValue())) || (this.currentMode == Mode2.CRYSTALS && (((!EntityUtil.isSafe((Entity)mc.field_71439_g) || bedPlaceable()) && EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.crystalHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.crystalHoleHealth.getValue()).floatValue())) || (this.currentMode == Mode2.OBSIDIAN && (((!EntityUtil.isSafe((Entity)mc.field_71439_g) || bedPlaceable()) && EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.obsidianHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.obsidianHoleHealth.getValue()).floatValue())) || (this.currentMode == Mode2.WEBS && (((!EntityUtil.isSafe((Entity)mc.field_71439_g) || bedPlaceable()) && EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.webHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.webHoleHealth.getValue()).floatValue()))) {
/* 445 */       if (((Boolean)this.returnToCrystal.getValue()).booleanValue() && this.currentMode == Mode2.CRYSTALS) {
/* 446 */         this.switchedForHealthReason = true;
/*     */       }
/* 448 */       setMode(Mode2.TOTEMS);
/*     */     } 
/* 450 */     if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)mc.field_71439_g) && !bedPlaceable() && EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) > ((Float)this.crystalHoleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, ((Boolean)this.absorption.getValue()).booleanValue()) > ((Float)this.crystalHealth.getValue()).floatValue())) {
/* 451 */       setMode(Mode2.CRYSTALS);
/* 452 */       this.switchedForHealthReason = false;
/*     */     } 
/* 454 */     if (mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiContainer && !((Boolean)this.guis.getValue()).booleanValue() && !(mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiInventory)) {
/*     */       return;
/*     */     }
/* 457 */     Item currentOffhandItem = mc.field_71439_g.func_184592_cb().func_77973_b();
/* 458 */     switch (this.currentMode) {
/*     */       case GAPPLES:
/* 460 */         if (this.totems <= 0 || this.holdingTotem)
/* 461 */           break;  this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.field_190929_cY, false);
/* 462 */         lastSlot = getLastSlot(currentOffhandItem, this.lastTotemSlot);
/* 463 */         putItemInOffhand(this.lastTotemSlot, lastSlot);
/*     */         break;
/*     */       
/*     */       case WEBS:
/* 467 */         if (this.gapples <= 0 || this.holdingGapple)
/* 468 */           break;  this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.field_151153_ao, false);
/* 469 */         lastSlot = getLastSlot(currentOffhandItem, this.lastGappleSlot);
/* 470 */         putItemInOffhand(this.lastGappleSlot, lastSlot);
/*     */         break;
/*     */       
/*     */       case OBSIDIAN:
/* 474 */         if (this.webs <= 0 || this.holdingWeb)
/* 475 */           break;  this.lastWebSlot = InventoryUtil.findInventoryBlock(BlockWeb.class, false);
/* 476 */         lastSlot = getLastSlot(currentOffhandItem, this.lastWebSlot);
/* 477 */         putItemInOffhand(this.lastWebSlot, lastSlot);
/*     */         break;
/*     */       
/*     */       case CRYSTALS:
/* 481 */         if (this.obby <= 0 || this.holdingObby)
/* 482 */           break;  this.lastObbySlot = InventoryUtil.findInventoryBlock(BlockObsidian.class, false);
/* 483 */         lastSlot = getLastSlot(currentOffhandItem, this.lastObbySlot);
/* 484 */         putItemInOffhand(this.lastObbySlot, lastSlot);
/*     */         break;
/*     */       
/*     */       default:
/* 488 */         if (this.crystals <= 0 || this.holdingCrystal)
/* 489 */           break;  this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.field_185158_cP, false);
/* 490 */         lastSlot = getLastSlot(currentOffhandItem, this.lastCrystalSlot);
/* 491 */         putItemInOffhand(this.lastCrystalSlot, lastSlot);
/*     */         break;
/*     */     } 
/* 494 */     for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
/* 495 */       InventoryUtil.Task task = this.taskList.poll();
/* 496 */       if (task != null) {
/* 497 */         task.run();
/* 498 */         if (task.isSwitching())
/* 499 */           this.didSwitchThisTick = true; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private int getLastSlot(Item item, int slotIn) {
/* 504 */     if (item == Items.field_185158_cP) {
/* 505 */       return this.lastCrystalSlot;
/*     */     }
/* 507 */     if (item == Items.field_151153_ao) {
/* 508 */       return this.lastGappleSlot;
/*     */     }
/* 510 */     if (item == Items.field_190929_cY) {
/* 511 */       return this.lastTotemSlot;
/*     */     }
/* 513 */     if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
/* 514 */       return this.lastObbySlot;
/*     */     }
/* 516 */     if (InventoryUtil.isBlock(item, BlockWeb.class)) {
/* 517 */       return this.lastWebSlot;
/*     */     }
/* 519 */     if (item == Items.field_190931_a) {
/* 520 */       return -1;
/*     */     }
/* 522 */     return slotIn;
/*     */   }
/*     */   
/*     */   private void putItemInOffhand(int slotIn, int slotOut) {
/* 526 */     if (slotIn != -1 && this.taskList.isEmpty()) {
/* 527 */       this.taskList.add(new InventoryUtil.Task(slotIn));
/* 528 */       this.taskList.add(new InventoryUtil.Task(45));
/* 529 */       this.taskList.add(new InventoryUtil.Task(slotOut));
/* 530 */       this.taskList.add(new InventoryUtil.Task());
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean noNearbyPlayers() {
/* 535 */     return (this.mode == Mode.CRYSTALS && mc.field_71441_e.field_73010_i.stream().noneMatch(e -> (e != mc.field_71439_g && !Banzem.friendManager.isFriend(e) && mc.field_71439_g.func_70032_d((Entity)e) <= ((Float)this.cTargetDistance.getValue()).floatValue())));
/*     */   }
/*     */   
/*     */   private boolean isItemInOffhand() {
/* 539 */     switch (this.mode) {
/*     */       case GAPPLES:
/* 541 */         return (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao);
/*     */       
/*     */       case CRYSTALS:
/* 544 */         return (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP);
/*     */       
/*     */       case OBSIDIAN:
/* 547 */         return (mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184592_cb().func_77973_b()).field_150939_a == Blocks.field_150343_Z);
/*     */       
/*     */       case WEBS:
/* 550 */         return (mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184592_cb().func_77973_b()).field_150939_a == Blocks.field_150321_G);
/*     */     } 
/*     */     
/* 553 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isHeldInMainHand() {
/* 557 */     switch (this.mode) {
/*     */       case GAPPLES:
/* 559 */         return (mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151153_ao);
/*     */       
/*     */       case CRYSTALS:
/* 562 */         return (mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP);
/*     */       
/*     */       case OBSIDIAN:
/* 565 */         return (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184614_ca().func_77973_b()).field_150939_a == Blocks.field_150343_Z);
/*     */       
/*     */       case WEBS:
/* 568 */         return (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184614_ca().func_77973_b()).field_150939_a == Blocks.field_150321_G);
/*     */     } 
/*     */     
/* 571 */     return false;
/*     */   }
/*     */   
/*     */   private boolean shouldTotem() {
/* 575 */     if (isHeldInMainHand() || isSwapToTotem()) {
/* 576 */       return true;
/*     */     }
/* 578 */     if (((Boolean)this.holeCheck.getValue()).booleanValue() && EntityUtil.isInHole((Entity)mc.field_71439_g) && !bedPlaceable()) {
/* 579 */       return (mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj() <= getHoleHealth() || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_185160_cR || mc.field_71439_g.field_70143_R >= 3.0F || noNearbyPlayers() || (((Boolean)this.crystalCheck.getValue()).booleanValue() && isCrystalsAABBEmpty()));
/*     */     }
/* 581 */     return (mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj() <= getHealth() || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_185160_cR || mc.field_71439_g.field_70143_R >= 3.0F || noNearbyPlayers() || (((Boolean)this.crystalCheck.getValue()).booleanValue() && isCrystalsAABBEmpty()));
/*     */   }
/*     */   
/*     */   private boolean isNotEmpty(BlockPos pos) {
/* 585 */     return mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(pos)).stream().anyMatch(e -> e instanceof net.minecraft.entity.item.EntityEnderCrystal);
/*     */   }
/*     */   
/*     */   private float getHealth() {
/* 589 */     switch (this.mode) {
/*     */       case CRYSTALS:
/* 591 */         return ((Float)this.crystalHealth.getValue()).floatValue();
/*     */       
/*     */       case GAPPLES:
/* 594 */         return ((Float)this.gappleHealth.getValue()).floatValue();
/*     */       
/*     */       case OBSIDIAN:
/* 597 */         return ((Float)this.obsidianHealth.getValue()).floatValue();
/*     */     } 
/*     */     
/* 600 */     return ((Float)this.webHealth.getValue()).floatValue();
/*     */   }
/*     */   
/*     */   private float getHoleHealth() {
/* 604 */     switch (this.mode) {
/*     */       case CRYSTALS:
/* 606 */         return ((Float)this.crystalHoleHealth.getValue()).floatValue();
/*     */       
/*     */       case GAPPLES:
/* 609 */         return ((Float)this.gappleHoleHealth.getValue()).floatValue();
/*     */       
/*     */       case OBSIDIAN:
/* 612 */         return ((Float)this.obsidianHoleHealth.getValue()).floatValue();
/*     */     } 
/*     */     
/* 615 */     return ((Float)this.webHoleHealth.getValue()).floatValue();
/*     */   }
/*     */   
/*     */   private boolean isCrystalsAABBEmpty() {
/* 619 */     return (isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(1, 0, 0)) || isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(-1, 0, 0)) || isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(0, 0, 1)) || isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(0, 0, -1)) || isNotEmpty(mc.field_71439_g.func_180425_c()));
/*     */   }
/*     */   
/*     */   int getStackSize() {
/* 623 */     int size = 0;
/* 624 */     if (shouldTotem()) {
/* 625 */       for (int i = 45; i > 0; i--) {
/* 626 */         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190929_cY)
/* 627 */           size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E(); 
/*     */       } 
/* 629 */     } else if (this.mode == Mode.OBSIDIAN) {
/* 630 */       for (int i = 45; i > 0; i--) {
/* 631 */         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a == Blocks.field_150343_Z)
/*     */         {
/* 633 */           size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E(); } 
/*     */       } 
/* 635 */     } else if (this.mode == Mode.WEBS) {
/* 636 */       for (int i = 45; i > 0; i--) {
/* 637 */         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a == Blocks.field_150321_G)
/*     */         {
/* 639 */           size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E(); } 
/*     */       } 
/*     */     } else {
/* 642 */       for (int i = 45; i > 0; i--) {
/* 643 */         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == ((this.mode == Mode.CRYSTALS) ? Items.field_185158_cP : Items.field_151153_ao))
/*     */         {
/* 645 */           size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E(); } 
/*     */       } 
/*     */     } 
/* 648 */     return size;
/*     */   }
/*     */   
/*     */   int getSlot(Mode m) {
/* 652 */     int slot = -1;
/* 653 */     if (m == Mode.OBSIDIAN) {
/* 654 */       for (int i = 45; i > 0; ) {
/* 655 */         if (!(mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock) || ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a != Blocks.field_150343_Z) {
/*     */           i--; continue;
/* 657 */         }  slot = i;
/*     */       }
/*     */     
/* 660 */     } else if (m == Mode.WEBS) {
/* 661 */       for (int i = 45; i > 0; ) {
/* 662 */         if (!(mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock) || ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a != Blocks.field_150321_G) {
/*     */           i--; continue;
/* 664 */         }  slot = i;
/*     */       } 
/*     */     } else {
/*     */       
/* 668 */       for (int i = 45; i > 0; ) {
/* 669 */         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() != ((m == Mode.CRYSTALS) ? Items.field_185158_cP : Items.field_151153_ao)) {
/*     */           i--; continue;
/* 671 */         }  slot = i;
/*     */       } 
/*     */     } 
/*     */     
/* 675 */     return slot;
/*     */   }
/*     */   
/*     */   int getTotemSlot() {
/* 679 */     int totemSlot = -1;
/* 680 */     for (int i = 45; i > 0; ) {
/* 681 */       if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() != Items.field_190929_cY) { i--; continue; }
/* 682 */        totemSlot = i;
/*     */     } 
/*     */     
/* 685 */     return totemSlot;
/*     */   }
/*     */   
/*     */   private String getModeStr() {
/* 689 */     switch (this.mode) {
/*     */       case GAPPLES:
/* 691 */         return "G";
/*     */       
/*     */       case WEBS:
/* 694 */         return "W";
/*     */       
/*     */       case OBSIDIAN:
/* 697 */         return "O";
/*     */     } 
/*     */     
/* 700 */     return "C";
/*     */   }
/*     */   
/*     */   public void setMode(Mode mode) {
/* 704 */     this.mode = mode;
/*     */   }
/*     */   
/*     */   public void setMode(Mode2 mode) {
/* 708 */     this.currentMode = (this.currentMode == mode) ? Mode2.TOTEMS : ((!((Boolean)this.cycle.getValue()).booleanValue() && ((Boolean)this.crystalToTotem.getValue()).booleanValue() && (this.currentMode == Mode2.CRYSTALS || this.currentMode == Mode2.OBSIDIAN || this.currentMode == Mode2.WEBS) && mode == Mode2.GAPPLES) ? Mode2.TOTEMS : mode);
/*     */   }
/*     */   
/*     */   public boolean isSwapToTotem() {
/* 712 */     return this.swapToTotem;
/*     */   }
/*     */   
/*     */   public void setSwapToTotem(boolean swapToTotem) {
/* 716 */     this.swapToTotem = swapToTotem;
/*     */   }
/*     */   
/*     */   private boolean bedPlaceable() {
/* 720 */     if (!((Boolean)this.bedcheck.getValue()).booleanValue()) {
/* 721 */       return false;
/*     */     }
/* 723 */     if (mc.field_71441_e.func_180495_p(mc.field_71439_g.func_180425_c()).func_177230_c() != Blocks.field_150324_C && mc.field_71441_e.func_180495_p(mc.field_71439_g.func_180425_c()).func_177230_c() != Blocks.field_150350_a)
/* 724 */       return false;  EnumFacing[] arrayOfEnumFacing; int i;
/*     */     byte b;
/* 726 */     for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) { EnumFacing facing = arrayOfEnumFacing[b];
/* 727 */       if (facing == EnumFacing.UP || facing == EnumFacing.DOWN || (mc.field_71441_e.func_180495_p(mc.field_71439_g.func_180425_c().func_177972_a(facing)).func_177230_c() != Blocks.field_150324_C && mc.field_71441_e.func_180495_p(mc.field_71439_g.func_180425_c().func_177972_a(facing)).func_177230_c() != Blocks.field_150350_a)) {
/*     */         b++; continue;
/* 729 */       }  return true; }
/*     */     
/* 731 */     return false;
/*     */   }
/*     */   
/*     */   public enum NameMode {
/* 735 */     MODE,
/* 736 */     TOTEM,
/* 737 */     AMOUNT;
/*     */   }
/*     */   
/*     */   public enum Mode2
/*     */   {
/* 742 */     TOTEMS,
/* 743 */     GAPPLES,
/* 744 */     CRYSTALS,
/* 745 */     OBSIDIAN,
/* 746 */     WEBS;
/*     */   }
/*     */   
/*     */   public enum Type
/*     */   {
/* 751 */     OLD,
/* 752 */     NEW;
/*     */   }
/*     */   
/*     */   public enum Mode
/*     */   {
/* 757 */     CRYSTALS,
/* 758 */     GAPPLES,
/* 759 */     OBSIDIAN,
/* 760 */     WEBS;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\Offhand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */