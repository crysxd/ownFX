package de.crysxd.ownfx.http;

public class BasicRequestLine implements RequestLine {
	
	//Die Methode dieser RequestLin
	private String method;
	
	//Die angefragte URI
	private String uri;
	
	//Das verwendete Protocol
	private ProtocolVersion protocol;
	
	/**
	 * Erstellt eine neue {@link BasicRequestLine} aus dem gegbenen String.
	 * @param line der String, der in eine {@link BasicRequestLine} geparst werden soll.
	 * @return die erstellte {@link BasicRequestLine}
	 */
	public static BasicRequestLine parse(String line) {
		String[] requestLine = line.split(" ");
		
		//ProtocolVersion erstellen
		ProtocolVersion protocol = ProtocolVersion.parse(requestLine[2]);
		
		//RequestLine ertsllen
		return new BasicRequestLine(requestLine[0], requestLine[1], protocol);
		
	}
	
	/**
	 * Erstellt eine neue {@link BasicRequestLine} aus den gegbenen Informationen.
	 * @param method die verwendete Methode, z.B. GET oder POST
	 * @param uri die angefrate URI
	 * @param protocol das verwendete Protocol
	 */
	public BasicRequestLine(String method, String uri, ProtocolVersion protocol) {
		this.method = method;
		this.uri = uri;
		this.protocol = protocol;
		
	}
	
	@Override
	public String getMethod() {
		return method;
	
	}

	@Override
	public String getUri() {
		return uri;
	
	}
	
	@Override
	public ProtocolVersion getProtocolVersion() {
		return protocol;
	
	}

	@Override
	public void setMethod(String method) {
		this.method = method;
		
	}

	@Override
	public void setUri(String uri) {
		this.uri = uri;
		
	}

	@Override
	public void setProtocolVersion(ProtocolVersion protocol) {
		this.protocol = protocol;
		
	}
}
