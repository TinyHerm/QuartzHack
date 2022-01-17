/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.features.modules.movement;

import me.mohalk.banzem.features.modules.Module;

public class ReverseStep
extends Module {
    public ReverseStep() {
        super("ReverseStep", "Screams chinese words and teleports you", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (ReverseStep.mc.field_71439_g.field_70122_E) {
            ReverseStep.mc.field_71439_g.field_70181_x -= 1.0;
        }
    }
}

