package de.crysxd.ownfx.http.core;

import java.util.List;

public interface HttpRequest {

	/**
	 * Gibt die {@link RequestLine} dieses {@link BasicHttpRequest}s zurück.
	 * @return die {@link RequestLine} dieses {@link BasicHttpRequest}s
	 */
	public RequestLine getRequestLine();
	
	/**
	 * Gibt die Verwendete {@link ProtocolVersion} zurück.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion();
	
	/**
	 * Gibt eine {@link List} mit allen gespeicherten {@link Header}n zurück.
	 * @return eine {@link List} mit allen gespeicherten {@link Header}n
	 */
	public List<Header> getAllHeaders();
	
}
