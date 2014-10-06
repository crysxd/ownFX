package de.crysxd.ownfx.http;

/**
 * Ein Interface f�r Header, wie sie im HTTP-Protokoll vorkommen.
 * @author chrwuer
 */
public interface Header {
	
	/**
	 * Gibt den Namen des Headers zur�ck.
	 * @return der Name des Headers
	 */
	public String getName();
	
	/**
	 * Gibt den Wert des Headers zur�ck.
	 * @return der Wert des Hedaers
	 */
	public String getValue();

}
