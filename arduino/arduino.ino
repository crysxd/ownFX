#include <Adafruit_NeoPixel.h>
#include <EEPROMex.h>
#include <EEPROMVar.h>
#include <stdint>

#define EEPROM_CURRENT_PROFILE_START       64
#define EEPROM_CURRENT_PROFILE_ID           0 // 8 Byte
#define EEPROM_CURRENT_BRIGHTNESS           8 // 1 Byte
#define EEPROM_INITIALISED_FLAG             9 // 1 Byte
#define EEPROM_NEOPIXLES_PIN               10 // 1 Byte
#define EEPROM_LED_COUNT                   11 // 2 Byte
#define EEPROM_CURRENT_PROFILE_FRAME_COUNT 13 // 1 Byte

#define TRANSMISSION_TASK_GET_PROFILE_ID       0
#define TRANSMISSION_TASK_SET_PROFILE          1
#define TRANSMISSION_TASK_GET_INFO             2
#define TRANSMISSION_TASK_SET_SETIINGS         3

#define TRANSMISSION_STATE_DONE          17
#define TRANSMISSION_STATE_READY         18
#define TRANSMISSION_STATE_ERROR         69 // 'E'

#if (RAMEND < 1000)
    #define SERIAL_BUFFER_SIZE 16
#else
    #define SERIAL_BUFFER_SIZE 64
#endif

Adafruit_NeoPixel* strip;

void setup() {
  //Beginn Serial Communication
  Serial.begin(9600);
        
  //If the initialise flag is exactly 42, this application was used before and is initialised
  //If not, we must set some settings
  if(EEPROM.read(EEPROM_INITIALISED_FLAG) != 42) {
    Serial.println("INIT");
     //Set the current profile id to zero
     int64_t v = 0;
     EEPROMwrite64(EEPROM_CURRENT_PROFILE_ID, &v);
     
     //Set the led Count to 60
     EEPROM.writeInt(EEPROM_LED_COUNT, 60);
     
     //Set the current system brightness to max
     EEPROM.write(EEPROM_CURRENT_BRIGHTNESS, 255);
     
     //Set the neopixels' pin to 6
     EEPROM.write(EEPROM_NEOPIXLES_PIN, 6);
     
     //Set the flag so we wont initialise again
     EEPROM.write(EEPROM_INITIALISED_FLAG, 42);
     
  }
  
  //Create Neopixels strip and init
  strip = &Adafruit_NeoPixel((uint16_t)60, (uint8_t) 6,(uint8_t)( NEO_GRB + NEO_KHZ800));
  strip.begin();
  
  //Apply red to green gradient
  for(int i=0; i<60; i++) {
    strip.setPixelColor(i, 255*(60-i)/60, 255*i/60, 0);

  }

  //show the gradient
  strip.show();
  
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
  writeToSerial(&transmissionLength, 2);
  
  //DEBUGGING REMOVE
  EEPROM.setMaxAllowedWrites(256);

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
  int64_t id = EEPROMread64(EEPROM_CURRENT_PROFILE_ID);
  writeToSerial(&id, 8);
  
  //Send done to signal of all data send
  sendDone();
  
}

void receiveProfile(int16_t transmissionLength) {
  int64_t id = read64();
  int8_t frameCount = read8();
  
  EEPROMwrite64(EEPROM_CURRENT_PROFILE_ID, &id);
  EEPROM.write(EEPROM_CURRENT_PROFILE_FRAME_COUNT, frameCount);
  
  int16_t receivedCount = 9;
  transmissionLength -= receivedCount;
  
  int64_t checksum = 0;
  int8_t buffer;
  
  while(!EEPROM.isReady());
  
  for(int16_t i=0; i<transmissionLength; i++) {
    if(receivedCount++ == SERIAL_BUFFER_SIZE) {
      sendReady();
      receivedCount = 1;
      
    }
    
    buffer = read8();
    checksum += buffer;
    EEPROM.write(EEPROM_CURRENT_PROFILE_START+i, buffer);
    
  }
  
  sendDone();
  
  writeToSerial(&checksum, 8);
  
  sendDone();
  
}

void receiveSettings(int16_t transmissionLength) {
  //Read and Save data
  EEPROM.write(EEPROM_CURRENT_BRIGHTNESS, read8());
  EEPROM.write(EEPROM_NEOPIXLES_PIN, read8());
  EEPROM.writeInt(EEPROM_LED_COUNT, read16());

  //Send done
  sendDone();

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

/*
 *======================================================================================
 * Low Level EEPROM
 *======================================================================================
 */
void EEPROMwrite64(int16_t eepromAddress, int64_t* value) {
  int32_t* source = (int32_t*) value;
  
  EEPROM.writeLong(eepromAddress, *source);
  EEPROM.writeLong(eepromAddress+4, *(source+1));

}

int64_t EEPROMread64(int16_t eepromAddress) {
  int32_t buf[2];
  
  buf[0] = EEPROM.readLong(eepromAddress);
  buf[1] = EEPROM.readLong(eepromAddress+4);
  
  return *((int64_t*) buf);
  
}
