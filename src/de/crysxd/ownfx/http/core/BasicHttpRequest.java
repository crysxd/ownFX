package de.crysxd.ownfx.http.core;


/**
 * Stellt eine Http-Anfrage dar mit allen enthaltenen Informationen und Headern.
 * @author chrwuer
 */
public class BasicHttpRequest extends HttpTransmission implements HttpRequest {
	
	//Die erste Zeile der HTTP-Anfrage
	private final RequestLine REQUEST_LINE;
	
	/**
	 * Erzeugt aus dem Text eines HTTP-Headers ein {@link BasicHttpRequest} Objekt.
	 * @param headerText der Text des HTTP-Headers
	 * @return das erzeugte {@link BasicHttpRequest} Objekt
	 */
	public static BasicHttpRequest parse(String headerText) {
		//Zeilen aufsplitten
		String[] lines = headerText.split("\n");
		
		//Erste Zeile ls RequestLine interpretieren
		RequestLine requestLine = BasicRequestLine.parse(lines[0]);
		
		//RequestLine ertsllen
		BasicHttpRequest request = new BasicHttpRequest(requestLine);
				
		//Über Zeilen iterieren
		for(int i=1; i<lines.length; i++) {		
			try {
				//Header hinzufügen
				request.addHeader(BasicHeader.parse(lines[i]));	
				
			} catch(Exception e) {
				//Dürfte bei einem normalen Header nicht vorkommen...
				e.printStackTrace();
				
			}
		}
		
		//Request zurück geben
		return request;
		
	}
	
	/**
	 * Erzeugt einen neuen {@link BasicHttpRequest} mit der gegbenen {@link RequestLine}.
	 * @param requestLine die {@link RequestLine} dieses {@link BasicHttpRequest}s
	 */
	public BasicHttpRequest(RequestLine requestLine) {
		super(requestLine.getProtocolVersion());
		this.REQUEST_LINE = requestLine;
		
	}
	
	@Override
	public RequestLine getRequestLine() {
		return this.REQUEST_LINE;
		
	}
}
