package de.crysxd.ownfx;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class R {
	
	public static Image getImage(String name) {
		try {
			return ImageIO.read(R.class.getResourceAsStream(name));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image getImage(String name, int width, int height) {
		Image img = R.getImage(name);
		
		if(img == null)
			return null;
		
		return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		
	}

	public static Icon getIcon(String name, Dimension iconSize) {
		Image img = R.getImage(name);
		
		if(img == null)
			return null;
		
		return new ImageIcon(img);
	}
	
	public static Icon getIcon(String name, int width, int height) {
		Image img = R.getImage(name, width, height);
		
		if(img == null)
			return null;
		
		return new ImageIcon(img);
	}
}
