var urlProfilesList = 'rest/profiles_list';
var urlProfileData = 'rest/profile';
var ledCount = 60;
var currentProfileConfiguration;
var currentProfileConfigurationBackup;

$(function() {
  //Load list of all profiles and display them in the sidebar
  loadProfilesList();
  
});

function loadProfilesList() {
  //load URL and go on in callback
  loadURLAsync(urlProfilesList, function(state, result) {
    //If successfull
    if(state == 200) {
      //Transform laoded text in object
      try {
        var json = JSON.parse(result);
       
        //Fade the loading animation out
        $('#profiles_list_loading').fadeOut(function() {
          //iterate over profiles from json object
          $(json).each(function(i, e) {
            //add a list entry for profile
            $('#profiles_list_list').append('<li profileid="' + e.id + '"><a href="#"><span class="nav-icon glyphicon"></span>'+ e.name + '</a></li>');

            //check if the current profile is active. if so mark it
            if(e.active) {
              setActiveProfile(e.id);

            }

            //Add click listener to change the displayed project
            $('#profiles_list_list li:last').click(function() {
              showProfile(e.id);

            });
          });

          //fade list in and sho first
          $('#profiles_list').fadeIn();
          showProfile(json[0].id);

        });
      } catch(e) {
        console.log(e);
        //Error handling!

      }
      
    //If not successfull (404 etc)
    } else {
      console.log("File not Found.")
      //Error handling!
      
    }
  });
}

function setActiveProfile(id) {
  //Remove the ok glyphicon from all and add it to the active profile
  $('#profiles_list_list li>a>span').removeClass('glyphicon-ok');
  $('#profiles_list_list li[profileid=' + id + ']>a>span').addClass('glyphicon-ok');

}

function showProfile(id) {
  //Update sidebar active entry
  $('#profiles_list_list li').removeClass('active');
  $('#profiles_list_list li[profileid=' + id + ']').addClass('active');
  
  $('#profile_edit').fadeOut(function() {
    //Fade Loading animation in
    $('#profile_edit_loading').fadeIn(function() {
      //Load information about profile from server, continue in callback
      loadURLAsync(urlProfileData, function(state, result) {
        //If successfull
        if(state == 200) {
          //Transform laoded text in object
          try {
            currentProfileConfiguration = JSON.parse(result);
            currentProfileConfigurationBackup = JSON.parse(result);
            
            //Fade the loading animation and div out
            $('#profile_edit_loading').fadeOut(function() {

              //Set profile's name
              $('#profile_edit_name').html(currentProfileConfiguration.name);

              //Apply current configuration
              applyCurrentProfileConfiguration();

              //Everything is setup, fade back in
              $('#profile_edit').fadeIn();

            });
          } catch(e) {
            console.log(e);
            //Error handling!

          }

        //If not successfull (404 etc)
        } else {
          console.log("File not Found.")
          //Error handling!

        }
      });
    });
  });
}

function applyCurrentProfileConfiguration() {
  //Empty configuration
  $('#profile_edit_configuration').html('');
  
  //iterate over all frames
  $(currentProfileConfiguration.frames).each(function(i) {
    appendFrameToProfileConfiguration();
    updateFrame(i);
    
  });
}

function appendFrameToProfileConfiguration() {
  $('#profile_edit_configuration').append('<div class="frame"></div>');
  $('#profile_edit_configuration').append('<div class="frame-connector"></div>');
  
}

function updateFrame(index) {
  var frame = currentProfileConfiguration.frames[index];
  var colorStop = frame.colorStops[0];
  $('#profile_edit_configuration .frame:eq(' + index + ')').css('background-image', createGradientFromColorStops(frame.colorStops));
  $('#profile_edit_configuration .frame:eq(' + index + ')').html(makeMsReadable(frame.pauseTime));
  $('#profile_edit_configuration .frame-connector:eq(' + index + ')').attr('data-content', makeMsReadable(frame.transitionTime));

}

function makeMsReadable(ms) {
  return ms/1000 + 's';
  
}

function createGradientFromColorStops(colorStops) {
  var gradient = 'linear-gradient(to right,';
  
  if(colorStops.length == 1) {
    colorStops[1] = colorStops[0];
    
  }
  
  $(colorStops).each(function(){
    gradient += 'rgb(' + this.r + ',' + this.g + ',' + this.b + ') ' + Math.round((this.i / ledCount)*10000)/100 + '%,';
    
  });
  
  gradient = gradient.substr(0, gradient.length-1) + ')';
  console.log(gradient);
  
  return gradient;
  
}