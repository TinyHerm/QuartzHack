/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 */
package me.mohalk.banzem.mixin.mixins;

import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.features.modules.client.FontMod;
import me.mohalk.banzem.features.modules.client.HUD;
import me.mohalk.banzem.features.modules.client.Media;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    @Shadow
    protected abstract int func_180455_b(String var1, float var2, float var3, int var4, boolean var5);

    @Shadow
    protected abstract void func_78255_a(String var1, boolean var2);

    @Inject(method={"drawString(Ljava/lang/String;FFIZ)I"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info) {
        if (FontMod.getInstance().isOn() && FontMod.getInstance().full.getValue().booleanValue() && Banzem.textManager != null) {
            float result = Banzem.textManager.drawString(text, x, y, color, dropShadow);
            info.setReturnValue((int)result);
        }
    }

    @Redirect(method={"drawString(Ljava/lang/String;FFIZ)I"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"))
    public int renderStringHook(FontRenderer fontrenderer, String text, float x, float y, int color, boolean dropShadow) {
        if (Banzem.moduleManager != null && HUD.getInstance().shadow.getValue().booleanValue() && dropShadow) {
            return this.func_180455_b(text, x - 0.5f, y - 0.5f, color, true);
        }
        return this.func_180455_b(text, x, y, color, dropShadow);
    }

    @Redirect(method={"renderString(Ljava/lang/String;FFIZ)I"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer renderer, String text, boolean shadow) {
        if (Media.getInstance().isOn() && Media.getInstance().changeOwn.getValue().booleanValue()) {
            this.func_78255_a(text.replace(Media.getPlayerName(), Media.getInstance().ownName.getValue()), shadow);
        } else {
            this.func_78255_a(text, shadow);
        }
    }
}

