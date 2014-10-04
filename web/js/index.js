var urlProfilesList = 'rest/profiles_list';
var urlProfileData = 'rest/profile';
var ledCount = 60;
var maxFrameCount = 12;
var curConfiguration;
var curConfigurationBackup;

$(function() {
  //Load list of all profiles and display them in the sidebar
  loadProfilesList();
  
  //Add listern to "new frame" button
  $('.frame-new').click(function() {
    if(curConfiguration.frames.length >= maxFrameCount) {
      alert('You can not add more than ' + maxFrameCount + ' Frames!');
      return;
      
    }
    appendFrameToProfileConfiguration();
    
  });
  
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
            $('#profiles_list').append('<li profileid="' + e.id + '"><a href="#"><span class="nav-icon glyphicon"></span>'+ e.name + '</a></li>');

            //check if the current profile is active. if so mark it
            if(e.active) {
              setActiveProfile(e.id);

            }

            //Add click listener to change the displayed project
            $('#profiles_list li:last').click(function() {
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
  $('#profiles_list li>a>span').removeClass('glyphicon-ok');
  $('#profiles_list li[profileid=' + id + ']>a>span').addClass('glyphicon-ok');

}

function showProfile(id) {
  //Update sidebar active entry
  $('#profiles_list li').removeClass('active');
  $('#profiles_list li[profileid=' + id + ']').addClass('active');
  
  $('#profile_edit').fadeOut(function() {
    //Fade Loading animation in
    $('#profile_edit_loading').fadeIn(function() {
      //Load information about profile from server, continue in callback
      loadURLAsync(urlProfileData, function(state, result) {
        //If successfull
        if(state == 200) {
          //Transform laoded text in object
          try {
            curConfiguration = JSON.parse(result);
            curConfigurationBackup = JSON.parse(result);
            
            //Fade the loading animation and div out
            $('#profile_edit_loading').fadeOut(function() {

              //Set profile's name
              $('#profile_edit_name').html(curConfiguration.name);

              //Apply current configuration
              applycurConfiguration();

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

function applycurConfiguration() {
  //Empty configuration
  $('#profile_edit_configuration').html('');
  
  //iterate over all frames
  $(curConfiguration.frames).each(function(i) {
    appendFrameToProfileConfiguration();
    
  });
}

function appendFrameToProfileConfiguration() {
  $('#profile_edit_configuration').append('<div class="frame"></div>');
  $('#profile_edit_configuration').append('<div class="frame-connector"></div>');
  var lastIndex = $('#profile_edit_configuration').children().length/2-1;
  console.log(lastIndex);
  
  if(curConfiguration.frames[lastIndex] === undefined) {
   curConfiguration.frames[lastIndex] = {pauseTime:0, transitionTime:2000, colorStops:[{i:0, r:0, g:0, b:255, a:255}]};
    
  }
  
  updateFrame(lastIndex);

}

function updateFrame(index) {
  var frame = curConfiguration.frames[index];
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
  return gradient;
  
}