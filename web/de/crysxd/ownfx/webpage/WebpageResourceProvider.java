package de.crysxd.ownfx.webpage;
import java.io.InputStream;

import de.crysxd.ownfx.http.ResourceProvider;


public class WebpageResourceProvider implements ResourceProvider {

	@Override
	public InputStream getResourceAsStream(String name) {
		return this.getClass().getResourceAsStream(name);
		
	}
}
