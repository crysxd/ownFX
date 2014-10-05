var urlProfilesList = 'rest/profiles_list';
var urlProfileData = 'rest/profile';
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
});

function onColorChanged(color) {
  var colorStop = config.frames[selectedFrameIndex].colorStops[selectedColorStopIndex];
  var colorRGB = hexToRgb(color);
  colorStop.r = colorRGB.r;
  colorStop.g = colorRGB.g;
  colorStop.b = colorRGB.b;
  
  $('#color_stop_color_preview').css('background', color);
  $('#color_stop_color_edit').val(color.substr(1).toUpperCase());
  
  updateFrame(selectedFrameIndex);
  
}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}

function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}


function rgbToHex(r, g, b) {
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

function makeMsReadable(ms) {
  //Convert in secs ans add s
  return ms/1000 + 's';
  
}

function createCssRgb(r, g, b) {
  //Create a CSS rgb function with the given values
  return 'rgb(' + r + ',' + g + ',' + b + ')';
  
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
  frame.mousedown(function(e) {
    selectFrame(index);
    
    if(config.frames[selectedFrameIndex].colorStops.length < maxColorStopsCount) {
      var ledNumber = calculateLedFromPixles(e.clientX);
      config.frames[selectedFrameIndex].colorStops.push({
        i: ledNumber,
        r: 0,
        g: 0,
        b: 255,
        a: 0
      });

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
    colorStops[0].i = 0;
    colorStops[1].i = maxLedIndex;
    
  }
  
  //Iterate over color stops
  $(colorStops).each(function(i, e) {
    //if i is the last color stop, break
    if(i == colorStops.length-1)
      return;
    
    //If i and i+1 are located at the same LED
    if(e.i == colorStops[i+1].i) {
      //if the i is the selected color stop (which is aktually dragged by the user), move it over the other color stop
      if(i == selectedColorStopIndex) {
        e.i++;

      //if the i+1 is the selected color stop (which is aktually dragged by the user), move it over the other color stop
      } else if(i+1 == selectedColorStopIndex) {
         colorStops[i+1].i--;

      }
    }
    
    //if i is located after i+1, swapp them
    if(e.i > colorStops[i+1].i) {
      //Swapp
      colorStops[i] = colorStops[i+1];
      colorStops[i+1] = e;
      
      //If i is the selected color stop, now i+1 must be selected and other wise round
      if(i == selectedColorStopIndex) {
        selectedColorStopIndex=i+1
      
      } else if(i+1 == selectedColorStopIndex) {
         selectedColorStopIndex = i;
        
      }
    }  
  });
  
  //Start to build a CSS gradient
  var gradient = 'linear-gradient(to right, ';
  
  //Iterate over color stops
  $(colorStops).each(function(i){
    //Calculate the position for the HTML elements and the CSS gradient
    //% with two digits 
    var position = Math.round((this.i / maxLedIndex)*10000)/100;
    
    //Add the stop to the gradient followed by a comma
    gradient += createCssRgb(this.r, this.g, this.b) + ' ' + position + '%,';
    
    //A a color stop to the HTML to be modified by the user
    addColorStopNode(this.r, this.g, this.b, position, frameNode, index, i);
    
  });
  
  //Remove the last comma and add a ) to complete the syntax
  gradient = gradient.substr(0, gradient.length-1) + ')';  
  
  //Set the gradient
  frameNode.css('background-image', gradient);

}

function addColorStopNode(r, g, b, position, frameNode, frameIndex, colorStopIndex) {
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
  colorStop.css('background-color', createCssRgb(r, g, b));
  
  //Add the color stop div to the frame
  frameNode.append(colorStop);
  
  //Add mousedown listener to start drag
  colorStop.mousedown(function(e) {
        console.log('click on color stop');

    $(document).mousemove(onColorStopNodeDragged);
    $('*').css('cursor', 'ew-resize');
    
    selectColorStop(colorStopIndex);

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
  config.frames[selectedFrameIndex].colorStops[selectedColorStopIndex].i = ledNumber;
  colorStopNode.css('left', e.clientX - minx + 'px');
  updateFrame(selectedFrameIndex);
  $('#color_stop_position').val(config.frames[selectedFrameIndex].colorStops[selectedColorStopIndex].i);


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
  $('#color_stop_position').val(config.frames[selectedFrameIndex].colorStops[index].i);
  $.farbtastic('#color_stop_color_picker').setColor(rgbToHex(colorStop.r, colorStop.g, colorStop.b));
  
  //Update the frame. This will mark the newly selected color stop
  updateFrame(selectedFrameIndex);

  //Save changes of position
  $('#color_stop_position').off('change');
  $('#color_stop_position').change(function() {
    config.frames[selectedFrameIndex].colorStops[index] =   $('#color_stop_position').val();
    updateFrame(selectedFrameIndex);
    
  });
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
  
  //select the alrady selected color stop
  //this causes the sidebar to update and if the user clicked 
  //directly on a colorstop instead of the frame the color stop
  //will stay selected
  selectColorStop(selectedColorStopIndex);
  
}