package de.crysxd.ownfx.http.core;

/**
 * Ein Interface f�r die erste Zeile einer HTTP-Anfrage.
 * z.B.: GET /mein/toller/pfad/datei.html HTTP/1.1
 * @author chrwuer
 *
 */
public interface RequestLine {
	
	/**
	 * Gibt die Methode die verwendet wird zur�ck, z.B. GET
	 * @return die Methode die verwendet wird.
	 */
	public String getMethod();
	
	/**
	 * Setzt die Methode die verwendet wird.
	 * @param method die neue Methode, z.B. GET
	 */
	public void setMethod(String method);
	
	/**
	 * Gibt die URI zur�ck die angefragt wurde.
	 * @return die URI die angefragt wurde
	 */
	public String getUri();
	
	/**
	 * Setzt die URI die angefragt wurde.
	 * @param uri die URI die angefragt wurde
	 */
	public void setUri(String uri);
	
	/**
	 * Gibt die verwendete {@link ProtocolVersion} zur�ck.
	 * @return die verwendete {@link ProtocolVersion} zur�ck.
	 */
	public ProtocolVersion getProtocolVersion();
	
	/**
	 * Setzt die verwendete {@link ProtocolVersion}
	 * @param protocol die neue {@link ProtocolVersion}
	 */
	public void setProtocolVersion(ProtocolVersion protocol);
	
}
