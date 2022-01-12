/*     */ package me.mohalk.banzem.manager;
/*     */ import com.google.common.base.Strings;
/*     */ import java.nio.FloatBuffer;
/*     */ import java.nio.IntBuffer;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.ClientEvent;
/*     */ import me.mohalk.banzem.event.events.ConnectionEvent;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.Render2DEvent;
/*     */ import me.mohalk.banzem.event.events.Render3DEvent;
/*     */ import me.mohalk.banzem.event.events.TotemPopEvent;
/*     */ import me.mohalk.banzem.event.events.UpdateWalkingPlayerEvent;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.client.ServerModule;
/*     */ import me.mohalk.banzem.features.modules.combat.AutoCrystal;
/*     */ import me.mohalk.banzem.util.GLUProjection;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.gui.ScaledResolution;
/*     */ import net.minecraft.client.renderer.GLAllocation;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.network.play.client.CPacketChatMessage;
/*     */ import net.minecraft.network.play.server.SPacketEntityStatus;
/*     */ import net.minecraft.network.play.server.SPacketPlayerListItem;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.client.event.ClientChatEvent;
/*     */ import net.minecraftforge.client.event.RenderGameOverlayEvent;
/*     */ import net.minecraftforge.client.event.RenderWorldLastEvent;
/*     */ import net.minecraftforge.common.MinecraftForge;
/*     */ import net.minecraftforge.event.entity.living.LivingEvent;
/*     */ import net.minecraftforge.fml.common.eventhandler.Event;
/*     */ import net.minecraftforge.fml.common.eventhandler.EventPriority;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import net.minecraftforge.fml.common.gameevent.TickEvent;
/*     */ import net.minecraftforge.fml.common.network.FMLNetworkEvent;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ public class EventManager extends Feature {
/*  42 */   private final Timer timer = new Timer();
/*  43 */   private final Timer logoutTimer = new Timer();
/*  44 */   private final Timer switchTimer = new Timer();
/*     */   private boolean keyTimeout;
/*  46 */   private final AtomicBoolean tickOngoing = new AtomicBoolean(false);
/*     */   
/*     */   public void init() {
/*  49 */     MinecraftForge.EVENT_BUS.register(this);
/*     */   }
/*     */   
/*     */   public void onUnload() {
/*  53 */     MinecraftForge.EVENT_BUS.unregister(this);
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onUpdate(LivingEvent.LivingUpdateEvent event) {
/*  58 */     if (!fullNullCheck() && (event.getEntity().func_130014_f_()).field_72995_K && event.getEntityLiving().equals(mc.field_71439_g)) {
/*  59 */       Banzem.potionManager.update();
/*  60 */       Banzem.totemPopManager.onUpdate();
/*  61 */       Banzem.inventoryManager.update();
/*  62 */       Banzem.holeManager.update();
/*  63 */       Banzem.safetyManager.onUpdate();
/*  64 */       Banzem.moduleManager.onUpdate();
/*  65 */       Banzem.timerManager.update();
/*  66 */       if (this.timer.passedMs(((Integer)(Managers.getInstance()).moduleListUpdates.getValue()).intValue())) {
/*  67 */         Banzem.moduleManager.sortModules(true);
/*  68 */         Banzem.moduleManager.alphabeticallySortModules();
/*  69 */         this.timer.reset();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSettingChange(ClientEvent event) {
/*  76 */     if (event.getStage() == 2 && mc.func_147114_u() != null && ServerModule.getInstance().isConnected() && mc.field_71441_e != null) {
/*  77 */       String command = "@Server" + ServerModule.getInstance().getServerPrefix() + "module " + event.getSetting().getFeature().getName() + " set " + event.getSetting().getName() + " " + event.getSetting().getPlannedValue().toString();
/*  78 */       CPacketChatMessage cPacketChatMessage = new CPacketChatMessage(command);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.HIGHEST)
/*     */   public void onTickHighest(TickEvent.ClientTickEvent event) {
/*  84 */     if (event.phase == TickEvent.Phase.START) {
/*  85 */       this.tickOngoing.set(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.LOWEST)
/*     */   public void onTickLowest(TickEvent.ClientTickEvent event) {
/*  91 */     if (event.phase == TickEvent.Phase.END) {
/*  92 */       this.tickOngoing.set(false);
/*  93 */       AutoCrystal.getInstance().postTick();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean ticksOngoing() {
/*  98 */     return this.tickOngoing.get();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
/* 103 */     this.logoutTimer.reset();
/* 104 */     Banzem.moduleManager.onLogin();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
/* 109 */     Banzem.moduleManager.onLogout();
/* 110 */     Banzem.totemPopManager.onLogout();
/* 111 */     Banzem.potionManager.onLogout();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onTick(TickEvent.ClientTickEvent event) {
/* 116 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/* 119 */     Banzem.moduleManager.onTick();
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.HIGHEST)
/*     */   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
/* 124 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/* 127 */     if (event.getStage() == 0) {
/* 128 */       Banzem.baritoneManager.onUpdateWalkingPlayer();
/* 129 */       Banzem.speedManager.updateValues();
/* 130 */       Banzem.rotationManager.updateRotations();
/* 131 */       Banzem.positionManager.updatePosition();
/*     */     } 
/* 133 */     if (event.getStage() == 1) {
/* 134 */       Banzem.rotationManager.restoreRotations();
/* 135 */       Banzem.positionManager.restorePosition();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketSend(PacketEvent.Send event) {
/* 141 */     if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketHeldItemChange) {
/* 142 */       this.switchTimer.reset();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isOnSwitchCoolDown() {
/* 147 */     return !this.switchTimer.passedMs(500L);
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceive(PacketEvent.Receive event) {
/* 152 */     if (event.getStage() != 0) {
/*     */       return;
/*     */     }
/* 155 */     Banzem.serverManager.onPacketReceived();
/* 156 */     if (event.getPacket() instanceof SPacketEntityStatus) {
/* 157 */       SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
/* 158 */       if (packet.func_149160_c() == 35 && packet.func_149161_a((World)mc.field_71441_e) instanceof EntityPlayer) {
/* 159 */         EntityPlayer player = (EntityPlayer)packet.func_149161_a((World)mc.field_71441_e);
/* 160 */         MinecraftForge.EVENT_BUS.post((Event)new TotemPopEvent(player));
/* 161 */         Banzem.totemPopManager.onTotemPop(player);
/* 162 */         Banzem.potionManager.onTotemPop(player);
/*     */       } 
/* 164 */     } else if (event.getPacket() instanceof SPacketPlayerListItem && !fullNullCheck() && this.logoutTimer.passedS(1.0D)) {
/* 165 */       SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
/* 166 */       if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.func_179768_b()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.func_179768_b())) {
/*     */         return;
/*     */       }
/* 169 */       packet.func_179767_a().stream().filter(Objects::nonNull).filter(data -> (!Strings.isNullOrEmpty(data.func_179962_a().getName()) || data.func_179962_a().getId() != null)).forEach(data -> {
/*     */             String name; EntityPlayer entity;
/*     */             UUID id = data.func_179962_a().getId();
/*     */             switch (packet.func_179768_b()) {
/*     */               case ADD_PLAYER:
/*     */                 name = data.func_179962_a().getName();
/*     */                 MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(0, id, name));
/*     */                 break;
/*     */               case REMOVE_PLAYER:
/*     */                 entity = mc.field_71441_e.func_152378_a(id);
/*     */                 if (entity != null) {
/*     */                   String logoutName = entity.func_70005_c_();
/*     */                   MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(1, entity, id, logoutName));
/*     */                   break;
/*     */                 } 
/*     */                 MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(2, id, null));
/*     */                 break;
/*     */             } 
/*     */           });
/* 188 */     } else if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketTimeUpdate) {
/* 189 */       Banzem.serverManager.update();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onWorldRender(RenderWorldLastEvent event) {
/* 195 */     if (event.isCanceled()) {
/*     */       return;
/*     */     }
/* 198 */     mc.field_71424_I.func_76320_a("phobos");
/* 199 */     GlStateManager.func_179090_x();
/* 200 */     GlStateManager.func_179147_l();
/* 201 */     GlStateManager.func_179118_c();
/* 202 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/* 203 */     GlStateManager.func_179103_j(7425);
/* 204 */     GlStateManager.func_179097_i();
/* 205 */     GlStateManager.func_187441_d(1.0F);
/* 206 */     Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
/* 207 */     GLUProjection projection = GLUProjection.getInstance();
/* 208 */     IntBuffer viewPort = GLAllocation.func_74527_f(16);
/* 209 */     FloatBuffer modelView = GLAllocation.func_74529_h(16);
/* 210 */     FloatBuffer projectionPort = GLAllocation.func_74529_h(16);
/* 211 */     GL11.glGetFloat(2982, modelView);
/* 212 */     GL11.glGetFloat(2983, projectionPort);
/* 213 */     GL11.glGetInteger(2978, viewPort);
/* 214 */     ScaledResolution scaledResolution = new ScaledResolution(Minecraft.func_71410_x());
/* 215 */     projection.updateMatrices(viewPort, modelView, projectionPort, scaledResolution.func_78326_a() / (Minecraft.func_71410_x()).field_71443_c, scaledResolution.func_78328_b() / (Minecraft.func_71410_x()).field_71440_d);
/* 216 */     Banzem.moduleManager.onRender3D(render3dEvent);
/* 217 */     GlStateManager.func_187441_d(1.0F);
/* 218 */     GlStateManager.func_179103_j(7424);
/* 219 */     GlStateManager.func_179084_k();
/* 220 */     GlStateManager.func_179141_d();
/* 221 */     GlStateManager.func_179098_w();
/* 222 */     GlStateManager.func_179126_j();
/* 223 */     GlStateManager.func_179089_o();
/* 224 */     GlStateManager.func_179089_o();
/* 225 */     GlStateManager.func_179132_a(true);
/* 226 */     GlStateManager.func_179098_w();
/* 227 */     GlStateManager.func_179147_l();
/* 228 */     GlStateManager.func_179126_j();
/* 229 */     mc.field_71424_I.func_76319_b();
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void renderHUD(RenderGameOverlayEvent.Post event) {
/* 234 */     if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
/* 235 */       Banzem.textManager.updateResolution();
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.LOW)
/*     */   public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
/* 241 */     if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
/* 242 */       ScaledResolution resolution = new ScaledResolution(mc);
/* 243 */       Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
/* 244 */       Banzem.moduleManager.onRender2D(render2DEvent);
/* 245 */       GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.HIGHEST)
/*     */   public void onChatSent(ClientChatEvent event) {
/* 251 */     if (event.getMessage().startsWith(Command.getCommandPrefix())) {
/* 252 */       event.setCanceled(true);
/*     */       try {
/* 254 */         mc.field_71456_v.func_146158_b().func_146239_a(event.getMessage());
/* 255 */         if (event.getMessage().length() > 1) {
/* 256 */           Banzem.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
/*     */         } else {
/* 258 */           Command.sendMessage("Please enter a command.");
/*     */         } 
/* 260 */       } catch (Exception e) {
/* 261 */         e.printStackTrace();
/* 262 */         Command.sendMessage("§cAn error occurred while running this command. Check the log!");
/*     */       } 
/* 264 */       event.setMessage("");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\EventManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */