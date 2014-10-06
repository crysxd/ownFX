package de.crysxd.ownfx.http;

/**
 * Ein Interface für Header, wie sie im HTTP-Protokoll vorkommen.
 * @author chrwuer
 */
public interface Header {
	
	/**
	 * Gibt den Namen des Headers zurück.
	 * @return der Name des Headers
	 */
	public String getName();
	
	/**
	 * Gibt den Wert des Headers zurück.
	 * @return der Wert des Hedaers
	 */
	public String getValue();

}
