/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.features.modules.render;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;

public class ViewModel
extends Module {
    public final Setting<Integer> translateX = this.register(new Setting<Integer>("TranslateX", 0, -200, 200));
    public final Setting<Integer> translateY = this.register(new Setting<Integer>("TranslateY", 0, -200, 200));
    public final Setting<Integer> translateZ = this.register(new Setting<Integer>("TranslateZ", 0, -200, 200));
    public final Setting<Integer> rotateX = this.register(new Setting<Integer>("RotateX", 0, -200, 200));
    public final Setting<Integer> rotateY = this.register(new Setting<Integer>("RotateY", 0, -200, 200));
    public final Setting<Integer> rotateZ = this.register(new Setting<Integer>("RotateZ", 0, -200, 200));
    public final Setting<Integer> scaleX = this.register(new Setting<Integer>("ScaleX", 100, 0, 200));
    public final Setting<Integer> scaleY = this.register(new Setting<Integer>("ScaleY", 100, 0, 200));
    public final Setting<Integer> scaleZ = this.register(new Setting<Integer>("ScaleZ", 100, 0, 200));
    public static ViewModel INSTANCE;

    public ViewModel() {
        super("ViewModel", "Cool", Module.Category.RENDER, true, false, false);
        INSTANCE = this;
    }
}

