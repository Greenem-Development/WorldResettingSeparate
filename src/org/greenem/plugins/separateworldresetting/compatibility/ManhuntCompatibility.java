package org.greenem.plugins.separateworldresetting.compatibility;

import org.greenem.plugins.separateworldresetting.util.SimpleLogger;

public class ManhuntCompatibility {
	private static boolean checkManhuntAvaliable() {
		if(isClassLoaded("org.greenem.plugins.manhunt.worldManagement", "WorldResetting")) return true;
		return false;
	}
	
	public static boolean isClassLoaded(String packageName, String className) {
		try {
            Class<?> clazz = Class.forName(packageName + "." + className);
            SimpleLogger.debug(clazz);
            if(clazz!=null) return true;
//            if (!isInstanceMethod) {
//                Method method = clazz.getMethod(staticMethodName, formalParameters);
//                method.invoke(null, effectiveParameters);
//            }
//
//            else {
//                Method method = clazz.getMethod(instanceMethodName, formalParameters);
//                Object newInstance = clazz.newInstance();
//                method.invoke(newInstance, effectiveParameters);
//
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
	}
	
	public static void initComoatibility() {
		if(checkManhuntAvaliable()) {
			SimpleLogger.debug("Detected manhunt");
		}
	}
}
