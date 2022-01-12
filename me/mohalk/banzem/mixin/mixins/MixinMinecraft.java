/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import javax.annotation.Nullable;
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.gui.custom.GuiCustomMainScreen;
/*    */ import me.mohalk.banzem.features.modules.client.Managers;
/*    */ import me.mohalk.banzem.features.modules.player.MultiTask;
/*    */ import me.mohalk.banzem.features.modules.render.NoRender;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.entity.EntityPlayerSP;
/*    */ import net.minecraft.client.gui.GuiScreen;
/*    */ import net.minecraft.client.multiplayer.PlayerControllerMP;
/*    */ import net.minecraft.client.multiplayer.WorldClient;
/*    */ import net.minecraft.crash.CrashReport;
/*    */ import org.lwjgl.input.Keyboard;
/*    */ import org.lwjgl.opengl.Display;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.Redirect;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
/*    */ 
/*    */ @Mixin({Minecraft.class})
/*    */ public abstract class MixinMinecraft
/*    */ {
/*    */   @Shadow
/*    */   public abstract void func_147108_a(@Nullable GuiScreen paramGuiScreen);
/*    */   
/*    */   @Inject(method = {"runTickKeyboard"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 0)}, locals = LocalCapture.CAPTURE_FAILSOFT)
/*    */   private void onRunTickKeyboard(CallbackInfo ci, int i) {
/* 34 */     if (Keyboard.getEventKeyState() && Banzem.moduleManager != null) {
/* 35 */       Banzem.moduleManager.onKeyPressed(i);
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"getLimitFramerate"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void getLimitFramerateHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
/*    */     try {
/* 42 */       if (((Boolean)(Managers.getInstance()).unfocusedCpu.getValue()).booleanValue() && !Display.isActive()) {
/* 43 */         callbackInfoReturnable.setReturnValue((Managers.getInstance()).cpuFPS.getValue());
/*    */       }
/*    */     }
/* 46 */     catch (NullPointerException nullPointerException) {}
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Redirect(method = {"runGameLoop"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;sync(I)V", remap = false))
/*    */   public void syncHook(int maxFps) {
/* 53 */     if (((Boolean)(Managers.getInstance()).betterFrames.getValue()).booleanValue()) {
/* 54 */       Display.sync(((Integer)(Managers.getInstance()).betterFPS.getValue()).intValue());
/*    */     } else {
/* 56 */       Display.sync(maxFps);
/*    */     } 
/*    */   }
/*    */   
/*    */   @Inject(method = {"displayGuiScreen"}, at = {@At("HEAD")})
/*    */   private void displayGuiScreen(GuiScreen screen, CallbackInfo ci) {
/* 62 */     if (screen instanceof net.minecraft.client.gui.GuiMainMenu) {
/* 63 */       func_147108_a((GuiScreen)new GuiCustomMainScreen());
/*    */     }
/*    */   }
/*    */   
/*    */   @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
/*    */   public void displayCrashReportHook(Minecraft minecraft, CrashReport crashReport) {
/* 69 */     unload();
/*    */   }
/*    */   
/*    */   @Redirect(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;doVoidFogParticles(III)V"))
/*    */   public void doVoidFogParticlesHook(WorldClient world, int x, int y, int z) {
/* 74 */     NoRender.getInstance().doVoidFogParticles(x, y, z);
/*    */   }
/*    */   
/*    */   @Inject(method = {"shutdown"}, at = {@At("HEAD")})
/*    */   public void shutdownHook(CallbackInfo info) {
/* 79 */     unload();
/*    */   }
/*    */   
/*    */   private void unload() {
/* 83 */     System.out.println("Shutting down: saving configuration");
/* 84 */     Banzem.onUnload();
/* 85 */     System.out.println("Configuration saved.");
/*    */   }
/*    */   
/*    */   @Redirect(method = {"sendClickBlockToController"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
/*    */   private boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
/* 90 */     return (!MultiTask.getInstance().isOn() && playerSP.func_184587_cr());
/*    */   }
/*    */   
/*    */   @Redirect(method = {"rightClickMouse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0), require = 1)
/*    */   private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
/* 95 */     return (!MultiTask.getInstance().isOn() && playerControllerMP.func_181040_m());
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinMinecraft.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */