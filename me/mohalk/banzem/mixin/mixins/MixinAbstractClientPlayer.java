/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import javax.annotation.Nullable;
/*    */ import me.mohalk.banzem.features.modules.render.Chams;
/*    */ import net.minecraft.client.entity.AbstractClientPlayer;
/*    */ import net.minecraft.client.network.NetworkPlayerInfo;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({AbstractClientPlayer.class})
/*    */ public abstract class MixinAbstractClientPlayer
/*    */ {
/*    */   @Shadow
/*    */   @Nullable
/*    */   protected abstract NetworkPlayerInfo func_175155_b();
/*    */   
/*    */   @Inject(method = {"getLocationSkin()Lnet/minecraft/util/ResourceLocation;"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void getLocationSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
/* 23 */     if (((Boolean)(Chams.getInstance()).textured.getValue()).booleanValue() && Chams.getInstance().isEnabled())
/* 24 */       callbackInfoReturnable.setReturnValue(new ResourceLocation("eralp232/chams.png")); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinAbstractClientPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */