var settings = new Object();

settings.urlSettingsLoad = 'rest/settings';
settings.urlSettingsSave = 'rest/settings_save';
settings.loadedSettings = undefined;

settings.load = function(callback) {
  loadURLAsync(this.urlSettingsLoad, function(state, result) {
    if(state == 200) {
      try {
        settings.loadedSettings = JSON.parse(result);
        settings.maxColorStopsCount = settings.loadedSettings.maxColorStops;
        settings.maxFrameCount = settings.loadedSettings.maxFrameCount;
        settings.maxLedIndex = settings.loadedSettings.ledCount - 1;
        
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

settings.save = function(callback) {
  var postParams = "settings=" + JSON.parse(settings.loadedSettings);
  loadURLAsync(this.urlSettingsSave, function(state, result) {
     if(state == 200) {
      if(callback != undefined) {
        callback();
        
      }
    } else {
      alert("Error while saving settings from server!");
      
    }
  }, postParams); 
}