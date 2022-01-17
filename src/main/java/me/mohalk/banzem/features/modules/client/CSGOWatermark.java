/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.features.modules.client;

import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.event.events.Render2DEvent;
import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;
import me.mohalk.banzem.util.ColorUtil;
import me.mohalk.banzem.util.RenderUtil;
import me.mohalk.banzem.util.Timer;

public class CSGOWatermark
extends Module {
    Timer delayTimer = new Timer();
    public Setting<Integer> X = this.register(new Setting<Integer>("WatermarkX", 0, 0, 300));
    public Setting<Integer> Y = this.register(new Setting<Integer>("WatermarkY", 0, 0, 300));
    public float hue;
    public int red = 1;
    public int green = 1;
    public int blue = 1;
    private String message = "";

    public CSGOWatermark() {
        super("WatermarkNew", "2nd watermark by eralp232", Module.Category.CLIENT, true, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        this.drawCsgoWatermark();
    }

    public void drawCsgoWatermark() {
        int padding = 5;
        this.message = "QuartzHack 0.3.0 | " + CSGOWatermark.mc.field_71439_g.func_70005_c_() + " | " + Banzem.serverManager.getPing() + "ms";
        Integer textWidth = CSGOWatermark.mc.field_71466_p.func_78256_a(this.message);
        Integer textHeight = CSGOWatermark.mc.field_71466_p.field_78288_b;
        RenderUtil.drawRectangleCorrectly(this.X.getValue() - 4, this.Y.getValue() - 4, textWidth + 16, textHeight + 12, ColorUtil.toRGBA(22, 22, 22, 255));
        RenderUtil.drawRectangleCorrectly(this.X.getValue(), this.Y.getValue(), textWidth + 4, textHeight + 4, ColorUtil.toRGBA(0, 0, 0, 255));
        RenderUtil.drawRectangleCorrectly(this.X.getValue(), this.Y.getValue(), textWidth + 8, textHeight + 4, ColorUtil.toRGBA(0, 0, 0, 255));
        CSGOWatermark.mc.field_71466_p.func_175065_a(this.message, (float)(this.X.getValue() + 3), (float)(this.Y.getValue() + 3), ColorUtil.toRGBA(255, 255, 255, 255), false);
    }
}

