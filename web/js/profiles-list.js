var profilesList = new Object();

profilesList.urlProfilesList = 'rest/profiles_list';

profilesList.removeEntry = function(id) {
  $('#profiles_list li[profileid=' + id + ']').remove();
  
}

profilesList.addEntry = function(name, id) {
  //add a list entry for profile
  $('#profiles_list').append('<li profilename="' + name + '" profileid="' + id + '"><a href="#"><span class="nav-icon glyphicon"></span><span class="name">' + name + '</span></a></li>');

  //Add click listener to change the displayed project
  $('#profiles_list li:last').click(function() {
    profile.showProfile(id);

  });
  
  //Update automatically
  this.update();
  
}

profilesList.update = function() {
  this.setSelectedProfile();
  
}

profilesList.setSelectedProfile = function(id) {
  if(id == undefined && profile.displayedProfile != undefined)
    id = profile.displayedProfile.id;
  
  if(id == undefined)
    return;
    
  //Remove the blue glow from all and add it to the selected profile
  $('#profiles_list li').removeClass('active');
  $('#profiles_list li[profileid=' + id + ']').addClass('active');
  
  //Show the name of the selected profile
  this.setSelectedName($('#profiles_list li[profileid=' + id + ']').attr('profilename'));

}

profilesList.setSelectedName = function(name) {
    $('#profiles_selected_name').html(name);

}

profilesList.setActiveProfile = function(id) { 
  //Remove the ok glyphicon from all and add it to the active profile
  $('#profiles_list li>a>span').removeClass('glyphicon-cloud');
  $('#profiles_list li[profileid=' + id + ']>a>.nav-icon').addClass('glyphicon-cloud');
  
}

profilesList.load = function(callback) {
  //load URL and go on in callback
  loadURLAsync(this.urlProfilesList, function(state, result) {
    //If successfull
    if(state == 200) {
      //Transform laoded text in object
      var json = JSON.parse(result);

      //Empty the projects list
      $('#profiles_list').html('');

      //iterate over profiles from json object
      $(json).each(function(i, e) {
        profilesList.addEntry(e.name, e.id);

      });

      //report success
      callback(json);

    //If not successfull (404 etc)
    } else {
      alert('The profiles could not be loaded.');
      
    }
  });
}