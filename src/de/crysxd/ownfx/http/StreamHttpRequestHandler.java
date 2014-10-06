package de.crysxd.ownfx.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class StreamHttpRequestHandler implements HttpRequestHandler {

	@Override
	public boolean handleGet(URL url, BasicHttpRequest request,
			BasicHttpResponse preparedResponse, HttpServer server) throws Exception {
		
		//Stream öffnen
		InputStream in = this.getFileAsStream(url.getPath());
		
		//Wenn in != null ist
		if(in != null) {
			//Mime-Type erfragen
			String mimeType = URLConnection.guessContentTypeFromStream(in);
			
			//Wichtigste MIME-Types anhand der Dateiendung zuordnen
			String name = url.getPath();
			
			//CSS
			if(name.endsWith(".css")) {
				mimeType = "text/css";
				
			}
			
			//Javascript
			if(name.endsWith(".js")) {
				mimeType = "text/javascript";
				
			}
			
			//MIME-Type und Cache-Control setzen
			preparedResponse.addHeader(new BasicHeader("Content-Type", mimeType));
			preparedResponse.addHeader(new BasicHeader("Cache-Control", "cache-control: private, max-age=0, no-cache"));
			preparedResponse.send(in.available());

			OutputStream out = preparedResponse.getOutputStream();
			
			//Stream in out kopieren (Datei senden)
			byte[] buffer = new byte[1024];
			int readLength = 0;
			while((readLength = in.read(buffer)) > 0) {
				out.write(buffer, 0, readLength);
				
			}
			out.write(4);
			
			//Close input, flush output
			in.close();
			out.flush();
			
			//True zurückgeben, Anfrage wurde bearbeitet
			return true;
			
		}
		
		//False zurückgeben
		return false;

	}
	
	/**
	 * Gibt den {@link InputStream} für die angefragte Resource zurück oder null, wenn die Resource nicht gefunden wurde.
	 * @param path der Pfad der angefragten Resource.
	 * @return der {@link InputStream} der angefragten Resource oder null wenn die Resource nicht gefunden wurde.
	 * @throws Exception
	 */
	protected abstract InputStream getFileAsStream(String path) throws Exception;
	
}
