package de.crysxd.ownfx;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PortUnreachableException;
import java.util.TooManyListenersException;

public class SerialConnection {

	// The idetifier for the used SerialPort
	private final CommPortIdentifier PORT_IDENTIFIER;
	
	// The used SerialPort
	private final SerialPort SERIAL_PORT;
	
	// The InputStream over which data can be received
	private final InputStream INPUT;
	
	// The OutputStream over which data can be send
	private final OutputStream OUTPUT;
	
	public SerialConnection(String portName, int baud) throws 
		PortInUseException, UnsupportedCommOperationException, IOException {
		// Get the port identifier
		this.PORT_IDENTIFIER = SerialSupport.getSerialInterface(portName);
		
		//if the port identifier is null, the requested port is not available
		if(this.PORT_IDENTIFIER == null) {
			throw new PortUnreachableException("The given port " + portName + " is not available");
			
		}
		
		// open serial port, and use class name for the appName.
		this.SERIAL_PORT = (SerialPort) this.PORT_IDENTIFIER.open(this.getClass().getName(), 2000);

		// set port parameters
		this.SERIAL_PORT.setSerialPortParams(baud,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		// open the streams
		this.INPUT = this.SERIAL_PORT.getInputStream();
		this.OUTPUT = this.SERIAL_PORT.getOutputStream();

		// let listeners be notified about new data
		this.SERIAL_PORT.notifyOnDataAvailable(true);
		
	}
	
	/**
	 * Returns the {@link CommPortIdentifier} idetifying the {@link SerialPort} used by this instance.
	 * @return the {@link CommPortIdentifier} idetifying the {@link SerialPort} used by this instance
	 */
	public CommPortIdentifier getPortIdentifier() {
		return this.PORT_IDENTIFIER;
		
	}
	
	/**
	 * Returns the {@link InputStream} over which data can be received over this {@link SerialConnection}.
	 * @return the {@link InputStream} over which data can be received over this {@link SerialConnection}
	 */
	public InputStream getInputStream() {
		return this.INPUT;

	}
	
	/**
	 * Returns the {@link OutputStream} over which data can be send over this {@link SerialConnection}.
	 * @return the {@link OutputStream} over which data can be send over this {@link SerialConnection}
	 */
	public OutputStream getOutputStream() {
		return this.OUTPUT;

	}
	
	/**
	 * Adds a {@link SerialPortEventListener} to the {@link SerialPort} used by this instance. The listener will
	 * be informed when new data is available.
	 * @param listener the {@link SerialPortEventListener} which should be added
	 * @throws TooManyListenersException
	 */
	public void addEventListener(SerialPortEventListener listener) throws TooManyListenersException {
		this.SERIAL_PORT.addEventListener(listener);
		
	}
	
	/**
	 * Removes all {@link SerialPortEventListener}s from the {@link SerialPort} used by this instance.
	 */
	public void removeEventListener() {
		this.SERIAL_PORT.removeEventListener();
		
	}
	
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		this.removeEventListener();
		this.SERIAL_PORT.close();
		
	}
}
