/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.event.events.KeyEvent;
/*    */ import net.minecraft.client.settings.KeyBinding;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.Event;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({KeyBinding.class})
/*    */ public class MixinKeyBinding {
/*    */   @Shadow
/*    */   private boolean field_74513_e;
/*    */   
/*    */   @Inject(method = {"isKeyDown"}, at = {@At("RETURN")}, cancellable = true)
/*    */   private void isKeyDown(CallbackInfoReturnable<Boolean> info) {
/* 20 */     KeyEvent event = new KeyEvent(0, ((Boolean)info.getReturnValue()).booleanValue(), this.field_74513_e);
/* 21 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 22 */     info.setReturnValue(Boolean.valueOf(event.info));
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinKeyBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */