/*     */ package me.mohalk.banzem.util;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Enchantments;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.inventory.ClickType;
/*     */ import net.minecraft.inventory.EntityEquipmentSlot;
/*     */ import net.minecraft.inventory.Slot;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemArmor;
/*     */ import net.minecraft.item.ItemBlock;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketHeldItemChange;
/*     */ 
/*     */ public class InventoryUtil implements Util {
/*     */   public static void switchToHotbarSlot(int slot, boolean silent) {
/*  26 */     if (mc.field_71439_g.field_71071_by.field_70461_c == slot || slot < 0) {
/*     */       return;
/*     */     }
/*  29 */     if (silent) {
/*  30 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
/*  31 */       mc.field_71442_b.func_78765_e();
/*     */     } else {
/*  33 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
/*  34 */       mc.field_71439_g.field_71071_by.field_70461_c = slot;
/*  35 */       mc.field_71442_b.func_78765_e();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void switchToHotbarSlot(Class clazz, boolean silent) {
/*  40 */     int slot = findHotbarBlock(clazz);
/*  41 */     if (slot > -1) {
/*  42 */       switchToHotbarSlot(slot, silent);
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isNull(ItemStack stack) {
/*  47 */     return (stack == null || stack.func_77973_b() instanceof net.minecraft.item.ItemAir);
/*     */   }
/*     */   
/*     */   public static int findHotbarBlock(Class clazz) {
/*  51 */     for (int i = 0; i < 9; i++) {
/*     */       
/*  53 */       ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
/*  54 */       if (stack != ItemStack.field_190927_a) {
/*  55 */         if (clazz.isInstance(stack.func_77973_b()))
/*  56 */           return i; 
/*     */         Block block;
/*  58 */         if (stack.func_77973_b() instanceof ItemBlock && clazz.isInstance(block = ((ItemBlock)stack.func_77973_b()).func_179223_d()))
/*     */         {
/*  60 */           return i; } 
/*     */       } 
/*  62 */     }  return -1;
/*     */   }
/*     */   
/*     */   public static int findHotbarBlock(Block blockIn) {
/*  66 */     for (int i = 0; i < 9; ) {
/*     */       
/*  68 */       ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i); Block block;
/*  69 */       if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || (block = ((ItemBlock)stack.func_77973_b()).func_179223_d()) != blockIn) {
/*     */         i++; continue;
/*  71 */       }  return i;
/*     */     } 
/*  73 */     return -1;
/*     */   }
/*     */   
/*     */   public static int getItemHotbar(Item input) {
/*  77 */     for (int i = 0; i < 9; ) {
/*  78 */       Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
/*  79 */       if (Item.func_150891_b(item) != Item.func_150891_b(input)) { i++; continue; }
/*  80 */        return i;
/*     */     } 
/*  82 */     return -1;
/*     */   }
/*     */   
/*     */   public static int findStackInventory(Item input) {
/*  86 */     return findStackInventory(input, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int findStackInventory(Item input, boolean withHotbar) {
/*  91 */     int i = withHotbar ? 0 : 9, n = i;
/*  92 */     while (i < 36) {
/*  93 */       Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
/*  94 */       if (Item.func_150891_b(input) == Item.func_150891_b(item)) {
/*  95 */         return i + ((i < 9) ? 36 : 0);
/*     */       }
/*  97 */       i++;
/*     */     } 
/*  99 */     return -1;
/*     */   }
/*     */   
/*     */   public static int findItemInventorySlot(Item item, boolean offHand) {
/* 103 */     AtomicInteger slot = new AtomicInteger();
/* 104 */     slot.set(-1);
/* 105 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 106 */       if (((ItemStack)entry.getValue()).func_77973_b() != item || (((Integer)entry.getKey()).intValue() == 45 && !offHand))
/* 107 */         continue;  slot.set(((Integer)entry.getKey()).intValue());
/* 108 */       return slot.get();
/*     */     } 
/* 110 */     return slot.get();
/*     */   }
/*     */   
/*     */   public static List<Integer> findEmptySlots(boolean withXCarry) {
/* 114 */     ArrayList<Integer> outPut = new ArrayList<>();
/* 115 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 116 */       if (!((ItemStack)entry.getValue()).field_190928_g && ((ItemStack)entry.getValue()).func_77973_b() != Items.field_190931_a)
/* 117 */         continue;  outPut.add(entry.getKey());
/*     */     } 
/* 119 */     if (withXCarry)
/* 120 */       for (int i = 1; i < 5; i++) {
/* 121 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 122 */         ItemStack craftingStack = craftingSlot.func_75211_c();
/* 123 */         if (craftingStack.func_190926_b() || craftingStack.func_77973_b() == Items.field_190931_a) {
/* 124 */           outPut.add(Integer.valueOf(i));
/*     */         }
/*     */       }  
/* 127 */     return outPut;
/*     */   }
/*     */   
/*     */   public static int findInventoryBlock(Class clazz, boolean offHand) {
/* 131 */     AtomicInteger slot = new AtomicInteger();
/* 132 */     slot.set(-1);
/* 133 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 134 */       if (!isBlock(((ItemStack)entry.getValue()).func_77973_b(), clazz) || (((Integer)entry.getKey()).intValue() == 45 && !offHand))
/* 135 */         continue;  slot.set(((Integer)entry.getKey()).intValue());
/* 136 */       return slot.get();
/*     */     } 
/* 138 */     return slot.get();
/*     */   }
/*     */   
/*     */   public static int findInventoryWool(boolean offHand) {
/* 142 */     AtomicInteger slot = new AtomicInteger();
/* 143 */     slot.set(-1);
/* 144 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 145 */       if (!(((ItemStack)entry.getValue()).func_77973_b() instanceof ItemBlock))
/* 146 */         continue;  ItemBlock wool = (ItemBlock)((ItemStack)entry.getValue()).func_77973_b();
/* 147 */       if ((wool.func_179223_d()).field_149764_J != Material.field_151580_n || (((Integer)entry.getKey()).intValue() == 45 && !offHand))
/* 148 */         continue;  slot.set(((Integer)entry.getKey()).intValue());
/* 149 */       return slot.get();
/*     */     } 
/* 151 */     return slot.get();
/*     */   }
/*     */   
/*     */   public static int findEmptySlot() {
/* 155 */     AtomicInteger slot = new AtomicInteger();
/* 156 */     slot.set(-1);
/* 157 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 158 */       if (!((ItemStack)entry.getValue()).func_190926_b())
/* 159 */         continue;  slot.set(((Integer)entry.getKey()).intValue());
/* 160 */       return slot.get();
/*     */     } 
/* 162 */     return slot.get();
/*     */   }
/*     */   
/*     */   public static boolean isBlock(Item item, Class clazz) {
/* 166 */     if (item instanceof ItemBlock) {
/* 167 */       Block block = ((ItemBlock)item).func_179223_d();
/* 168 */       return clazz.isInstance(block);
/*     */     } 
/* 170 */     return false;
/*     */   }
/*     */   
/*     */   public static void confirmSlot(int slot) {
/* 174 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
/* 175 */     mc.field_71439_g.field_71071_by.field_70461_c = slot;
/* 176 */     mc.field_71442_b.func_78765_e();
/*     */   }
/*     */   
/*     */   public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
/* 180 */     if (mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiCrafting) {
/* 181 */       return fuckYou3arthqu4kev2(10, 45);
/*     */     }
/* 183 */     return getInventorySlots(9, 44);
/*     */   }
/*     */   
/*     */   private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
/* 187 */     HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
/* 188 */     for (int current = currentI; current <= last; current++) {
/* 189 */       fullInventorySlots.put(Integer.valueOf(current), mc.field_71439_g.field_71069_bz.func_75138_a().get(current));
/*     */     }
/* 191 */     return fullInventorySlots;
/*     */   }
/*     */   
/*     */   private static Map<Integer, ItemStack> fuckYou3arthqu4kev2(int currentI, int last) {
/* 195 */     HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
/* 196 */     for (int current = currentI; current <= last; current++) {
/* 197 */       fullInventorySlots.put(Integer.valueOf(current), mc.field_71439_g.field_71070_bA.func_75138_a().get(current));
/*     */     }
/* 199 */     return fullInventorySlots;
/*     */   }
/*     */   
/*     */   public static boolean[] switchItem(boolean back, int lastHotbarSlot, boolean switchedItem, Switch mode, Class clazz) {
/* 203 */     boolean[] switchedItemSwitched = { switchedItem, false };
/* 204 */     switch (mode) {
/*     */       case NORMAL:
/* 206 */         if (!back && !switchedItem) {
/* 207 */           switchToHotbarSlot(findHotbarBlock(clazz), false);
/* 208 */           switchedItemSwitched[0] = true;
/* 209 */         } else if (back && switchedItem) {
/* 210 */           switchToHotbarSlot(lastHotbarSlot, false);
/* 211 */           switchedItemSwitched[0] = false;
/*     */         } 
/* 213 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case SILENT:
/* 217 */         if (!back && !switchedItem) {
/* 218 */           switchToHotbarSlot(findHotbarBlock(clazz), true);
/* 219 */           switchedItemSwitched[0] = true;
/* 220 */         } else if (back && switchedItem) {
/* 221 */           switchedItemSwitched[0] = false;
/* 222 */           Banzem.inventoryManager.recoverSilent(lastHotbarSlot);
/*     */         } 
/* 224 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case NONE:
/* 228 */         switchedItemSwitched[1] = (back || mc.field_71439_g.field_71071_by.field_70461_c == findHotbarBlock(clazz));
/*     */         break;
/*     */     } 
/* 231 */     return switchedItemSwitched;
/*     */   }
/*     */   
/*     */   public static boolean[] switchItemToItem(boolean back, int lastHotbarSlot, boolean switchedItem, Switch mode, Item item) {
/* 235 */     boolean[] switchedItemSwitched = { switchedItem, false };
/* 236 */     switch (mode) {
/*     */       case NORMAL:
/* 238 */         if (!back && !switchedItem) {
/* 239 */           switchToHotbarSlot(getItemHotbar(item), false);
/* 240 */           switchedItemSwitched[0] = true;
/* 241 */         } else if (back && switchedItem) {
/* 242 */           switchToHotbarSlot(lastHotbarSlot, false);
/* 243 */           switchedItemSwitched[0] = false;
/*     */         } 
/* 245 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case SILENT:
/* 249 */         if (!back && !switchedItem) {
/* 250 */           switchToHotbarSlot(getItemHotbar(item), true);
/* 251 */           switchedItemSwitched[0] = true;
/* 252 */         } else if (back && switchedItem) {
/* 253 */           switchedItemSwitched[0] = false;
/* 254 */           Banzem.inventoryManager.recoverSilent(lastHotbarSlot);
/*     */         } 
/* 256 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case NONE:
/* 260 */         switchedItemSwitched[1] = (back || mc.field_71439_g.field_71071_by.field_70461_c == getItemHotbar(item));
/*     */         break;
/*     */     } 
/* 263 */     return switchedItemSwitched;
/*     */   }
/*     */   
/*     */   public static boolean holdingItem(Class clazz) {
/* 267 */     boolean result = false;
/* 268 */     ItemStack stack = mc.field_71439_g.func_184614_ca();
/* 269 */     result = isInstanceOf(stack, clazz);
/* 270 */     if (!result) {
/* 271 */       ItemStack offhand = mc.field_71439_g.func_184592_cb();
/* 272 */       result = isInstanceOf(stack, clazz);
/*     */     } 
/* 274 */     return result;
/*     */   }
/*     */   
/*     */   public static boolean isInstanceOf(ItemStack stack, Class clazz) {
/* 278 */     if (stack == null) {
/* 279 */       return false;
/*     */     }
/* 281 */     Item item = stack.func_77973_b();
/* 282 */     if (clazz.isInstance(item)) {
/* 283 */       return true;
/*     */     }
/* 285 */     if (item instanceof ItemBlock) {
/* 286 */       Block block = Block.func_149634_a(item);
/* 287 */       return clazz.isInstance(block);
/*     */     } 
/* 289 */     return false;
/*     */   }
/*     */   
/*     */   public static int getEmptyXCarry() {
/* 293 */     for (int i = 1; i < 5; ) {
/* 294 */       Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 295 */       ItemStack craftingStack = craftingSlot.func_75211_c();
/* 296 */       if (!craftingStack.func_190926_b() && craftingStack.func_77973_b() != Items.field_190931_a) { i++; continue; }
/* 297 */        return i;
/*     */     } 
/* 299 */     return -1;
/*     */   }
/*     */   
/*     */   public static boolean isSlotEmpty(int i) {
/* 303 */     Slot slot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 304 */     ItemStack stack = slot.func_75211_c();
/* 305 */     return stack.func_190926_b();
/*     */   }
/*     */   
/*     */   public static int convertHotbarToInv(int input) {
/* 309 */     return 36 + input;
/*     */   }
/*     */   
/*     */   public static boolean areStacksCompatible(ItemStack stack1, ItemStack stack2) {
/* 313 */     if (!stack1.func_77973_b().equals(stack2.func_77973_b())) {
/* 314 */       return false;
/*     */     }
/* 316 */     if (stack1.func_77973_b() instanceof ItemBlock && stack2.func_77973_b() instanceof ItemBlock) {
/* 317 */       Block block1 = ((ItemBlock)stack1.func_77973_b()).func_179223_d();
/* 318 */       Block block2 = ((ItemBlock)stack2.func_77973_b()).func_179223_d();
/* 319 */       if (!block1.field_149764_J.equals(block2.field_149764_J)) {
/* 320 */         return false;
/*     */       }
/*     */     } 
/* 323 */     if (!stack1.func_82833_r().equals(stack2.func_82833_r())) {
/* 324 */       return false;
/*     */     }
/* 326 */     return (stack1.func_77952_i() == stack2.func_77952_i());
/*     */   }
/*     */   
/*     */   public static EntityEquipmentSlot getEquipmentFromSlot(int slot) {
/* 330 */     if (slot == 5) {
/* 331 */       return EntityEquipmentSlot.HEAD;
/*     */     }
/* 333 */     if (slot == 6) {
/* 334 */       return EntityEquipmentSlot.CHEST;
/*     */     }
/* 336 */     if (slot == 7) {
/* 337 */       return EntityEquipmentSlot.LEGS;
/*     */     }
/* 339 */     return EntityEquipmentSlot.FEET;
/*     */   }
/*     */   
/*     */   public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
/* 343 */     int slot = -1;
/* 344 */     float damage = 0.0F;
/* 345 */     for (int i = 9; i < 45; i++) {
/*     */       
/* 347 */       ItemStack s = (Minecraft.func_71410_x()).field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
/* 348 */       if (s.func_77973_b() != Items.field_190931_a && s.func_77973_b() instanceof ItemArmor) {
/* 349 */         ItemArmor armor = (ItemArmor)s.func_77973_b();
/* 350 */         if (armor.field_77881_a == type)
/* 351 */         { float currentDamage = (armor.field_77879_b + EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, s));
/* 352 */           boolean cursed = (binding && EnchantmentHelper.func_190938_b(s)), bl = cursed;
/* 353 */           if (currentDamage > damage && !cursed)
/* 354 */           { damage = currentDamage;
/* 355 */             slot = i; }  } 
/*     */       } 
/* 357 */     }  return slot;
/*     */   }
/*     */   
/*     */   public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
/* 361 */     int slot = findArmorSlot(type, binding);
/* 362 */     if (slot == -1 && withXCarry) {
/* 363 */       float damage = 0.0F;
/* 364 */       for (int i = 1; i < 5; i++) {
/*     */         
/* 366 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 367 */         ItemStack craftingStack = craftingSlot.func_75211_c();
/* 368 */         if (craftingStack.func_77973_b() != Items.field_190931_a && craftingStack.func_77973_b() instanceof ItemArmor) {
/* 369 */           ItemArmor armor = (ItemArmor)craftingStack.func_77973_b();
/* 370 */           if (armor.field_77881_a == type)
/* 371 */           { float currentDamage = (armor.field_77879_b + EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, craftingStack));
/* 372 */             boolean cursed = (binding && EnchantmentHelper.func_190938_b(craftingStack)), bl = cursed;
/* 373 */             if (currentDamage > damage && !cursed)
/* 374 */             { damage = currentDamage;
/* 375 */               slot = i; }  } 
/*     */         } 
/*     */       } 
/* 378 */     }  return slot;
/*     */   }
/*     */   
/*     */   public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
/* 382 */     int slot = findItemInventorySlot(item, offHand);
/* 383 */     if (slot == -1 && withXCarry)
/* 384 */       for (int i = 1; i < 5; i++) {
/*     */         
/* 386 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 387 */         ItemStack craftingStack = craftingSlot.func_75211_c(); Item craftingStackItem;
/* 388 */         if (craftingStack.func_77973_b() != Items.field_190931_a && (craftingStackItem = craftingStack.func_77973_b()) == item)
/*     */         {
/* 390 */           slot = i;
/*     */         }
/*     */       }  
/* 393 */     return slot;
/*     */   }
/*     */   
/*     */   public static int findBlockSlotInventory(Class clazz, boolean offHand, boolean withXCarry) {
/* 397 */     int slot = findInventoryBlock(clazz, offHand);
/* 398 */     if (slot == -1 && withXCarry)
/* 399 */       for (int i = 1; i < 5; i++) {
/*     */         
/* 401 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 402 */         ItemStack craftingStack = craftingSlot.func_75211_c();
/* 403 */         if (craftingStack.func_77973_b() != Items.field_190931_a) {
/* 404 */           Item craftingStackItem = craftingStack.func_77973_b();
/* 405 */           if (clazz.isInstance(craftingStackItem)) {
/* 406 */             slot = i;
/*     */           } else {
/*     */             Block block;
/* 409 */             if (craftingStackItem instanceof ItemBlock && clazz.isInstance(block = ((ItemBlock)craftingStackItem).func_179223_d()))
/*     */             {
/* 411 */               slot = i; } 
/*     */           } 
/*     */         } 
/* 414 */       }   return slot;
/*     */   }
/*     */   
/*     */   public enum Switch {
/* 418 */     NORMAL,
/* 419 */     SILENT,
/* 420 */     NONE;
/*     */   }
/*     */   
/*     */   public static class Task
/*     */   {
/*     */     private final int slot;
/*     */     private final boolean update;
/*     */     private final boolean quickClick;
/*     */     
/*     */     public Task() {
/* 430 */       this.update = true;
/* 431 */       this.slot = -1;
/* 432 */       this.quickClick = false;
/*     */     }
/*     */     
/*     */     public Task(int slot) {
/* 436 */       this.slot = slot;
/* 437 */       this.quickClick = false;
/* 438 */       this.update = false;
/*     */     }
/*     */     
/*     */     public Task(int slot, boolean quickClick) {
/* 442 */       this.slot = slot;
/* 443 */       this.quickClick = quickClick;
/* 444 */       this.update = false;
/*     */     }
/*     */     
/*     */     public void run() {
/* 448 */       if (this.update) {
/* 449 */         Util.mc.field_71442_b.func_78765_e();
/*     */       }
/* 451 */       if (this.slot != -1) {
/* 452 */         Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, (EntityPlayer)Util.mc.field_71439_g);
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isSwitching() {
/* 457 */       return !this.update;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\InventoryUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */