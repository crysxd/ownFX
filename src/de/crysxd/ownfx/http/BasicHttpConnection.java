package de.crysxd.ownfx.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Implementierung einer einfachen HTTP-Verbindung.
 * @author chrwuer
 */
public class BasicHttpConnection implements HttpConnection, Runnable {

	//Das Socket, ¸ber das die Verbindung kommuniziert
	private final Socket MY_SOCKET;
	
	//Der Server, der diese Verbindung verwaltet
	private final HttpServer MY_SERVER;
	
	/**
	 * Erstellt eine neue 
	 * @param server
	 * @param soc
	 */
	public BasicHttpConnection(HttpServer server, Socket soc) {
		//Socket und Server sichern
		this.MY_SOCKET = soc;
		this.MY_SERVER = server;
		
		//Die Runnable in der die Verbindung bearbeitet wird auf dem Threadpool des Servers ausf¸hren
		this.MY_SERVER.execute(this);
		
	}
	
	/**
	 * Ermittelt, ob die Verbindung pffen gehalten werden sollte oder nicht.
	 * @param request der letzte {@link BasicHttpRequest} der Verbindung
	 * @return true, wenn die Verbindung geschlossen werden sollte, false wenn nicht
	 */
	private boolean shouldCloseConnection(BasicHttpRequest request) {
		//If HTTP 1.0 -> return true
		if(request.getProtocolVersion().getProtocol().equals("HTTP")
				&& request.getProtocolVersion().getMajor() == 1 
				&& request.getProtocolVersion().getMinor() == 0)
			return true;
		
		//If Connection header is keep-alive -> return false
		for(Header h : request.getAllHeaders()) {
			if(h.getName().toLowerCase().equals("connection")) {
				if(h.getValue().toLowerCase().equals("close"))
					return true;
				
			}
		}

		//header not found -> return false
		return false;
	}

	/**
	 * Liest den n‰chsten Request-Header ein und erstellt ein {@link BasicHttpRequest} Objekt.
	 * @param in der {@link InputStream}, von dem der Header einhgelesen werden soll
	 * @return das erstellte {@link BasicHttpRequest} Objekt
	 * @throws Exception
	 */
	private BasicHttpRequest readRequestHeader(InputStream in) throws Exception {
		//BufferedReader erzeugen, sowie String f¸r Zeile und StringBuilder f¸r Header
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuilder headerText = new StringBuilder();
		
		//Solange keine leere Zeile eingelesen wurde und nicht der Stream geschlossen wurde
		while((line = br.readLine()) != null && line.length() > 0) {
			//Zeile einlesen und an StringBuilder anh‰ngen (inkl. new Line)
			headerText.append(line);
			headerText.append("\n");
			
		}
		
		//Wenn nichts eingelesen wurde -> null zur¸ckgeben
		if(headerText.length() == 0)
			return null;
		
		//Header parsen und zur¸ckgeben
		return BasicHttpRequest.parse(headerText.toString());
		
	}
	
	@Override
	public void run() {
		
		//Streams sichern
		OutputStream out = null;
		InputStream in = null;
		try {
			out = this.getSocket().getOutputStream();
			in = this.getSocket().getInputStream();

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		//Solange der Thread nicht unterbrochen wurde
		while(!Thread.interrupted()) {
			try {
				
				//Request einlesen
				BasicHttpRequest request = this.readRequestHeader(in);	
				
				//Wird null zur¸ckgegeben konnte der Request nicht eingelesen werden -> Verbindung schlieﬂen
				if(request == null) {
					this.close();
					return;
				
				}
				
				//Request bearbeiten (der Server k¸mmert sich darum)
				this.getServer().handleRequest(request, out);
				
				//‹berpr¸fen ob die VErbindung geschlossen werden soll. Falls ja -> Verbindung schlieﬂen
				if(this.shouldCloseConnection(request)) {
					try {
						this.close();

					} catch(Exception e) {}
					return;

				}
				
			} catch(SocketException e) {
				return;
				
			} catch (Exception e) {
				//Ein Fehler ist aufgetreten. Wenn die Verbindung geschlossen ist, sollte sie nochmals sauber geschlossen werden
				if(this.isClosed()) {
					try {
						this.close();
					} catch (Exception e1) {}
					return;
					
				}
				
				//Fehler ausgeben
				e.printStackTrace();
			
			}
		}
	}


	@Override
	public void close() throws Exception {
		this.getSocket().close();
		this.MY_SERVER.removeConnection(this);
		
	}

	@Override
	public boolean isClosed() {
		return this.getSocket().isClosed();
		
	}

	@Override
	public Socket getSocket() {
		return this.MY_SOCKET;
		
	}

	@Override
	public HttpServer getServer() {
		return this.MY_SERVER;
		
	}
}
