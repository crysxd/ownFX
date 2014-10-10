package de.crysxd.ownfx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

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

	//The URL on which a profile can be deleted
	private final String URL_DELETE_PROFILE = "/rest/delete";
	
	//The URL on which a profile can be saved
	private final String URL_SAVE_PROFILE = "/rest/save";
	
	//The URL on which a profile can be saved
	private final String URL_IMPORT_PROFILE = "/rest/import";

	//The ID of the current activated profile
	//FIXME: Request from board on startup
	private long activeProfileId = 0;

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
	
	private synchronized Profile getProfile(long id) {
		for(Profile p: this.PROFILES) {
			if(p.getId() == id) {
				return p;
				
			}			
		}
		
		return null;
		
	}
	
	private synchronized void setProfile(Profile p) {
		for(int i=0; i<this.PROFILES.size(); i++) {
			if(this.PROFILES.get(i).getId() == p.getId()) {
				this.PROFILES.set(i, p);
				
			}			
		}
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String answer = null;
		
		if(target.equals(this.URL_GET_PROFILES_LIST)) {
			answer = this.sendProfilesList();
			
		} else if(target.equals(this.URL_GET_PROFILE)) {
			long id = Long.valueOf(baseRequest.getParameter("id"));
			Profile p = this.getProfile(id);
			
			if(p != null) {
				answer = this.sendProfile(p);
				
			}

		} else if(target.equals(this.URL_DELETE_PROFILE)) {
			long id = Long.valueOf(baseRequest.getParameter("id"));
			Profile p = this.getProfile(id);
			
			if(p != null) {
				answer = this.deleteProfile(p);
				
			}

		} else if(target.equals(this.URL_SAVE_PROFILE)) {
			Profile p = Profile.readProfile(baseRequest.getParameter("profile"));
			boolean apply = Boolean.valueOf(baseRequest.getParameter("apply"));
			
			answer = this.saveProfile(p, apply);
			
		} else if(target.equals(this.URL_IMPORT_PROFILE)) {
			answer = this.importProfile();
			
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
	
	private String deleteProfile(Profile p) {
		System.out.println(p);
		this.PROFILES.remove(p);
		this.getProfileSaveFile(p).delete();
		
		return "";
		
	}
	
	private String importProfile() throws IOException {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "ownFX Profile";
				
			}
			
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".profile");
				
			}
		});
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.showOpenDialog(null);
		File[] files = fc.getSelectedFiles();
		
		if(files == null || files.length == 0)
			return "";
		
		long id = System.currentTimeMillis();
		for(File f: files) {
			Profile p = Profile.readProfile(f);
			p.setId(id++);
			this.PROFILES.add(p);
			this.saveProfile(p, false);
			
		}
		
		return "";
	}

	private String sendProfilesList() {
		//Create a new list which is send as answer
		List<SimpleProfileWrapper> profilesList = new ArrayList<SimpleProfileWrapper>(this.PROFILES.size());

		//Copy all projects represented by a SimpleProjectWrapper into the list
		//Using the wrapper saves bandwith because only needed informations are transferred
		for(Profile p: this.PROFILES) {
			profilesList.add(new SimpleProfileWrapper(p, this.activeProfileId));

		}

		//Create the answer String by serializing the list to JSON
		return GsonSupport.createGson().toJson(profilesList);
		
	}
	
	private String sendProfile(Profile p) {
		return GsonSupport.createGson().toJson(p);
		
	}
	
	private String saveProfile(Profile p, boolean apply) throws IOException {
		this.writeProfile(p);
		
		this.PROFILES.remove(this.getProfile(p.getId()));
		this.PROFILES.add(p);
		
		if(apply) {
			//FIXME Apply project
			
		}
		
		return "";
		
	}
	
	private void writeProfile(Profile p) throws IOException {
		this.setProfile(p);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(this.getProfileSaveFile(p)));
		bw.write(GsonSupport.createGson().toJson(p));
		bw.close();
		
	}
	
	private File getProfileSaveFile(Profile p) {
		return new File(this.PROFILE_SAVE_LOCATION, p.getId() + ".json");
		
	}
}
