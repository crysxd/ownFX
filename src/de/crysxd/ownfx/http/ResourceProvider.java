package de.crysxd.ownfx.http;

import java.io.InputStream;

public interface ResourceProvider {
	
	public InputStream getResourceAsStream(String name);

}
