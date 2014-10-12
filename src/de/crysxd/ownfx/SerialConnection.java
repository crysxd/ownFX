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
import java.nio.ByteBuffer;
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
	
	// The dafault polling rate in ns (1ms)
	private final long DEFAULT_POLLING_RATE = 1000000;
	
	/**
	 * Creates a new {@link SerialConnection} on the given serial port with the given baud rate.
	 * @param portName the name of the serial port on which the connection should be opened.
	 * @param baud the baud rate which should be used for the connection
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws IOException
	 */
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
	
	/**
	 * Waits until at least one byte is available in the {@link InputStream}.
	 * @throws IOException
	 */
	public void waitForInput() throws IOException {
		this.waitForInput(this.DEFAULT_POLLING_RATE);
		
	}
	
	/**
	 * Waits until at least one byte is available in the {@link InputStream}.
	 * @param pollingIntervalNs the time interval ins nanoseconds in which the {@link InputStream} is checked for new data
	 * @throws IOException
	 */
	public void waitForInput(long pollingIntervalNs) throws IOException {
		this.waitForInput(pollingIntervalNs, 1);
		
	}
	
	/**
	 * Waits until at least the given number of bytes is available in the {@link InputStream}.
	 * @param pollingIntervalNs the time interval ins nanoseconds in which the {@link InputStream} is checked for new data
	 * @param availableBytes the number of bytes which must be at least available
	 * @throws IOException
	 */
	public void waitForInput(long pollingIntervalNs, int availableBytes) throws IOException {
		/*
		 * IMPORTANT NOTE
		 * =============================================================================
		 * Polling is used here because using Locks/Conditions and the provided listener
		 * interface is causing a fatal JRE Error crashing the program.
		 * To provide instant feedback about new data we need to use busy waiting in 
		 * #waitNanos. Thread.sleep(0) would need about 15ms to 25ms under Windows due to 
		 * content switching and is therefore a heavy bottleneck when reading big amounts
		 * of data. 
		 */
		while(this.getInputStream().available() < availableBytes) {
			this.waitNanos(pollingIntervalNs);
			
		}
	}
	
	/**
	 * Waits the given time in nanoseconds using busy waiting.
	 * @param nanoTime the time in nanoseconds that should be waited
	 */
	private void waitNanos(long nanoTime) {
		long start = System.nanoTime();
		while(start+nanoTime >= System.nanoTime());
		
	}
	
	/**
	 * Reads 8 bits represented by a {@link Byte}.
	 * @return the read {@link Byte}
	 * @throws IOException
	 */
	public byte read8() throws IOException {
		this.waitForInput(this.DEFAULT_POLLING_RATE, 1);
		return (byte) this.getInputStream().read();

	}
	
	/**
	 * Reads 16 bits represented by a {@link Short}.
	 * @return the read {@link Short}
	 * @throws IOException
	 */
	public short read16() throws IOException {
		this.waitForInput(this.DEFAULT_POLLING_RATE, 2);
		return this.read(2).getShort();

	}
	
	/**
	 * Reads 32 bits represented by a {@link Integer}.
	 * @return the read {@link Integer}
	 * @throws IOException
	 */
	public int read32() throws IOException {
		this.waitForInput(this.DEFAULT_POLLING_RATE, 4);
		return this.read(4).getInt();

	}
	
	/**
	 * Reads 64 bits represented by a {@link Long}.
	 * @return the read {@link Long}
	 * @throws IOException
	 */
	public long read64() throws IOException {
		this.waitForInput(this.DEFAULT_POLLING_RATE, 8);
		return this.read(8).getLong();
		
	}
	
	/**
	 * Reads the given number of bytes and returns them wrapped in a {@link ByteBuffer} object.
	 * @param bytes the number of bytes that should be read
	 * @return the read bytes wrapped in a {@link ByteBuffer} object
	 * @throws IOException
	 */
	public ByteBuffer read(int bytes) throws IOException {
		//Creating array to store read data
		byte[] buf = new byte[bytes];
		
		//Readng data
		this.getInputStream().read(buf, 0, bytes);
		
		//Convert to byteBuffer and return
		return ByteBuffer.wrap(buf);
		
	}
	
}
