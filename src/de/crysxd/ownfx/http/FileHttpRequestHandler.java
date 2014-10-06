package de.crysxd.ownfx.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileHttpRequestHandler extends StreamHttpRequestHandler {

	//Der Root-Ordner der Verzeichnisstruktur, welche von diesem Handler bereit gestellt werden soll.
	private final File ROOT;
	
	/**
	 * Erzeugt einen neuen {@link StreamHttpRequestHandler}.
	 * @param root der Root-Ordner der Verzeichnisstruktur, welche von diesem Handler bereit gestellt werden soll
	 */
	public FileHttpRequestHandler(File root) {
		this.ROOT = root;
		
	}
	
	@Override
	protected InputStream getFileAsStream(String path) throws Exception {
		File file = new File(this.ROOT, path);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		return in;
		
	}
}
