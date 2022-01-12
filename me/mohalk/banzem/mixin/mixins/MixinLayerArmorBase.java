/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.render.NoRender;
/*    */ import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
/*    */ import net.minecraft.entity.EntityLivingBase;
/*    */ import net.minecraft.inventory.EntityEquipmentSlot;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({LayerArmorBase.class})
/*    */ public class MixinLayerArmorBase {
/*    */   @Inject(method = {"doRenderLayer"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
/* 16 */     if (NoRender.getInstance().isEnabled() && (NoRender.getInstance()).noArmor.getValue() == NoRender.NoArmor.ALL) {
/* 17 */       ci.cancel();
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"renderArmorLayer"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo ci) {
/* 23 */     if (NoRender.getInstance().isEnabled() && (NoRender.getInstance()).noArmor.getValue() == NoRender.NoArmor.HELMET && slotIn == EntityEquipmentSlot.HEAD)
/* 24 */       ci.cancel(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinLayerArmorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */