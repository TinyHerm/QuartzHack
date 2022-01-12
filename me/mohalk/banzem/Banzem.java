/*     */ package me.mohalk.banzem;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import me.mohalk.banzem.features.gui.custom.GuiCustomMainScreen;
/*     */ import me.mohalk.banzem.features.modules.client.IRC;
/*     */ import me.mohalk.banzem.features.modules.misc.RPC;
/*     */ import me.mohalk.banzem.features.modules.module.Display;
/*     */ import me.mohalk.banzem.manager.ColorManager;
/*     */ import me.mohalk.banzem.manager.CommandManager;
/*     */ import me.mohalk.banzem.manager.ConfigManager;
/*     */ import me.mohalk.banzem.manager.EventManager;
/*     */ import me.mohalk.banzem.manager.FileManager;
/*     */ import me.mohalk.banzem.manager.FriendManager;
/*     */ import me.mohalk.banzem.manager.HoleManager;
/*     */ import me.mohalk.banzem.manager.InventoryManager;
/*     */ import me.mohalk.banzem.manager.ModuleManager;
/*     */ import me.mohalk.banzem.manager.NoStopManager;
/*     */ import me.mohalk.banzem.manager.NotificationManager;
/*     */ import me.mohalk.banzem.manager.PacketManager;
/*     */ import me.mohalk.banzem.manager.PositionManager;
/*     */ import me.mohalk.banzem.manager.PotionManager;
/*     */ import me.mohalk.banzem.manager.ReloadManager;
/*     */ import me.mohalk.banzem.manager.RotationManager;
/*     */ import me.mohalk.banzem.manager.SafetyManager;
/*     */ import me.mohalk.banzem.manager.ServerManager;
/*     */ import me.mohalk.banzem.manager.SpeedManager;
/*     */ import me.mohalk.banzem.manager.TextManager;
/*     */ import me.mohalk.banzem.manager.TimerManager;
/*     */ import me.mohalk.banzem.manager.TotemPopManager;
/*     */ import me.mohalk.banzem.manager.WaypointManager;
/*     */ import net.minecraftforge.fml.common.Mod;
/*     */ import net.minecraftforge.fml.common.Mod.EventHandler;
/*     */ import net.minecraftforge.fml.common.Mod.Instance;
/*     */ import net.minecraftforge.fml.common.event.FMLInitializationEvent;
/*     */ import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
/*     */ import org.apache.logging.log4j.LogManager;
/*     */ import org.apache.logging.log4j.Logger;
/*     */ import org.lwjgl.opengl.Display;
/*     */ 
/*     */ @Mod(modid = "quartzhack", name = "QuartzHack", version = "0.3.0")
/*     */ public class Banzem {
/*     */   public static final String MODID = "quartzhack";
/*     */   public static final String MODNAME = "QuartzHack";
/*     */   public static final String MODVER = "0.3.0";
/*  45 */   public static final Logger LOGGER = LogManager.getLogger("QuartzHack");
/*     */   
/*     */   public static ModuleManager moduleManager;
/*     */   public static SpeedManager speedManager;
/*     */   public static PositionManager positionManager;
/*     */   public static RotationManager rotationManager;
/*     */   public static CommandManager commandManager;
/*     */   public static EventManager eventManager;
/*     */   public static ConfigManager configManager;
/*     */   public static FileManager fileManager;
/*     */   public static FriendManager friendManager;
/*     */   public static TextManager textManager;
/*     */   public static ColorManager colorManager;
/*     */   public static ServerManager serverManager;
/*     */   public static PotionManager potionManager;
/*     */   public static InventoryManager inventoryManager;
/*     */   public static TimerManager timerManager;
/*     */   public static PacketManager packetManager;
/*     */   public static ReloadManager reloadManager;
/*     */   public static TotemPopManager totemPopManager;
/*     */   public static HoleManager holeManager;
/*     */   public static NotificationManager notificationManager;
/*     */   public static SafetyManager safetyManager;
/*     */   public static GuiCustomMainScreen customMainScreen;
/*     */   public static NoStopManager baritoneManager;
/*     */   public static WaypointManager waypointManager;
/*     */   @Instance
/*     */   public static Banzem INSTANCE;
/*     */   
/*     */   public static void load() {
/*  75 */     LOGGER.info("  Loading QuartzHack");
/*  76 */     unloaded = false;
/*  77 */     if (reloadManager != null) {
/*  78 */       reloadManager.unload();
/*  79 */       reloadManager = null;
/*     */     } 
/*  81 */     baritoneManager = new NoStopManager();
/*  82 */     totemPopManager = new TotemPopManager();
/*  83 */     timerManager = new TimerManager();
/*  84 */     packetManager = new PacketManager();
/*  85 */     serverManager = new ServerManager();
/*  86 */     colorManager = new ColorManager();
/*  87 */     textManager = new TextManager();
/*  88 */     moduleManager = new ModuleManager();
/*  89 */     speedManager = new SpeedManager();
/*  90 */     rotationManager = new RotationManager();
/*  91 */     positionManager = new PositionManager();
/*  92 */     commandManager = new CommandManager();
/*  93 */     eventManager = new EventManager();
/*  94 */     configManager = new ConfigManager();
/*  95 */     fileManager = new FileManager();
/*  96 */     friendManager = new FriendManager();
/*  97 */     potionManager = new PotionManager();
/*  98 */     inventoryManager = new InventoryManager();
/*  99 */     holeManager = new HoleManager();
/* 100 */     notificationManager = new NotificationManager();
/* 101 */     safetyManager = new SafetyManager();
/* 102 */     waypointManager = new WaypointManager();
/* 103 */     LOGGER.info("Initialized Managers");
/* 104 */     moduleManager.init();
/* 105 */     LOGGER.info("Modules loaded.");
/* 106 */     configManager.init();
/* 107 */     eventManager.init();
/* 108 */     LOGGER.info("EventManager loaded.");
/* 109 */     textManager.init(true);
/* 110 */     moduleManager.onLoad();
/* 111 */     totemPopManager.init();
/* 112 */     timerManager.init();
/* 113 */     if (((RPC)moduleManager.getModuleByClass(RPC.class)).isEnabled()) {
/* 114 */       DiscordPresence.start();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void unload(boolean unload) {
/* 119 */     LOGGER.info("  Unloading QuartzHack");
/* 120 */     if (unload) {
/* 121 */       reloadManager = new ReloadManager();
/* 122 */       reloadManager.init((commandManager != null) ? commandManager.getPrefix() : ".");
/*     */     } 
/* 124 */     if (baritoneManager != null) {
/* 125 */       baritoneManager.stop();
/*     */     }
/* 127 */     onUnload();
/* 128 */     eventManager = null;
/* 129 */     holeManager = null;
/* 130 */     timerManager = null;
/* 131 */     moduleManager = null;
/* 132 */     totemPopManager = null;
/* 133 */     serverManager = null;
/* 134 */     colorManager = null;
/* 135 */     textManager = null;
/* 136 */     speedManager = null;
/* 137 */     rotationManager = null;
/* 138 */     positionManager = null;
/* 139 */     commandManager = null;
/* 140 */     configManager = null;
/* 141 */     fileManager = null;
/* 142 */     friendManager = null;
/* 143 */     potionManager = null;
/* 144 */     inventoryManager = null;
/* 145 */     notificationManager = null;
/* 146 */     safetyManager = null;
/* 147 */     LOGGER.info("QuartzHack unloaded! ");
/*     */   }
/*     */   
/*     */   public static void reload() {
/* 151 */     unload(false);
/* 152 */     load();
/*     */   }
/*     */   
/*     */   public static void onUnload() {
/* 156 */     if (!unloaded) {
/*     */       try {
/* 158 */         IRC.INSTANCE.disconnect();
/*     */       }
/* 160 */       catch (IOException e) {
/* 161 */         e.printStackTrace();
/*     */       } 
/* 163 */       eventManager.onUnload();
/* 164 */       moduleManager.onUnload();
/* 165 */       configManager.saveConfig(configManager.config.replaceFirst("QuartzHack/", ""));
/* 166 */       moduleManager.onUnloadPost();
/* 167 */       timerManager.unload();
/* 168 */       unloaded = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void preInit(FMLPreInitializationEvent event) {}
/*     */   
/*     */   @EventHandler
/*     */   public void init(FMLInitializationEvent event) {
/* 178 */     customMainScreen = new GuiCustomMainScreen();
/* 179 */     Display.setTitle((String)(Display.getInstance()).gang.getDefaultValue());
/* 180 */     load();
/*     */   }
/*     */   
/*     */   private static boolean unloaded = false;
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\Banzem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */