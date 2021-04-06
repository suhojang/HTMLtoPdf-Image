package com.kwic.util;

import java.io.InputStream;

public final class ResourceUtil {
	public static InputStream getResourceStream(String key){
		return getResourceStream(key, null);
	}
	
	public static InputStream getResourceStream(String key, ClassLoader loader){
		if (key.startsWith("/")) {
			key	= key.substring(1);
		}
		
		InputStream stream	= null;
		if (loader != null) {
			stream	= loader.getResourceAsStream(key);
			if (stream != null) {
				return stream;
			}
		}
		
		try {
			ClassLoader contextClassLoader	= Thread.currentThread().getContextClassLoader();
			if (contextClassLoader != null) {
				stream	= contextClassLoader.getResourceAsStream(key);
			}
		} catch (Throwable e) {
		}
		
		if (stream == null) {
			stream	= ResourceUtil.class.getResourceAsStream("/" + key);
		}
		if (stream == null) {
			stream	= ClassLoader.getSystemResourceAsStream(key);
		}
		return stream;
	}
}
