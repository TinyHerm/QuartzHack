package me.mohalk.banzem.mixin.mixins.accessors;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({EntityLivingBase.class})
public interface IEntityLivingBase {
  @Invoker("getArmSwingAnimationEnd")
  int getArmSwingAnimationEnd();
}


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\accessors\IEntityLivingBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */