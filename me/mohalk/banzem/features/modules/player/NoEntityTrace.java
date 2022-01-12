/*    */ package me.mohalk.banzem.features.modules.player;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraft.init.Blocks;
/*    */ import net.minecraft.init.Items;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemBlock;
/*    */ 
/*    */ 
/*    */ public class NoEntityTrace
/*    */   extends Module
/*    */ {
/* 14 */   private static NoEntityTrace INSTANCE = new NoEntityTrace();
/* 15 */   public Setting<Boolean> pick = register(new Setting("Pick", Boolean.valueOf(true)));
/* 16 */   public Setting<Boolean> gap = register(new Setting("Gap", Boolean.valueOf(false)));
/* 17 */   public Setting<Boolean> obby = register(new Setting("Obby", Boolean.valueOf(false)));
/*    */   
/*    */   public boolean noTrace;
/*    */   
/*    */   public NoEntityTrace() {
/* 22 */     super("NoEntityTrace", "Mine through entities", Module.Category.PLAYER, false, false, false);
/* 23 */     setInstance();
/*    */   }
/*    */ 
/*    */   
/*    */   public static NoEntityTrace getINSTANCE() {
/* 28 */     if (INSTANCE == null) {
/* 29 */       INSTANCE = new NoEntityTrace();
/*    */     }
/* 31 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   private void setInstance() {
/* 36 */     INSTANCE = this;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 42 */     Item item = mc.field_71439_g.func_184614_ca().func_77973_b();
/* 43 */     if (item instanceof net.minecraft.item.ItemPickaxe && ((Boolean)this.pick.getValue()).booleanValue()) {
/* 44 */       this.noTrace = true;
/*    */       return;
/*    */     } 
/* 47 */     if (item == Items.field_151153_ao && ((Boolean)this.gap.getValue()).booleanValue()) {
/* 48 */       this.noTrace = true;
/*    */       return;
/*    */     } 
/* 51 */     if (item instanceof ItemBlock) {
/* 52 */       this.noTrace = (((ItemBlock)item).func_179223_d() == Blocks.field_150343_Z && ((Boolean)this.obby.getValue()).booleanValue());
/*    */       return;
/*    */     } 
/* 55 */     this.noTrace = false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\NoEntityTrace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */