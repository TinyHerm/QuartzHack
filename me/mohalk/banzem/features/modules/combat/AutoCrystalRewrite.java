/*      */ package me.mohalk.banzem.features.modules.combat;
/*      */ import com.mojang.authlib.GameProfile;
/*      */ import java.awt.Color;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentLinkedQueue;
/*      */ import java.util.concurrent.ScheduledExecutorService;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import me.mohalk.banzem.Banzem;
/*      */ import me.mohalk.banzem.event.events.ClientEvent;
/*      */ import me.mohalk.banzem.event.events.PacketEvent;
/*      */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*      */ import me.mohalk.banzem.features.command.Command;
/*      */ import me.mohalk.banzem.features.modules.Module;
/*      */ import me.mohalk.banzem.features.setting.Bind;
/*      */ import me.mohalk.banzem.features.setting.Setting;
/*      */ import me.mohalk.banzem.util.BlockUtil;
/*      */ import me.mohalk.banzem.util.DamageUtil;
/*      */ import me.mohalk.banzem.util.EntityUtil;
/*      */ import me.mohalk.banzem.util.InventoryUtil;
/*      */ import me.mohalk.banzem.util.MathUtil;
/*      */ import me.mohalk.banzem.util.RenderUtil;
/*      */ import me.mohalk.banzem.util.Timer;
/*      */ import net.minecraft.block.state.IBlockState;
/*      */ import net.minecraft.client.entity.EntityOtherPlayerMP;
/*      */ import net.minecraft.entity.Entity;
/*      */ import net.minecraft.entity.item.EntityEnderCrystal;
/*      */ import net.minecraft.entity.player.EntityPlayer;
/*      */ import net.minecraft.init.Items;
/*      */ import net.minecraft.network.Packet;
/*      */ import net.minecraft.network.play.client.CPacketPlayer;
/*      */ import net.minecraft.network.play.client.CPacketUseEntity;
/*      */ import net.minecraft.network.play.server.SPacketDestroyEntities;
/*      */ import net.minecraft.network.play.server.SPacketEntityStatus;
/*      */ import net.minecraft.network.play.server.SPacketExplosion;
/*      */ import net.minecraft.network.play.server.SPacketSoundEffect;
/*      */ import net.minecraft.network.play.server.SPacketSpawnObject;
/*      */ import net.minecraft.util.EnumFacing;
/*      */ import net.minecraft.util.EnumHand;
/*      */ import net.minecraft.util.SoundCategory;
/*      */ import net.minecraft.util.math.AxisAlignedBB;
/*      */ import net.minecraft.util.math.BlockPos;
/*      */ import net.minecraft.util.math.RayTraceResult;
/*      */ import net.minecraft.util.math.Vec3d;
/*      */ import net.minecraft.world.World;
/*      */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*      */ import org.lwjgl.input.Keyboard;
/*      */ import org.lwjgl.input.Mouse;
/*      */ 
/*      */ public class AutoCrystalRewrite extends Module {
/*   56 */   public static Set<BlockPos> lowDmgPos = (Set<BlockPos>)new ConcurrentSet(); public static EntityPlayer target;
/*   57 */   public static Set<BlockPos> placedPos = new HashSet<>();
/*   58 */   public static Set<BlockPos> brokenPos = new HashSet<>();
/*      */   private static AutoCrystalRewrite instance;
/*   60 */   private final Timer switchTimer = new Timer();
/*   61 */   private final Timer manualTimer = new Timer();
/*   62 */   private final Timer breakTimer = new Timer();
/*   63 */   private final Timer placeTimer = new Timer();
/*   64 */   private final Timer syncTimer = new Timer();
/*   65 */   private final Timer predictTimer = new Timer();
/*   66 */   private final Timer renderTimer = new Timer();
/*   67 */   private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
/*   68 */   private final Timer syncroTimer = new Timer();
/*   69 */   private final Map<EntityPlayer, Timer> totemPops = new ConcurrentHashMap<>();
/*   70 */   private final Queue<CPacketUseEntity> packetUseEntities = new LinkedList<>();
/*   71 */   private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
/*   72 */   private final List<RenderPos> positions = new ArrayList<>();
/*   73 */   private final Setting<Settings> setting = register(new Setting("Settings", Settings.PLACE));
/*      */   
/*   75 */   public Setting<Boolean> place = register(new Setting("Place", Boolean.TRUE, v -> (this.setting.getValue() == Settings.PLACE)));
/*   76 */   public Setting<Integer> placeDelay = register(new Setting("PlaceDelay", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   77 */   public Setting<Float> placeRange = register(new Setting("PlaceRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   78 */   public Setting<Float> minDamage = register(new Setting("MinDamage", Float.valueOf(7.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   79 */   public Setting<Float> maxSelfPlace = register(new Setting("MaxSelfPlace", Float.valueOf(10.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   80 */   public Setting<Integer> wasteAmount = register(new Setting("WasteAmount", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   81 */   public Setting<Boolean> wasteMinDmgCount = register(new Setting("CountMinDmg", Boolean.TRUE, v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   82 */   public Setting<Float> facePlace = register(new Setting("FacePlace", Float.valueOf(8.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   83 */   public Setting<Float> placetrace = register(new Setting("Placetrace", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK)));
/*   84 */   public Setting<Boolean> antiSurround = register(new Setting("AntiSurround", Boolean.TRUE, v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   85 */   public Setting<Boolean> limitFacePlace = register(new Setting("LimitFacePlace", Boolean.TRUE, v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   86 */   public Setting<Boolean> oneDot15 = register(new Setting("1.15", Boolean.FALSE, v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   87 */   public Setting<Boolean> doublePop = register(new Setting("AntiTotem", Boolean.FALSE, v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   88 */   public Setting<Double> popHealth = register(new Setting("PopHealth", Double.valueOf(1.0D), Double.valueOf(0.0D), Double.valueOf(3.0D), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
/*   89 */   public Setting<Float> popDamage = register(new Setting("PopDamage", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
/*   90 */   public Setting<Integer> popTime = register(new Setting("PopTime", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
/*   91 */   public Setting<Boolean> doublePopOnDamage = register(new Setting("DamagePop", Boolean.FALSE, v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue() && this.targetMode.getValue() == Target.DAMAGE)));
/*      */   
/*   93 */   public Setting<Boolean> explode = register(new Setting("Break", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK)));
/*   94 */   public Setting<Switch> switchMode = register(new Setting("Attack", Switch.BREAKSLOT, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   95 */   public Setting<Integer> breakDelay = register(new Setting("BreakDelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   96 */   public Setting<Float> breakRange = register(new Setting("BreakRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   97 */   public Setting<Integer> packets = register(new Setting("Packets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(6), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   98 */   public Setting<Float> maxSelfBreak = register(new Setting("MaxSelfBreak", Float.valueOf(10.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   99 */   public Setting<Float> breaktrace = register(new Setting("Breaktrace", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE)));
/*  100 */   public Setting<Boolean> manual = register(new Setting("Manual", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK)));
/*  101 */   public Setting<Boolean> manualMinDmg = register(new Setting("ManMinDmg", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.manual.getValue()).booleanValue())));
/*  102 */   public Setting<Integer> manualBreak = register(new Setting("ManualDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.manual.getValue()).booleanValue())));
/*  103 */   public Setting<Boolean> sync = register(new Setting("Sync", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK && (((Boolean)this.explode.getValue()).booleanValue() || ((Boolean)this.manual.getValue()).booleanValue()))));
/*  104 */   public Setting<Boolean> instant = register(new Setting("Predict", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue())));
/*  105 */   public Setting<PredictTimer> instantTimer = register(new Setting("PredictTimer", PredictTimer.NONE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  106 */   public Setting<Boolean> resetBreakTimer = register(new Setting("ResetBreakTimer", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  107 */   public Setting<Integer> predictDelay = register(new Setting("PredictDelay", Integer.valueOf(12), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue() && this.instantTimer.getValue() == PredictTimer.PREDICT)));
/*  108 */   public Setting<Boolean> predictCalc = register(new Setting("PredictCalc", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  109 */   public Setting<Boolean> superSafe = register(new Setting("SuperSafe", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  110 */   public Setting<Boolean> antiCommit = register(new Setting("AntiOverCommit", Boolean.TRUE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*      */   
/*  112 */   public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER)));
/*  113 */   public Setting<Boolean> justRender = register(new Setting("JustRender", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  114 */   public Setting<RenderMode> renderMode = register(new Setting("Mode", RenderMode.STATIC, v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  115 */   private final Setting<Boolean> fadeFactor = register(new Setting("Fade", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && ((Boolean)this.render.getValue()).booleanValue())));
/*  116 */   private final Setting<Boolean> scaleFactor = register(new Setting("Shrink", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && ((Boolean)this.render.getValue()).booleanValue())));
/*  117 */   private final Setting<Boolean> slabFactor = register(new Setting("Slab", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && ((Boolean)this.render.getValue()).booleanValue())));
/*  118 */   private final Setting<Boolean> onlyplaced = register(new Setting("OnlyPlaced", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && ((Boolean)this.render.getValue()).booleanValue())));
/*  119 */   private final Setting<Float> duration = register(new Setting("Duration", Float.valueOf(1500.0F), Float.valueOf(0.0F), Float.valueOf(5000.0F), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && ((Boolean)this.render.getValue()).booleanValue())));
/*  120 */   private final Setting<Integer> max = register(new Setting("MaxPositions", Integer.valueOf(15), Integer.valueOf(1), Integer.valueOf(30), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && ((Boolean)this.render.getValue()).booleanValue())));
/*  121 */   private final Setting<Float> slabHeight = register(new Setting("SlabDepth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(1.0F), v -> (this.setting.getValue() == Settings.RENDER && (this.renderMode.getValue() == RenderMode.STATIC || this.renderMode.getValue() == RenderMode.GLIDE) && ((Boolean)this.render.getValue()).booleanValue())));
/*  122 */   private final Setting<Float> moveSpeed = register(new Setting("Speed", Float.valueOf(900.0F), Float.valueOf(0.0F), Float.valueOf(1500.0F), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GLIDE && ((Boolean)this.render.getValue()).booleanValue())));
/*  123 */   private final Setting<Float> accel = register(new Setting("Deceleration", Float.valueOf(0.8F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> (this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GLIDE && ((Boolean)this.render.getValue()).booleanValue())));
/*  124 */   public Setting<Boolean> colorSync = register(new Setting("CSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  125 */   public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  126 */   private final Setting<Integer> bRed = register(new Setting("BoxRed", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.box.getValue()).booleanValue())));
/*  127 */   private final Setting<Integer> bGreen = register(new Setting("BoxGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.box.getValue()).booleanValue())));
/*  128 */   private final Setting<Integer> bBlue = register(new Setting("BoxBlue", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.box.getValue()).booleanValue())));
/*  129 */   private final Setting<Integer> bAlpha = register(new Setting("BoxAlpha", Integer.valueOf(40), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.box.getValue()).booleanValue())));
/*  130 */   public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  131 */   private final Setting<Integer> oRed = register(new Setting("OutlineRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  132 */   private final Setting<Integer> oGreen = register(new Setting("OutlineGreen", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  133 */   private final Setting<Integer> oBlue = register(new Setting("OutlineBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  134 */   private final Setting<Integer> oAlpha = register(new Setting("OutlineAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  135 */   private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.5F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  136 */   public Setting<Boolean> text = register(new Setting("Text", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*      */   
/*  138 */   private final Setting<Integer> switchCooldown = register(new Setting("Cooldown", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.MISC)));
/*  139 */   public Setting<Boolean> holdFacePlace = register(new Setting("HoldFacePlace", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC)));
/*  140 */   public Setting<Boolean> holdFaceBreak = register(new Setting("HoldSlowBreak", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC && ((Boolean)this.holdFacePlace.getValue()).booleanValue())));
/*  141 */   public Setting<Boolean> slowFaceBreak = register(new Setting("SlowFaceBreak", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC)));
/*  142 */   public Setting<Boolean> actualSlowBreak = register(new Setting("ActuallySlow", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC)));
/*  143 */   public Setting<Integer> facePlaceSpeed = register(new Setting("FaceSpeed", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.MISC)));
/*  144 */   public Setting<Boolean> antiNaked = register(new Setting("AntiNaked", Boolean.TRUE, v -> (this.setting.getValue() == Settings.MISC)));
/*  145 */   public Setting<Float> range = register(new Setting("Range", Float.valueOf(12.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.MISC)));
/*  146 */   public Setting<Target> targetMode = register(new Setting("Target", Target.CLOSEST, v -> (this.setting.getValue() == Settings.MISC)));
/*  147 */   public Setting<Integer> minArmor = register(new Setting("MinArmor", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(125), v -> (this.setting.getValue() == Settings.MISC)));
/*  148 */   public Setting<AutoSwitch> autoSwitch = register(new Setting("Switch", AutoSwitch.TOGGLE, v -> (this.setting.getValue() == Settings.MISC)));
/*  149 */   public Setting<Bind> switchBind = register(new Setting("SwitchBind", new Bind(-1), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE)));
/*  150 */   public Setting<Boolean> offhandSwitch = register(new Setting("Offhand", Boolean.TRUE, v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT)));
/*  151 */   public Setting<Boolean> switchBack = register(new Setting("Switchback", Boolean.TRUE, v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && ((Boolean)this.offhandSwitch.getValue()).booleanValue() && this.autoSwitch.getValue() != AutoSwitch.SILENT)));
/*  152 */   public Setting<Boolean> lethalSwitch = register(new Setting("LethalSwitch", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT)));
/*  153 */   public Setting<Boolean> mineSwitch = register(new Setting("MineSwitch", Boolean.TRUE, v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT)));
/*  154 */   public Setting<Rotate> rotate = register(new Setting("Rotate", Rotate.OFF, v -> (this.setting.getValue() == Settings.MISC)));
/*  155 */   public Setting<Boolean> suicide = register(new Setting("Suicide", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC)));
/*  156 */   public Setting<Boolean> webAttack = register(new Setting("WebAttack", Boolean.TRUE, v -> (this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE)));
/*  157 */   public Setting<Boolean> fullCalc = register(new Setting("ExtraCalc", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC)));
/*  158 */   public Setting<Boolean> sound = register(new Setting("Sound", Boolean.TRUE, v -> (this.setting.getValue() == Settings.MISC)));
/*  159 */   public Setting<Float> soundRange = register(new Setting("SoundRange", Float.valueOf(12.0F), Float.valueOf(0.0F), Float.valueOf(12.0F), v -> (this.setting.getValue() == Settings.MISC)));
/*  160 */   public Setting<Float> soundPlayer = register(new Setting("SoundPlayer", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(12.0F), v -> (this.setting.getValue() == Settings.MISC)));
/*  161 */   public Setting<Boolean> soundConfirm = register(new Setting("SoundConfirm", Boolean.TRUE, v -> (this.setting.getValue() == Settings.MISC)));
/*  162 */   public Setting<Boolean> extraSelfCalc = register(new Setting("MinSelfDmg", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC)));
/*  163 */   public Setting<AntiFriendPop> antiFriendPop = register(new Setting("FriendPop", AntiFriendPop.NONE, v -> (this.setting.getValue() == Settings.MISC)));
/*  164 */   public Setting<Boolean> noCount = register(new Setting("AntiCount", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK))));
/*  165 */   public Setting<Boolean> calcEvenIfNoDamage = register(new Setting("BigFriendCalc", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.targetMode.getValue() != Target.DAMAGE)));
/*  166 */   public Setting<Boolean> predictFriendDmg = register(new Setting("PredictFriend", Boolean.FALSE, v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && ((Boolean)this.instant.getValue()).booleanValue())));
/*  167 */   public Setting<Raytrace> raytrace = register(new Setting("Raytrace", Raytrace.NONE, v -> (this.setting.getValue() == Settings.MISC)));
/*      */   
/*  169 */   private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> (this.setting.getValue() == Settings.DEV)));
/*  170 */   public final Setting<Boolean> attackOppositeHand = register(new Setting("OppositeHand", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV)));
/*  171 */   public final Setting<Boolean> removeAfterAttack = register(new Setting("AttackRemove", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV)));
/*  172 */   public final Setting<Boolean> antiBlock = register(new Setting("AntiFeetPlace", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV)));
/*  173 */   public Setting<Float> minMinDmg = register(new Setting("MinMinDmg", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(3.0F), v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.place.getValue()).booleanValue())));
/*  174 */   public Setting<Boolean> breakSwing = register(new Setting("BreakSwing", Boolean.TRUE, v -> (this.setting.getValue() == Settings.DEV)));
/*  175 */   public Setting<Boolean> placeSwing = register(new Setting("PlaceSwing", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV)));
/*  176 */   public Setting<Boolean> exactHand = register(new Setting("ExactHand", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.placeSwing.getValue()).booleanValue())));
/*  177 */   public Setting<Boolean> fakeSwing = register(new Setting("FakeSwing", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.justRender.getValue()).booleanValue())));
/*  178 */   public Setting<Logic> logic = register(new Setting("Logic", Logic.BREAKPLACE, v -> (this.setting.getValue() == Settings.DEV)));
/*  179 */   public Setting<DamageSync> damageSync = register(new Setting("DamageSync", DamageSync.NONE, v -> (this.setting.getValue() == Settings.DEV)));
/*  180 */   public Setting<Integer> damageSyncTime = register(new Setting("SyncDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
/*  181 */   public Setting<Float> dropOff = register(new Setting("DropOff", Float.valueOf(5.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() == DamageSync.BREAK)));
/*  182 */   public Setting<Integer> confirm = register(new Setting("Confirm", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
/*  183 */   public Setting<Boolean> syncedFeetPlace = register(new Setting("FeetSync", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
/*  184 */   public Setting<Boolean> fullSync = register(new Setting("FullSync", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  185 */   public Setting<Boolean> syncCount = register(new Setting("SyncCount", Boolean.TRUE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  186 */   public Setting<Boolean> hyperSync = register(new Setting("HyperSync", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  187 */   public Setting<Boolean> gigaSync = register(new Setting("GigaSync", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  188 */   public Setting<Boolean> syncySync = register(new Setting("SyncySync", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  189 */   public Setting<Boolean> enormousSync = register(new Setting("EnormousSync", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  190 */   public Setting<Boolean> holySync = register(new Setting("UnbelievableSync", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  191 */   public Setting<Boolean> rotateFirst = register(new Setting("FirstRotation", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV && this.rotate.getValue() != Rotate.OFF && ((Integer)this.eventMode.getValue()).intValue() == 2)));
/*  192 */   public Setting<ThreadMode> threadMode = register(new Setting("Thread", ThreadMode.NONE, v -> (this.setting.getValue() == Settings.DEV)));
/*  193 */   public Setting<Integer> threadDelay = register(new Setting("ThreadDelay", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE)));
/*  194 */   public Setting<Boolean> syncThreadBool = register(new Setting("ThreadSync", Boolean.TRUE, v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE)));
/*  195 */   public Setting<Integer> syncThreads = register(new Setting("SyncThreads", Integer.valueOf(1000), Integer.valueOf(1), Integer.valueOf(10000), v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE && ((Boolean)this.syncThreadBool.getValue()).booleanValue())));
/*  196 */   public Setting<Boolean> predictPos = register(new Setting("PredictPos", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV)));
/*  197 */   public Setting<Integer> predictTicks = register(new Setting("ExtrapolationTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20), v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.predictPos.getValue()).booleanValue())));
/*  198 */   public Setting<Integer> rotations = register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), v -> (this.setting.getValue() == Settings.DEV)));
/*  199 */   public Setting<Boolean> predictRotate = register(new Setting("PredictRotate", Boolean.FALSE, v -> (this.setting.getValue() == Settings.DEV)));
/*  200 */   public Setting<Float> predictOffset = register(new Setting("PredictOffset", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(4.0F), v -> (this.setting.getValue() == Settings.DEV)));
/*      */   public boolean rotating;
/*  202 */   private Queue<Entity> attackList = new ConcurrentLinkedQueue<>();
/*  203 */   private Map<Entity, Float> crystalMap = new HashMap<>();
/*      */   private Entity efficientTarget;
/*      */   private double currentDamage;
/*      */   private double renderDamage;
/*      */   private double lastDamage;
/*      */   private boolean didRotation;
/*      */   private boolean switching;
/*      */   private BlockPos placePos;
/*      */   private BlockPos renderPos;
/*      */   private boolean mainHand;
/*      */   private boolean offHand;
/*      */   private int crystalCount;
/*      */   private int minDmgCount;
/*  216 */   private int lastSlot = -1;
/*      */   
/*      */   private float yaw;
/*      */   private float pitch;
/*      */   private BlockPos webPos;
/*      */   private BlockPos lastPos;
/*      */   private boolean posConfirmed;
/*      */   private boolean foundDoublePop;
/*      */   private int rotationPacketsSpoofed;
/*      */   private ScheduledExecutorService executor;
/*      */   private Thread thread;
/*      */   private EntityPlayer currentSyncTarget;
/*      */   private BlockPos syncedPlayerPos;
/*      */   private BlockPos syncedCrystalPos;
/*      */   private PlaceInfo placeInfo;
/*      */   private boolean addTolowDmg;
/*      */   private boolean shouldSilent;
/*      */   private BlockPos lastRenderPos;
/*      */   private AxisAlignedBB renderBB;
/*      */   private float timePassed;
/*      */   
/*      */   public AutoCrystalRewrite() {
/*  238 */     super("AutoCrystal", "Best CA on the market", Module.Category.COMBAT, true, false, false);
/*  239 */     instance = this;
/*      */   }
/*      */ 
/*      */   
/*      */   public static AutoCrystalRewrite getInstance() {
/*  244 */     if (instance == null) {
/*  245 */       instance = new AutoCrystalRewrite();
/*      */     }
/*  247 */     return instance;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void onTick() {
/*  253 */     if (this.threadMode.getValue() == ThreadMode.NONE && ((Integer)this.eventMode.getValue()).intValue() == 3) {
/*  254 */       doAutoCrystalRewrite();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/*  261 */     if (event.getStage() == 1) {
/*  262 */       postProcessing();
/*      */     }
/*  264 */     if (event.getStage() != 0) {
/*      */       return;
/*      */     }
/*  267 */     if (((Integer)this.eventMode.getValue()).intValue() == 2) {
/*  268 */       doAutoCrystalRewrite();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void postTick() {
/*  274 */     if (this.threadMode.getValue() != ThreadMode.NONE) {
/*  275 */       processMultiThreading();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void onUpdate() {
/*  282 */     if (this.threadMode.getValue() == ThreadMode.NONE && ((Integer)this.eventMode.getValue()).intValue() == 1) {
/*  283 */       doAutoCrystalRewrite();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void onToggle() {
/*  290 */     brokenPos.clear();
/*  291 */     placedPos.clear();
/*  292 */     this.totemPops.clear();
/*  293 */     this.rotating = false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void onDisable() {
/*  299 */     this.positions.clear();
/*  300 */     this.lastRenderPos = null;
/*  301 */     if (this.thread != null) {
/*  302 */       this.shouldInterrupt.set(true);
/*      */     }
/*  304 */     if (this.executor != null) {
/*  305 */       this.executor.shutdown();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void onEnable() {
/*  312 */     if (this.threadMode.getValue() != ThreadMode.NONE) {
/*  313 */       processMultiThreading();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDisplayInfo() {
/*  320 */     if (this.switching) {
/*  321 */       return "§aSwitch";
/*      */     }
/*  323 */     if (target != null) {
/*  324 */       return target.func_70005_c_();
/*      */     }
/*  326 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onPacketSend(PacketEvent.Send event) {
/*  333 */     if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && ((Integer)this.eventMode.getValue()).intValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
/*  334 */       CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
/*  335 */       packet2.field_149476_e = this.yaw;
/*  336 */       packet2.field_149473_f = this.pitch;
/*  337 */       this.rotationPacketsSpoofed++;
/*  338 */       if (this.rotationPacketsSpoofed >= ((Integer)this.rotations.getValue()).intValue()) {
/*  339 */         this.rotating = false;
/*  340 */         this.rotationPacketsSpoofed = 0;
/*      */       } 
/*      */     } 
/*  343 */     BlockPos pos = null; CPacketUseEntity packet;
/*  344 */     if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && packet.func_149564_a((World)mc.field_71441_e) instanceof EntityEnderCrystal) {
/*  345 */       pos = ((Entity)Objects.<Entity>requireNonNull(packet.func_149564_a((World)mc.field_71441_e))).func_180425_c();
/*  346 */       if (((Boolean)this.removeAfterAttack.getValue()).booleanValue()) {
/*  347 */         ((Entity)Objects.<Entity>requireNonNull(packet.func_149564_a((World)mc.field_71441_e))).func_70106_y();
/*  348 */         mc.field_71441_e.func_73028_b(packet.field_149567_a);
/*      */       } 
/*      */     } 
/*  351 */     if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && packet.func_149564_a((World)mc.field_71441_e) instanceof EntityEnderCrystal) {
/*  352 */       EntityEnderCrystal crystal = (EntityEnderCrystal)packet.func_149564_a((World)mc.field_71441_e);
/*  353 */       if (((Boolean)this.antiBlock.getValue()).booleanValue() && EntityUtil.isCrystalAtFeet(crystal, ((Float)this.range.getValue()).floatValue()) && pos != null) {
/*  354 */         rotateToPos(pos);
/*  355 */         BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.placeSwing.getValue()).booleanValue(), ((Boolean)this.exactHand.getValue()).booleanValue(), this.shouldSilent);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
/*      */   public void onPacketReceive(PacketEvent.Receive event) {
/*  364 */     if (fullNullCheck()) {
/*      */       return;
/*      */     }
/*  367 */     if (!((Boolean)this.justRender.getValue()).booleanValue() && this.switchTimer.passedMs(((Integer)this.switchCooldown.getValue()).intValue()) && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !((Boolean)this.syncedFeetPlace.getValue()).booleanValue() || this.damageSync.getValue() == DamageSync.NONE)) {
/*      */       
/*  369 */       SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket(); BlockPos pos;
/*  370 */       if (packet2.func_148993_l() == 51 && mc.field_71439_g.func_174818_b(pos = new BlockPos(packet2.func_186880_c(), packet2.func_186882_d(), packet2.func_186881_e())) + ((Float)this.predictOffset.getValue()).floatValue() <= MathUtil.square(((Float)this.breakRange.getValue()).floatValue()) && (this.instantTimer.getValue() == PredictTimer.NONE || (this.instantTimer.getValue() == PredictTimer.BREAK && this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue())) || (this.instantTimer.getValue() == PredictTimer.PREDICT && this.predictTimer.passedMs(((Integer)this.predictDelay.getValue()).intValue())))) {
/*  371 */         if (predictSlowBreak(pos.func_177977_b())) {
/*      */           return;
/*      */         }
/*  374 */         if (((Boolean)this.predictFriendDmg.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.ALL) && isRightThread())
/*  375 */           for (EntityPlayer friend : mc.field_71441_e.field_73010_i) {
/*  376 */             if (friend == null || mc.field_71439_g.equals(friend) || friend.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue()) || !Banzem.friendManager.isFriend(friend) || DamageUtil.calculateDamage(pos, (Entity)friend) <= EntityUtil.getHealth((Entity)friend) + 0.5D) {
/*      */               continue;
/*      */             }
/*      */             return;
/*      */           }  
/*  381 */         if (placedPos.contains(pos.func_177977_b())) {
/*      */           float selfDamage;
/*  383 */           if (isRightThread() ? (((Boolean)this.superSafe.getValue()).booleanValue() ? (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue()) && ((selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.field_71439_g)) - 0.5D > EntityUtil.getHealth((Entity)mc.field_71439_g) || selfDamage > ((Float)this.maxSelfBreak.getValue()).floatValue())) : ((Boolean)this.superSafe.getValue()).booleanValue()) : ((Boolean)this.superSafe.getValue()).booleanValue()) {
/*      */             return;
/*      */           }
/*  386 */           attackCrystalPredict(packet2.func_149001_c(), pos);
/*  387 */         } else if (((Boolean)this.predictCalc.getValue()).booleanValue() && isRightThread()) {
/*  388 */           float selfDamage = -1.0F;
/*  389 */           if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) {
/*  390 */             selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.field_71439_g);
/*      */           }
/*  392 */           if (selfDamage + 0.5D < EntityUtil.getHealth((Entity)mc.field_71439_g) && selfDamage <= ((Float)this.maxSelfBreak.getValue()).floatValue()) {
/*  393 */             for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*      */               float damage;
/*  395 */               if (player.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue()) || !EntityUtil.isValid((Entity)player, (((Float)this.range.getValue()).floatValue() + ((Float)this.breakRange.getValue()).floatValue())) || (((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player)) || ((damage = DamageUtil.calculateDamage(pos, (Entity)player)) <= selfDamage && (damage <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && damage <= EntityUtil.getHealth((Entity)player)))
/*      */                 continue; 
/*  397 */               if (((Boolean)this.predictRotate.getValue()).booleanValue() && ((Integer)this.eventMode.getValue()).intValue() != 2 && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
/*  398 */                 rotateToPos(pos);
/*      */               }
/*  400 */               attackCrystalPredict(packet2.func_149001_c(), pos);
/*      */             }
/*      */           
/*      */           }
/*      */         } 
/*      */       } 
/*  406 */     } else if (!((Boolean)this.soundConfirm.getValue()).booleanValue() && event.getPacket() instanceof SPacketExplosion) {
/*  407 */       SPacketExplosion packet3 = (SPacketExplosion)event.getPacket();
/*  408 */       BlockPos pos = (new BlockPos(packet3.func_149148_f(), packet3.func_149143_g(), packet3.func_149145_h())).func_177977_b();
/*  409 */       removePos(pos);
/*  410 */     } else if (event.getPacket() instanceof SPacketDestroyEntities) {
/*  411 */       SPacketDestroyEntities packet4 = (SPacketDestroyEntities)event.getPacket();
/*  412 */       for (int id : packet4.func_149098_c()) {
/*  413 */         Entity entity = mc.field_71441_e.func_73045_a(id);
/*  414 */         if (entity instanceof EntityEnderCrystal)
/*  415 */         { brokenPos.remove((new BlockPos(entity.func_174791_d())).func_177977_b());
/*  416 */           placedPos.remove((new BlockPos(entity.func_174791_d())).func_177977_b()); } 
/*      */       } 
/*  418 */     } else if (event.getPacket() instanceof SPacketEntityStatus) {
/*  419 */       SPacketEntityStatus packet5 = (SPacketEntityStatus)event.getPacket();
/*  420 */       if (packet5.func_149160_c() == 35 && packet5.func_149161_a((World)mc.field_71441_e) instanceof EntityPlayer)
/*  421 */         this.totemPops.put((EntityPlayer)packet5.func_149161_a((World)mc.field_71441_e), (new Timer()).reset()); 
/*      */     } else {
/*  423 */       SPacketSoundEffect packet; if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB) {
/*  424 */         BlockPos pos = new BlockPos(packet.func_149207_d(), packet.func_149211_e(), packet.func_149210_f());
/*  425 */         if (((Boolean)this.sound.getValue()).booleanValue() || this.threadMode.getValue() == ThreadMode.SOUND) {
/*  426 */           if (fullNullCheck())
/*  427 */             return;  NoSoundLag.removeEntities(packet, ((Float)this.soundRange.getValue()).floatValue());
/*      */         } 
/*  429 */         if (((Boolean)this.soundConfirm.getValue()).booleanValue()) {
/*  430 */           removePos(pos);
/*      */         }
/*  432 */         if (this.threadMode.getValue() == ThreadMode.SOUND && isRightThread() && mc.field_71439_g != null && mc.field_71439_g.func_174818_b(pos) < MathUtil.square(((Float)this.soundPlayer.getValue()).floatValue())) {
/*  433 */           handlePool(true);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean predictSlowBreak(BlockPos pos) {
/*  440 */     if (((Boolean)this.antiCommit.getValue()).booleanValue() && lowDmgPos.remove(pos)) {
/*  441 */       return shouldSlowBreak(false);
/*      */     }
/*  443 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isRightThread() {
/*  448 */     return (mc.func_152345_ab() || (!Banzem.eventManager.ticksOngoing() && !this.threadOngoing.get()));
/*      */   }
/*      */ 
/*      */   
/*      */   private void attackCrystalPredict(int entityID, BlockPos pos) {
/*  453 */     if (((Boolean)this.predictRotate.getValue()).booleanValue() && (((Integer)this.eventMode.getValue()).intValue() != 2 || this.threadMode.getValue() != ThreadMode.NONE) && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
/*  454 */       rotateToPos(pos);
/*      */     }
/*  456 */     CPacketUseEntity attackPacket = new CPacketUseEntity();
/*  457 */     attackPacket.field_149567_a = entityID;
/*  458 */     attackPacket.field_149566_b = CPacketUseEntity.Action.ATTACK;
/*  459 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)attackPacket);
/*  460 */     if (((Boolean)this.breakSwing.getValue()).booleanValue()) {
/*  461 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
/*      */     }
/*  463 */     if (((Boolean)this.resetBreakTimer.getValue()).booleanValue()) {
/*  464 */       this.breakTimer.reset();
/*      */     }
/*  466 */     this.predictTimer.reset();
/*      */   }
/*      */ 
/*      */   
/*      */   private void removePos(BlockPos pos) {
/*  471 */     if (this.damageSync.getValue() == DamageSync.PLACE) {
/*  472 */       if (placedPos.remove(pos)) {
/*  473 */         this.posConfirmed = true;
/*      */       }
/*  475 */     } else if (this.damageSync.getValue() == DamageSync.BREAK && brokenPos.remove(pos)) {
/*  476 */       this.posConfirmed = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void onRender3D(Render3DEvent event) {
/*  483 */     if (!((Boolean)this.render.getValue()).booleanValue())
/*  484 */       return;  Color boxC = new Color(((Integer)this.bRed.getValue()).intValue(), ((Integer)this.bGreen.getValue()).intValue(), ((Integer)this.bBlue.getValue()).intValue(), ((Integer)this.bAlpha.getValue()).intValue());
/*  485 */     Color outlineC = new Color(((Integer)this.oRed.getValue()).intValue(), ((Integer)this.oGreen.getValue()).intValue(), ((Integer)this.oBlue.getValue()).intValue(), ((Integer)this.oAlpha.getValue()).intValue());
/*  486 */     if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && (((Boolean)this.box.getValue()).booleanValue() || ((Boolean)this.outline.getValue()).booleanValue())) {
/*  487 */       if (this.renderMode.getValue() == RenderMode.FADE) {
/*  488 */         this.positions.removeIf(pos -> pos.getPos().equals(this.renderPos));
/*  489 */         this.positions.add(new RenderPos(this.renderPos, 0.0F));
/*      */       } 
/*  491 */       if (this.renderMode.getValue() == RenderMode.STATIC) {
/*  492 */         RenderUtil.drawSexyBoxBanzemIsRetardedFuckYouESP(new AxisAlignedBB(this.renderPos), boxC, outlineC, ((Float)this.lineWidth
/*      */ 
/*      */ 
/*      */             
/*  496 */             .getValue()).floatValue(), ((Boolean)this.outline
/*  497 */             .getValue()).booleanValue(), ((Boolean)this.box
/*  498 */             .getValue()).booleanValue(), ((Boolean)this.colorSync
/*  499 */             .getValue()).booleanValue(), 1.0F, 1.0F, ((Float)this.slabHeight
/*      */ 
/*      */             
/*  502 */             .getValue()).floatValue());
/*      */       }
/*  504 */       if (this.renderMode.getValue() == RenderMode.GLIDE) {
/*  505 */         if (this.lastRenderPos == null || mc.field_71439_g.func_70011_f(this.renderBB.field_72340_a, this.renderBB.field_72338_b, this.renderBB.field_72339_c) > ((Float)this.range.getValue()).floatValue()) {
/*  506 */           this.lastRenderPos = this.renderPos;
/*  507 */           this.renderBB = new AxisAlignedBB(this.renderPos);
/*  508 */           this.timePassed = 0.0F;
/*      */         } 
/*  510 */         if (!this.lastRenderPos.equals(this.renderPos)) {
/*  511 */           this.lastRenderPos = this.renderPos;
/*  512 */           this.timePassed = 0.0F;
/*      */         } 
/*  514 */         double xDiff = this.renderPos.func_177958_n() - this.renderBB.field_72340_a;
/*  515 */         double yDiff = this.renderPos.func_177956_o() - this.renderBB.field_72338_b;
/*  516 */         double zDiff = this.renderPos.func_177952_p() - this.renderBB.field_72339_c;
/*  517 */         float multiplier = this.timePassed / ((Float)this.moveSpeed.getValue()).floatValue() * ((Float)this.accel.getValue()).floatValue();
/*  518 */         if (multiplier > 1.0F) multiplier = 1.0F; 
/*  519 */         this.renderBB = this.renderBB.func_72317_d(xDiff * multiplier, yDiff * multiplier, zDiff * multiplier);
/*  520 */         RenderUtil.drawSexyBoxBanzemIsRetardedFuckYouESP(this.renderBB, boxC, outlineC, ((Float)this.lineWidth
/*      */ 
/*      */ 
/*      */             
/*  524 */             .getValue()).floatValue(), ((Boolean)this.outline
/*  525 */             .getValue()).booleanValue(), ((Boolean)this.box
/*  526 */             .getValue()).booleanValue(), ((Boolean)this.colorSync
/*  527 */             .getValue()).booleanValue(), 1.0F, 1.0F, ((Float)this.slabHeight
/*      */ 
/*      */             
/*  530 */             .getValue()).floatValue());
/*      */         
/*  532 */         if (((Boolean)this.text.getValue()).booleanValue()) {
/*  533 */           RenderUtil.drawText(this.renderBB
/*  534 */               .func_72317_d(0.0D, (1.0F - ((Float)this.slabHeight.getValue()).floatValue() / 2.0F) - 0.4D, 0.0D), (
/*  535 */               (Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + "");
/*      */         }
/*  537 */         if (this.renderBB.equals(new AxisAlignedBB(this.renderPos)))
/*  538 */         { this.timePassed = 0.0F; }
/*  539 */         else { this.timePassed += 50.0F; }
/*      */       
/*      */       } 
/*  542 */     }  if (this.renderMode.getValue() == RenderMode.FADE) {
/*  543 */       this.positions.forEach(pos -> {
/*      */             float factor = (((Float)this.duration.getValue()).floatValue() - pos.getRenderTime()) / ((Float)this.duration.getValue()).floatValue();
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             RenderUtil.drawSexyBoxBanzemIsRetardedFuckYouESP(new AxisAlignedBB(pos.getPos()), boxC, outlineC, ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Boolean)this.colorSync.getValue()).booleanValue(), ((Boolean)this.fadeFactor.getValue()).booleanValue() ? factor : 1.0F, ((Boolean)this.scaleFactor.getValue()).booleanValue() ? factor : 1.0F, ((Boolean)this.slabFactor.getValue()).booleanValue() ? factor : 1.0F);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             pos.setRenderTime(pos.getRenderTime() + 50.0F);
/*      */           });
/*      */ 
/*      */ 
/*      */       
/*  559 */       this.positions.removeIf(pos -> 
/*  560 */           (pos.getRenderTime() >= ((Float)this.duration.getValue()).floatValue() || mc.field_71441_e.func_175623_d(pos.getPos()) || !mc.field_71441_e.func_175623_d(pos.getPos().func_177972_a(EnumFacing.UP))));
/*      */ 
/*      */ 
/*      */       
/*  564 */       if (this.positions.size() > ((Integer)this.max.getValue()).intValue())
/*  565 */         this.positions.remove(0); 
/*      */     } 
/*  567 */     if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && ((Boolean)this.text.getValue()).booleanValue() && this.renderMode.getValue() != RenderMode.GLIDE) {
/*  568 */       RenderUtil.drawText((new AxisAlignedBB(this.renderPos))
/*  569 */           .func_72317_d(0.0D, (this.renderMode.getValue() != RenderMode.FADE) ? ((1.0F - ((Float)this.slabHeight.getValue()).floatValue() / 2.0F) - 0.4D) : 0.1D, 0.0D), (
/*  570 */           (Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + "");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onKeyInput(InputEvent.KeyInputEvent event) {
/*  577 */     if (Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof me.mohalk.banzem.features.gui.PhobosGui) && ((Bind)this.switchBind.getValue()).getKey() == Keyboard.getEventKey()) {
/*  578 */       if (((Boolean)this.switchBack.getValue()).booleanValue() && ((Boolean)this.offhandSwitch.getValue()).booleanValue() && this.offHand) {
/*  579 */         Offhand module = (Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class);
/*  580 */         if (module.isOff()) {
/*  581 */           Command.sendMessage("<" + getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
/*  582 */         } else if (module.type.getValue() == Offhand.Type.NEW) {
/*  583 */           module.setSwapToTotem(true);
/*  584 */           module.doOffhand();
/*      */         } else {
/*  586 */           module.setMode(Offhand.Mode2.TOTEMS);
/*  587 */           module.doSwitch();
/*      */         } 
/*      */         return;
/*      */       } 
/*  591 */       this.switching = !this.switching;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onSettingChange(ClientEvent event) {
/*  598 */     if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
/*  599 */       if (this.executor != null) {
/*  600 */         this.executor.shutdown();
/*      */       }
/*  602 */       if (this.thread != null) {
/*  603 */         this.shouldInterrupt.set(true);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void postProcessing() {
/*  610 */     if (this.threadMode.getValue() != ThreadMode.NONE || ((Integer)this.eventMode.getValue()).intValue() != 2 || this.rotate.getValue() == Rotate.OFF || !((Boolean)this.rotateFirst.getValue()).booleanValue()) {
/*      */       return;
/*      */     }
/*  613 */     switch ((Logic)this.logic.getValue()) {
/*      */       case OFF:
/*  615 */         postProcessBreak();
/*  616 */         postProcessPlace();
/*      */         break;
/*      */       
/*      */       case PLACE:
/*  620 */         postProcessPlace();
/*  621 */         postProcessBreak();
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void postProcessBreak() {
/*  628 */     while (!this.packetUseEntities.isEmpty()) {
/*  629 */       CPacketUseEntity packet = this.packetUseEntities.poll();
/*  630 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)packet);
/*  631 */       if (((Boolean)this.breakSwing.getValue()).booleanValue()) {
/*  632 */         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/*      */       }
/*  634 */       this.breakTimer.reset();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void postProcessPlace() {
/*  640 */     if (this.placeInfo != null) {
/*  641 */       this.placeInfo.runPlace();
/*  642 */       this.placeTimer.reset();
/*  643 */       this.placeInfo = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void processMultiThreading() {
/*  649 */     if (isOff()) {
/*      */       return;
/*      */     }
/*  652 */     if (this.threadMode.getValue() == ThreadMode.WHILE) {
/*  653 */       handleWhile();
/*  654 */     } else if (this.threadMode.getValue() != ThreadMode.NONE) {
/*  655 */       handlePool(false);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void handlePool(boolean justDoIt) {
/*  661 */     if (justDoIt || this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || (this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue()) && ((Boolean)this.syncThreadBool.getValue()).booleanValue())) {
/*  662 */       if (this.executor != null) {
/*  663 */         this.executor.shutdown();
/*      */       }
/*  665 */       this.executor = getExecutor();
/*  666 */       this.syncroTimer.reset();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void handleWhile() {
/*  672 */     if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || (this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue()) && ((Boolean)this.syncThreadBool.getValue()).booleanValue())) {
/*  673 */       if (this.thread == null) {
/*  674 */         this.thread = new Thread(RAutoCrystalRewrite.getInstance(this));
/*  675 */       } else if (this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue()) && !this.shouldInterrupt.get() && ((Boolean)this.syncThreadBool.getValue()).booleanValue()) {
/*  676 */         this.shouldInterrupt.set(true);
/*  677 */         this.syncroTimer.reset();
/*      */         return;
/*      */       } 
/*  680 */       if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
/*  681 */         this.thread = new Thread(RAutoCrystalRewrite.getInstance(this));
/*      */       }
/*  683 */       if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
/*      */         try {
/*  685 */           this.thread.start();
/*  686 */         } catch (Exception e) {
/*  687 */           e.printStackTrace();
/*      */         } 
/*  689 */         this.syncroTimer.reset();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private ScheduledExecutorService getExecutor() {
/*  696 */     ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
/*  697 */     service.scheduleAtFixedRate(RAutoCrystalRewrite.getInstance(this), 0L, ((Integer)this.threadDelay.getValue()).intValue(), TimeUnit.MILLISECONDS);
/*  698 */     return service;
/*      */   }
/*      */ 
/*      */   
/*      */   public void doAutoCrystalRewrite() {
/*  703 */     if (check()) {
/*  704 */       switch ((Logic)this.logic.getValue()) {
/*      */         case PLACE:
/*  706 */           placeCrystal();
/*  707 */           breakCrystal();
/*      */           break;
/*      */         
/*      */         case OFF:
/*  711 */           breakCrystal();
/*  712 */           placeCrystal();
/*      */           break;
/*      */       } 
/*      */       
/*  716 */       manualBreaker();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean check() {
/*  722 */     if (fullNullCheck()) {
/*  723 */       return false;
/*      */     }
/*  725 */     if (this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue())) {
/*  726 */       this.currentSyncTarget = null;
/*  727 */       this.syncedCrystalPos = null;
/*  728 */       this.syncedPlayerPos = null;
/*  729 */     } else if (((Boolean)this.syncySync.getValue()).booleanValue() && this.syncedCrystalPos != null) {
/*  730 */       this.posConfirmed = true;
/*      */     } 
/*  732 */     this.foundDoublePop = false;
/*  733 */     if (this.renderTimer.passedMs(500L)) {
/*  734 */       this.renderPos = null;
/*  735 */       this.renderTimer.reset();
/*      */     } 
/*  737 */     this.mainHand = (mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP);
/*  738 */     if (this.autoSwitch.getValue() == AutoSwitch.SILENT && InventoryUtil.getItemHotbar(Items.field_185158_cP) != -1) {
/*  739 */       this.mainHand = true;
/*  740 */       this.shouldSilent = true;
/*      */     } else {
/*  742 */       this.shouldSilent = false;
/*  743 */     }  this.offHand = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP);
/*  744 */     this.currentDamage = 0.0D;
/*  745 */     this.placePos = null;
/*  746 */     if (this.lastSlot != mc.field_71439_g.field_71071_by.field_70461_c || AutoTrap.isPlacing || Surround.isPlacing) {
/*  747 */       this.lastSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*  748 */       this.switchTimer.reset();
/*      */     } 
/*  750 */     if (!this.offHand && !this.mainHand) {
/*  751 */       this.placeInfo = null;
/*  752 */       this.packetUseEntities.clear();
/*      */     } 
/*  754 */     if (this.offHand || this.mainHand) {
/*  755 */       this.switching = false;
/*      */     }
/*  757 */     if ((!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching) || !DamageUtil.canBreakWeakness((EntityPlayer)mc.field_71439_g) || !this.switchTimer.passedMs(((Integer)this.switchCooldown.getValue()).intValue())) {
/*  758 */       this.renderPos = null;
/*  759 */       target = null;
/*  760 */       this.rotating = false;
/*  761 */       return false;
/*      */     } 
/*  763 */     if (((Boolean)this.mineSwitch.getValue()).booleanValue() && Mouse.isButtonDown(0) && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && Mouse.isButtonDown(1) && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemPickaxe) {
/*  764 */       switchItem();
/*      */     }
/*  766 */     mapCrystals();
/*  767 */     if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(((Integer)this.confirm.getValue()).intValue())) {
/*  768 */       this.syncTimer.setMs((((Integer)this.damageSyncTime.getValue()).intValue() + 1));
/*      */     }
/*  770 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   private void mapCrystals() {
/*  775 */     this.efficientTarget = null;
/*  776 */     if (((Integer)this.packets.getValue()).intValue() != 1) {
/*  777 */       this.attackList = new ConcurrentLinkedQueue<>();
/*  778 */       this.crystalMap = new HashMap<>();
/*      */     } 
/*  780 */     this.crystalCount = 0;
/*  781 */     this.minDmgCount = 0;
/*  782 */     Entity maxCrystal = null;
/*  783 */     float maxDamage = 0.5F;
/*  784 */     for (Entity entity : mc.field_71441_e.field_72996_f) {
/*  785 */       if (entity.field_70128_L || !(entity instanceof EntityEnderCrystal) || !isValid(entity))
/*  786 */         continue;  if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && entity.func_180425_c().func_177977_b().equals(this.syncedCrystalPos) && this.damageSync.getValue() != DamageSync.NONE) {
/*  787 */         this.minDmgCount++;
/*  788 */         this.crystalCount++;
/*  789 */         if (((Boolean)this.syncCount.getValue()).booleanValue()) {
/*  790 */           this.minDmgCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*  791 */           this.crystalCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*      */         } 
/*  793 */         if (!((Boolean)this.hyperSync.getValue()).booleanValue())
/*  794 */           continue;  maxCrystal = null;
/*      */         break;
/*      */       } 
/*  797 */       boolean count = false;
/*  798 */       boolean countMin = false;
/*  799 */       float selfDamage = -1.0F;
/*  800 */       if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) {
/*  801 */         selfDamage = DamageUtil.calculateDamage(entity, (Entity)mc.field_71439_g);
/*      */       }
/*  803 */       if (selfDamage + 0.5D < EntityUtil.getHealth((Entity)mc.field_71439_g) && selfDamage <= ((Float)this.maxSelfBreak.getValue()).floatValue()) {
/*  804 */         Entity beforeCrystal = maxCrystal;
/*  805 */         float beforeDamage = maxDamage;
/*  806 */         for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*      */           
/*  808 */           if (player.func_70068_e(entity) > MathUtil.square(((Float)this.range.getValue()).floatValue()))
/*      */             continue; 
/*  810 */           if (EntityUtil.isValid((Entity)player, (((Float)this.range.getValue()).floatValue() + ((Float)this.breakRange.getValue()).floatValue()))) {
/*  811 */             float damage; if ((((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player)) || ((damage = DamageUtil.calculateDamage(entity, (Entity)player)) <= selfDamage && (damage <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && damage <= EntityUtil.getHealth((Entity)player)))
/*      */               continue; 
/*  813 */             if (damage > maxDamage) {
/*  814 */               maxDamage = damage;
/*  815 */               maxCrystal = entity;
/*      */             } 
/*  817 */             if (((Integer)this.packets.getValue()).intValue() == 1) {
/*  818 */               if (damage >= ((Float)this.minDamage.getValue()).floatValue() || !((Boolean)this.wasteMinDmgCount.getValue()).booleanValue()) {
/*  819 */                 count = true;
/*      */               }
/*  821 */               countMin = true;
/*      */               continue;
/*      */             } 
/*  824 */             if (this.crystalMap.get(entity) != null && ((Float)this.crystalMap.get(entity)).floatValue() >= damage)
/*      */               continue; 
/*  826 */             this.crystalMap.put(entity, Float.valueOf(damage));
/*      */             continue;
/*      */           } 
/*  829 */           if ((this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.ALL) || !Banzem.friendManager.isFriend(player.func_70005_c_()) || DamageUtil.calculateDamage(entity, (Entity)player) <= EntityUtil.getHealth((Entity)player) + 0.5D)
/*      */             continue; 
/*  831 */           maxCrystal = beforeCrystal;
/*  832 */           maxDamage = beforeDamage;
/*  833 */           this.crystalMap.remove(entity);
/*  834 */           if (!((Boolean)this.noCount.getValue()).booleanValue())
/*  835 */             break;  count = false;
/*  836 */           countMin = false;
/*      */         } 
/*      */       } 
/*      */       
/*  840 */       if (!countMin)
/*  841 */         continue;  this.minDmgCount++;
/*  842 */       if (!count)
/*  843 */         continue;  this.crystalCount++;
/*      */     } 
/*  845 */     if (this.damageSync.getValue() == DamageSync.BREAK && (maxDamage > this.lastDamage || this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
/*  846 */       this.lastDamage = maxDamage;
/*      */     }
/*  848 */     if (((Boolean)this.enormousSync.getValue()).booleanValue() && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && this.damageSync.getValue() != DamageSync.NONE && this.syncedCrystalPos != null) {
/*  849 */       if (((Boolean)this.syncCount.getValue()).booleanValue()) {
/*  850 */         this.minDmgCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*  851 */         this.crystalCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*      */       } 
/*      */       return;
/*      */     } 
/*  855 */     if (((Boolean)this.webAttack.getValue()).booleanValue() && this.webPos != null) {
/*  856 */       if (mc.field_71439_g.func_174818_b(this.webPos.func_177984_a()) > MathUtil.square(((Float)this.breakRange.getValue()).floatValue())) {
/*  857 */         this.webPos = null;
/*      */       } else {
/*  859 */         for (Entity entity : mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(this.webPos.func_177984_a()))) {
/*  860 */           if (!(entity instanceof EntityEnderCrystal))
/*  861 */             continue;  this.attackList.add(entity);
/*  862 */           this.efficientTarget = entity;
/*  863 */           this.webPos = null;
/*  864 */           this.lastDamage = 0.5D;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     }
/*  869 */     if (shouldSlowBreak(true) && maxDamage < ((Float)this.minDamage.getValue()).floatValue() && (target == null || EntityUtil.getHealth((Entity)target) > ((Float)this.facePlace.getValue()).floatValue() || (!this.breakTimer.passedMs(((Integer)this.facePlaceSpeed.getValue()).intValue()) && ((Boolean)this.slowFaceBreak.getValue()).booleanValue() && Mouse.isButtonDown(0) && ((Boolean)this.holdFacePlace.getValue()).booleanValue() && ((Boolean)this.holdFaceBreak.getValue()).booleanValue()))) {
/*  870 */       this.efficientTarget = null;
/*      */       return;
/*      */     } 
/*  873 */     if (((Integer)this.packets.getValue()).intValue() == 1) {
/*  874 */       this.efficientTarget = maxCrystal;
/*      */     } else {
/*  876 */       this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
/*  877 */       for (Map.Entry<Entity, Float> entry : this.crystalMap.entrySet()) {
/*  878 */         Entity crystal = entry.getKey();
/*  879 */         float damage = ((Float)entry.getValue()).floatValue();
/*  880 */         if (damage >= ((Float)this.minDamage.getValue()).floatValue() || !((Boolean)this.wasteMinDmgCount.getValue()).booleanValue()) {
/*  881 */           this.crystalCount++;
/*      */         }
/*  883 */         this.attackList.add(crystal);
/*  884 */         this.minDmgCount++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean shouldSlowBreak(boolean withManual) {
/*  891 */     return ((withManual && ((Boolean)this.manual.getValue()).booleanValue() && ((Boolean)this.manualMinDmg.getValue()).booleanValue() && Mouse.isButtonDown(1) && (!Mouse.isButtonDown(0) || !((Boolean)this.holdFacePlace.getValue()).booleanValue())) || (((Boolean)this.holdFacePlace.getValue()).booleanValue() && ((Boolean)this.holdFaceBreak.getValue()).booleanValue() && Mouse.isButtonDown(0) && !this.breakTimer.passedMs(((Integer)this.facePlaceSpeed.getValue()).intValue())) || (((Boolean)this.slowFaceBreak.getValue()).booleanValue() && !this.breakTimer.passedMs(((Integer)this.facePlaceSpeed.getValue()).intValue())));
/*      */   }
/*      */ 
/*      */   
/*      */   private void placeCrystal() {
/*  896 */     int crystalLimit = ((Integer)this.wasteAmount.getValue()).intValue();
/*  897 */     if (this.placeTimer.passedMs(((Integer)this.placeDelay.getValue()).intValue()) && ((Boolean)this.place.getValue()).booleanValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || (this.switchMode.getValue() == Switch.BREAKSLOT && this.switching))) {
/*  898 */       if ((this.offHand || this.mainHand || (this.switchMode.getValue() != Switch.ALWAYS && !this.switching)) && this.crystalCount >= crystalLimit && (!((Boolean)this.antiSurround.getValue()).booleanValue() || this.lastPos == null || !this.lastPos.equals(this.placePos))) {
/*      */         return;
/*      */       }
/*  901 */       calculateDamage(getTarget((this.targetMode.getValue() == Target.UNSAFE)));
/*  902 */       if (target != null && this.placePos != null) {
/*  903 */         if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || (((Boolean)this.lethalSwitch.getValue()).booleanValue() && EntityUtil.getHealth((Entity)target) <= ((Float)this.facePlace.getValue()).floatValue())) && !switchItem()) {
/*      */           return;
/*      */         }
/*  906 */         if (this.currentDamage < ((Float)this.minDamage.getValue()).floatValue() && ((Boolean)this.limitFacePlace.getValue()).booleanValue()) {
/*  907 */           crystalLimit = 1;
/*      */         }
/*  909 */         if (this.currentDamage >= ((Float)this.minMinDmg.getValue()).floatValue() && (this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (((Boolean)this.antiSurround.getValue()).booleanValue() && this.lastPos != null && this.lastPos.equals(this.placePos))) && (this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0D && (DamageUtil.isArmorLow(target, ((Integer)this.minArmor.getValue()).intValue()) || EntityUtil.getHealth((Entity)target) <= ((Float)this.facePlace.getValue()).floatValue() || this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || shouldHoldFacePlace())) {
/*  910 */           float damageOffset = (this.damageSync.getValue() == DamageSync.BREAK) ? (((Float)this.dropOff.getValue()).floatValue() - 5.0F) : 0.0F;
/*  911 */           boolean syncflag = false;
/*  912 */           if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && this.placePos.equals(this.lastPos) && isEligableForFeetSync(target, this.placePos) && !this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) && target.equals(this.currentSyncTarget) && target.func_180425_c().equals(this.syncedPlayerPos) && this.damageSync.getValue() != DamageSync.NONE) {
/*  913 */             this.syncedCrystalPos = this.placePos;
/*  914 */             this.lastDamage = this.currentDamage;
/*  915 */             if (((Boolean)this.fullSync.getValue()).booleanValue()) {
/*  916 */               this.lastDamage = 100.0D;
/*      */             }
/*  918 */             syncflag = true;
/*      */           } 
/*  920 */           if (syncflag || this.currentDamage - damageOffset > this.lastDamage || this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == DamageSync.NONE) {
/*  921 */             if (!syncflag && this.damageSync.getValue() != DamageSync.BREAK) {
/*  922 */               this.lastDamage = this.currentDamage;
/*      */             }
/*  924 */             if (!((Boolean)this.onlyplaced.getValue()).booleanValue())
/*  925 */               this.renderPos = this.placePos; 
/*  926 */             this.renderDamage = this.currentDamage;
/*  927 */             if (switchItem()) {
/*  928 */               this.currentSyncTarget = target;
/*  929 */               this.syncedPlayerPos = target.func_180425_c();
/*  930 */               if (this.foundDoublePop) {
/*  931 */                 this.totemPops.put(target, (new Timer()).reset());
/*      */               }
/*  933 */               rotateToPos(this.placePos);
/*  934 */               if (this.addTolowDmg || (((Boolean)this.actualSlowBreak.getValue()).booleanValue() && this.currentDamage < ((Float)this.minDamage.getValue()).floatValue())) {
/*  935 */                 lowDmgPos.add(this.placePos);
/*      */               }
/*  937 */               placedPos.add(this.placePos);
/*  938 */               if (!((Boolean)this.justRender.getValue()).booleanValue()) {
/*  939 */                 if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && ((Boolean)this.rotateFirst.getValue()).booleanValue() && this.rotate.getValue() != Rotate.OFF) {
/*  940 */                   this.placeInfo = new PlaceInfo(this.placePos, this.offHand, ((Boolean)this.placeSwing.getValue()).booleanValue(), ((Boolean)this.exactHand.getValue()).booleanValue(), this.shouldSilent);
/*      */                 } else {
/*  942 */                   BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.placeSwing.getValue()).booleanValue(), ((Boolean)this.exactHand.getValue()).booleanValue(), this.shouldSilent);
/*      */                 } 
/*      */               }
/*  945 */               this.lastPos = this.placePos;
/*  946 */               this.placeTimer.reset();
/*  947 */               this.posConfirmed = false;
/*  948 */               if (this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue())) {
/*  949 */                 this.syncedCrystalPos = null;
/*  950 */                 this.syncTimer.reset();
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } else {
/*  956 */         this.renderPos = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean shouldHoldFacePlace() {
/*  963 */     this.addTolowDmg = false;
/*  964 */     if (((Boolean)this.holdFacePlace.getValue()).booleanValue() && Mouse.isButtonDown(0)) {
/*  965 */       this.addTolowDmg = true;
/*  966 */       return true;
/*      */     } 
/*  968 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean switchItem() {
/*  973 */     if (this.offHand || this.mainHand) {
/*  974 */       return true;
/*      */     }
/*  976 */     switch ((AutoSwitch)this.autoSwitch.getValue()) {
/*      */       case OFF:
/*  978 */         return false;
/*      */       
/*      */       case PLACE:
/*  981 */         if (!this.switching) {
/*  982 */           return false;
/*      */         }
/*      */       
/*      */       case BREAK:
/*  986 */         if (!doSwitch())
/*  987 */           break;  return true;
/*      */     } 
/*      */     
/*  990 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean doSwitch() {
/*  995 */     if (((Boolean)this.offhandSwitch.getValue()).booleanValue()) {
/*  996 */       Offhand module = (Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class);
/*  997 */       if (module.isOff()) {
/*  998 */         Command.sendMessage("<" + getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
/*  999 */         this.switching = false;
/* 1000 */         return false;
/*      */       } 
/* 1002 */       if (module.type.getValue() == Offhand.Type.NEW) {
/* 1003 */         module.setSwapToTotem(false);
/* 1004 */         module.setMode(Offhand.Mode.CRYSTALS);
/* 1005 */         module.doOffhand();
/*      */       } else {
/* 1007 */         module.setMode(Offhand.Mode2.CRYSTALS);
/* 1008 */         module.doSwitch();
/*      */       } 
/* 1010 */       this.switching = false;
/* 1011 */       return true;
/*      */     } 
/* 1013 */     if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
/* 1014 */       this.mainHand = false;
/*      */     } else {
/* 1016 */       InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
/* 1017 */       this.mainHand = true;
/*      */     } 
/* 1019 */     this.switching = false;
/* 1020 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void calculateDamage(EntityPlayer targettedPlayer) {
/* 1026 */     if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !((Boolean)this.fullCalc.getValue()).booleanValue()) {
/*      */       return;
/*      */     }
/* 1029 */     float maxDamage = 0.5F;
/* 1030 */     EntityPlayer currentTarget = null;
/* 1031 */     BlockPos currentPos = null;
/* 1032 */     float maxSelfDamage = 0.0F;
/* 1033 */     this.foundDoublePop = false;
/* 1034 */     BlockPos setToAir = null;
/* 1035 */     IBlockState state = null; BlockPos playerPos;
/* 1036 */     if (((Boolean)this.webAttack.getValue()).booleanValue() && targettedPlayer != null && mc.field_71441_e.func_180495_p(playerPos = new BlockPos(targettedPlayer.func_174791_d())).func_177230_c() == Blocks.field_150321_G) {
/* 1037 */       setToAir = playerPos;
/* 1038 */       state = mc.field_71441_e.func_180495_p(playerPos);
/* 1039 */       mc.field_71441_e.func_175698_g(playerPos);
/*      */     } 
/*      */     
/* 1042 */     for (BlockPos pos : BlockUtil.possiblePlacePositions(((Float)this.placeRange.getValue()).floatValue(), ((Boolean)this.antiSurround.getValue()).booleanValue(), ((Boolean)this.oneDot15.getValue()).booleanValue())) {
/* 1043 */       if (!BlockUtil.rayTracePlaceCheck(pos, ((this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && mc.field_71439_g.func_174818_b(pos) > MathUtil.square(((Float)this.placetrace.getValue()).floatValue())), 1.0F))
/*      */         continue; 
/* 1045 */       float selfDamage = -1.0F;
/* 1046 */       if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) {
/* 1047 */         selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.field_71439_g);
/*      */       }
/* 1049 */       if (selfDamage + 0.5D >= EntityUtil.getHealth((Entity)mc.field_71439_g) || selfDamage > ((Float)this.maxSelfPlace.getValue()).floatValue())
/*      */         continue; 
/* 1051 */       if (targettedPlayer != null) {
/* 1052 */         float playerDamage = DamageUtil.calculateDamage(pos, (Entity)targettedPlayer);
/* 1053 */         if (((Boolean)this.calcEvenIfNoDamage.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.PLACE)) {
/* 1054 */           boolean friendPop = false;
/* 1055 */           for (EntityPlayer friend : mc.field_71441_e.field_73010_i) {
/* 1056 */             if (friend == null || mc.field_71439_g.equals(friend) || friend.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue()) || !Banzem.friendManager.isFriend(friend) || DamageUtil.calculateDamage(pos, (Entity)friend) <= EntityUtil.getHealth((Entity)friend) + 0.5D)
/*      */               continue; 
/* 1058 */             friendPop = true;
/*      */           } 
/*      */           
/* 1061 */           if (friendPop)
/*      */             continue; 
/* 1063 */         }  if (isDoublePoppable(targettedPlayer, playerDamage) && (currentPos == null || targettedPlayer.func_174818_b(pos) < targettedPlayer.func_174818_b(currentPos))) {
/* 1064 */           currentTarget = targettedPlayer;
/* 1065 */           maxDamage = playerDamage;
/* 1066 */           currentPos = pos;
/* 1067 */           this.foundDoublePop = true;
/*      */           continue;
/*      */         } 
/* 1070 */         if (this.foundDoublePop || (playerDamage <= maxDamage && (!((Boolean)this.extraSelfCalc.getValue()).booleanValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage <= selfDamage && (playerDamage <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && playerDamage <= EntityUtil.getHealth((Entity)targettedPlayer)))
/*      */           continue; 
/* 1072 */         maxDamage = playerDamage;
/* 1073 */         currentTarget = targettedPlayer;
/* 1074 */         currentPos = pos;
/* 1075 */         maxSelfDamage = selfDamage;
/*      */         continue;
/*      */       } 
/* 1078 */       float maxDamageBefore = maxDamage;
/* 1079 */       EntityPlayer currentTargetBefore = currentTarget;
/* 1080 */       BlockPos currentPosBefore = currentPos;
/* 1081 */       float maxSelfDamageBefore = maxSelfDamage;
/* 1082 */       for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 1083 */         if (EntityUtil.isValid((Entity)player, (((Float)this.placeRange.getValue()).floatValue() + ((Float)this.range.getValue()).floatValue()))) {
/* 1084 */           if (((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player))
/* 1085 */             continue;  float playerDamage = DamageUtil.calculateDamage(pos, (Entity)player);
/* 1086 */           if (((Boolean)this.doublePopOnDamage.getValue()).booleanValue() && isDoublePoppable(player, playerDamage) && (currentPos == null || player.func_174818_b(pos) < player.func_174818_b(currentPos))) {
/* 1087 */             currentTarget = player;
/* 1088 */             maxDamage = playerDamage;
/* 1089 */             currentPos = pos;
/* 1090 */             maxSelfDamage = selfDamage;
/* 1091 */             this.foundDoublePop = true;
/* 1092 */             if (this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.PLACE)
/*      */               continue; 
/*      */             break;
/*      */           } 
/* 1096 */           if (this.foundDoublePop || (playerDamage <= maxDamage && (!((Boolean)this.extraSelfCalc.getValue()).booleanValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage <= selfDamage && (playerDamage <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && playerDamage <= EntityUtil.getHealth((Entity)player)))
/*      */             continue; 
/* 1098 */           maxDamage = playerDamage;
/* 1099 */           currentTarget = player;
/* 1100 */           currentPos = pos;
/* 1101 */           maxSelfDamage = selfDamage;
/*      */           continue;
/*      */         } 
/* 1104 */         if ((this.antiFriendPop.getValue() != AntiFriendPop.ALL && this.antiFriendPop.getValue() != AntiFriendPop.PLACE) || player == null || player.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue()) || !Banzem.friendManager.isFriend(player) || DamageUtil.calculateDamage(pos, (Entity)player) <= EntityUtil.getHealth((Entity)player) + 0.5D)
/*      */           continue; 
/* 1106 */         maxDamage = maxDamageBefore;
/* 1107 */         currentTarget = currentTargetBefore;
/* 1108 */         currentPos = currentPosBefore;
/* 1109 */         maxSelfDamage = maxSelfDamageBefore;
/*      */       } 
/*      */     } 
/*      */     
/* 1113 */     if (setToAir != null) {
/* 1114 */       mc.field_71441_e.func_175656_a(setToAir, state);
/* 1115 */       this.webPos = currentPos;
/*      */     } 
/* 1117 */     target = currentTarget;
/* 1118 */     this.currentDamage = maxDamage;
/* 1119 */     this.placePos = currentPos;
/*      */   }
/*      */   
/*      */   private EntityPlayer getTarget(boolean unsafe) {
/*      */     EntityOtherPlayerMP entityOtherPlayerMP;
/* 1124 */     if (this.targetMode.getValue() == Target.DAMAGE) {
/* 1125 */       return null;
/*      */     }
/* 1127 */     EntityPlayer currentTarget = null;
/* 1128 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 1129 */       if (EntityUtil.isntValid((Entity)player, (((Float)this.placeRange.getValue()).floatValue() + ((Float)this.range.getValue()).floatValue())) || (((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player)) || (unsafe && EntityUtil.isSafe((Entity)player)))
/*      */         continue; 
/* 1131 */       if (((Integer)this.minArmor.getValue()).intValue() > 0 && DamageUtil.isArmorLow(player, ((Integer)this.minArmor.getValue()).intValue())) {
/* 1132 */         currentTarget = player;
/*      */         break;
/*      */       } 
/* 1135 */       if (currentTarget == null) {
/* 1136 */         currentTarget = player;
/*      */         continue;
/*      */       } 
/* 1139 */       if (mc.field_71439_g.func_70068_e((Entity)player) >= mc.field_71439_g.func_70068_e((Entity)currentTarget))
/*      */         continue; 
/* 1141 */       currentTarget = player;
/*      */     } 
/* 1143 */     if (unsafe && currentTarget == null) {
/* 1144 */       return getTarget(false);
/*      */     }
/* 1146 */     if (((Boolean)this.predictPos.getValue()).booleanValue() && currentTarget != null) {
/* 1147 */       currentTarget.func_110124_au();
/* 1148 */       GameProfile profile = new GameProfile(currentTarget.func_110124_au(), currentTarget.func_70005_c_());
/* 1149 */       EntityOtherPlayerMP newTarget = new EntityOtherPlayerMP((World)mc.field_71441_e, profile);
/* 1150 */       Vec3d extrapolatePosition = MathUtil.extrapolatePlayerPosition(currentTarget, ((Integer)this.predictTicks.getValue()).intValue());
/* 1151 */       newTarget.func_82149_j((Entity)currentTarget);
/* 1152 */       newTarget.field_70165_t = extrapolatePosition.field_72450_a;
/* 1153 */       newTarget.field_70163_u = extrapolatePosition.field_72448_b;
/* 1154 */       newTarget.field_70161_v = extrapolatePosition.field_72449_c;
/* 1155 */       newTarget.func_70606_j(EntityUtil.getHealth((Entity)currentTarget));
/* 1156 */       newTarget.field_71071_by.func_70455_b(currentTarget.field_71071_by);
/* 1157 */       entityOtherPlayerMP = newTarget;
/*      */     } 
/* 1159 */     return (EntityPlayer)entityOtherPlayerMP;
/*      */   }
/*      */ 
/*      */   
/*      */   private void breakCrystal() {
/* 1164 */     if (((Boolean)this.explode.getValue()).booleanValue() && this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
/* 1165 */       if (((Integer)this.packets.getValue()).intValue() == 1 && this.efficientTarget != null) {
/* 1166 */         if (((Boolean)this.justRender.getValue()).booleanValue()) {
/* 1167 */           doFakeSwing();
/*      */           return;
/*      */         } 
/* 1170 */         if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean)this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
/*      */           return;
/*      */         }
/* 1173 */         rotateTo(this.efficientTarget);
/* 1174 */         attackEntity(this.efficientTarget);
/* 1175 */         this.breakTimer.reset();
/* 1176 */       } else if (!this.attackList.isEmpty()) {
/* 1177 */         if (((Boolean)this.justRender.getValue()).booleanValue()) {
/* 1178 */           doFakeSwing();
/*      */           return;
/*      */         } 
/* 1181 */         if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean)this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
/*      */           return;
/*      */         }
/* 1184 */         for (int i = 0; i < ((Integer)this.packets.getValue()).intValue(); i++) {
/* 1185 */           Entity entity = this.attackList.poll();
/* 1186 */           if (entity != null) {
/* 1187 */             rotateTo(entity);
/* 1188 */             attackEntity(entity);
/*      */           } 
/* 1190 */         }  this.breakTimer.reset();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void attackEntity(Entity entity) {
/* 1197 */     if (entity != null) {
/* 1198 */       if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && ((Boolean)this.rotateFirst.getValue()).booleanValue() && this.rotate.getValue() != Rotate.OFF) {
/* 1199 */         this.packetUseEntities.add(new CPacketUseEntity(entity));
/*      */       } else {
/* 1201 */         EntityUtil.attackEntity(entity, ((Boolean)this.sync.getValue()).booleanValue(), ((Boolean)this.breakSwing.getValue()).booleanValue());
/* 1202 */         EntityUtil.OffhandAttack(entity, ((Boolean)this.attackOppositeHand.getValue()).booleanValue(), ((Boolean)this.attackOppositeHand.getValue()).booleanValue());
/* 1203 */         brokenPos.add((new BlockPos(entity.func_174791_d())).func_177977_b());
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void doFakeSwing() {
/* 1210 */     if (((Boolean)this.fakeSwing.getValue()).booleanValue()) {
/* 1211 */       EntityUtil.swingArmNoPacket(EnumHand.MAIN_HAND, (EntityLivingBase)mc.field_71439_g);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void manualBreaker() {
/* 1218 */     if (this.rotate.getValue() != Rotate.OFF && ((Integer)this.eventMode.getValue()).intValue() != 2 && this.rotating)
/* 1219 */       if (this.didRotation) {
/* 1220 */         mc.field_71439_g.field_70125_A = (float)(mc.field_71439_g.field_70125_A + 4.0E-4D);
/* 1221 */         this.didRotation = false;
/*      */       } else {
/* 1223 */         mc.field_71439_g.field_70125_A = (float)(mc.field_71439_g.field_70125_A - 4.0E-4D);
/* 1224 */         this.didRotation = true;
/*      */       }  
/*      */     RayTraceResult result;
/* 1227 */     if ((this.offHand || this.mainHand) && ((Boolean)this.manual.getValue()).booleanValue() && this.manualTimer.passedMs(((Integer)this.manualBreak.getValue()).intValue()) && Mouse.isButtonDown(1) && mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151031_f && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151062_by && (result = mc.field_71476_x) != null) {
/* 1228 */       Entity entity; BlockPos mousePos; switch (result.field_72313_a) {
/*      */         case OFF:
/* 1230 */           entity = result.field_72308_g;
/* 1231 */           if (!(entity instanceof EntityEnderCrystal))
/* 1232 */             break;  EntityUtil.attackEntity(entity, ((Boolean)this.sync.getValue()).booleanValue(), ((Boolean)this.breakSwing.getValue()).booleanValue());
/* 1233 */           EntityUtil.OffhandAttack(entity, ((Boolean)this.attackOppositeHand.getValue()).booleanValue(), ((Boolean)this.attackOppositeHand.getValue()).booleanValue());
/* 1234 */           this.manualTimer.reset();
/*      */           break;
/*      */         
/*      */         case PLACE:
/* 1238 */           mousePos = mc.field_71476_x.func_178782_a().func_177984_a();
/* 1239 */           for (Entity target : mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(mousePos))) {
/* 1240 */             if (!(target instanceof EntityEnderCrystal))
/* 1241 */               continue;  EntityUtil.attackEntity(target, ((Boolean)this.sync.getValue()).booleanValue(), ((Boolean)this.breakSwing.getValue()).booleanValue());
/* 1242 */             EntityUtil.OffhandAttack(target, ((Boolean)this.attackOppositeHand.getValue()).booleanValue(), ((Boolean)this.attackOppositeHand.getValue()).booleanValue());
/* 1243 */             this.manualTimer.reset();
/*      */           } 
/*      */           break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void rotateTo(Entity entity) {
/*      */     float[] angle;
/* 1253 */     switch ((Rotate)this.rotate.getValue()) {
/*      */       case OFF:
/* 1255 */         this.rotating = false;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case BREAK:
/*      */       case ALL:
/* 1262 */         angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), entity.func_174791_d());
/* 1263 */         if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
/* 1264 */           Banzem.rotationManager.setPlayerRotations(angle[0], angle[1]);
/*      */           break;
/*      */         } 
/* 1267 */         this.yaw = angle[0];
/* 1268 */         this.pitch = angle[1];
/* 1269 */         this.rotating = true;
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void rotateToPos(BlockPos pos) {
/*      */     float[] angle;
/* 1276 */     switch ((Rotate)this.rotate.getValue()) {
/*      */       case OFF:
/* 1278 */         this.rotating = false;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case PLACE:
/*      */       case ALL:
/* 1285 */         angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((pos.func_177958_n() + 0.5F), (pos.func_177956_o() - 0.5F), (pos.func_177952_p() + 0.5F)));
/* 1286 */         if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
/* 1287 */           Banzem.rotationManager.setPlayerRotations(angle[0], angle[1]);
/*      */           break;
/*      */         } 
/* 1290 */         this.yaw = angle[0];
/* 1291 */         this.pitch = angle[1];
/* 1292 */         this.rotating = true;
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isDoublePoppable(EntityPlayer player, float damage) {
/*      */     float health;
/* 1300 */     if (((Boolean)this.doublePop.getValue()).booleanValue() && (health = EntityUtil.getHealth((Entity)player)) <= ((Double)this.popHealth.getValue()).doubleValue() && damage > health + 0.5D && damage <= ((Float)this.popDamage.getValue()).floatValue()) {
/* 1301 */       Timer timer = this.totemPops.get(player);
/* 1302 */       return (timer == null || timer.passedMs(((Integer)this.popTime.getValue()).intValue()));
/*      */     } 
/* 1304 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isValid(Entity entity) {
/* 1309 */     return (entity != null && mc.field_71439_g.func_70068_e(entity) <= MathUtil.square(((Float)this.breakRange.getValue()).floatValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || mc.field_71439_g.func_70685_l(entity) || (!mc.field_71439_g.func_70685_l(entity) && mc.field_71439_g.func_70068_e(entity) <= MathUtil.square(((Float)this.breaktrace.getValue()).floatValue()))));
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isEligableForFeetSync(EntityPlayer player, BlockPos pos) {
/* 1314 */     if (((Boolean)this.holySync.getValue()).booleanValue()) {
/* 1315 */       BlockPos playerPos = new BlockPos(player.func_174791_d()); EnumFacing[] arrayOfEnumFacing; int i; byte b;
/* 1316 */       for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) { EnumFacing facing = arrayOfEnumFacing[b];
/* 1317 */         if (facing == EnumFacing.DOWN || facing == EnumFacing.UP || !pos.equals(playerPos.func_177977_b().func_177972_a(facing))) {
/*      */           b++; continue;
/* 1319 */         }  return true; }
/*      */       
/* 1321 */       return false;
/*      */     } 
/* 1323 */     return true;
/*      */   }
/*      */   
/*      */   public enum PredictTimer
/*      */   {
/* 1328 */     NONE,
/* 1329 */     BREAK,
/* 1330 */     PREDICT;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum AntiFriendPop
/*      */   {
/* 1336 */     NONE,
/* 1337 */     PLACE,
/* 1338 */     BREAK,
/* 1339 */     ALL;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum ThreadMode
/*      */   {
/* 1345 */     NONE,
/* 1346 */     POOL,
/* 1347 */     SOUND,
/* 1348 */     WHILE;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum AutoSwitch
/*      */   {
/* 1354 */     NONE,
/* 1355 */     TOGGLE,
/* 1356 */     ALWAYS,
/* 1357 */     SILENT;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum Raytrace
/*      */   {
/* 1363 */     NONE,
/* 1364 */     PLACE,
/* 1365 */     BREAK,
/* 1366 */     FULL;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum Switch
/*      */   {
/* 1372 */     ALWAYS,
/* 1373 */     BREAKSLOT,
/* 1374 */     CALC;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum Logic
/*      */   {
/* 1380 */     BREAKPLACE,
/* 1381 */     PLACEBREAK;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum Target
/*      */   {
/* 1387 */     CLOSEST,
/* 1388 */     UNSAFE,
/* 1389 */     DAMAGE;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum Rotate
/*      */   {
/* 1395 */     OFF,
/* 1396 */     PLACE,
/* 1397 */     BREAK,
/* 1398 */     ALL;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum DamageSync
/*      */   {
/* 1404 */     NONE,
/* 1405 */     PLACE,
/* 1406 */     BREAK;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum Settings
/*      */   {
/* 1412 */     PLACE,
/* 1413 */     BREAK,
/* 1414 */     RENDER,
/* 1415 */     MISC,
/* 1416 */     DEV;
/*      */   }
/*      */ 
/*      */   
/*      */   public enum RenderMode
/*      */   {
/* 1422 */     STATIC,
/* 1423 */     FADE,
/* 1424 */     GLIDE;
/*      */   }
/*      */ 
/*      */   
/*      */   public static class PlaceInfo
/*      */   {
/*      */     private final BlockPos pos;
/*      */     private final boolean offhand;
/*      */     private final boolean placeSwing;
/*      */     private final boolean exactHand;
/*      */     private final boolean silent;
/*      */     
/*      */     public PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand, boolean silent) {
/* 1437 */       this.pos = pos;
/* 1438 */       this.offhand = offhand;
/* 1439 */       this.placeSwing = placeSwing;
/* 1440 */       this.exactHand = exactHand;
/* 1441 */       this.silent = silent;
/*      */     }
/*      */ 
/*      */     
/*      */     public void runPlace() {
/* 1446 */       BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand, this.silent);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class RAutoCrystalRewrite
/*      */     implements Runnable
/*      */   {
/*      */     private static RAutoCrystalRewrite instance;
/*      */     private AutoCrystalRewrite autoCrystal;
/*      */     
/*      */     public static RAutoCrystalRewrite getInstance(AutoCrystalRewrite autoCrystal) {
/* 1458 */       if (instance == null) {
/* 1459 */         instance = new RAutoCrystalRewrite();
/* 1460 */         instance.autoCrystal = autoCrystal;
/*      */       } 
/* 1462 */       return instance;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void run() {
/* 1468 */       if (this.autoCrystal.threadMode.getValue() == AutoCrystalRewrite.ThreadMode.WHILE) {
/* 1469 */         while (this.autoCrystal.isOn() && this.autoCrystal.threadMode.getValue() == AutoCrystalRewrite.ThreadMode.WHILE) {
/* 1470 */           while (Banzem.eventManager.ticksOngoing());
/*      */           
/* 1472 */           if (this.autoCrystal.shouldInterrupt.get()) {
/* 1473 */             this.autoCrystal.shouldInterrupt.set(false);
/* 1474 */             this.autoCrystal.syncroTimer.reset();
/* 1475 */             this.autoCrystal.thread.interrupt();
/*      */             break;
/*      */           } 
/* 1478 */           this.autoCrystal.threadOngoing.set(true);
/* 1479 */           Banzem.safetyManager.doSafetyCheck();
/* 1480 */           this.autoCrystal.doAutoCrystalRewrite();
/* 1481 */           this.autoCrystal.threadOngoing.set(false);
/*      */           try {
/* 1483 */             Thread.sleep(((Integer)this.autoCrystal.threadDelay.getValue()).intValue());
/* 1484 */           } catch (InterruptedException e) {
/* 1485 */             this.autoCrystal.thread.interrupt();
/* 1486 */             e.printStackTrace();
/*      */           } 
/*      */         } 
/* 1489 */       } else if (this.autoCrystal.threadMode.getValue() != AutoCrystalRewrite.ThreadMode.NONE && this.autoCrystal.isOn()) {
/* 1490 */         while (Banzem.eventManager.ticksOngoing());
/*      */         
/* 1492 */         this.autoCrystal.threadOngoing.set(true);
/* 1493 */         Banzem.safetyManager.doSafetyCheck();
/* 1494 */         this.autoCrystal.doAutoCrystalRewrite();
/* 1495 */         this.autoCrystal.threadOngoing.set(false);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private class RenderPos
/*      */   {
/*      */     private BlockPos renderPos;
/*      */     private float renderTime;
/*      */     
/*      */     public RenderPos(BlockPos pos, float time) {
/* 1507 */       this.renderPos = pos;
/* 1508 */       this.renderTime = time;
/*      */     }
/*      */ 
/*      */     
/*      */     public BlockPos getPos() {
/* 1513 */       return this.renderPos;
/*      */     }
/*      */ 
/*      */     
/*      */     public float getRenderTime() {
/* 1518 */       return this.renderTime;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setPos(BlockPos pos) {
/* 1523 */       this.renderPos = pos;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setRenderTime(float time) {
/* 1528 */       this.renderTime = time;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\AutoCrystalRewrite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */