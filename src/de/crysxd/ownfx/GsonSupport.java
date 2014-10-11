package de.crysxd.ownfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSupport {
	
	public static Gson createGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ColorStop.class, new ColorStop.JsonDeserializer());
		gsonBuilder.registerTypeAdapter(ColorStop.class, new ColorStop.JsonSerializer());
		
		return gsonBuilder.create();
		
	}
	
	public static <T> T parse(String json, Class<T> outputClass) {
		return GsonSupport.createGson().fromJson(json, outputClass);
		
	}
	
	public static <T> T parse(InputStream jsonStream, Class<T> outputClass) throws IOException {
		return GsonSupport.<T>parse(GsonSupport.readStream(jsonStream), outputClass);
		
	}
	
	private static String readStream(InputStream in) throws IOException {
		BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
		StringBuilder readContent = new StringBuilder();
		String line;
		
		try {
			while((line = inReader.readLine()) != null) {
				readContent.append(line);
				readContent.append("\n");
				
			}	
		} finally {
			inReader.close();
	
		}
		
		return readContent.toString();
		
	}
}
