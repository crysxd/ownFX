package de.crysxd.ownfx;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;

import javax.naming.CommunicationException;

public class ArduinoCommunicator {
	
	//The serial connection to communicate with the Arduino
	private final SerialConnection MY_CONNECTION;
	
	private final byte TRANSMISSION_STATE_DONE 			= 0;
	private final byte TRANSMISSION_STATE_READY			= 1;
	private final byte TRANSMISSION_STATE_ERROR			= 10;
	
	private final byte TRANSMISSION_TASK_GET_PROFILE_ID = 0;
	private final byte TRANSMISSION_TASK_SET_PROFILE    = 1;
	private final byte TRANSMISSION_TASK_GET_INFO	    = 2;
	private final byte TRANSMISSION_TASK_SET_SETTINGS   = 3;
	
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
	
	private void waitForDone() throws CommunicationException, IOException  {
		this.waitFor(this.TRANSMISSION_STATE_DONE);
		
	}
	
	private void waitForRady() throws CommunicationException, IOException {
		this.waitFor(this.TRANSMISSION_STATE_DONE);

	}
	
	private void waitFor(byte what) throws CommunicationException, IOException {
		this.MY_CONNECTION.waitForInput();
		
		byte read = 0;
		if((read = this.MY_CONNECTION.read8()) != what) {
			throw new CommunicationException("Waited for " + what + ", received " + read);
			
		}
	}
	
	public void sendSettings(Settings s) {
		throw new RuntimeException("Not implemented!");

	}
	
	public void sendProfile(Profile p) {
		throw new RuntimeException("Not implemented!");

	}
	
	public long getCurrentProfileId() {
		long currentProfileId = 0;
		
		try {
			//Send request
			System.out.println("Send request...");
			this.sendTask(this.TRANSMISSION_TASK_GET_PROFILE_ID, new byte[0]);

			//Read 8 bytes
			System.out.println("Reading...");
			currentProfileId = this.MY_CONNECTION.read64();

			//Wait for Done signal
			System.out.println("Waiting for done...");
			this.waitForDone();
			
		} catch (CommunicationException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return currentProfileId;
		
	}
	
	public void updateSettings(Settings s) {
		
		try {
			//Send request
			System.out.println("Send request...");
			this.sendTask(this.TRANSMISSION_TASK_GET_INFO, new byte[0]);

			//Read 8 bytes
			System.out.println("Reading...");
			s.setRamSize(this.MY_CONNECTION.read16());
			s.setEepromSize(this.MY_CONNECTION.read16());

			//Wait for Done signal
			System.out.println("Waiting for done...");
			this.waitForDone();
			
		} catch (CommunicationException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void sendTask(byte taskId, byte[] data) throws IOException, CommunicationException {
		OutputStream out = this.MY_CONNECTION.getOutputStream();
		
		//Check if the array is too long
		if(data.length > 65536) {
			throw new RuntimeException("Data array is to large. Max: 65536 Byte");
			
		}
		
		//Send task and transmission length
		System.out.println("Writing task...");
		out.write(taskId);
		System.out.println("Writing data length (" + data.length + ")...");
		out.write(0);
		out.write(0);
		
		//Receive the buffer size
		System.out.println("Waiting for buffer size...");
		byte bufferSize = this.MY_CONNECTION.read8();
		System.out.println("Buffer size is " + bufferSize);
		
		//Send data
		short sendBytes = 0;
		while(sendBytes < data.length) {
			System.out.println("Sending " + sendBytes + " to " + (sendBytes + bufferSize) + "...");
			this.waitForRady();
			out.write(data, sendBytes, bufferSize);
			sendBytes += bufferSize;
			
		}
		
		System.out.println("Waiting for done...");
		this.waitForDone();
	}
	
	public void close() {
		this.MY_CONNECTION.close();

	}
}
