package org.greenem.plugins.separateworldresetting.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.greenem.plugins.separateworldresetting.config.ConfigData;
import org.greenem.plugins.separateworldresetting.util.SimpleLogger;
import org.greenem.plugins.separateworldresetting.util.Stash;
import org.greenem.plugins.separateworldresetting.worldManagement.WorldResetting;

public class MainEvents implements Listener {
	@EventHandler
	public void onNonPlayerCommand(ServerCommandEvent e) { // Command blocks here?
		separateMessage(e.getCommand(), e, e.getSender());
	}
	
	private void separateMessage(String wholeCommand, Cancellable e, CommandSender sender) {
		if(wholeCommand.length()==0) return;
		String cmd = wholeCommand.split(" ")[0];
		String[] args = new String[0];
//		SimpleLogger.debug("|" + cmd + "|" + wholeCommand + "|");s
		if(cmd.length()+1<wholeCommand.length()) {
			args = wholeCommand.substring(cmd.length()+1).split(" ");
		}
//		SimpleLogger.debug(cmd);
//		for(int i = 0; i < args.length; i++) { SimpleLogger.debug(args[i]); }
		processTheCommand(cmd, args, e, sender);
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		separateMessage(e.getMessage().substring(1), e, e.getPlayer());
	}
	
	private void processTheCommand(String cmd, String[] args, Cancellable e, CommandSender sender) {
		if(cmd.equalsIgnoreCase("resetworld")) { // Ignore case?
			if(e.isCancelled()) return; // to make other plugins (including my) be able to control this one a bit
			if(!Stash.isSenderAPlayerOrConsole(sender)) return;
			boolean mhIsOn = Stash.isManhuntPluginEnabled();
			if(!mhIsOn || (mhIsOn && ConfigData.priorityAboveManhuntWorldResetting)) {
				e.setCancelled(true);
//				if (!sender.hasPermission("worldresettingseparate.resetworld")) {
//					sender.sendMessage("No permissions");
//	            }
//	            else{
	            WorldResetting.resetWorld(sender, args);
//	            }
			}
		}
	}
}
