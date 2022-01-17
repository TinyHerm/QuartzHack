/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 */
package me.mohalk.banzem.features.modules.movement;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AntiVoid
extends Module {
    public Setting<Double> yLevel = this.register(new Setting<Double>("YLevel", 1.0, 0.1, 5.0));
    public Setting<Double> yForce = this.register(new Setting<Double>("YMotion", 0.1, 0.0, 1.0));

    public AntiVoid() {
        super("AntiVoid", "Glitches you up from void.", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
        if (!AntiVoid.mc.field_71439_g.field_70145_X && AntiVoid.mc.field_71439_g.field_70163_u <= this.yLevel.getValue()) {
            RayTraceResult trace = AntiVoid.mc.field_71441_e.func_147447_a(AntiVoid.mc.field_71439_g.func_174791_d(), new Vec3d(AntiVoid.mc.field_71439_g.field_70165_t, 0.0, AntiVoid.mc.field_71439_g.field_70161_v), false, false, false);
            if (trace != null && trace.field_72313_a == RayTraceResult.Type.BLOCK) {
                return;
            }
            AntiVoid.mc.field_71439_g.field_70181_x = this.yForce.getValue();
            if (AntiVoid.mc.field_71439_g.func_184187_bx() != null) {
                AntiVoid.mc.field_71439_g.func_184187_bx().field_70181_x = this.yForce.getValue();
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.yLevel.getValue().toString() + ", " + this.yForce.getValue().toString();
    }
}

