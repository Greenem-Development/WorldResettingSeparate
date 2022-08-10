package org.greenem.plugins.separateworldresetting.util;

import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.log;
import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Stash {
	public static String manhuntPluginName = "AdvancedManhunt";
	
	public static boolean isManhuntPluginEnabled() {
		Plugin pl = Bukkit.getPluginManager().getPlugin(manhuntPluginName);
		return pl.isEnabled(); // ?
	}
	
	public static void AddItemStack(Player p, ItemStack ItemStack, int slot) { // can be better, change this later
		if(!p.getInventory().contains(ItemStack)) {
			if(p.getInventory().getItem(slot) == null) p.getInventory().setItem(slot, ItemStack);
			else p.getInventory().addItem(ItemStack);
		}
	}

	public static void RemoveMaterial(Player p, Material material) {
		if(p.getInventory().contains(material)) {
			p.getInventory().remove(material);
		}
	}
	
	public static void RemoveMaterial(Player p, int slot) {
		//p.getInventory().remove(p.getInventory().getItem(slot));
		p.getInventory().setItem(slot, new ItemStack(Material.AIR));
	}
	
	public static boolean isSenderAPlayer(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED + "You can't do it via console!");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public static boolean isSenderAPlayerOrConsole(CommandSender sender) {
		if(!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
}
