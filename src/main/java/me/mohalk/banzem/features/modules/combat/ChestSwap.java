/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemElytra
 *  net.minecraft.item.ItemStack
 */
package me.mohalk.banzem.features.modules.combat;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

public class ChestSwap
extends Module {
    public final Setting<Boolean> PreferElytra = new Setting<Boolean>("PreferElytra", true);
    public final Setting<Boolean> Curse = new Setting<Boolean>("Curse", false);

    public ChestSwap() {
        super("ChestSwap", "Will attempt to instantly swap your chestplate with an elytra or vice versa, depending on what is already equipped", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (ChestSwap.mc.field_71439_g == null) {
            return;
        }
        ItemStack l_ChestSlot = ChestSwap.mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c();
        if (l_ChestSlot.func_190926_b()) {
            int l_Slot = this.FindChestItem(this.PreferElytra.getValue());
            if (!this.PreferElytra.getValue().booleanValue() && l_Slot == -1) {
                l_Slot = this.FindChestItem(true);
            }
            if (l_Slot != -1) {
                ChestSwap.mc.field_71442_b.func_187098_a(ChestSwap.mc.field_71439_g.field_71069_bz.field_75152_c, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)ChestSwap.mc.field_71439_g);
                ChestSwap.mc.field_71442_b.func_187098_a(ChestSwap.mc.field_71439_g.field_71069_bz.field_75152_c, 6, 0, ClickType.PICKUP, (EntityPlayer)ChestSwap.mc.field_71439_g);
                ChestSwap.mc.field_71442_b.func_187098_a(ChestSwap.mc.field_71439_g.field_71069_bz.field_75152_c, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)ChestSwap.mc.field_71439_g);
            }
            this.toggle();
            return;
        }
        int l_Slot = this.FindChestItem(l_ChestSlot.func_77973_b() instanceof ItemArmor);
        if (l_Slot != -1) {
            ChestSwap.mc.field_71442_b.func_187098_a(ChestSwap.mc.field_71439_g.field_71069_bz.field_75152_c, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)ChestSwap.mc.field_71439_g);
            ChestSwap.mc.field_71442_b.func_187098_a(ChestSwap.mc.field_71439_g.field_71069_bz.field_75152_c, 6, 0, ClickType.PICKUP, (EntityPlayer)ChestSwap.mc.field_71439_g);
            ChestSwap.mc.field_71442_b.func_187098_a(ChestSwap.mc.field_71439_g.field_71069_bz.field_75152_c, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)ChestSwap.mc.field_71439_g);
        }
        this.toggle();
    }

    private int FindChestItem(boolean p_Elytra) {
        int slot = -1;
        float damage = 0.0f;
        for (int i = 0; i < ChestSwap.mc.field_71439_g.field_71069_bz.func_75138_a().size(); ++i) {
            ItemStack s;
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8 || (s = (ItemStack)ChestSwap.mc.field_71439_g.field_71069_bz.func_75138_a().get(i)) == null || s.func_77973_b() == Items.field_190931_a) continue;
            if (s.func_77973_b() instanceof ItemArmor) {
                boolean cursed;
                ItemArmor armor = (ItemArmor)s.func_77973_b();
                if (armor.field_77881_a != EntityEquipmentSlot.CHEST) continue;
                float currentDamage = armor.field_77879_b + EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_180310_c, (ItemStack)s);
                boolean bl = cursed = this.Curse.getValue() != false ? EnchantmentHelper.func_190938_b((ItemStack)s) : false;
                if (!(currentDamage > damage) || cursed) continue;
                damage = currentDamage;
                slot = i;
                continue;
            }
            if (!p_Elytra || !(s.func_77973_b() instanceof ItemElytra)) continue;
            return i;
        }
        return slot;
    }
}

