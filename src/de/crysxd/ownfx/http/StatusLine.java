package de.crysxd.ownfx.http;

/**
 * Ein Interface für die Statuszeile einer HTTP-Antwort
 * @author chrwuer
 */
public interface StatusLine {
	
	/**
	 * Gibt den Statuscode zurück, z.B. 200 oder 404.
	 * @reutn der Statuscode
	 */
	public int getStatusCode();
	
	/**
	 * Setzt den Statuscode.
	 * @param statusCode der neue Statuscode, z.B. 200 oder 404
	 */
	public void setStatusCode(int statusCode);
	
	/**
	 * Gibt die Begründung des Status zurück, z.B. OK oder Not Found
	 * @return die Begründung des Status
	 */
	public String getReasonPhrase();
	
	/**
	 * Setzt die Begründung des Status.
	 * @param reasonPhrase die neue Begründung des Status, z.B. OK oder Not Found
	 */
	public void setReasonPhrase(String reasonPhrase);
	
	/**
	 * Gibt die verwendete {@link ProtocolVersion} zurück.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion();
	
	/**
	 * Setzt die verwendete {@link ProtocolVersion}
	 * @param version die verwendete {@link ProtocolVersion}
	 */
	public void setProtocolVersion(ProtocolVersion version);

}
