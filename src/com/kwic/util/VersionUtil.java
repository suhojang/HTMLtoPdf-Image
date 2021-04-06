package com.kwic.util;

public class VersionUtil {
	private static final long RENDERING_JDK_VERSION	= 18;
	
	public static final String RENDERING_NAME		= "sun.java2d.cmm";
	public static final String RENDERING_VALUE		= "sun.java2d.cmm.kcms.KcmsServiceProvider";
	
	public static boolean isReneringVersion(){
		long JDK_VERSION	= Long.parseLong(System.getProperty("java.version").replace(".", "").substring(0,2));
		if (JDK_VERSION >= RENDERING_JDK_VERSION)
			return true;
		
		return false;
	}
}
