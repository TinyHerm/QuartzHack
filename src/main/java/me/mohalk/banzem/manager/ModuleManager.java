/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  org.lwjgl.input.Keyboard
 */
package me.mohalk.banzem.manager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.mohalk.banzem.event.events.Render2DEvent;
import me.mohalk.banzem.event.events.Render3DEvent;
import me.mohalk.banzem.features.Feature;
import me.mohalk.banzem.features.gui.PhobosGui;
import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.modules.client.CSGOWatermark;
import me.mohalk.banzem.features.modules.client.ClickGui;
import me.mohalk.banzem.features.modules.client.Colors;
import me.mohalk.banzem.features.modules.client.FontMod;
import me.mohalk.banzem.features.modules.client.HUD;
import me.mohalk.banzem.features.modules.client.Managers;
import me.mohalk.banzem.features.modules.client.Notifications;
import me.mohalk.banzem.features.modules.combat.AutoArmor;
import me.mohalk.banzem.features.modules.combat.AutoCrystalRewrite;
import me.mohalk.banzem.features.modules.combat.AutoTrap;
import me.mohalk.banzem.features.modules.combat.Criticals;
import me.mohalk.banzem.features.modules.combat.HoleFiller;
import me.mohalk.banzem.features.modules.combat.Killaura;
import me.mohalk.banzem.features.modules.combat.OffhandRewrite;
import me.mohalk.banzem.features.modules.combat.Quiver;
import me.mohalk.banzem.features.modules.combat.Selftrap;
import me.mohalk.banzem.features.modules.combat.Surround;
import me.mohalk.banzem.features.modules.combat.Webaura;
import me.mohalk.banzem.features.modules.misc.AutoGG;
import me.mohalk.banzem.features.modules.misc.AutoLog;
import me.mohalk.banzem.features.modules.misc.AutoReconnect;
import me.mohalk.banzem.features.modules.misc.AutoRespawn;
import me.mohalk.banzem.features.modules.misc.BuildHeight;
import me.mohalk.banzem.features.modules.misc.ChatModifier;
import me.mohalk.banzem.features.modules.misc.MCF;
import me.mohalk.banzem.features.modules.misc.NoAFK;
import me.mohalk.banzem.features.modules.misc.NoHandShake;
import me.mohalk.banzem.features.modules.misc.NoRotate;
import me.mohalk.banzem.features.modules.misc.NoSoundLag;
import me.mohalk.banzem.features.modules.misc.RPC;
import me.mohalk.banzem.features.modules.misc.Spammer;
import me.mohalk.banzem.features.modules.misc.ToolTips;
import me.mohalk.banzem.features.modules.module.ModuleTools;
import me.mohalk.banzem.features.modules.movement.Anchor;
import me.mohalk.banzem.features.modules.movement.AntiVoid;
import me.mohalk.banzem.features.modules.movement.BlockLag;
import me.mohalk.banzem.features.modules.movement.ElytraFlight;
import me.mohalk.banzem.features.modules.movement.NoSlowDown;
import me.mohalk.banzem.features.modules.movement.ReverseStep;
import me.mohalk.banzem.features.modules.movement.Scaffold;
import me.mohalk.banzem.features.modules.movement.Speed;
import me.mohalk.banzem.features.modules.movement.Step;
import me.mohalk.banzem.features.modules.movement.Strafe;
import me.mohalk.banzem.features.modules.movement.Velocity;
import me.mohalk.banzem.features.modules.player.FakePlayer;
import me.mohalk.banzem.features.modules.player.FastPlace;
import me.mohalk.banzem.features.modules.player.Freecam;
import me.mohalk.banzem.features.modules.player.MCP;
import me.mohalk.banzem.features.modules.player.MultiTask;
import me.mohalk.banzem.features.modules.player.Replenish;
import me.mohalk.banzem.features.modules.player.SilentXP;
import me.mohalk.banzem.features.modules.player.Speedmine;
import me.mohalk.banzem.features.modules.player.Swing;
import me.mohalk.banzem.features.modules.player.TimerSpeed;
import me.mohalk.banzem.features.modules.player.XCarry;
import me.mohalk.banzem.features.modules.render.BlockHighlight;
import me.mohalk.banzem.features.modules.render.CameraClip;
import me.mohalk.banzem.features.modules.render.Chams;
import me.mohalk.banzem.features.modules.render.CrystalScale;
import me.mohalk.banzem.features.modules.render.ESP;
import me.mohalk.banzem.features.modules.render.Fullbright;
import me.mohalk.banzem.features.modules.render.HoleESP;
import me.mohalk.banzem.features.modules.render.LogoutSpots;
import me.mohalk.banzem.features.modules.render.Nametags;
import me.mohalk.banzem.features.modules.render.NoRender;
import me.mohalk.banzem.features.modules.render.Trajectories;
import me.mohalk.banzem.features.modules.render.ViewModel;
import me.mohalk.banzem.features.modules.render.VoidESP;
import me.mohalk.banzem.features.modules.render.XRay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

public class ModuleManager
extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<Module> alphabeticallySortedModules = new ArrayList<Module>();
    public Map<Module, Color> moduleColorMap = new HashMap<Module, Color>();

    public void init() {
        this.modules.add(new ModuleTools());
        this.modules.add(new OffhandRewrite());
        this.modules.add(new Surround());
        this.modules.add(new AutoTrap());
        this.modules.add(new AutoArmor());
        this.modules.add(new Criticals());
        this.modules.add(new Killaura());
        this.modules.add(new Quiver());
        this.modules.add(new AutoCrystalRewrite());
        this.modules.add(new HoleFiller());
        this.modules.add(new Selftrap());
        this.modules.add(new Webaura());
        this.modules.add(new ChatModifier());
        this.modules.add(new BuildHeight());
        this.modules.add(new AutoRespawn());
        this.modules.add(new NoRotate());
        this.modules.add(new MCF());
        this.modules.add(new AutoLog());
        this.modules.add(new Spammer());
        this.modules.add(new AutoReconnect());
        this.modules.add(new NoAFK());
        this.modules.add(new NoHandShake());
        this.modules.add(new NoSoundLag());
        this.modules.add(new RPC());
        this.modules.add(new AutoGG());
        this.modules.add(new ReverseStep());
        this.modules.add(new Strafe());
        this.modules.add(new Velocity());
        this.modules.add(new Scaffold());
        this.modules.add(new BlockLag());
        this.modules.add(new Speed());
        this.modules.add(new Step());
        this.modules.add(new Anchor());
        this.modules.add(new AntiVoid());
        this.modules.add(new ElytraFlight());
        this.modules.add(new NoSlowDown());
        this.modules.add(new FakePlayer());
        this.modules.add(new TimerSpeed());
        this.modules.add(new FastPlace());
        this.modules.add(new Freecam());
        this.modules.add(new Speedmine());
        this.modules.add(new MultiTask());
        this.modules.add(new XCarry());
        this.modules.add(new SilentXP());
        this.modules.add(new Swing());
        this.modules.add(new Replenish());
        this.modules.add(new MCP());
        this.modules.add(new NoRender());
        this.modules.add(new ViewModel());
        this.modules.add(new Fullbright());
        this.modules.add(new CameraClip());
        this.modules.add(new Chams());
        this.modules.add(new ESP());
        this.modules.add(new HoleESP());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Trajectories());
        this.modules.add(new LogoutSpots());
        this.modules.add(new XRay());
        this.modules.add(new Nametags());
        this.modules.add(new VoidESP());
        this.modules.add(new CrystalScale());
        this.modules.add(new Notifications());
        this.modules.add(new HUD());
        this.modules.add(new ToolTips());
        this.modules.add(new FontMod());
        this.modules.add(new ClickGui());
        this.modules.add(new Managers());
        this.modules.add(new Colors());
        this.modules.add(new CSGOWatermark());
        this.moduleColorMap.put(this.getModuleByClass(AutoTrap.class), new Color(193, 49, 244));
        this.moduleColorMap.put(this.getModuleByClass(Criticals.class), new Color(204, 151, 184));
        this.moduleColorMap.put(this.getModuleByClass(HoleFiller.class), new Color(166, 55, 110));
        this.moduleColorMap.put(this.getModuleByClass(Killaura.class), new Color(255, 37, 0));
        this.moduleColorMap.put(this.getModuleByClass(Selftrap.class), new Color(22, 127, 145));
        this.moduleColorMap.put(this.getModuleByClass(Surround.class), new Color(100, 0, 150));
        this.moduleColorMap.put(this.getModuleByClass(Webaura.class), new Color(11, 161, 121));
        this.moduleColorMap.put(this.getModuleByClass(AutoGG.class), new Color(240, 49, 110));
        this.moduleColorMap.put(this.getModuleByClass(AutoLog.class), new Color(176, 176, 176));
        this.moduleColorMap.put(this.getModuleByClass(AutoReconnect.class), new Color(17, 85, 153));
        this.moduleColorMap.put(this.getModuleByClass(BuildHeight.class), new Color(64, 136, 199));
        this.moduleColorMap.put(this.getModuleByClass(ChatModifier.class), new Color(255, 59, 216));
        this.moduleColorMap.put(this.getModuleByClass(MCF.class), new Color(17, 85, 255));
        this.moduleColorMap.put(this.getModuleByClass(NoAFK.class), new Color(80, 5, 98));
        this.moduleColorMap.put(this.getModuleByClass(NoRotate.class), new Color(69, 81, 223));
        this.moduleColorMap.put(this.getModuleByClass(RPC.class), new Color(0, 64, 255));
        this.moduleColorMap.put(this.getModuleByClass(Spammer.class), new Color(140, 87, 166));
        this.moduleColorMap.put(this.getModuleByClass(ToolTips.class), new Color(209, 125, 156));
        this.moduleColorMap.put(this.getModuleByClass(BlockHighlight.class), new Color(103, 182, 224));
        this.moduleColorMap.put(this.getModuleByClass(CameraClip.class), new Color(247, 169, 107));
        this.moduleColorMap.put(this.getModuleByClass(Chams.class), new Color(34, 152, 34));
        this.moduleColorMap.put(this.getModuleByClass(ESP.class), new Color(255, 27, 155));
        this.moduleColorMap.put(this.getModuleByClass(Fullbright.class), new Color(255, 164, 107));
        this.moduleColorMap.put(this.getModuleByClass(HoleESP.class), new Color(95, 83, 130));
        this.moduleColorMap.put(this.getModuleByClass(LogoutSpots.class), new Color(2, 135, 134));
        this.moduleColorMap.put(this.getModuleByClass(Nametags.class), new Color(98, 82, 223));
        this.moduleColorMap.put(this.getModuleByClass(NoRender.class), new Color(255, 164, 107));
        this.moduleColorMap.put(this.getModuleByClass(ViewModel.class), new Color(145, 223, 187));
        this.moduleColorMap.put(this.getModuleByClass(Trajectories.class), new Color(98, 18, 223));
        this.moduleColorMap.put(this.getModuleByClass(VoidESP.class), new Color(68, 178, 142));
        this.moduleColorMap.put(this.getModuleByClass(XRay.class), new Color(217, 118, 37));
        this.moduleColorMap.put(this.getModuleByClass(ElytraFlight.class), new Color(55, 161, 201));
        this.moduleColorMap.put(this.getModuleByClass(NoSlowDown.class), new Color(61, 204, 78));
        this.moduleColorMap.put(this.getModuleByClass(Speed.class), new Color(55, 161, 196));
        this.moduleColorMap.put(this.getModuleByClass(AntiVoid.class), new Color(86, 53, 98));
        this.moduleColorMap.put(this.getModuleByClass(Step.class), new Color(144, 212, 203));
        this.moduleColorMap.put(this.getModuleByClass(Strafe.class), new Color(0, 204, 255));
        this.moduleColorMap.put(this.getModuleByClass(Velocity.class), new Color(115, 134, 140));
        this.moduleColorMap.put(this.getModuleByClass(ReverseStep.class), new Color(1, 134, 140));
        this.moduleColorMap.put(this.getModuleByClass(FakePlayer.class), new Color(37, 192, 170));
        this.moduleColorMap.put(this.getModuleByClass(FastPlace.class), new Color(217, 118, 37));
        this.moduleColorMap.put(this.getModuleByClass(Freecam.class), new Color(206, 232, 128));
        this.moduleColorMap.put(this.getModuleByClass(MCP.class), new Color(153, 68, 170));
        this.moduleColorMap.put(this.getModuleByClass(MultiTask.class), new Color(17, 223, 235));
        this.moduleColorMap.put(this.getModuleByClass(Replenish.class), new Color(153, 223, 235));
        this.moduleColorMap.put(this.getModuleByClass(Speedmine.class), new Color(152, 166, 113));
        this.moduleColorMap.put(this.getModuleByClass(TimerSpeed.class), new Color(255, 133, 18));
        this.moduleColorMap.put(this.getModuleByClass(XCarry.class), new Color(254, 161, 51));
        this.moduleColorMap.put(this.getModuleByClass(ClickGui.class), new Color(26, 81, 135));
        this.moduleColorMap.put(this.getModuleByClass(Colors.class), new Color(135, 133, 26));
        this.moduleColorMap.put(this.getModuleByClass(FontMod.class), new Color(135, 26, 88));
        this.moduleColorMap.put(this.getModuleByClass(HUD.class), new Color(110, 26, 135));
        this.moduleColorMap.put(this.getModuleByClass(Managers.class), new Color(26, 90, 135));
        this.moduleColorMap.put(this.getModuleByClass(Notifications.class), new Color(170, 153, 255));
        for (Module module : this.modules) {
            module.animation.start();
        }
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T)module;
        }
        return null;
    }

    public void enableModule(Class clazz) {
        Object module = this.getModuleByClass(clazz);
        if (module != null) {
            ((Module)module).enable();
        }
    }

    public void disableModule(Class clazz) {
        Object module = this.getModuleByClass(clazz);
        if (module != null) {
            ((Module)module).disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class clazz) {
        Object module = this.getModuleByClass(clazz);
        return module != null && ((Module)module).isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled() && !module.isSliding()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add((Module)module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(arg_0 -> ((EventBus)MinecraftForge.EVENT_BUS).register(arg_0));
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void alphabeticallySortModules() {
        this.alphabeticallySortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(Module::getDisplayName)).collect(Collectors.toList());
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(arg_0 -> ((EventBus)MinecraftForge.EVENT_BUS).unregister(arg_0));
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.field_71462_r instanceof PhobosGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    public List<Module> getAnimationModules(Module.Category category) {
        ArrayList<Module> animationModules = new ArrayList<Module>();
        for (Module module : this.getEnabledModules()) {
            if (module.getCategory() != category || module.isDisabled() || !module.isSliding() || !module.isDrawn()) continue;
            animationModules.add(module);
        }
        return animationModules;
    }
}

