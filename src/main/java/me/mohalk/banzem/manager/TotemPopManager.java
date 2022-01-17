/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.mohalk.banzem.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.features.Feature;
import me.mohalk.banzem.features.command.Command;
import me.mohalk.banzem.features.modules.client.Notifications;
import me.mohalk.banzem.features.modules.module.ModuleTools;
import net.minecraft.entity.player.EntityPlayer;

public class TotemPopManager
extends Feature {
    private Notifications notifications;
    private Map<EntityPlayer, Integer> poplist = new ConcurrentHashMap<EntityPlayer, Integer>();
    private final Set<EntityPlayer> toAnnounce = new HashSet<EntityPlayer>();

    public void onUpdate() {
        if (this.notifications.totemAnnounce.passedMs(this.notifications.delay.getValue().intValue()) && this.notifications.isOn() && this.notifications.totemPops.getValue().booleanValue()) {
            for (EntityPlayer player : this.toAnnounce) {
                if (player == null) continue;
                int playerNumber = 0;
                for (char character : player.func_70005_c_().toCharArray()) {
                    playerNumber += character;
                    playerNumber *= 10;
                }
                Command.sendOverwriteMessage(this.pop(player), playerNumber, this.notifications.totemNoti.getValue());
                this.toAnnounce.remove(player);
                this.notifications.totemAnnounce.reset();
                break;
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public String pop(EntityPlayer player) {
        if (this.getTotemPops(player) == 1) {
            if (!ModuleTools.getInstance().isEnabled()) return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totem.";
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.GRAY + " totem.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + this.getTotemPops(player) + ChatFormatting.RED + " totem.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " has popped " + ChatFormatting.RED + this.getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " time in total!";
                }
                case NONE: {
                    return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totem.";
                }
            }
            return "";
        } else {
            if (!ModuleTools.getInstance().isEnabled()) return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totems.";
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.GRAY + " totems.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + this.getTotemPops(player) + ChatFormatting.RED + " totems.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " has popped " + ChatFormatting.RED + this.getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " times in total!";
                }
                case NONE: {
                    return ChatFormatting.WHITE + player.func_70005_c_() + " popped " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totems.";
                }
            }
        }
        return "";
    }

    public void onLogout() {
        this.onOwnLogout(this.notifications.clearOnLogout.getValue());
    }

    public void init() {
        this.notifications = Banzem.moduleManager.getModuleByClass(Notifications.class);
    }

    public void onTotemPop(EntityPlayer player) {
        this.popTotem(player);
        if (!player.equals((Object)TotemPopManager.mc.field_71439_g)) {
            this.toAnnounce.add(player);
            this.notifications.totemAnnounce.reset();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public String death1(EntityPlayer player) {
        if (this.getTotemPops(player) == 1) {
            if (!ModuleTools.getInstance().isEnabled()) return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totem!";
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.GRAY + " totem.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + this.getTotemPops(player) + ChatFormatting.RED + " totem.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " time!";
                }
                case NONE: {
                    return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totem!";
                }
            }
            return null;
        } else {
            if (!ModuleTools.getInstance().isEnabled()) return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totems!";
            switch (ModuleTools.getInstance().popNotifier.getValue()) {
                case FUTURE: {
                    return ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.func_70005_c_() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.GRAY + " totems.";
                }
                case PHOBOS: {
                    return ChatFormatting.GOLD + player.func_70005_c_() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + this.getTotemPops(player) + ChatFormatting.RED + " totems.";
                }
                case DOTGOD: {
                    return ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "QuartzHack.cc" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.LIGHT_PURPLE + " times!";
                }
                case NONE: {
                    return Banzem.commandManager.getClientMessage() + ChatFormatting.WHITE + player.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getTotemPops(player) + ChatFormatting.WHITE + " Totems!";
                }
            }
        }
        return null;
    }

    public void onDeath(EntityPlayer player) {
        if (this.getTotemPops(player) != 0 && !player.equals((Object)TotemPopManager.mc.field_71439_g) && this.notifications.isOn() && this.notifications.totemPops.getValue().booleanValue()) {
            int playerNumber = 0;
            for (char character : player.func_70005_c_().toCharArray()) {
                playerNumber += character;
                playerNumber *= 10;
            }
            Command.sendOverwriteMessage(this.death1(player), playerNumber, this.notifications.totemNoti.getValue());
            this.toAnnounce.remove(player);
        }
        this.resetPops(player);
    }

    public void onLogout(EntityPlayer player, boolean clearOnLogout) {
        if (clearOnLogout) {
            this.resetPops(player);
        }
    }

    public void onOwnLogout(boolean clearOnLogout) {
        if (clearOnLogout) {
            this.clearList();
        }
    }

    public void clearList() {
        this.poplist = new ConcurrentHashMap<EntityPlayer, Integer>();
    }

    public void resetPops(EntityPlayer player) {
        this.setTotemPops(player, 0);
    }

    public void popTotem(EntityPlayer player) {
        this.poplist.merge(player, 1, Integer::sum);
    }

    public void setTotemPops(EntityPlayer player, int amount) {
        this.poplist.put(player, amount);
    }

    public int getTotemPops(EntityPlayer player) {
        Integer pops = this.poplist.get(player);
        if (pops == null) {
            return 0;
        }
        return pops;
    }

    public String getTotemPopString(EntityPlayer player) {
        return "\u00a7f" + (this.getTotemPops(player) <= 0 ? "" : "-" + this.getTotemPops(player) + " ");
    }
}

