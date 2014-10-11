package de.crysxd.ownfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Settings {
	
	private int ledCount;
	private int neopixlesPin;
	private String[] serialInterfaces;
	private int serialInterfaceSelected;
	private int ramSize;
	private int eepromSize;
	private int maxFrameCount;
	private int maxColorStopsCount;
	private int bytesPerColorStop;
	private int bytesPerFrame;
	private int maxPossibleLedCount;
	private int basicRamUsage;
	private int basicEepromUsage;
	private int ramUsagePerLed;
	private int systemBrightness;
	
	public static Settings readSettings(String json) {
		return GsonSupport.<Settings>parse(json, Settings.class);
		
	}
	
	public static Settings readSettings(InputStream jsonStream) throws IOException {
		return GsonSupport.<Settings>parse(jsonStream, Settings.class);
		
	}
	
	public static Settings readSettings(File jsonFile) throws IOException {
		return Settings.readSettings(new FileInputStream(jsonFile));
		
	}
	
	public int getLedCount() {
		return ledCount;
	
	}
	
	public void setLedCount(int ledCount) {
		this.ledCount = ledCount;
	
	}
	
	public int getNeopixlesPin() {
		return neopixlesPin;
	
	}
	
	public void setNeopixlesPin(int neopixlesPin) {
		this.neopixlesPin = neopixlesPin;
	
	}
	
	public String[] getSerialInterfaces() {
		return serialInterfaces;
	
	}
	
	public void setSerialInterfaces(String[] serialInterfaces) {
		this.serialInterfaces = serialInterfaces;
	
	}
	
	public int getSerialInterfaceSelected() {
		return serialInterfaceSelected;
	
	}
	
	public void setSerialInterfaceSelected(int serialInterfaceSelected) {
		this.serialInterfaceSelected = serialInterfaceSelected;
	
	}
	
	public int getRamSize() {
		return ramSize;
	
	}
	
	public void setRamSize(int ramSize) {
		this.ramSize = ramSize;
	
	}
	
	public int getEepromSize() {
		return eepromSize;
	
	}
	
	public void setEepromSize(int eepromSize) {
		this.eepromSize = eepromSize;
	
	}
	
	public int getMaxFrameCount() {
		return maxFrameCount;
	
	}
	
	public void setMaxFrameCount(int maxFrameCount) {
		this.maxFrameCount = maxFrameCount;
	
	}
	
	public int getMaxColorStopsCount() {
		return maxColorStopsCount;
	
	}
	
	public void setMaxColorStopsCount(int maxColorStopsCount) {
		this.maxColorStopsCount = maxColorStopsCount;
	
	}
	
	public int getBytesPerColorStop() {
		return bytesPerColorStop;
	
	}
	
	public void setBytesPerColorStop(int bytesPerColorStop) {
		this.bytesPerColorStop = bytesPerColorStop;
	
	}
	
	public int getBytesPerFrame() {
		return bytesPerFrame;
	
	}
	
	public void setBytesPerFrame(int bytesPerFrame) {
		this.bytesPerFrame = bytesPerFrame;
	
	}
	
	public int getMaxPossibleLedCount() {
		return maxPossibleLedCount;
	
	}
	
	public void setMaxPossibleLedCount(int maxPossibleLedCount) {
		this.maxPossibleLedCount = maxPossibleLedCount;
	
	}
	
	public int getBasicRamUsage() {
		return basicRamUsage;
	
	}
	
	public void setBasicRamUsage(int basicRamUsage) {
		this.basicRamUsage = basicRamUsage;
	
	}
	
	public int getBasicEepromUsage() {
		return basicEepromUsage;
	
	}
	
	public void setBasicEepromUsage(int basicEepromUsage) {
		this.basicEepromUsage = basicEepromUsage;
	
	}
	
	public int getRamUsagePerLed() {
		return ramUsagePerLed;
	
	}
	
	public void setRamUsagePerLed(int ramUsagePerLed) {
		this.ramUsagePerLed = ramUsagePerLed;
	
	}
	
	public int getSystemBrightness() {
		return systemBrightness;
	
	}
	
	public void setSystemBrightness(int systemBrightness) {
		this.systemBrightness = systemBrightness;
	
	}
}
