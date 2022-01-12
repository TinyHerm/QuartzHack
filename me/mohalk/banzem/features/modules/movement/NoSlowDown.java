/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import me.mohalk.banzem.event.events.KeyEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import net.minecraft.client.settings.KeyBinding;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketEntityAction;
/*     */ import net.minecraft.network.play.client.CPacketPlayerDigging;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraftforge.client.event.InputUpdateEvent;
/*     */ import net.minecraftforge.event.entity.player.PlayerInteractEvent;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NoSlowDown
/*     */   extends Module
/*     */ {
/*  26 */   private static NoSlowDown INSTANCE = new NoSlowDown();
/*  27 */   private static final KeyBinding[] keys = new KeyBinding[] { mc.field_71474_y.field_74351_w, mc.field_71474_y.field_74368_y, mc.field_71474_y.field_74370_x, mc.field_71474_y.field_74366_z, mc.field_71474_y.field_74314_A, mc.field_71474_y.field_151444_V };
/*  28 */   public final Setting<Double> webHorizontalFactor = register(new Setting("WebHSpeed", Double.valueOf(2.0D), Double.valueOf(0.0D), Double.valueOf(100.0D)));
/*  29 */   public final Setting<Double> webVerticalFactor = register(new Setting("WebVSpeed", Double.valueOf(2.0D), Double.valueOf(0.0D), Double.valueOf(100.0D)));
/*  30 */   public Setting<Boolean> guiMove = register(new Setting("GuiMove", Boolean.valueOf(true)));
/*  31 */   public Setting<Boolean> noSlow = register(new Setting("NoSlow", Boolean.valueOf(true)));
/*  32 */   public Setting<Boolean> soulSand = register(new Setting("SoulSand", Boolean.valueOf(true)));
/*  33 */   public Setting<Boolean> strict = register(new Setting("Strict", Boolean.valueOf(false)));
/*  34 */   public Setting<Boolean> sneakPacket = register(new Setting("SneakPacket", Boolean.valueOf(false)));
/*  35 */   public Setting<Boolean> endPortal = register(new Setting("EndPortal", Boolean.valueOf(false)));
/*  36 */   public Setting<Boolean> webs = register(new Setting("Webs", Boolean.valueOf(false)));
/*     */   private boolean sneaking = false;
/*     */   
/*     */   public NoSlowDown() {
/*  40 */     super("NoSlowDown", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
/*  41 */     setInstance();
/*     */   }
/*     */   
/*     */   public static NoSlowDown getInstance() {
/*  45 */     if (INSTANCE == null) {
/*  46 */       INSTANCE = new NoSlowDown();
/*     */     }
/*  48 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  52 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  57 */     if (((Boolean)this.guiMove.getValue()).booleanValue())
/*  58 */       if (mc.field_71462_r instanceof net.minecraft.client.gui.GuiOptions || mc.field_71462_r instanceof net.minecraft.client.gui.GuiVideoSettings || mc.field_71462_r instanceof net.minecraft.client.gui.GuiScreenOptionsSounds || mc.field_71462_r instanceof net.minecraft.client.gui.inventory.GuiContainer || mc.field_71462_r instanceof net.minecraft.client.gui.GuiIngameMenu) {
/*  59 */         for (KeyBinding bind : keys) {
/*  60 */           KeyBinding.func_74510_a(bind.func_151463_i(), Keyboard.isKeyDown(bind.func_151463_i()));
/*     */         }
/*  62 */       } else if (mc.field_71462_r == null) {
/*  63 */         for (KeyBinding bind : keys) {
/*  64 */           if (!Keyboard.isKeyDown(bind.func_151463_i())) {
/*  65 */             KeyBinding.func_74510_a(bind.func_151463_i(), false);
/*     */           }
/*     */         } 
/*     */       }  
/*  69 */     if (((Boolean)this.webs.getValue()).booleanValue() && mc.field_71439_g.field_70134_J) {
/*  70 */       mc.field_71439_g.field_70159_w *= ((Double)this.webHorizontalFactor.getValue()).doubleValue();
/*  71 */       mc.field_71439_g.field_70179_y *= ((Double)this.webHorizontalFactor.getValue()).doubleValue();
/*  72 */       mc.field_71439_g.field_70181_x *= ((Double)this.webVerticalFactor.getValue()).doubleValue();
/*     */     } 
/*  74 */     Item item = mc.field_71439_g.func_184607_cu().func_77973_b();
/*  75 */     if (this.sneaking && !mc.field_71439_g.func_184587_cr() && ((Boolean)this.sneakPacket.getValue()).booleanValue()) {
/*  76 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
/*  77 */       this.sneaking = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUseItem(PlayerInteractEvent.RightClickItem event) {
/*  83 */     Item item = mc.field_71439_g.func_184586_b(event.getHand()).func_77973_b();
/*  84 */     if ((item instanceof net.minecraft.item.ItemFood || item instanceof net.minecraft.item.ItemBow || (item instanceof net.minecraft.item.ItemPotion && ((Boolean)this.sneakPacket.getValue()).booleanValue())) && !this.sneaking) {
/*  85 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
/*  86 */       this.sneaking = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onInput(InputUpdateEvent event) {
/*  92 */     if (((Boolean)this.noSlow.getValue()).booleanValue() && mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.func_184218_aH()) {
/*  93 */       (event.getMovementInput()).field_78902_a *= 5.0F;
/*  94 */       (event.getMovementInput()).field_192832_b *= 5.0F;
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onKeyEvent(KeyEvent event) {
/* 100 */     if (((Boolean)this.guiMove.getValue()).booleanValue() && event.getStage() == 0 && !(mc.field_71462_r instanceof net.minecraft.client.gui.GuiChat)) {
/* 101 */       event.info = event.pressed;
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacket(PacketEvent.Send event) {
/* 107 */     if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer && ((Boolean)this.strict.getValue()).booleanValue() && ((Boolean)this.noSlow.getValue()).booleanValue() && mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.func_184218_aH())
/* 108 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v)), EnumFacing.DOWN)); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\NoSlowDown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */