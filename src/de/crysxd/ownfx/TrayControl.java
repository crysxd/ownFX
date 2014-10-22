package de.crysxd.ownfx;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TrayControl implements ActionListener {
	
	private final MenuItem MENU_EXIT = new MenuItem("Quit ownFX");
	private final MenuItem MENU_OPEN_WEB = new MenuItem("Open Configurator");
	private final Map<Integer, MenuItem> MENU_BRIGTHNESS = new HashMap<>();
	
	public TrayControl() throws AWTException {
		//Get Sysstem tray
		SystemTray tray = SystemTray.getSystemTray();
		
		//Determine optimized trayicon size
		Dimension iconSize = tray.getTrayIconSize();
		
		//Resize image
		Image sizedImage = R.getImage("favicon.png", iconSize.width, iconSize.height);
		
		//Create TrayIcon
		TrayIcon ico = new TrayIcon(sizedImage);
		
		//Create submenu for brightness
		Menu submenu = new Menu("Brightness");
		for(int i=0; i<=100; i+=25) {
			MenuItem mi = new MenuItem(i + "%");
			this.MENU_BRIGTHNESS.put(i, mi);
			submenu.add(mi);
			mi.addActionListener(this);
			
		}

		//Create Menu and 
		PopupMenu m = new PopupMenu();
		m.add(this.MENU_OPEN_WEB);
		this.MENU_OPEN_WEB.addActionListener(this);
		m.add(submenu);
		m.addSeparator();
		m.add(this.MENU_EXIT);
		this.MENU_EXIT.addActionListener(this);
		
		//Add PopupMenu to ico
		ico.setPopupMenu(m);
		
		//Add Trayicon to SystemTray
		tray.add(ico);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.MENU_EXIT) {
			System.exit(0);
			
		} else if(e.getSource() == this.MENU_OPEN_WEB) {
			try {
				URI uri = new URI("http://localhost");
				Desktop.getDesktop().browse(uri);
				
			} catch(Exception e1) {
				e1.printStackTrace();
				
			}
		} else if(this.MENU_BRIGTHNESS.containsValue(e.getSource())) {
			int brightness = 100;
			
			//Search brightness for the selected menu item
			for(Entry<Integer, MenuItem> entry : this.MENU_BRIGTHNESS.entrySet()) {
				if(entry.getValue() == e.getSource())
					brightness = entry.getKey();
				
			}
			
			Main.getMain().setSystemBrightness(brightness);
			
		}
	}
}
