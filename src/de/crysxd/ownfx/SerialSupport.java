package de.crysxd.ownfx;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SerialSupport {
	
	public static String[] getSerialInterfaces() {
		
		List<String> ports = new ArrayList<String>();
		Enumeration<?> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		
		while (portIdentifiers.hasMoreElements()) {
		    CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();
		    if(pid.getPortType() == CommPortIdentifier.PORT_SERIAL){
		        ports.add(pid.getName());
		        
		    }
		}
		
		return ports.toArray(new String[ports.size()]);
	}
	
}
