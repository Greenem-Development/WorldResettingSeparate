package org.greenem.plugins.separateworldresetting.config;

import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.log;
import static org.greenem.plugins.separateworldresetting.util.SimpleLogger.debug;
import static org.greenem.plugins.separateworldresetting.data.References.serverDirectory;
import static org.greenem.plugins.separateworldresetting.data.References.pluginConfigDirectory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.greenem.plugins.separateworldresetting.data.Lines;
import org.greenem.plugins.separateworldresetting.data.References;
import org.greenem.plugins.separateworldresetting.util.SimpleLogger;
import org.greenem.plugins.separateworldresetting.versionCompatibility.ConfigCompatibility;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

public class ConfigOperations {
	public static void updateAndLoadConfig() {
		if(!checkValidStructure(getConfigFile())) return;
		ConfigCompatibility.updateConfig();
		loadConfig();
	}
	
	public static boolean checkValidStructure(File f) {
		boolean result = true;
		try {
			new Yaml().load(new FileInputStream(f));
		} catch (ScannerException e) {
			SimpleLogger.log(ChatColor.RED + "Cannot load the config, structure/syntax error:\n" + e.getMessage());
			result = false;
		} catch (Exception e) {
//			SimpleLogger.log(e.getClass().getCanonicalName());
//			e.printStackTrace();
			SimpleLogger.log(ChatColor.RED + "Cannot load the config:\n" + e.getMessage());
			result = false;
		}
		return result;
	}
	
	public static File getConfigFile() {
		return new File(References.pluginConfigDirectory, Lines.mainConfigName);
	}
	
	public static void loadConfig() {
		File f = getConfigFile();
		if(!ConfigOperations.checkValidStructure(f)) return;
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		String currentKey = null;
		try {
			currentKey = "settings.warnings.disable-no-existing-start-file-warning";
			ConfigData.disableNoExistingStartFileWarning = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.launch-server-script-name";
			References.runFileString = (String) loadKey(c, currentKey, null, null); //() -> { checkStartScript(); }, () -> { checkStartScript(); });
			currentKey = "settings.world-resetting.allow-ops-use-world-resetting";
			ConfigData.allowWorldResetting = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.require-confirmation-for-world-resetting"; // TODO
			ConfigData.requireConfirmationForWorldResetting = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.use-halt-for-instant-server-stopping";
			ConfigData.useHaltForWorldResettings = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.priority.above-mahnunt-plugin-command";
			ConfigData.priorityAboveManhuntWorldResetting = (boolean) loadKey(c, currentKey, null, null);
			//"java-path-to-start-java-middle-script" // maybe later
		} catch (Throwable t) {
			log("Error while reading \"" + currentKey + "\" from the config");
		}
		saveConfigIfHasChanges(c, cOld, f);
		deleteUnnesessaryFields(c, f);
	}
	
	public static List<String> allConfigValuesNames = Arrays.asList(
			"settings",
			"settings.world-resetting",
			"settings.warnings",
			"settings.world-resetting.priority",
			
			"settings.world-resetting.launch-server-script-name",
			"settings.world-resetting.require-confirmation-for-world-resetting",
			"settings.world-resetting.use-halt-for-instant-server-stopping",
			"settings.warnings.disable-no-existing-start-file-warning",
			"settings.world-resetting.allow-ops-use-world-resetting",
			"settings.world-resetting.priority.above-mahnunt-plugin-command");
	
	public static Object getDefaultValue(String key) {
		switch (key) {
			case "settings.world-resetting.launch-server-script-name": return "Run.bat";
			case "settings.commands.allow-gm-command-for-ops": return true;
			case "settings.commands.allow-heal-command-for-ops": return true;
			case "settings.world-resetting.require-confirmation-for-world-resetting": return false;
			case "settings.world-resetting.use-halt-for-instant-server-stopping": return false;
			case "settings.game.delay-for-speedrunner-on-start-in-milliseconds": return 2000;
			case "settings.warnings.disable-no-existing-start-file-warning": return false;
			case "settings.game.clear-items-on-start": return true;
			case "settings.world-resetting.allow-ops-use-world-resetting": return true;
			case "settings.world-resetting.priority.above-mahnunt-plugin-command": return false;
			default:
				return "UNDEFINED_CONFIG_VALUE";
		}
	}
	
	private static Object loadKey(YamlConfiguration c, String currentKey, Runnable ifExists, Runnable ifNotExists) {
		Object currentValue = c.get(currentKey);
		Object def = null;
		Object returnValue = null;
		if (currentValue == null || currentValue.equals("UNDEFINED_CONFIG_VALUE")) {
			def = getDefaultValue(currentKey);
			c.set(currentKey, def);
			returnValue = def;
			if(ifExists!=null) {
				ifExists.run();
			}
		} else {
			try {
				returnValue = currentValue;
				if(ifNotExists!=null) {
					ifNotExists.run();
				}
			} catch (ClassCastException e) {
				logCastEx(currentKey);
			}
		}
		if(currentKey=="settings.world-resetting.launch-server-script-name") {
			checkStartScript((String) returnValue);
		}
		return returnValue;
	}
	
	/***
	 * Returns true if found changes
	 * **/
	public static boolean saveConfigIfHasChanges(YamlConfiguration newConfig, YamlConfiguration oldConfig, File f) {
		boolean hasChanges = false;
	    for(String key : newConfig.getKeys(true)) { // missing keys added by user
	    	if(!(oldConfig.get(key) instanceof MemorySection) || !(newConfig.get(key) instanceof MemorySection)) {
		        if(!oldConfig.contains(key)) {
		        	hasChanges = true;
		        	break;
		        } else {
		        	if(!newConfig.get(key).equals(oldConfig.get(key))) {
		        		hasChanges = true;
//		        		try {
//		        			SimpleLogger.debug(oldConfig.get(key) instanceof MemorySection);
//		        			SimpleLogger.debug("break 2 " + ((MemorySection) oldConfig.get(key)).getKeys(true) + " | " + ((MemorySection) newConfig.get(key)).getKeys(true));
//		        		} catch(ClassCastException e) {}
			        	break;
		        	}
		        }
	        }
	    }
	    for(String key : oldConfig.getKeys(true)) {
	    	if(!(oldConfig.get(key) instanceof MemorySection) || !(newConfig.get(key) instanceof MemorySection)) {
		        if(!newConfig.contains(key)) {
		        	hasChanges = true;
		        	break;
		        } else {
		        	if(!newConfig.get(key).equals(oldConfig.get(key))) {
		        		hasChanges = true;
			        	break;
		        	}
		        }
	    	}
	    }
		if (hasChanges) {
			try {
				newConfig.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hasChanges;
	}
	
	public static void deleteUnnesessaryFields(YamlConfiguration c, File f) { // think about old versions
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		for(String key : c.getKeys(true)) {
//			if(!(c.get(key) instanceof MemorySection)) {
			if(!allConfigValuesNames.contains(key)) {
				c.set(key, null);
			}
//			}
		}
		saveConfigIfHasChanges(c, cOld, f);
	}
	
	private static void checkStartScript(String runFileString) {
		if(ConfigData.disableNoExistingStartFileWarning) return;
		if(!new File(serverDirectory, runFileString).exists()) {
			if(!References.alreadySentBrokenRunFileMessage) {
				String noExistingStartFileWarning = ChatColor.RED + "You haven't specified your run script file name in the config! "
						+ "(for world reseting, but other things are fine)";
				log(noExistingStartFileWarning);
				References.alreadySentBrokenRunFileMessage = true;
			}
		} else {
			References.alreadySentBrokenRunFileMessage = false;
		}
	}
	
	public static void logCastEx(String currentKey) {
		log("Wrong value type for \"" + currentKey + "\" in the config");
	}
}
