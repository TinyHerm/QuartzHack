/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.manager;

import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.features.Feature;
import me.mohalk.banzem.features.modules.player.TimerSpeed;

public class TimerManager
extends Feature {
    private float timer = 1.0f;
    private TimerSpeed module;

    public void init() {
        this.module = Banzem.moduleManager.getModuleByClass(TimerSpeed.class);
    }

    public void unload() {
        this.timer = 1.0f;
        TimerManager.mc.field_71428_T.field_194149_e = 50.0f;
    }

    public void update() {
        if (this.module != null && this.module.isEnabled()) {
            this.timer = this.module.speed;
        }
        TimerManager.mc.field_71428_T.field_194149_e = 50.0f / (this.timer <= 0.0f ? 0.1f : this.timer);
    }

    public float getTimer() {
        return this.timer;
    }

    public void setTimer(float timer) {
        if (timer > 0.0f) {
            this.timer = timer;
        }
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
    }
}

