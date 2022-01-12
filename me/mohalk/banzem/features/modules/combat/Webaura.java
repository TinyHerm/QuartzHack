/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.block.BlockWeb;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketChatMessage;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Webaura extends Module {
/*  26 */   private final Setting<Boolean> server = register(new Setting("Server", Boolean.valueOf(false))); public static boolean isPlacing = false;
/*  27 */   private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
/*  28 */   private final Setting<Integer> blocksPerPlace = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
/*  29 */   private final Setting<Double> targetRange = register(new Setting("TargetRange", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(20.0D)));
/*  30 */   private final Setting<Double> range = register(new Setting("PlaceRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D)));
/*  31 */   private final Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.CLOSEST));
/*  32 */   private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
/*  33 */   private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
/*  34 */   private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
/*  35 */   private final Setting<Double> speed = register(new Setting("Speed", Double.valueOf(30.0D), Double.valueOf(0.0D), Double.valueOf(30.0D)));
/*  36 */   private final Setting<Boolean> upperBody = register(new Setting("Upper", Boolean.valueOf(false)));
/*  37 */   private final Setting<Boolean> lowerbody = register(new Setting("Lower", Boolean.valueOf(true)));
/*  38 */   private final Setting<Boolean> ylower = register(new Setting("Y-1", Boolean.valueOf(false)));
/*  39 */   private final Setting<Boolean> antiSelf = register(new Setting("AntiSelf", Boolean.valueOf(false)));
/*  40 */   private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3)));
/*  41 */   private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false)));
/*  42 */   private final Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(false)));
/*  43 */   private final Setting<Boolean> disable = register(new Setting("TSelfMove", Boolean.valueOf(false)));
/*  44 */   private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
/*  45 */   private final Timer timer = new Timer();
/*     */   public EntityPlayer target;
/*     */   private boolean didPlace = false;
/*     */   private boolean switchedItem;
/*     */   private boolean isSneaking;
/*     */   private int lastHotbarSlot;
/*  51 */   private int placements = 0;
/*     */   private boolean smartRotate = false;
/*  53 */   private BlockPos startPos = null;
/*     */   
/*     */   public Webaura() {
/*  56 */     super("Webaura", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
/*     */   }
/*     */   
/*     */   private boolean shouldServer() {
/*  60 */     return (ServerModule.getInstance().isConnected() && ((Boolean)this.server.getValue()).booleanValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  65 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/*  68 */     this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.field_71439_g);
/*  69 */     this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*  70 */     if (shouldServer()) {
/*  71 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Serverprefix" + (String)(ClickGui.getInstance()).prefix.getValue()));
/*  72 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Server" + (String)(ClickGui.getInstance()).prefix.getValue() + "module Webaura set Enabled true"));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  78 */     if (((Integer)this.eventMode.getValue()).intValue() == 3) {
/*  79 */       this.smartRotate = false;
/*  80 */       doTrap();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/*  86 */     if (event.getStage() == 0 && ((Integer)this.eventMode.getValue()).intValue() == 2) {
/*  87 */       this.smartRotate = (((Boolean)this.rotate.getValue()).booleanValue() && ((Integer)this.blocksPerPlace.getValue()).intValue() == 1);
/*  88 */       doTrap();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  94 */     if (((Integer)this.eventMode.getValue()).intValue() == 1) {
/*  95 */       this.smartRotate = false;
/*  96 */       doTrap();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayInfo() {
/* 102 */     if (((Boolean)this.info.getValue()).booleanValue() && this.target != null) {
/* 103 */       return this.target.func_70005_c_();
/*     */     }
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 110 */     if (shouldServer()) {
/* 111 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Serverprefix" + (String)(ClickGui.getInstance()).prefix.getValue()));
/* 112 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Server" + (String)(ClickGui.getInstance()).prefix.getValue() + "module Webaura set Enabled false"));
/*     */       return;
/*     */     } 
/* 115 */     isPlacing = false;
/* 116 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 117 */     switchItem(true);
/*     */   }
/*     */   
/*     */   private void doTrap() {
/* 121 */     if (shouldServer() || check()) {
/*     */       return;
/*     */     }
/* 124 */     doWebTrap();
/* 125 */     if (this.didPlace) {
/* 126 */       this.timer.reset();
/*     */     }
/*     */   }
/*     */   
/*     */   private void doWebTrap() {
/* 131 */     List<Vec3d> placeTargets = getPlacements();
/* 132 */     placeList(placeTargets);
/*     */   }
/*     */   
/*     */   private List<Vec3d> getPlacements() {
/* 136 */     ArrayList<Vec3d> list = new ArrayList<>();
/* 137 */     Vec3d baseVec = this.target.func_174791_d();
/* 138 */     if (((Boolean)this.ylower.getValue()).booleanValue()) {
/* 139 */       list.add(baseVec.func_72441_c(0.0D, -1.0D, 0.0D));
/*     */     }
/* 141 */     if (((Boolean)this.lowerbody.getValue()).booleanValue()) {
/* 142 */       list.add(baseVec);
/*     */     }
/* 144 */     if (((Boolean)this.upperBody.getValue()).booleanValue()) {
/* 145 */       list.add(baseVec.func_72441_c(0.0D, 1.0D, 0.0D));
/*     */     }
/* 147 */     return list;
/*     */   }
/*     */   
/*     */   private void placeList(List<Vec3d> list) {
/* 151 */     list.sort((vec3d, vec3d2) -> Double.compare(mc.field_71439_g.func_70092_e(vec3d2.field_72450_a, vec3d2.field_72448_b, vec3d2.field_72449_c), mc.field_71439_g.func_70092_e(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c)));
/* 152 */     list.sort(Comparator.comparingDouble(vec3d -> vec3d.field_72448_b));
/* 153 */     for (Vec3d vec3d3 : list) {
/* 154 */       BlockPos position = new BlockPos(vec3d3);
/* 155 */       int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
/* 156 */       if ((placeability != 3 && placeability != 1) || (((Boolean)this.antiSelf.getValue()).booleanValue() && MathUtil.areVec3dsAligned(mc.field_71439_g.func_174791_d(), vec3d3)))
/*     */         continue; 
/* 158 */       placeBlock(position);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean check() {
/* 163 */     isPlacing = false;
/* 164 */     this.didPlace = false;
/* 165 */     this.placements = 0;
/* 166 */     int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
/* 167 */     if (isOff()) {
/* 168 */       return true;
/*     */     }
/* 170 */     if (((Boolean)this.disable.getValue()).booleanValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.field_71439_g))) {
/* 171 */       disable();
/* 172 */       return true;
/*     */     } 
/* 174 */     if (obbySlot == -1) {
/* 175 */       if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
/* 176 */         if (((Boolean)this.info.getValue()).booleanValue()) {
/* 177 */           Command.sendMessage("<" + getDisplayName() + "> §cYou are out of Webs.");
/*     */         }
/* 179 */         disable();
/*     */       } 
/* 181 */       return true;
/*     */     } 
/* 183 */     if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != obbySlot) {
/* 184 */       this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*     */     }
/* 186 */     switchItem(true);
/* 187 */     this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
/* 188 */     this.target = getTarget(((Double)this.targetRange.getValue()).doubleValue(), (this.targetMode.getValue() == TargetMode.UNTRAPPED));
/* 189 */     return (this.target == null || (Banzem.moduleManager.isModuleEnabled("Freecam") && !((Boolean)this.freecam.getValue()).booleanValue()) || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock(BlockWeb.class)));
/*     */   }
/*     */   
/*     */   private EntityPlayer getTarget(double range, boolean trapped) {
/* 193 */     EntityPlayer target = null;
/* 194 */     double distance = Math.pow(range, 2.0D) + 1.0D;
/* 195 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 196 */       if (EntityUtil.isntValid((Entity)player, range) || (trapped && player.field_70134_J) || (EntityUtil.getRoundedBlockPos((Entity)mc.field_71439_g).equals(EntityUtil.getRoundedBlockPos((Entity)player)) && ((Boolean)this.antiSelf.getValue()).booleanValue()) || Banzem.speedManager.getPlayerSpeed(player) > ((Double)this.speed.getValue()).doubleValue())
/*     */         continue; 
/* 198 */       if (target == null) {
/* 199 */         target = player;
/* 200 */         distance = mc.field_71439_g.func_70068_e((Entity)player);
/*     */         continue;
/*     */       } 
/* 203 */       if (mc.field_71439_g.func_70068_e((Entity)player) >= distance)
/* 204 */         continue;  target = player;
/* 205 */       distance = mc.field_71439_g.func_70068_e((Entity)player);
/*     */     } 
/* 207 */     return target;
/*     */   }
/*     */   
/*     */   private void placeBlock(BlockPos pos) {
/* 211 */     if (this.placements < ((Integer)this.blocksPerPlace.getValue()).intValue() && mc.field_71439_g.func_174818_b(pos) <= MathUtil.square(((Double)this.range.getValue()).doubleValue()) && switchItem(false)) {
/* 212 */       isPlacing = true;
/* 213 */       this.isSneaking = this.smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
/* 214 */       this.didPlace = true;
/* 215 */       this.placements++;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean switchItem(boolean back) {
/* 220 */     boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), BlockWeb.class);
/* 221 */     this.switchedItem = value[0];
/* 222 */     return value[1];
/*     */   }
/*     */   
/*     */   public enum TargetMode {
/* 226 */     CLOSEST,
/* 227 */     UNTRAPPED;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\Webaura.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */