/*    */ package me.mohalk.banzem.features.modules.player;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraft.util.EnumHand;
/*    */ 
/*    */ public class Swing
/*    */   extends Module {
/*  9 */   private Setting<Hand> hand = register(new Setting("Hand", Hand.OFFHAND));
/*    */   public Swing() {
/* 11 */     super("Swing", "Changes the hand you swing with", Module.Category.PLAYER, false, false, false);
/*    */   }
/*    */   
/*    */   public void onUpdate() {
/* 15 */     if (mc.field_71441_e == null)
/*    */       return; 
/* 17 */     if (((Hand)this.hand.getValue()).equals(Hand.OFFHAND)) {
/* 18 */       mc.field_71439_g.field_184622_au = EnumHand.OFF_HAND;
/*    */     }
/* 20 */     if (((Hand)this.hand.getValue()).equals(Hand.MAINHAND)) {
/* 21 */       mc.field_71439_g.field_184622_au = EnumHand.MAIN_HAND;
/*    */     }
/* 23 */     if (((Hand)this.hand.getValue()).equals(Hand.PACKETSWING) && 
/* 24 */       mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemSword && mc.field_71460_t.field_78516_c.field_187470_g >= 0.9D) {
/* 25 */       mc.field_71460_t.field_78516_c.field_187469_f = 1.0F;
/* 26 */       mc.field_71460_t.field_78516_c.field_187467_d = mc.field_71439_g.func_184614_ca();
/*    */     } 
/*    */   }
/*    */   
/*    */   public enum Hand
/*    */   {
/* 32 */     OFFHAND,
/* 33 */     MAINHAND,
/* 34 */     PACKETSWING;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\Swing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */