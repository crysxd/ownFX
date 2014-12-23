package de.crysxd.ownfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ColorPickerView extends JPanel implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 4272658906584541547L;
	
	private final BufferedImage COLOR_PICKER_IMAGE;
	private final ColorPickerListener LISTENER;
	private final int PADDING = 10;
	
	private Point selectorLocation = new Point(0,  0);
	private Color currentColor = null;
	
	public ColorPickerView(Color initColor, ColorPickerListener listener) throws Exception {
		this.LISTENER = listener;
		
		Image colorPicker = R.getImage("color_picker.png");
		this.COLOR_PICKER_IMAGE = new BufferedImage(colorPicker.getWidth(null),colorPicker.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D bGr = this.COLOR_PICKER_IMAGE.createGraphics();
	    bGr.drawImage(colorPicker, 0, 0, null);
	    bGr.dispose();
		
		this.setPreferredSize(new Dimension(this.COLOR_PICKER_IMAGE.getWidth() + this.PADDING, this.COLOR_PICKER_IMAGE.getHeight() + this.PADDING));
	
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.setColor(initColor);
		
	}
	
	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Draw color picker image
		g.drawImage(this.COLOR_PICKER_IMAGE, this.PADDING, this.PADDING, null);
			
		//Draw selector
		g.setColor(Color.WHITE);
		g.fillRect((int) this.selectorLocation.getX() - 8, (int) this.selectorLocation.getY() - 8, 16, 16);
		g.setColor(Color.DARK_GRAY);
		g.drawRect((int) this.selectorLocation.getX() - 8, (int) this.selectorLocation.getY() - 8, 15, 15);
		g.setColor(this.currentColor);
		g.fillRect((int) this.selectorLocation.getX() - 5, (int) this.selectorLocation.getY() - 5, 10, 10);
	}
	
	private Color getColorAt(int x, int y) {
		return new Color(this.COLOR_PICKER_IMAGE.getRGB(x, y));
		
	}
	
	private Point getPointFor(Color c) {	
		for(int x=0; x<this.COLOR_PICKER_IMAGE.getWidth(); x++) {
			for(int y=0; y<this.COLOR_PICKER_IMAGE.getHeight(); y++) {
				if(new Color(this.COLOR_PICKER_IMAGE.getRGB(x, y)).equals(c)) {
					return new Point(x + this.PADDING, y + this.PADDING);
					
				}
			}
		}
		
		return new Point(this.PADDING, this.PADDING);
		
	}
	
	private void updateColor(Point p) {
		if(p.getX() <= this.PADDING)
			p.setLocation(this.PADDING, p.getY());
		
		if(p.getY() <= this.PADDING)
			p.setLocation(p.getX(), this.PADDING);
		
		if(p.getX() > this.PADDING + this.COLOR_PICKER_IMAGE.getWidth(null) - 1)
			p.setLocation(this.PADDING + this.COLOR_PICKER_IMAGE.getWidth(null) - 1, p.getY());
		
		if(p.getY() > this.PADDING + this.COLOR_PICKER_IMAGE.getHeight(null) - 1)
			p.setLocation(p.getX(), this.PADDING + this.COLOR_PICKER_IMAGE.getHeight(null) - 1);
		
		this.selectorLocation = p;
		
		this.currentColor = this.getColorAt((int) this.selectorLocation.getX() - this.PADDING, (int) this.selectorLocation.getY() - this.PADDING);
	
		this.repaint();
	}
	
	public Color getColor() {
		return currentColor;
		
	}
	
	public void setColor(Color c) throws Exception {
		this.updateColor(this.getPointFor(c));
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.updateColor(e.getPoint());
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.updateColor(e.getPoint());


	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.LISTENER.colorPicked(this.currentColor);

	}
}
