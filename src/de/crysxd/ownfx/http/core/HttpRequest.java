package de.crysxd.ownfx.http.core;

import java.util.List;

public interface HttpRequest {

	/**
	 * Gibt die {@link RequestLine} dieses {@link BasicHttpRequest}s zur�ck.
	 * @return die {@link RequestLine} dieses {@link BasicHttpRequest}s
	 */
	public RequestLine getRequestLine();
	
	/**
	 * Gibt die Verwendete {@link ProtocolVersion} zur�ck.
	 * @return die verwendete {@link ProtocolVersion}
	 */
	public ProtocolVersion getProtocolVersion();
	
	/**
	 * Gibt eine {@link List} mit allen gespeicherten {@link Header}n zur�ck.
	 * @return eine {@link List} mit allen gespeicherten {@link Header}n
	 */
	public List<Header> getAllHeaders();
	
}
