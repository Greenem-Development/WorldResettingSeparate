package org.greenem.plugins.separateworldresetting.commands;

import org.bukkit.command.CommandSender;
import org.greenem.plugins.separateworldresetting.config.ConfigOperations;

import net.md_5.bungee.api.ChatColor;

public class MainCommands {
	public static void reloadConfigCommand(CommandSender sender) {
		ConfigOperations.updateAndLoadConfig(); // maybe just load
		// and maybe check for user-added fields and delete them or something like that (maybe change something else too)
		sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config");
	}
}
