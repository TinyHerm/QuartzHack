/*    */ package me.mohalk.banzem.features.modules.misc;
/*    */ 
/*    */ import java.util.Random;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraft.network.play.client.CPacketAnimation;
/*    */ import net.minecraft.util.EnumHand;
/*    */ 
/*    */ public class NoAFK
/*    */   extends Module {
/* 12 */   private final Setting<Boolean> swing = register(new Setting("Swing", Boolean.valueOf(true)));
/* 13 */   private final Setting<Boolean> turn = register(new Setting("Turn", Boolean.valueOf(true)));
/* 14 */   private final Random random = new Random();
/*    */   
/*    */   public NoAFK() {
/* 17 */     super("NoAFK", "Prevents you from getting kicked for afk.", Module.Category.MISC, false, false, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 22 */     if (mc.field_71442_b.func_181040_m()) {
/*    */       return;
/*    */     }
/* 25 */     if (mc.field_71439_g.field_70173_aa % 40 == 0 && ((Boolean)this.swing.getValue()).booleanValue()) {
/* 26 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
/*    */     }
/* 28 */     if (mc.field_71439_g.field_70173_aa % 15 == 0 && ((Boolean)this.turn.getValue()).booleanValue()) {
/* 29 */       mc.field_71439_g.field_70177_z = (this.random.nextInt(360) - 180);
/*    */     }
/* 31 */     if (!((Boolean)this.swing.getValue()).booleanValue() && !((Boolean)this.turn.getValue()).booleanValue() && mc.field_71439_g.field_70173_aa % 80 == 0)
/* 32 */       mc.field_71439_g.func_70664_aZ(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\NoAFK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */