/*    */ package me.mohalk.banzem.features.modules.player;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.InventoryUtil;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.init.Items;
/*    */ import net.minecraft.item.ItemEnderPearl;
/*    */ import net.minecraft.util.EnumHand;
/*    */ import net.minecraft.util.math.RayTraceResult;
/*    */ import net.minecraft.world.World;
/*    */ import org.lwjgl.input.Mouse;
/*    */ 
/*    */ public class MCP extends Module {
/* 16 */   private final Setting<Mode> mode = register(new Setting("Mode", Mode.MIDDLECLICK));
/* 17 */   private final Setting<Boolean> stopRotation = register(new Setting("Rotation", Boolean.valueOf(true)));
/* 18 */   private final Setting<Boolean> antiFriend = register(new Setting("AntiFriend", Boolean.valueOf(true)));
/* 19 */   private final Setting<Integer> rotation = register(new Setting("Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.stopRotation.getValue()).booleanValue()));
/*    */   private boolean clicked = false;
/*    */   
/*    */   public MCP() {
/* 23 */     super("MCP", "Throws a pearl", Module.Category.PLAYER, false, false, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 28 */     if (!fullNullCheck() && this.mode.getValue() == Mode.TOGGLE) {
/* 29 */       throwPearl();
/* 30 */       disable();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onTick() {
/* 36 */     if (this.mode.getValue() == Mode.MIDDLECLICK) {
/* 37 */       if (Mouse.isButtonDown(2)) {
/* 38 */         if (!this.clicked) {
/* 39 */           throwPearl();
/*    */         }
/* 41 */         this.clicked = true;
/*    */       } else {
/* 43 */         this.clicked = false;
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   private void throwPearl() {
/*    */     Entity entity;
/*    */     RayTraceResult result;
/* 52 */     if (((Boolean)this.antiFriend.getValue()).booleanValue() && (result = mc.field_71476_x) != null && result.field_72313_a == RayTraceResult.Type.ENTITY && entity = result.field_72308_g instanceof EntityPlayer) {
/*    */       return;
/*    */     }
/* 55 */     int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
/* 56 */     boolean offhand = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151079_bi), bl = offhand;
/* 57 */     if (pearlSlot != -1 || offhand) {
/* 58 */       int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
/* 59 */       if (!offhand) {
/* 60 */         InventoryUtil.switchToHotbarSlot(pearlSlot, false);
/*    */       }
/* 62 */       mc.field_71442_b.func_187101_a((EntityPlayer)mc.field_71439_g, (World)mc.field_71441_e, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
/* 63 */       if (!offhand)
/* 64 */         InventoryUtil.switchToHotbarSlot(oldslot, false); 
/*    */     } 
/*    */   }
/*    */   
/*    */   public enum Mode
/*    */   {
/* 70 */     TOGGLE,
/* 71 */     MIDDLECLICK;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\MCP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */