package de.crysxd.ownfx;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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
	private final File COM_PORT_FILE = new File(System.getProperty("user.home"), "\\AppData\\Local\\ownFX\\com");
	
	public Main() {
		this.ARDUINO_COM = new ArduinoCommunicator();
		Color currentColor = null;
		this.comPort = null;
		
		//Try to read the com port. If not successfull or if it no longer exists query the user for a new one
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.COM_PORT_FILE));
			comPort = br.readLine();
			br.close();
			
		} catch(Exception e) {
			this.comPort = Ui.queryComPort();
			
			try {
				this.COM_PORT_FILE.getParentFile().mkdirs();
				BufferedWriter bw = new BufferedWriter(new FileWriter(this.COM_PORT_FILE));
				bw.write(this.comPort);
				bw.close();
				
			} catch(Exception e2) {
				Ui.showErrorDialog("Unable to save the COM port. You will be asked again the next time.\nDetailes: " + e2);
				
			}
		}
		
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
