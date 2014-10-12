package de.crysxd.ownfx;

import java.io.File;

import javax.swing.UIManager;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

public class Main {
	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		SettingsManager sManager = new SettingsManager();
		ProfileManager pManager = new ProfileManager();
		
		ArduinoCommunicator arduinoCom = new ArduinoCommunicator(sManager.getCurrentSettings().getSerialInterfaceSelected());
		
		Server server = new Server();

		//create and add Connector for given port
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(80);
		server.addConnector(connector);

		//Create Resource Handler for accessing local files over HTTP
		ResourceHandler resourceHandler = new ResourceHandler();

		//Set Settings for the eRsourceHandler
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
		resourceHandler.setResourceBase(new File("web").getAbsolutePath());

		//Create a HandlerList including the ResourceHandler, a RestHandler (for accessing the Database) and a DefaultHandler
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {pManager, sManager, resourceHandler, new DefaultHandler() });
		server.setHandler(handlers);

		//Start Server (seperate Thread)
		server.start();
		
		System.out.println("Sending request...");
		System.out.println(arduinoCom.getCurrentProfileId());
		System.out.println("Done");
		
		System.out.println("=======================");
		System.out.println("Sending request...");
		Settings s = new Settings();
		arduinoCom.updateSettings(s);
		System.out.println("Done");
		
		System.out.println("RAM size: " + s.getRamSize());
		System.out.println("EEPROM size: " + s.getEepromSize());
		
		System.out.println("Sending settings");
		arduinoCom.sendSettings(sManager.getCurrentSettings());
		System.out.println("Send!");
		
	}
}
