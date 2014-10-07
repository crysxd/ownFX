package de.crysxd.ownfx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ProfileManager extends AbstractHandler {

	//The location where the profiles are stored
	//FIXME Mac OS X and Linux Support
	private final File PROFILE_SAVE_LOCATION = new File(System.getProperty("user.home"), "AppData\\Local\\ownFX\\Profiles\\");

	//The URL on which a single profile can be requested
	private final String URL_GET_PROFILE = "/rest/profile";

	//The URL on which a overview over all profiles can be requested
	private final String URL_GET_PROFILES_LIST = "/rest/profiles_list";

	//The URL on which a profile can be saved
	private final String URL_SAVE_PROFILES = "/rest/save";

	//The ID of the current activated profile
	//FIXME: Request from board on startup
	private int activeProfileId = -1;

	//The list of all available projects
	private final List<Profile> PROFILES = new Vector<>();

	public ProfileManager() {
		//Create the profile save location if it not exists
		this.PROFILE_SAVE_LOCATION.mkdirs();

		//Iterate over all .json files in profile save location
		for(File f:this.PROFILE_SAVE_LOCATION.listFiles(new JsonFilenameFilter())) {
			try {
				//try to create a profile from the file contents
				this.PROFILES.add(Profile.readProfile(f));

			} catch(Exception e) {
				e.printStackTrace();

			}
		}		
	}
	
	private Profile getProfile(int id) {
		for(Profile p: this.PROFILES) {
			if(p.getId() == id) {
				return p;
				
			}			
		}
		
		return null;
		
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String answer = null;
		
		if(target.equals(this.URL_GET_PROFILES_LIST)) {
			answer = this.sendProfilesList();
			
		} else if(target.equals(this.URL_GET_PROFILE)) {
			int id = Integer.valueOf(baseRequest.getParameter("id"));
			Profile p = this.getProfile(id);
			
			if(p != null) {
				answer = this.sendProfile(p);
				
			}

		} else if(target.equals(this.URL_SAVE_PROFILES)) {
			Profile p = Profile.readProfile(baseRequest.getParameter("profile"));
			boolean apply = false;
			try {
				apply = Boolean.valueOf(baseRequest.getParameter("apply"));
				
			} catch(Exception e) {}
			
			answer = this.saveProfile(p, apply);
			
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
	
	private String sendProfilesList() {
		//Create a new list which is send as answer
		List<SimpleProfileWrapper> profilesList = new ArrayList<SimpleProfileWrapper>(this.PROFILES.size());

		//Copy all projects represented by a SimpleProjectWrapper into the list
		//Using the wrapper saves bandwith because only needed informations are transferred
		for(Profile p: this.PROFILES) {
			profilesList.add(new SimpleProfileWrapper(p, this.activeProfileId));

		}

		//Create the answr String by serializing the list to JSON
		return GsonSupport.createGson().toJson(profilesList);
		
	}
	
	private String sendProfile(Profile p) {
		return GsonSupport.createGson().toJson(p);
		
	}
	
	private String saveProfile(Profile p, boolean apply) {
		System.out.println(p);
		return null;
		
	}
}
