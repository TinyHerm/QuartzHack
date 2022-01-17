/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.event.events;

import me.mohalk.banzem.event.EventStage;
import me.mohalk.banzem.features.setting.Setting;

public class ValueChangeEvent
extends EventStage {
    public Setting setting;
    public Object value;

    public ValueChangeEvent(Setting setting, Object value) {
        this.setting = setting;
        this.value = value;
    }
}

