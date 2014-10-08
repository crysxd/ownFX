var profile = new Object();

//The URL used to retrieve profiles
profile.urlProfileData   = 'rest/profile?id=';

//The URL used to save profiles
profile.urlProfileSave   = 'rest/save';

//The URl used to delete profiles
profile.urlProfileDelete = 'rest/delete?id=';

//The URL to start a import action
profile.urlProfileImport = 'rest/import';

//The currently displayed profile (object) and it's backup (JSON string)
profile.displayedProfile = undefined;
profile.displayedBackup  = undefined;

/****************************************************************************************
 * Loads the profile with the given ID from the server and displays it.
 */
profile.showProfile = function(id) {
  if(this.checkChange()) {
    if(!confirm(this.checkChange()))
      return;
    
  }
  
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

/****************************************************************************************
 * Returns the number of available frames
 */
profile.getFrameCount = function() {
  //Simply return the number of available frames
  return this.displayedProfile.frames.length;
  
}

/****************************************************************************************
 * Restores the profile to the last saved state. Updates the UI to display changes
 */
profile.restore = function() {
  //Override the profile with the parsed backup
  this.displayedProfile = JSON.parse(this.displayedBackup);
  
  //select the first frame and color stop
  profileEditor.selectedColorStopIndex = 0;
  profileEditor.selectedFrameIndex = 0;
  
  //show the changes
  updateUi();
  
}

/****************************************************************************************
 * Returns the Frame with the given ID
 */
profile.getFrame = function(index) {
  return this.displayedProfile.frames[index];
  
}

/****************************************************************************************
 * Returns the color stop with the given index from the frame with the given index.
 */
profile.getColorStop = function(frameIndex, colorStopIndex) {
  return this.getFrame(frameIndex).colorStops[colorStopIndex];
  
}

profile.import = function() {
   loadURLAsync(this.urlProfileImport, function(state, result) {
      window.location.reload();
      
    });
}

/****************************************************************************************
 * Renames the current profile to the given Name and displays the changes
 */
profile.rename = function(newName) {
  //Save name
  this.displayedProfile.name = newName;
  
  //Update UI
  $('#profiles_selected_name').html(newName);
  $('.dropdown-menu li[profileId=' + profile.displayedProfile.id + '] .name').html(newName);
  $('.dropdown-menu li[profileId=' + profile.displayedProfile.id + ']').attr('profileName', newName);

  
}

/****************************************************************************************
 * SAves the current profile by sending it to the server.
 */
profile.save = function(apply) {
  //Set apply to true or false
  apply = apply != false && apply != undefined;
  
  //Create post data
  var post = 'profile=' + JSON.stringify(this.displayedProfile) + '&apply=' + apply;
  
  //Send POST request
  loadURLAsync(this.urlProfileSave, function() {}, post);
  
  //Save current profile as backup
  profile.displayedBackup = JSON.stringify(this.displayedProfile);
  
}

/****************************************************************************************
 * Deletes the current profile and reloads the webpage
 */
profile.delete = function() {
  loadURLAsync(this.urlProfileDelete + this.displayedProfile.id, function() {
      window.location.reload();
    
  });  
}

/****************************************************************************************
 * Creates a new profile with the given name and sisplays it
 */
profile.newProfile = function(name) {
  //Generate ID (UNIX timestamp)
  var id = new Date().getTime();
  
  //Create empty profile (First frame is added by UI as error handling)
  this.displayedProfile = {id: id, name: name, frames:new Array()};
  
  //Save a backup
  this.displayedBackup = JSON.stringify(this.displayedProfile);
  
  //Add a entry to provide the user the ability to select the newly created project
  profilesList.addEntry(name, id);
  
  //Display the profile
  this.restore();
  
  //Save it
  this.save();
  
}

/****************************************************************************************
 * Returns nothing if there are no unsaved changes and a text if there are changes.
 */
profile.checkChange = function() {
  if(JSON.stringify(profile.displayedProfile) !== profile.displayedBackup) {
    return "If you leave now, unsaved changes will be lost. Continue?";
    
  }
}