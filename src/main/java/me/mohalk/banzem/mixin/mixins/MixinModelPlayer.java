/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelPlayer
 */
package me.mohalk.banzem.mixin.mixins;

import net.minecraft.client.model.ModelPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ModelPlayer.class})
public class MixinModelPlayer {
    @Redirect(method={"renderCape"}, at=@At(value="HEAD"))
    public void renderCape(float scale) {
    }
}

