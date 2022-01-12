/*    */ package me.mohalk.banzem.features.modules.player;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.InventoryUtil;
/*    */ import net.minecraft.block.BlockEnderChest;
/*    */ import net.minecraft.block.BlockObsidian;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.init.Items;
/*    */ import net.minecraft.item.ItemEndCrystal;
/*    */ import net.minecraft.item.ItemExpBottle;
/*    */ import net.minecraft.item.ItemMinecart;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
/*    */ import net.minecraft.util.EnumFacing;
/*    */ import net.minecraft.util.EnumHand;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.math.RayTraceResult;
/*    */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*    */ 
/*    */ public class FastPlace extends Module {
/* 24 */   private final Setting<Boolean> all = register(new Setting("All", Boolean.valueOf(false)));
/* 25 */   private final Setting<Boolean> obby = register(new Setting("Obsidian", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
/* 26 */   private final Setting<Boolean> enderChests = register(new Setting("EnderChests", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
/* 27 */   private final Setting<Boolean> crystals = register(new Setting("Crystals", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
/* 28 */   private final Setting<Boolean> exp = register(new Setting("Experience", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
/* 29 */   private final Setting<Boolean> Minecart = register(new Setting("Minecarts", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
/* 30 */   private final Setting<Boolean> feetExp = register(new Setting("ExpFeet", Boolean.valueOf(false)));
/* 31 */   private final Setting<Boolean> fastCrystal = register(new Setting("PacketCrystal", Boolean.valueOf(false)));
/* 32 */   private BlockPos mousePos = null;
/*    */   
/*    */   public FastPlace() {
/* 35 */     super("FastPlace", "Fast everything.", Module.Category.PLAYER, true, false, false);
/*    */   }
/*    */   
/*    */   @SubscribeEvent
/*    */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/* 40 */     if (event.getStage() == 0 && ((Boolean)this.feetExp.getValue()).booleanValue()) {
/*    */       
/* 42 */       boolean mainHand = (mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151062_by);
/* 43 */       boolean offHand = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151062_by), bl = offHand;
/* 44 */       if (mc.field_71474_y.field_74313_G.func_151470_d() && ((mc.field_71439_g.func_184600_cs() == EnumHand.MAIN_HAND && mainHand) || (mc.field_71439_g.func_184600_cs() == EnumHand.OFF_HAND && offHand))) {
/* 45 */         Banzem.rotationManager.lookAtVec3d(mc.field_71439_g.func_174791_d());
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 52 */     if (fullNullCheck()) {
/*    */       return;
/*    */     }
/* 55 */     if (InventoryUtil.holdingItem(ItemExpBottle.class) && ((Boolean)this.exp.getValue()).booleanValue()) {
/* 56 */       mc.field_71467_ac = 0;
/*    */     }
/* 58 */     if (InventoryUtil.holdingItem(BlockObsidian.class) && ((Boolean)this.obby.getValue()).booleanValue()) {
/* 59 */       mc.field_71467_ac = 0;
/*    */     }
/* 61 */     if (InventoryUtil.holdingItem(BlockEnderChest.class) && ((Boolean)this.enderChests.getValue()).booleanValue()) {
/* 62 */       mc.field_71467_ac = 0;
/*    */     }
/* 64 */     if (InventoryUtil.holdingItem(ItemMinecart.class) && ((Boolean)this.Minecart.getValue()).booleanValue()) {
/* 65 */       mc.field_71467_ac = 0;
/*    */     }
/* 67 */     if (((Boolean)this.all.getValue()).booleanValue()) {
/* 68 */       mc.field_71467_ac = 0;
/*    */     }
/* 70 */     if (InventoryUtil.holdingItem(ItemEndCrystal.class) && (((Boolean)this.crystals.getValue()).booleanValue() || ((Boolean)this.all.getValue()).booleanValue())) {
/* 71 */       mc.field_71467_ac = 0;
/*    */     }
/* 73 */     if (((Boolean)this.fastCrystal.getValue()).booleanValue() && mc.field_71474_y.field_74313_G.func_151470_d()) {
/*    */       
/* 75 */       boolean offhand = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP), bl = offhand;
/* 76 */       if (offhand || mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
/* 77 */         Entity entity; RayTraceResult result = mc.field_71476_x;
/* 78 */         if (result == null) {
/*    */           return;
/*    */         }
/* 81 */         switch (result.field_72313_a) {
/*    */           case MISS:
/* 83 */             this.mousePos = null;
/*    */             break;
/*    */           
/*    */           case BLOCK:
/* 87 */             this.mousePos = mc.field_71476_x.func_178782_a();
/*    */             break;
/*    */ 
/*    */           
/*    */           case ENTITY:
/* 92 */             if (this.mousePos == null || (entity = result.field_72308_g) == null || !this.mousePos.equals(new BlockPos(entity.field_70165_t, entity.field_70163_u - 1.0D, entity.field_70161_v)))
/*    */               break; 
/* 94 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
/*    */             break;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\FastPlace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */