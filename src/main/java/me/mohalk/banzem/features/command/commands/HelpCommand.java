/*
 * Decompiled with CFR 0.151.
 */
package me.mohalk.banzem.features.command.commands;

import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.features.command.Command;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("commands");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("You can use following commands: ");
        for (Command command : Banzem.commandManager.getCommands()) {
            HelpCommand.sendMessage(Banzem.commandManager.getPrefix() + command.getName());
        }
    }
}

