/*      */ package me.mohalk.banzem.features.modules.combat;
/*      */ import com.mojang.authlib.GameProfile;
/*      */ import java.awt.Color;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
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
/*      */ import me.mohalk.banzem.features.modules.client.Colors;
/*      */ import me.mohalk.banzem.features.setting.Bind;
/*      */ import me.mohalk.banzem.features.setting.Setting;
/*      */ import me.mohalk.banzem.util.BlockUtil;
/*      */ import me.mohalk.banzem.util.DamageUtil;
/*      */ import me.mohalk.banzem.util.EntityUtil;
/*      */ import me.mohalk.banzem.util.MathUtil;
/*      */ import me.mohalk.banzem.util.Timer;
/*      */ import me.mohalk.banzem.util.Util;
/*      */ import net.minecraft.block.Block;
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
/*      */ import net.minecraft.util.math.AxisAlignedBB;
/*      */ import net.minecraft.util.math.BlockPos;
/*      */ import net.minecraft.util.math.RayTraceResult;
/*      */ import net.minecraft.util.math.Vec3d;
/*      */ import net.minecraft.world.World;
/*      */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*      */ import org.lwjgl.input.Keyboard;
/*      */ import org.lwjgl.input.Mouse;
/*      */ 
/*      */ public class AutoCrystal extends Module {
/*   54 */   public static EntityPlayer target = null;
/*   55 */   public static Set<BlockPos> lowDmgPos = (Set<BlockPos>)new ConcurrentSet();
/*   56 */   public static Set<BlockPos> placedPos = new HashSet<>();
/*   57 */   public static Set<BlockPos> brokenPos = new HashSet<>();
/*      */   private static AutoCrystal instance;
/*   59 */   public final Timer threadTimer = new Timer();
/*   60 */   private final Setting<Settings> setting = register(new Setting("Settings", Settings.PLACE));
/*   61 */   public final Setting<Boolean> attackOppositeHand = register(new Setting("OppositeHand", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
/*   62 */   public final Setting<Boolean> removeAfterAttack = register(new Setting("AttackRemove", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
/*   63 */   public final Setting<Boolean> antiBlock = register(new Setting("AntiFeetPlace", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
/*   64 */   private final Setting<Integer> switchCooldown = register(new Setting("Cooldown", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.MISC)));
/*   65 */   private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> (this.setting.getValue() == Settings.DEV)));
/*   66 */   private final Timer switchTimer = new Timer();
/*   67 */   private final Timer manualTimer = new Timer();
/*   68 */   private final Timer breakTimer = new Timer();
/*   69 */   private final Timer placeTimer = new Timer();
/*   70 */   private final Timer syncTimer = new Timer();
/*   71 */   private final Timer predictTimer = new Timer();
/*   72 */   private final Timer renderTimer = new Timer();
/*   73 */   private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
/*   74 */   private final Timer syncroTimer = new Timer();
/*   75 */   private final Map<EntityPlayer, Timer> totemPops = new ConcurrentHashMap<>();
/*   76 */   private final Queue<CPacketUseEntity> packetUseEntities = new LinkedList<>();
/*   77 */   private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
/*   78 */   public Setting<Raytrace> raytrace = register(new Setting("Raytrace", Raytrace.NONE, v -> (this.setting.getValue() == Settings.MISC)));
/*   79 */   public Setting<Boolean> place = register(new Setting("Place", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE)));
/*   80 */   public Setting<Integer> placeDelay = register(new Setting("PlaceDelay", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   81 */   public Setting<Float> placeRange = register(new Setting("PlaceRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   82 */   public Setting<Float> minDamage = register(new Setting("MinDamage", Float.valueOf(7.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   83 */   public Setting<Float> maxSelfPlace = register(new Setting("MaxSelfPlace", Float.valueOf(10.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   84 */   public Setting<Integer> wasteAmount = register(new Setting("WasteAmount", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   85 */   public Setting<Boolean> wasteMinDmgCount = register(new Setting("CountMinDmg", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   86 */   public Setting<Float> facePlace = register(new Setting("FacePlace", Float.valueOf(8.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   87 */   public Setting<Float> placetrace = register(new Setting("Placetrace", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK)));
/*   88 */   public Setting<Boolean> antiSurround = register(new Setting("AntiSurround", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   89 */   public Setting<Boolean> limitFacePlace = register(new Setting("LimitFacePlace", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   90 */   public Setting<Boolean> oneDot15 = register(new Setting("1.15", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   91 */   public Setting<Boolean> doublePop = register(new Setting("AntiTotem", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
/*   92 */   public Setting<Double> popHealth = register(new Setting("PopHealth", Double.valueOf(1.0D), Double.valueOf(0.0D), Double.valueOf(3.0D), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
/*   93 */   public Setting<Float> popDamage = register(new Setting("PopDamage", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
/*   94 */   public Setting<Integer> popTime = register(new Setting("PopTime", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
/*   95 */   public Setting<Boolean> explode = register(new Setting("Break", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK)));
/*   96 */   public Setting<Switch> switchMode = register(new Setting("Attack", Switch.BREAKSLOT, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   97 */   public Setting<Integer> breakDelay = register(new Setting("BreakDelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   98 */   public Setting<Float> breakRange = register(new Setting("BreakRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*   99 */   public Setting<Integer> packets = register(new Setting("Packets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(6), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*  100 */   public Setting<Float> maxSelfBreak = register(new Setting("MaxSelfBreak", Float.valueOf(10.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
/*  101 */   public Setting<Float> breaktrace = register(new Setting("Breaktrace", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE)));
/*  102 */   public Setting<Boolean> manual = register(new Setting("Manual", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK)));
/*  103 */   public Setting<Boolean> manualMinDmg = register(new Setting("ManMinDmg", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.manual.getValue()).booleanValue())));
/*  104 */   public Setting<Integer> manualBreak = register(new Setting("ManualDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.manual.getValue()).booleanValue())));
/*  105 */   public Setting<Boolean> sync = register(new Setting("Sync", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && (((Boolean)this.explode.getValue()).booleanValue() || ((Boolean)this.manual.getValue()).booleanValue()))));
/*  106 */   public Setting<Boolean> instant = register(new Setting("Predict", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue())));
/*  107 */   public Setting<PredictTimer> instantTimer = register(new Setting("PredictTimer", PredictTimer.NONE, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  108 */   public Setting<Boolean> resetBreakTimer = register(new Setting("ResetBreakTimer", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  109 */   public Setting<Integer> predictDelay = register(new Setting("PredictDelay", Integer.valueOf(12), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue() && this.instantTimer.getValue() == PredictTimer.PREDICT)));
/*  110 */   public Setting<Boolean> predictCalc = register(new Setting("PredictCalc", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  111 */   public Setting<Boolean> superSafe = register(new Setting("SuperSafe", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  112 */   public Setting<Boolean> antiCommit = register(new Setting("AntiOverCommit", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue())));
/*  113 */   public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER)));
/*  114 */   private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  115 */   private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  116 */   private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  117 */   private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  118 */   public Setting<Boolean> colorSync = register(new Setting("CSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER)));
/*  119 */   public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  120 */   private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.box.getValue()).booleanValue())));
/*  121 */   public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  122 */   private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.5F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  123 */   public Setting<Boolean> text = register(new Setting("Text", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
/*  124 */   public Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  125 */   private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  126 */   private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  127 */   private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  128 */   private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  129 */   public Setting<Boolean> holdFacePlace = register(new Setting("HoldFacePlace", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
/*  130 */   public Setting<Boolean> holdFaceBreak = register(new Setting("HoldSlowBreak", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && ((Boolean)this.holdFacePlace.getValue()).booleanValue())));
/*  131 */   public Setting<Boolean> slowFaceBreak = register(new Setting("SlowFaceBreak", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
/*  132 */   public Setting<Boolean> actualSlowBreak = register(new Setting("ActuallySlow", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
/*  133 */   public Setting<Integer> facePlaceSpeed = register(new Setting("FaceSpeed", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.MISC)));
/*  134 */   public Setting<Boolean> antiNaked = register(new Setting("AntiNaked", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC)));
/*  135 */   public Setting<Float> range = register(new Setting("Range", Float.valueOf(12.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.MISC)));
/*  136 */   public Setting<Target> targetMode = register(new Setting("Target", Target.CLOSEST, v -> (this.setting.getValue() == Settings.MISC)));
/*  137 */   public Setting<Integer> minArmor = register(new Setting("MinArmor", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(125), v -> (this.setting.getValue() == Settings.MISC)));
/*  138 */   public Setting<AutoSwitch> autoSwitch = register(new Setting("Switch", AutoSwitch.TOGGLE, v -> (this.setting.getValue() == Settings.MISC)));
/*  139 */   public Setting<Bind> switchBind = register(new Setting("SwitchBind", new Bind(-1), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE)));
/*  140 */   public Setting<Boolean> offhandSwitch = register(new Setting("Offhand", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE)));
/*  141 */   public Setting<Boolean> switchBack = register(new Setting("Switchback", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && ((Boolean)this.offhandSwitch.getValue()).booleanValue())));
/*  142 */   public Setting<Boolean> lethalSwitch = register(new Setting("LethalSwitch", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE)));
/*  143 */   public Setting<Boolean> mineSwitch = register(new Setting("MineSwitch", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE)));
/*  144 */   public Setting<Rotate> rotate = register(new Setting("Rotate", Rotate.OFF, v -> (this.setting.getValue() == Settings.MISC)));
/*  145 */   public Setting<Boolean> suicide = register(new Setting("Suicide", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
/*  146 */   public Setting<Boolean> webAttack = register(new Setting("WebAttack", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE)));
/*  147 */   public Setting<Boolean> fullCalc = register(new Setting("ExtraCalc", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
/*  148 */   public Setting<Boolean> sound = register(new Setting("Sound", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC)));
/*  149 */   public Setting<Float> soundPlayer = register(new Setting("SoundPlayer", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(12.0F), v -> (this.setting.getValue() == Settings.MISC)));
/*  150 */   public Setting<Boolean> soundConfirm = register(new Setting("SoundConfirm", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC)));
/*  151 */   public Setting<Boolean> extraSelfCalc = register(new Setting("MinSelfDmg", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
/*  152 */   public Setting<AntiFriendPop> antiFriendPop = register(new Setting("FriendPop", AntiFriendPop.NONE, v -> (this.setting.getValue() == Settings.MISC)));
/*  153 */   public Setting<Boolean> noCount = register(new Setting("AntiCount", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK))));
/*  154 */   public Setting<Boolean> calcEvenIfNoDamage = register(new Setting("BigFriendCalc", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.targetMode.getValue() != Target.DAMAGE)));
/*  155 */   public Setting<Boolean> predictFriendDmg = register(new Setting("PredictFriend", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && ((Boolean)this.instant.getValue()).booleanValue())));
/*  156 */   public Setting<Float> minMinDmg = register(new Setting("MinMinDmg", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(3.0F), v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.place.getValue()).booleanValue())));
/*  157 */   public Setting<Boolean> breakSwing = register(new Setting("BreakSwing", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.DEV)));
/*  158 */   public Setting<Boolean> placeSwing = register(new Setting("PlaceSwing", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
/*  159 */   public Setting<Boolean> exactHand = register(new Setting("ExactHand", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.placeSwing.getValue()).booleanValue())));
/*  160 */   public Setting<Boolean> justRender = register(new Setting("JustRender", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
/*  161 */   public Setting<Boolean> fakeSwing = register(new Setting("FakeSwing", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.justRender.getValue()).booleanValue())));
/*  162 */   public Setting<Logic> logic = register(new Setting("Logic", Logic.BREAKPLACE, v -> (this.setting.getValue() == Settings.DEV)));
/*  163 */   public Setting<DamageSync> damageSync = register(new Setting("DamageSync", DamageSync.NONE, v -> (this.setting.getValue() == Settings.DEV)));
/*  164 */   public Setting<Integer> damageSyncTime = register(new Setting("SyncDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
/*  165 */   public Setting<Float> dropOff = register(new Setting("DropOff", Float.valueOf(5.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() == DamageSync.BREAK)));
/*  166 */   public Setting<Integer> confirm = register(new Setting("Confirm", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
/*  167 */   public Setting<Boolean> syncedFeetPlace = register(new Setting("FeetSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
/*  168 */   public Setting<Boolean> fullSync = register(new Setting("FullSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  169 */   public Setting<Boolean> syncCount = register(new Setting("SyncCount", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  170 */   public Setting<Boolean> hyperSync = register(new Setting("HyperSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  171 */   public Setting<Boolean> gigaSync = register(new Setting("GigaSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  172 */   public Setting<Boolean> syncySync = register(new Setting("SyncySync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  173 */   public Setting<Boolean> enormousSync = register(new Setting("EnormousSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  174 */   public Setting<Boolean> holySync = register(new Setting("UnbelievableSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
/*  175 */   public Setting<Boolean> rotateFirst = register(new Setting("FirstRotation", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.rotate.getValue() != Rotate.OFF && ((Integer)this.eventMode.getValue()).intValue() == 2)));
/*  176 */   public Setting<ThreadMode> threadMode = register(new Setting("Thread", ThreadMode.NONE, v -> (this.setting.getValue() == Settings.DEV)));
/*  177 */   public Setting<Integer> threadDelay = register(new Setting("ThreadDelay", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE)));
/*  178 */   public Setting<Boolean> syncThreadBool = register(new Setting("ThreadSync", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE)));
/*  179 */   public Setting<Integer> syncThreads = register(new Setting("SyncThreads", Integer.valueOf(1000), Integer.valueOf(1), Integer.valueOf(10000), v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE && ((Boolean)this.syncThreadBool.getValue()).booleanValue())));
/*  180 */   public Setting<Boolean> predictPos = register(new Setting("PredictPos", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
/*  181 */   public Setting<Integer> predictTicks = register(new Setting("ExtrapolationTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20), v -> (this.setting.getValue() == Settings.DEV && ((Boolean)this.predictPos.getValue()).booleanValue())));
/*  182 */   public Setting<Integer> rotations = register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), v -> (this.setting.getValue() == Settings.DEV)));
/*  183 */   public Setting<Boolean> predictRotate = register(new Setting("PredictRotate", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
/*  184 */   public Setting<Float> predictOffset = register(new Setting("PredictOffset", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(4.0F), v -> (this.setting.getValue() == Settings.DEV)));
/*  185 */   public Setting<Boolean> brownZombie = register(new Setting("BrownZombieMode", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
/*  186 */   public Setting<Boolean> doublePopOnDamage = register(new Setting("DamagePop", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue() && this.targetMode.getValue() == Target.DAMAGE)));
/*      */   public boolean rotating = false;
/*  188 */   private Queue<Entity> attackList = new ConcurrentLinkedQueue<>();
/*  189 */   private Map<Entity, Float> crystalMap = new HashMap<>();
/*  190 */   private Entity efficientTarget = null;
/*  191 */   private double currentDamage = 0.0D;
/*  192 */   private double renderDamage = 0.0D;
/*  193 */   private double lastDamage = 0.0D;
/*      */   private boolean didRotation = false;
/*      */   private boolean switching = false;
/*  196 */   private BlockPos placePos = null;
/*  197 */   private BlockPos renderPos = null;
/*      */   private boolean mainHand = false;
/*      */   private boolean offHand = false;
/*  200 */   private int crystalCount = 0;
/*  201 */   private int minDmgCount = 0;
/*  202 */   private int lastSlot = -1;
/*  203 */   private float yaw = 0.0F;
/*  204 */   private float pitch = 0.0F;
/*  205 */   private BlockPos webPos = null;
/*  206 */   private BlockPos lastPos = null;
/*      */   private boolean posConfirmed = false;
/*      */   private boolean foundDoublePop = false;
/*  209 */   private int rotationPacketsSpoofed = 0;
/*      */   private ScheduledExecutorService executor;
/*      */   private Thread thread;
/*      */   private EntityPlayer currentSyncTarget;
/*      */   private BlockPos syncedPlayerPos;
/*      */   private BlockPos syncedCrystalPos;
/*      */   private PlaceInfo placeInfo;
/*      */   private boolean addTolowDmg;
/*      */   
/*      */   public AutoCrystal() {
/*  219 */     super("AutoCrystal", "Best CA on the market", Module.Category.COMBAT, true, false, false);
/*  220 */     instance = this;
/*      */   }
/*      */   
/*      */   public static AutoCrystal getInstance() {
/*  224 */     if (instance == null) {
/*  225 */       instance = new AutoCrystal();
/*      */     }
/*  227 */     return instance;
/*      */   }
/*      */ 
/*      */   
/*      */   public void onTick() {
/*  232 */     if (this.threadMode.getValue() == ThreadMode.NONE && ((Integer)this.eventMode.getValue()).intValue() == 3) {
/*  233 */       doAutoCrystal();
/*      */     }
/*      */   }
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/*  239 */     if (event.getStage() == 1) {
/*  240 */       postProcessing();
/*      */     }
/*  242 */     if (event.getStage() != 0) {
/*      */       return;
/*      */     }
/*  245 */     if (((Integer)this.eventMode.getValue()).intValue() == 2) {
/*  246 */       doAutoCrystal();
/*      */     }
/*      */   }
/*      */   
/*      */   public void postTick() {
/*  251 */     if (this.threadMode.getValue() != ThreadMode.NONE) {
/*  252 */       processMultiThreading();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void onUpdate() {
/*  258 */     if (this.threadMode.getValue() == ThreadMode.NONE && ((Integer)this.eventMode.getValue()).intValue() == 1) {
/*  259 */       doAutoCrystal();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void onToggle() {
/*  265 */     brokenPos.clear();
/*  266 */     placedPos.clear();
/*  267 */     this.totemPops.clear();
/*  268 */     this.rotating = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void onDisable() {
/*  273 */     if (this.thread != null) {
/*  274 */       this.shouldInterrupt.set(true);
/*      */     }
/*  276 */     if (this.executor != null) {
/*  277 */       this.executor.shutdown();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void onEnable() {
/*  283 */     if (this.threadMode.getValue() != ThreadMode.NONE) {
/*  284 */       processMultiThreading();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public String getDisplayInfo() {
/*  290 */     if (this.switching) {
/*  291 */       return "§aSwitch";
/*      */     }
/*  293 */     if (target != null) {
/*  294 */       return target.func_70005_c_();
/*      */     }
/*  296 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onPacketSend(PacketEvent.Send event) {
/*  302 */     if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && ((Integer)this.eventMode.getValue()).intValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
/*  303 */       CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
/*  304 */       packet2.field_149476_e = this.yaw;
/*  305 */       packet2.field_149473_f = this.pitch;
/*  306 */       this.rotationPacketsSpoofed++;
/*  307 */       if (this.rotationPacketsSpoofed >= ((Integer)this.rotations.getValue()).intValue()) {
/*  308 */         this.rotating = false;
/*  309 */         this.rotationPacketsSpoofed = 0;
/*      */       } 
/*      */     } 
/*  312 */     BlockPos pos = null; CPacketUseEntity packet;
/*  313 */     if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && packet.func_149564_a((World)mc.field_71441_e) instanceof EntityEnderCrystal) {
/*  314 */       pos = packet.func_149564_a((World)mc.field_71441_e).func_180425_c();
/*  315 */       if (((Boolean)this.removeAfterAttack.getValue()).booleanValue()) {
/*  316 */         ((Entity)Objects.<Entity>requireNonNull(packet.func_149564_a((World)mc.field_71441_e))).func_70106_y();
/*  317 */         mc.field_71441_e.func_73028_b(packet.field_149567_a);
/*      */       } 
/*      */     } 
/*  320 */     if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && packet.func_149564_a((World)mc.field_71441_e) instanceof EntityEnderCrystal) {
/*  321 */       EntityEnderCrystal crystal = (EntityEnderCrystal)packet.func_149564_a((World)mc.field_71441_e);
/*  322 */       if (((Boolean)this.antiBlock.getValue()).booleanValue() && EntityUtil.isCrystalAtFeet(crystal, ((Float)this.range.getValue()).floatValue()) && pos != null) {
/*  323 */         rotateToPos(pos);
/*  324 */         BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.placeSwing.getValue()).booleanValue(), ((Boolean)this.exactHand.getValue()).booleanValue());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
/*      */   public void onPacketReceive(PacketEvent.Receive event) {
/*  332 */     if (fullNullCheck()) {
/*      */       return;
/*      */     }
/*  335 */     if (!((Boolean)this.justRender.getValue()).booleanValue() && this.switchTimer.passedMs(((Integer)this.switchCooldown.getValue()).intValue()) && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !((Boolean)this.syncedFeetPlace.getValue()).booleanValue() || this.damageSync.getValue() == DamageSync.NONE)) {
/*      */       
/*  337 */       SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket(); BlockPos pos;
/*  338 */       if (packet2.func_148993_l() == 51 && mc.field_71439_g.func_174818_b(pos = new BlockPos(packet2.func_186880_c(), packet2.func_186882_d(), packet2.func_186881_e())) + ((Float)this.predictOffset.getValue()).floatValue() <= MathUtil.square(((Float)this.breakRange.getValue()).floatValue()) && (this.instantTimer.getValue() == PredictTimer.NONE || (this.instantTimer.getValue() == PredictTimer.BREAK && this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue())) || (this.instantTimer.getValue() == PredictTimer.PREDICT && this.predictTimer.passedMs(((Integer)this.predictDelay.getValue()).intValue())))) {
/*  339 */         if (predictSlowBreak(pos.func_177977_b())) {
/*      */           return;
/*      */         }
/*  342 */         if (((Boolean)this.predictFriendDmg.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.ALL) && isRightThread())
/*  343 */           for (EntityPlayer friend : mc.field_71441_e.field_73010_i) {
/*  344 */             if (friend == null || mc.field_71439_g.equals(friend) || friend.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue()) || !Banzem.friendManager.isFriend(friend) || DamageUtil.calculateDamage(pos, (Entity)friend) <= EntityUtil.getHealth((Entity)friend) + 0.5D) {
/*      */               continue;
/*      */             }
/*      */             return;
/*      */           }  
/*  349 */         if (placedPos.contains(pos.func_177977_b())) {
/*      */           float selfDamage;
/*  351 */           if (isRightThread() ? (((Boolean)this.superSafe.getValue()).booleanValue() ? (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue()) && ((selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.field_71439_g)) - 0.5D > EntityUtil.getHealth((Entity)mc.field_71439_g) || selfDamage > ((Float)this.maxSelfBreak.getValue()).floatValue())) : ((Boolean)this.superSafe.getValue()).booleanValue()) : ((Boolean)this.superSafe.getValue()).booleanValue()) {
/*      */             return;
/*      */           }
/*  354 */           attackCrystalPredict(packet2.func_149001_c(), pos);
/*  355 */         } else if (((Boolean)this.predictCalc.getValue()).booleanValue() && isRightThread()) {
/*  356 */           float selfDamage = -1.0F;
/*  357 */           if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) {
/*  358 */             selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.field_71439_g);
/*      */           }
/*  360 */           if (selfDamage + 0.5D < EntityUtil.getHealth((Entity)mc.field_71439_g) && selfDamage <= ((Float)this.maxSelfBreak.getValue()).floatValue()) {
/*  361 */             for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*      */               float damage;
/*  363 */               if (player.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue()) || !EntityUtil.isValid((Entity)player, (((Float)this.range.getValue()).floatValue() + ((Float)this.breakRange.getValue()).floatValue())) || (((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player)) || ((damage = DamageUtil.calculateDamage(pos, (Entity)player)) <= selfDamage && (damage <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && damage <= EntityUtil.getHealth((Entity)player)))
/*      */                 continue; 
/*  365 */               if (((Boolean)this.predictRotate.getValue()).booleanValue() && ((Integer)this.eventMode.getValue()).intValue() != 2 && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
/*  366 */                 rotateToPos(pos);
/*      */               }
/*  368 */               attackCrystalPredict(packet2.func_149001_c(), pos);
/*      */             }
/*      */           
/*      */           }
/*      */         } 
/*      */       } 
/*  374 */     } else if (!((Boolean)this.soundConfirm.getValue()).booleanValue() && event.getPacket() instanceof SPacketExplosion) {
/*  375 */       SPacketExplosion packet3 = (SPacketExplosion)event.getPacket();
/*  376 */       BlockPos pos = (new BlockPos(packet3.func_149148_f(), packet3.func_149143_g(), packet3.func_149145_h())).func_177977_b();
/*  377 */       removePos(pos);
/*  378 */     } else if (event.getPacket() instanceof SPacketDestroyEntities) {
/*  379 */       SPacketDestroyEntities packet4 = (SPacketDestroyEntities)event.getPacket();
/*  380 */       for (int id : packet4.func_149098_c()) {
/*  381 */         Entity entity = mc.field_71441_e.func_73045_a(id);
/*  382 */         if (entity instanceof EntityEnderCrystal)
/*  383 */         { brokenPos.remove((new BlockPos(entity.func_174791_d())).func_177977_b());
/*  384 */           placedPos.remove((new BlockPos(entity.func_174791_d())).func_177977_b()); } 
/*      */       } 
/*  386 */     } else if (event.getPacket() instanceof SPacketEntityStatus) {
/*  387 */       SPacketEntityStatus packet5 = (SPacketEntityStatus)event.getPacket();
/*  388 */       if (packet5.func_149160_c() == 35 && packet5.func_149161_a((World)mc.field_71441_e) instanceof EntityPlayer)
/*  389 */         this.totemPops.put((EntityPlayer)packet5.func_149161_a((World)mc.field_71441_e), (new Timer()).reset()); 
/*      */     } else {
/*  391 */       SPacketSoundEffect packet; if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB) {
/*  392 */         BlockPos pos = new BlockPos(packet.func_149207_d(), packet.func_149211_e(), packet.func_149210_f());
/*  393 */         if (((Boolean)this.soundConfirm.getValue()).booleanValue()) {
/*  394 */           removePos(pos);
/*      */         }
/*  396 */         if (this.threadMode.getValue() == ThreadMode.SOUND && isRightThread() && mc.field_71439_g != null && mc.field_71439_g.func_174818_b(pos) < MathUtil.square(((Float)this.soundPlayer.getValue()).floatValue()))
/*  397 */           handlePool(true); 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean predictSlowBreak(BlockPos pos) {
/*  403 */     if (((Boolean)this.antiCommit.getValue()).booleanValue() && lowDmgPos.remove(pos)) {
/*  404 */       return shouldSlowBreak(false);
/*      */     }
/*  406 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isRightThread() {
/*  410 */     return (Util.mc.func_152345_ab() || (!Banzem.eventManager.ticksOngoing() && !this.threadOngoing.get()));
/*      */   }
/*      */   
/*      */   private void attackCrystalPredict(int entityID, BlockPos pos) {
/*  414 */     if (((Boolean)this.predictRotate.getValue()).booleanValue() && (((Integer)this.eventMode.getValue()).intValue() != 2 || this.threadMode.getValue() != ThreadMode.NONE) && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
/*  415 */       rotateToPos(pos);
/*      */     }
/*  417 */     CPacketUseEntity attackPacket = new CPacketUseEntity();
/*  418 */     attackPacket.field_149567_a = entityID;
/*  419 */     attackPacket.field_149566_b = CPacketUseEntity.Action.ATTACK;
/*  420 */     mc.field_71439_g.field_71174_a.func_147297_a((Packet)attackPacket);
/*  421 */     if (((Boolean)this.breakSwing.getValue()).booleanValue()) {
/*  422 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
/*      */     }
/*  424 */     if (((Boolean)this.resetBreakTimer.getValue()).booleanValue()) {
/*  425 */       this.breakTimer.reset();
/*      */     }
/*  427 */     this.predictTimer.reset();
/*      */   }
/*      */   
/*      */   private void removePos(BlockPos pos) {
/*  431 */     if (this.damageSync.getValue() == DamageSync.PLACE) {
/*  432 */       if (placedPos.remove(pos)) {
/*  433 */         this.posConfirmed = true;
/*      */       }
/*  435 */     } else if (this.damageSync.getValue() == DamageSync.BREAK && brokenPos.remove(pos)) {
/*  436 */       this.posConfirmed = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void onRender3D(Render3DEvent event) {
/*  442 */     if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && ((Boolean)this.render.getValue()).booleanValue() && (((Boolean)this.box.getValue()).booleanValue() || ((Boolean)this.text.getValue()).booleanValue() || ((Boolean)this.outline.getValue()).booleanValue())) {
/*  443 */       RenderUtil.drawBoxESP(this.renderPos, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
/*  444 */       if (((Boolean)this.text.getValue()).booleanValue());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onKeyInput(InputEvent.KeyInputEvent event) {
/*  451 */     if (Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof me.mohalk.banzem.features.gui.PhobosGui) && ((Bind)this.switchBind.getValue()).getKey() == Keyboard.getEventKey()) {
/*  452 */       if (((Boolean)this.switchBack.getValue()).booleanValue() && ((Boolean)this.offhandSwitch.getValue()).booleanValue() && this.offHand) {
/*  453 */         Offhand module = (Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class);
/*  454 */         if (module.isOff()) {
/*  455 */           Command.sendMessage("<" + getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
/*  456 */         } else if (module.type.getValue() == Offhand.Type.NEW) {
/*  457 */           module.setSwapToTotem(true);
/*  458 */           module.doOffhand();
/*      */         } else {
/*  460 */           module.setMode(Offhand.Mode2.TOTEMS);
/*  461 */           module.doSwitch();
/*      */         } 
/*      */         return;
/*      */       } 
/*  465 */       this.switching = !this.switching;
/*      */     } 
/*      */   }
/*      */   
/*      */   @SubscribeEvent
/*      */   public void onSettingChange(ClientEvent event) {
/*  471 */     if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
/*  472 */       if (this.executor != null) {
/*  473 */         this.executor.shutdown();
/*      */       }
/*  475 */       if (this.thread != null) {
/*  476 */         this.shouldInterrupt.set(true);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void postProcessing() {
/*  482 */     if (this.threadMode.getValue() != ThreadMode.NONE || ((Integer)this.eventMode.getValue()).intValue() != 2 || this.rotate.getValue() == Rotate.OFF || !((Boolean)this.rotateFirst.getValue()).booleanValue()) {
/*      */       return;
/*      */     }
/*  485 */     switch ((Logic)this.logic.getValue()) {
/*      */       case OFF:
/*  487 */         postProcessBreak();
/*  488 */         postProcessPlace();
/*      */         break;
/*      */       
/*      */       case PLACE:
/*  492 */         postProcessPlace();
/*  493 */         postProcessBreak();
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void postProcessBreak() {
/*  499 */     while (!this.packetUseEntities.isEmpty()) {
/*  500 */       CPacketUseEntity packet = this.packetUseEntities.poll();
/*  501 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)packet);
/*  502 */       if (((Boolean)this.breakSwing.getValue()).booleanValue()) {
/*  503 */         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
/*      */       }
/*  505 */       this.breakTimer.reset();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void postProcessPlace() {
/*  510 */     if (this.placeInfo != null) {
/*  511 */       this.placeInfo.runPlace();
/*  512 */       this.placeTimer.reset();
/*  513 */       this.placeInfo = null;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void processMultiThreading() {
/*  518 */     if (isOff()) {
/*      */       return;
/*      */     }
/*  521 */     if (this.threadMode.getValue() == ThreadMode.WHILE) {
/*  522 */       handleWhile();
/*  523 */     } else if (this.threadMode.getValue() != ThreadMode.NONE) {
/*  524 */       handlePool(false);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void handlePool(boolean justDoIt) {
/*  529 */     if (justDoIt || this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || (this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue()) && ((Boolean)this.syncThreadBool.getValue()).booleanValue())) {
/*  530 */       if (this.executor != null) {
/*  531 */         this.executor.shutdown();
/*      */       }
/*  533 */       this.executor = getExecutor();
/*  534 */       this.syncroTimer.reset();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void handleWhile() {
/*  539 */     if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || (this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue()) && ((Boolean)this.syncThreadBool.getValue()).booleanValue())) {
/*  540 */       if (this.thread == null) {
/*  541 */         this.thread = new Thread(RAutoCrystal.getInstance(this));
/*  542 */       } else if (this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue()) && !this.shouldInterrupt.get() && ((Boolean)this.syncThreadBool.getValue()).booleanValue()) {
/*  543 */         this.shouldInterrupt.set(true);
/*  544 */         this.syncroTimer.reset();
/*      */         return;
/*      */       } 
/*  547 */       if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
/*  548 */         this.thread = new Thread(RAutoCrystal.getInstance(this));
/*      */       }
/*  550 */       if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
/*      */         try {
/*  552 */           this.thread.start();
/*  553 */         } catch (Exception e) {
/*  554 */           e.printStackTrace();
/*      */         } 
/*  556 */         this.syncroTimer.reset();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private ScheduledExecutorService getExecutor() {
/*  562 */     ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
/*  563 */     service.scheduleAtFixedRate(RAutoCrystal.getInstance(this), 0L, ((Integer)this.threadDelay.getValue()).intValue(), TimeUnit.MILLISECONDS);
/*  564 */     return service;
/*      */   }
/*      */   
/*      */   public void doAutoCrystal() {
/*  568 */     if (((Boolean)this.brownZombie.getValue()).booleanValue()) {
/*      */       return;
/*      */     }
/*  571 */     if (check()) {
/*  572 */       switch ((Logic)this.logic.getValue()) {
/*      */         case PLACE:
/*  574 */           placeCrystal();
/*  575 */           breakCrystal();
/*      */           break;
/*      */         
/*      */         case OFF:
/*  579 */           breakCrystal();
/*  580 */           placeCrystal();
/*      */           break;
/*      */       } 
/*      */       
/*  584 */       manualBreaker();
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean check() {
/*  589 */     if (fullNullCheck()) {
/*  590 */       return false;
/*      */     }
/*  592 */     if (this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue())) {
/*  593 */       this.currentSyncTarget = null;
/*  594 */       this.syncedCrystalPos = null;
/*  595 */       this.syncedPlayerPos = null;
/*  596 */     } else if (((Boolean)this.syncySync.getValue()).booleanValue() && this.syncedCrystalPos != null) {
/*  597 */       this.posConfirmed = true;
/*      */     } 
/*  599 */     this.foundDoublePop = false;
/*  600 */     if (this.renderTimer.passedMs(500L)) {
/*  601 */       this.renderPos = null;
/*  602 */       this.renderTimer.reset();
/*      */     } 
/*  604 */     this.mainHand = (mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP);
/*  605 */     this.offHand = (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP);
/*  606 */     this.currentDamage = 0.0D;
/*  607 */     this.placePos = null;
/*  608 */     if (this.lastSlot != mc.field_71439_g.field_71071_by.field_70461_c || AutoTrap.isPlacing || Surround.isPlacing) {
/*  609 */       this.lastSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*  610 */       this.switchTimer.reset();
/*      */     } 
/*  612 */     if (!this.offHand && !this.mainHand) {
/*  613 */       this.placeInfo = null;
/*  614 */       this.packetUseEntities.clear();
/*      */     } 
/*  616 */     if (this.offHand || this.mainHand) {
/*  617 */       this.switching = false;
/*      */     }
/*  619 */     if ((!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching) || !DamageUtil.canBreakWeakness((EntityPlayer)mc.field_71439_g) || !this.switchTimer.passedMs(((Integer)this.switchCooldown.getValue()).intValue())) {
/*  620 */       this.renderPos = null;
/*  621 */       target = null;
/*  622 */       this.rotating = false;
/*  623 */       return false;
/*      */     } 
/*  625 */     if (((Boolean)this.mineSwitch.getValue()).booleanValue() && Mouse.isButtonDown(0) && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && Mouse.isButtonDown(1) && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemPickaxe) {
/*  626 */       switchItem();
/*      */     }
/*  628 */     mapCrystals();
/*  629 */     if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(((Integer)this.confirm.getValue()).intValue())) {
/*  630 */       this.syncTimer.setMs((((Integer)this.damageSyncTime.getValue()).intValue() + 1));
/*      */     }
/*  632 */     return true;
/*      */   }
/*      */   
/*      */   private void mapCrystals() {
/*  636 */     this.efficientTarget = null;
/*  637 */     if (((Integer)this.packets.getValue()).intValue() != 1) {
/*  638 */       this.attackList = new ConcurrentLinkedQueue<>();
/*  639 */       this.crystalMap = new HashMap<>();
/*      */     } 
/*  641 */     this.crystalCount = 0;
/*  642 */     this.minDmgCount = 0;
/*  643 */     Entity maxCrystal = null;
/*  644 */     float maxDamage = 0.5F;
/*  645 */     for (Entity entity : mc.field_71441_e.field_72996_f) {
/*  646 */       if (entity.field_70128_L || !(entity instanceof EntityEnderCrystal) || !isValid(entity))
/*  647 */         continue;  if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && entity.func_180425_c().func_177977_b().equals(this.syncedCrystalPos) && this.damageSync.getValue() != DamageSync.NONE) {
/*  648 */         this.minDmgCount++;
/*  649 */         this.crystalCount++;
/*  650 */         if (((Boolean)this.syncCount.getValue()).booleanValue()) {
/*  651 */           this.minDmgCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*  652 */           this.crystalCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*      */         } 
/*  654 */         if (!((Boolean)this.hyperSync.getValue()).booleanValue())
/*  655 */           continue;  maxCrystal = null;
/*      */         break;
/*      */       } 
/*  658 */       boolean count = false;
/*  659 */       boolean countMin = false;
/*  660 */       float selfDamage = -1.0F;
/*  661 */       if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) {
/*  662 */         selfDamage = DamageUtil.calculateDamage(entity, (Entity)mc.field_71439_g);
/*      */       }
/*  664 */       if (selfDamage + 0.5D < EntityUtil.getHealth((Entity)mc.field_71439_g) && selfDamage <= ((Float)this.maxSelfBreak.getValue()).floatValue()) {
/*  665 */         Entity beforeCrystal = maxCrystal;
/*  666 */         float beforeDamage = maxDamage;
/*  667 */         for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*      */           
/*  669 */           if (player.func_70068_e(entity) > MathUtil.square(((Float)this.range.getValue()).floatValue()))
/*      */             continue; 
/*  671 */           if (EntityUtil.isValid((Entity)player, (((Float)this.range.getValue()).floatValue() + ((Float)this.breakRange.getValue()).floatValue()))) {
/*  672 */             float f; if ((((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player)) || ((f = DamageUtil.calculateDamage(entity, (Entity)player)) <= selfDamage && (f <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && f <= EntityUtil.getHealth((Entity)player)))
/*      */               continue; 
/*  674 */             if (f > maxDamage) {
/*  675 */               maxDamage = f;
/*  676 */               maxCrystal = entity;
/*      */             } 
/*  678 */             if (((Integer)this.packets.getValue()).intValue() == 1) {
/*  679 */               if (f >= ((Float)this.minDamage.getValue()).floatValue() || !((Boolean)this.wasteMinDmgCount.getValue()).booleanValue()) {
/*  680 */                 count = true;
/*      */               }
/*  682 */               countMin = true;
/*      */               continue;
/*      */             } 
/*  685 */             if (this.crystalMap.get(entity) != null && ((Float)this.crystalMap.get(entity)).floatValue() >= f)
/*      */               continue; 
/*  687 */             this.crystalMap.put(entity, Float.valueOf(f)); continue;
/*      */           } 
/*      */           float damage;
/*  690 */           if ((this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.ALL) || !Banzem.friendManager.isFriend(player.func_70005_c_()) || (damage = DamageUtil.calculateDamage(entity, (Entity)player)) <= EntityUtil.getHealth((Entity)player) + 0.5D)
/*      */             continue; 
/*  692 */           maxCrystal = beforeCrystal;
/*  693 */           maxDamage = beforeDamage;
/*  694 */           this.crystalMap.remove(entity);
/*  695 */           if (!((Boolean)this.noCount.getValue()).booleanValue())
/*  696 */             break;  count = false;
/*  697 */           countMin = false;
/*      */         } 
/*      */       } 
/*      */       
/*  701 */       if (!countMin)
/*  702 */         continue;  this.minDmgCount++;
/*  703 */       if (!count)
/*  704 */         continue;  this.crystalCount++;
/*      */     } 
/*  706 */     if (this.damageSync.getValue() == DamageSync.BREAK && (maxDamage > this.lastDamage || this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
/*  707 */       this.lastDamage = maxDamage;
/*      */     }
/*  709 */     if (((Boolean)this.enormousSync.getValue()).booleanValue() && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && this.damageSync.getValue() != DamageSync.NONE && this.syncedCrystalPos != null) {
/*  710 */       if (((Boolean)this.syncCount.getValue()).booleanValue()) {
/*  711 */         this.minDmgCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*  712 */         this.crystalCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
/*      */       } 
/*      */       return;
/*      */     } 
/*  716 */     if (((Boolean)this.webAttack.getValue()).booleanValue() && this.webPos != null) {
/*  717 */       if (mc.field_71439_g.func_174818_b(this.webPos.func_177984_a()) > MathUtil.square(((Float)this.breakRange.getValue()).floatValue())) {
/*  718 */         this.webPos = null;
/*      */       } else {
/*  720 */         for (Entity entity : mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(this.webPos.func_177984_a()))) {
/*  721 */           if (!(entity instanceof EntityEnderCrystal))
/*  722 */             continue;  this.attackList.add(entity);
/*  723 */           this.efficientTarget = entity;
/*  724 */           this.webPos = null;
/*  725 */           this.lastDamage = 0.5D;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     }
/*  730 */     if (shouldSlowBreak(true) && maxDamage < ((Float)this.minDamage.getValue()).floatValue() && (target == null || EntityUtil.getHealth((Entity)target) > ((Float)this.facePlace.getValue()).floatValue() || (!this.breakTimer.passedMs(((Integer)this.facePlaceSpeed.getValue()).intValue()) && ((Boolean)this.slowFaceBreak.getValue()).booleanValue() && Mouse.isButtonDown(0) && ((Boolean)this.holdFacePlace.getValue()).booleanValue() && ((Boolean)this.holdFaceBreak.getValue()).booleanValue()))) {
/*  731 */       this.efficientTarget = null;
/*      */       return;
/*      */     } 
/*  734 */     if (((Integer)this.packets.getValue()).intValue() == 1) {
/*  735 */       this.efficientTarget = maxCrystal;
/*      */     } else {
/*  737 */       this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
/*  738 */       for (Map.Entry<Entity, Float> entry : this.crystalMap.entrySet()) {
/*  739 */         Entity crystal = (Entity)entry.getKey();
/*  740 */         float damage = ((Float)entry.getValue()).floatValue();
/*  741 */         if (damage >= ((Float)this.minDamage.getValue()).floatValue() || !((Boolean)this.wasteMinDmgCount.getValue()).booleanValue()) {
/*  742 */           this.crystalCount++;
/*      */         }
/*  744 */         this.attackList.add(crystal);
/*  745 */         this.minDmgCount++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean shouldSlowBreak(boolean withManual) {
/*  751 */     return ((withManual && ((Boolean)this.manual.getValue()).booleanValue() && ((Boolean)this.manualMinDmg.getValue()).booleanValue() && Mouse.isButtonDown(1) && (!Mouse.isButtonDown(0) || !((Boolean)this.holdFacePlace.getValue()).booleanValue())) || (((Boolean)this.holdFacePlace.getValue()).booleanValue() && ((Boolean)this.holdFaceBreak.getValue()).booleanValue() && Mouse.isButtonDown(0) && !this.breakTimer.passedMs(((Integer)this.facePlaceSpeed.getValue()).intValue())) || (((Boolean)this.slowFaceBreak.getValue()).booleanValue() && !this.breakTimer.passedMs(((Integer)this.facePlaceSpeed.getValue()).intValue())));
/*      */   }
/*      */   
/*      */   private void placeCrystal() {
/*  755 */     int crystalLimit = ((Integer)this.wasteAmount.getValue()).intValue();
/*  756 */     if (this.placeTimer.passedMs(((Integer)this.placeDelay.getValue()).intValue()) && ((Boolean)this.place.getValue()).booleanValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || (this.switchMode.getValue() == Switch.BREAKSLOT && this.switching))) {
/*  757 */       if ((this.offHand || this.mainHand || (this.switchMode.getValue() != Switch.ALWAYS && !this.switching)) && this.crystalCount >= crystalLimit && (!((Boolean)this.antiSurround.getValue()).booleanValue() || this.lastPos == null || !this.lastPos.equals(this.placePos))) {
/*      */         return;
/*      */       }
/*  760 */       calculateDamage(getTarget((this.targetMode.getValue() == Target.UNSAFE)));
/*  761 */       if (target != null && this.placePos != null) {
/*  762 */         if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || (((Boolean)this.lethalSwitch.getValue()).booleanValue() && EntityUtil.getHealth((Entity)target) <= ((Float)this.facePlace.getValue()).floatValue())) && !switchItem()) {
/*      */           return;
/*      */         }
/*  765 */         if (this.currentDamage < ((Float)this.minDamage.getValue()).floatValue() && ((Boolean)this.limitFacePlace.getValue()).booleanValue()) {
/*  766 */           crystalLimit = 1;
/*      */         }
/*  768 */         if (this.currentDamage >= ((Float)this.minMinDmg.getValue()).floatValue() && (this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (((Boolean)this.antiSurround.getValue()).booleanValue() && this.lastPos != null && this.lastPos.equals(this.placePos))) && (this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0D && (DamageUtil.isArmorLow(target, ((Integer)this.minArmor.getValue()).intValue()) || EntityUtil.getHealth((Entity)target) <= ((Float)this.facePlace.getValue()).floatValue() || this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || shouldHoldFacePlace())) {
/*  769 */           float damageOffset = (this.damageSync.getValue() == DamageSync.BREAK) ? (((Float)this.dropOff.getValue()).floatValue() - 5.0F) : 0.0F;
/*  770 */           boolean syncflag = false;
/*  771 */           if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && this.placePos.equals(this.lastPos) && isEligableForFeetSync(target, this.placePos) && !this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) && target.equals(this.currentSyncTarget) && target.func_180425_c().equals(this.syncedPlayerPos) && this.damageSync.getValue() != DamageSync.NONE) {
/*  772 */             this.syncedCrystalPos = this.placePos;
/*  773 */             this.lastDamage = this.currentDamage;
/*  774 */             if (((Boolean)this.fullSync.getValue()).booleanValue()) {
/*  775 */               this.lastDamage = 100.0D;
/*      */             }
/*  777 */             syncflag = true;
/*      */           } 
/*  779 */           if (syncflag || this.currentDamage - damageOffset > this.lastDamage || this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == DamageSync.NONE) {
/*  780 */             if (!syncflag && this.damageSync.getValue() != DamageSync.BREAK) {
/*  781 */               this.lastDamage = this.currentDamage;
/*      */             }
/*  783 */             this.renderPos = this.placePos;
/*  784 */             this.renderDamage = this.currentDamage;
/*  785 */             if (switchItem()) {
/*  786 */               this.currentSyncTarget = target;
/*  787 */               this.syncedPlayerPos = target.func_180425_c();
/*  788 */               if (this.foundDoublePop) {
/*  789 */                 this.totemPops.put(target, (new Timer()).reset());
/*      */               }
/*  791 */               rotateToPos(this.placePos);
/*  792 */               if (this.addTolowDmg || (((Boolean)this.actualSlowBreak.getValue()).booleanValue() && this.currentDamage < ((Float)this.minDamage.getValue()).floatValue())) {
/*  793 */                 lowDmgPos.add(this.placePos);
/*      */               }
/*  795 */               placedPos.add(this.placePos);
/*  796 */               if (!((Boolean)this.justRender.getValue()).booleanValue()) {
/*  797 */                 if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && ((Boolean)this.rotateFirst.getValue()).booleanValue() && this.rotate.getValue() != Rotate.OFF) {
/*  798 */                   this.placeInfo = new PlaceInfo(this.placePos, this.offHand, ((Boolean)this.placeSwing.getValue()).booleanValue(), ((Boolean)this.exactHand.getValue()).booleanValue());
/*      */                 } else {
/*  800 */                   BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.placeSwing.getValue()).booleanValue(), ((Boolean)this.exactHand.getValue()).booleanValue());
/*      */                 } 
/*      */               }
/*  803 */               this.lastPos = this.placePos;
/*  804 */               this.placeTimer.reset();
/*  805 */               this.posConfirmed = false;
/*  806 */               if (this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue())) {
/*  807 */                 this.syncedCrystalPos = null;
/*  808 */                 this.syncTimer.reset();
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } else {
/*  814 */         this.renderPos = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean shouldHoldFacePlace() {
/*  820 */     this.addTolowDmg = false;
/*  821 */     if (((Boolean)this.holdFacePlace.getValue()).booleanValue() && Mouse.isButtonDown(0)) {
/*  822 */       this.addTolowDmg = true;
/*  823 */       return true;
/*      */     } 
/*  825 */     return false;
/*      */   }
/*      */   
/*      */   private boolean switchItem() {
/*  829 */     if (this.offHand || this.mainHand) {
/*  830 */       return true;
/*      */     }
/*  832 */     switch ((AutoSwitch)this.autoSwitch.getValue()) {
/*      */       case OFF:
/*  834 */         return false;
/*      */       
/*      */       case PLACE:
/*  837 */         if (!this.switching) {
/*  838 */           return false;
/*      */         }
/*      */       
/*      */       case BREAK:
/*  842 */         if (!doSwitch())
/*  843 */           break;  return true;
/*      */     } 
/*      */     
/*  846 */     return false;
/*      */   }
/*      */   
/*      */   private boolean doSwitch() {
/*  850 */     if (((Boolean)this.offhandSwitch.getValue()).booleanValue()) {
/*  851 */       Offhand module = (Offhand)Banzem.moduleManager.getModuleByClass(Offhand.class);
/*  852 */       if (module.isOff()) {
/*  853 */         Command.sendMessage("<" + getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
/*  854 */         this.switching = false;
/*  855 */         return false;
/*      */       } 
/*  857 */       if (module.type.getValue() == Offhand.Type.NEW) {
/*  858 */         module.setSwapToTotem(false);
/*  859 */         module.setMode(Offhand.Mode.CRYSTALS);
/*  860 */         module.doOffhand();
/*      */       } else {
/*  862 */         module.setMode(Offhand.Mode2.CRYSTALS);
/*  863 */         module.doSwitch();
/*      */       } 
/*  865 */       this.switching = false;
/*  866 */       return true;
/*      */     } 
/*  868 */     if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
/*  869 */       this.mainHand = false;
/*      */     } else {
/*  871 */       InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
/*  872 */       this.mainHand = true;
/*      */     } 
/*  874 */     this.switching = false;
/*  875 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void calculateDamage(EntityPlayer targettedPlayer) {
/*  881 */     if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !((Boolean)this.fullCalc.getValue()).booleanValue()) {
/*      */       return;
/*      */     }
/*  884 */     float maxDamage = 0.5F;
/*  885 */     EntityPlayer currentTarget = null;
/*  886 */     BlockPos currentPos = null;
/*  887 */     float maxSelfDamage = 0.0F;
/*  888 */     this.foundDoublePop = false;
/*  889 */     BlockPos setToAir = null;
/*  890 */     IBlockState state = null; BlockPos playerPos; Block web;
/*  891 */     if (((Boolean)this.webAttack.getValue()).booleanValue() && targettedPlayer != null && (web = mc.field_71441_e.func_180495_p(playerPos = new BlockPos(targettedPlayer.func_174791_d())).func_177230_c()) == Blocks.field_150321_G) {
/*  892 */       setToAir = playerPos;
/*  893 */       state = mc.field_71441_e.func_180495_p(playerPos);
/*  894 */       mc.field_71441_e.func_175698_g(playerPos);
/*      */     } 
/*      */     
/*  897 */     for (BlockPos pos : BlockUtil.possiblePlacePositions(((Float)this.placeRange.getValue()).floatValue(), ((Boolean)this.antiSurround.getValue()).booleanValue(), ((Boolean)this.oneDot15.getValue()).booleanValue())) {
/*  898 */       if (!BlockUtil.rayTracePlaceCheck(pos, ((this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && mc.field_71439_g.func_174818_b(pos) > MathUtil.square(((Float)this.placetrace.getValue()).floatValue())), 1.0F))
/*      */         continue; 
/*  900 */       float selfDamage = -1.0F;
/*  901 */       if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) {
/*  902 */         selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.field_71439_g);
/*      */       }
/*  904 */       if (selfDamage + 0.5D >= EntityUtil.getHealth((Entity)mc.field_71439_g) || selfDamage > ((Float)this.maxSelfPlace.getValue()).floatValue())
/*      */         continue; 
/*  906 */       if (targettedPlayer != null) {
/*  907 */         float playerDamage = DamageUtil.calculateDamage(pos, (Entity)targettedPlayer);
/*  908 */         if (((Boolean)this.calcEvenIfNoDamage.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.PLACE)) {
/*  909 */           boolean friendPop = false;
/*  910 */           for (EntityPlayer friend : mc.field_71441_e.field_73010_i) {
/*      */             float friendDamage;
/*  912 */             if (friend == null || mc.field_71439_g.equals(friend) || friend.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue()) || !Banzem.friendManager.isFriend(friend) || (friendDamage = DamageUtil.calculateDamage(pos, (Entity)friend)) <= EntityUtil.getHealth((Entity)friend) + 0.5D)
/*      */               continue; 
/*  914 */             friendPop = true;
/*      */           } 
/*      */           
/*  917 */           if (friendPop)
/*      */             continue; 
/*  919 */         }  if (isDoublePoppable(targettedPlayer, playerDamage) && (currentPos == null || targettedPlayer.func_174818_b(pos) < targettedPlayer.func_174818_b(currentPos))) {
/*  920 */           currentTarget = targettedPlayer;
/*  921 */           maxDamage = playerDamage;
/*  922 */           currentPos = pos;
/*  923 */           this.foundDoublePop = true;
/*      */           continue;
/*      */         } 
/*  926 */         if (this.foundDoublePop || (playerDamage <= maxDamage && (!((Boolean)this.extraSelfCalc.getValue()).booleanValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage <= selfDamage && (playerDamage <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && playerDamage <= EntityUtil.getHealth((Entity)targettedPlayer)))
/*      */           continue; 
/*  928 */         maxDamage = playerDamage;
/*  929 */         currentTarget = targettedPlayer;
/*  930 */         currentPos = pos;
/*  931 */         maxSelfDamage = selfDamage;
/*      */         continue;
/*      */       } 
/*  934 */       float maxDamageBefore = maxDamage;
/*  935 */       EntityPlayer currentTargetBefore = currentTarget;
/*  936 */       BlockPos currentPosBefore = currentPos;
/*  937 */       float maxSelfDamageBefore = maxSelfDamage;
/*  938 */       for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*      */         
/*  940 */         if (EntityUtil.isValid((Entity)player, (((Float)this.placeRange.getValue()).floatValue() + ((Float)this.range.getValue()).floatValue()))) {
/*  941 */           if (((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player))
/*  942 */             continue;  float playerDamage = DamageUtil.calculateDamage(pos, (Entity)player);
/*  943 */           if (((Boolean)this.doublePopOnDamage.getValue()).booleanValue() && isDoublePoppable(player, playerDamage) && (currentPos == null || player.func_174818_b(pos) < player.func_174818_b(currentPos))) {
/*  944 */             currentTarget = player;
/*  945 */             maxDamage = playerDamage;
/*  946 */             currentPos = pos;
/*  947 */             maxSelfDamage = selfDamage;
/*  948 */             this.foundDoublePop = true;
/*  949 */             if (this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.PLACE)
/*      */               continue; 
/*      */             break;
/*      */           } 
/*  953 */           if (this.foundDoublePop || (playerDamage <= maxDamage && (!((Boolean)this.extraSelfCalc.getValue()).booleanValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage)) || (playerDamage <= selfDamage && (playerDamage <= ((Float)this.minDamage.getValue()).floatValue() || DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) && playerDamage <= EntityUtil.getHealth((Entity)player)))
/*      */             continue; 
/*  955 */           maxDamage = playerDamage;
/*  956 */           currentTarget = player;
/*  957 */           currentPos = pos;
/*  958 */           maxSelfDamage = selfDamage; continue;
/*      */         } 
/*      */         float friendDamage;
/*  961 */         if ((this.antiFriendPop.getValue() != AntiFriendPop.ALL && this.antiFriendPop.getValue() != AntiFriendPop.PLACE) || player == null || player.func_174818_b(pos) > MathUtil.square(((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue()) || !Banzem.friendManager.isFriend(player) || (friendDamage = DamageUtil.calculateDamage(pos, (Entity)player)) <= EntityUtil.getHealth((Entity)player) + 0.5D)
/*      */           continue; 
/*  963 */         maxDamage = maxDamageBefore;
/*  964 */         currentTarget = currentTargetBefore;
/*  965 */         currentPos = currentPosBefore;
/*  966 */         maxSelfDamage = maxSelfDamageBefore;
/*      */       } 
/*      */     } 
/*      */     
/*  970 */     if (setToAir != null) {
/*  971 */       mc.field_71441_e.func_175656_a(setToAir, state);
/*  972 */       this.webPos = currentPos;
/*      */     } 
/*  974 */     target = currentTarget;
/*  975 */     this.currentDamage = maxDamage;
/*  976 */     this.placePos = currentPos;
/*      */   }
/*      */   private EntityPlayer getTarget(boolean unsafe) {
/*      */     EntityOtherPlayerMP entityOtherPlayerMP;
/*  980 */     if (this.targetMode.getValue() == Target.DAMAGE) {
/*  981 */       return null;
/*      */     }
/*  983 */     EntityPlayer currentTarget = null;
/*  984 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*  985 */       if (EntityUtil.isntValid((Entity)player, (((Float)this.placeRange.getValue()).floatValue() + ((Float)this.range.getValue()).floatValue())) || (((Boolean)this.antiNaked.getValue()).booleanValue() && DamageUtil.isNaked(player)) || (unsafe && EntityUtil.isSafe((Entity)player)))
/*      */         continue; 
/*  987 */       if (((Integer)this.minArmor.getValue()).intValue() > 0 && DamageUtil.isArmorLow(player, ((Integer)this.minArmor.getValue()).intValue())) {
/*  988 */         currentTarget = player;
/*      */         break;
/*      */       } 
/*  991 */       if (currentTarget == null) {
/*  992 */         currentTarget = player;
/*      */         continue;
/*      */       } 
/*  995 */       if (mc.field_71439_g.func_70068_e((Entity)player) >= mc.field_71439_g.func_70068_e((Entity)currentTarget))
/*      */         continue; 
/*  997 */       currentTarget = player;
/*      */     } 
/*  999 */     if (unsafe && currentTarget == null) {
/* 1000 */       return getTarget(false);
/*      */     }
/* 1002 */     if (((Boolean)this.predictPos.getValue()).booleanValue() && currentTarget != null) {
/* 1003 */       GameProfile profile = new GameProfile((currentTarget.func_110124_au() == null) ? UUID.fromString("8af022c8-b926-41a0-8b79-2b544ff00fcf") : currentTarget.func_110124_au(), currentTarget.func_70005_c_());
/* 1004 */       EntityOtherPlayerMP newTarget = new EntityOtherPlayerMP((World)mc.field_71441_e, profile);
/* 1005 */       Vec3d extrapolatePosition = MathUtil.extrapolatePlayerPosition(currentTarget, ((Integer)this.predictTicks.getValue()).intValue());
/* 1006 */       newTarget.func_82149_j((Entity)currentTarget);
/* 1007 */       newTarget.field_70165_t = extrapolatePosition.field_72450_a;
/* 1008 */       newTarget.field_70163_u = extrapolatePosition.field_72448_b;
/* 1009 */       newTarget.field_70161_v = extrapolatePosition.field_72449_c;
/* 1010 */       newTarget.func_70606_j(EntityUtil.getHealth((Entity)currentTarget));
/* 1011 */       newTarget.field_71071_by.func_70455_b(currentTarget.field_71071_by);
/* 1012 */       entityOtherPlayerMP = newTarget;
/*      */     } 
/* 1014 */     return (EntityPlayer)entityOtherPlayerMP;
/*      */   }
/*      */   
/*      */   private void breakCrystal() {
/* 1018 */     if (((Boolean)this.explode.getValue()).booleanValue() && this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
/* 1019 */       if (((Integer)this.packets.getValue()).intValue() == 1 && this.efficientTarget != null) {
/* 1020 */         if (((Boolean)this.justRender.getValue()).booleanValue()) {
/* 1021 */           doFakeSwing();
/*      */           return;
/*      */         } 
/* 1024 */         if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean)this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
/*      */           return;
/*      */         }
/* 1027 */         rotateTo(this.efficientTarget);
/* 1028 */         attackEntity(this.efficientTarget);
/* 1029 */         this.breakTimer.reset();
/* 1030 */       } else if (!this.attackList.isEmpty()) {
/* 1031 */         if (((Boolean)this.justRender.getValue()).booleanValue()) {
/* 1032 */           doFakeSwing();
/*      */           return;
/*      */         } 
/* 1035 */         if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean)this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
/*      */           return;
/*      */         }
/* 1038 */         for (int i = 0; i < ((Integer)this.packets.getValue()).intValue(); i++) {
/* 1039 */           Entity entity = this.attackList.poll();
/* 1040 */           if (entity != null) {
/* 1041 */             rotateTo(entity);
/* 1042 */             attackEntity(entity);
/*      */           } 
/* 1044 */         }  this.breakTimer.reset();
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private void attackEntity(Entity entity) {
/* 1050 */     if (entity != null) {
/* 1051 */       if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && ((Boolean)this.rotateFirst.getValue()).booleanValue() && this.rotate.getValue() != Rotate.OFF) {
/* 1052 */         this.packetUseEntities.add(new CPacketUseEntity(entity));
/*      */       } else {
/* 1054 */         EntityUtil.attackEntity(entity, ((Boolean)this.sync.getValue()).booleanValue(), ((Boolean)this.breakSwing.getValue()).booleanValue());
/* 1055 */         brokenPos.add((new BlockPos(entity.func_174791_d())).func_177977_b());
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private void doFakeSwing() {
/* 1061 */     if (((Boolean)this.fakeSwing.getValue()).booleanValue()) {
/* 1062 */       EntityUtil.swingArmNoPacket(EnumHand.MAIN_HAND, (EntityLivingBase)mc.field_71439_g);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void manualBreaker() {
/* 1068 */     if (this.rotate.getValue() != Rotate.OFF && ((Integer)this.eventMode.getValue()).intValue() != 2 && this.rotating)
/* 1069 */       if (this.didRotation) {
/* 1070 */         mc.field_71439_g.field_70125_A = (float)(mc.field_71439_g.field_70125_A + 4.0E-4D);
/* 1071 */         this.didRotation = false;
/*      */       } else {
/* 1073 */         mc.field_71439_g.field_70125_A = (float)(mc.field_71439_g.field_70125_A - 4.0E-4D);
/* 1074 */         this.didRotation = true;
/*      */       }  
/*      */     RayTraceResult result;
/* 1077 */     if ((this.offHand || this.mainHand) && ((Boolean)this.manual.getValue()).booleanValue() && this.manualTimer.passedMs(((Integer)this.manualBreak.getValue()).intValue()) && Mouse.isButtonDown(1) && mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151031_f && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151062_by && (result = mc.field_71476_x) != null) {
/* 1078 */       Entity entity; BlockPos mousePos; switch (result.field_72313_a) {
/*      */         case OFF:
/* 1080 */           entity = result.field_72308_g;
/* 1081 */           if (!(entity instanceof EntityEnderCrystal))
/* 1082 */             break;  EntityUtil.attackEntity(entity, ((Boolean)this.sync.getValue()).booleanValue(), ((Boolean)this.breakSwing.getValue()).booleanValue());
/* 1083 */           this.manualTimer.reset();
/*      */           break;
/*      */         
/*      */         case PLACE:
/* 1087 */           mousePos = mc.field_71476_x.func_178782_a().func_177984_a();
/* 1088 */           for (Entity target : mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(mousePos))) {
/* 1089 */             if (!(target instanceof EntityEnderCrystal))
/* 1090 */               continue;  EntityUtil.attackEntity(target, ((Boolean)this.sync.getValue()).booleanValue(), ((Boolean)this.breakSwing.getValue()).booleanValue());
/* 1091 */             this.manualTimer.reset();
/*      */           } 
/*      */           break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void rotateTo(Entity entity) {
/*      */     float[] angle;
/* 1100 */     switch ((Rotate)this.rotate.getValue()) {
/*      */       case OFF:
/* 1102 */         this.rotating = false;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case BREAK:
/*      */       case ALL:
/* 1109 */         angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(Util.mc.func_184121_ak()), entity.func_174791_d());
/* 1110 */         if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
/* 1111 */           Banzem.rotationManager.setPlayerRotations(angle[0], angle[1]);
/*      */           break;
/*      */         } 
/* 1114 */         this.yaw = angle[0];
/* 1115 */         this.pitch = angle[1];
/* 1116 */         this.rotating = true;
/*      */         break;
/*      */     } 
/*      */   }
/*      */   private void rotateToPos(BlockPos pos) {
/*      */     float[] angle;
/* 1122 */     switch ((Rotate)this.rotate.getValue()) {
/*      */       case OFF:
/* 1124 */         this.rotating = false;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case PLACE:
/*      */       case ALL:
/* 1131 */         angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(Util.mc.func_184121_ak()), new Vec3d((pos.func_177958_n() + 0.5F), (pos.func_177956_o() - 0.5F), (pos.func_177952_p() + 0.5F)));
/* 1132 */         if (((Integer)this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
/* 1133 */           Banzem.rotationManager.setPlayerRotations(angle[0], angle[1]);
/*      */           break;
/*      */         } 
/* 1136 */         this.yaw = angle[0];
/* 1137 */         this.pitch = angle[1];
/* 1138 */         this.rotating = true;
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isDoublePoppable(EntityPlayer player, float damage) {
/*      */     float health;
/* 1145 */     if (((Boolean)this.doublePop.getValue()).booleanValue() && (health = EntityUtil.getHealth((Entity)player)) <= ((Double)this.popHealth.getValue()).doubleValue() && damage > health + 0.5D && damage <= ((Float)this.popDamage.getValue()).floatValue()) {
/* 1146 */       Timer timer = this.totemPops.get(player);
/* 1147 */       return (timer == null || timer.passedMs(((Integer)this.popTime.getValue()).intValue()));
/*      */     } 
/* 1149 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isValid(Entity entity) {
/* 1153 */     return (entity != null && mc.field_71439_g.func_70068_e(entity) <= MathUtil.square(((Float)this.breakRange.getValue()).floatValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || mc.field_71439_g.func_70685_l(entity) || (!mc.field_71439_g.func_70685_l(entity) && mc.field_71439_g.func_70068_e(entity) <= MathUtil.square(((Float)this.breaktrace.getValue()).floatValue()))));
/*      */   }
/*      */   
/*      */   private boolean isEligableForFeetSync(EntityPlayer player, BlockPos pos) {
/* 1157 */     if (((Boolean)this.holySync.getValue()).booleanValue()) {
/* 1158 */       BlockPos playerPos = new BlockPos(player.func_174791_d()); EnumFacing[] arrayOfEnumFacing; int i; byte b;
/* 1159 */       for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) { EnumFacing facing = arrayOfEnumFacing[b];
/*      */         BlockPos holyPos;
/* 1161 */         if (facing == EnumFacing.DOWN || facing == EnumFacing.UP || !pos.equals(holyPos = playerPos.func_177977_b().func_177972_a(facing))) {
/*      */           b++; continue;
/* 1163 */         }  return true; }
/*      */       
/* 1165 */       return false;
/*      */     } 
/* 1167 */     return true;
/*      */   }
/*      */   
/*      */   public enum PredictTimer {
/* 1171 */     NONE,
/* 1172 */     BREAK,
/* 1173 */     PREDICT;
/*      */   }
/*      */   
/*      */   public enum AntiFriendPop
/*      */   {
/* 1178 */     NONE,
/* 1179 */     PLACE,
/* 1180 */     BREAK,
/* 1181 */     ALL;
/*      */   }
/*      */   
/*      */   public enum ThreadMode
/*      */   {
/* 1186 */     NONE,
/* 1187 */     POOL,
/* 1188 */     SOUND,
/* 1189 */     WHILE;
/*      */   }
/*      */   
/*      */   public enum AutoSwitch
/*      */   {
/* 1194 */     NONE,
/* 1195 */     TOGGLE,
/* 1196 */     ALWAYS;
/*      */   }
/*      */   
/*      */   public enum Raytrace
/*      */   {
/* 1201 */     NONE,
/* 1202 */     PLACE,
/* 1203 */     BREAK,
/* 1204 */     FULL;
/*      */   }
/*      */   
/*      */   public enum Switch
/*      */   {
/* 1209 */     ALWAYS,
/* 1210 */     BREAKSLOT,
/* 1211 */     CALC;
/*      */   }
/*      */   
/*      */   public enum Logic
/*      */   {
/* 1216 */     BREAKPLACE,
/* 1217 */     PLACEBREAK;
/*      */   }
/*      */   
/*      */   public enum Target
/*      */   {
/* 1222 */     CLOSEST,
/* 1223 */     UNSAFE,
/* 1224 */     DAMAGE;
/*      */   }
/*      */   
/*      */   public enum Rotate
/*      */   {
/* 1229 */     OFF,
/* 1230 */     PLACE,
/* 1231 */     BREAK,
/* 1232 */     ALL;
/*      */   }
/*      */   
/*      */   public enum DamageSync
/*      */   {
/* 1237 */     NONE,
/* 1238 */     PLACE,
/* 1239 */     BREAK;
/*      */   }
/*      */   
/*      */   public enum Settings
/*      */   {
/* 1244 */     PLACE,
/* 1245 */     BREAK,
/* 1246 */     RENDER,
/* 1247 */     MISC,
/* 1248 */     DEV;
/*      */   }
/*      */   
/*      */   public static class PlaceInfo
/*      */   {
/*      */     private final BlockPos pos;
/*      */     private final boolean offhand;
/*      */     private final boolean placeSwing;
/*      */     private final boolean exactHand;
/*      */     
/*      */     public PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand) {
/* 1259 */       this.pos = pos;
/* 1260 */       this.offhand = offhand;
/* 1261 */       this.placeSwing = placeSwing;
/* 1262 */       this.exactHand = exactHand;
/*      */     }
/*      */     
/*      */     public void runPlace() {
/* 1266 */       BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class RAutoCrystal
/*      */     implements Runnable
/*      */   {
/*      */     private static RAutoCrystal instance;
/*      */     
/*      */     private AutoCrystal autoCrystal;
/*      */     
/*      */     public static RAutoCrystal getInstance(AutoCrystal autoCrystal) {
/* 1279 */       if (instance == null) {
/* 1280 */         instance = new RAutoCrystal();
/* 1281 */         instance.autoCrystal = autoCrystal;
/*      */       } 
/* 1283 */       return instance;
/*      */     }
/*      */ 
/*      */     
/*      */     public void run() {
/* 1288 */       if (this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
/* 1289 */         while (this.autoCrystal.isOn() && this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
/* 1290 */           while (Banzem.eventManager.ticksOngoing());
/*      */           
/* 1292 */           if (this.autoCrystal.shouldInterrupt.get()) {
/* 1293 */             this.autoCrystal.shouldInterrupt.set(false);
/* 1294 */             this.autoCrystal.syncroTimer.reset();
/* 1295 */             this.autoCrystal.thread.interrupt();
/*      */             break;
/*      */           } 
/* 1298 */           this.autoCrystal.threadOngoing.set(true);
/* 1299 */           Banzem.safetyManager.doSafetyCheck();
/* 1300 */           this.autoCrystal.doAutoCrystal();
/* 1301 */           this.autoCrystal.threadOngoing.set(false);
/*      */           try {
/* 1303 */             Thread.sleep(((Integer)this.autoCrystal.threadDelay.getValue()).intValue());
/* 1304 */           } catch (InterruptedException e) {
/* 1305 */             this.autoCrystal.thread.interrupt();
/* 1306 */             e.printStackTrace();
/*      */           } 
/*      */         } 
/* 1309 */       } else if (this.autoCrystal.threadMode.getValue() != AutoCrystal.ThreadMode.NONE && this.autoCrystal.isOn()) {
/* 1310 */         while (Banzem.eventManager.ticksOngoing());
/*      */         
/* 1312 */         this.autoCrystal.threadOngoing.set(true);
/* 1313 */         Banzem.safetyManager.doSafetyCheck();
/* 1314 */         this.autoCrystal.doAutoCrystal();
/* 1315 */         this.autoCrystal.threadOngoing.set(false);
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\AutoCrystal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */