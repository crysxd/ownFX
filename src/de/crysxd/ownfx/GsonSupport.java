package de.crysxd.ownfx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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
	
	public static String stringify(Object o) {
		return GsonSupport.createGson().toJson(o);
		
	}
	
	public static void stringify(Object o, OutputStream out) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		bw.write(GsonSupport.stringify(o));
		bw.close();
		
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
