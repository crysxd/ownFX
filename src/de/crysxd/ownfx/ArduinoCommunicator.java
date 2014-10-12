package de.crysxd.ownfx;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

public class ArduinoCommunicator {
	
	//The serial connection to communicate with the Arduino
	private final SerialConnection MY_CONNECTION;
	
	/**
	 * Creates a new {@link ArduinoCommunicator} instance to communicate with the Arduino board connected
	 * to the given serial port.
	 * @param comPort the name of the serial port on which the Arduino is connected
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws IOException
	 */
	public ArduinoCommunicator(String comPort) throws PortInUseException, UnsupportedCommOperationException, IOException {
		//Establish the connection
		this.MY_CONNECTION = new SerialConnection(comPort, 9600);
		System.out.println("Connection established. Waiting for Arduino to reboot...");
		
		/*
		 * IMPORTANT NOTE
		 * =============================================================================
		 * After establishing a new serial connection, Arduino resets itself. This needs
		 * some time and the Arduino is not responding or receiving any data. In order 
		 * to guarantee that the here created instance is working properly after the 
		 * constructor is left we have to wait until the Arduino sends a line 
		 * (terminated by \n) to  indicate it is ready.
		 */
		int read = 0;
		long start = System.currentTimeMillis();
		while(read != '\n') {
			this.waitForInput();
			read = this.MY_CONNECTION.getInputStream().read();
			
		}	
		
		//Small information output :)
		System.out.println("    -> Done after " + (System.currentTimeMillis() - start) + " ms");
		
		//Add a shutdownhook to close the connection properly on program exit.
		//This is important under Linux!
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Closing serial connection...");
				ArduinoCommunicator.this.close();
				System.out.println("    -> Done");
				
			}
		});
		
	}
	
	private void waitForInput() throws IOException {
		this.waitForInput(1000000);
		
	}
	
	private void waitForInput(long pollingIntervalNs) throws IOException {
		/*
		 * IMPORTANT NOTE
		 * =============================================================================
		 * Polling is used here because using Locks/Conditions and the provided Listener
		 * Interface is causing a fatal JRE Error crashing the program.
		 * To provide instant feedback about new data we need to use busy waiting in 
		 * #waitNanos. Thread.sleep(0) would need about 15ms to 25ms under Windows due to 
		 * content switching and is therefore a heavy bottleneck when reading big amounts
		 * of data. 
		 */
		while(this.MY_CONNECTION.getInputStream().available() == 0) {
			this.waitNanos(pollingIntervalNs);
			
		}
	}
	
	private void waitNanos(long nanoTime) {
		long start = System.nanoTime();
		while(start+nanoTime >= System.nanoTime());
		
	}

	public void sendSettings(Settings s) {
		throw new RuntimeException("Not implemented!");

	}
	
	public void sendProfile(Profile p) {
		throw new RuntimeException("Not implemented!");

	}
	
	public long getCurrentProfileId() {
		throw new RuntimeException("Not implemented!");
		
	}
	
	public void updateSettings(Settings s) {
		throw new RuntimeException("Not implemented!");

	}
	
	public void close() {
		this.MY_CONNECTION.close();

	}
}
