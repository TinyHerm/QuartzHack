/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  org.lwjgl.input.Keyboard
 */
package me.mohalk.banzem.features.modules.client;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.event.events.Render2DEvent;
import me.mohalk.banzem.event.events.Render3DEvent;
import me.mohalk.banzem.features.Feature;
import me.mohalk.banzem.features.command.Command;
import me.mohalk.banzem.features.gui.PhobosGui;
import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Bind;
import me.mohalk.banzem.features.setting.Setting;
import me.mohalk.banzem.manager.WaypointManager;
import me.mohalk.banzem.util.ColorUtil;
import me.mohalk.banzem.util.RenderUtil;
import me.mohalk.banzem.util.Timer;
import me.mohalk.banzem.util.Util;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

public class IRC
extends Module {
    public static final Random avRandomizer = new Random();
    private static final ResourceLocation SHULKER_GUI_TEXTURE = null;
    public static IRC INSTANCE;
    public static IRCHandler handler;
    public static List<String> phobosUsers;
    public Setting<String> ip = this.register(new Setting<String>("IP", "206.189.218.150"));
    public Setting<Boolean> waypoints = this.register(new Setting<Boolean>("Waypoints", false));
    public Setting<Boolean> ding = this.register(new Setting<Boolean>("Ding", Boolean.valueOf(false), v -> this.waypoints.getValue()));
    public Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Boolean> inventories = this.register(new Setting<Boolean>("Inventories", false));
    public Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.inventories.getValue()));
    public Setting<Boolean> own = this.register(new Setting<Object>("OwnShulker", Boolean.valueOf(true), v -> this.inventories.getValue()));
    public Setting<Integer> cooldown = this.register(new Setting<Object>("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> this.inventories.getValue()));
    public Setting<Boolean> offsets = this.register(new Setting<Boolean>("Offsets", false));
    private final Setting<Integer> yPerPlayer = this.register(new Setting<Object>("Y/Player", Integer.valueOf(18), v -> this.offsets.getValue()));
    private final Setting<Integer> xOffset = this.register(new Setting<Object>("XOffset", Integer.valueOf(4), v -> this.offsets.getValue()));
    private final Setting<Integer> yOffset = this.register(new Setting<Object>("YOffset", Integer.valueOf(2), v -> this.offsets.getValue()));
    private final Setting<Integer> trOffset = this.register(new Setting<Object>("TROffset", Integer.valueOf(2), v -> this.offsets.getValue()));
    public Setting<Integer> invH = this.register(new Setting<Object>("InvH", Integer.valueOf(3), v -> this.inventories.getValue()));
    public Setting<Bind> pingBind = this.register(new Setting<Bind>("Ping", new Bind(-1)));
    public boolean status = false;
    public Timer updateTimer = new Timer();
    public Timer downTimer = new Timer();
    public BlockPos waypointTarget;
    private int textRadarY = 0;
    private boolean down = false;
    private boolean pressed = false;

    public IRC() {
        super("PhobosChat", "QuartzHack  chat server", Module.Category.CLIENT, true, false, true);
        INSTANCE = this;
    }

    public static void updateInventory() throws IOException {
        IRC.handler.outputStream.writeUTF("updateinventory");
        IRC.handler.outputStream.writeUTF(IRC.mc.field_71439_g.func_70005_c_());
        IRC.writeByteArray(IRC.serializeInventory(), IRC.handler.outputStream);
    }

    public static void updateInventories() {
        for (String player : phobosUsers) {
            try {
                IRC.send("inventory", player);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateWaypoint(BlockPos pos, String server, String dimension, Color color) throws IOException {
        IRC.send("waypoint", server + ":" + dimension + ":" + pos.func_177958_n() + ":" + pos.func_177956_o() + ":" + pos.func_177952_p(), color.getRed() + ":" + color.getGreen() + ":" + color.getBlue() + ":" + color.getAlpha());
    }

    public static void removeWaypoint() throws IOException {
        IRC.handler.outputStream.writeUTF("removewaypoint");
        IRC.handler.outputStream.writeUTF(IRC.mc.field_71439_g.func_70005_c_());
        IRC.handler.outputStream.flush();
    }

    public static void send(String command, String data, String data1) throws IOException {
        IRC.handler.outputStream.writeUTF(command);
        IRC.handler.outputStream.writeUTF(IRC.mc.field_71439_g.func_70005_c_());
        IRC.handler.outputStream.writeUTF(data);
        IRC.handler.outputStream.writeUTF(data1);
        IRC.handler.outputStream.flush();
    }

    public static void send(String command, String data) throws IOException {
        IRC.handler.outputStream.writeUTF(command);
        IRC.handler.outputStream.writeUTF(IRC.mc.field_71439_g.func_70005_c_());
        IRC.handler.outputStream.writeUTF(data);
        IRC.handler.outputStream.flush();
    }

    private static byte[] readByteArrayLWithLength(DataInputStream reader) throws IOException {
        int length = reader.readInt();
        if (length > 0) {
            byte[] cifrato = new byte[length];
            reader.readFully(cifrato, 0, cifrato.length);
            return cifrato;
        }
        return null;
    }

    public static void writeByteArray(byte[] data, DataOutputStream writer) throws IOException {
        writer.writeInt(data.length);
        writer.write(data);
        writer.flush();
    }

    public static List<ItemStack> deserializeInventory(byte[] inventory) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inventory));
        ArrayList inventoryList = (ArrayList)stream.readObject();
        return inventoryList;
    }

    public static byte[] serializeInventory() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(new ArrayList(IRC.mc.field_71439_g.field_71071_by.field_70462_a));
        return bos.toByteArray();
    }

    public static void say(String message) throws IOException {
        IRC.handler.outputStream.writeUTF("message");
        IRC.handler.outputStream.writeUTF(IRC.mc.field_71439_g.func_70005_c_());
        IRC.handler.outputStream.writeUTF(message);
        IRC.handler.outputStream.flush();
    }

    public static void cockt(int id) throws IOException {
        IRC.handler.outputStream.writeUTF("cockt");
        IRC.handler.outputStream.writeInt(id);
        IRC.handler.outputStream.flush();
    }

    public static String getDimension(int dim) {
        switch (dim) {
            case 0: {
                return "Overworld";
            }
            case -1: {
                return "Nether";
            }
            case 1: {
                return "End";
            }
        }
        return "";
    }

    @Override
    public void onUpdate() {
        this.status = handler != null && handler.isAlive() && !handler.isInterrupted() ? !IRC.handler.socket.isClosed() : false;
        if (this.updateTimer.passedMs(5000L) && handler != null && handler.isAlive() && !IRC.handler.socket.isClosed()) {
            try {
                IRC.handler.outputStream.writeUTF("update");
                IRC.handler.outputStream.writeUTF(IRC.mc.field_71439_g.func_70005_c_());
                IRC.handler.outputStream.flush();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.updateTimer.reset();
        }
        if (!mc.func_71356_B() && !(IRC.mc.field_71462_r instanceof PhobosGui) && handler != null && !IRC.handler.socket.isClosed() && this.status) {
            if (this.down) {
                if (this.downTimer.passedMs(2000L)) {
                    try {
                        IRC.removeWaypoint();
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    this.down = false;
                    this.downTimer.reset();
                }
                if (!Keyboard.isKeyDown((int)this.pingBind.getValue().getKey())) {
                    try {
                        IRC.updateWaypoint(this.waypointTarget, IRC.mc.field_71422_O.field_78845_b, String.valueOf(IRC.mc.field_71439_g.field_71093_bK), new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()));
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            if (Keyboard.isKeyDown((int)this.pingBind.getValue().getKey())) {
                if (!this.pressed) {
                    this.down = true;
                    this.pressed = true;
                }
            } else {
                this.down = false;
                this.pressed = false;
                this.downTimer.reset();
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Feature.fullNullCheck() || mc.func_71356_B()) {
            return;
        }
        RayTraceResult result = IRC.mc.field_71439_g.func_174822_a(2000.0, event.getPartialTicks());
        if (result != null) {
            this.waypointTarget = new BlockPos(result.field_72307_f);
        }
        if (this.waypoints.getValue().booleanValue()) {
            for (WaypointManager.Waypoint waypoint : Banzem.waypointManager.waypoints.values()) {
                if (IRC.mc.field_71439_g.field_71093_bK != waypoint.dimension || !IRC.mc.field_71422_O.field_78845_b.equals(waypoint.server)) continue;
                waypoint.renderBox();
                waypoint.render();
                GlStateManager.func_179126_j();
                GlStateManager.func_179132_a((boolean)true);
                GlStateManager.func_179145_e();
                GlStateManager.func_179084_k();
                GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                RenderHelper.func_74518_a();
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (Feature.fullNullCheck()) {
            return;
        }
        if (this.inventories.getValue().booleanValue()) {
            int x = -4 + this.xOffset.getValue();
            int y = 10 + this.yOffset.getValue();
            this.textRadarY = 0;
            for (String player : phobosUsers) {
                if (Banzem.inventoryManager.inventories.get(player) != null) continue;
                List<ItemStack> stacks = Banzem.inventoryManager.inventories.get(player);
                this.renderShulkerToolTip(stacks, x, y, player);
                this.textRadarY = (y += this.yPerPlayer.getValue() + 60) - 10 - this.yOffset.getValue() + this.trOffset.getValue();
            }
        }
    }

    public void connect() throws IOException {
        if (!IRC.INSTANCE.status) {
            Socket socket = new Socket(this.ip.getValue(), 1488);
            handler = new IRCHandler(socket);
            handler.start();
            IRC.handler.outputStream.writeUTF("update");
            IRC.handler.outputStream.writeUTF(IRC.mc.field_71439_g.func_70005_c_());
            IRC.handler.outputStream.flush();
            IRC.INSTANCE.status = true;
            Command.sendMessage("\u00c2\u00a7aIRC connected successfully!");
        } else {
            Command.sendMessage("\u00c2\u00a7cIRC is already connected!");
        }
    }

    public void disconnect() throws IOException {
        if (IRC.INSTANCE.status) {
            IRC.handler.socket.close();
            if (!handler.isInterrupted()) {
                handler.interrupt();
            }
        } else {
            Command.sendMessage("\u00c2\u00a7cIRC is not connected!");
        }
    }

    public void friendAll() throws IOException {
        IRC.handler.outputStream.writeUTF("friendall");
        IRC.handler.outputStream.flush();
    }

    public void list() throws IOException {
        IRC.handler.outputStream.writeUTF("list");
        IRC.handler.outputStream.flush();
    }

    public void renderShulkerToolTip(List<ItemStack> stacks, int x, int y, String name) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179140_f();
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        mc.func_110434_K().func_110577_a(SHULKER_GUI_TEXTURE);
        RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
        RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + this.invH.getValue(), 500);
        RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
        GlStateManager.func_179097_i();
        Color color = new Color(0, 0, 0, 255);
        this.renderer.drawStringWithShadow(name, x + 8, y + 6, ColorUtil.toRGBA(color));
        GlStateManager.func_179126_j();
        RenderHelper.func_74520_c();
        GlStateManager.func_179091_B();
        GlStateManager.func_179142_g();
        GlStateManager.func_179145_e();
        for (int i = 0; i < stacks.size(); ++i) {
            int iX = x + i % 9 * 18 + 8;
            int iY = y + i / 9 * 18 + 18;
            ItemStack itemStack = stacks.get(i);
            IRC.mc.func_175599_af().field_77023_b = 501.0f;
            RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
            RenderUtil.itemRender.func_180453_a(IRC.mc.field_71466_p, itemStack, iX, iY, null);
            IRC.mc.func_175599_af().field_77023_b = 0.0f;
        }
        GlStateManager.func_179140_f();
        GlStateManager.func_179084_k();
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static class IRCHandler
    extends Thread {
        public Socket socket;
        public DataInputStream inputStream;
        public DataOutputStream outputStream;

        public IRCHandler(Socket socket) {
            super(Util.mc.field_71439_g.func_70005_c_());
            this.socket = socket;
            try {
                this.inputStream = new DataInputStream(socket.getInputStream());
                this.outputStream = new DataOutputStream(socket.getOutputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Command.sendMessage("\u00c2\u00a7aSocket thread starting!");
            while (true) {
                try {
                    while (true) {
                        String[] friends;
                        String f;
                        String name;
                        String input;
                        if ((input = this.inputStream.readUTF()).equalsIgnoreCase("message")) {
                            name = this.inputStream.readUTF();
                            String message = this.inputStream.readUTF();
                            Command.sendMessage("\u00c2\u00a7c[IRC] \u00c2\u00a7r<" + name + ">: " + message);
                        }
                        if (input.equalsIgnoreCase("list")) {
                            String[] split;
                            f = this.inputStream.readUTF();
                            friends = split = f.split("%%%");
                            for (String friend : split) {
                                Command.sendMessage("\u00c2\u00a7b" + friend.replace("_&_", " ID: "));
                            }
                        } else if (input.equalsIgnoreCase("friendall")) {
                            String[] split2;
                            f = this.inputStream.readUTF();
                            friends = split2 = f.split("%%%");
                            for (String friend : split2) {
                                if (friend.equals(Util.mc.field_71439_g.func_70005_c_())) continue;
                                Banzem.friendManager.addFriend(friend);
                                Command.sendMessage("\u00c2\u00a7b" + friend + " has been friended");
                            }
                        } else if (input.equalsIgnoreCase("waypoint")) {
                            name = this.inputStream.readUTF();
                            String[] inputs = this.inputStream.readUTF().split(":");
                            String[] colors = this.inputStream.readUTF().split(":");
                            String server = inputs[0];
                            String dimension = inputs[1];
                            Color color = new Color(Integer.parseInt((String)colors[0]), Integer.parseInt((String)colors[1]), Integer.parseInt((String)colors[2]), Integer.parseInt((String)colors[3]));
                            Banzem.waypointManager.waypoints.put(name, new WaypointManager.Waypoint(name, server, Integer.parseInt(dimension), Integer.parseInt(inputs[2]), Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), color));
                            Command.sendMessage("\u00c2\u00a7c[IRC] \u00c2\u00a7r" + name + " has set a waypoint at \u00c2\u00a7c(" + Integer.parseInt(inputs[2]) + "," + Integer.parseInt(inputs[3]) + "," + Integer.parseInt(inputs[4]) + ")\u00c2\u00a7r on the server \u00c2\u00a7c" + server + "\u00c2\u00a7r in the dimension \u00c2\u00a7c" + IRC.getDimension(Integer.parseInt(dimension)));
                            if (IRC.INSTANCE.ding.getValue().booleanValue()) {
                                Util.mc.field_71441_e.func_184134_a(Util.mc.field_71439_g.field_70165_t, Util.mc.field_71439_g.field_70163_u, Util.mc.field_71439_g.field_70161_v, SoundEvents.field_187604_bf, SoundCategory.PLAYERS, 1.0f, 0.7f, false);
                            }
                        } else if (input.equalsIgnoreCase("removewaypoint")) {
                            name = this.inputStream.readUTF();
                            Banzem.waypointManager.waypoints.remove(name);
                            Command.sendMessage("\u00c2\u00a7c[IRC] \u00c2\u00a7r" + name + " has removed their waypoint");
                            if (IRC.INSTANCE.ding.getValue().booleanValue()) {
                                Util.mc.field_71441_e.func_184134_a(Util.mc.field_71439_g.field_70165_t, Util.mc.field_71439_g.field_70163_u, Util.mc.field_71439_g.field_70161_v, SoundEvents.field_187604_bf, SoundCategory.PLAYERS, 1.0f, -0.7f, false);
                            }
                        } else if (input.equalsIgnoreCase("inventory")) {
                            name = this.inputStream.readUTF();
                            byte[] inventory = IRC.readByteArrayLWithLength(this.inputStream);
                            for (String player : phobosUsers) {
                                if (!player.equalsIgnoreCase(name)) continue;
                                Banzem.inventoryManager.inventories.put(player, IRC.deserializeInventory(inventory));
                            }
                        } else if (input.equalsIgnoreCase("users")) {
                            byte[] inputBytes = IRC.readByteArrayLWithLength(this.inputStream);
                            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inputBytes));
                            List players = (List)stream.readObject();
                            Command.sendMessage("\u00c2\u00a7c[IRC]\u00c2\u00a7r Active Users:");
                            for (String name2 : players) {
                                Command.sendMessage(name2);
                                if (phobosUsers.contains(name2)) continue;
                                phobosUsers.add(name2);
                            }
                        }
                        IRC.INSTANCE.status = !this.socket.isClosed();
                    }
                }
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }
                break;
            }
        }
    }
}

