/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.features.modules.client;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.modules.client.ServerModule;
import me.mohalk.banzem.features.setting.Setting;
import me.mohalk.banzem.util.Util;

public class Media
extends Module {
    private static Media instance;
    public final Setting<Boolean> changeOwn = this.register(new Setting<Boolean>("MyName", true));
    public final Setting<String> ownName = this.register(new Setting<Object>("Name", "Name here...", v -> this.changeOwn.getValue()));

    public Media() {
        super("NickChanger", "Helps with creating Media", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    public static Media getInstance() {
        if (instance == null) {
            instance = new Media();
        }
        return instance;
    }

    public static String getPlayerName() {
        if (Media.fullNullCheck() || !ServerModule.getInstance().isConnected()) {
            return Util.mc.func_110432_I().func_111285_a();
        }
        String name = ServerModule.getInstance().getPlayerName();
        if (name == null || name.isEmpty()) {
            return Util.mc.func_110432_I().func_111285_a();
        }
        return name;
    }
}

