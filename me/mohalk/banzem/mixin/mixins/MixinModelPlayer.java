package me.mohalk.banzem.mixin.mixins;

import net.minecraft.client.model.ModelPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ModelPlayer.class})
public class MixinModelPlayer {
  @Redirect(method = {"renderCape"}, at = @At("HEAD"))
  public void renderCape(float scale) {}
}


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinModelPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */