/**
 * Loads the contents of the given URL and returns the laoded content. 
 * This funtion is incompatible with IE 6 and older.
 * @param {Object} the URL of the requested resource.
 * @param {Object} the post parameters to send along with the request or null
 * @param {Function} the function to call after the transmission is done. Should have two params: the http status (200, 404...) and the laoded content
 * @return The loaded content.
 */
function loadURLAsync(url, func, postParams) {
  //Create a new XMLHttpRequest
  x = new XMLHttpRequest();
    
  //Set a onreadystatechanged function to be notified about status updates (connection openened, transmission done...)
  x.onreadystatechange=function() {
    //If the new state is done call the given function
    if(x.readyState == 4)
      func(x.status,x.responseText);

  };

  //If post parameters where given
  if(postParams) {
    //Open a connection to the url using HTTP-POST Method.
    //true means the connection is in a background thread
    x.open("POST", url, true);

    //Set content type for POST-Parameters
    x.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');	

    //Send the request
    x.send(postParams);

  } else {
    //Open a connection to the url using HTTP-GET Method.
    //true means the connection is in a background thread
    x.open("GET",url,true);

    //Send the request
    x.send();

  }
}