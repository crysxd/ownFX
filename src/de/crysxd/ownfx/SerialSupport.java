package de.crysxd.ownfx;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SerialSupport {
	
	public static List<CommPortIdentifier> getSerialInterfaces() {
		List<CommPortIdentifier> ports = new ArrayList<CommPortIdentifier>();
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
		
		while (portEnum.hasMoreElements()) {
		    ports.add((CommPortIdentifier) portEnum.nextElement());
		    
		}
		
		return ports;
	}
	
	public static List<String> getSerialInterfaceNames() {
		List<CommPortIdentifier> ports = SerialSupport.getSerialInterfaces();
		ArrayList<String> portNames = new ArrayList<String>();
		
		for(CommPortIdentifier c : ports) {
			portNames.add(c.getName());
		
		}
		
		return portNames;
		
	}
	
	public static CommPortIdentifier getSerialInterface(String name) {
		List<CommPortIdentifier> ports = SerialSupport.getSerialInterfaces();
		
		for(CommPortIdentifier c : ports) {
			if(c.getName().equalsIgnoreCase(name)) {
				return c;
				
			}
		}
		
		return null;
		
	}
}
