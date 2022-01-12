/*    */ package me.mohalk.banzem.features.modules.player;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Bind;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.InventoryUtil;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.init.Items;
/*    */ import net.minecraft.item.ItemExpBottle;
/*    */ import net.minecraft.util.EnumHand;
/*    */ import net.minecraft.util.math.RayTraceResult;
/*    */ import net.minecraft.world.World;
/*    */ import org.lwjgl.input.Keyboard;
/*    */ import org.lwjgl.input.Mouse;
/*    */ 
/*    */ public class SilentXP
/*    */   extends Module {
/* 18 */   public Setting<Mode> mode = register(new Setting("Mode", Mode.MIDDLECLICK));
/* 19 */   public Setting<Boolean> antiFriend = register(new Setting("AntiFriend", Boolean.valueOf(true)));
/* 20 */   public Setting<Bind> key = register(new Setting("Key", new Bind(-1), v -> (this.mode.getValue() != Mode.MIDDLECLICK)));
/* 21 */   public Setting<Boolean> groundOnly = register(new Setting("BelowHorizon", Boolean.valueOf(false)));
/*    */   
/*    */   private boolean last;
/*    */   private boolean on;
/*    */   
/*    */   public SilentXP() {
/* 27 */     super("SilentXP", "Silent XP", Module.Category.PLAYER, false, false, false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 33 */     if (fullNullCheck())
/* 34 */       return;  switch ((Mode)this.mode.getValue()) {
/*    */       case PRESS:
/* 36 */         if (((Bind)this.key.getValue()).isDown())
/* 37 */           throwXP(false); 
/*    */         return;
/*    */       case TOGGLE:
/* 40 */         if (toggled()) {
/* 41 */           throwXP(false);
/*    */         }
/*    */         return;
/*    */     } 
/* 45 */     if (((Boolean)this.groundOnly.getValue()).booleanValue() && mc.field_71439_g.field_70125_A < 0.0F)
/* 46 */       return;  if (Mouse.isButtonDown(2)) {
/* 47 */       throwXP(true);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean toggled() {
/* 53 */     if (((Bind)this.key.getValue()).getKey() == -1)
/* 54 */       return false; 
/* 55 */     if (!Keyboard.isKeyDown(((Bind)this.key.getValue()).getKey()))
/* 56 */     { this.last = true; }
/* 57 */     else { if (Keyboard.isKeyDown(((Bind)this.key.getValue()).getKey()) && this.last && !this.on) {
/* 58 */         this.last = false;
/* 59 */         this.on = true;
/* 60 */         return this.on;
/* 61 */       }  if (Keyboard.isKeyDown(((Bind)this.key.getValue()).getKey()) && this.last && this.on) {
/* 62 */         this.last = false;
/* 63 */         this.on = false;
/* 64 */         return this.on;
/*    */       }  }
/* 66 */      return this.on;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private void throwXP(boolean mcf) {
/*    */     RayTraceResult result;
/* 73 */     if (mcf && ((Boolean)this.antiFriend.getValue()).booleanValue() && (result = mc.field_71476_x) != null && result.field_72313_a == RayTraceResult.Type.ENTITY && result.field_72308_g instanceof EntityPlayer)
/*    */       return; 
/* 75 */     int xpSlot = InventoryUtil.findHotbarBlock(ItemExpBottle.class);
/* 76 */     boolean offhand = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151062_by);
/* 77 */     if (xpSlot != -1 || offhand) {
/* 78 */       int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
/* 79 */       if (!offhand) {
/* 80 */         InventoryUtil.switchToHotbarSlot(xpSlot, false);
/*    */       }
/* 82 */       mc.field_71442_b.func_187101_a((EntityPlayer)mc.field_71439_g, (World)mc.field_71441_e, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
/* 83 */       if (!offhand) {
/* 84 */         InventoryUtil.switchToHotbarSlot(oldslot, false);
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public enum Mode
/*    */   {
/* 91 */     MIDDLECLICK,
/* 92 */     TOGGLE,
/* 93 */     PRESS;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\SilentXP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */