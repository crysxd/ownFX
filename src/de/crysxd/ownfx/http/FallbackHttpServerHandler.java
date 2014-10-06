package de.crysxd.ownfx.http;

import java.net.URL;

/**
 * Ein spezieller Handler, der immer eine 404 Seite sendet.
 * @author chrwuer
 */
public class FallbackHttpServerHandler implements HttpRequestHandler {

	//HTML-Code der 404 Seite.
	private final String PAGE_404 = 
			"<html>"
			+ "<head><title>Page not found</title></head>"
			+ "<body><h1>404 - Page not found</h1></body>"
			+ "</html>";
	
	@Override
	public boolean handleGet(URL url, BasicHttpRequest request,
			BasicHttpResponse preparedResponse, HttpServer server) throws Exception {
		
		//Header auf 404 setzen und senden
		preparedResponse.setStatusCode(404);
		preparedResponse.setReasonPhrase("Not found");
		preparedResponse.send(this.PAGE_404.length());

		
		//404 Seite schreiben
		preparedResponse.write(this.PAGE_404);
		preparedResponse.flush();

		return true;
		
	}
}
