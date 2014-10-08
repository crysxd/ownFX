$(function() {
  //Add listern to "new frame" button
  $('.frame-new').click(function() {
    if(profile.getFrameCount() >= maxFrameCount) {
      alert('You can not add more than ' + maxFrameCount + ' Frames!');
      return;
      
    }
    
    profileEditor.addNewFrame();
    
  });
  
  //On chnage
  $('#color_stop_color_edit').change(function() {
    var v = $('#color_stop_color_edit').val();
    if(/[0-9a-f]{6}/gi.test(v)) {
      $.farbtastic('#color_stop_color_picker').setColor('#' + v);
      $('#color_stop_color_edit').val(v.toUpperCase());

    }
  });
  
    
  //Save changes of pause time
  $('#frame_pause_time').change(function() {
    profileEditor.getSelectedFrame().pauseTime = $('#frame_pause_time').val();
    profileEditor.updateFrame();
    
  });
  
  //Save changes of transition time
  $('#frame_transition_time').change(function() {
    profileEditor.getSelectedFrame().transitionTime = $('#frame_transition_time').val();
    profileEditor.updateFrame();
    
  });
  
  //Save changes of position
  $('#color_stop_position').change(function() {
    profileEditor.getSelectedColorStop().ledIndex = $('#color_stop_position').val();
    profileEditor.updateFrame();
    
  });
  
  //click code for remove color stop button
  $('#btn_remove_color_stop').click(function(){
    //If two or less color stops are available, thats the minimum.
    //Show error message and return
    if(profileEditor.getSelectedFrame().colorStops.length <= 2) {
      alert("A frame must contain at least two color stop!");
      return;
      
    }
    
    //Remove color stop from array by cutting it out
    //Will contain the indices in correct order
    profileEditor.getSelectedFrame().colorStops.splice(profileEditor.selectedColorStopIndex, 1 );

    profileEditor.selectColorStop(0);
    profileEditor.updateFrame();
    
  });
  
  //click code for remove color stop button
  $('#btn_remove_frame').click(function(){
    //If two or less color stops are available, thats the minimum.
    //Show error message and return
    console.log(profile.getFrameCount());
    if(profile.getFrameCount() <= 1) {
      alert("A profile must contain at least one frame!");
      return;
      
    }
    
    //Remove color stop from array by cutting it out
    //Will contain the indices in correct order
    profile.displayedProfile.frames.splice(profileEditor.selectedFrameIndex, 1 );

    profileEditor.selectedFrameIndex = 0;
    updateUi();
    
  });
  
  //click listener for btn_undo_changes
  $('#btn_undo_changes').click(function() {
    if(confirm('Do you really want to dismiss all changes you\'ve made to this profile?')) {
      profile.restore();
      
    }
  });
  
   //click listener for btn_save
  $('#btn_save').click(function() {
    profile.save();
    
  });
  
   //click listener for btn_activate
  $('#btn_activate').click(function() {
    profile.save(true);
    
  });
  
  //click listener for btn_delete
  $('#btn_delete').click(function() {
    if(confirm('Do you really want to DELETE this profile? This can not be undone.')) {
      profile.delete();
      
      //loadURLAsync(urlProfileDelete + config.id, function(){});
      //$('.dropdown-menu li[profileId=' + config.id + ']').remove();
      //showProfile(0);
      
    }
  });
  
  //click listener for btn_download
  $('#btn_export').click(function() {
    var blob = new Blob([JSON.stringify(profile.displayedProfile)], {type: "application/json"});
    var url = URL.createObjectURL(blob);
    a = $("<a><a/>"); // the id of the <a> element where you will render the download link
    $(a).attr('href', url);
    $(a).attr('download', profile.displayedProfile.name + ".profile");
    $(a)[0].click();
    
  });

  //click listener for btn_rename
  $('#btn_rename').click(function() {
    var newName = prompt('Enter a new name for "' + profile.displayedProfile.name + '":');
    
    if(newName.length > 0) {
      profile.rename(newName);
      
    }
  });
  
  //click listener for btn_import
  $('#btn_import').click(function() {
   profile.import();
    
  });
  
  //click listener for btn_new_profile
  $('#btn_new_profile').click(function() {
    var name = prompt('Enter a new name for the new profile:');
    
    if(name.length > 0) {
    profile.newProfile(name);
      
    }
  });
});

function onColorChanged(color) {
  var colorStop = profile.getColorStop(profileEditor.selectedFrameIndex, profileEditor.selectedColorStopIndex);
  colorStop.color = color;
  $('#color_stop_color_preview').css('background', color);
  $('#color_stop_color_edit').val(color.toUpperCase().substr(1));
  
  profileEditor.updateFrame(profileEditor.selectedFrameIndex);
  
}
