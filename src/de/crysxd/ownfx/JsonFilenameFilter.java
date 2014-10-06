package de.crysxd.ownfx;

import java.io.File;
import java.io.FilenameFilter;

public class JsonFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return name.toLowerCase().endsWith(".json");
		
	}

}
