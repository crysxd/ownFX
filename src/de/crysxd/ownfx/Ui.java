package de.crysxd.ownfx;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Ui implements ActionListener, ItemListener, ColorPickerListener{

	/*
	 * -----------------------------------------------------------------------------------
	 * static
	 */
	
	private final static Image TRAY_ICON;
	
	static {
		SystemTray tray = SystemTray.getSystemTray();
		Dimension iconSize = tray.getTrayIconSize();
		TRAY_ICON = R.getImage("favicon.png", iconSize.width, iconSize.height);

	}
	
	public static void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(null,
			    message,
			    "Internal Error",
			    JOptionPane.ERROR_MESSAGE);
		
	}
	
	public static String queryComPort() {
		Object[] possibilities = SerialSupport.getSerialInterfaceNames().toArray();
		String s = (String)JOptionPane.showInputDialog(
		                    null,
		                    "Please choose the COM port on which the Arduino is connected:",
		                    "COM selection",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    possibilities,
		                 	null);

		//If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
		   
		    return s;
		}
		
		return queryComPort();
		
	}
	
	/*
	 * -----------------------------------------------------------------------------------
	 * Non-static
	 */
	
	private final Main MY_MAIN;
	private Color currentColor;
	private final Map<Integer, CheckboxMenuItem> MENU_BRIGHTNESS_DATA = new HashMap<>();
	private final JDialog COLOR_PICKER = new JDialog();
	
	private final MenuItem MENU_EXIT;
	private final MenuItem MENU_ABOUT;
	private final MenuItem MENU_COLOR;
	private final Menu MENU_BRIGHTNESS;
	
	public Ui(Main m, Color currentColor) throws Exception {
		this.MY_MAIN = m;
		this.currentColor = currentColor;
		
		//Create menu items
		this.MENU_EXIT = new MenuItem("Exit");
		this.MENU_COLOR = new MenuItem("Color");
		this.MENU_ABOUT = new MenuItem("About ownFx");
		this.MENU_BRIGHTNESS = new Menu("Brightness");
		
		//Build TrayIcon
		this.buildTrayIcon();
		
		//Build color picker
		this.buildColorPicker();
	}
	
	private void buildColorPicker() {
		//Create Color picker
		ColorPickerView v = null;
		try {
			v = new ColorPickerView(this.currentColor, this);
			
		} catch(Exception e) {
			try {
				v = new ColorPickerView(Color.RED, this);
				
			} catch(Exception e2) {
				
			}
		}
			
		//Build view
		this.COLOR_PICKER.add(v);
		this.COLOR_PICKER.pack();
		this.COLOR_PICKER.setResizable(false);
		this.COLOR_PICKER.setTitle("ownFx");
		this.COLOR_PICKER.setAlwaysOnTop(true);
		
		//Calculate location
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int x = gd.getDisplayMode().getWidth() - 20 - this.COLOR_PICKER.getWidth();
		int y = gd.getDisplayMode().getHeight() - 60 - this.COLOR_PICKER.getHeight();
		
		//Set location
		this.COLOR_PICKER.setLocation(new Point(x, y));
		
	}
	
	private void buildTrayIcon() throws AWTException {	
		//Get Sysstem tray
		SystemTray tray = SystemTray.getSystemTray();
		
		//Create TrayIcon
		TrayIcon ico = new TrayIcon(TRAY_ICON);
				


		//Add ActionListener
		this.MENU_EXIT.addActionListener(this);
		this.MENU_COLOR.addActionListener(this);
		this.MENU_ABOUT.addActionListener(this);

		//Create submenu for brightness
		for(int i=0; i<=100; i+=10) {
			CheckboxMenuItem mi = new CheckboxMenuItem(i + "%");
			this.MENU_BRIGHTNESS_DATA.put(i, mi);
			this.MENU_BRIGHTNESS.add(mi);
			mi.addItemListener(this);
			mi.setState(false);
			
		}
		
		//Calculate current brightness and set selected
		int brightness = (int) Math.round(this.currentColor.getAlpha() / 255. * 100.);
		this.MENU_BRIGHTNESS_DATA.get(brightness).setState(true);
		
		//Create Menu and 
		PopupMenu m = new PopupMenu();
		m.add(this.MENU_COLOR);
		m.add(this.MENU_BRIGHTNESS);
		m.addSeparator();
		m.add(this.MENU_ABOUT);
		m.addSeparator();
		m.add(this.MENU_EXIT);
		
		//Add PopupMenu to ico
		ico.setPopupMenu(m);
		
		//Add Trayicon to SystemTray
		tray.add(ico);
		
	}
	
	private void showAbout() {
		JOptionPane.showMessageDialog(null,
			    "ownFX 1.0\nCreated by crys_\nGitHub Repository: https://github.com/crysxd/ownFX",
			    "About ownFX",
			    JOptionPane.INFORMATION_MESSAGE);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.MENU_ABOUT) {
			this.showAbout();
			
		} else if(e.getSource() == this.MENU_COLOR) {
			this.COLOR_PICKER.setVisible(true);
		
		} else if(e.getSource() == this.MENU_EXIT) {
			System.exit(0);
			
		} 
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		int brightness = 0;
		
		//Search brightness for the selected menu item
		for(Entry<Integer, CheckboxMenuItem> entry : this.MENU_BRIGHTNESS_DATA.entrySet()) {
			if(entry.getValue() == e.getSource())
				brightness = entry.getKey();
			
		}
		
		//Select pressed item, disselect all others
		for(CheckboxMenuItem cbm : this.MENU_BRIGHTNESS_DATA.values()) {
			cbm.setState(false);
			
			if(cbm == e.getSource())
				cbm.setState(true);
			
		}
		
		//Calculate brightness in range from 0 to 255
		brightness = (int) ((double)brightness / 100f * 255f);
				
		//Create color
		Color c = new Color(this.currentColor.getRed(), this.currentColor.getGreen(), this.currentColor.getBlue(), brightness);
		
		//Apply
		this.MY_MAIN.applyColor(c);
		
	}

	@Override
	public void colorPicked(Color c) {
		this.MY_MAIN.applyColor(c);
		this.currentColor = c;
		
	}
}
