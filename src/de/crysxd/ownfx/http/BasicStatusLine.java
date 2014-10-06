package de.crysxd.ownfx.http;

/**
 * Stellt die Statuszeile einer HTTP-Antwort dar.
 * @author chrwuer
 */
public class BasicStatusLine implements StatusLine {

	//Der Statuscode nach HTTP Definition, z.B. 404 oder 200
	private int statusCode;
	
	//Die Begründung des Status, z.B. Not found oder OK
	private String reasonPhrase;
	
	//Die verwendetet Protokollversion
	private ProtocolVersion protocolVersion;
	
	/**
	 * Erzeugt eine neue {@link BasicStatusLine} aus den gegebenen Informationen.
	 * @param protocol die verwendete {@link ProtocolVersion}
	 * @param code der Statuscode nach HTTP-Definiton, z.B. 200 oder 404
	 * @param phrase die Begründung des Status, z.B. Ok oder Not found
	 */
	public BasicStatusLine(ProtocolVersion protocol, int code, String phrase) {
		this.statusCode = code;
		this.reasonPhrase = phrase;
		this.protocolVersion = protocol;

	}
	
	@Override
	public int getStatusCode() {
		return this.statusCode;
	
	}
	
	@Override
	public String getReasonPhrase() {
		return this.reasonPhrase;
	
	}
	
	@Override
	public ProtocolVersion getProtocolVersion() {
		return this.protocolVersion;
		
	}

	@Override
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		
	}

	@Override
	public void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
		
	}

	@Override
	public void setProtocolVersion(ProtocolVersion version) {
		this.protocolVersion = version;
		
	}
	
	@Override
	public String toString() {
		return String.format("%s %d %s", 
				this.getProtocolVersion().toString(), 
				this.getStatusCode(),
				this.getReasonPhrase());

	}
}
