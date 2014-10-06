package de.crysxd.ownfx.http;

import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

/**
 * Interface um {@link BasicHttpRequest} Objekte zu behandeln
 * @author chrwuer
 */
public interface HttpRequestHandler {

	/**
	 * Behandelt das gegebene {@link BasicHttpRequest} Objekt. Bevor eine Antwort in den {@link OutputStream} geschrieben werden darf,
	 * muss {@link HttpServer#sendFinalizedResponseHeader(BasicHttpResponse, OutputStream)} oder 
	 * {@link HttpServer#sendFinalizedResponseHeader(BasicHttpResponse, OutputStream, long)} auf dem übergebenen {@link HttpServer} 
	 * aufgerufen werden, damit der HTTP-Header der Antwort gesendet wird.
	 * @param url die angeforderte {@link URI}
	 * @param request der {@link BasicHttpRequest} der behandelt werden soll
	 * @param preparedResponse die vom Server vorbereitetet {@link BasicHttpResponse}
	 * @param server der {@link HttpServer} der die bearbeitung des {@link BasicHttpRequest}s anfordert
	 * @return true, wenn der {@link BasicHttpRequest} bearbeitet wurde, false wenn nicht.
	 * @throws Exception
	 */
	public boolean handleGet(
			URL url, 
			BasicHttpRequest request, 
			BasicHttpResponse preparedResponse,
			HttpServer server) throws Exception;

}
