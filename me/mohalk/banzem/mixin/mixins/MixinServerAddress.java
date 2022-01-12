/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.client.ServerModule;
/*    */ import me.mohalk.banzem.mixin.mixins.accessors.IServerAddress;
/*    */ import net.minecraft.client.multiplayer.ServerAddress;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Redirect;
/*    */ 
/*    */ @Mixin({ServerAddress.class})
/*    */ public abstract class MixinServerAddress {
/*    */   @Redirect(method = {"fromString"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerAddress;getServerAddress(Ljava/lang/String;)[Ljava/lang/String;"))
/*    */   private static String[] getServerAddressHook(String ip) {
/*    */     ServerModule module;
/*    */     int port;
/* 16 */     if (ip.equals((ServerModule.getInstance()).ip.getValue()) && (port = (module = ServerModule.getInstance()).getPort()) != -1) {
/* 17 */       return new String[] { (String)(ServerModule.getInstance()).ip.getValue(), Integer.toString(port) };
/*    */     }
/* 19 */     return IServerAddress.getServerAddress(ip);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinServerAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */