package org.greenem.plugins.separateworldresetting.main;

import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.log;
import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.debug;
import static org.greenem.plugins.separateworldresetting.data.References.serverDirectory;
import static org.greenem.plugins.separateworldresetting.data.References.configsDirectory;
import static org.greenem.plugins.separateworldresetting.data.References.pluginConfigDirectory;

import static org.greenem.plugins.separateworldresetting.util.Stash.isSenderAPlayer;
import static org.greenem.plugins.separateworldresetting.util.Stash.isSenderAPlayerOrConsole;

import java.io.File;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.greenem.plugins.separateworldresetting.commands.MainCommands;
import org.greenem.plugins.separateworldresetting.compatibility.ManhuntCompatibility;
import org.greenem.plugins.separateworldresetting.completers.plain.EmptyCompleterNoArgs;
import org.greenem.plugins.separateworldresetting.config.ConfigData;
import org.greenem.plugins.separateworldresetting.config.ConfigOperations;
import org.greenem.plugins.separateworldresetting.config.ConfigWatcher;
import org.greenem.plugins.separateworldresetting.data.References;
import org.greenem.plugins.separateworldresetting.events.MainEvents;
import org.greenem.plugins.separateworldresetting.util.DebugModeSwitch;
import org.greenem.plugins.separateworldresetting.util.OsDependent;
import org.greenem.plugins.separateworldresetting.util.SimpleLogger;
import org.greenem.plugins.separateworldresetting.util.Stash;
import org.greenem.plugins.separateworldresetting.worldManagement.WorldResetting;
//import org.spigotmc.event.entity.EntityDismountEvent;

public class Main extends JavaPlugin implements Listener {
	public Plugin plugin = this;
	public String pluginName = "World Resetting";
	public String pluginNameColor = ChatColor.BOLD + "" + ChatColor.GREEN + "";

	private static Main instance;
	public static Main getInstance() { // not synchronized, but that's ok
		return instance;
	}
	
	@Override
	public void onEnable() {
		initAll();
		ConfigOperations.updateAndLoadConfig();
//		ManhuntCompatibility.initComoatibility();
		finishInit();
		instance = this;
		log(pluginNameColor + pluginName + ChatColor.RESET + " plugin has been enabled!");
	}
	
	@Override
	public void onDisable() {
		References.configWatcher.stop();
		log(pluginName + ChatColor.RESET + " plugin has been disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
//		if (cmd.getName().equalsIgnoreCase("wrreloadconfig")) {
//			if(!isSenderAPlayerOrConsole(sender)) return true;
//			MainCommands.reloadConfigCommand(sender);
//		}
		return true;
	}
    
	private void initAll() {
		initFiles();
		DebugModeSwitch.init();
		References.os = OsDependent.getSystemType();
		registerAllCommands();
		registerEvents();
		startConfigWatcher();
	}
	
	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new MainEvents(), plugin);
	}
	
	private void registerAllCommands() { // can forget to change command name everywhere and get a bug
		getCommand("resetworld").setTabCompleter(new EmptyCompleterNoArgs());
	}
	
	private void initFiles() {
		serverDirectory = Bukkit.getWorldContainer();
		configsDirectory = new File(serverDirectory, "plugins/configs");
		pluginConfigDirectory = new File(configsDirectory, "WorldResetting");
		pluginConfigDirectory.mkdirs();
		WorldResetting.deleteWorldResettingFiles();
	}
	
	private void finishInit() {
		if(SimpleLogger.theDebugOutputEnabled) {
			log(ChatColor.AQUA + "Debug mode is enabled");
		}
	}
	
	private void startConfigWatcher() {
		References.configWatcher = new ConfigWatcher();
		References.configWatcher.start();
	}
}