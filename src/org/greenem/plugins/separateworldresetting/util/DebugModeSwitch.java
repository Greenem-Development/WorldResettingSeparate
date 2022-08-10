package org.greenem.plugins.separateworldresetting.util;

import java.io.File;

import org.greenem.plugins.separateworldresetting.data.References;

public class DebugModeSwitch {
	public static void init() {
		File f = new File(References.pluginConfigDirectory, "enableDebug.dev");
		if(!f.exists()) {
			SimpleLogger.theDebugOutputEnabled = false;
			References.logCommandInfo = false;
		}
	}
}
