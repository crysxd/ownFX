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

struct Frame {
  uint16_t transitionTime;
  uint16_t pauseTime;
  uint8_t colorStopCount;  

};

struct ColorStop {
  uint16_t ledIndex;
  uint8_t r;
  uint8_t g;
  uint8_t b;

};

Adafruit_NeoPixel* strip       = NULL;
struct Frame* currentFrame     = NULL;
struct ColorStop** colorStops = NULL;
uint8_t currentFrameIndex = 0;

void setup() {
  //Beginn Serial Communication
  Serial.begin(9600);
        
  //If the initialise flag is exactly 42, this application was used before and is initialised
  //If not, we must set some settings
  if(EEPROM.read(EEPROM_INITIALISED_FLAG) != 42) {
    Serial.println("INIT");
     //Set the current profile id to zero
     uint64_t v = 0;
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
  strip->begin();
  
  //Apply red to green gradient
  for(int i=0; i<60; i++) {
    strip->setPixelColor(i, 255*(60-i)/60, 255*i/60, 0);

  }

  //show the gradient
  strip->setBrightness(64);
  strip->show();
  
  //Load current profile
  applyProfile();
  
  //Tell host boot is complete
  sendDone();
  
}

void loop() {

}

void applyProfile() {
  currentFrameIndex = 0;
  loadFrame(currentFrameIndex);

}

void loadFrame(uint8_t index) {
  uint8_t i;
  
  //Create a Frame struct if not allocated
  if(currentFrame == NULL) {
    currentFrame = (struct Frame*) malloc(sizeof(struct Frame));
    currentFrame->transitionTime = 0;
    currentFrame->pauseTime =      0;
    currentFrame->colorStopCount = 0;  
  
  }
  
  uint8_t availableColorStops = currentFrame->colorStopCount;
  
  //Init Frame address with the porfile start
  uint16_t frameAddress = EEPROM_CURRENT_PROFILE_START;
  
  //Iterate over all Frames until the desired one
  for(i=0; i<index; i++) {
    //Read how many ColorStops the Frame contains and multiply it by the size of one ColorStop, add it to Address
    frameAddress += EEPROM.read(frameAddress + 4) * sizeof(struct ColorStop);
    //Add the size of one frameAddress
    frameAddress += sizeof(struct Frame);
    //Now frameAddress is the address of the next Frame
       
  }
  
  //frameAddress is now the address of the desired Frame
  //Read the values
  currentFrame->transitionTime = EEPROM.readInt(frameAddress);
  currentFrame->pauseTime =      EEPROM.readInt(frameAddress+2);
  currentFrame->colorStopCount = EEPROM.read(frameAddress+4);  
  
  /*
  Serial.print("colorStopCount: ");
  Serial.println(currentFrame->colorStopCount);
  Serial.print("transitionTime: ");
  Serial.println(currentFrame->transitionTime);
  Serial.print("pauseTime: ");
  Serial.println(currentFrame->pauseTime);
  Serial.print("availableColorStops: ");
  Serial.println(availableColorStops);
  */
  
  //free all ColorStops
  for(i=0; i<availableColorStops; i++) {
    free(colorStops[i]);
    
  } 
  
  //free colorStops and allocate new
  free(colorStops);
  colorStops = (ColorStop**) malloc(sizeof(struct ColorStop*) * currentFrame->colorStopCount);
  
  //Serial.println("ColorStops: ");

  //Load ColorStops
  for(i=0; i<currentFrame->colorStopCount; i++) {
    colorStops[i] = (ColorStop*) malloc(sizeof(struct ColorStop));
    loadColorStop(i, frameAddress, colorStops[i]);
    
    /*
    Serial.println(i);
    Serial.print("  ledIndex: ");
    Serial.println(colorStops[i]->ledIndex);
    Serial.print("  r: ");
    Serial.println(colorStops[i]->r);
    Serial.print("  g: ");
    Serial.println(colorStops[i]->g);
    Serial.print("  b: ");
    Serial.println(colorStops[i]->b);
    */
    
  }
}

void loadColorStop(uint8_t index, uint16_t eepromFrameAddress, struct ColorStop* colorStop) {
  eepromFrameAddress += sizeof(struct Frame);
  eepromFrameAddress += sizeof(struct ColorStop) * index;
  
  colorStop->ledIndex = EEPROM.readInt(eepromFrameAddress);
  colorStop->r = EEPROM.readInt(eepromFrameAddress+2);
  colorStop->g = EEPROM.readInt(eepromFrameAddress+3);
  colorStop->b = EEPROM.readInt(eepromFrameAddress+4);
  
}

/*
 SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  //Read the task that should be performed
  uint8_t task = read8();
  
  //Read the length of the complete transmission
  uint16_t transmissionLength = read16();
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
  uint16_t buf = RAMEND + 1;
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
  uint64_t id = EEPROMread64(EEPROM_CURRENT_PROFILE_ID);
  writeToSerial(&id, 8);
  
  //Send done to signal of all data send
  sendDone();
  
}

void receiveProfile(int16_t transmissionLength) {
  uint64_t id = read64();
  uint8_t frameCount = read8();
  
  EEPROMwrite64(EEPROM_CURRENT_PROFILE_ID, &id);
  EEPROM.write(EEPROM_CURRENT_PROFILE_FRAME_COUNT, frameCount);
  
  uint16_t receivedCount = 9;
  transmissionLength -= receivedCount;
  
  uint64_t checksum = 0;
  uint8_t buffer;
  
  while(!EEPROM.isReady());
  
  for(uint16_t i=0; i<transmissionLength; i++) {
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

uint8_t read8() {
  uint8_t buffer = 0;
  read(1, (char*)&buffer);
  
  return buffer;
}

uint16_t read16() {
  uint16_t buffer = 0;
  read(2, (char*)&buffer);
  
  return buffer;
}

uint32_t read32() {
  uint32_t buffer = 0;
  read(4, (char*)&buffer);
  
  return buffer;
}

uint64_t read64() {
  uint64_t buffer = 0;
  read(8, (char*)&buffer);
  
  return buffer;
}

void read(uint16_t length, char* target) {
  for(uint16_t i=0; i<length; i++) {
     *(target+i) = readBlocking();
      
   }
}

uint8_t readBlocking() {
  while(Serial.available() <= 0);
  return Serial.read();
  
}

void writeToSerial(void* source, uint16_t length) {
  char* sourceP = (char*) source;

  for(uint16_t i=0; i<length; i++) {
    Serial.write(*(sourceP + i));
    
  } 
}

/*
 *======================================================================================
 * Low Level EEPROM
 *======================================================================================
 */
void EEPROMwrite64(uint16_t eepromAddress, uint64_t* value) {
  uint32_t* source = (uint32_t*) value;
  
  EEPROM.writeLong(eepromAddress, *source);
  EEPROM.writeLong(eepromAddress+4, *(source+1));

}

uint64_t EEPROMread64(uint16_t eepromAddress) {
  uint32_t buf[2];
  
  buf[0] = EEPROM.readLong(eepromAddress);
  buf[1] = EEPROM.readLong(eepromAddress+4);
  
  return *((uint64_t*) buf);
  
}
