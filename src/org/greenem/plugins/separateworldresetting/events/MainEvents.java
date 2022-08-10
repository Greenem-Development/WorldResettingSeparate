package org.greenem.plugins.separateworldresetting.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.greenem.plugins.separateworldresetting.config.ConfigData;
import org.greenem.plugins.separateworldresetting.util.SimpleLogger;
import org.greenem.plugins.separateworldresetting.util.Stash;
import org.greenem.plugins.separateworldresetting.worldManagement.WorldResetting;

public class MainEvents implements Listener {
	@EventHandler
	public void onNonPlayerCommand(ServerCommandEvent e) { // Command blocks here?
		String wholeCommand = e.getCommand();
		String cmd = wholeCommand.split(" ")[0];
		String[] args = new String[0];
//		SimpleLogger.debug("|" + cmd + "|" + wholeCommand + "|");
		if(cmd.length()+1<wholeCommand.length()) {
			args = wholeCommand.substring(cmd.length()+1).split(" ");
		}
//		SimpleLogger.debug(cmd);
//		for(int i = 0; i < args.length; i++) { SimpleLogger.debug(args[i]); }
		processTheCommand(cmd, args, e, e.getSender());
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandSendEvent e) {
		SimpleLogger.log("The developer has forgot to do this code");
		SimpleLogger.debug(e.getCommands());
	}
	
	private void processTheCommand(String cmd, String[] args, Cancellable e, CommandSender sender) {
		if(cmd.equalsIgnoreCase("resetworld")) { // Ignore case?
			if(!Stash.isSenderAPlayerOrConsole(sender)) return;
			boolean mhIsOn = Stash.isManhuntPluginEnabled();
			if(!mhIsOn || (mhIsOn && ConfigData.priorityAboveManhuntWorldResetting)) {
				e.setCancelled(true);
				WorldResetting.resetWorld(sender, args);
			}
		}
	}
}
