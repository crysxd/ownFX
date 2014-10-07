var profile = new Object();
var profileProtoype = new Object();

profile.urlProfileData   = 'rest/profile?id=';
profile.urlProfileSave   = 'rest/save';
profile.urlProfileDelete = 'rest/delete?id=';
profile.urlProfileImport = 'rest/import';
profile.displayedProfile = undefined;
profile.displayedBackup  = undefined;

profile.showProfile = function(id) {
  //Update profiles list active entry
  profilesList.setSelectedName('Loading...');
  
  //Load information about profile from server, continue in callback
  loadURLAsync(profile.urlProfileData + id, function(state, result) {
      //If successfull
      if(state == 200) {      
        //Set profile's name
        profilesList.update();

        //Apply current configuration
        profile.displayedBackup = result;
        profile.restore();

      //If not successfull (404 etc)
      } else {
        console.log("File not Found.")
        //Error handling!

      }
    });
}

profile.getFrameCount = function() {
  return this.displayedProfile.frames.length;
  
}

profile.restore = function() {
  this.displayedProfile = JSON.parse(this.displayedBackup);
  
  profileEditor.selectedColorStopIndex = 0;
  profileEditor.selectedFrameIndex = 0;
  
  updateUi();
  
}

profile.getFrame = function(index) {
  return this.displayedProfile.frames[index];
  
}

profile.getColorStop = function(frameIndex, colorStopIndex) {
  return this.getFrame(frameIndex).colorStops[colorStopIndex];
  
}

profile.import = function() {
   loadURLAsync(this.urlProfileImport, function(state, result) {
      window.location.reload();
      
    });
}

profile.rename = function(newName) {
  this.displayedProfile.name = newName;
  $('#profiles_selected_name').html(newName);
  $('.dropdown-menu li[profileId=' + profile.displayedProfile.id + '] .name').html(newName);
  
}

profile.save = function(apply) {
  apply = apply != false && apply != undefined;
  var post = 'profile=' + JSON.stringify(this.displayedProfile) + '&apply=' + apply;
  loadURLAsync(this.urlProfileSave, function() {}, post);
  
  //Save current profile as backup
  profile.displayedBackup = JSON.stringify(this.displayedProfile);
  
}

profile.delete = function() {
  loadURLAsync(this.urlProfileDelete + this.displayedProfile.id, function() {
      window.location.reload();
    
  });  
}

profile.newProfile = function(name) {
  var id = new Date().getTime();
  this.displayedProfile = {id: id,name: name, frames:new Array()};
  this.displayedBackup = JSON.stringify(this.displayedProfile);
  
  profilesList.addEntry(name, id);
  this.restore();
  
}