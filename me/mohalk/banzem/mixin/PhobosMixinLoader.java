/*    */ package me.mohalk.banzem.mixin;
/*    */ 
/*    */ import java.util.Map;
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
/*    */ import org.spongepowered.asm.launch.MixinBootstrap;
/*    */ import org.spongepowered.asm.mixin.MixinEnvironment;
/*    */ import org.spongepowered.asm.mixin.Mixins;
/*    */ 
/*    */ public class PhobosMixinLoader
/*    */   implements IFMLLoadingPlugin
/*    */ {
/*    */   private static boolean isObfuscatedEnvironment = false;
/*    */   
/*    */   public PhobosMixinLoader() {
/* 16 */     Banzem.LOGGER.info("QuartzHack mixins initialized");
/* 17 */     MixinBootstrap.init();
/* 18 */     Mixins.addConfiguration("mixins.eralp232.json");
/* 19 */     MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
/* 20 */     Banzem.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
/*    */   }
/*    */   
/*    */   public String[] getASMTransformerClass() {
/* 24 */     return new String[0];
/*    */   }
/*    */   
/*    */   public String getModContainerClass() {
/* 28 */     return null;
/*    */   }
/*    */   
/*    */   public String getSetupClass() {
/* 32 */     return null;
/*    */   }
/*    */   
/*    */   public void injectData(Map<String, Object> data) {
/* 36 */     isObfuscatedEnvironment = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
/*    */   }
/*    */   
/*    */   public String getAccessTransformerClass() {
/* 40 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\PhobosMixinLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */