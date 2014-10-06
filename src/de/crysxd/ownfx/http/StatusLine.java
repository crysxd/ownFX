package de.crysxd.ownfx.http;

/**
 * Ein Interface f�r die Statuszeile einer HTTP-Antwort
 * @author chrwuer
 */
public interface StatusLine {
	
	/**
	 * Gibt den Statuscode zur�ck, z.B. 200 oder 404.
	 * @reutn der Statuscode
	 */
	public int getStatusCode();
	
	/**
	 * Setzt den Statuscode.
	 * @param statusCode der neue Statuscode, z.B. 200 oder 404
	 */
	public void setStatusCode(int statusCode);
	
	/**
	 * Gibt die Begr�ndung des Status zur�ck, z.B. OK oder Not Found
	 * @return die Begr�ndung des Status
	 */
	public String getReasonPhrase();
	
	/**
	 * Setzt die Begr�ndung des Status.
	 * @param reasonPhrase die neue Begr�ndung des Status, z.B. OK oder Not Found
	 */
	public void setReasonPhrase(String reasonPhrase);
	
	/**
	 * Gibt die verwendete {@link ProtocolVersion} zur�ck.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion();
	
	/**
	 * Setzt die verwendete {@link ProtocolVersion}
	 * @param version die verwendete {@link ProtocolVersion}
	 */
	public void setProtocolVersion(ProtocolVersion version);

}
