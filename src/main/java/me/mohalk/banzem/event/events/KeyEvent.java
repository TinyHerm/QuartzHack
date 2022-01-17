/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.event.events;

import me.mohalk.banzem.event.EventStage;

public class KeyEvent
extends EventStage {
    public boolean info;
    public boolean pressed;

    public KeyEvent(int stage, boolean info, boolean pressed) {
        super(stage);
        this.info = info;
        this.pressed = pressed;
    }
}

