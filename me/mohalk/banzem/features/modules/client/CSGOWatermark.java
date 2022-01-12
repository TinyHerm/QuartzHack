/*    */ package me.mohalk.banzem.features.modules.client;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.event.events.Render2DEvent;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.ColorUtil;
/*    */ import me.mohalk.banzem.util.RenderUtil;
/*    */ import me.mohalk.banzem.util.Timer;
/*    */ 
/*    */ public class CSGOWatermark
/*    */   extends Module {
/* 13 */   Timer delayTimer = new Timer();
/* 14 */   public Setting<Integer> X = register(new Setting("WatermarkX", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(300)));
/* 15 */   public Setting<Integer> Y = register(new Setting("WatermarkY", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(300)));
/*    */   public float hue;
/* 17 */   public int red = 1;
/* 18 */   public int green = 1;
/* 19 */   public int blue = 1;
/*    */   
/* 21 */   private String message = "";
/*    */   public CSGOWatermark() {
/* 23 */     super("WatermarkNew", "2nd watermark by eralp232", Module.Category.CLIENT, true, false, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onRender2D(Render2DEvent event) {
/* 28 */     drawCsgoWatermark();
/*    */   }
/*    */   
/*    */   public void drawCsgoWatermark() {
/* 32 */     int padding = 5;
/* 33 */     this.message = "QuartzHack 0.3.0 | " + mc.field_71439_g.func_70005_c_() + " | " + Banzem.serverManager.getPing() + "ms";
/* 34 */     Integer textWidth = Integer.valueOf(mc.field_71466_p.func_78256_a(this.message));
/* 35 */     Integer textHeight = Integer.valueOf(mc.field_71466_p.field_78288_b);
/* 36 */     RenderUtil.drawRectangleCorrectly(((Integer)this.X.getValue()).intValue() - 4, ((Integer)this.Y.getValue()).intValue() - 4, textWidth.intValue() + 16, textHeight.intValue() + 12, ColorUtil.toRGBA(22, 22, 22, 255));
/* 37 */     RenderUtil.drawRectangleCorrectly(((Integer)this.X.getValue()).intValue(), ((Integer)this.Y.getValue()).intValue(), textWidth.intValue() + 4, textHeight.intValue() + 4, ColorUtil.toRGBA(0, 0, 0, 255));
/* 38 */     RenderUtil.drawRectangleCorrectly(((Integer)this.X.getValue()).intValue(), ((Integer)this.Y.getValue()).intValue(), textWidth.intValue() + 8, textHeight.intValue() + 4, ColorUtil.toRGBA(0, 0, 0, 255));
/* 39 */     mc.field_71466_p.func_175065_a(this.message, (((Integer)this.X.getValue()).intValue() + 3), (((Integer)this.Y.getValue()).intValue() + 3), ColorUtil.toRGBA(255, 255, 255, 255), false);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\CSGOWatermark.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */