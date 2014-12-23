package de.crysxd.ownfx;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Color;
import java.io.IOException;

import javax.naming.CommunicationException;

public class ArduinoCommunicator {
	
	//The serial connection to communicate with the Arduino
	private SerialConnection myConnection;
	
	private final byte TRANSMISSION_STATE_DONE = 17;
	
	/**
	 * Starts the communicate with the Arduino board
	 * to the given serial port.
	 * @param comPort the name of the serial port on which the Arduino is connected
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws IOException
	 * @throws CommunicationException 
	 * @return the {@link Color} the Arduino send as active color
	 */
	public Color start(String comPort) throws PortInUseException, UnsupportedCommOperationException, IOException, CommunicationException {
		//Close conenction if already established
		if(this.myConnection != null) {
			this.myConnection.close();
		}
		
		//Establish the connection
		this.myConnection = new SerialConnection(comPort, 9600);
		System.out.println("Connection established. Waiting for Arduino to reboot...");
		
		/*
		 * IMPORTANT NOTE
		 * =============================================================================
		 * After establishing a new serial connection, Arduino resets itself. This needs
		 * some time and the Arduino is not responding or receiving any data. In order 
		 * to guarantee that the here created instance is working properly after the 
		 * start function is left we have to wait until the Arduino sends a done signal to 
		 * indicate it is ready.
		 */
		long start = System.currentTimeMillis();
		this.waitForDone();	
		
		//Small information output :)
		System.out.println("    -> Done after " + (System.currentTimeMillis() - start) + " ms");
		
		//Receive current Settings from Arduino
		int r = this.myConnection.read8();
		int g = this.myConnection.read8();
		int b = this.myConnection.read8();
		int a = this.myConnection.read8();	
		
		//Add a shutdownhook to close the connection properly on program exit.
		//This is important under Linux!
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Closing serial connection...");
				ArduinoCommunicator.this.myConnection.close();
				System.out.println("    -> Done");
				
			}
		});
		
		//Create Color and return
		return new Color(r, g, b, a);
		
	}
	
	/**
	 * Send a {@link Color} to the Arduino. The red, green and blue values of the {@link Color} object 
	 * will be used to specify the Color which should be dispalyed by the Arduino. The Alpha value is used for the brigthness.
	 * Fully opaque is the maximum brightness.
	 * @param c the Color which should be displayed by the Arduino
	 * @throws IOException
	 * @throws CommunicationException
	 * @throws InterruptedException 
	 */
	public synchronized void sendColor(Color c) throws IOException, CommunicationException, InterruptedException {
		this.myConnection.getOutputStream().write(c.getRed());
		this.myConnection.getOutputStream().write(c.getGreen());
		this.myConnection.getOutputStream().write(c.getBlue());
		
		/*
		 * IMPORTANT NOTE
		 * =============================================================================
		 * Somehow this sleep is required. The Arduino does not receive the last byte
		 * otherwise causing this program to wait infinitely for the done signal.
		 */
		Thread.sleep(20);
		
		this.myConnection.getOutputStream().write(c.getAlpha());
		this.myConnection.getOutputStream().flush();
		
		System.out.println("Send new Color (" + c + ")");
		System.out.println("Waiting for confirmation...");
		
		waitForDone();
		
		System.out.println("\t-> Received");
		
	}
	
	public void stop() {
		this.myConnection.close();
		this.myConnection = null;
	
	}
	
	private void waitForDone() throws CommunicationException, IOException  {
		this.waitFor(this.TRANSMISSION_STATE_DONE);
		
	}
	
	private void waitFor(byte what) throws CommunicationException, IOException {
		this.myConnection.waitForInput();
		
		int read = 0;
		if((read = this.myConnection.read8()) != what) {
			throw new CommunicationException("Waited for " + what + ", received " + read);
			
		}
	}
}
