/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.inventory.EntityEquipmentSlot;
/*     */ import net.minecraft.item.ItemExpBottle;
/*     */ import net.minecraft.item.ItemStack;
/*     */ 
/*     */ public class AutoArmor extends Module {
/*  20 */   private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
/*  21 */   private final Setting<Boolean> curse = register(new Setting("Vanishing", Boolean.valueOf(false)));
/*  22 */   private final Setting<Boolean> mendingTakeOff = register(new Setting("AutoMend", Boolean.valueOf(false)));
/*  23 */   private final Setting<Integer> closestEnemy = register(new Setting("Enemy", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
/*  24 */   private final Setting<Integer> repair = register(new Setting("Repair%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
/*  25 */   private final Setting<Integer> actions = register(new Setting("Packets", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12)));
/*  26 */   private final Timer timer = new Timer();
/*  27 */   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
/*  28 */   private final List<Integer> doneSlots = new ArrayList<>();
/*     */   boolean flag;
/*     */   
/*     */   public AutoArmor() {
/*  32 */     super("AutoArmor", "Puts Armor on for you.", Module.Category.COMBAT, true, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLogin() {
/*  37 */     this.timer.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  42 */     this.taskList.clear();
/*  43 */     this.doneSlots.clear();
/*  44 */     this.flag = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLogout() {
/*  49 */     this.taskList.clear();
/*  50 */     this.doneSlots.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  55 */     if (fullNullCheck() || (mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiContainer && !(mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiInventory))) {
/*     */       return;
/*     */     }
/*  58 */     if (this.taskList.isEmpty()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  66 */       if (((Boolean)this.mendingTakeOff.getValue()).booleanValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && mc.field_71474_y.field_74313_G.func_151470_d() && mc.field_71441_e.field_73010_i.stream().noneMatch(e -> (e != mc.field_71439_g && !Banzem.friendManager.isFriend(e.func_70005_c_()) && mc.field_71439_g.func_70032_d((Entity)e) <= ((Integer)this.closestEnemy.getValue()).intValue())) && !this.flag) {
/*     */ 
/*     */         
/*  69 */         int takeOff = 0;
/*  70 */         for (Map.Entry<Integer, ItemStack> armorSlot : getArmor().entrySet()) {
/*  71 */           ItemStack stack = armorSlot.getValue();
/*  72 */           float percent = ((Integer)this.repair.getValue()).intValue() / 100.0F;
/*  73 */           int dam = Math.round(stack.func_77958_k() * percent); int goods;
/*  74 */           if (dam >= (goods = stack.func_77958_k() - stack.func_77952_i()))
/*  75 */             continue;  takeOff++;
/*     */         } 
/*  77 */         if (takeOff == 4) {
/*  78 */           this.flag = true;
/*     */         }
/*  80 */         if (!this.flag) {
/*  81 */           ItemStack itemStack1 = mc.field_71439_g.field_71069_bz.func_75139_a(5).func_75211_c();
/*  82 */           if (!itemStack1.field_190928_g) {
/*     */             
/*  84 */             float f = ((Integer)this.repair.getValue()).intValue() / 100.0F;
/*  85 */             int dam2 = Math.round(itemStack1.func_77958_k() * f); int goods2;
/*  86 */             if (dam2 < (goods2 = itemStack1.func_77958_k() - itemStack1.func_77952_i())) {
/*  87 */               takeOffSlot(5);
/*     */             }
/*     */           } 
/*  90 */           ItemStack itemStack2 = mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c();
/*  91 */           if (!itemStack2.field_190928_g) {
/*     */             
/*  93 */             float f = ((Integer)this.repair.getValue()).intValue() / 100.0F;
/*  94 */             int dam3 = Math.round(itemStack2.func_77958_k() * f); int goods3;
/*  95 */             if (dam3 < (goods3 = itemStack2.func_77958_k() - itemStack2.func_77952_i())) {
/*  96 */               takeOffSlot(6);
/*     */             }
/*     */           } 
/*  99 */           ItemStack itemStack3 = mc.field_71439_g.field_71069_bz.func_75139_a(7).func_75211_c();
/*     */           
/* 101 */           float percent = ((Integer)this.repair.getValue()).intValue() / 100.0F;
/* 102 */           int dam = Math.round(itemStack3.func_77958_k() * percent); int goods;
/* 103 */           if (!itemStack3.field_190928_g && dam < (goods = itemStack3.func_77958_k() - itemStack3.func_77952_i())) {
/* 104 */             takeOffSlot(7);
/*     */           }
/*     */           
/* 107 */           ItemStack itemStack4 = mc.field_71439_g.field_71069_bz.func_75139_a(8).func_75211_c();
/* 108 */           if (!itemStack4.field_190928_g) {
/*     */             
/* 110 */             float f = ((Integer)this.repair.getValue()).intValue() / 100.0F;
/* 111 */             int dam4 = Math.round(itemStack4.func_77958_k() * f); int goods4;
/* 112 */             if (dam4 < (goods4 = itemStack4.func_77958_k() - itemStack4.func_77952_i())) {
/* 113 */               takeOffSlot(8);
/*     */             }
/*     */           } 
/*     */         } 
/*     */         return;
/*     */       } 
/* 119 */       this.flag = false;
/* 120 */       ItemStack helm = mc.field_71439_g.field_71069_bz.func_75139_a(5).func_75211_c(); int slot4;
/* 121 */       if (helm.func_77973_b() == Items.field_190931_a && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1)
/* 122 */         getSlotOn(5, slot4);  int slot3;
/*     */       ItemStack chest;
/* 124 */       if ((chest = mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c()).func_77973_b() == Items.field_190931_a && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1)
/* 125 */         getSlotOn(6, slot3);  int slot2;
/*     */       ItemStack legging;
/* 127 */       if ((legging = mc.field_71439_g.field_71069_bz.func_75139_a(7).func_75211_c()).func_77973_b() == Items.field_190931_a && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1)
/* 128 */         getSlotOn(7, slot2);  int slot;
/*     */       ItemStack feet;
/* 130 */       if ((feet = mc.field_71439_g.field_71069_bz.func_75139_a(8).func_75211_c()).func_77973_b() == Items.field_190931_a && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1) {
/* 131 */         getSlotOn(8, slot);
/*     */       }
/*     */     } 
/* 134 */     if (this.timer.passedMs((int)(((Integer)this.delay.getValue()).intValue() * Banzem.serverManager.getTpsFactor()))) {
/* 135 */       if (!this.taskList.isEmpty())
/* 136 */         for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
/* 137 */           InventoryUtil.Task task = this.taskList.poll();
/* 138 */           if (task != null) {
/* 139 */             task.run();
/*     */           }
/*     */         }  
/* 142 */       this.timer.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void takeOffSlot(int slot) {
/* 147 */     if (this.taskList.isEmpty()) {
/* 148 */       int target = -1;
/* 149 */       for (Iterator<Integer> iterator = InventoryUtil.findEmptySlots(true).iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/* 150 */         if (this.doneSlots.contains(Integer.valueOf(target)))
/* 151 */           continue;  target = i;
/* 152 */         this.doneSlots.add(Integer.valueOf(i)); }
/*     */       
/* 154 */       if (target != -1) {
/* 155 */         this.taskList.add(new InventoryUtil.Task(slot));
/* 156 */         this.taskList.add(new InventoryUtil.Task(target));
/* 157 */         this.taskList.add(new InventoryUtil.Task());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void getSlotOn(int slot, int target) {
/* 163 */     if (this.taskList.isEmpty()) {
/* 164 */       this.doneSlots.remove(Integer.valueOf(target));
/* 165 */       this.taskList.add(new InventoryUtil.Task(target));
/* 166 */       this.taskList.add(new InventoryUtil.Task(slot));
/* 167 */       this.taskList.add(new InventoryUtil.Task());
/*     */     } 
/*     */   }
/*     */   
/*     */   private Map<Integer, ItemStack> getArmor() {
/* 172 */     return getInventorySlots(5, 8);
/*     */   }
/*     */   
/*     */   private Map<Integer, ItemStack> getInventorySlots(int current, int last) {
/* 176 */     HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
/* 177 */     while (current <= last) {
/* 178 */       fullInventorySlots.put(Integer.valueOf(current), mc.field_71439_g.field_71069_bz.func_75138_a().get(current));
/* 179 */       current++;
/*     */     } 
/* 181 */     return fullInventorySlots;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\AutoArmor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */