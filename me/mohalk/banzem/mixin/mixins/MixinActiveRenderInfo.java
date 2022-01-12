/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.util.RenderUtil;
/*    */ import net.minecraft.client.renderer.ActiveRenderInfo;
/*    */ import net.minecraft.entity.Entity;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({ActiveRenderInfo.class})
/*    */ public class MixinActiveRenderInfo {
/*    */   @Inject(method = {"updateRenderInfo(Lnet/minecraft/entity/Entity;Z)V"}, at = {@At("HEAD")}, remap = false)
/*    */   private static void updateRenderInfo(Entity entity, boolean wtf, CallbackInfo ci) {
/* 15 */     RenderUtil.updateModelViewProjectionMatrix();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinActiveRenderInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */