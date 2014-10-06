package de.crysxd.ownfx.http.core;

/**
 * Stellt einen Header in HTTP-Anfragen oder HTTP-Antworten dar.
 * @author chrwuer
 */
public class BasicHeader implements Header {

	//Der Name des Headers
	private String headerName;
	
	//Der Wert des Headers
	private String headerValue;
	
	/**
	 * Interpretiert den gegebenen {@link String} als Header. 
	 * Der String muss das Format .*:.* haben, wie es in HTTP-Anfragen oder Antworten vorkommt.
	 * @param headerLine der {@link String} der als {@link Header} interpretiert werden soll.
	 * @return der erstellte {@link BasicHeader}
	 */
	public static BasicHeader parse(String headerLine) {
		//Ende des Header-Namens finden
		int nameEnd = headerLine.indexOf(":");
		
		//Wenn nichts gefunden -> Überspringen
		if(nameEnd < 0)
			throw new IllegalArgumentException("Fehler beim Parsen des Headers. Kein ':' gefunden!");
		
		//Name und Wert finden
		String name = headerLine.substring(0, nameEnd).trim();
		String value = headerLine.substring(nameEnd + 1).trim();
	
		//Header erzeugen
		return new BasicHeader(name, value);
		
	}
	
	/**
	 * Erzeugt einen neuen {@link BasicHeader} mit den gegebenen Werten.
	 * @param name der Name des Headers
	 * @param value der Wert des Headers
	 */
	public BasicHeader(String name, String value) {
		this.headerName = name;
		this.headerValue = value;
		
	}
	
	@Override
	public String getName() {
		return headerName;
		
	}
	
	@Override
	public String getValue() {
		return headerValue;
		
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s", this.getName(), this.getValue());
		
	}
}
