/*     */ package me.mohalk.banzem.manager;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Collectors;
/*     */ import me.mohalk.banzem.event.events.Render2DEvent;
/*     */ import me.mohalk.banzem.event.events.Render3DEvent;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.CSGOWatermark;
/*     */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*     */ import me.mohalk.banzem.features.modules.client.Colors;
/*     */ import me.mohalk.banzem.features.modules.client.Components;
/*     */ import me.mohalk.banzem.features.modules.client.FontMod;
/*     */ import me.mohalk.banzem.features.modules.client.HUD;
/*     */ import me.mohalk.banzem.features.modules.client.Managers;
/*     */ import me.mohalk.banzem.features.modules.client.Media;
/*     */ import me.mohalk.banzem.features.modules.client.Notifications;
/*     */ import me.mohalk.banzem.features.modules.combat.AutoArmor;
/*     */ import me.mohalk.banzem.features.modules.combat.AutoCrystalRewrite;
/*     */ import me.mohalk.banzem.features.modules.combat.AutoTrap;
/*     */ import me.mohalk.banzem.features.modules.combat.Criticals;
/*     */ import me.mohalk.banzem.features.modules.combat.Flatten;
/*     */ import me.mohalk.banzem.features.modules.combat.HoleFiller;
/*     */ import me.mohalk.banzem.features.modules.combat.Killaura;
/*     */ import me.mohalk.banzem.features.modules.combat.OffhandRewrite;
/*     */ import me.mohalk.banzem.features.modules.combat.Quiver;
/*     */ import me.mohalk.banzem.features.modules.combat.Selftrap;
/*     */ import me.mohalk.banzem.features.modules.combat.Surround;
/*     */ import me.mohalk.banzem.features.modules.combat.Webaura;
/*     */ import me.mohalk.banzem.features.modules.misc.AutoGG;
/*     */ import me.mohalk.banzem.features.modules.misc.AutoLog;
/*     */ import me.mohalk.banzem.features.modules.misc.AutoReconnect;
/*     */ import me.mohalk.banzem.features.modules.misc.AutoRespawn;
/*     */ import me.mohalk.banzem.features.modules.misc.BuildHeight;
/*     */ import me.mohalk.banzem.features.modules.misc.ChatModifier;
/*     */ import me.mohalk.banzem.features.modules.misc.Logger;
/*     */ import me.mohalk.banzem.features.modules.misc.MCF;
/*     */ import me.mohalk.banzem.features.modules.misc.NoAFK;
/*     */ import me.mohalk.banzem.features.modules.misc.NoHandShake;
/*     */ import me.mohalk.banzem.features.modules.misc.NoRotate;
/*     */ import me.mohalk.banzem.features.modules.misc.NoSoundLag;
/*     */ import me.mohalk.banzem.features.modules.misc.PingSpoof;
/*     */ import me.mohalk.banzem.features.modules.misc.RPC;
/*     */ import me.mohalk.banzem.features.modules.misc.Spammer;
/*     */ import me.mohalk.banzem.features.modules.misc.ToolTips;
/*     */ import me.mohalk.banzem.features.modules.module.ModuleTools;
/*     */ import me.mohalk.banzem.features.modules.movement.Anchor;
/*     */ import me.mohalk.banzem.features.modules.movement.AntiVoid;
/*     */ import me.mohalk.banzem.features.modules.movement.BlockLag;
/*     */ import me.mohalk.banzem.features.modules.movement.BoatFly;
/*     */ import me.mohalk.banzem.features.modules.movement.ElytraFlight;
/*     */ import me.mohalk.banzem.features.modules.movement.NoSlowDown;
/*     */ import me.mohalk.banzem.features.modules.movement.ReverseStep;
/*     */ import me.mohalk.banzem.features.modules.movement.Scaffold;
/*     */ import me.mohalk.banzem.features.modules.movement.Speed;
/*     */ import me.mohalk.banzem.features.modules.movement.Step;
/*     */ import me.mohalk.banzem.features.modules.movement.Strafe;
/*     */ import me.mohalk.banzem.features.modules.movement.Velocity;
/*     */ import me.mohalk.banzem.features.modules.player.FakePlayer;
/*     */ import me.mohalk.banzem.features.modules.player.FastPlace;
/*     */ import me.mohalk.banzem.features.modules.player.Freecam;
/*     */ import me.mohalk.banzem.features.modules.player.MCP;
/*     */ import me.mohalk.banzem.features.modules.player.MultiTask;
/*     */ import me.mohalk.banzem.features.modules.player.NoEntityTrace;
/*     */ import me.mohalk.banzem.features.modules.player.Replenish;
/*     */ import me.mohalk.banzem.features.modules.player.SilentXP;
/*     */ import me.mohalk.banzem.features.modules.player.Speedmine;
/*     */ import me.mohalk.banzem.features.modules.player.Swing;
/*     */ import me.mohalk.banzem.features.modules.player.TimerSpeed;
/*     */ import me.mohalk.banzem.features.modules.player.XCarry;
/*     */ import me.mohalk.banzem.features.modules.render.BlockHighlight;
/*     */ import me.mohalk.banzem.features.modules.render.CameraClip;
/*     */ import me.mohalk.banzem.features.modules.render.Chams;
/*     */ import me.mohalk.banzem.features.modules.render.CrystalScale;
/*     */ import me.mohalk.banzem.features.modules.render.ESP;
/*     */ import me.mohalk.banzem.features.modules.render.Fullbright;
/*     */ import me.mohalk.banzem.features.modules.render.HoleESP;
/*     */ import me.mohalk.banzem.features.modules.render.LogoutSpots;
/*     */ import me.mohalk.banzem.features.modules.render.Nametags;
/*     */ import me.mohalk.banzem.features.modules.render.NoRender;
/*     */ import me.mohalk.banzem.features.modules.render.Trajectories;
/*     */ import me.mohalk.banzem.features.modules.render.ViewModel;
/*     */ import me.mohalk.banzem.features.modules.render.VoidESP;
/*     */ import me.mohalk.banzem.features.modules.render.XRay;
/*     */ import net.minecraftforge.common.MinecraftForge;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ 
/*     */ public class ModuleManager
/*     */   extends Feature
/*     */ {
/*  99 */   public ArrayList<Module> modules = new ArrayList<>();
/* 100 */   public List<Module> sortedModules = new ArrayList<>();
/* 101 */   public List<Module> alphabeticallySortedModules = new ArrayList<>();
/* 102 */   public Map<Module, Color> moduleColorMap = new HashMap<>();
/*     */   
/*     */   public void init() {
/* 105 */     this.modules.add(new ModuleTools());
/* 106 */     this.modules.add(new OffhandRewrite());
/* 107 */     this.modules.add(new Surround());
/* 108 */     this.modules.add(new AutoTrap());
/* 109 */     this.modules.add(new AutoArmor());
/* 110 */     this.modules.add(new Criticals());
/* 111 */     this.modules.add(new Killaura());
/* 112 */     this.modules.add(new Quiver());
/* 113 */     this.modules.add(new Flatten());
/* 114 */     this.modules.add(new AutoCrystalRewrite());
/* 115 */     this.modules.add(new HoleFiller());
/* 116 */     this.modules.add(new Selftrap());
/* 117 */     this.modules.add(new Webaura());
/* 118 */     this.modules.add(new ChatModifier());
/* 119 */     this.modules.add(new BuildHeight());
/* 120 */     this.modules.add(new AutoRespawn());
/* 121 */     this.modules.add(new NoRotate());
/* 122 */     this.modules.add(new MCF());
/* 123 */     this.modules.add(new AutoLog());
/* 124 */     this.modules.add(new Spammer());
/* 125 */     this.modules.add(new AutoReconnect());
/* 126 */     this.modules.add(new NoAFK());
/* 127 */     this.modules.add(new Logger());
/* 128 */     this.modules.add(new NoHandShake());
/* 129 */     this.modules.add(new NoSoundLag());
/* 130 */     this.modules.add(new RPC());
/* 131 */     this.modules.add(new PingSpoof());
/* 132 */     this.modules.add(new AutoGG());
/* 133 */     this.modules.add(new ReverseStep());
/* 134 */     this.modules.add(new Strafe());
/* 135 */     this.modules.add(new Velocity());
/* 136 */     this.modules.add(new Scaffold());
/* 137 */     this.modules.add(new BoatFly());
/* 138 */     this.modules.add(new BlockLag());
/* 139 */     this.modules.add(new Speed());
/* 140 */     this.modules.add(new Step());
/* 141 */     this.modules.add(new Anchor());
/* 142 */     this.modules.add(new AntiVoid());
/* 143 */     this.modules.add(new ElytraFlight());
/* 144 */     this.modules.add(new NoSlowDown());
/* 145 */     this.modules.add(new FakePlayer());
/* 146 */     this.modules.add(new TimerSpeed());
/* 147 */     this.modules.add(new FastPlace());
/* 148 */     this.modules.add(new Freecam());
/* 149 */     this.modules.add(new Speedmine());
/* 150 */     this.modules.add(new MultiTask());
/* 151 */     this.modules.add(new XCarry());
/* 152 */     this.modules.add(new SilentXP());
/* 153 */     this.modules.add(new NoEntityTrace());
/* 154 */     this.modules.add(new Swing());
/* 155 */     this.modules.add(new Replenish());
/* 156 */     this.modules.add(new MCP());
/* 157 */     this.modules.add(new NoRender());
/* 158 */     this.modules.add(new ViewModel());
/* 159 */     this.modules.add(new Fullbright());
/* 160 */     this.modules.add(new CameraClip());
/* 161 */     this.modules.add(new Chams());
/* 162 */     this.modules.add(new ESP());
/* 163 */     this.modules.add(new HoleESP());
/* 164 */     this.modules.add(new BlockHighlight());
/* 165 */     this.modules.add(new Trajectories());
/* 166 */     this.modules.add(new LogoutSpots());
/* 167 */     this.modules.add(new XRay());
/* 168 */     this.modules.add(new Nametags());
/* 169 */     this.modules.add(new VoidESP());
/* 170 */     this.modules.add(new CrystalScale());
/* 171 */     this.modules.add(new Notifications());
/* 172 */     this.modules.add(new HUD());
/* 173 */     this.modules.add(new ToolTips());
/* 174 */     this.modules.add(new FontMod());
/* 175 */     this.modules.add(new ClickGui());
/* 176 */     this.modules.add(new Managers());
/* 177 */     this.modules.add(new Components());
/* 178 */     this.modules.add(new Colors());
/* 179 */     this.modules.add(new Media());
/* 180 */     this.modules.add(new CSGOWatermark());
/* 181 */     this.moduleColorMap.put((Module)getModuleByClass(AutoTrap.class), new Color(193, 49, 244));
/* 182 */     this.moduleColorMap.put((Module)getModuleByClass(Criticals.class), new Color(204, 151, 184));
/* 183 */     this.moduleColorMap.put((Module)getModuleByClass(HoleFiller.class), new Color(166, 55, 110));
/* 184 */     this.moduleColorMap.put((Module)getModuleByClass(Killaura.class), new Color(255, 37, 0));
/* 185 */     this.moduleColorMap.put((Module)getModuleByClass(Selftrap.class), new Color(22, 127, 145));
/* 186 */     this.moduleColorMap.put((Module)getModuleByClass(Surround.class), new Color(100, 0, 150));
/* 187 */     this.moduleColorMap.put((Module)getModuleByClass(Webaura.class), new Color(11, 161, 121));
/* 188 */     this.moduleColorMap.put((Module)getModuleByClass(AutoGG.class), new Color(240, 49, 110));
/* 189 */     this.moduleColorMap.put((Module)getModuleByClass(AutoLog.class), new Color(176, 176, 176));
/* 190 */     this.moduleColorMap.put((Module)getModuleByClass(AutoReconnect.class), new Color(17, 85, 153));
/* 191 */     this.moduleColorMap.put((Module)getModuleByClass(BuildHeight.class), new Color(64, 136, 199));
/* 192 */     this.moduleColorMap.put((Module)getModuleByClass(ChatModifier.class), new Color(255, 59, 216));
/* 193 */     this.moduleColorMap.put((Module)getModuleByClass(Logger.class), new Color(186, 0, 109));
/* 194 */     this.moduleColorMap.put((Module)getModuleByClass(MCF.class), new Color(17, 85, 255));
/* 195 */     this.moduleColorMap.put((Module)getModuleByClass(NoAFK.class), new Color(80, 5, 98));
/* 196 */     this.moduleColorMap.put((Module)getModuleByClass(NoRotate.class), new Color(69, 81, 223));
/* 197 */     this.moduleColorMap.put((Module)getModuleByClass(RPC.class), new Color(0, 64, 255));
/* 198 */     this.moduleColorMap.put((Module)getModuleByClass(Spammer.class), new Color(140, 87, 166));
/* 199 */     this.moduleColorMap.put((Module)getModuleByClass(ToolTips.class), new Color(209, 125, 156));
/* 200 */     this.moduleColorMap.put((Module)getModuleByClass(BlockHighlight.class), new Color(103, 182, 224));
/* 201 */     this.moduleColorMap.put((Module)getModuleByClass(CameraClip.class), new Color(247, 169, 107));
/* 202 */     this.moduleColorMap.put((Module)getModuleByClass(Chams.class), new Color(34, 152, 34));
/* 203 */     this.moduleColorMap.put((Module)getModuleByClass(ESP.class), new Color(255, 27, 155));
/* 204 */     this.moduleColorMap.put((Module)getModuleByClass(Fullbright.class), new Color(255, 164, 107));
/* 205 */     this.moduleColorMap.put((Module)getModuleByClass(HoleESP.class), new Color(95, 83, 130));
/* 206 */     this.moduleColorMap.put((Module)getModuleByClass(LogoutSpots.class), new Color(2, 135, 134));
/* 207 */     this.moduleColorMap.put((Module)getModuleByClass(Nametags.class), new Color(98, 82, 223));
/* 208 */     this.moduleColorMap.put((Module)getModuleByClass(NoRender.class), new Color(255, 164, 107));
/* 209 */     this.moduleColorMap.put((Module)getModuleByClass(ViewModel.class), new Color(145, 223, 187));
/* 210 */     this.moduleColorMap.put((Module)getModuleByClass(Trajectories.class), new Color(98, 18, 223));
/* 211 */     this.moduleColorMap.put((Module)getModuleByClass(VoidESP.class), new Color(68, 178, 142));
/* 212 */     this.moduleColorMap.put((Module)getModuleByClass(XRay.class), new Color(217, 118, 37));
/* 213 */     this.moduleColorMap.put((Module)getModuleByClass(ElytraFlight.class), new Color(55, 161, 201));
/* 214 */     this.moduleColorMap.put((Module)getModuleByClass(NoSlowDown.class), new Color(61, 204, 78));
/* 215 */     this.moduleColorMap.put((Module)getModuleByClass(Speed.class), new Color(55, 161, 196));
/* 216 */     this.moduleColorMap.put((Module)getModuleByClass(AntiVoid.class), new Color(86, 53, 98));
/* 217 */     this.moduleColorMap.put((Module)getModuleByClass(Step.class), new Color(144, 212, 203));
/* 218 */     this.moduleColorMap.put((Module)getModuleByClass(Strafe.class), new Color(0, 204, 255));
/* 219 */     this.moduleColorMap.put((Module)getModuleByClass(Velocity.class), new Color(115, 134, 140));
/* 220 */     this.moduleColorMap.put((Module)getModuleByClass(ReverseStep.class), new Color(1, 134, 140));
/* 221 */     this.moduleColorMap.put((Module)getModuleByClass(FakePlayer.class), new Color(37, 192, 170));
/* 222 */     this.moduleColorMap.put((Module)getModuleByClass(FastPlace.class), new Color(217, 118, 37));
/* 223 */     this.moduleColorMap.put((Module)getModuleByClass(Freecam.class), new Color(206, 232, 128));
/* 224 */     this.moduleColorMap.put((Module)getModuleByClass(MCP.class), new Color(153, 68, 170));
/* 225 */     this.moduleColorMap.put((Module)getModuleByClass(MultiTask.class), new Color(17, 223, 235));
/* 226 */     this.moduleColorMap.put((Module)getModuleByClass(Replenish.class), new Color(153, 223, 235));
/* 227 */     this.moduleColorMap.put((Module)getModuleByClass(Speedmine.class), new Color(152, 166, 113));
/* 228 */     this.moduleColorMap.put((Module)getModuleByClass(TimerSpeed.class), new Color(255, 133, 18));
/* 229 */     this.moduleColorMap.put((Module)getModuleByClass(XCarry.class), new Color(254, 161, 51));
/* 230 */     this.moduleColorMap.put((Module)getModuleByClass(ClickGui.class), new Color(26, 81, 135));
/* 231 */     this.moduleColorMap.put((Module)getModuleByClass(Colors.class), new Color(135, 133, 26));
/* 232 */     this.moduleColorMap.put((Module)getModuleByClass(Components.class), new Color(135, 26, 26));
/* 233 */     this.moduleColorMap.put((Module)getModuleByClass(FontMod.class), new Color(135, 26, 88));
/* 234 */     this.moduleColorMap.put((Module)getModuleByClass(HUD.class), new Color(110, 26, 135));
/* 235 */     this.moduleColorMap.put((Module)getModuleByClass(Managers.class), new Color(26, 90, 135));
/* 236 */     this.moduleColorMap.put((Module)getModuleByClass(Notifications.class), new Color(170, 153, 255));
/* 237 */     this.moduleColorMap.put((Module)getModuleByClass(Media.class), new Color(138, 45, 13));
/* 238 */     for (Module module : this.modules) {
/* 239 */       module.animation.start();
/*     */     }
/*     */   }
/*     */   
/*     */   public Module getModuleByName(String name) {
/* 244 */     for (Module module : this.modules) {
/* 245 */       if (!module.getName().equalsIgnoreCase(name))
/* 246 */         continue;  return module;
/*     */     } 
/* 248 */     return null;
/*     */   }
/*     */   
/*     */   public <T extends Module> T getModuleByClass(Class<T> clazz) {
/* 252 */     for (Module module : this.modules) {
/* 253 */       if (!clazz.isInstance(module))
/* 254 */         continue;  return (T)module;
/*     */     } 
/* 256 */     return null;
/*     */   }
/*     */   
/*     */   public void enableModule(Class<Module> clazz) {
/* 260 */     Object module = getModuleByClass(clazz);
/* 261 */     if (module != null) {
/* 262 */       ((Module)module).enable();
/*     */     }
/*     */   }
/*     */   
/*     */   public void disableModule(Class<Module> clazz) {
/* 267 */     Object module = getModuleByClass(clazz);
/* 268 */     if (module != null) {
/* 269 */       ((Module)module).disable();
/*     */     }
/*     */   }
/*     */   
/*     */   public void enableModule(String name) {
/* 274 */     Module module = getModuleByName(name);
/* 275 */     if (module != null) {
/* 276 */       module.enable();
/*     */     }
/*     */   }
/*     */   
/*     */   public void disableModule(String name) {
/* 281 */     Module module = getModuleByName(name);
/* 282 */     if (module != null) {
/* 283 */       module.disable();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isModuleEnabled(String name) {
/* 288 */     Module module = getModuleByName(name);
/* 289 */     return (module != null && module.isOn());
/*     */   }
/*     */   
/*     */   public boolean isModuleEnabled(Class<Module> clazz) {
/* 293 */     Object module = getModuleByClass(clazz);
/* 294 */     return (module != null && ((Module)module).isOn());
/*     */   }
/*     */   
/*     */   public Module getModuleByDisplayName(String displayName) {
/* 298 */     for (Module module : this.modules) {
/* 299 */       if (!module.getDisplayName().equalsIgnoreCase(displayName))
/* 300 */         continue;  return module;
/*     */     } 
/* 302 */     return null;
/*     */   }
/*     */   
/*     */   public ArrayList<Module> getEnabledModules() {
/* 306 */     ArrayList<Module> enabledModules = new ArrayList<>();
/* 307 */     for (Module module : this.modules) {
/* 308 */       if (!module.isEnabled() && !module.isSliding())
/* 309 */         continue;  enabledModules.add(module);
/*     */     } 
/* 311 */     return enabledModules;
/*     */   }
/*     */   
/*     */   public ArrayList<Module> getModulesByCategory(Module.Category category) {
/* 315 */     ArrayList<Module> modulesCategory = new ArrayList<>();
/* 316 */     this.modules.forEach(module -> {
/*     */           if (module.getCategory() == category) {
/*     */             modulesCategory.add(module);
/*     */           }
/*     */         });
/* 321 */     return modulesCategory;
/*     */   }
/*     */   
/*     */   public List<Module.Category> getCategories() {
/* 325 */     return Arrays.asList(Module.Category.values());
/*     */   }
/*     */   
/*     */   public void onLoad() {
/* 329 */     Objects.requireNonNull(MinecraftForge.EVENT_BUS); this.modules.stream().filter(Module::listening).forEach(MinecraftForge.EVENT_BUS::register);
/* 330 */     this.modules.forEach(Module::onLoad);
/*     */   }
/*     */   
/*     */   public void onUpdate() {
/* 334 */     this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
/*     */   }
/*     */   
/*     */   public void onTick() {
/* 338 */     this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
/*     */   }
/*     */   
/*     */   public void onRender2D(Render2DEvent event) {
/* 342 */     this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
/*     */   }
/*     */   
/*     */   public void onRender3D(Render3DEvent event) {
/* 346 */     this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
/*     */   }
/*     */   
/*     */   public void sortModules(boolean reverse) {
/* 350 */     this.sortedModules = (List<Module>)getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> Integer.valueOf(this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1)))).collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   public void alphabeticallySortModules() {
/* 354 */     this.alphabeticallySortedModules = (List<Module>)getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(Module::getDisplayName)).collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   public void onLogout() {
/* 358 */     this.modules.forEach(Module::onLogout);
/*     */   }
/*     */   
/*     */   public void onLogin() {
/* 362 */     this.modules.forEach(Module::onLogin);
/*     */   }
/*     */   
/*     */   public void onUnload() {
/* 366 */     Objects.requireNonNull(MinecraftForge.EVENT_BUS); this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
/* 367 */     this.modules.forEach(Module::onUnload);
/*     */   }
/*     */   
/*     */   public void onUnloadPost() {
/* 371 */     for (Module module : this.modules) {
/* 372 */       module.enabled.setValue(Boolean.valueOf(false));
/*     */     }
/*     */   }
/*     */   
/*     */   public void onKeyPressed(int eventKey) {
/* 377 */     if (eventKey == 0 || !Keyboard.getEventKeyState() || mc.field_71462_r instanceof me.mohalk.banzem.features.gui.PhobosGui) {
/*     */       return;
/*     */     }
/* 380 */     this.modules.forEach(module -> {
/*     */           if (module.getBind().getKey() == eventKey) {
/*     */             module.toggle();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public List<Module> getAnimationModules(Module.Category category) {
/* 388 */     ArrayList<Module> animationModules = new ArrayList<>();
/* 389 */     for (Module module : getEnabledModules()) {
/* 390 */       if (module.getCategory() != category || module.isDisabled() || !module.isSliding() || !module.isDrawn())
/* 391 */         continue;  animationModules.add(module);
/*     */     } 
/* 393 */     return animationModules;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\ModuleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */