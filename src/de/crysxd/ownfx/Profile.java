package de.crysxd.ownfx;

import java.util.List;
import java.util.Vector;

public class Profile {
	
	private String name;
	private int id;
	private List<Frame> frames = new Vector<>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public List<Frame> getFrames() {
		return frames;
	}
}
