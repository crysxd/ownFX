package de.crysxd.ownfx.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Ein Interface f�r Http-Antworten.
 * @author chrwuer
 */
public interface HttpResponse {

	/**
	 * Gibt die {@link StatusLine} dieser {@link BasicHttpResponse} zur�ck.
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
	 * �berpr�ft ob der Header bereits gesendet wurde.
	 * @return <code>true</code> wenn der Header bereits gesendet wurde, <code>false</code> wenn nicht
	 */
	public boolean isHeaderSend();
	
	/**
	 * Sendet den Header.
	 * @throws IOException
	 */
	public void send() throws IOException;
	/**
	 * Setzt das Headerfeld f�r die L�nge der Antwort und sendet den Header anschlie�end.
	 * @param contentLength die L�nge der Antwort in Bytes
	 * @throws IOException
	 */
	public void send(long contentLength) throws IOException;
	
	/**
	 * Gibt den {@link OutputStream} zur�ck, in den die Antwort geschrieben werden soll zur�ck.
	 * @return der {@link OutputStream} zur�ck, in den die Antwort geschrieben werden soll
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
	 * Gibt die Verwendete {@link ProtocolVersion} zur�ck.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion();
	
	/**
	 * F�gt einen {@link Header} hinzu. Ist bereits ein Header mit dem gleichen Namen vorhanden, wird dieser �berschrieben.
	 * @param header der neue {@link Header}
	 * @return der �berschribene {@link Header} oder <code>null</code> wenn kein Header �berschrieben wurde.
	 */
	public Header addHeader(Header header);
	
	/**
	 * Gibt eine {@link List} mit allen gespeicherten {@link Header}n zur�ck.
	 * @return eine {@link List} mit allen gespeicherten {@link Header}n
	 */
	public List<Header> getAllHeaders();
	
}
