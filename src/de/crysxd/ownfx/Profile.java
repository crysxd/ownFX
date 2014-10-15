package de.crysxd.ownfx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	public byte[] serializeForC() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		bos.write(SerialSupport.toLittleEndianBytes(this.getId(), 8));
		bos.write(SerialSupport.toLittleEndianBytes(this.getFrames().size(), 1));
		
		for(Frame f : this.frames)
			f.serializeForC(bos);
		
		return bos.toByteArray();
		
	}
	
	public static Profile readProfile(String json) {
		return GsonSupport.<Profile>parse(json, Profile.class);
		
	}
	
	public static Profile readProfile(InputStream jsonStream) throws IOException {
		return GsonSupport.<Profile>parse(jsonStream, Profile.class);
		
	}
	
	
	public static Profile readProfile(File jsonFile) throws IOException {
		return Profile.readProfile(new FileInputStream(jsonFile));
		
	}
}
