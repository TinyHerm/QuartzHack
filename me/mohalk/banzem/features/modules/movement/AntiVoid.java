/*    */ package me.mohalk.banzem.features.modules.movement;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraft.util.math.RayTraceResult;
/*    */ import net.minecraft.util.math.Vec3d;
/*    */ 
/*    */ public class AntiVoid
/*    */   extends Module {
/* 10 */   public Setting<Double> yLevel = register(new Setting("YLevel", Double.valueOf(1.0D), Double.valueOf(0.1D), Double.valueOf(5.0D)));
/* 11 */   public Setting<Double> yForce = register(new Setting("YMotion", Double.valueOf(0.1D), Double.valueOf(0.0D), Double.valueOf(1.0D)));
/*    */   
/*    */   public AntiVoid() {
/* 14 */     super("AntiVoid", "Glitches you up from void.", Module.Category.MOVEMENT, false, false, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 19 */     if (fullNullCheck()) {
/*    */       return;
/*    */     }
/* 22 */     if (!mc.field_71439_g.field_70145_X && mc.field_71439_g.field_70163_u <= ((Double)this.yLevel.getValue()).doubleValue()) {
/* 23 */       RayTraceResult trace = mc.field_71441_e.func_147447_a(mc.field_71439_g.func_174791_d(), new Vec3d(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v), false, false, false);
/* 24 */       if (trace != null && trace.field_72313_a == RayTraceResult.Type.BLOCK) {
/*    */         return;
/*    */       }
/* 27 */       mc.field_71439_g.field_70181_x = ((Double)this.yForce.getValue()).doubleValue();
/* 28 */       if (mc.field_71439_g.func_184187_bx() != null) {
/* 29 */         (mc.field_71439_g.func_184187_bx()).field_70181_x = ((Double)this.yForce.getValue()).doubleValue();
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDisplayInfo() {
/* 36 */     return ((Double)this.yLevel.getValue()).toString() + ", " + ((Double)this.yForce.getValue()).toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\AntiVoid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */