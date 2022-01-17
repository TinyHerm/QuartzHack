/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.features.modules.module;

import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;

public class ModuleTools
extends Module {
    private static ModuleTools INSTANCE;
    public Setting<Notifier> notifier = this.register(new Setting<Notifier>("ModuleNotifier", Notifier.FUTURE));
    public Setting<PopNotifier> popNotifier = this.register(new Setting<PopNotifier>("PopNotifier", PopNotifier.FUTURE));

    public ModuleTools() {
        super("ModuleTools", "Change settings", Module.Category.CLIENT, true, false, false);
        INSTANCE = this;
    }

    public static ModuleTools getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleTools();
        }
        return INSTANCE;
    }

    public static enum PopNotifier {
        PHOBOS,
        FUTURE,
        DOTGOD,
        NONE;

    }

    public static enum Notifier {
        PHOBOS,
        FUTURE,
        DOTGOD;

    }
}

