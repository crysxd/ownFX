package de.crysxd.ownfx;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import java.awt.Color;

import javax.naming.CommunicationException;
import javax.swing.UIManager;

public class Main {
	
	/*
	 * -----------------------------------------------------------------------------------
	 * static
	 */
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}	
		
		new Main();
		
	}
	
	/*
	 * -----------------------------------------------------------------------------------
	 * Non-static
	 */
	
	private final ArduinoCommunicator ARDUINO_COM;
	private String comPort;
	
	public Main() {
		this.ARDUINO_COM = new ArduinoCommunicator();
		Color currentColor = null;
		this.comPort = Ui.queryComPort();
		
		try {
			currentColor = this.ARDUINO_COM.start(this.comPort);
			
		} catch (Exception e) {
			this.handleCommnicationException(e);
			System.exit(1);
		
		} 
		
		try {
			new Ui(this, currentColor);
			
		} catch (Exception e) {
			e.printStackTrace();
			this.ARDUINO_COM.stop();
			Ui.showErrorDialog("Unable to create the user interface.");

		}
	}
	
	public void applyColor(Color c) {
		try {
			this.ARDUINO_COM.sendColor(c);
		} catch (Exception e) {
			this.handleCommnicationException(e);
			
		}
	}
	
	private void handleCommnicationException(Exception e) {
		if(e instanceof PortInUseException) {
			Ui.showErrorDialog("An other application is using '" + this.comPort + "'. Please close this application.");

		} else if(e instanceof CommunicationException || e instanceof UnsupportedCommOperationException) {
			Ui.showErrorDialog("The communication with the Arduino failed.\nPlease upload the correct firmware to the Arduino and check the COM port.");

		} else {
			Ui.showErrorDialog("An error occured while setting up the communication with the Arduino.");

		}
		
		e.printStackTrace();

	}
} 
