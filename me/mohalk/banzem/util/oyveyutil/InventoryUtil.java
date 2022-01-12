/*     */ package me.mohalk.banzem.util.oyveyutil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
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
/*  24 */     if (mc.field_71439_g.field_71071_by.field_70461_c == slot || slot < 0) {
/*     */       return;
/*     */     }
/*  27 */     if (silent) {
/*  28 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
/*  29 */       mc.field_71442_b.func_78765_e();
/*     */     } else {
/*  31 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
/*  32 */       mc.field_71439_g.field_71071_by.field_70461_c = slot;
/*  33 */       mc.field_71442_b.func_78765_e();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void switchToHotbarSlot(Class clazz, boolean silent) {
/*  38 */     int slot = findHotbarBlock(clazz);
/*  39 */     if (slot > -1) {
/*  40 */       switchToHotbarSlot(slot, silent);
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isNull(ItemStack stack) {
/*  45 */     return (stack == null || stack.func_77973_b() instanceof net.minecraft.item.ItemAir);
/*     */   }
/*     */   
/*     */   public static int findHotbarBlock(Class clazz) {
/*  49 */     for (int i = 0; i < 9; i++) {
/*     */       
/*  51 */       ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
/*  52 */       if (stack != ItemStack.field_190927_a) {
/*  53 */         if (clazz.isInstance(stack.func_77973_b()))
/*  54 */           return i; 
/*     */         Block block;
/*  56 */         if (stack.func_77973_b() instanceof ItemBlock && clazz.isInstance(block = ((ItemBlock)stack.func_77973_b()).func_179223_d()))
/*     */         {
/*  58 */           return i; } 
/*     */       } 
/*  60 */     }  return -1;
/*     */   }
/*     */   
/*     */   public static int findHotbarBlock(Block blockIn) {
/*  64 */     for (int i = 0; i < 9; ) {
/*     */       
/*  66 */       ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i); Block block;
/*  67 */       if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || (block = ((ItemBlock)stack.func_77973_b()).func_179223_d()) != blockIn) {
/*     */         i++; continue;
/*  69 */       }  return i;
/*     */     } 
/*  71 */     return -1;
/*     */   }
/*     */   
/*     */   public static int getItemHotbar(Item input) {
/*  75 */     for (int i = 0; i < 9; ) {
/*  76 */       Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
/*  77 */       if (Item.func_150891_b(item) != Item.func_150891_b(input)) { i++; continue; }
/*  78 */        return i;
/*     */     } 
/*  80 */     return -1;
/*     */   }
/*     */   
/*     */   public static int findStackInventory(Item input) {
/*  84 */     return findStackInventory(input, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int findStackInventory(Item input, boolean withHotbar) {
/*  89 */     int i = withHotbar ? 0 : 9, n = i;
/*  90 */     while (i < 36) {
/*  91 */       Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
/*  92 */       if (Item.func_150891_b(input) == Item.func_150891_b(item)) {
/*  93 */         return i + ((i < 9) ? 36 : 0);
/*     */       }
/*  95 */       i++;
/*     */     } 
/*  97 */     return -1;
/*     */   }
/*     */   
/*     */   public static int findItemInventorySlot(Item item, boolean offHand) {
/* 101 */     AtomicInteger slot = new AtomicInteger();
/* 102 */     slot.set(-1);
/* 103 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 104 */       if (((ItemStack)entry.getValue()).func_77973_b() != item || (((Integer)entry.getKey()).intValue() == 45 && !offHand))
/* 105 */         continue;  slot.set(((Integer)entry.getKey()).intValue());
/* 106 */       return slot.get();
/*     */     } 
/* 108 */     return slot.get();
/*     */   }
/*     */   public static List<Integer> getItemInventory(Item item) {
/* 111 */     List<Integer> ints = new ArrayList<>();
/* 112 */     for (int i = 9; i < 36; i++) {
/*     */       
/* 114 */       Item target = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
/*     */       
/* 116 */       if (item instanceof ItemBlock && ((ItemBlock)item).func_179223_d().equals(item)) ints.add(Integer.valueOf(i));
/*     */     
/*     */     } 
/* 119 */     if (ints.size() == 0) ints.add(Integer.valueOf(-1));
/*     */     
/* 121 */     return ints;
/*     */   }
/*     */   public static List<Integer> findEmptySlots(boolean withXCarry) {
/* 124 */     ArrayList<Integer> outPut = new ArrayList<>();
/* 125 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 126 */       if (!((ItemStack)entry.getValue()).field_190928_g && ((ItemStack)entry.getValue()).func_77973_b() != Items.field_190931_a)
/* 127 */         continue;  outPut.add(entry.getKey());
/*     */     } 
/* 129 */     if (withXCarry)
/* 130 */       for (int i = 1; i < 5; i++) {
/* 131 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 132 */         ItemStack craftingStack = craftingSlot.func_75211_c();
/* 133 */         if (craftingStack.func_190926_b() || craftingStack.func_77973_b() == Items.field_190931_a) {
/* 134 */           outPut.add(Integer.valueOf(i));
/*     */         }
/*     */       }  
/* 137 */     return outPut;
/*     */   }
/*     */   
/*     */   public static int findInventoryBlock(Class clazz, boolean offHand) {
/* 141 */     AtomicInteger slot = new AtomicInteger();
/* 142 */     slot.set(-1);
/* 143 */     for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
/* 144 */       if (!isBlock(((ItemStack)entry.getValue()).func_77973_b(), clazz) || (((Integer)entry.getKey()).intValue() == 45 && !offHand))
/* 145 */         continue;  slot.set(((Integer)entry.getKey()).intValue());
/* 146 */       return slot.get();
/*     */     } 
/* 148 */     return slot.get();
/*     */   }
/*     */   
/*     */   public static boolean isBlock(Item item, Class clazz) {
/* 152 */     if (item instanceof ItemBlock) {
/* 153 */       Block block = ((ItemBlock)item).func_179223_d();
/* 154 */       return clazz.isInstance(block);
/*     */     } 
/* 156 */     return false;
/*     */   }
/*     */   
/*     */   public static void confirmSlot(int slot) {
/* 160 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
/* 161 */     mc.field_71439_g.field_71071_by.field_70461_c = slot;
/* 162 */     mc.field_71442_b.func_78765_e();
/*     */   }
/*     */   
/*     */   public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
/* 166 */     return getInventorySlots(9, 44);
/*     */   }
/*     */   
/*     */   private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
/* 170 */     HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
/* 171 */     for (int current = currentI; current <= last; current++) {
/* 172 */       fullInventorySlots.put(Integer.valueOf(current), mc.field_71439_g.field_71069_bz.func_75138_a().get(current));
/*     */     }
/* 174 */     return fullInventorySlots;
/*     */   }
/*     */   
/*     */   public static boolean[] switchItem(boolean back, int lastHotbarSlot, boolean switchedItem, Switch mode, Class clazz) {
/* 178 */     boolean[] switchedItemSwitched = { switchedItem, false };
/* 179 */     switch (mode) {
/*     */       case NORMAL:
/* 181 */         if (!back && !switchedItem) {
/* 182 */           switchToHotbarSlot(findHotbarBlock(clazz), false);
/* 183 */           switchedItemSwitched[0] = true;
/* 184 */         } else if (back && switchedItem) {
/* 185 */           switchToHotbarSlot(lastHotbarSlot, false);
/* 186 */           switchedItemSwitched[0] = false;
/*     */         } 
/* 188 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case SILENT:
/* 192 */         if (!back && !switchedItem) {
/* 193 */           switchToHotbarSlot(findHotbarBlock(clazz), true);
/* 194 */           switchedItemSwitched[0] = true;
/* 195 */         } else if (back && switchedItem) {
/* 196 */           switchedItemSwitched[0] = false;
/* 197 */           Banzem.inventoryManager.recoverSilent(lastHotbarSlot);
/*     */         } 
/* 199 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case NONE:
/* 203 */         switchedItemSwitched[1] = (back || mc.field_71439_g.field_71071_by.field_70461_c == findHotbarBlock(clazz));
/*     */         break;
/*     */     } 
/* 206 */     return switchedItemSwitched;
/*     */   }
/*     */   
/*     */   public static boolean[] switchItemToItem(boolean back, int lastHotbarSlot, boolean switchedItem, Switch mode, Item item) {
/* 210 */     boolean[] switchedItemSwitched = { switchedItem, false };
/* 211 */     switch (mode) {
/*     */       case NORMAL:
/* 213 */         if (!back && !switchedItem) {
/* 214 */           switchToHotbarSlot(getItemHotbar(item), false);
/* 215 */           switchedItemSwitched[0] = true;
/* 216 */         } else if (back && switchedItem) {
/* 217 */           switchToHotbarSlot(lastHotbarSlot, false);
/* 218 */           switchedItemSwitched[0] = false;
/*     */         } 
/* 220 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case SILENT:
/* 224 */         if (!back && !switchedItem) {
/* 225 */           switchToHotbarSlot(getItemHotbar(item), true);
/* 226 */           switchedItemSwitched[0] = true;
/* 227 */         } else if (back && switchedItem) {
/* 228 */           switchedItemSwitched[0] = false;
/* 229 */           Banzem.inventoryManager.recoverSilent(lastHotbarSlot);
/*     */         } 
/* 231 */         switchedItemSwitched[1] = true;
/*     */         break;
/*     */       
/*     */       case NONE:
/* 235 */         switchedItemSwitched[1] = (back || mc.field_71439_g.field_71071_by.field_70461_c == getItemHotbar(item));
/*     */         break;
/*     */     } 
/* 238 */     return switchedItemSwitched;
/*     */   }
/*     */   
/*     */   public static boolean holdingItem(Class clazz) {
/* 242 */     boolean result = false;
/* 243 */     ItemStack stack = mc.field_71439_g.func_184614_ca();
/* 244 */     result = isInstanceOf(stack, clazz);
/* 245 */     if (!result) {
/* 246 */       ItemStack offhand = mc.field_71439_g.func_184592_cb();
/* 247 */       result = isInstanceOf(stack, clazz);
/*     */     } 
/* 249 */     return result;
/*     */   }
/*     */   
/*     */   public static boolean isInstanceOf(ItemStack stack, Class clazz) {
/* 253 */     if (stack == null) {
/* 254 */       return false;
/*     */     }
/* 256 */     Item item = stack.func_77973_b();
/* 257 */     if (clazz.isInstance(item)) {
/* 258 */       return true;
/*     */     }
/* 260 */     if (item instanceof ItemBlock) {
/* 261 */       Block block = Block.func_149634_a(item);
/* 262 */       return clazz.isInstance(block);
/*     */     } 
/* 264 */     return false;
/*     */   }
/*     */   
/*     */   public static int getEmptyXCarry() {
/* 268 */     for (int i = 1; i < 5; ) {
/* 269 */       Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 270 */       ItemStack craftingStack = craftingSlot.func_75211_c();
/* 271 */       if (!craftingStack.func_190926_b() && craftingStack.func_77973_b() != Items.field_190931_a) { i++; continue; }
/* 272 */        return i;
/*     */     } 
/* 274 */     return -1;
/*     */   }
/*     */   
/*     */   public static boolean isSlotEmpty(int i) {
/* 278 */     Slot slot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 279 */     ItemStack stack = slot.func_75211_c();
/* 280 */     return stack.func_190926_b();
/*     */   }
/*     */   
/*     */   public static int convertHotbarToInv(int input) {
/* 284 */     return 36 + input;
/*     */   }
/*     */   
/*     */   public static boolean areStacksCompatible(ItemStack stack1, ItemStack stack2) {
/* 288 */     if (!stack1.func_77973_b().equals(stack2.func_77973_b())) {
/* 289 */       return false;
/*     */     }
/* 291 */     if (stack1.func_77973_b() instanceof ItemBlock && stack2.func_77973_b() instanceof ItemBlock) {
/* 292 */       Block block1 = ((ItemBlock)stack1.func_77973_b()).func_179223_d();
/* 293 */       Block block2 = ((ItemBlock)stack2.func_77973_b()).func_179223_d();
/* 294 */       if (!block1.field_149764_J.equals(block2.field_149764_J)) {
/* 295 */         return false;
/*     */       }
/*     */     } 
/* 298 */     if (!stack1.func_82833_r().equals(stack2.func_82833_r())) {
/* 299 */       return false;
/*     */     }
/* 301 */     return (stack1.func_77952_i() == stack2.func_77952_i());
/*     */   }
/*     */   
/*     */   public static EntityEquipmentSlot getEquipmentFromSlot(int slot) {
/* 305 */     if (slot == 5) {
/* 306 */       return EntityEquipmentSlot.HEAD;
/*     */     }
/* 308 */     if (slot == 6) {
/* 309 */       return EntityEquipmentSlot.CHEST;
/*     */     }
/* 311 */     if (slot == 7) {
/* 312 */       return EntityEquipmentSlot.LEGS;
/*     */     }
/* 314 */     return EntityEquipmentSlot.FEET;
/*     */   }
/*     */   
/*     */   public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
/* 318 */     int slot = -1;
/* 319 */     float damage = 0.0F;
/* 320 */     for (int i = 9; i < 45; i++) {
/*     */ 
/*     */       
/* 323 */       ItemStack s = (Minecraft.func_71410_x()).field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c(); ItemArmor armor;
/* 324 */       if (s.func_77973_b() != Items.field_190931_a && s.func_77973_b() instanceof ItemArmor && (armor = (ItemArmor)s.func_77973_b()).func_185083_B_() == type) {
/*     */         
/* 326 */         float currentDamage = (armor.field_77879_b + EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, s));
/* 327 */         boolean cursed = (binding && EnchantmentHelper.func_190938_b(s)), bl = cursed;
/* 328 */         if (currentDamage > damage && !cursed)
/* 329 */         { damage = currentDamage;
/* 330 */           slot = i; } 
/*     */       } 
/* 332 */     }  return slot;
/*     */   }
/*     */   
/*     */   public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
/* 336 */     int slot = findArmorSlot(type, binding);
/* 337 */     if (slot == -1 && withXCarry) {
/* 338 */       float damage = 0.0F;
/* 339 */       for (int i = 1; i < 5; i++) {
/*     */ 
/*     */         
/* 342 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 343 */         ItemStack craftingStack = craftingSlot.func_75211_c(); ItemArmor armor;
/* 344 */         if (craftingStack.func_77973_b() != Items.field_190931_a && craftingStack.func_77973_b() instanceof ItemArmor && (armor = (ItemArmor)craftingStack.func_77973_b()).func_185083_B_() == type) {
/*     */           
/* 346 */           float currentDamage = (armor.field_77879_b + EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, craftingStack));
/* 347 */           boolean cursed = (binding && EnchantmentHelper.func_190938_b(craftingStack)), bl = cursed;
/* 348 */           if (currentDamage > damage && !cursed)
/* 349 */           { damage = currentDamage;
/* 350 */             slot = i; } 
/*     */         } 
/*     */       } 
/* 353 */     }  return slot;
/*     */   }
/*     */   
/*     */   public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
/* 357 */     int slot = findItemInventorySlot(item, offHand);
/* 358 */     if (slot == -1 && withXCarry)
/* 359 */       for (int i = 1; i < 5; i++) {
/*     */         
/* 361 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 362 */         ItemStack craftingStack = craftingSlot.func_75211_c(); Item craftingStackItem;
/* 363 */         if (craftingStack.func_77973_b() != Items.field_190931_a && (craftingStackItem = craftingStack.func_77973_b()) == item)
/*     */         {
/* 365 */           slot = i;
/*     */         }
/*     */       }  
/* 368 */     return slot;
/*     */   }
/*     */   
/*     */   public static int findBlockSlotInventory(Class clazz, boolean offHand, boolean withXCarry) {
/* 372 */     int slot = findInventoryBlock(clazz, offHand);
/* 373 */     if (slot == -1 && withXCarry)
/* 374 */       for (int i = 1; i < 5; i++) {
/*     */         
/* 376 */         Slot craftingSlot = mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
/* 377 */         ItemStack craftingStack = craftingSlot.func_75211_c();
/* 378 */         if (craftingStack.func_77973_b() != Items.field_190931_a) {
/* 379 */           Item craftingStackItem = craftingStack.func_77973_b();
/* 380 */           if (clazz.isInstance(craftingStackItem)) {
/* 381 */             slot = i;
/*     */           } else {
/*     */             Block block;
/* 384 */             if (craftingStackItem instanceof ItemBlock && clazz.isInstance(block = ((ItemBlock)craftingStackItem).func_179223_d()))
/*     */             {
/* 386 */               slot = i; } 
/*     */           } 
/*     */         } 
/* 389 */       }   return slot;
/*     */   }
/*     */   
/*     */   public enum Switch {
/* 393 */     NORMAL,
/* 394 */     SILENT,
/* 395 */     NONE;
/*     */   }
/*     */   
/*     */   public static class Task
/*     */   {
/*     */     private final int slot;
/*     */     private final boolean update;
/*     */     private final boolean quickClick;
/*     */     
/*     */     public Task() {
/* 405 */       this.update = true;
/* 406 */       this.slot = -1;
/* 407 */       this.quickClick = false;
/*     */     }
/*     */     
/*     */     public Task(int slot) {
/* 411 */       this.slot = slot;
/* 412 */       this.quickClick = false;
/* 413 */       this.update = false;
/*     */     }
/*     */     
/*     */     public Task(int slot, boolean quickClick) {
/* 417 */       this.slot = slot;
/* 418 */       this.quickClick = quickClick;
/* 419 */       this.update = false;
/*     */     }
/*     */     
/*     */     public void run() {
/* 423 */       if (this.update) {
/* 424 */         Util.mc.field_71442_b.func_78765_e();
/*     */       }
/* 426 */       if (this.slot != -1) {
/* 427 */         Util.mc.field_71442_b.func_187098_a(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, (EntityPlayer)Util.mc.field_71439_g);
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isSwitching() {
/* 432 */       return !this.update;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\oyveyutil\InventoryUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */