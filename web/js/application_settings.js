var settings = new Object();

settings.urlSettingsLoad = 'rest/settings';
settings.urlSettingsSave = 'rest/settings_save';
settings.currentSettings = undefined;
settings.currentSettingsBackup = undefined;

settings.load = function(callback) {
  loadURLAsync(this.urlSettingsLoad, function(state, result) {
    if(state == 200) {
      try {
        settings.currentSettings = JSON.parse(result);
        settings.currentSettingsBackup = JSON.stringify(settings.currentSettings);
        settings.maxColorStopsCount = settings.currentSettings.maxColorStops;
        settings.maxFrameCount = settings.currentSettings.maxFrameCount;
        settings.maxLedIndex = settings.currentSettings.ledCount - 1;
        
        if(callback != undefined) {
          callback();

        }
      } catch(e) {
        console.log(e);
        alert("Error while loading settings from server!");

      }
    } else {
      alert("Error while loading settings from server!");
      
    }
  });
}

settings.undo = function() {
  settings.currentSettings = JSON.parse(settings.currentSettingsBackup);
  
}

settings.save = function(callback) {
  var postParams = "settings=" + JSON.stringify(settings.loadedSettings);
  loadURLAsync(this.urlSettingsSave, function(state, result) {
     if(state == 200) {
       settings.currentSettingsBackup = JSON.stringify(settings.currentSettings);
       
      if(callback != undefined) {
        callback();
        
      }
    } else {
      alert("Error while saving settings!");
      
    }
  }, postParams); 
}

settings.isChanged = function() {
  return !(settings.currentSettingsBackup === JSON.stringify(settings.currentSettings));
  
}