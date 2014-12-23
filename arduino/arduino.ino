#include <Adafruit_NeoPixel.h>
#include <EEPROM.h>
#include <stdint>

#define HARDWARE_NEOPIXELS_PIN    6
#define HARDWARE_NEOPIXELS_COUNT  512

#define EEPROM_COLOR_R           0 // 1 Byte
#define EEPROM_COLOR_G           1 // 1 Byte
#define EEPROM_COLOR_B           2 // 1 Byte
#define EEPROM_BRIGHTNESS        3 // 1 Byte
#define EEPROM_INITIALISED_FLAG  4 // 1 Byte

#define TRANSMISSION_STATE_DONE   17
#define TRANSMISSION_STATE_READY  18
#define TRANSMISSION_STATE_ERROR  69 // 'E'

Adafruit_NeoPixel* strip   = NULL;
uint8_t current_color_r    = 255;
uint8_t current_color_g    = 0;
uint8_t current_color_b    = 0;
uint8_t current_brigthness = 255;

void setup() {
  //Beginn Serial Communication
  Serial.begin(9600);
        
  //Create Neopixels strip and init
  strip = new Adafruit_NeoPixel(HARDWARE_NEOPIXELS_COUNT, HARDWARE_NEOPIXELS_PIN, NEO_GRB + NEO_KHZ800);
  strip->begin(); 

  //If the initialise flag is exactly 42, this application was used before and is initialised
  //If not, we must set some settings
  if(EEPROM.read(EEPROM_INITIALISED_FLAG) != 42) {   
    //Set the current color to red at full brigthness, set init flag
    saveCurrentColorToEEPROM();
    
  }
  
  //Load led count and neopixels pin
  current_color_r    = EEPROM.read(EEPROM_COLOR_R);
  current_color_g    = EEPROM.read(EEPROM_COLOR_G);
  current_color_b    = EEPROM.read(EEPROM_COLOR_B);
  current_brigthness = EEPROM.read(EEPROM_BRIGHTNESS);
  
  //Tell host boot is complete and send current color
  sendDone();
  Serial.write(current_color_r);
  Serial.write(current_color_g);
  Serial.write(current_color_b);
  Serial.write(current_brigthness);
  
}

void loop() {
  //Set color to every pixel
  for(uint16_t i=0; i<strip->numPixels(); i++) {
    strip->setPixelColor(i, current_color_r, current_color_g, current_color_b);
    
  }
  
  //Set brightness
  strip->setBrightness(current_brigthness);
  
  //Show changes
  strip->show();
  
}

void saveCurrentColorToEEPROM() {
  //Save color and brightness
  EEPROM.write(EEPROM_COLOR_R, current_color_r);
  EEPROM.write(EEPROM_COLOR_G, current_color_g);
  EEPROM.write(EEPROM_COLOR_B, current_color_b);
  EEPROM.write(EEPROM_BRIGHTNESS, current_brigthness);
  
  //Set the initialised flag
  EEPROM.write(EEPROM_INITIALISED_FLAG, 42);
  
}

/*
 SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  
  //Read the new color and new brigthness
  current_color_r    = readBlocking();
  current_color_g    = readBlocking();
  current_color_b    = readBlocking();
  current_brigthness = readBlocking();
  
  //send done
  sendDone();

  //save new color and brigthness to EEPROM
  saveCurrentColorToEEPROM();
  
}

/*
 *======================================================================================
 * Low Level Serial IOµ
 *======================================================================================
 */
void sendDone() {
  Serial.write(TRANSMISSION_STATE_DONE);

}

uint8_t readBlocking() {
  while(Serial.available() <= 0);
  return Serial.read();
  
}
