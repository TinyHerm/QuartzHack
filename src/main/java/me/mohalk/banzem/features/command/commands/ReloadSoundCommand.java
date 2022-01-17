/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.SoundHandler
 *  net.minecraft.client.audio.SoundManager
 *  net.minecraftforge.fml.common.ObfuscationReflectionHelper
 */
package me.mohalk.banzem.features.command.commands;

import me.mohalk.banzem.features.command.Command;
import me.mohalk.banzem.util.Util;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand
extends Command {
    public ReloadSoundCommand() {
        super("sound", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        try {
            SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, (Object)Util.mc.func_147118_V(), (String[])new String[]{"sndManager", "field_147694_f"});
            sndManager.func_148596_a();
            ReloadSoundCommand.sendMessage("\u00a7aReloaded Sound System.");
        }
        catch (Exception e) {
            System.out.println("Could not restart sound manager: " + e.toString());
            e.printStackTrace();
            ReloadSoundCommand.sendMessage("\u00a7cCouldnt Reload Sound System!");
        }
    }
}

