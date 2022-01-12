/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.misc.ToolTips;
/*    */ import net.minecraft.client.gui.Gui;
/*    */ import net.minecraft.client.gui.GuiScreen;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({GuiScreen.class})
/*    */ public class MixinGuiScreen
/*    */   extends Gui
/*    */ {
/*    */   @Inject(method = {"renderToolTip"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
/* 18 */     if (ToolTips.getInstance().isOn() && ((Boolean)(ToolTips.getInstance()).shulkers.getValue()).booleanValue() && stack.func_77973_b() instanceof net.minecraft.item.ItemShulkerBox) {
/* 19 */       ToolTips.getInstance().renderShulkerToolTip(stack, x, y, null);
/* 20 */       info.cancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinGuiScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */