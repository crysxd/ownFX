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
		handlers.setHandlers(new Handler[] {new ProfileManager(),  resourceHandler, new DefaultHandler() });
		server.setHandler(handlers);

		//Start Server (seperate Thread)
		server.start();

	}
}
