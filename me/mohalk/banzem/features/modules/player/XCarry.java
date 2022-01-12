/*     */ package me.mohalk.banzem.features.modules.player;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import me.mohalk.banzem.event.events.ClientEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Bind;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.ReflectionUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.gui.GuiScreen;
/*     */ import net.minecraft.client.gui.inventory.GuiInventory;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.inventory.Slot;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketCloseWindow;
/*     */ import net.minecraftforge.client.event.GuiOpenEvent;
/*     */ import net.minecraftforge.fml.common.eventhandler.EventPriority;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import net.minecraftforge.fml.common.gameevent.InputEvent;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ import org.lwjgl.input.Mouse;
/*     */ 
/*     */ public class XCarry extends Module {
/*  32 */   private static XCarry INSTANCE = new XCarry();
/*  33 */   private final Setting<Boolean> simpleMode = register(new Setting("Simple", Boolean.valueOf(false)));
/*  34 */   private final Setting<Bind> autoStore = register(new Setting("AutoDuel", new Bind(-1)));
/*  35 */   private final Setting<Integer> obbySlot = register(new Setting("ObbySlot", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(9), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
/*  36 */   private final Setting<Integer> slot1 = register(new Setting("Slot1", Integer.valueOf(22), Integer.valueOf(9), Integer.valueOf(44), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
/*  37 */   private final Setting<Integer> slot2 = register(new Setting("Slot2", Integer.valueOf(23), Integer.valueOf(9), Integer.valueOf(44), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
/*  38 */   private final Setting<Integer> slot3 = register(new Setting("Slot3", Integer.valueOf(24), Integer.valueOf(9), Integer.valueOf(44), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
/*  39 */   private final Setting<Integer> tasks = register(new Setting("Actions", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
/*  40 */   private final Setting<Boolean> store = register(new Setting("Store", Boolean.valueOf(false)));
/*  41 */   private final Setting<Boolean> shiftClicker = register(new Setting("ShiftClick", Boolean.valueOf(false)));
/*  42 */   private final Setting<Boolean> withShift = register(new Setting("WithShift", Boolean.valueOf(true), v -> ((Boolean)this.shiftClicker.getValue()).booleanValue()));
/*  43 */   private final Setting<Bind> keyBind = register(new Setting("ShiftBind", new Bind(-1), v -> ((Boolean)this.shiftClicker.getValue()).booleanValue()));
/*  44 */   private final AtomicBoolean guiNeedsClose = new AtomicBoolean(false);
/*  45 */   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
/*  46 */   private GuiInventory openedGui = null;
/*     */   private boolean guiCloseGuard = false;
/*     */   private boolean autoDuelOn = false;
/*     */   private boolean obbySlotDone = false;
/*     */   private boolean slot1done = false;
/*     */   private boolean slot2done = false;
/*     */   private boolean slot3done = false;
/*  53 */   private List<Integer> doneSlots = new ArrayList<>();
/*     */   
/*     */   public XCarry() {
/*  56 */     super("XCarry", "Uses the crafting inventory for storage", Module.Category.PLAYER, true, false, false);
/*  57 */     setInstance();
/*     */   }
/*     */   
/*     */   public static XCarry getInstance() {
/*  61 */     if (INSTANCE == null) {
/*  62 */       INSTANCE = new XCarry();
/*     */     }
/*  64 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  68 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  73 */     if (((Boolean)this.shiftClicker.getValue()).booleanValue() && mc.field_71462_r instanceof GuiInventory) {
/*     */ 
/*     */       
/*  76 */       boolean ourBind = (((Bind)this.keyBind.getValue()).getKey() != -1 && Keyboard.isKeyDown(((Bind)this.keyBind.getValue()).getKey()) && !Keyboard.isKeyDown(42)), bl = ourBind; Slot slot;
/*  77 */       if (((Keyboard.isKeyDown(42) && ((Boolean)this.withShift.getValue()).booleanValue()) || ourBind) && Mouse.isButtonDown(0) && (slot = ((GuiInventory)mc.field_71462_r).getSlotUnderMouse()) != null && InventoryUtil.getEmptyXCarry() != -1) {
/*  78 */         int slotNumber = slot.field_75222_d;
/*  79 */         if (slotNumber > 4 && ourBind) {
/*  80 */           this.taskList.add(new InventoryUtil.Task(slotNumber));
/*  81 */           this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
/*  82 */         } else if (slotNumber > 4 && ((Boolean)this.withShift.getValue()).booleanValue()) {
/*  83 */           boolean isHotBarFull = true;
/*  84 */           boolean isInvFull = true;
/*  85 */           for (Iterator<Integer> iterator = InventoryUtil.findEmptySlots(false).iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/*  86 */             if (i > 4 && i < 36) {
/*  87 */               isInvFull = false;
/*     */               continue;
/*     */             } 
/*  90 */             if (i <= 35 || i >= 45)
/*  91 */               continue;  isHotBarFull = false; }
/*     */           
/*  93 */           if (slotNumber > 35 && slotNumber < 45) {
/*  94 */             if (isInvFull) {
/*  95 */               this.taskList.add(new InventoryUtil.Task(slotNumber));
/*  96 */               this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
/*     */             } 
/*  98 */           } else if (isHotBarFull) {
/*  99 */             this.taskList.add(new InventoryUtil.Task(slotNumber));
/* 100 */             this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 105 */     if (this.autoDuelOn) {
/* 106 */       this.doneSlots = new ArrayList<>();
/* 107 */       if (InventoryUtil.getEmptyXCarry() == -1 || (this.obbySlotDone && this.slot1done && this.slot2done && this.slot3done)) {
/* 108 */         this.autoDuelOn = false;
/*     */       }
/* 110 */       if (this.autoDuelOn) {
/* 111 */         if (!this.obbySlotDone && !(mc.field_71439_g.field_71071_by.func_70301_a(((Integer)this.obbySlot.getValue()).intValue() - 1)).field_190928_g) {
/* 112 */           addTasks(36 + ((Integer)this.obbySlot.getValue()).intValue() - 1);
/*     */         }
/* 114 */         this.obbySlotDone = true;
/* 115 */         if (!this.slot1done && !(((Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(((Integer)this.slot1.getValue()).intValue())).func_75211_c()).field_190928_g) {
/* 116 */           addTasks(((Integer)this.slot1.getValue()).intValue());
/*     */         }
/* 118 */         this.slot1done = true;
/* 119 */         if (!this.slot2done && !(((Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(((Integer)this.slot2.getValue()).intValue())).func_75211_c()).field_190928_g) {
/* 120 */           addTasks(((Integer)this.slot2.getValue()).intValue());
/*     */         }
/* 122 */         this.slot2done = true;
/* 123 */         if (!this.slot3done && !(((Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(((Integer)this.slot3.getValue()).intValue())).func_75211_c()).field_190928_g) {
/* 124 */           addTasks(((Integer)this.slot3.getValue()).intValue());
/*     */         }
/* 126 */         this.slot3done = true;
/*     */       } 
/*     */     } else {
/* 129 */       this.obbySlotDone = false;
/* 130 */       this.slot1done = false;
/* 131 */       this.slot2done = false;
/* 132 */       this.slot3done = false;
/*     */     } 
/* 134 */     if (!this.taskList.isEmpty())
/* 135 */       for (int i = 0; i < ((Integer)this.tasks.getValue()).intValue(); i++) {
/* 136 */         InventoryUtil.Task task = this.taskList.poll();
/* 137 */         if (task != null) {
/* 138 */           task.run();
/*     */         }
/*     */       }  
/*     */   }
/*     */   
/*     */   private void addTasks(int slot) {
/* 144 */     if (InventoryUtil.getEmptyXCarry() != -1) {
/* 145 */       int xcarrySlot = InventoryUtil.getEmptyXCarry();
/* 146 */       if ((this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) && (this.doneSlots.contains(Integer.valueOf(++xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) && (this.doneSlots.contains(Integer.valueOf(++xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) && (this.doneSlots.contains(Integer.valueOf(++xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot))) {
/*     */         return;
/*     */       }
/* 149 */       if (xcarrySlot > 4) {
/*     */         return;
/*     */       }
/* 152 */       this.doneSlots.add(Integer.valueOf(xcarrySlot));
/* 153 */       this.taskList.add(new InventoryUtil.Task(slot));
/* 154 */       this.taskList.add(new InventoryUtil.Task(xcarrySlot));
/* 155 */       this.taskList.add(new InventoryUtil.Task());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 161 */     if (!fullNullCheck()) {
/* 162 */       if (!((Boolean)this.simpleMode.getValue()).booleanValue()) {
/* 163 */         closeGui();
/* 164 */         close();
/*     */       } else {
/* 166 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketCloseWindow(mc.field_71439_g.field_71069_bz.field_75152_c));
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLogout() {
/* 173 */     onDisable();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onCloseGuiScreen(PacketEvent.Send event) {
/* 178 */     if (((Boolean)this.simpleMode.getValue()).booleanValue() && event.getPacket() instanceof CPacketCloseWindow) {
/* 179 */       CPacketCloseWindow packet = (CPacketCloseWindow)event.getPacket();
/* 180 */       if (packet.field_149556_a == mc.field_71439_g.field_71069_bz.field_75152_c) {
/* 181 */         event.setCanceled(true);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.LOWEST)
/*     */   public void onGuiOpen(GuiOpenEvent event) {
/* 188 */     if (!((Boolean)this.simpleMode.getValue()).booleanValue()) {
/* 189 */       if (this.guiCloseGuard) {
/* 190 */         event.setCanceled(true);
/* 191 */       } else if (event.getGui() instanceof GuiInventory) {
/* 192 */         this.openedGui = createGuiWrapper((GuiInventory)event.getGui());
/* 193 */         event.setGui((GuiScreen)this.openedGui);
/* 194 */         this.guiNeedsClose.set(false);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSettingChange(ClientEvent event) {
/* 201 */     if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
/* 202 */       Setting setting = event.getSetting();
/* 203 */       String settingname = event.getSetting().getName();
/* 204 */       if (setting.equals(this.simpleMode) && setting.getPlannedValue() != setting.getValue()) {
/* 205 */         disable();
/* 206 */       } else if (settingname.equalsIgnoreCase("Store")) {
/* 207 */         event.setCanceled(true);
/* 208 */         this.autoDuelOn = !this.autoDuelOn;
/* 209 */         Command.sendMessage("<XCarry> §aAutostoring...");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onKeyInput(InputEvent.KeyInputEvent event) {
/* 216 */     if (Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof me.mohalk.banzem.features.gui.PhobosGui) && ((Bind)this.autoStore.getValue()).getKey() == Keyboard.getEventKey()) {
/* 217 */       this.autoDuelOn = !this.autoDuelOn;
/* 218 */       Command.sendMessage("<XCarry> §aAutostoring...");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void close() {
/* 223 */     this.openedGui = null;
/* 224 */     this.guiNeedsClose.set(false);
/* 225 */     this.guiCloseGuard = false;
/*     */   }
/*     */   
/*     */   private void closeGui() {
/* 229 */     if (this.guiNeedsClose.compareAndSet(true, false) && !fullNullCheck()) {
/* 230 */       this.guiCloseGuard = true;
/* 231 */       mc.field_71439_g.func_71053_j();
/* 232 */       if (this.openedGui != null) {
/* 233 */         this.openedGui.func_146281_b();
/* 234 */         this.openedGui = null;
/*     */       } 
/* 236 */       this.guiCloseGuard = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private GuiInventory createGuiWrapper(GuiInventory gui) {
/*     */     try {
/* 242 */       GuiInventoryWrapper wrapper = new GuiInventoryWrapper();
/* 243 */       ReflectionUtil.copyOf(gui, wrapper);
/* 244 */       return wrapper;
/* 245 */     } catch (IllegalAccessException|NoSuchFieldException e) {
/* 246 */       e.printStackTrace();
/* 247 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private class GuiInventoryWrapper
/*     */     extends GuiInventory {
/*     */     GuiInventoryWrapper() {
/* 254 */       super((EntityPlayer)Util.mc.field_71439_g);
/*     */     }
/*     */     
/*     */     protected void func_73869_a(char typedChar, int keyCode) throws IOException {
/* 258 */       if (XCarry.this.isEnabled() && (keyCode == 1 || this.field_146297_k.field_71474_y.field_151445_Q.isActiveAndMatches(keyCode))) {
/* 259 */         XCarry.this.guiNeedsClose.set(true);
/* 260 */         this.field_146297_k.func_147108_a(null);
/*     */       } else {
/* 262 */         super.func_73869_a(typedChar, keyCode);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void func_146281_b() {
/* 267 */       if (XCarry.this.guiCloseGuard || !XCarry.this.isEnabled())
/* 268 */         super.func_146281_b(); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\XCarry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */