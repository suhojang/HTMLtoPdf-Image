package com.kwic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Json2Map {
	
	@SuppressWarnings("rawtypes")
	public static Map<String,Object> json2Map(JSONObject json){
		Map<String,Object> map	= new HashMap<String,Object>();
		Iterator keys	= json.keySet().iterator();
		while (keys.hasNext()) {
			String key	= (String) keys.next();
			map.put(key, fromJson(json.get(key)));
		}
		return map;
	}
	
	public static List<Object> json2List(JSONArray array) {
		List<Object> list	= new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			list.add(fromJson(array.get(i)));
		}
		return list;
	}
	
	private static Object fromJson(Object json){
		if (json instanceof JSONObject) {
			return json2Map((JSONObject) json);
		} else if (json instanceof JSONArray) {
			return json2List((JSONArray)json);
		}
		return json;
	}
}
