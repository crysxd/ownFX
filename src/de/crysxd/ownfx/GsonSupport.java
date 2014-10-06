package de.crysxd.ownfx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSupport {
	
	public static Gson createGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ColorStop.class, new ColorStop.JsonDeserializer());
		gsonBuilder.registerTypeAdapter(ColorStop.class, new ColorStop.JsonSerializer());
		
		return gsonBuilder.create();
		
	}
}
