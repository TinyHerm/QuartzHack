/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.movement.ElytraFlight;
/*    */ import me.mohalk.banzem.util.Util;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.EntityLivingBase;
/*    */ import net.minecraft.world.World;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({EntityLivingBase.class})
/*    */ public abstract class MixinEntityLivingBase
/*    */   extends Entity {
/*    */   public MixinEntityLivingBase(World worldIn) {
/* 17 */     super(worldIn);
/*    */   }
/*    */   
/*    */   @Inject(method = {"isElytraFlying"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void isElytraFlyingHook(CallbackInfoReturnable<Boolean> info) {
/* 22 */     if (Util.mc.field_71439_g != null && Util.mc.field_71439_g.equals(this) && ElytraFlight.getInstance().isOn() && (ElytraFlight.getInstance()).mode.getValue() == ElytraFlight.Mode.BETTER)
/* 23 */       info.setReturnValue(Boolean.valueOf(false)); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinEntityLivingBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */