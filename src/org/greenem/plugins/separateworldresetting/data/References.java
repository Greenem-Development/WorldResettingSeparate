package org.greenem.plugins.separateworldresetting.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.greenem.plugins.separateworldresetting.config.ConfigWatcher;
import org.greenem.plugins.separateworldresetting.util.OsType;

public class References {
	public static File serverDirectory;
	public static File configsDirectory;
	public static File pluginConfigDirectory;
	
	public static String runFileString = "asdgsdfgsd";
//	public static String javaString = "java"; //"\"C:\\Program Files\\Java\\jdk-17.0.1\\bin\\java\"";
	public static OsType os;
	public static boolean logCommandInfo = true;
	public static ConfigWatcher configWatcher;
	
	public static boolean alreadySentBrokenRunFileMessage = false;
}
