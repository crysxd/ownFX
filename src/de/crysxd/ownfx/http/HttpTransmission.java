package de.crysxd.ownfx.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stellt einen Http-Übertragung dar und bietet die Möglichkeit {@link Header} zu speichern.
 * @author chrwuer
 */
public abstract class HttpTransmission {

	//Die verwendete ProtocolVersion 
	private final ProtocolVersion PROTOCOL;
	
	//Alle gespeicherten Header.
	//Es wird eine Map verwendet um doppelte Einträge zu verhindern
	private final Map<Integer, Header> HEADERS = new HashMap<Integer, Header>();

	/**
	 * Erzeugt einen neue {@link HttpTransmission}.
	 * @param protocol die verwendete {@link ProtocolVersion}
	 */
	public HttpTransmission(ProtocolVersion protocol) {
		this.PROTOCOL = protocol;
		
	}
	
	/**
	 * Gibt die Verwendete {@link ProtocolVersion} zurück.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion() {
		return PROTOCOL;
	}
	
	/**
	 * Fügt einen {@link Header} hinzu. Ist bereits ein Header mit dem gleichen Namen vorhanden, wird dieser überschrieben.
	 * @param header der neue {@link Header}
	 * @return der überschribene {@link Header} oder <code>null</code> wenn kein Header überschrieben wurde.
	 */
	public Header addHeader(Header header) {
		return this.HEADERS.put(header.getName().hashCode(), header);
		
	}
	
	/**
	 * Gibt eine {@link List} mit allen gespeicherten {@link Header}n zurück.
	 * @return eine {@link List} mit allen gespeicherten {@link Header}n
	 */
	public List<Header> getAllHeaders() {
		return new ArrayList<Header>(this.HEADERS.values());
		
	}
}
