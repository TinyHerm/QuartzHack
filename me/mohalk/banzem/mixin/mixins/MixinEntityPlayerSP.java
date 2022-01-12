/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.event.events.ChatEvent;
/*    */ import me.mohalk.banzem.event.events.MoveEvent;
/*    */ import me.mohalk.banzem.event.events.PushEvent;
/*    */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*    */ import me.mohalk.banzem.features.modules.movement.Speed;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.entity.AbstractClientPlayer;
/*    */ import net.minecraft.client.entity.EntityPlayerSP;
/*    */ import net.minecraft.client.network.NetHandlerPlayClient;
/*    */ import net.minecraft.entity.MoverType;
/*    */ import net.minecraft.stats.RecipeBook;
/*    */ import net.minecraft.stats.StatisticsManager;
/*    */ import net.minecraft.util.math.AxisAlignedBB;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.Event;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.Redirect;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin(value = {EntityPlayerSP.class}, priority = 9998)
/*    */ public abstract class MixinEntityPlayerSP
/*    */   extends AbstractClientPlayer {
/*    */   public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_) {
/* 31 */     super(p_i47378_2_, p_i47378_3_.func_175105_e());
/*    */   }
/*    */   
/*    */   @Inject(method = {"sendChatMessage"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void sendChatMessage(String message, CallbackInfo callback) {
/* 36 */     ChatEvent chatEvent = new ChatEvent(message);
/* 37 */     MinecraftForge.EVENT_BUS.post((Event)chatEvent);
/*    */   }
/*    */   
/*    */   @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void pushOutOfBlocksHook(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
/* 42 */     PushEvent event = new PushEvent(1);
/* 43 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 44 */     if (event.isCanceled()) {
/* 45 */       info.setReturnValue(Boolean.valueOf(false));
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void preMotion(CallbackInfo info) {
/* 51 */     UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(0);
/* 52 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 53 */     if (event.isCanceled()) {
/* 54 */       info.cancel();
/*    */     }
/*    */   }
/*    */   
/*    */   @Redirect(method = {"onUpdateWalkingPlayer"}, at = @At(value = "FIELD", target = "net/minecraft/util/math/AxisAlignedBB.minY:D"))
/*    */   private double minYHook(AxisAlignedBB bb) {
/* 60 */     if (Speed.getInstance().isOn() && (Speed.getInstance()).mode.getValue() == Speed.Mode.VANILLA && (Speed.getInstance()).changeY) {
/* 61 */       (Speed.getInstance()).changeY = false;
/* 62 */       return (Speed.getInstance()).minY;
/*    */     } 
/* 64 */     return bb.field_72338_b;
/*    */   }
/*    */   
/*    */   @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("RETURN")})
/*    */   private void postMotion(CallbackInfo info) {
/* 69 */     UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
/* 70 */     MinecraftForge.EVENT_BUS.post((Event)event);
/*    */   }
/*    */   
/*    */   @Inject(method = {"Lnet/minecraft/client/entity/EntityPlayerSP;setServerBrand(Ljava/lang/String;)V"}, at = {@At("HEAD")})
/*    */   public void getBrand(String brand, CallbackInfo callbackInfo) {
/* 75 */     if (Banzem.serverManager != null) {
/* 76 */       Banzem.serverManager.setServerBrand(brand);
/*    */     }
/*    */   }
/*    */   
/*    */   @Redirect(method = {"move"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
/*    */   public void move(AbstractClientPlayer player, MoverType moverType, double x, double y, double z) {
/* 82 */     MoveEvent event = new MoveEvent(0, moverType, x, y, z);
/* 83 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 84 */     if (!event.isCanceled())
/* 85 */       func_70091_d(event.getType(), event.getX(), event.getY(), event.getZ()); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinEntityPlayerSP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */