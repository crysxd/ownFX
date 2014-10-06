package de.crysxd.ownfx.http;

import java.io.InputStream;


/**
 * Ein spezieller {@link StreamHttpRequestHandler}, der versucht die URI in {@link WebpageResourceProvider} zu finden.
 * @author chrwuer
 */
public class ResourceHttpRequestHandler extends StreamHttpRequestHandler {
	
	//Der ResourceProvider, welcher Resourcen zur Verfügung stellt
	private final ResourceProvider MY_PROVIDER;
	
	/**
	 * Erzeugt einen neuen {@link ResourceHttpRequestHandler}, der die Resourcen des {@link ResourceProvider}s bereit stellt.
	 * @param p der {@link ResourceProvider}, dessen Resourcen verfügbar gemacht werden sollen.
	 */
	public ResourceHttpRequestHandler(ResourceProvider p) {
		this.MY_PROVIDER = p;
		
	}
	
	@Override
	protected InputStream getFileAsStream(String path) throws Exception {
		//Das erste / wegschneiden, dann versuchen einen Stream auf die Resource zu erhalten
		return this.MY_PROVIDER.getResourceAsStream(path.substring(1));
		
	}

}
