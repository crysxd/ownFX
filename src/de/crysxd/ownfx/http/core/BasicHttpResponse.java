package de.crysxd.ownfx.http.core;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Stellt eine Http-Antwort dar mit allen enthaltenen Informationen und Headern.
 * @author chrwuer
 */
public class BasicHttpResponse extends HttpTransmission implements HttpResponse {

	//Die StatusLine dieses HttpResponse
	private final StatusLine STATUS_LINE;
	
	//Der Stream, in den die Antwort geschrieben werden soll
	private final OutputStream OUTPUT;
	
	//Ein Flag das angiebt, ob die Header schon gesenedet wurden
	private boolean isHeaderSend = false;
	
	/**
	 * Erstellt eine neue {@link BasicHttpResponse} mit der gegbenen {@link StatusLine}
	 * @param status die {@link StatusLine} dieses {@link BasicHttpResponse} Objektes
	 */
	public BasicHttpResponse(OutputStream out, StatusLine status) {
		super(status.getProtocolVersion());
		this.STATUS_LINE = status;
		
		//OutputStream in einen BasicHttpResponseOutputStream wrappen
		//Somit wird vor jedem schreiben überprüft ob der Header bereits gesendet ist
		this.OUTPUT = new BasicHttpResponseOutputStream(out);
		
	}
	
	@Override
	public StatusLine getStatusLine() {
		return this.STATUS_LINE;

	}
		
	@Override
	public void setReasonPhrase(String reasonPhrase) {
		this.STATUS_LINE.setReasonPhrase(reasonPhrase);
		
	}
	
	@Override
	public void setStatusCode(int code) {
		this.STATUS_LINE.setStatusCode(code);
		
	}
	
	@Override
	public String toString() {
		//StringBuilder bauen
		StringBuilder s = new StringBuilder();
		
		//Statuszeile anhängen
		s.append(this.getStatusLine().toString());
		s.append('\n');
		
		//Jeden Header anhängen
		for(Header h: this.getAllHeaders()) {
			s.append(h.toString());
			s.append('\n');
			
		}
		
		//Extra Leerzeile um Antwort abzuschließen
		s.append('\n');
		
		//String erzeugen und rückgeben
		return s.toString();
		
	}
	
	@Override
	public boolean isHeaderSend() {
		return isHeaderSend;
		
	}
	
	@Override
	public void send() throws IOException {
		if(this.isHeaderSend()) {
			throw new IllegalStateException("Der Header wurde breits versendet!");
			
		}
		
		this.isHeaderSend = true;
		this.write(this.toString());
		
	}
	
	@Override
	public void send(long contentLength) throws IOException {
		this.addHeader(new BasicHeader("Content-Length", String.valueOf(contentLength)));
		this.send();
		
	}
	
	@Override
	public OutputStream getOutputStream() {
		return this.OUTPUT;
		
	}
	
	@Override
	public void write(String s) throws IOException{
		this.getOutputStream().write(s.getBytes());
		
	}
	
	@Override
	public void flush() throws IOException {
		this.getOutputStream().flush();
		
	}
	
	public void close() throws IOException {
		this.getOutputStream().flush();
		this.getOutputStream().close();
		
	}
	
	/**
	 * Ein spzieller {@link OutputStream}, der vor derm schreiben überprüft, ob der Header bereits gesendet wurde und eine
	 * Fehlermeldung wirft, wenn dies nicht der Fall ist.
	 * @author chrwuer
	 */
	private class BasicHttpResponseOutputStream extends BufferedOutputStream {
		
		/**
		 * Erzeugt einen neuen {@link BasicHttpResponseOutputStream} und ummantelt den gegebenen {@link OutputStream}
		 * @param out der {@link OutputStream}, der ummantelt werden soll
		 */
		public BasicHttpResponseOutputStream(OutputStream out) {
			super(out);
			
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			//Überprüfen, ob der Header bereits gesendet wurde
			this.checkHeaderSend();
			
			//byte schreiben
			super.write(b);
			
		}
		
		@Override
		public synchronized void write(byte[] b, int off, int len) throws IOException {
			//Überprüfen, ob der Header bereits gesendet wurde
			this.checkHeaderSend();
			
			//byte[] schreiben
			super.write(b, off, len);
			
		}
		
		/**
		 * Überprüft, ob der Header bereits gesendet wurde und wirft eine {@link IOException}, falls dies nicht der Fall ist.
		 * @throws IOException
		 */
		private void checkHeaderSend() throws IOException {
			if(!BasicHttpResponse.this.isHeaderSend()) {
				throw new IOException("Der Header wurde noch nicht gesendet! Bevor Daten gesendet werden können muss send() oder send(long) aufgerufen werden!");
				
			}
		}
	}
}
