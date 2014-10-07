var urlProfilesList = 'rest/profiles_list';
var urlProfileData = 'rest/profile?id=';
var urlProfileSave = 'rest/save';
var urlProfileDelete = 'rest/delete?id=';
var urlProfileImport = 'rest/import';
var maxLedIndex = 59;
var maxFrameCount = 12;
var maxColorStopsCount = 10;
var config;
var configBackup;
var selectedFrameIndex;
var selectedColorStopIndex;

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
   
  //Add mouseup listener to end drag of color stop
  $(document).mouseup(function() {
      $(document).off('mousemove');
      $('*').css('cursor', '');
    
  });
  
  //Spinner init
  $("input[name='time']").TouchSpin({
    min: 0,
    max: 65536,
    step: 100,
    maxboostedstep: 1000,
    postfix: 'ms'
  });
  $("input[name='percent']").TouchSpin({
    postfix: "%",
    min: 0,
    max: 100
  });
    $("input[name='position']").TouchSpin({
    prefix: "LED #",
    min: 0,
    max: maxLedIndex
  });
  
  //Colorpicker init
  $('#color_stop_color_picker').farbtastic(onColorChanged);
  $('#color_stop_color_edit').change(function() {
    $.farbtastic('#color_stop_color_picker').setColor(this.val());
  });
  
    
  //Save changes of pause time
  $('#frame_pause_time').change(function() {
    config.frames[selectedFrameIndex].pauseTime = $('#frame_pause_time').val();
    updateFrame(selectedFrameIndex);
    
  });
  
  //Save changes of transition time
  $('#frame_transition_time').change(function() {
    config.frames[selectedFrameIndex].transitionTime = $('#frame_transition_time').val();
    updateFrame(selectedFrameIndex);
    
  });
  
  //Save changes of position
  $('#color_stop_position').change(function() {
    config.frames[selectedFrameIndex].colorStops[selectedColorStopIndex].ledIndex =   $('#color_stop_position').val();
    updateFrame(selectedFrameIndex);
    
  });
  
  //click code for remove color stop button
  $('#btn_remove_color_stop').click(function(){
    //If two or less color stops are available, thats the minimum.
    //Show error message and return
    if(config.frames[selectedFrameIndex].colorStops.length <= 2) {
      alert("A frame must contain at least two color stop!");
      return;
      
    }
    
    //Remove color stop from array by cutting it out
    //Will contain the indices in correct order
    config.frames[selectedFrameIndex].colorStops.splice(selectedColorStopIndex, 1 );

    selectedColorStopIndex = 0;
    updateFrame(selectedFrameIndex);
    
  });
  
  //click code for remove color stop button
  $('#btn_remove_frame').click(function(){
    //If two or less color stops are available, thats the minimum.
    //Show error message and return
    if(config.frames.length <= 1) {
      alert("A profile must contain at least one frame!");
      return;
      
    }
    
    //Remove color stop from array by cutting it out
    //Will contain the indices in correct order
    config.frames.splice(selectedFrameIndex, 1 );

    selectedColorStopIndex = 0;
    applyConfig();
    
  });
  
  //click listener for btn_undo_changes
  $('#btn_undo_changes').click(function() {
    if(confirm('Do you really want to dismiss all changes you\'ve made to this profile?')) {
      config = JSON.parse(JSON.stringify(configBackup));
      applyConfig();
      rename(config.name);
      
    }
  });
  
   //click listener for btn_save
  $('#btn_save').click(function() {
    saveProfile(this);
  });
  
   //click listener for btn_activate
  $('#btn_activate').click(function() {
    saveProfile(this, true);
    
  });
  
  //click listener for btn_delete
  $('#btn_delete').click(function() {
    if(confirm('Do you really want to DELETE this profile? This can not be undone.')) {
      loadURLAsync(urlProfileDelete + config.id, function(){});
      $('.dropdown-menu li[profileId=' + config.id + ']').remove();
      showProfile(0);
      
    }
  });
  
  //click listener for btn_download
  $('#btn_export').click(function() {
    var blob = new Blob([JSON.stringify(config)], {type: "application/json"});
    var url = URL.createObjectURL(blob);
    a = $("<a><a/>"); // the id of the <a> element where you will render the download link
    $(a).attr('href', url);
    $(a).attr('download', config.name + ".profile");
    $(a)[0].click();
    
  });

  //click listener for btn_rename
  $('#btn_rename').click(function() {
    var newName = prompt('Enter a new name for "' + config.name + '":');
    
    if(newName.length > 0) {
      rename(newName);
      
    }
  });
  
  
  //click listener for btn_import
  $('#btn_import').click(function() {
    loadURLAsync(urlProfileImport, function(state, result) {
      window.location.reload();
      
    });
  });
});

function rename(newName) {
   config.name = newName;
    $('#profiles_selected_name').html(newName);
    $('.dropdown-menu li[profileId=' + config.id + '] .name').html(newName);
  
}

function saveProfile(button, apply) {
  apply = apply != false && apply != undefined;
  var post = 'profile=' + JSON.stringify(config) + '&apply=' + apply;
  loadURLAsync(urlProfileSave, function() {}, post);
  
}

function onColorChanged(color) {
  var colorStop = config.frames[selectedFrameIndex].colorStops[selectedColorStopIndex];
  colorStop.color = color;
  $('#color_stop_color_preview').css('background', color);
  $('#color_stop_color_edit').val(color.toUpperCase().substr(1));
  
  updateFrame(selectedFrameIndex);
  
}

function makeMsReadable(ms) {
  //Convert in secs ans add s
  return ms/1000 + 's';
  
}

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
          $('#profiles_list').append('<li profilename="' + e.name + '" profileid="' + e.id + '"><a href="#"><span class="nav-icon glyphicon"></span><span class="name">'+ e.name + '</span></a></li>');

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
      loadURLAsync(urlProfileData + id, function(state, result) {
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
   config.frames[index] = {pauseTime:0, transitionTime:2000, brightness:255, colorStops:[{i:0, c:"0000FF"}]};
    
  }
  
  //Add a click function to the frame to select it on click
  frame.mousedown(function(e) {
    selectFrame(index);
    
    if(config.frames[selectedFrameIndex].colorStops.length < maxColorStopsCount) {
      var ledNumber = calculateLedFromPixles(e.clientX);
      config.frames[selectedFrameIndex].colorStops.push({
        ledIndex: ledNumber,
        color: "#0000FF"
      });
      
      //select the newly added color stop
      selectedColorStopIndex =  config.frames[selectedFrameIndex].colorStops.length - 1;

      //Update the frame to show the new color stop
      updateFrame(selectedFrameIndex);
      
    } else {
      alert('You can not add more than ' + maxColorStopsCount + ' color stops in a frame.');
      
    }
    


  });
  
  //Update gradient and color stops for the new frame
  updateFrame(index);

  //Select the first color stop, because the not existent colot stop x could be selected
  selectedColorStopIndex = 0;
  
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

function getColorStopNode(frameIndex, colorStopIndex) {
  return $('#profile_edit_configuration .frame:eq(' + frameIndex + ') .color-stop:eq(' + colorStopIndex + ')');
  
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
    colorStops[0].ledIndex = 0;
    colorStops[1].ledIndex = maxLedIndex;
    
  }
  
  //Sort color stops by led number (e.ledIndex) Bubble sort
  $(colorStops).each(function(i, e) {
    
    
    $(colorStops).each(function(j, e) {
      //if j is the last color stop, break
      if(j == colorStops.length-1)
        return;
      
      //if j is located after j+1, swapp them
      if(e.ledIndex > colorStops[j+1].ledIndex) {
        //Swapp
        colorStops[j] = colorStops[j+1];
        colorStops[j+1] = e;

        //Swapp selectedColorIndex, if one of the swapped is selected
        //If j is the selected color stop, now j+1 must be selected and other wise round
        if(j == selectedColorStopIndex) {
          selectedColorStopIndex=j+1

        } else if(j+1 == selectedColorStopIndex) {
           selectedColorStopIndex = j;

        }
      }  
    });
  });
  
  //Start to build a CSS gradient
  var gradient = 'linear-gradient(to right, ';
  
  //Iterate over color stops
  $(colorStops).each(function(i){
    //Calculate the position for the HTML elements and the CSS gradient
    //% with two digits 
    var position = Math.round((this.ledIndex / maxLedIndex)*10000)/100;
    
    //Add the stop to the gradient followed by a comma
    gradient += this.color + ' ' + position + '%,';
    
    //A a color stop to the HTML to be modified by the user
    addColorStopNode(this.color, position, frameNode, index, i);
    
  });
  
  //Remove the last comma and add a ) to complete the syntax
  gradient = gradient.substr(0, gradient.length-1) + ')';  
  
  //Set the gradient
  frameNode.css('background-image', gradient);

}

function addColorStopNode(color, position, frameNode, frameIndex, colorStopIndex) {
  //Create a color stop node
  var colorStop = $('<div class="color-stop"></div>');
  
  //If the color stop is the selected one (frame and color stop must match)
  if(colorStopIndex == selectedColorStopIndex && frameIndex == selectedFrameIndex) {
    //remove the selected class from all color stops
    $('.color-stop').removeClass('selected');
    //Add it to the new one
    colorStop.addClass('selected');

  }
  
  //set its position in percent from the left
  colorStop.css('left', position + '%');
  
  //Apply the color
  colorStop.css('background-color', color);
  
  //Add the color stop div to the frame
  frameNode.append(colorStop);
  
  //Add mousedown listener to start drag
  colorStop.mousedown(function(e) {
        console.log('click on color stop');

    $(document).mousemove(onColorStopNodeDragged);
    $('*').css('cursor', 'ew-resize');
    
    selectColorStop(colorStopIndex);
    selectFrame(frameIndex);
    
    e.stopPropagation();
  });
}

function onColorStopNodeDragged(e) {
  //Save nodes
  var frameNode = getFrameNode(selectedFrameIndex);
  var colorStopNode = getColorStopNode(selectedFrameIndex, selectedColorStopIndex);
  
  //calc min and max x-position
  var halfWidth = colorStopNode.width() / 2;
  var minx = frameNode.offset().left - halfWidth;
  var maxx = minx + frameNode.width();
  
  //If the new x would be smaller than the minimum, assume min
  if(e.clientX < minx) {
    e.clientX = minx;
    
  //If the new x would be bigger than the maximum, assume mac
  } else if(e.clientX > maxx) {
    e.clientX = maxx;
    
  }
  
  //calculate the led number on base of x
  var ledNumber = calculateLedFromPixles(e.clientX);
  
  //Update the LED number
  config.frames[selectedFrameIndex].colorStops[selectedColorStopIndex].ledIndex = ledNumber;
  colorStopNode.css('left', e.clientX - minx + 'px');
  updateFrame(selectedFrameIndex);
  $('#color_stop_position').val(config.frames[selectedFrameIndex].colorStops[selectedColorStopIndex].ledIndex);


}

function calculateLedFromPixles(xPixels) {  
  //Get Colorstop node and calculate half width
  var colorStopNode = getColorStopNode(selectedFrameIndex, selectedColorStopIndex);
  var halfWidth = colorStopNode.width() / 2;
  
  //Get Frame Node and Bounds
  var frameNode = getFrameNode(selectedFrameIndex);
  var minx = frameNode.offset().left - halfWidth;
  var maxx = minx + frameNode.width();

  //Calculate the # LED from x
  return Math.round((xPixels - minx) / (maxx - minx) * maxLedIndex);
  
}



function selectColorStop(index) {
  //If index is undefines assume zero
  if(index == undefined) {
    index = 0;
  }
  
  //Save index
  selectedColorStopIndex = index;
  
  //Set values
  var colorStop = config.frames[selectedFrameIndex].colorStops[index];
  $('#sidebar_color_stop_title').html('Color Stop ' + (index + 1));
  $('#color_stop_position').val(config.frames[selectedFrameIndex].colorStops[index].ledIndex);
  $.farbtastic('#color_stop_color_picker').setColor(colorStop.color);
  
  //Update the frame. This will mark the newly selected color stop
  updateFrame(selectedFrameIndex);

}

function selectFrame(index) {
  //Cancel if frame is already selected
  if(selectedFrameIndex == index) {
    return;
    
  }
  
  //Save index
  selectedFrameIndex = index;
  
  //Remove the frame-selected class from all frames 
  $('.frame').removeClass('selected');
  
  //Add it to the newly selected one
  getFrameNode(index).addClass('selected');
  
  //Set values
  $('#sidebar_frame_title').html('Frame ' + (index + 1));
  $('#frame_pause_time').val(config.frames[index].pauseTime);
  $('#frame_transition_time').val(config.frames[index].transitionTime);
  
  //select the alrady selected color stop
  //this causes the sidebar to update and if the user clicked 
  //directly on a colorstop instead of the frame the color stop
  //will stay selected
  selectColorStop(selectedColorStopIndex);
  
}