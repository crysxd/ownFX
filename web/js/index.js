var urlProfilesList = 'rest/profiles_list';
var urlProfileData = 'rest/profile';
var ledCount = 60;
var maxFrameCount = 12;
var config;
var configBackup;

$(function() {
  //Load list of all profiles and display them in the sidebar
  loadProfilesList();
  
  //Add listern to "new frame" button
  $('.frame-new').click(function() {
    if(config.frames.length >= maxFrameCount) {
      alert('You can not add more than ' + maxFrameCount + ' Frames!');
      return;
      
    }
    appendFrameToProfileConfiguration();
    
  });
  
  //Spinner init
  $("input[name='time']").TouchSpin({
    min: 0,
    max: 65536,
    step: 100,
    maxboostedstep: 1000,
    postfix: 'ms'
  });
  $("input[name='color_stop']").TouchSpin({
    prefix: "#",
    min: 0,
    verticalbuttons: true,
    verticalupclass: 'glyphicon glyphicon-plus',
    verticaldownclass: 'glyphicon glyphicon-minus',
    max: ledCount
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

        $('#profiles_list').html('');
          
        //iterate over profiles from json object
        $(json).each(function(i, e) {
          //add a list entry for profile
          $('#profiles_list').append('<li profilename="' + e.name + '" profileid="' + e.id + '"><a href="#"><span class="nav-icon glyphicon"></span>'+ e.name + '</a></li>');

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
        //$('#profiles_list').fadeIn();
        showProfile(json[0].id);

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
  var entry = $('#profiles_list li[profileid=' + id + ']');
  $('#profiles_list li').removeClass('active');
  entry.addClass('active');
  $('#profiles_selected_name').html(entry.attr('profilename'));
  
  $('#profile_edit').fadeOut(function() {
    //Fade Loading animation in
    $('#profile_edit_loading').fadeIn(function() {
      //Load information about profile from server, continue in callback
      loadURLAsync(urlProfileData, function(state, result) {
        //If successfull
        if(state == 200) {
          //Transform laoded text in object
          try {
            config = JSON.parse(result);
            configBackup = JSON.parse(result);
            
            //Fade the loading animation and div out
            $('#profile_edit_loading').fadeOut(function() {

              //Set profile's name
              $('#profile_edit_name').html(config.name);

              //Apply current configuration
              applyConfig();

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

function applyConfig() {
  //Empty configuration
  $('#profile_edit_configuration').html('');
  
  //iterate over all frames
  $(config.frames).each(function(i) {
    appendFrameToProfileConfiguration();
    
  });
}

function appendFrameToProfileConfiguration() {
  //Calculate the next frame index based on the number of existing frames
  var index = $('#profile_edit_configuration .frame').length;

  //Create frame and its connector
  var frame = $('<div class="frame" index="' + index + '"></div>');
  var frameConnector = $('<div class="frame-connector" index="' + index + '"></div>');
  $('#profile_edit_configuration').append(frame, frameConnector);
    
  //Check if frame is in config, if not, create a new empty frame
  if(config.frames[index] === undefined) {
   config.frames[index] = {pauseTime:0, transitionTime:2000, colorStops:[{i:0, r:0, g:0, b:255, a:255}]};
    
  }
  
  //Add a click function to the frame to select it on click
  frame.click(function() {
    selectFrame(index);
  });
  
  //Update gradient and color stops for the new frame
  updateFrame(index);
  
  //Select the new Frame
  selectFrame(index);

}

function updateFrame(index) {
  //Save the frame to be updated
  var frame = config.frames[index];
  
  //Save the frame node (the div Element representing the frame)
  var frameNode = getFrameNode(index);
  
  //Update the shown times by setting the data-attribute
  frameNode.attr('data-content', makeMsReadable(frame.pauseTime));
  getFrameConenctorNode(index).attr('data-content', makeMsReadable(frame.transitionTime));
  
  //Show ColorStops and gradient
  renderColorStops(frame.colorStops, index);

}

function getFrameNode(index) {
  return $('#profile_edit_configuration .frame:eq(' + index + ')');
  
}

function getFrameConenctorNode(index) {
  return $('#profile_edit_configuration .frame-connector:eq(' + index + ')');
  
}

function renderColorStops(colorStops, index) {
  //Save the framenode and clear its contents
  var frameNode = getFrameNode(index);  
  frameNode.html('');

  //If only one color stop is availabel, duplicate it
  //This is necessary because CSS needs min two ColorStops
  //Set the first to the first LED and the second to the last LED
  if(colorStops.length == 1) {
    colorStops[1] = JSON.parse(JSON.stringify(colorStops[0]));
    colorStops[0].i = 0;
    colorStops[1].i = ledCount;
    
  }
  
  //Start to build a CSS gradient
  var gradient = 'linear-gradient(to right, ';
  
  //Iterate over color stops
  $(colorStops).each(function(){
    //Calculate the position for the HTML elements and the CSS gradient
    //% with two digits 
    var position = Math.round((this.i / ledCount)*10000)/100;
    
    //Add the stop to the gradient followed by a comma
    gradient += createCssRgb(this.r, this.g, this.b) + ' ' + position + '%,';
    
    //A a color stop to the HTML to be modified by the user
    addColorStop(this.r, this.g, this.b, position, frameNode);
    
  });
  
  //Remove the last comma and add a ) to complete the syntax
  gradient = gradient.substr(0, gradient.length-1) + ')';  
  
  //Set the gradient
  frameNode.css('background-image', gradient);

}

function addColorStop(r, g, b, position, frameNode) {
  //Create a color stop node
  var colorStop = $('<div class="color-stop"></div>');
  
  //set its position in percent from the left
  colorStop.css('left', position + '%');
  
  //Apply the color
  colorStop.css('background-color', createCssRgb(r, g, b));
  
  //Add the color stop div to the frame
  frameNode.append(colorStop);
  
}

function makeMsReadable(ms) {
  //Convert in secs ans add s
  return ms/1000 + 's';
  
}

function createCssRgb(r, g, b) {
  //Create a CSS rgb function with the given values
  return 'rgb(' + r + ',' + g + ',' + b + ')';
  
}

function selectFrame(index) {
  //Remove the frame-selected class from all frames 
  $('.frame').removeClass('frame-selected');
  
  //Add it to the newly selected one
  getFrameNode(index).addClass('frame-selected');
  
  //Set values
  $('#sidebar_title').html('Frame ' + (index + 1));
  $('#frame_pause_time').val(config.frames[index].pauseTime);
  $('#frame_transition_time').val(config.frames[index].transitionTime);
  
  //Save changes of pause time
  $('#frame_pause_time').off('change');
  $('#frame_pause_time').change(function() {
    config.frames[index].pauseTime = $('#frame_pause_time').val();
    updateFrame(index);
    
  });
  
  //Save changes of transition time
  $('#frame_transition_time').off('change');
  $('#frame_transition_time').change(function() {
    config.frames[index].transitionTime = $('#frame_transition_time').val();
    updateFrame(index);
    
  });

}