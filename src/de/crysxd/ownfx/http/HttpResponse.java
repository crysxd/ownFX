package de.crysxd.ownfx.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Ein Interface für Http-Antworten.
 * @author chrwuer
 */
public interface HttpResponse {

	/**
	 * Gibt die {@link StatusLine} dieser {@link BasicHttpResponse} zurück.
	 * @return die {@link StatusLine} dieser {@link BasicHttpResponse}
	 */
	public StatusLine getStatusLine();
	/**
	 * Setzt den Resaon-Phrase der {@link StatusLine}, welche von diesem {@link BasicHttpResponse} Objekt verwendet wird.
	 * @param reasonPhrase die neue Reason-Phrase,  z.B. OK oder Forbidden
	 */
	public void setReasonPhrase(String reasonPhrase);
	
	/**
	 * Setzt den Statuscode der {@link StatusLine}, welche von diesem {@link BasicHttpResponse} Objekt verwendet wird.
	 * @param reasonPhrase der neue Statuscode, z.B. 200 oder 404, siehe HTTP-Definition
	 */
	public void setStatusCode(int code);
	
	/**
	 * Überprüft ob der Header bereits gesendet wurde.
	 * @return <code>true</code> wenn der Header bereits gesendet wurde, <code>false</code> wenn nicht
	 */
	public boolean isHeaderSend();
	
	/**
	 * Sendet den Header.
	 * @throws IOException
	 */
	public void send() throws IOException;
	/**
	 * Setzt das Headerfeld für die Länge der Antwort und sendet den Header anschließend.
	 * @param contentLength die Länge der Antwort in Bytes
	 * @throws IOException
	 */
	public void send(long contentLength) throws IOException;
	
	/**
	 * Gibt den {@link OutputStream} zurück, in den die Antwort geschrieben werden soll zurück.
	 * @return der {@link OutputStream} zurück, in den die Antwort geschrieben werden soll
	 */
	public OutputStream getOutputStream();
	
	/**
	 * Schreibt den gegebenen String in den {@link OutputStream}, in den die Antwort geschrieben werden soll.
	 * @param s der {@link String}, der in den {@link OutputStream} geschrieben werden soll.
	 * @see #getOutputStream()
	 * @throws IOException
	 */
	public void write(String s) throws IOException;
	
	/**
	 * Leert den Puffer des {@link OutputStream}s, in den die Antwort geschrieben werden soll.
	 * @see #getOutputStream()
	 * @throws IOException
	 */
	public void flush() throws IOException;
	
	/**
	 * Gibt die Verwendete {@link ProtocolVersion} zurück.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion();
	
	/**
	 * Fügt einen {@link Header} hinzu. Ist bereits ein Header mit dem gleichen Namen vorhanden, wird dieser überschrieben.
	 * @param header der neue {@link Header}
	 * @return der überschribene {@link Header} oder <code>null</code> wenn kein Header überschrieben wurde.
	 */
	public Header addHeader(Header header);
	
	/**
	 * Gibt eine {@link List} mit allen gespeicherten {@link Header}n zurück.
	 * @return eine {@link List} mit allen gespeicherten {@link Header}n
	 */
	public List<Header> getAllHeaders();
	
}
