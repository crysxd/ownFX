#define EEPROM_CURRENT_PROFILE_START     64
#define EEPROM_CURRENT_PROFILE_ID         0 // 4 Byte
#define EEPROM_CURRENT_BRIGHTNESS         4 // 1 Byte

#define TRANSMISSION_GET_PROFILE_ID       0
#define TRANSMISSION_SET_PROFILE          1
#define TRANSMISSION_GET_SETTINGS         2
#define TRANSMISSION_SET_SETTINGS         3

#define TRANSMISSION_STATE_DONE           0
#define TRANSMISSION_STATE_READY          1
#define TRANSMISSION_STATE_ERROR         10

#include <stdint>

int led = 13;

void setup() {
  //Beginn Serial Communication
  Serial.begin(9600);
  
  //Init here
 
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
  
  //Send the size of the serial buffer which the partner should use as package size
  Serial.write(SERIAL_BUFFER_SIZE);
  
  //Read the length of the complete transmission
  int16_t transmissionLength = read16();
  
  //Read the length of the complete transmission
  int64_t checksum = read64();  
  
  switch(task) {
    case TRANSMISSION_GET_PROFILE_ID:
    case TRANSMISSION_SET_PROFILE:
    case TRANSMISSION_SET_SETTINGS:
    case TRANSMISSION_GET_SETTINGS:
    default: Serial.write(TRANSMISSION_STATE_ERROR); break; 
  }
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
     *(target+i) = Serial.read();
      
   }
}
