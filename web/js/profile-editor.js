var profileEditor = new Object();

profileEditor.selectedFrameIndex      = 0;
profileEditor.selectedColorStopIndex  = 0;

var maxLedIndex = 59;

function updateUi() {
  //Empty configuration
  $('#profile_edit_configuration').html('');

  //iterate over all frames
  $(profile.displayedProfile.frames).each(function(i, e) {
    profileEditor.addNewFrame(e);

  });
    
  //Update displayed name
  profilesList.update();
  
  //Update Sidebar
  profileEditor.updateSidebar();
  
}

//=======================================================================================
profileEditor.addNewFrame = function() {
  //Calculate the next frame index based on the number of existing frames
  var index = this.getFrameNodeCount();

  //Create frame and its connector
  var frame = $('<div class="frame" index="' + index + '"></div>');
  var frameConnector = $('<div class="frame-connector" index="' + index + '"></div>');
  $('#profile_edit_configuration').append(frame, frameConnector);
  
  //Add a click function to the frame to select it on click
  frame.mousedown(function(e) {
    profileEditor.selectFrame(index);
    
    if(profile.getFrame(profileEditor.selectedFrameIndex).colorStops.length < maxColorStopsCount) {
      var ledNumber = profileEditor.pixelsToLedNumber(e.clientX);
      profile.getFrame(profileEditor.selectedFrameIndex).colorStops.push({
        ledIndex: ledNumber,
        color: "#0000FF"
      });
      
      //Update the frame to show the new color stop
      profileEditor.updateFrame(profileEditor.selectedFrameIndex);
      
      //select the newly added color stop, search it first (may be sorted)
      $(profileEditor.getSelectedFrame().colorStops).each(function(i, e) {
        if(ledNumber == e.ledIndex) {
          profileEditor.selectColorStop(i);

        }
      });
    } else {
      alert('You can not add more than ' + maxColorStopsCount + ' color stops in a frame.');
      
    }

  });
  
  //Update gradient and color stops for the new frame
  this.updateFrame(index);

  //Select the first color stop, because the not existent colot stop x could be selected
  this.selectedColorStopIndex = 0;
  
  //Select the new Frame
  this.selectFrame(index);
  
}

//=======================================================================================
profileEditor.updateFrame = function(frameIndex) {
  if(frameIndex == undefined) {
    frameIndex = this.selectedFrameIndex;
    
  }
  
  //Check if frame is in config, if not, create a new empty frame
  if(profile.getFrame(frameIndex) === undefined) {
   profile.displayedProfile.frames[frameIndex] = {pauseTime:0, transitionTime:2000, colorStops:[{ledIndex:0, color:"#0000FF"}]};
    
  }
  
  //Save the frame to be updated
  var frame = profile.getFrame(frameIndex);
  
  //Save the frame node (the div Element representing the frame)
  var frameNode = this.getFrameNode(frameIndex);
  
  //Update the shown times by setting the data-attribute
  frameNode.attr('data-content', makeMsReadable(frame.pauseTime));
  this.getFrameConenctorNode(frameIndex).attr('data-content', makeMsReadable(frame.transitionTime));
  
  //Show ColorStops and gradient
  this.renderColorStops(frameIndex);

}

//=======================================================================================
profileEditor.selectFrame = function(frameIndex) {
  //Cancel if frame is already selected
  if(this.selectedFrameIndex == frameIndex) {
    return;

  }

  //Save index
  this.selectedFrameIndex = frameIndex;

  //Remove the frame-selected class from all frames 
  $('.frame').removeClass('selected');

  //Add it to the newly selected one
  this.getFrameNode(frameIndex).addClass('selected');

  //Set values
  profileEditor.updateSidebar();

  //select the alrady selected color stop
  //this causes the sidebar to update and if the user clicked 
  //directly on a colorstop instead of the frame the color stop
  //will stay selected
  profileEditor.selectColorStop(this.selectedColorStopIndex);

}

//=======================================================================================
profileEditor.selectColorStop = function(colorStopIndex) {
  //If index is undefines assume zero
  if(colorStopIndex == undefined) {
    colorStopIndex = 0;
  }
  
  //Save index
  this.selectedColorStopIndex = colorStopIndex;
    
  //Set values
  profileEditor.updateSidebar();
  
  
  //Update the frame. This will mark the newly selected color stop
  this.updateFrame(this.selectedFrameIndex);

}

//=======================================================================================
profileEditor.getFrameNodeCount = function() {
  return $('#profile_edit_configuration .frame').length;
  
}

//=======================================================================================
profileEditor.getFrameNode = function(frameIndex) {
    return $('#profile_edit_configuration .frame:eq(' + frameIndex + ')');

}

//=======================================================================================
profileEditor.getFrameConenctorNode = function(frameIndex) {
  return $('#profile_edit_configuration .frame-connector:eq(' + frameIndex + ')');

}

//=======================================================================================
profileEditor.getColorStopNode = function(frameIndex, colorStopIndex) {
  return $('#profile_edit_configuration .frame:eq(' + frameIndex + ') .color-stop:eq(' + colorStopIndex + ')');

}

//=======================================================================================
profileEditor.getSelectedFrameNode = function() {
    return $('#profile_edit_configuration .frame:eq(' + this.selectedFrameIndex + ')');

}

//=======================================================================================
profileEditor.getSelectedFrameConenctorNode = function() {
  return $('#profile_edit_configuration .frame-connector:eq(' + this.selectedFrameIndex + ')');

}

//=======================================================================================
profileEditor.getSelectedColorStopNode = function() {
  return $('#profile_edit_configuration .frame:eq(' + this.selectedFrameIndex + ') .color-stop:eq(' + this.selectedColorStopIndex + ')');

}

//=======================================================================================
profileEditor.getSelectedFrame = function() {
  return profile.getFrame(this.selectedFrameIndex);

}

//=======================================================================================
profileEditor.getSelectedColorStop = function() {
  return profile.getColorStop(this.selectedFrameIndex, this.selectedColorStopIndex);

}

//=======================================================================================
profileEditor.renderColorStops = function(frameIndex) {
  //Save the framenode and clear its contents
  var frameNode = this.getFrameNode(frameIndex);  
  frameNode.html('');
    
  //Load color stops
  var colorStops = profile.getFrame(this.selectedFrameIndex).colorStops;

  //If only one color stop is availabel, duplicate it
  //This is necessary because CSS needs min two ColorStops
  //Set the first to the first LED and the second to the last LED
  if(colorStops.length == 1) {
    colorStops[1] = JSON.parse(JSON.stringify(colorStops[0]));
    colorStops[0].ledIndex = 0;
    colorStops[1].ledIndex = maxLedIndex;
    
  }
  
  //Sort color stops
  this.sortColorStops(colorStops);
  
  //Set the gradient
  frameNode.css('background-image', this.buildCssGradient(colorStops));
  
  //Add ColorStop Nodes
  //Iterate over color stops
  $(colorStops).each(function(i, e){ 
    //A a color stop to the HTML to be modified by the user
    profileEditor.addColorStopNode(e.color, e.ledIndex, frameNode, frameIndex, i);
    
  });
  
}


//=======================================================================================
profileEditor.addColorStopNode = function(color, ledNumber, frameNode, frameIndex, colorStopIndex) {
  //Create a color stop node
  var colorStop = $('<div class="color-stop"></div>');
  
  //If the color stop is the selected one (frame and color stop must match)
  if(colorStopIndex == this.selectedColorStopIndex && frameIndex == this.selectedFrameIndex) {
    //remove the selected class from all color stops
    $('.color-stop').removeClass('selected');
    //Add it to the new one
    colorStop.addClass('selected');

  }
  
  //set its position in percent from the left
  colorStop.css('left', this.ledNumberToPercent(ledNumber) + '%');

  //Apply the color
  colorStop.css('background-color', color);
  
  //Add the color stop div to the frame
  frameNode.append(colorStop);
  
  //Add mousedown listener to start drag
  colorStop.mousedown(function(e) {
    $(document).mousemove(profileEditor.onColorStopNodeDragged);
    $('*').css('cursor', 'ew-resize');

    profileEditor.selectColorStop(colorStopIndex);
    profileEditor.selectFrame(frameIndex);
    
    e.stopPropagation();

  });
}

//=======================================================================================
profileEditor.sortColorStops = function ColorStops(colorStops) {
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
        if(j == this.selectedColorStopIndex) {
          this.selectedColorStopIndex=j+1

        } else if(j+1 == this.selectedColorStopIndex) {
           this.selectedColorStopIndex = j;

        }
      }  
    });
  });
}

//=======================================================================================
profileEditor.pixelsToLedNumber = function(pixels) {
  //Get Colorstop node and calculate half width
  var colorStopNode = this.getColorStopNode(this.selectedFrameIndex, this.selectedColorStopIndex);
  var halfWidth = colorStopNode.width() / 2;
  
  //Get Frame Node and Bounds
  var frameNode = this.getFrameNode(this.selectedFrameIndex);
  var minx = frameNode.offset().left - halfWidth;
  var maxx = minx + frameNode.width();

  //Calculate the # LED from x
  return Math.round((pixels - minx) / (maxx - minx) * maxLedIndex);
  
}

//=======================================================================================
profileEditor.ledNumberToPercent = function(ledIndex) {
    //Calculate the position for the HTML elements and the CSS gradient
  return Math.round((ledIndex / maxLedIndex)*10000)/100;
}

//=======================================================================================
profileEditor.buildCssGradient = function(colorStops) {
    //Start to build a CSS gradient
  var gradient = 'linear-gradient(to right, ';
  
  //Iterate over color stops
  $(colorStops).each(function(i, e){
    //Add the stop to the gradient followed by a comma
    gradient += e.color + ' ' + profileEditor.ledNumberToPercent(e.ledIndex) + '%,';
    
  });
    
  //Remove the last comma and add a ) to complete the syntax
  return gradient.substr(0, gradient.length-1) + ')';  
  
}

profileEditor.onColorStopNodeDragged = function(e) {
  //Save nodes
  var frameNode = profileEditor.getFrameNode(profileEditor.selectedFrameIndex);
  var colorStopNode = profileEditor.getColorStopNode(profileEditor.selectedFrameIndex, profileEditor.selectedColorStopIndex);

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
  var ledNumber = profileEditor.pixelsToLedNumber(e.clientX);
  
  //Update the LED number
  profile.getColorStop(profileEditor.selectedFrameIndex, profileEditor.selectedColorStopIndex).ledIndex = ledNumber;
  colorStopNode.css('left', e.clientX - minx + 'px');
  profileEditor.updateFrame(profileEditor.selectedFrameIndex);
  profileEditor.updateSidebar();

}

profileEditor.updateSidebar = function() {
  $('#sidebar_frame_title').html('Frame ' + (this.selectedFrameIndex + 1));
  $('#sidebar_color_stop_title').html('Color Stop ' + (this.selectedColorStopIndex + 1));
  $('#frame_pause_time').val(this.getSelectedFrame().pauseTime);
  $('#frame_transition_time').val(this.getSelectedFrame().transitionTime);
  $('#color_stop_color_edit').val(this.getSelectedColorStop().color);
  $.farbtastic('#color_stop_color_picker').setColor($('#color_stop_color_edit').val());
  $('#color_stop_position').val(this.getSelectedColorStop().ledIndex);
  
}