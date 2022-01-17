/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.Display
 */
package me.mohalk.banzem;

import java.io.IOException;
import me.mohalk.banzem.DiscordPresence;
import me.mohalk.banzem.features.gui.custom.GuiCustomMainScreen;
import me.mohalk.banzem.features.modules.client.IRC;
import me.mohalk.banzem.features.modules.misc.RPC;
import me.mohalk.banzem.features.modules.module.Display;
import me.mohalk.banzem.manager.ColorManager;
import me.mohalk.banzem.manager.CommandManager;
import me.mohalk.banzem.manager.ConfigManager;
import me.mohalk.banzem.manager.EventManager;
import me.mohalk.banzem.manager.FileManager;
import me.mohalk.banzem.manager.FriendManager;
import me.mohalk.banzem.manager.HoleManager;
import me.mohalk.banzem.manager.InventoryManager;
import me.mohalk.banzem.manager.ModuleManager;
import me.mohalk.banzem.manager.NoStopManager;
import me.mohalk.banzem.manager.NotificationManager;
import me.mohalk.banzem.manager.PacketManager;
import me.mohalk.banzem.manager.PositionManager;
import me.mohalk.banzem.manager.PotionManager;
import me.mohalk.banzem.manager.ReloadManager;
import me.mohalk.banzem.manager.RotationManager;
import me.mohalk.banzem.manager.SafetyManager;
import me.mohalk.banzem.manager.ServerManager;
import me.mohalk.banzem.manager.SpeedManager;
import me.mohalk.banzem.manager.TextManager;
import me.mohalk.banzem.manager.TimerManager;
import me.mohalk.banzem.manager.TotemPopManager;
import me.mohalk.banzem.manager.WaypointManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid="quartzhack", name="QuartzHack", version="0.3.0")
public class Banzem {
    public static final String MODID = "quartzhack";
    public static final String MODNAME = "QuartzHack";
    public static final String MODVER = "0.3.0";
    public static final Logger LOGGER = LogManager.getLogger((String)"QuartzHack");
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static NotificationManager notificationManager;
    public static SafetyManager safetyManager;
    public static GuiCustomMainScreen customMainScreen;
    public static NoStopManager baritoneManager;
    public static WaypointManager waypointManager;
    @Mod.Instance
    public static Banzem INSTANCE;
    private static boolean unloaded;

    public static void load() {
        LOGGER.info("  Loading QuartzHack");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        baritoneManager = new NoStopManager();
        totemPopManager = new TotemPopManager();
        timerManager = new TimerManager();
        packetManager = new PacketManager();
        serverManager = new ServerManager();
        colorManager = new ColorManager();
        textManager = new TextManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        commandManager = new CommandManager();
        eventManager = new EventManager();
        configManager = new ConfigManager();
        fileManager = new FileManager();
        friendManager = new FriendManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        holeManager = new HoleManager();
        notificationManager = new NotificationManager();
        safetyManager = new SafetyManager();
        waypointManager = new WaypointManager();
        LOGGER.info("Initialized Managers");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        totemPopManager.init();
        timerManager.init();
        if (moduleManager.getModuleByClass(RPC.class).isEnabled()) {
            DiscordPresence.start();
        }
    }

    public static void unload(boolean unload) {
        LOGGER.info("  Unloading QuartzHack");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        if (baritoneManager != null) {
            baritoneManager.stop();
        }
        Banzem.onUnload();
        eventManager = null;
        holeManager = null;
        timerManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        positionManager = null;
        commandManager = null;
        configManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        notificationManager = null;
        safetyManager = null;
        LOGGER.info("QuartzHack unloaded! ");
    }

    public static void reload() {
        Banzem.unload(false);
        Banzem.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            try {
                IRC.INSTANCE.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Banzem.configManager.config.replaceFirst("QuartzHack/", ""));
            moduleManager.onUnloadPost();
            timerManager.unload();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        customMainScreen = new GuiCustomMainScreen();
        org.lwjgl.opengl.Display.setTitle((String)Display.getInstance().gang.getDefaultValue());
        Banzem.load();
    }

    static {
        unloaded = false;
    }
}

