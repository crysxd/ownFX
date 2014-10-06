package de.crysxd.ownfx;

import java.util.List;
import java.util.Vector;

public class Frame {
	
	private int pauseTime;
	private int transitionTime;
	private List<ColorStop> colorStops = new Vector<>();
	
	public int getPauseTime() {
		return pauseTime;
	}
	
	public void setPauseTime(int pauseTime) {
		this.pauseTime = pauseTime;
	}
	
	public int getTransitionTime() {
		return transitionTime;
	}
	
	public void setTransitionTime(int transitionTime) {
		this.transitionTime = transitionTime;
	}
	
	public List<ColorStop> getColorStops() {
		return colorStops;
	}
}
