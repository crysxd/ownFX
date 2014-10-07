package de.crysxd.ownfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

public class Profile {
	
	private String name;
	private long id;
	private List<Frame> frames = new Vector<>();
	
	public Profile() {
		
	}

	public String getName() {
		return name;
		
	}
	
	public void setName(String name) {
		this.name = name;
		
	}
	
	public long getId() {
		return id;
		
	}
	
	public void setId(long id) {
		this.id = id;
		
	}
	
	public List<Frame> getFrames() {
		return frames;
		
	}
	
	public static Profile readProfile(String json) {
		return GsonSupport.createGson().fromJson(json, Profile.class);
		
	}
	
	public static Profile readProfile(InputStream jsonStream) throws IOException {
		return Profile.readProfile(Profile.readStream(jsonStream));
		
	}
	
	
	public static Profile readProfile(File jsonFile) throws IOException {
		return Profile.readProfile(new FileInputStream(jsonFile));
		
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
