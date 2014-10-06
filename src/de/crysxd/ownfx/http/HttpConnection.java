package de.crysxd.ownfx.http;

import java.net.Socket;

/**
 * Interface für eine HTTP-Verbindung
 * @author chrwuer
 */
public interface HttpConnection {
	
	/**
	 * Schließt die Verbindung.
	 * @throws Exception
	 */
	public void close() throws Exception;

	/**
	 * Überprüft ob die Verbindung geschlossen wurde.
	 * @return true, wenn die Verbindung geschlossen wurde, false wenn nicht
	 */
	public boolean isClosed();

	/**
	 * Gibt das {@link Socket} zurück, über das diese Verbindung kommuniziert.
	 * @return das {@link Socket} zurück, über das diese Verbindung kommuniziert
	 */
	public Socket getSocket();
	
	/**
	 * Gibt den {@link HttpServer} zurück, der diese Verbindung verwaltet.
	 * @return der {@link HttpServer} zurück, der diese Verbindung verwaltet
	 */
	public HttpServer getServer();

}
