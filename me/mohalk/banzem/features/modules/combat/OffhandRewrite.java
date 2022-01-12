/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.ProcessRightClickBlockEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.block.BlockObsidian;
/*     */ import net.minecraft.block.BlockWeb;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.inventory.EntityEquipmentSlot;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
/*     */ import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import org.lwjgl.input.Mouse;
/*     */ 
/*     */ public class OffhandRewrite
/*     */   extends Module {
/*     */   private static OffhandRewrite instance;
/*  31 */   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
/*  32 */   private final Timer timer = new Timer();
/*  33 */   private final Timer secondTimer = new Timer();
/*  34 */   public Setting<Boolean> crystal = register(new Setting("Crystal", Boolean.valueOf(true)));
/*  35 */   public Setting<Float> crystalHealth = register(new Setting("CrystalHP", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  36 */   public Setting<Float> crystalHoleHealth = register(new Setting("CrystalHoleHP", Float.valueOf(3.5F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/*  37 */   public Setting<Boolean> gapple = register(new Setting("Gapple", Boolean.valueOf(true)));
/*  38 */   public Setting<Boolean> antiGappleFail = register(new Setting("AntiGapFail", Boolean.valueOf(false)));
/*  39 */   public Setting<Boolean> armorCheck = register(new Setting("ArmorCheck", Boolean.valueOf(true)));
/*  40 */   public Setting<Integer> actions = register(new Setting("Packets", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4)));
/*  41 */   public Setting<Boolean> fallDistance = register(new Setting("FallDistance", Boolean.valueOf(false)));
/*  42 */   public Setting<Float> Height = register(new Setting("Height", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(30.0F), v -> ((Boolean)this.fallDistance.getValue()).booleanValue()));
/*  43 */   public Mode2 currentMode = Mode2.TOTEMS;
/*  44 */   public int totems = 0;
/*  45 */   public int crystals = 0;
/*  46 */   public int gapples = 0;
/*  47 */   public int lastTotemSlot = -1;
/*  48 */   public int lastGappleSlot = -1;
/*  49 */   public int lastCrystalSlot = -1;
/*  50 */   public int lastObbySlot = -1;
/*  51 */   public int lastWebSlot = -1;
/*     */   public boolean holdingCrystal = false;
/*     */   public boolean holdingTotem = false;
/*     */   public boolean holdingGapple = false;
/*     */   public boolean didSwitchThisTick = false;
/*     */   private boolean second = false;
/*     */   private boolean switchedForHealthReason = false;
/*     */   
/*     */   public OffhandRewrite() {
/*  60 */     super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
/*  61 */     instance = this;
/*     */   }
/*     */   
/*     */   public static OffhandRewrite getInstance() {
/*  65 */     if (instance == null) {
/*  66 */       instance = new OffhandRewrite();
/*     */     }
/*  68 */     return instance;
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
/*  73 */     if (event.hand == EnumHand.MAIN_HAND && event.stack.func_77973_b() == Items.field_185158_cP && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71476_x != null && event.pos == mc.field_71476_x.func_178782_a()) {
/*  74 */       event.setCanceled(true);
/*  75 */       mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
/*  76 */       mc.field_71442_b.func_187101_a((EntityPlayer)mc.field_71439_g, (World)mc.field_71441_e, EnumHand.OFF_HAND);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  82 */     if (this.timer.passedMs(50L)) {
/*  83 */       if (mc.field_71439_g != null && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && Mouse.isButtonDown(1)) {
/*  84 */         mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
/*  85 */         mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown(1);
/*     */       } 
/*  87 */     } else if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
/*  88 */       mc.field_71474_y.field_74313_G.field_74513_e = false;
/*     */     } 
/*  90 */     if (Offhand.nullCheck()) {
/*     */       return;
/*     */     }
/*  93 */     doOffhand();
/*  94 */     if (this.secondTimer.passedMs(50L) && this.second) {
/*  95 */       this.second = false;
/*  96 */       this.timer.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketSend(PacketEvent.Send event) {
/* 102 */     if (!Offhand.fullNullCheck() && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && mc.field_71474_y.field_74313_G.func_151470_d())
/*     */     {
/* 104 */       if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
/* 105 */       { CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
/* 106 */         if (packet2.func_187022_c() == EnumHand.MAIN_HAND) {
/* 107 */           if (this.timer.passedMs(50L)) {
/* 108 */             mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
/* 109 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
/*     */           } 
/* 111 */           event.setCanceled(true);
/*     */         }  }
/* 113 */       else { CPacketPlayerTryUseItem packet; if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet = (CPacketPlayerTryUseItem)event.getPacket()).func_187028_a() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
/* 114 */           event.setCanceled(true);
/*     */         } }
/*     */     
/*     */     }
/*     */   }
/*     */   
/*     */   public String getDisplayInfo() {
/* 121 */     if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
/* 122 */       return "Crystals";
/*     */     }
/* 124 */     if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
/* 125 */       return "Totems";
/*     */     }
/* 127 */     if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao) {
/* 128 */       return "Gapples";
/*     */     }
/* 130 */     return null;
/*     */   }
/*     */   
/*     */   public void doOffhand() {
/* 134 */     this.didSwitchThisTick = false;
/* 135 */     this.holdingCrystal = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP);
/* 136 */     this.holdingTotem = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY);
/* 137 */     this.holdingGapple = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao);
/* 138 */     this.totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_190929_cY)).mapToInt(ItemStack::func_190916_E).sum();
/* 139 */     if (this.holdingTotem) {
/* 140 */       this.totems += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_190929_cY)).mapToInt(ItemStack::func_190916_E).sum();
/*     */     }
/* 142 */     this.crystals = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_185158_cP)).mapToInt(ItemStack::func_190916_E).sum();
/* 143 */     if (this.holdingCrystal) {
/* 144 */       this.crystals += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_185158_cP)).mapToInt(ItemStack::func_190916_E).sum();
/*     */     }
/* 146 */     this.gapples = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_151153_ao)).mapToInt(ItemStack::func_190916_E).sum();
/* 147 */     if (this.holdingGapple) {
/* 148 */       this.gapples += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_151153_ao)).mapToInt(ItemStack::func_190916_E).sum();
/*     */     }
/* 150 */     doSwitch();
/*     */   }
/*     */   public void doSwitch() {
/*     */     int lastSlot;
/* 154 */     this.currentMode = Mode2.TOTEMS;
/* 155 */     if (((Boolean)this.gapple.getValue()).booleanValue() && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemSword && mc.field_71474_y.field_74313_G.func_151470_d()) {
/* 156 */       this.currentMode = Mode2.GAPPLES;
/* 157 */     } else if (this.currentMode != Mode2.CRYSTALS && ((Boolean)this.crystal.getValue()).booleanValue() && ((EntityUtil.isSafe((Entity)mc.field_71439_g) && EntityUtil.getHealth((Entity)mc.field_71439_g, true) > ((Float)this.crystalHoleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, true) > ((Float)this.crystalHealth.getValue()).floatValue())) {
/* 158 */       this.currentMode = Mode2.CRYSTALS;
/*     */     } 
/* 160 */     if (((Boolean)this.antiGappleFail.getValue()).booleanValue() && 
/* 161 */       this.currentMode == Mode2.GAPPLES && ((!EntityUtil.isSafe((Entity)mc.field_71439_g) && EntityUtil.getHealth((Entity)mc.field_71439_g, true) <= ((Float)this.crystalHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, true) <= ((Float)this.crystalHoleHealth.getValue()).floatValue())) {
/* 162 */       this.switchedForHealthReason = true;
/* 163 */       setMode(Mode2.TOTEMS);
/*     */     } 
/*     */     
/* 166 */     if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
/* 167 */       setMode(Mode2.TOTEMS);
/*     */     }
/* 169 */     if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity)mc.field_71439_g) && EntityUtil.getHealth((Entity)mc.field_71439_g, true) <= ((Float)this.crystalHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, true) <= ((Float)this.crystalHoleHealth.getValue()).floatValue())) {
/* 170 */       if (this.currentMode == Mode2.CRYSTALS) {
/* 171 */         this.switchedForHealthReason = true;
/*     */       }
/* 173 */       setMode(Mode2.TOTEMS);
/*     */     } 
/* 175 */     if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)mc.field_71439_g) && EntityUtil.getHealth((Entity)mc.field_71439_g, true) > ((Float)this.crystalHoleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.field_71439_g, true) > ((Float)this.crystalHealth.getValue()).floatValue())) {
/* 176 */       setMode(Mode2.CRYSTALS);
/* 177 */       this.switchedForHealthReason = false;
/*     */     } 
/* 179 */     if (this.currentMode == Mode2.CRYSTALS && ((Boolean)this.armorCheck.getValue()).booleanValue() && (mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_190931_a || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.HEAD).func_77973_b() == Items.field_190931_a || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.LEGS).func_77973_b() == Items.field_190931_a || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.FEET).func_77973_b() == Items.field_190931_a)) {
/* 180 */       setMode(Mode2.TOTEMS);
/*     */     }
/* 182 */     if ((this.currentMode == Mode2.CRYSTALS || this.currentMode == Mode2.GAPPLES) && mc.field_71439_g.field_70143_R > ((Float)this.Height.getValue()).floatValue() && ((Boolean)this.fallDistance.getValue()).booleanValue()) {
/* 183 */       setMode(Mode2.TOTEMS);
/*     */     }
/* 185 */     if (mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiContainer && !(mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiInventory)) {
/*     */       return;
/*     */     }
/* 188 */     Item currentOffhandItem = mc.field_71439_g.func_184592_cb().func_77973_b();
/* 189 */     switch (this.currentMode) {
/*     */       case TOTEMS:
/* 191 */         if (this.totems <= 0 || this.holdingTotem)
/* 192 */           break;  this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.field_190929_cY, false);
/* 193 */         lastSlot = getLastSlot(currentOffhandItem, this.lastTotemSlot);
/* 194 */         putItemInOffhand(this.lastTotemSlot, lastSlot);
/*     */         break;
/*     */       
/*     */       case GAPPLES:
/* 198 */         if (this.gapples <= 0 || this.holdingGapple)
/* 199 */           break;  this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.field_151153_ao, false);
/* 200 */         lastSlot = getLastSlot(currentOffhandItem, this.lastGappleSlot);
/* 201 */         putItemInOffhand(this.lastGappleSlot, lastSlot);
/*     */         break;
/*     */       
/*     */       default:
/* 205 */         if (this.crystals <= 0 || this.holdingCrystal)
/* 206 */           break;  this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.field_185158_cP, false);
/* 207 */         lastSlot = getLastSlot(currentOffhandItem, this.lastCrystalSlot);
/* 208 */         putItemInOffhand(this.lastCrystalSlot, lastSlot);
/*     */         break;
/*     */     } 
/* 211 */     for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
/* 212 */       InventoryUtil.Task task = this.taskList.poll();
/* 213 */       if (task != null) {
/* 214 */         task.run();
/* 215 */         if (task.isSwitching())
/* 216 */           this.didSwitchThisTick = true; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private int getLastSlot(Item item, int slotIn) {
/* 221 */     if (item == Items.field_185158_cP) {
/* 222 */       return this.lastCrystalSlot;
/*     */     }
/* 224 */     if (item == Items.field_151153_ao) {
/* 225 */       return this.lastGappleSlot;
/*     */     }
/* 227 */     if (item == Items.field_190929_cY) {
/* 228 */       return this.lastTotemSlot;
/*     */     }
/* 230 */     if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
/* 231 */       return this.lastObbySlot;
/*     */     }
/* 233 */     if (InventoryUtil.isBlock(item, BlockWeb.class)) {
/* 234 */       return this.lastWebSlot;
/*     */     }
/* 236 */     if (item == Items.field_190931_a) {
/* 237 */       return -1;
/*     */     }
/* 239 */     return slotIn;
/*     */   }
/*     */   
/*     */   private void putItemInOffhand(int slotIn, int slotOut) {
/* 243 */     if (slotIn != -1 && this.taskList.isEmpty()) {
/* 244 */       this.taskList.add(new InventoryUtil.Task(slotIn));
/* 245 */       this.taskList.add(new InventoryUtil.Task(45));
/* 246 */       this.taskList.add(new InventoryUtil.Task(slotOut));
/* 247 */       this.taskList.add(new InventoryUtil.Task());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setMode(Mode2 mode) {
/* 252 */     this.currentMode = (this.currentMode == mode) ? Mode2.TOTEMS : mode;
/*     */   }
/*     */   
/*     */   public enum Mode2 {
/* 256 */     TOTEMS,
/* 257 */     GAPPLES,
/* 258 */     CRYSTALS;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\OffhandRewrite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */