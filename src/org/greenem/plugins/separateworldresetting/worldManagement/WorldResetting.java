package org.greenem.plugins.separateworldresetting.worldManagement;

import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.log;
import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.debug;
import static org.greenem.plugins.separateworldresetting.data.References.serverDirectory;
import static org.greenem.plugins.separateworldresetting.data.References.runFileString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.separateworldresetting.config.ConfigData;
import org.greenem.plugins.separateworldresetting.config.ConfigOperations;
import org.greenem.plugins.separateworldresetting.data.Lines;
import org.greenem.plugins.separateworldresetting.data.References;
import org.greenem.plugins.separateworldresetting.main.Main;
import org.greenem.plugins.separateworldresetting.util.OsDependent;
import org.greenem.plugins.separateworldresetting.util.OsType;
import org.greenem.plugins.separateworldresetting.util.SimpleLogger;
import org.greenem.plugins.separateworldresetting.util.Stash;

public class WorldResetting {
	public static void resetWorld(CommandSender sender, String[] args) {
		if(sender instanceof ConsoleCommandSender || (sender instanceof Player && ((Player) sender).isOp())) {
			if(!OsDependent.checkSystemSupported(sender, OsType.WINDOWS, OsType.LINUX)) return;
			if(!ConfigData.allowWorldResetting) {
				sender.sendMessage(ChatColor.RED + Lines.featureDisabledByConfig);
				return;
			}
			if(!new File(serverDirectory, runFileString).exists()) {
				sender.sendMessage(ChatColor.RED + "You haven't specified a valid server start script file name in the config! "
						+ "(at server/plugins/configs/WorldResetting/" + Lines.mainConfigName + ")");
				return;
			}
			if(!ConfigOperations.checkJavaPath()) {
				sender.sendMessage(ChatColor.RED + Lines.javaPathNotDetected);
				return;
			}
			
			boolean allow = !ConfigData.requireConfirmationForWorldResetting;
			String usage = ChatColor.BOLD + "/resetworld confirm";
			if(args.length==1 && !allow) {
				if(args[0].equals("confirm")) {
					allow = true;
				} else {
					sender.sendMessage(ChatColor.RED + "To confirm the world resetting, use " + usage);
					// TODO All command usages highlighted like that
				}
			} else if(args.length==0 && !allow) {
				sender.sendMessage(ChatColor.RED + "You need to confirm the world resetting. Use " + usage);
			} else if(args.length>1 && !allow) {
				sender.sendMessage(ChatColor.RED + "You have entered too many arguments. Use " + usage);
			}
			if(!allow) return;
				
			File pluginsDir = new File(Bukkit.getWorldContainer(), "plugins");
			File filesForResetingWorldDirectory = new File(pluginsDir, "FilesForResetingWorld");
			
//			SimpleLogger.debug("Exists: " + filesForResetingWorldDirectory.exists());
			Stash.deleteDirectory(filesForResetingWorldDirectory);
			
//			File prev = new File(filesForResetingWorldDirectory, "ResetWorldAndRestartV1.1.jar");
//			if(prev.exists()) {
//				prev.delete();
//			}
			
			copyResetWorldJarFile();
			
			SimpleLogger.log("Using the separate " + ChatColor.RED + "WorldResetting" + ChatColor.RESET + " plugin to reset the world (" + ChatColor.RED + "Jar version 1.1" + ChatColor.RESET + ")");
			
			String javaPath = ConfigData.javaPathForResettingJar;
			
			if(!javaPath.startsWith("\"")) javaPath = "\"" + javaPath;
			if(!javaPath.endsWith("\"")) javaPath = javaPath + "\"";
			
			AtomicReference<Process> ps = new AtomicReference<>();
			AtomicReference<String> javaPathReference = new AtomicReference<>();
			javaPathReference.set(javaPath);
			Thread jarThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					try {
						//ProcessBuilder pb = new ProcessBuilder("cmd.exe /c \"java -jar ResetWorldAndRestart.jar\"");
						//pb.directory(filesForResetingWorldDirectory);
						//ps.set(pb.start());
						if(References.os==OsType.WINDOWS) {
							String command = "cmd.exe /c \"pushd \"" + filesForResetingWorldDirectory.getCanonicalPath() + "\" && start cmd /c " + javaPathReference.get() + " -jar ResetWorldAndRestartV1.1.jar\"";
							ps.set(Runtime.getRuntime().exec(command));
							SimpleLogger.debug(command);
						} else if (References.os==OsType.LINUX) {
//							ps.set(Runtime.getRuntime().exec("cd \"" + filesForResetingWorldDirectory + "\" && " + javaString + " -jar ResetWorldAndRestart.jar")); // TODO: java string in config (for path)
							String command = "bash -c (" + javaPathReference.get() + " -jar ResetWorldAndRestartV1.1.jar)";
							ps.set(Runtime.getRuntime().exec(command, null, filesForResetingWorldDirectory));
							Runtime.getRuntime().exec("fgh " + ps.get().pid()); // TODO ?
							SimpleLogger.debug(command);
							try {
								ps.get().waitFor();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							SimpleLogger.log(ps.get().exitValue());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			jarThread.start();
			
			try {
				Thread.currentThread().sleep(500); //TODO: GOOD TIMINGS (and above)
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(ConfigData.useHaltForWorldResettings) {
				Runtime.getRuntime().halt(0);
			}
			else {
				Bukkit.shutdown();
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "You don't have enough permissions to do this");
		}
	}
	
	public static void copyResetWorldJarFile() {
		File pluginsDir = new File(serverDirectory, "plugins");
		File filesForResetingWorldDirectory = new File(pluginsDir, "FilesForResetingWorld");
		filesForResetingWorldDirectory.mkdirs();
		
		File f = new File(filesForResetingWorldDirectory, "ResetWorldAndRestartV1.1.jar");
		if (!f.exists()) {
			InputStream is = Main.getInstance().getClass().getResourceAsStream("/extraFiles/ResetWorldAndRestart.jar");
			OutputStream os = null;
			try {
				os = new FileOutputStream(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			byte[] buffer = new byte[4096];
			int length;
			try {
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void deleteWorldResettingFiles() {
		File pluginsDir = new File(Bukkit.getWorldContainer(), "plugins");
		File filesForResetingWorldDirectory = new File(pluginsDir, "FilesForResetingWorld");
		Stash.deleteDirectory(filesForResetingWorldDirectory);
	}
}
