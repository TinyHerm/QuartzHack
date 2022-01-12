/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockLagUtil;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.BlockChest;
/*     */ import net.minecraft.block.BlockEnderChest;
/*     */ import net.minecraft.block.BlockObsidian;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketPlayer;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ 
/*     */ public class BlockLag
/*     */   extends Module
/*     */ {
/*     */   private final Setting<Integer> offset;
/*     */   private final Setting<Boolean> rotate;
/*     */   private final Setting<Mode> mode;
/*     */   private BlockPos originalPos;
/*     */   private int oldSlot;
/*     */   Block returnBlock;
/*     */   
/*     */   public BlockLag() {
/*  31 */     super("Burrow", "TPs you into a block", Module.Category.MOVEMENT, true, false, false);
/*  32 */     this.offset = register(new Setting("Offset", Integer.valueOf(3), Integer.valueOf(-10), Integer.valueOf(10)));
/*  33 */     this.rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
/*  34 */     this.mode = register(new Setting("Mode", Mode.OBBY));
/*  35 */     this.oldSlot = -1;
/*  36 */     this.returnBlock = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  41 */     super.onEnable();
/*  42 */     this.originalPos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
/*  43 */     switch ((Mode)this.mode.getValue()) {
/*     */       case OBBY:
/*  45 */         this.returnBlock = Blocks.field_150343_Z;
/*     */         break;
/*     */       
/*     */       case ECHEST:
/*  49 */         this.returnBlock = Blocks.field_150477_bB;
/*     */         break;
/*     */       
/*     */       case EABypass:
/*  53 */         this.returnBlock = (Block)Blocks.field_150486_ae;
/*     */         break;
/*     */     } 
/*     */     
/*  57 */     if (mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)).func_177230_c().equals(this.returnBlock) || intersectsWithEntity(this.originalPos)) {
/*  58 */       toggle();
/*     */       return;
/*     */     } 
/*  61 */     this.oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  66 */     switch ((Mode)this.mode.getValue()) {
/*     */       case OBBY:
/*  68 */         if (BlockLagUtil.findHotbarBlock(BlockObsidian.class) == -1) {
/*  69 */           Command.sendMessage("Can't find obby in hotbar!");
/*  70 */           disable();
/*     */           return;
/*     */         } 
/*     */         break;
/*     */       
/*     */       case ECHEST:
/*  76 */         if (BlockLagUtil.findHotbarBlock(BlockEnderChest.class) == -1) {
/*  77 */           Command.sendMessage("Can't find echest in hotbar!");
/*  78 */           disable();
/*     */           return;
/*     */         } 
/*     */         break;
/*     */       
/*     */       case EABypass:
/*  84 */         if (BlockLagUtil.findHotbarBlock(BlockChest.class) == -1) {
/*  85 */           Command.sendMessage("Can't find chest in hotbar!");
/*  86 */           disable();
/*     */           return;
/*     */         } 
/*     */         break;
/*     */     } 
/*     */     
/*  92 */     BlockLagUtil.switchToSlot((this.mode.getValue() == Mode.OBBY) ? BlockLagUtil.findHotbarBlock(BlockObsidian.class) : ((this.mode.getValue() == Mode.ECHEST) ? BlockLagUtil.findHotbarBlock(BlockEnderChest.class) : BlockLagUtil.findHotbarBlock(BlockChest.class)));
/*  93 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.41999998688698D, mc.field_71439_g.field_70161_v, true));
/*  94 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.7531999805211997D, mc.field_71439_g.field_70161_v, true));
/*  95 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.00133597911214D, mc.field_71439_g.field_70161_v, true));
/*  96 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.16610926093821D, mc.field_71439_g.field_70161_v, true));
/*  97 */     BlockLagUtil.placeBlock(this.originalPos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), true, false);
/*  98 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + ((Integer)this.offset.getValue()).intValue(), mc.field_71439_g.field_70161_v, false));
/*  99 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
/* 100 */     mc.field_71439_g.func_70095_a(false);
/* 101 */     BlockLagUtil.switchToSlot(this.oldSlot);
/* 102 */     toggle();
/*     */   }
/*     */   
/*     */   private boolean intersectsWithEntity(BlockPos pos) {
/* 106 */     for (Entity entity : mc.field_71441_e.field_72996_f) {
/* 107 */       if (entity.equals(mc.field_71439_g)) {
/*     */         continue;
/*     */       }
/* 110 */       if (entity instanceof net.minecraft.entity.item.EntityItem) {
/*     */         continue;
/*     */       }
/* 113 */       if ((new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ())) {
/* 114 */         return true;
/*     */       }
/*     */     } 
/* 117 */     return false;
/*     */   }
/*     */   
/*     */   public enum Mode
/*     */   {
/* 122 */     OBBY,
/* 123 */     ECHEST,
/* 124 */     EABypass;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\BlockLag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */