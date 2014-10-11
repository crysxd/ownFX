var fieldRamSize = $('#ramSize');
var fieldEepromSize = $('#eepromSize');
var fieldLedCount = $('#ledCount');
var fieldNeopixlesPin = $('#neopixlesPin');
var fieldSerialInterface = $('#serialInterface');
var fieldMaximumFrames = $('#maximumFrames');
var fieldMaximumColorStops = $('#maximumColorStops');
var fieldSystemBrightness = $('#systemBrightness');
var progressbarRam = $('#progressRam');
var progressbarEeprom = $('#progressEeprom');

$(function() {
  settings.load(function() {
    
    //Spinner init
    fieldRamSize.TouchSpin({
      min: 0,
      max: 65536,
      step: 512,
      postfix: 'Bytes'
    });
    fieldEepromSize.TouchSpin({
      min: 0,
      max: 65536,
      step: 512,
      postfix: 'Bytes'
    });
    fieldLedCount.TouchSpin({
      min: 0,
      max: settings.currentSettings.maxPossibleLedCount,
      step: 10,
      postfix: 'LEDs'
    });
    fieldNeopixlesPin.TouchSpin({
      min: 0,
      max: 1024,
      step: 1,
      prefix: 'Pin'
    });
    fieldMaximumFrames.TouchSpin({
      min: 1,
      max: 1024,
      step: 1,
      postfix: 'Frames'
    });
    fieldMaximumColorStops.TouchSpin({
      min: 2,
      max: 1024,
      step: 1,
      postfix: 'Color Stops'
    });
    fieldSystemBrightness.TouchSpin({
      min: 0,
      max: 100,
      step: 1,
      postfix: '%'
    });
    
    //set values
    updateUi();
    
    //add change listener to all select and input elements
    $('input, select').change(function() {
      updateSystemUsage();
      applyChanges();
      
    });
    
    //add listener to save button
    $("#btn_save").click(function() {
      settings.save();
      
    });
    
     //add listener to save button
    $("#btn_undo").click(function() {
      settings.undo();
      updateUi();
      
    });
    
    //Make select element look like bootstrap
    $('select').selectpicker();
    
    //Add unbeforeunload to promt user if changed settings are not saved
    window.onbeforeunload = function() {
      if(settings.isChanged()) {
        return "Some changed values are not saved yet.";
        
      }
    }
    
  });
});

function updateUi() {
  fieldRamSize.val(settings.currentSettings.ramSize);
  fieldEepromSize.val(settings.currentSettings.eepromSize);
  fieldLedCount.val(settings.currentSettings.ledCount);
  fieldNeopixlesPin.val(settings.currentSettings.neopixlesPin);
  fieldMaximumFrames.val(settings.currentSettings.maxFrameCount);
  fieldMaximumColorStops.val(settings.currentSettings.maxColorStopsCount);
  fieldSystemBrightness.val(settings.currentSettings.systemBrightness/2.55);
  
  $(settings.currentSettings.serialInterfaces).each(function(i, e) {
    fieldSerialInterface.append('<option value="' + e + '">' + e + '</option>');
    
  });
  
  fieldSerialInterface.val(settings.currentSettings.serialInterfaceSelected);
  
  updateSystemUsage();
  
}

function updateSystemUsage() {
  var ramUsage = settings.currentSettings.basicRamUsage;
  ramUsage += fieldLedCount.val() * settings.currentSettings.ramUsagePerLed;
  var ramPercent = Math.round(ramUsage / fieldRamSize.val() * 10000) / 100;
  
  var eepromUsage = settings.currentSettings.basicEepromUsage;
  eepromUsage += fieldMaximumFrames.val() * settings.currentSettings.bytesPerFrame;
  eepromUsage += fieldMaximumFrames.val() * fieldMaximumColorStops.val() * settings.currentSettings.bytesPerColorStop;
  var eepromPercent = Math.round(eepromUsage / fieldEepromSize.val() * 10000) / 100;
  
  applyToProgress(progressbarRam, ramPercent, ramUsage);
  applyToProgress(progressbarEeprom, eepromPercent, eepromUsage);

}

function applyToProgress(progressbar, percent, value) {
  progressbar.removeClass('progress-bar-success progress-bar-warning progress-bar-danger');
  progressbar.css('width', percent + '%');
  progressbar.html(value + ' Bytes (' + percent + '%)');
  
  progressbar.addClass('progress-bar-success');
  
 if(percent > 80 && percent <= 95)
    progressbar.addClass('progress-bar-warning');
  
  else if(percent > 95)
      progressbar.addClass('progress-bar-danger');
  
}

function applyChanges() {
  settings.currentSettings.ramSize = fieldRamSize.val();
  settings.currentSettings.eepromSize = fieldEepromSize.val();
  settings.currentSettings.ledCount = fieldLedCount.val();
  settings.currentSettings.neopixlesPin = fieldNeopixlesPin.val();
  settings.currentSettings.maxFrameCount = fieldMaximumFrames.val();
  settings.currentSettings.maxColorStopsCount = fieldMaximumColorStops.val();
  settings.currentSettings.systemBrightness = fieldSystemBrightness.val() * 2.55;
  settings.currentSettings.serialInterfaceSelected = fieldSerialInterface.val();

}