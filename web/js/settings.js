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
      max: settings.loadedSettings.maxPossibleLedCount,
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
      
    });
    
    //add listener to save button
    $("#btn_save").click(function() {
      settings.save();
      
    });
    
     //add listener to save button
    $("#btn_undo").click(function() {
      settings.load(updateUi);
      
    });
    
    //Make select element look like bootstrap
    $('select').selectpicker();
    
  });
});

function updateUi() {
  fieldRamSize.val(settings.loadedSettings.ramSize);
  fieldEepromSize.val(settings.loadedSettings.eepromSize);
  fieldLedCount.val(settings.loadedSettings.ledCount);
  fieldNeopixlesPin.val(settings.loadedSettings.neopixlesPin);
  fieldMaximumFrames.val(settings.loadedSettings.maxFrameCount);
  fieldMaximumColorStops.val(settings.loadedSettings.maxColorStopsCount);
  fieldSystemBrightness.val(settings.loadedSettings.systemBrightness/2.55);
  
  $(settings.loadedSettings.serialInterfaces).each(function(i, e) {
    fieldSerialInterface.append('<option value="' + i + '">' + e + '</option>');
    
  });
  
  fieldSerialInterface.val(settings.loadedSettings.serialInterfaceSelected);
  
  updateSystemUsage();
  
}

function updateSystemUsage() {
    console.log('update');

  var ramUsage = settings.loadedSettings.basicRamUsage;
  ramUsage += fieldLedCount.val() * settings.loadedSettings.ramUsagePerLed;
  var ramPercent = Math.round(ramUsage / fieldRamSize.val() * 10000) / 100;
  
  var eepromUsage = settings.loadedSettings.basicEepromUsage;
  eepromUsage += fieldMaximumFrames.val() * settings.loadedSettings.bytesPerFrame;
  eepromUsage += fieldMaximumFrames.val() * fieldMaximumColorStops.val() * settings.loadedSettings.bytesPerColorStop;
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

function save() {
  
}