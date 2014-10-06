package de.crysxd.ownfx.http;

import java.io.OutputStream;

import de.crysxd.ownfx.http.core.BasicHttpRequest;

/**
 * Interface für einen HttpServer.
 * @author chrwuer
 */
public interface HttpServer {
	
	/**
	 * Fügt einen neuen {@link HttpRequestHandler} hinzu. Die Priorität wird dazu verwendet,
	 * die alle {@link HttpRequestHandler} in eine bestimmte Reihenfolge zu bringen, in der sie 
	 * nacheinander versuchen eine Anfrage zu bearbeiten. {@link HttpRequestHandler} mit einer 
	 * hohen Priorität werden zuerst verwendet.
	 * @param handler der {@link HttpRequestHandler}, der hinzugefügt werden soll
	 * @param priority die Priorität, die für den {@link HttpRequestHandler} hinterlegt werden soll
	 */
	public void addHandler(HttpRequestHandler handler, int priority);
	
	/**
	 * Entfernt den {@link HttpRequestHandler}.
	 * @param handler der {@link HttpRequestHandler}, welcher entfernt werden soll
	 */
	public void removeHandler(HttpRequestHandler handler);
	
	/**
	 * Startet den Server. <br>Nach diesem Aufruf ist der HTTP-Server von außen erreichbar.
	 * @throws Exception
	 */
	public void startServer() throws Exception;
	
	/**
	 * Stopt den Server. <br>Nach diesem Aufruf ist der HTTP-Server nicht mehr erreichbar.
	 * @throws Exception
	 */
	public void stopServer() throws Exception;
	
	/**
	 * Entfernt eine Verbindung aus der Liste der aktiven Verbindungen.
	 * @param con die {@link HttpConnection}, welche entfernt werden soll.
	 */
	public void removeConnection(HttpConnection con);
	
	/**
	 * Führt die gegebene {@link Runnable} auf dem Threadpool des Servers aus.
	 * @param r die {@link Runnable} die ausgeführt werden soll
	 */
	public void execute(Runnable r);
	
	/**
	 * Bearbeitet den gegebenen {@link BasicHttpRequest} indem die verschiedenen {@link HttpRequestHandler} 
	 * mit der Bearbeitung beauftragt werden. Die Antwort inkl. Header wird in den gegebenen {@link OutputStream}
	 * geschrieben.
	 * @param request der Anfrage, welche bearbeitet werden soll
	 * @param out der {@link OutputStream}, in dem die Antwort geschrieben wird.
	 * @throws Exception
	 */
	public void handleRequest(BasicHttpRequest request, OutputStream out) throws Exception;

}
