package de.crysxd.ownfx;

import de.crysxd.ownfx.http.BasicHttpServer;
import de.crysxd.ownfx.http.HttpServer;
import de.crysxd.ownfx.http.ResourceHttpRequestHandler;
import de.crysxd.ownfx.http.ResourceProvider;
import de.crysxd.ownfx.webpage.WebpageResourceProvider;

public class Main {
	public static void main(String[] args) {
		ResourceProvider webpage = new WebpageResourceProvider();
		
		HttpServer s = new BasicHttpServer(80);
		s.addHandler(new ResourceHttpRequestHandler(webpage), 100);
		
		try {
			s.startServer();
			System.out.println("Server started!");
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
}
