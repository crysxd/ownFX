package de.crysxd.ownfx.http;

/**
 * Stellt ein Protokoll und dessen Version dar. 
 * Um einen typischen Protokoll-String zu erzeugen wie er im HTTP-Protokoll eingesetzt wird, kann {@link #toString()} verwendet werden.
 * @author chrwuer
 */
public class ProtocolVersion {
	
	//Das Protokoll
	private String protocol;
	
	//Die Hauptversion, bei 1.0 -> 1
	private int minorVersion;
	
	//Die Unterversion, bei 1.0 -> 0
	private int majorVersion;
	
	/**
	 * Erstellt einen neue {@link ProtocolVersion} aus dem gegebenen String.
	 * Der String sollte das Vormat HTTP/1.0 aufweisen, wobei HTTP das verwendete Protokoll, 
	 * 1 die hauptversion und 0 die Unterversion ist.
	 * @param protocolVersion der String der als {@link ProtocolVersion} interpretiert werden soll.
	 * @return die erstellte {@link ProtocolVersion}
	 */
	public static ProtocolVersion parse(String protocolVersion) {
		//Zeile bei / trennen und ersten Teil abspeichern (ergibt das Protocol, z.B. HTTP)
		String protocol = protocolVersion.split("/")[0];
		
		//Zeile bei / trennen und zweiten Teil abspeichern (ergibt Verson, z.B. 1.1)
		String[] version = protocolVersion.split("/")[1].split("\\.");
		
		//Version in Ints casten
		int versionMajor = Integer.valueOf(version[0]);
		int versionMinor = Integer.valueOf(version[1]);
		
		//ProtocolVersion erstellen und zurückgeben
		return new ProtocolVersion(protocol, versionMajor, versionMinor);
		
	}
	
	/**
	 * Erstellt eine neue {@link ProtocolVersion} mit den gegebenen Informationen.
	 * @param protocol das verwendete Protokoll, z.B. HTTP
	 * @param versionMajor die Hauptversion des Protokolls, z.B. 1 bei 1.0
	 * @param versionMinor die Unterversion des Protokolls, z.B. 0 bei 1.0
	 */
	public ProtocolVersion(String protocol, int versionMajor, int versionMinor) {
		this.protocol = protocol;
		this.majorVersion = versionMajor;
		this.minorVersion = versionMinor;
	
	}

	/**
	 * Gibt das verwendete Protokoll zurück, z.B. HTTP.
	 * @return das verwendete Protokoll
	 */
	public String getProtocol() {
		return protocol;
	
	}
	
	/**
	 * Gibt die Unterversion zurück, z.B. 0 bei 1.0
	 * @return
	 */
	public int getMinor() {
		return this.minorVersion;
		
	}
	
	/**
	 * Gibt die Hauptversion zurück, z.B. 1 bei 1.0
	 * @return
	 */
	public int getMajor() {
		return this.majorVersion;
		
	}
	
	@Override
	public String toString() {
		return this.getProtocol() + "/" +  this.getMajor() + "." + this.getMinor(); 
		
	}
}
