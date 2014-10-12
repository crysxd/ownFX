package de.crysxd.ownfx;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

import javax.naming.CommunicationException;

public class ArduinoCommunicator {
	
	//The serial connection to communicate with the Arduino
	private final SerialConnection MY_CONNECTION;
	
	private final int TRANSMISSION_STATE_DONE 	= 0;
	private final int TRANSMISSION_STATE_READY	= 1;
	private final int TRANSMISSION_STATE_ERROR 	= 10;

	
	/**
	 * Creates a new {@link ArduinoCommunicator} instance to communicate with the Arduino board connected
	 * to the given serial port.
	 * @param comPort the name of the serial port on which the Arduino is connected
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws IOException
	 * @throws CommunicationException 
	 */
	public ArduinoCommunicator(String comPort) throws PortInUseException, UnsupportedCommOperationException, IOException, CommunicationException {
		//Establish the connection
		this.MY_CONNECTION = new SerialConnection(comPort, 9600);
		System.out.println("Connection established. Waiting for Arduino to reboot...");
		
		/*
		 * IMPORTANT NOTE
		 * =============================================================================
		 * After establishing a new serial connection, Arduino resets itself. This needs
		 * some time and the Arduino is not responding or receiving any data. In order 
		 * to guarantee that the here created instance is working properly after the 
		 * constructor is left we have to wait until the Arduino sends a done signal to 
		 * indicate it is ready.
		 */
		long start = System.currentTimeMillis();
		this.waitForDone();	
		
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
	
	private void waitForDone() throws CommunicationException, IOException  {
		this.waitFor(this.TRANSMISSION_STATE_DONE);
		
	}
	
	private void waitForRady() throws CommunicationException, IOException {
		this.waitFor(this.TRANSMISSION_STATE_DONE);

	}
	
	private void waitFor(int what) throws CommunicationException, IOException {
		this.waitForInput();
		
		int read = 0;
		if((read = this.MY_CONNECTION.getInputStream().read()) != what) {
			throw new CommunicationException("Waited for " + what + ", received " + read);
			
		}
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
