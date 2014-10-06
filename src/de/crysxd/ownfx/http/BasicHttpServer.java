package de.crysxd.ownfx.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Einfache Implementierung eines HTTP-Servers.
 * @author chrwuer
 */
public class BasicHttpServer implements Runnable, HttpServer {

	//Der Threadpool der von diesem HttpServer verwednet wird
	private ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
	
	//Eine Map die Alle Handler und ihre Priorität enthält
	private Map<HttpRequestHandler, Integer> HANDLERS = new LinkedHashMap<HttpRequestHandler, Integer>();
	
	//Eine Liste mit allen offenen Verbindungen
	private final List<BasicHttpConnection> OPEN_CONNECTIONS = new ArrayList<BasicHttpConnection>();
	
	//Der Port auf dem dieser Server läuft
	private final int PORT;

	//Die Future des Main-Threads, welcher auf eingehende Verbindungen wartet
	private Future<?> mainThreadFuture = null;
	
	//Das Serversocket
	private ServerSocket serverSocket = null;

	/**
	 * Erzeugt einen neuen {@link BasicHttpServer}. Der Server ist aber erst 
	 * nach dem Aufruf von {@link #startServer()} erreichbar!
	 * @param port der Port, an dem der Server erreichbar sein soll
	 */
	public BasicHttpServer(int port) {
		this.PORT = port;

		//Fallback Handler hinzufügen mit höchster Priorität
		//Der Fallbackhandler sendet ein 404, muss also als letztes beansprucht werden
		this.addHandler(new FallbackHttpServerHandler(), Integer.MIN_VALUE);

	}
	
	/**
	 * Sortiert die Handler nach ihrer Priorität.
	 */
	private void sortHandlersByPriority() {
		//Werte aus der Map in eine Liste kopieren
		//Jeder eintrag ist vom Typ (Entry<HttpRequestHandler, Integer>
		List<?> list = new LinkedList<Entry<HttpRequestHandler, Integer>>(this.HANDLERS.entrySet());

		//Comperator definieren um die Werte zu sortieren
		//Einfach die Prioritäten miteinander vergleichen
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object o1, Object o2) {
				Integer v1 = ((Entry<HttpRequestHandler, Integer>) o1).getValue();
				Integer v2 = ((Entry<HttpRequestHandler, Integer>) o2).getValue();

				return v2.compareTo(v1);

			}
		});

		//Ergebnisse zurückkopieren
		this.HANDLERS.clear();
		for (Iterator<?> it = list.iterator(); it.hasNext();) {
			@SuppressWarnings("unchecked")
			Entry<HttpRequestHandler, Integer> entry = (Entry<HttpRequestHandler, Integer>) it.next();
			this.HANDLERS.put(entry.getKey(), entry.getValue());

		} 
	}
	
	/**
	 * Erstellt eine neue {@link BasicHttpResponse} mit den Headern für Server und Date.
	 * Status der Response ist 200 OK.
	 * @return die neu erstellte {@link BasicHttpResponse}
	 */
	private BasicHttpResponse prepareHttpResponse(OutputStream out, BasicHttpRequest req) {
		BasicHttpResponse response = new BasicHttpResponse(out, new BasicStatusLine(req.getProtocolVersion(), 200, "OK"));
		response.addHeader(new BasicHeader("Server", this.getClass().getSimpleName()));
		response.addHeader(new BasicHeader("Date", new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss z", Locale.ENGLISH).format(new Date())));

		return response;

	}
	
	@Override
	public void addHandler(HttpRequestHandler handler, int priority) {
		//Hinzufügen und anschließend sortieren
		this.HANDLERS.put(handler, priority);
		this.sortHandlersByPriority();

	}

	@Override
	public void removeHandler(HttpRequestHandler handler) {
		this.HANDLERS.remove(handler);

	}

	@Override
	public synchronized void startServer() throws Exception {
		//Wenn noch nicht gestartet ist serverSocket restellen und Main-Thread starten
		if(this.mainThreadFuture == null && this.serverSocket == null) {
			this.serverSocket = new ServerSocket(this.PORT);
			this.mainThreadFuture = this.THREAD_POOL.submit(this);

		} else {
			//Server läuft schon -> Runtime Exception starten
			throw new RuntimeException("Server already running!");

		}
	}

	@Override
	public synchronized void stopServer() throws Exception {
		//Wenn der Server läuft stoppen, ansonsten nichts tun
		if(this.mainThreadFuture != null) {
			this.THREAD_POOL.shutdownNow();
			this.serverSocket.close();
			this.serverSocket = null;

		}
	}

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				
				//Verbindung akzeptieren
				Socket soc = this.serverSocket.accept();

				//Zu offnene Connections hinzufügen (und HttpConnection erstellen)
				this.OPEN_CONNECTIONS.add(new BasicHttpConnection(this, soc));

			} catch(SocketException e) {
				//Wenn das ServerSocket zu ist den Thread beenden
				if(this.serverSocket.isClosed()) {
					break;

				}

				//Ansonsten Fehler ausgeben
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}

	@Override
	public void removeConnection(HttpConnection con) {
		this.OPEN_CONNECTIONS.remove(con);

	}

	@Override
	public void execute(Runnable r) {
		this.THREAD_POOL.execute(r);

	}

	@Override
	public void handleRequest(BasicHttpRequest request, OutputStream out) throws Exception {
		//Alle Handler abfargen (bereits nach Priorität sortiert)
		Set<HttpRequestHandler> handlers = this.HANDLERS.keySet();

		//RequestLine abfragen
		RequestLine line = request.getRequestLine();

		//Angefprderte URI finden und URL erzeugen
		String uriString = "http://localhost" + line.getUri();
		URL url = new URL(uriString);
		
		//Wenn die URI mit / endet wird index.html ergänzt um auf die index.html Datei im Ordner zu verweisen
		if(url.getPath().endsWith("/")) {
			uriString = uriString + "index.html";
			url = new URL(uriString);
			
		}
		

		//Mthode abfragen
		String method = line.getMethod();

		//Antwort anfertigen
		BasicHttpResponse response = this.prepareHttpResponse(out, request);
		
		//Wenn die Methode GET ist behandeln
		if(method.equals("GET")) {
			
			//Alle Handler durchiterieren, wenn einer die Anfrage behandelt hat abbrechen
			for(HttpRequestHandler h : handlers) {
				try {
					if(h.handleGet(url , request, response, this))
						break;
					
				} catch(Exception e) {
					e.printStackTrace();
					
				}
			}
		} else {
			//Alle anderen Methoden sind nicht implementiert -> Fehler Not implemented senden
			response.setStatusCode(501);
			response.setReasonPhrase("Not implemented");
			response.send(0);
			
		}
	}
}
