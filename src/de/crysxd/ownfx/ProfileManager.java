package de.crysxd.ownfx;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.crysxd.ownfx.http.HttpServer;
import de.crysxd.ownfx.http.StreamHttpRequestHandler;
import de.crysxd.ownfx.http.core.BasicHeader;
import de.crysxd.ownfx.http.core.BasicHttpRequest;
import de.crysxd.ownfx.http.core.BasicHttpResponse;

public class ProfileManager extends StreamHttpRequestHandler {

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
	
	@Override
	public boolean handleGet(URL url, BasicHttpRequest request,
			BasicHttpResponse preparedResponse, HttpServer server)
			throws Exception {
		
		//if the information about a single profile is requested
		if(url.getPath().matches(this.URL_GET_PROFILE)) {
			try {
				int id = Integer.valueOf(this.getQueryParameter(url.getQuery(), "id"));
				
				for(Profile p: this.PROFILES) {
					//If the current profile is the requested one -> Send it
					if(p.getId() == id) {
						//Serialize the profile to create answer
						String answer = GsonSupport.createGson().toJson(p);
						
						//send the headers after adding a no cache header
						preparedResponse.addHeader(new BasicHeader("cache-control", "no-cache"));
						preparedResponse.send(answer.length());
						
						//write answer and close stream
						preparedResponse.write(answer);
						preparedResponse.close();
						
						return true;
					}			
				}
			} catch(Exception e) {
			}
			
			//No id parameter in query or id not found. Send a 400 Bad Request
			preparedResponse.setStatusCode(400);
			preparedResponse.setReasonPhrase("Bad Request");
			preparedResponse.send(0);
			preparedResponse.close();
			
			return true;
			
		}
		
		//if the list of all profiles is requested
		if(url.getPath().matches(this.URL_GET_PROFILES_LIST)) {
			//Create a new list which is send as answer
			List<SimpleProfileWrapper> profilesList = new ArrayList<SimpleProfileWrapper>(this.PROFILES.size());
			
			//Copy all projects represented by a SimpleProjectWrapper into the list
			//Using the wrapper saves bandwith because only needed informations are transferred
			for(Profile p: this.PROFILES) {
				profilesList.add(new SimpleProfileWrapper(p, this.activeProfileId));
				
			}
			
			//Create the answr String by serializing the list to JSON
			String answer = GsonSupport.createGson().toJson(profilesList);
			
			//send the headers after adding a no cache header
			preparedResponse.addHeader(new BasicHeader("cache-control", "no-cache"));
			preparedResponse.send(answer.length());
			
			//write answer and close stream
			preparedResponse.write(answer);
			preparedResponse.close();
			
			//return true to signalise the server the request is handeled
			return true;
			
		}
		
		//if a profile should be saved
		if(url.getPath().matches(this.URL_SAVE_PROFILES)) {
			
			return true;
			
		}
		
		return false;
	}

	@Override
	protected InputStream getFileAsStream(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getQueryParameter(String query, String parameterName) {
		parameterName += "=";
		int start = query.indexOf(parameterName);
		
		if(start >= 0) {
			if(start == 0 || query.charAt(start - 1) == '?') {
				start += parameterName.length();
				int end = query.indexOf('?', start);
				end = end < 0 ? query.length() : end;
				
				return query.substring(start, end);

			}
		} 
		
		return null;
		
	}
	
}
