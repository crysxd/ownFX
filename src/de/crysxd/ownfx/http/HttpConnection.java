package de.crysxd.ownfx.http;

import java.net.Socket;

/**
 * Interface f�r eine HTTP-Verbindung
 * @author chrwuer
 */
public interface HttpConnection {
	
	/**
	 * Schlie�t die Verbindung.
	 * @throws Exception
	 */
	public void close() throws Exception;

	/**
	 * �berpr�ft ob die Verbindung geschlossen wurde.
	 * @return true, wenn die Verbindung geschlossen wurde, false wenn nicht
	 */
	public boolean isClosed();

	/**
	 * Gibt das {@link Socket} zur�ck, �ber das diese Verbindung kommuniziert.
	 * @return das {@link Socket} zur�ck, �ber das diese Verbindung kommuniziert
	 */
	public Socket getSocket();
	
	/**
	 * Gibt den {@link HttpServer} zur�ck, der diese Verbindung verwaltet.
	 * @return der {@link HttpServer} zur�ck, der diese Verbindung verwaltet
	 */
	public HttpServer getServer();

}
