/*    */ package me.mohalk.banzem.features.modules.combat;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraft.enchantment.EnchantmentHelper;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.init.Enchantments;
/*    */ import net.minecraft.init.Items;
/*    */ import net.minecraft.inventory.ClickType;
/*    */ import net.minecraft.inventory.EntityEquipmentSlot;
/*    */ import net.minecraft.item.ItemArmor;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class ChestSwap
/*    */   extends Module {
/* 16 */   public final Setting<Boolean> PreferElytra = new Setting("PreferElytra", Boolean.valueOf(true));
/* 17 */   public final Setting<Boolean> Curse = new Setting("Curse", Boolean.valueOf(false));
/*    */ 
/*    */   
/*    */   public ChestSwap() {
/* 21 */     super("ChestSwap", "Will attempt to instantly swap your chestplate with an elytra or vice versa, depending on what is already equipped", Module.Category.COMBAT, true, false, false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 27 */     super.onEnable();
/*    */     
/* 29 */     if (mc.field_71439_g == null) {
/*    */       return;
/*    */     }
/* 32 */     ItemStack l_ChestSlot = mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c();
/*    */     
/* 34 */     if (l_ChestSlot.func_190926_b()) {
/*    */       
/* 36 */       int i = FindChestItem(((Boolean)this.PreferElytra.getValue()).booleanValue());
/*    */       
/* 38 */       if (!((Boolean)this.PreferElytra.getValue()).booleanValue() && i == -1) {
/* 39 */         i = FindChestItem(true);
/*    */       }
/* 41 */       if (i != -1) {
/*    */         
/* 43 */         mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, i, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 44 */         mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 6, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 45 */         mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, i, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*    */       } 
/*    */       
/* 48 */       toggle();
/*    */       
/*    */       return;
/*    */     } 
/* 52 */     int l_Slot = FindChestItem(l_ChestSlot.func_77973_b() instanceof ItemArmor);
/*    */     
/* 54 */     if (l_Slot != -1) {
/*    */       
/* 56 */       mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 57 */       mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 6, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 58 */       mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*    */     } 
/*    */     
/* 61 */     toggle();
/*    */   }
/*    */ 
/*    */   
/*    */   private int FindChestItem(boolean p_Elytra) {
/* 66 */     int slot = -1;
/* 67 */     float damage = 0.0F;
/*    */     
/* 69 */     for (int i = 0; i < mc.field_71439_g.field_71069_bz.func_75138_a().size(); i++) {
/*    */ 
/*    */       
/* 72 */       if (i != 0 && i != 5 && i != 6 && i != 7 && i != 8) {
/*    */ 
/*    */         
/* 75 */         ItemStack s = (ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(i);
/* 76 */         if (s != null && s.func_77973_b() != Items.field_190931_a)
/*    */         {
/* 78 */           if (s.func_77973_b() instanceof ItemArmor) {
/*    */             
/* 80 */             ItemArmor armor = (ItemArmor)s.func_77973_b();
/* 81 */             if (armor.field_77881_a == EntityEquipmentSlot.CHEST) {
/*    */               
/* 83 */               float currentDamage = (armor.field_77879_b + EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, s));
/*    */               
/* 85 */               boolean cursed = ((Boolean)this.Curse.getValue()).booleanValue() ? EnchantmentHelper.func_190938_b(s) : false;
/*    */               
/* 87 */               if (currentDamage > damage && !cursed)
/*    */               {
/* 89 */                 damage = currentDamage;
/* 90 */                 slot = i;
/*    */               }
/*    */             
/*    */             } 
/* 94 */           } else if (p_Elytra && s.func_77973_b() instanceof net.minecraft.item.ItemElytra) {
/* 95 */             return i;
/*    */           }  } 
/*    */       } 
/*    */     } 
/* 99 */     return slot;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\ChestSwap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */