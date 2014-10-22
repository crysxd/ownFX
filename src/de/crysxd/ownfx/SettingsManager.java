package de.crysxd.ownfx;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class SettingsManager extends AbstractHandler {

	//The URL on which a single profile can be requested
	private final String URL_GET_SETTINGS = "/rest/settings";

	//The URL on which a overview over all profiles can be requested
	private final String URL_SAVE_SETTINGS = "/rest/settings_save";
	
	//The file in which the settings are stored
	private final File SETTINGS_SAVE_LOCATION  = new File(System.getProperty("user.home"), "AppData\\Local\\ownFX\\settings.json");
	
	//The Settings object storing the current settings
	private Settings currentSettings;
	
	public SettingsManager() {
		try {
			this.currentSettings = Settings.readSettings(this.SETTINGS_SAVE_LOCATION);
	
		} catch(Exception e) {
			System.out.println("Could not load settings. Created new settings.");
			e.printStackTrace();
			
			this.currentSettings = new Settings();
			
			//Select the first serial interface if available
			this.currentSettings.updateAvailableSerialInterfaces();
			if(this.currentSettings.getSerialInterfaces().length > 0)
				this.currentSettings.setSerialInterfaceSelected(this.currentSettings.getSerialInterfaces()[0]);
			
			try {
				this.save();
				Desktop.getDesktop().browse(new URI("http://localhost/settings.html"));
				
			} catch (Exception f) {
				System.out.println("Error while saving newly created settings.");
				f.printStackTrace();
				
			}
		}
		
		this.currentSettings.updateAvailableSerialInterfaces();
		
	}
	
	public void save() throws FileNotFoundException, IOException {
		this.currentSettings.save(this.SETTINGS_SAVE_LOCATION);

	}
	
	public Settings getCurrentSettings() {
		return this.currentSettings;
		
	}

	@Override
	public synchronized void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		String answer = null;
		
		if(target.equals(this.URL_GET_SETTINGS)) {
			answer = GsonSupport.stringify(this.currentSettings);
			
		} else if(target.equals(this.URL_SAVE_SETTINGS)) {
			String newSettings = request.getParameter("settings");
			this.currentSettings = Settings.readSettings(newSettings);
			this.save();
			Main.getMain().sendSettings();
			answer = "";
			
		}
		
		if(answer != null ) {
			baseRequest.setHandled(true);
			response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
			response.addHeader("content-type", "application/json");
			response.setContentLength(answer.length());
			response.getWriter().write(answer);
			response.getWriter().flush();
			
		}
		
	}

}
