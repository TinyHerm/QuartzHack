/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.Display
 */
package me.mohalk.banzem.features.modules.module;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;

public class Display
extends Module {
    private static Display INSTANCE = new Display();
    public Setting<String> gang = this.register(new Setting<String>("Title", "QuartzHack - 0.3.0-rewrite"));
    public Setting<Boolean> version = this.register(new Setting<Boolean>("Version", true));

    public Display() {
        super("Display", "Sets the title of your game", Module.Category.MODULE, true, false, false);
        this.setInstance();
    }

    public static Display getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Display();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        org.lwjgl.opengl.Display.setTitle((String)this.gang.getValue());
    }
}

