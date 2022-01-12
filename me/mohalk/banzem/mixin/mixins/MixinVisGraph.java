/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.render.XRay;
/*    */ import net.minecraft.client.renderer.chunk.VisGraph;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({VisGraph.class})
/*    */ public class MixinVisGraph {
/*    */   @Inject(method = {"setOpaqueCube"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void setOpaqueCubeHook(BlockPos pos, CallbackInfo info) {
/*    */     try {
/* 16 */       if (XRay.getInstance().isOn()) {
/* 17 */         info.cancel();
/*    */       }
/*    */     }
/* 20 */     catch (Exception exception) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinVisGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */