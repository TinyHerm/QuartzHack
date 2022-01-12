/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import com.google.common.base.Predicate;
/*    */ import java.util.List;
/*    */ import me.mohalk.banzem.event.events.PushEvent;
/*    */ import me.mohalk.banzem.features.modules.render.NoRender;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.util.math.AxisAlignedBB;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.world.EnumSkyBlock;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraft.world.chunk.Chunk;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.Event;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.Redirect;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({World.class})
/*    */ public class MixinWorld
/*    */ {
/*    */   @Redirect(method = {"getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getEntitiesOfTypeWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lcom/google/common/base/Predicate;)V"))
/*    */   public <T extends Entity> void getEntitiesOfTypeWithinAABBHook(Chunk chunk, Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter) {
/*    */     try {
/* 27 */       chunk.func_177430_a(entityClass, aabb, listToFill, filter);
/*    */     }
/* 29 */     catch (Exception exception) {}
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject(method = {"checkLightFor"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void updateLightmapHook(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
/* 36 */     if (lightType == EnumSkyBlock.SKY && NoRender.getInstance().isOn() && ((NoRender.getInstance()).skylight.getValue() == NoRender.Skylight.WORLD || (NoRender.getInstance()).skylight.getValue() == NoRender.Skylight.ALL)) {
/* 37 */       info.setReturnValue(Boolean.valueOf(true));
/* 38 */       info.cancel();
/*    */     } 
/*    */   }
/*    */   
/*    */   @Redirect(method = {"handleMaterialAcceleration"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isPushedByWater()Z"))
/*    */   public boolean isPushedbyWaterHook(Entity entity) {
/* 44 */     PushEvent event = new PushEvent(2, entity);
/* 45 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 46 */     return (entity.func_96092_aw() && !event.isCanceled());
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinWorld.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */