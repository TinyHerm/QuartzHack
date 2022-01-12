/*     */ package me.mohalk.banzem.features.modules.player;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.item.ItemStack;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Replenish
/*     */   extends Module
/*     */ {
/*  20 */   private final Setting<Integer> threshold = register(new Setting("Threshold", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(63)));
/*  21 */   private final Setting<Integer> replenishments = register(new Setting("RUpdates", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000)));
/*  22 */   private final Setting<Integer> updates = register(new Setting("HBUpdates", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000)));
/*  23 */   private final Setting<Integer> actions = register(new Setting("Actions", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(30)));
/*  24 */   private final Setting<Boolean> pauseInv = register(new Setting("PauseInv", Boolean.valueOf(true)));
/*  25 */   private final Setting<Boolean> putBack = register(new Setting("PutBack", Boolean.valueOf(true)));
/*  26 */   private final Timer timer = new Timer();
/*  27 */   private final Timer replenishTimer = new Timer();
/*  28 */   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
/*  29 */   private Map<Integer, ItemStack> hotbar = new ConcurrentHashMap<>();
/*     */   
/*     */   public Replenish() {
/*  32 */     super("AutoReplenish", "Replenishes your hotbar", Module.Category.PLAYER, false, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  37 */     if (mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiContainer && (!(mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiInventory) || ((Boolean)this.pauseInv.getValue()).booleanValue())) {
/*     */       return;
/*     */     }
/*  40 */     if (this.timer.passedMs(((Integer)this.updates.getValue()).intValue())) {
/*  41 */       mapHotbar();
/*     */     }
/*  43 */     if (this.replenishTimer.passedMs(((Integer)this.replenishments.getValue()).intValue())) {
/*  44 */       for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
/*  45 */         InventoryUtil.Task task = this.taskList.poll();
/*  46 */         if (task != null)
/*  47 */           task.run(); 
/*     */       } 
/*  49 */       this.replenishTimer.reset();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  55 */     this.hotbar.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLogout() {
/*  60 */     onDisable();
/*     */   }
/*     */   
/*     */   private void mapHotbar() {
/*  64 */     ConcurrentHashMap<Integer, ItemStack> map = new ConcurrentHashMap<>();
/*  65 */     for (int i = 0; i < 9; i++) {
/*  66 */       ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
/*  67 */       map.put(Integer.valueOf(i), stack);
/*     */     } 
/*  69 */     if (this.hotbar.isEmpty()) {
/*  70 */       this.hotbar = map;
/*     */       return;
/*     */     } 
/*  73 */     ConcurrentHashMap<Integer, Integer> fromTo = new ConcurrentHashMap<>();
/*  74 */     for (Map.Entry<Integer, ItemStack> hotbarItem : map.entrySet()) {
/*     */       
/*  76 */       ItemStack stack = (ItemStack)hotbarItem.getValue();
/*  77 */       Integer slotKey = (Integer)hotbarItem.getKey();
/*  78 */       if (slotKey == null || stack == null || (!stack.field_190928_g && stack.func_77973_b() != Items.field_190931_a && (stack.field_77994_a > ((Integer)this.threshold.getValue()).intValue() || stack.field_77994_a >= stack.func_77976_d())))
/*     */         continue; 
/*  80 */       ItemStack previousStack = (ItemStack)hotbarItem.getValue();
/*  81 */       if (stack.field_190928_g || stack.func_77973_b() != Items.field_190931_a)
/*  82 */         previousStack = this.hotbar.get(slotKey); 
/*     */       int replenishSlot;
/*  84 */       if (previousStack == null || previousStack.field_190928_g || previousStack.func_77973_b() == Items.field_190931_a || (replenishSlot = getReplenishSlot(previousStack)) == -1)
/*     */         continue; 
/*  86 */       fromTo.put(Integer.valueOf(replenishSlot), Integer.valueOf(InventoryUtil.convertHotbarToInv(slotKey.intValue())));
/*     */     } 
/*  88 */     if (!fromTo.isEmpty()) {
/*  89 */       for (Map.Entry<Integer, Integer> slotMove : fromTo.entrySet()) {
/*  90 */         this.taskList.add(new InventoryUtil.Task(((Integer)slotMove.getKey()).intValue()));
/*  91 */         this.taskList.add(new InventoryUtil.Task(((Integer)slotMove.getValue()).intValue()));
/*  92 */         this.taskList.add(new InventoryUtil.Task(((Integer)slotMove.getKey()).intValue()));
/*  93 */         this.taskList.add(new InventoryUtil.Task());
/*     */       } 
/*     */     }
/*  96 */     this.hotbar = map;
/*     */   }
/*     */   
/*     */   private int getReplenishSlot(ItemStack stack) {
/* 100 */     AtomicInteger slot = new AtomicInteger();
/* 101 */     slot.set(-1);
/* 102 */     for (Map.Entry<Integer, ItemStack> entry : (Iterable<Map.Entry<Integer, ItemStack>>)InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
/* 103 */       if (((Integer)entry.getKey()).intValue() >= 36 || !InventoryUtil.areStacksCompatible(stack, entry.getValue()))
/* 104 */         continue;  slot.set(((Integer)entry.getKey()).intValue());
/* 105 */       return slot.get();
/*     */     } 
/* 107 */     return slot.get();
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\Replenish.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */