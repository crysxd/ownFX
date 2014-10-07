var maxLedIndex = 59;
var maxFrameCount = 12;
var maxColorStopsCount = 10;
var maxLedIndex = 59;

$(function() {
  //Load list of all profiles and display the first one
  profilesList.load(function(overview) {
    profile.showProfile(overview[0].id);
    
  });
   
  //Add mouseup listener to end drag of color stop
  $(document).mouseup(function() {
      $(document).off('mousemove');
      $('*').css('cursor', '');
    
  });
  
  //Colorpicker init
  $('#color_stop_color_picker').farbtastic(onColorChanged);
  
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
});

function makeMsReadable(ms) {
  //Convert in secs ans add s
  return ms/1000 + 's';
  
}
