package de.crysxd.ownfx.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stellt einen Http-�bertragung dar und bietet die M�glichkeit {@link Header} zu speichern.
 * @author chrwuer
 */
public abstract class HttpTransmission {

	//Die verwendete ProtocolVersion 
	private final ProtocolVersion PROTOCOL;
	
	//Alle gespeicherten Header.
	//Es wird eine Map verwendet um doppelte Eintr�ge zu verhindern
	private final Map<Integer, Header> HEADERS = new HashMap<Integer, Header>();

	/**
	 * Erzeugt einen neue {@link HttpTransmission}.
	 * @param protocol die verwendete {@link ProtocolVersion}
	 */
	public HttpTransmission(ProtocolVersion protocol) {
		this.PROTOCOL = protocol;
		
	}
	
	/**
	 * Gibt die Verwendete {@link ProtocolVersion} zur�ck.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion() {
		return PROTOCOL;
	}
	
	/**
	 * F�gt einen {@link Header} hinzu. Ist bereits ein Header mit dem gleichen Namen vorhanden, wird dieser �berschrieben.
	 * @param header der neue {@link Header}
	 * @return der �berschribene {@link Header} oder <code>null</code> wenn kein Header �berschrieben wurde.
	 */
	public Header addHeader(Header header) {
		return this.HEADERS.put(header.getName().hashCode(), header);
		
	}
	
	/**
	 * Gibt eine {@link List} mit allen gespeicherten {@link Header}n zur�ck.
	 * @return eine {@link List} mit allen gespeicherten {@link Header}n
	 */
	public List<Header> getAllHeaders() {
		return new ArrayList<Header>(this.HEADERS.values());
		
	}
}
