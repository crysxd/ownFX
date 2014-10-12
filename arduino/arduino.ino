#define EEPROM_PROFILE_START 64
#define EEPROM_CURRENT_PROFILE_ID 0 //4 Byte
#define EEPROM_CURRENT_BRIGHTNESS 4 //1 Byte

#define TRANSMISSION_GET_PROFILE_ID 0
#define TRANSMISSION_SET_PROFILE 1
#define TRANSMISSION_GET_BRIGHTNESS 2
#define TRANSMISSION_SET_BRIGHTNESS 3

int led = 13;

void setup() {
  //Beginn Serial Communication
  Serial.begin(9600);
  
  //Init here
 
  //Tell host boot is complete
  Serial.println("ARDUINO BOOT COMPLETE"); 
  
}

void loop() {

}

/*
 SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  while (Serial.available()) {
    Serial.write(Serial.read());
    
  }
}
