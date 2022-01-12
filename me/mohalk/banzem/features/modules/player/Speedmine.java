/*     */ package me.mohalk.banzem.features.modules.player;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.BlockEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.Render3DEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.item.ItemSword;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketHeldItemChange;
/*     */ import net.minecraft.network.play.client.CPacketPlayerDigging;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Speedmine
/*     */   extends Module {
/*  31 */   private static Speedmine INSTANCE = new Speedmine();
/*  32 */   private final Setting<Float> range = register(new Setting("Range", Float.valueOf(10.0F), Float.valueOf(0.0F), Float.valueOf(50.0F)));
/*  33 */   private final Timer timer = new Timer();
/*  34 */   public Setting<Boolean> tweaks = register(new Setting("Tweaks", Boolean.valueOf(true)));
/*  35 */   public Setting<Mode> mode = register(new Setting("Mode", Mode.PACKET, v -> ((Boolean)this.tweaks.getValue()).booleanValue()));
/*  36 */   public Setting<Boolean> reset = register(new Setting("Reset", Boolean.valueOf(true)));
/*  37 */   public Setting<Float> damage = register(new Setting("Damage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> (this.mode.getValue() == Mode.DAMAGE && ((Boolean)this.tweaks.getValue()).booleanValue())));
/*  38 */   public Setting<Boolean> noBreakAnim = register(new Setting("NoBreakAnim", Boolean.valueOf(false)));
/*  39 */   public Setting<Boolean> noDelay = register(new Setting("NoDelay", Boolean.valueOf(false)));
/*  40 */   public Setting<Boolean> noSwing = register(new Setting("NoSwing", Boolean.valueOf(false)));
/*  41 */   public Setting<Boolean> noTrace = register(new Setting("NoTrace", Boolean.valueOf(false)));
/*  42 */   public Setting<Boolean> noGapTrace = register(new Setting("NoGapTrace", Boolean.valueOf(false), v -> ((Boolean)this.noTrace.getValue()).booleanValue()));
/*  43 */   public Setting<Boolean> allow = register(new Setting("AllowMultiTask", Boolean.valueOf(false)));
/*  44 */   public Setting<Boolean> pickaxe = register(new Setting("Pickaxe", Boolean.valueOf(true), v -> ((Boolean)this.noTrace.getValue()).booleanValue()));
/*  45 */   public Setting<Boolean> doubleBreak = register(new Setting("DoubleBreak", Boolean.valueOf(false)));
/*  46 */   public Setting<Boolean> webSwitch = register(new Setting("WebSwitch", Boolean.valueOf(false)));
/*  47 */   public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", Boolean.valueOf(false)));
/*  48 */   public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(false)));
/*  49 */   public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> ((Boolean)this.render.getValue()).booleanValue()));
/*  50 */   private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
/*  51 */   public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> ((Boolean)this.render.getValue()).booleanValue()));
/*  52 */   private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
/*     */   public BlockPos currentPos;
/*     */   public IBlockState currentBlockState;
/*     */   private boolean isMining = false;
/*  56 */   private BlockPos lastPos = null;
/*  57 */   private EnumFacing lastFacing = null;
/*     */   
/*     */   public Speedmine() {
/*  60 */     super("Speedmine", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
/*  61 */     setInstance();
/*     */   }
/*     */   
/*     */   public static Speedmine getInstance() {
/*  65 */     if (INSTANCE == null) {
/*  66 */       INSTANCE = new Speedmine();
/*     */     }
/*  68 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  72 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  77 */     if (this.currentPos != null) {
/*  78 */       if (mc.field_71439_g != null && mc.field_71439_g.func_174818_b(this.currentPos) > MathUtil.square(((Float)this.range.getValue()).floatValue())) {
/*  79 */         this.currentPos = null;
/*  80 */         this.currentBlockState = null;
/*     */         return;
/*     */       } 
/*  83 */       if (mc.field_71439_g != null && ((Boolean)this.silentSwitch.getValue()).booleanValue() && this.timer.passedMs((int)(2000.0F * Banzem.serverManager.getTpsFactor())) && getPickSlot() != -1) {
/*  84 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(getPickSlot()));
/*     */       }
/*  86 */       if (!mc.field_71441_e.func_180495_p(this.currentPos).equals(this.currentBlockState) || mc.field_71441_e.func_180495_p(this.currentPos).func_177230_c() == Blocks.field_150350_a) {
/*  87 */         this.currentPos = null;
/*  88 */         this.currentBlockState = null;
/*  89 */       } else if (((Boolean)this.webSwitch.getValue()).booleanValue() && this.currentBlockState.func_177230_c() == Blocks.field_150321_G && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemPickaxe) {
/*  90 */         InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  97 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/* 100 */     if (((Boolean)this.noDelay.getValue()).booleanValue()) {
/* 101 */       mc.field_71442_b.field_78781_i = 0;
/*     */     }
/* 103 */     if (this.isMining && this.lastPos != null && this.lastFacing != null && ((Boolean)this.noBreakAnim.getValue()).booleanValue()) {
/* 104 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
/*     */     }
/* 106 */     if (((Boolean)this.reset.getValue()).booleanValue() && mc.field_71474_y.field_74313_G.func_151470_d() && !((Boolean)this.allow.getValue()).booleanValue()) {
/* 107 */       mc.field_71442_b.field_78778_j = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRender3D(Render3DEvent event) {
/* 113 */     if (((Boolean)this.render.getValue()).booleanValue() && this.currentPos != null) {
/* 114 */       Color color = new Color(this.timer.passedMs((int)(2000.0F * Banzem.serverManager.getTpsFactor())) ? 0 : 255, this.timer.passedMs((int)(2000.0F * Banzem.serverManager.getTpsFactor())) ? 255 : 0, 0, 255);
/* 115 */       RenderUtil.drawBoxESP(this.currentPos, color, false, color, ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketSend(PacketEvent.Send event) {
/* 121 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/* 124 */     if (event.getStage() == 0) {
/*     */       
/* 126 */       if (((Boolean)this.noSwing.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation)
/* 127 */         event.setCanceled(true); 
/*     */       CPacketPlayerDigging packet;
/* 129 */       if (((Boolean)this.noBreakAnim.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)event.getPacket()) != null && packet.func_179715_a() != null) {
/*     */         try {
/* 131 */           for (Entity entity : mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(packet.func_179715_a()))) {
/* 132 */             if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
/* 133 */               continue;  showAnimation();
/*     */             return;
/*     */           } 
/* 136 */         } catch (Exception exception) {}
/*     */ 
/*     */         
/* 139 */         if (packet.func_180762_c().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
/* 140 */           showAnimation(true, packet.func_179715_a(), packet.func_179714_b());
/*     */         }
/* 142 */         if (packet.func_180762_c().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
/* 143 */           showAnimation();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onBlockEvent(BlockEvent event) {
/* 151 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/* 154 */     if (event.getStage() == 3 && mc.field_71441_e.func_180495_p(event.pos).func_177230_c() instanceof net.minecraft.block.BlockEndPortalFrame) {
/* 155 */       mc.field_71441_e.func_180495_p(event.pos).func_177230_c().func_149711_c(50.0F);
/*     */     }
/* 157 */     if (event.getStage() == 3 && ((Boolean)this.reset.getValue()).booleanValue() && mc.field_71442_b.field_78770_f > 0.1F) {
/* 158 */       mc.field_71442_b.field_78778_j = true;
/*     */     }
/* 160 */     if (event.getStage() == 4 && ((Boolean)this.tweaks.getValue()).booleanValue()) {
/*     */       
/* 162 */       if (BlockUtil.canBreak(event.pos)) {
/* 163 */         if (((Boolean)this.reset.getValue()).booleanValue()) {
/* 164 */           mc.field_71442_b.field_78778_j = false;
/*     */         }
/* 166 */         switch ((Mode)this.mode.getValue()) {
/*     */           case PACKET:
/* 168 */             if (this.currentPos == null) {
/* 169 */               this.currentPos = event.pos;
/* 170 */               this.currentBlockState = mc.field_71441_e.func_180495_p(this.currentPos);
/* 171 */               this.timer.reset();
/*     */             } 
/* 173 */             mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 174 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
/* 175 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
/* 176 */             event.setCanceled(true);
/*     */             break;
/*     */           
/*     */           case DAMAGE:
/* 180 */             if (mc.field_71442_b.field_78770_f < ((Float)this.damage.getValue()).floatValue())
/*     */               break; 
/* 182 */             mc.field_71442_b.field_78770_f = 1.0F;
/*     */             break;
/*     */           
/*     */           case INSTANT:
/* 186 */             mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 187 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
/* 188 */             mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
/* 189 */             mc.field_71442_b.func_187103_a(event.pos);
/* 190 */             mc.field_71441_e.func_175698_g(event.pos); break;
/*     */         } 
/*     */       } 
/*     */       BlockPos above;
/* 194 */       if (((Boolean)this.doubleBreak.getValue()).booleanValue() && BlockUtil.canBreak(above = event.pos.func_177982_a(0, 1, 0)) && mc.field_71439_g.func_70011_f(above.func_177958_n(), above.func_177956_o(), above.func_177952_p()) <= 5.0D) {
/* 195 */         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/* 196 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
/* 197 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
/* 198 */         mc.field_71442_b.func_187103_a(above);
/* 199 */         mc.field_71441_e.func_175698_g(above);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getPickSlot() {
/* 205 */     for (int i = 0; i < 9; ) {
/* 206 */       if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() != Items.field_151046_w) { i++; continue; }
/* 207 */        return i;
/*     */     } 
/* 209 */     return -1;
/*     */   }
/*     */   
/*     */   private void showAnimation(boolean isMining, BlockPos lastPos, EnumFacing lastFacing) {
/* 213 */     this.isMining = isMining;
/* 214 */     this.lastPos = lastPos;
/* 215 */     this.lastFacing = lastFacing;
/*     */   }
/*     */   
/*     */   public void showAnimation() {
/* 219 */     showAnimation(false, (BlockPos)null, (EnumFacing)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/* 224 */     return this.mode.currentEnumName();
/*     */   }
/*     */   
/*     */   public enum Mode {
/* 228 */     PACKET,
/* 229 */     DAMAGE,
/* 230 */     INSTANT;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\Speedmine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */