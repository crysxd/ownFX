#include <EEPROM.h>
#include <stdint>

#define EEPROM_CURRENT_PROFILE_START     64
#define EEPROM_CURRENT_PROFILE_ID         0 // 8 Byte
#define EEPROM_CURRENT_BRIGHTNESS         8 // 1 Byte
#define EEPROM_INITIALISED_FLAG           9 // 1 Byte
#define EEPROM_NEOPIXLES_PIN             10 // 1 Byte
#define EEPROM_LED_COUNT                 11 // 2 Byte

#define TRANSMISSION_TASK_GET_PROFILE_ID       0
#define TRANSMISSION_TASK_SET_PROFILE          1
#define TRANSMISSION_TASK_GET_INFO             2
#define TRANSMISSION_TASK_SET_SETIINGS         3

#define TRANSMISSION_STATE_DONE           0
#define TRANSMISSION_STATE_READY          1
#define TRANSMISSION_STATE_ERROR         10

#if (RAMEND < 1000)
    #define SERIAL_BUFFER_SIZE 16
#else
    #define SERIAL_BUFFER_SIZE 64
#endif


int led = 13;

void setup() {
  //Beginn Serial Communication
  Serial.begin(9600);
  
  //If the initialise flag is exactly 42, this application was used before and is initialised
  //If not, we must set some settings
  if(EEPROM.read(EEPROM_INITIALISED_FLAG) != 42) {
    Serial.println("INIT");
     //Set the current profile id to zero
     int64_t buf = 0;
     copyToEEPROM(&buf, 8, EEPROM_CURRENT_PROFILE_ID);
     
     //Set the led Count to 60
     int16_t buf2 = 60;
     copyToEEPROM(&buf2, 2, EEPROM_LED_COUNT);
     
     //Set the current system brightness to max
     EEPROM.write(EEPROM_CURRENT_BRIGHTNESS, 255);
     
     //Set the neopixels' pin to 6
     EEPROM.write(EEPROM_NEOPIXLES_PIN, 6);
     
     //Set the flag so we wont initialise again
     EEPROM.write(EEPROM_INITIALISED_FLAG, 42);
     
  }
 
  //Tell host boot is complete
  sendDone();
  
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
  //Read the task that should be performed
  int8_t task = read8();
  
  //Read the length of the complete transmission
  int16_t transmissionLength = read16();

  //Send the size of the serial buffer which the partner should use as package size
  Serial.write(SERIAL_BUFFER_SIZE);

  switch(task) {
    case TRANSMISSION_TASK_GET_PROFILE_ID: sendCurrentProfileId();                 break;
    case TRANSMISSION_TASK_SET_PROFILE:    receiveProfile(transmissionLength);     break;
    case TRANSMISSION_TASK_SET_SETIINGS:   receiveSettings(transmissionLength);    break;
    case TRANSMISSION_TASK_GET_INFO:       sendInfo();                             break;
    default:                               Serial.write(TRANSMISSION_STATE_ERROR); break; 
  }
}

void sendInfo() {
  //Send done to signal of all data received
  sendDone();

  //Write the ramsize
  int16_t buf = RAMEND + 1;
  writeToSerial(&buf, 2);
  
  //Write the eeprom size
  buf = E2END + 1;
  writeToSerial(&buf, 2);
  
  //Send done to signal of all data send
  sendDone();
  
}
void sendCurrentProfileId() {  
  //Send done to signal of all data received
  sendDone();
  
  //Copy from eeprom to serial
  for(int16_t address=EEPROM_CURRENT_PROFILE_ID; address<EEPROM_CURRENT_PROFILE_ID+8; address++) {
    Serial.write(EEPROM.read(address));
  }
  
  //Send done to signal of all data send
  sendDone();
  
}

void receiveProfile(int16_t transmissionLength) {
  
}

void receiveSettings(int16_t transmissionLength) {
  //Read data
  int8_t brightness = read8();
  int8_t neopixlesPin = read8();
  int16_t ledCount = read16();
  
  //Save data
  //copyToEEPROM(&ledCount, 2, EEPROM_LED_COUNT);
  //EEPROM.write(EEPROM_CURRENT_BRIGHTNESS, brightness);
  //EEPROM.write(EEPROM_NEOPIXLES_PIN, neopixlesPin);
  
  //Send done
  sendDone();
  
  Serial.write(brightness);
  Serial.write(neopixlesPin);
  writeToSerial(&ledCount, 2);
}


/*
 *======================================================================================
 * Low Level Serial IO
 *======================================================================================
 */
void sendDone() {
  Serial.write(TRANSMISSION_STATE_DONE);

}

void sendReady() {
  Serial.write(TRANSMISSION_STATE_READY);

}

void sendError() {
  Serial.write(TRANSMISSION_STATE_ERROR);

}

int8_t read8() {
  int8_t buffer = 0;
  read(1, (char*)&buffer);
  
  return buffer;
}

int16_t read16() {
  int16_t buffer = 0;
  read(2, (char*)&buffer);
  
  return buffer;
}

int32_t read32() {
  int32_t buffer = 0;
  read(4, (char*)&buffer);
  
  return buffer;
}

int64_t read64() {
  int64_t buffer = 0;
  read(8, (char*)&buffer);
  
  return buffer;
}

void read(int16_t length, char* target) {
  for(int16_t i=0; i<length; i++) {
     *(target+i) = readBlocking();
      
   }
}

int8_t readBlocking() {
  while(Serial.available() <= 0);
  return Serial.read();
  
}

void writeToSerial(void* source, int16_t length) {
  char* sourceP = (char*) source;

  for(int16_t i=0; i<length; i++) {
    Serial.write(*(sourceP + i));
    
  } 
}

void copyToEEPROM(void* source, int16_t length, int16_t eepromAddress) {
  char* sourceP = (char*) source;
  
  for(int16_t i=0; i<length; i++) {
    EEPROM.write(eepromAddress + i, *(sourceP + i));
    
  } 
}
