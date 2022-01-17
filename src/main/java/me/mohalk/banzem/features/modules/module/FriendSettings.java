/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.features.modules.module;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;

public class FriendSettings
extends Module {
    private static FriendSettings INSTANCE;
    public Setting<Boolean> notify = this.register(new Setting<Boolean>("Notify", false));

    public FriendSettings() {
        super("FriendSettings", "Change aspects of friends", Module.Category.MODULE, true, false, false);
        INSTANCE = this;
    }

    public static FriendSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FriendSettings();
        }
        return INSTANCE;
    }
}

