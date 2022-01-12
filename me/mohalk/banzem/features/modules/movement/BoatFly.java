/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import net.minecraft.entity.item.EntityBoat;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketConfirmTeleport;
/*     */ import net.minecraft.network.play.client.CPacketVehicleMove;
/*     */ import net.minecraft.network.play.server.SPacketPlayerPosLook;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ 
/*     */ public class BoatFly
/*     */   extends Module
/*     */ {
/*  20 */   public Setting<Double> speed = register(new Setting("Speed", Double.valueOf(3.0D), Double.valueOf(1.0D), Double.valueOf(10.0D)));
/*  21 */   public Setting<Double> verticalSpeed = register(new Setting("VerticalSpeed", Double.valueOf(3.0D), Double.valueOf(1.0D), Double.valueOf(10.0D)));
/*  22 */   public Setting<Boolean> noKick = register(new Setting("No-Kick", Boolean.valueOf(true)));
/*  23 */   public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(true)));
/*  24 */   public Setting<Integer> packets = register(new Setting("Packets", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(5), v -> ((Boolean)this.packet.getValue()).booleanValue()));
/*  25 */   public Setting<Integer> interact = register(new Setting("Delay", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20)));
/*     */   public static BoatFly INSTANCE;
/*     */   private EntityBoat target;
/*     */   private int teleportID;
/*     */   
/*     */   public BoatFly() {
/*  31 */     super("BoatFly", "/fly but boat", Module.Category.MOVEMENT, true, false, false);
/*  32 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  37 */     if (mc.field_71439_g == null) {
/*     */       return;
/*     */     }
/*  40 */     if (mc.field_71441_e == null || mc.field_71439_g.func_184187_bx() == null) {
/*     */       return;
/*     */     }
/*  43 */     if (mc.field_71439_g.func_184187_bx() instanceof EntityBoat) {
/*  44 */       this.target = (EntityBoat)mc.field_71439_g.field_184239_as;
/*     */     }
/*  46 */     mc.field_71439_g.func_184187_bx().func_189654_d(true);
/*  47 */     (mc.field_71439_g.func_184187_bx()).field_70181_x = 0.0D;
/*  48 */     if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/*  49 */       (mc.field_71439_g.func_184187_bx()).field_70122_E = false;
/*  50 */       (mc.field_71439_g.func_184187_bx()).field_70181_x = ((Double)this.verticalSpeed.getValue()).doubleValue() / 10.0D;
/*     */     } 
/*  52 */     if (mc.field_71474_y.field_151444_V.func_151470_d()) {
/*  53 */       (mc.field_71439_g.func_184187_bx()).field_70122_E = false;
/*  54 */       (mc.field_71439_g.func_184187_bx()).field_70181_x = -(((Double)this.verticalSpeed.getValue()).doubleValue() / 10.0D);
/*     */     } 
/*  56 */     double[] normalDir = directionSpeed(((Double)this.speed.getValue()).doubleValue() / 2.0D);
/*  57 */     if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
/*  58 */       (mc.field_71439_g.func_184187_bx()).field_70159_w = normalDir[0];
/*  59 */       (mc.field_71439_g.func_184187_bx()).field_70179_y = normalDir[1];
/*     */     } else {
/*  61 */       (mc.field_71439_g.func_184187_bx()).field_70159_w = 0.0D;
/*  62 */       (mc.field_71439_g.func_184187_bx()).field_70179_y = 0.0D;
/*     */     } 
/*  64 */     if (((Boolean)this.noKick.getValue()).booleanValue()) {
/*  65 */       if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/*  66 */         if (mc.field_71439_g.field_70173_aa % 8 < 2) {
/*  67 */           (mc.field_71439_g.func_184187_bx()).field_70181_x = -0.03999999910593033D;
/*     */         }
/*  69 */       } else if (mc.field_71439_g.field_70173_aa % 8 < 4) {
/*  70 */         (mc.field_71439_g.func_184187_bx()).field_70181_x = -0.07999999821186066D;
/*     */       } 
/*     */     }
/*  73 */     handlePackets((mc.field_71439_g.func_184187_bx()).field_70159_w, (mc.field_71439_g.func_184187_bx()).field_70181_x, (mc.field_71439_g.func_184187_bx()).field_70179_y);
/*     */   }
/*     */   
/*     */   public void handlePackets(double x, double y, double z) {
/*  77 */     if (((Boolean)this.packet.getValue()).booleanValue()) {
/*  78 */       Vec3d vec = new Vec3d(x, y, z);
/*  79 */       if (mc.field_71439_g.func_184187_bx() == null) {
/*     */         return;
/*     */       }
/*  82 */       Vec3d position = mc.field_71439_g.func_184187_bx().func_174791_d().func_178787_e(vec);
/*  83 */       mc.field_71439_g.func_184187_bx().func_70107_b(position.field_72450_a, position.field_72448_b, position.field_72449_c);
/*  84 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketVehicleMove(mc.field_71439_g.func_184187_bx()));
/*  85 */       for (int i = 0; i < ((Integer)this.packets.getValue()).intValue(); i++) {
/*  86 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.teleportID++));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSendPacket(PacketEvent.Send event) {
/*  93 */     if (event.getPacket() instanceof CPacketVehicleMove && mc.field_71439_g.func_184218_aH() && mc.field_71439_g.field_70173_aa % ((Integer)this.interact.getValue()).intValue() == 0) {
/*  94 */       mc.field_71442_b.func_187097_a((EntityPlayer)mc.field_71439_g, mc.field_71439_g.field_184239_as, EnumHand.OFF_HAND);
/*     */     }
/*  96 */     if ((event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer.Rotation || event.getPacket() instanceof net.minecraft.network.play.client.CPacketInput) && mc.field_71439_g.func_184218_aH()) {
/*  97 */       event.setCanceled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onReceivePacket(PacketEvent.Receive event) {
/* 103 */     if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketMoveVehicle && mc.field_71439_g.func_184218_aH()) {
/* 104 */       event.setCanceled(true);
/*     */     }
/* 106 */     if (event.getPacket() instanceof SPacketPlayerPosLook) {
/* 107 */       this.teleportID = ((SPacketPlayerPosLook)event.getPacket()).field_186966_g;
/*     */     }
/*     */   }
/*     */   
/*     */   private double[] directionSpeed(double speed) {
/* 112 */     float forward = mc.field_71439_g.field_71158_b.field_192832_b;
/* 113 */     float side = mc.field_71439_g.field_71158_b.field_78902_a;
/* 114 */     float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
/* 115 */     if (forward != 0.0F) {
/* 116 */       if (side > 0.0F) {
/* 117 */         yaw += ((forward > 0.0F) ? -45 : 45);
/* 118 */       } else if (side < 0.0F) {
/* 119 */         yaw += ((forward > 0.0F) ? 45 : -45);
/*     */       } 
/* 121 */       side = 0.0F;
/* 122 */       if (forward > 0.0F) {
/* 123 */         forward = 1.0F;
/* 124 */       } else if (forward < 0.0F) {
/* 125 */         forward = -1.0F;
/*     */       } 
/*     */     } 
/* 128 */     double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
/* 129 */     double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
/* 130 */     double posX = forward * speed * cos + side * speed * sin;
/* 131 */     double posZ = forward * speed * sin - side * speed * cos;
/* 132 */     return new double[] { posX, posZ };
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\BoatFly.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */