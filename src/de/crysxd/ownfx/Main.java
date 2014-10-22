package de.crysxd.ownfx;

import java.awt.AWTException;
import java.io.File;

import javax.swing.UIManager;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

public class Main {

	private static Main mainInstance;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}	
		
		Main.mainInstance = new Main();
		
		try {
			Main.mainInstance.init();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
		
	public static Main getMain() {
		return mainInstance;
		
	}
	
	/*
	 * -----------------------------------------------------------------------------------
	 * Non-static
	 */
	
	private ArduinoCommunicator arduinoCom;
	private SettingsManager sManager;
	private ProfileManager pManager;
	public Main() {
		
	}
	
	private void init() throws Exception {
		sManager = new SettingsManager();
		pManager = new ProfileManager();
		this.createArduinoCommunicator();
		
		pManager.setActiveProfile(this.arduinoCom.getCurrentProfileId());
		
		try {
			new TrayControl();
			
		} catch (AWTException e) {
			e.printStackTrace();
			
		}
		
		//Create Jetty HTTP Server
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
		
	}
	
	public void createArduinoCommunicator() {
		try {
			this.arduinoCom = new ArduinoCommunicator(this.sManager.getCurrentSettings().getSerialInterfaceSelected());
			
		} catch (Exception e) {
			e.printStackTrace();
			this.arduinoCom = null;
			//TODO Fehlermeldung

		} 
	}
	
	public void sendSettings() {
		try {
			this.arduinoCom.sendSettings(this.sManager.getCurrentSettings());
			this.arduinoCom.close();
			
		} catch(Exception e) {
			e.printStackTrace();
			//TODO Fehlermeldung

		} 
		
		this.createArduinoCommunicator();

	}
	
	public void sendSelectedProfile() {
		try {
			this.arduinoCom.sendProfile(this.pManager.getActiveProfile());
			
		} catch (Exception e) {
			e.printStackTrace();
			//TODO Fehlermeldung
			
		}
		
	}
	
	public void setSystemBrightness(int percent) {
		sManager.getCurrentSettings().setSystemBrightness((int) (percent*2.55));
		this.sendSettings();
		
	}
} 
