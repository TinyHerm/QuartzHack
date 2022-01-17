/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemSword
 *  net.minecraft.util.EnumHand
 */
package me.mohalk.banzem.features.modules.player;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

public class Swing
extends Module {
    private Setting<Hand> hand = this.register(new Setting<Hand>("Hand", Hand.OFFHAND));

    public Swing() {
        super("Swing", "Changes the hand you swing with", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (Swing.mc.field_71441_e == null) {
            return;
        }
        if (this.hand.getValue().equals((Object)Hand.OFFHAND)) {
            Swing.mc.field_71439_g.field_184622_au = EnumHand.OFF_HAND;
        }
        if (this.hand.getValue().equals((Object)Hand.MAINHAND)) {
            Swing.mc.field_71439_g.field_184622_au = EnumHand.MAIN_HAND;
        }
        if (this.hand.getValue().equals((Object)Hand.PACKETSWING) && Swing.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword && (double)Swing.mc.field_71460_t.field_78516_c.field_187470_g >= 0.9) {
            Swing.mc.field_71460_t.field_78516_c.field_187469_f = 1.0f;
            Swing.mc.field_71460_t.field_78516_c.field_187467_d = Swing.mc.field_71439_g.func_184614_ca();
        }
    }

    public static enum Hand {
        OFFHAND,
        MAINHAND,
        PACKETSWING;

    }
}

