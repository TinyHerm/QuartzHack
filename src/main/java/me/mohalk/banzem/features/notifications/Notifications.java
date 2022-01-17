/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 */
package me.mohalk.banzem.features.notifications;

import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.features.modules.client.HUD;
import me.mohalk.banzem.util.RenderUtil;
import me.mohalk.banzem.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Notifications {
    private final String text;
    private final long disableTime;
    private final float width;
    private final Timer timer = new Timer();

    public Notifications(String text, long disableTime) {
        this.text = text;
        this.disableTime = disableTime;
        this.width = Banzem.moduleManager.getModuleByClass(HUD.class).renderer.getStringWidth(text);
        this.timer.reset();
    }

    public void onDraw(int y) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.func_71410_x());
        if (this.timer.passedMs(this.disableTime)) {
            Banzem.notificationManager.getNotifications().remove(this);
        }
        RenderUtil.drawRect((float)(scaledResolution.func_78326_a() - 4) - this.width, y, scaledResolution.func_78326_a() - 2, y + Banzem.moduleManager.getModuleByClass(HUD.class).renderer.getFontHeight() + 3, 0x75000000);
        Banzem.moduleManager.getModuleByClass(HUD.class).renderer.drawString(this.text, (float)scaledResolution.func_78326_a() - this.width - 3.0f, y + 2, -1, true);
    }
}

