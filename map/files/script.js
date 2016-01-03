


 var rec;
 var inforec;
 var recbounds;
 var geocoder;
 var revgeocode;
 var bounds;
 var map;
 var marker;
 var elevator;
 var autocomplete;
 var center_for_map={lat:13.111300,lng:80.095343};
 var places_to_search;
 var gplaces=getgplaces();
var osmplaces=getosmplaces();
var kmls=getkmls();
var min_places;
var kml_arr=new Array();



		function set_gmap()
		{
			map.setMapTypeId(google.maps.MapTypeId.ROADMAP);
			document.getElementById("setgmap").style.visibility="hidden";
		}

		function onload()
		{
		createtoggle();
		createcheckboxes();
		createchk2();
		$('#myModal2').modal('show');
		document.getElementById("loader").style.visibility="hidden";
		
		}


		///ccreate toggle button and listener for kmls
		function createtoggle()
		{ console.log(kml_arr);
			for(var i=0;i<kmls.length;i++)
			{
				var template = $("#kmls_template").html();
				$("#kmls").append(template.replace("{{name}}",kmls[i].name).replace("{{id}}","kml_"+i));
				
				var grnd=$("#kml_"+i);
				grnd.bootstrapSwitch();
				grnd.bootstrapSwitch('offColor','danger');
				grnd.bootstrapSwitch('onColor','success');
				grnd.bootstrapSwitch('size','mini');
				
				
				grnd.on('switchChange.bootstrapSwitch', function(event, state) {
				var i=event.target.id.toString();
				i=i.substring(4);
				if(state) kml_arr[parseInt(i)].setMap(map);
				else kml_arr[parseInt(i)].setMap(null);
			});
			}
		}

		///creating check boxes
		function createcheckboxes(){


			 for(var i=0;i<osmplaces.length;i++)
			 {var template = $("#cboxtemplate").html();
			$("#bbod").append(template.replace(/{{id}}/g,"c_"+i).replace("{{text}}",osmplaces[i].name));
			}
			  $("input[name='fancy-checkbox-default']").change(function(){
			   if($(this).is(':checked'))
				 {//$(this).next().children().first().addClass('btn-info').removeClass('btn-danger');
				  $(this).next().children().addClass("active");}

			  else
				 {
				  $(this).next().children().removeClass("active");
				  //$(this).next().children().first().addClass('btn-danger').removeClass('btn-info');
				  }
			 });
		}
		
		///create model for gmaps features
		function createchk2()
		{
			 for(var i=0;i<gplaces.length;i++)
			 {var template = $("#chkmod2").html();
			$("#mod2").append(template.replace(/{{id}}/g,"gm_"+i).replace("{{text}}",gplaces[i].name));
			}
			  $("input[name='fancy-checkbox']").change(function(){
			   if($(this).is(':checked'))
				 {//$(this).next().children().first().addClass('btn-info').removeClass('btn-danger');
				  $(this).next().children().addClass("active");}

			  else
				 {
				  $(this).next().children().removeClass("active");
				  //$(this).next().children().first().addClass('btn-danger').removeClass('btn-info');
				  }
			 });
		}






		///enter key override
				 $(document).ready(function() {
		  $(window).keydown(function(event){
			if(event.keyCode == 13) {
			  event.preventDefault();
			  return false;
			}
		  });
		});

		///done btn in gmaps selecter
		function setattributes()
		{
			places_to_search=new Array();
			for(var  i=0;i<gplaces.length;i++)
			{
				if(document.getElementById("gm_"+i).checked==true)
				{
					places_to_search.push(gplaces[i]);

				}
			}
			min_places=document.getElementById("n_of_feat").value;
			$('#myModal2').modal('hide');
		}

		///done btn listener
		 function donebtn()
		 {
			
			recbounds=rec.getBounds();
			///revgeocode(geocoder, map,recbounds.getCenter(),"selectedcenter");
			rec.setMap(null);
			inforec.setMap(null);
			bounds=rec.getBounds();
      
			document.getElementById("select").value="select";
			document.getElementById("done").style.visibility="hidden";
			///document.getElementById("info").style.visibility="visible";
			///document.getElementById("selectedcenter").style.visibility="visible";
			document.getElementById("select").setAttribute('class','btn btn-info bttn');
			
			
			
			var send_data=new Array();

			for(var  i=0;i<osmplaces.length;i++)
			{
				if(document.getElementById("c_"+i).checked==true)
				{
					send_data.push(osmplaces[i]);

				}
			}
			document.getElementById("loader").style.visibility="visible";
			document.getElementById("loadertext").innerHTML="Geting data from OSM....";
			$('#myModal').modal('hide');
			var distance_buffer=document.getElementById("buffer").value;

		   $.ajax({
        url:"index.php",
        type:'POST',
		//dataType: "json",
        data:
        {
			id:1,
			places:send_data,
			distance:distance_buffer,
			top:bounds.getNorthEast().lat(),
			left:bounds.getSouthWest().lng(),
			right:bounds.getNorthEast().lng(),
			bottom:bounds.getSouthWest().lat()
        },
        success: function(msg)
        {
		document.getElementById("loader").style.visibility="hidden";
		console.log(msg);
		var obj = JSON.parse(msg);
		if(obj[0].result=="no_records")
			alert("No such attributes in selected region");
		//console.log(obj);
		}
    });
		
		//map overlay
			map.setMapTypeId("OSM");
			map.mapTypes.set("OSM", new google.maps.ImageMapType({
                getTileUrl: function(coord, zoom) {
                    // "Wrap" x (logitude) at 180th meridian properly
                    // NB: Don't touch coord.x because coord param is by reference, and changing its x property breakes something in Google's lib 
                    var tilesPerGlobe = 1 << zoom;
                    var x = coord.x % tilesPerGlobe;
                    if (x < 0) {
                        x = tilesPerGlobe+x;
                    }
                    // Wrap y (latitude) in a like manner if you want to enable vertical infinite scroll
 
                    return "http://tile.openstreetmap.org/" + zoom + "/" + x + "/" + coord.y + ".png";
                },
                tileSize: new google.maps.Size(256, 256),
                name: "OpenStreetMap",
                maxZoom: 18
            }));
			
			document.getElementById("setgmap").style.visibility="visible";
			
			///////////////////////////////

		 }


		 ///select btn listener
		 function selectbtn()
		 {

			if(document.getElementById("select").value=='select')
			{

				document.getElementById("select").setAttribute('class','btn btn-danger bttn');
				document.getElementById("select").value="deselect";
				document.getElementById("done").style.visibility="visible";
        
				var latlan=(marker.getPosition()==null)? {lat:map.getCenter().lat(),lng:map.getCenter().lng()}:{lat:marker.getPosition().lat(),lng:marker.getPosition().lng()};

				///creating rectangle
				var bounds = {
				north: latlan['lat']+((1.0/3600)*(800)/30.0),
				south: latlan['lat']-((1.0/3600)*(800)  /30.0),
				east:  latlan['lng']+((1.0/3600)*(800)    /30.0),
				west:  latlan['lng']-((1.0/3600)*(800)    /30.0)
					};

				rec = new google.maps.Rectangle({
				strokeColor: '#FF0000',
				strokeOpacity: 0.8,
				strokeWeight: 2,
				fillColor: '#FF0000',
				fillOpacity: 0.35,
				map: map,
				bounds: bounds,
				editable:true,
				draggable:true,
				geodesic: true
			  });
			  marker.setPosition(null);


					inforec.setPosition(rec.getBounds().getCenter());
					var SW=rec.getBounds().getSouthWest();
					var NE=rec.getBounds().getNorthEast();
					var NW=new google.maps.LatLng({lat: NE.lat(), lng: SW.lng()});
					var SE=new google.maps.LatLng({lat: SW.lat(), lng: NE.lng()});
					inforec.setContent("<h6><font color='#0099cc'>"+getDistance(NE,NW).toFixed(3)+
										"km</font> * <font color='#e62e00'>"+
										getDistance(NE,SE).toFixed(3)+"km</font></h6>");
					inforec.open(map);



				///bounds change listener
				rec.addListener('bounds_changed', function() {


					SW=rec.getBounds().getSouthWest();
					NE=rec.getBounds().getNorthEast();
					NW=new google.maps.LatLng({lat: NE.lat(), lng: SW.lng()});
					SE=new google.maps.LatLng({lat: SW.lat(), lng: NE.lng()});
					inforec.setContent("<h6><font color='#0099cc'>"+getDistance(NE,NW).toFixed(3)+
										"km</font> * <font color='#e62e00'>"+
										getDistance(NE,SE).toFixed(3)+"km</font></h6>");
					inforec.setPosition(rec.getBounds().getCenter());


				});
			}
      

			else if(document.getElementById("select").value=='deselect')
			{
				rec.setMap(null);
				inforec.setMap(null);
			document.getElementById("select").setAttribute('class','btn btn-info bttn');
			document.getElementById("select").value="select";
			document.getElementById("done").style.visibility="hidden";
			}


        }



		 ///map loader
		function initMap() {



		   ///MAP CREATION
		 map = new google.maps.Map(document.getElementById('map'), {
			center: center_for_map,
			zoom: 14,
			mapTypeId: google.maps.MapTypeId.ROADMAP,
			scaleControl: true,
			rotateControl: true
		  });

		  ///initialize kmls
		  for(var i=0;i<kmls.length;i++)
		  {
			  var kml = new google.maps.KmlLayer({
				url:kmls[i].url,
				map: null,
				preserveViewport: true
			  });
			  kml_arr.push(kml);
		  }

		  


		  ///geocoder initialise
		  geocoder = new google.maps.Geocoder();

		  ///ELEVATOR CREATION
		  elevator = new google.maps.ElevationService;

		  ///info window inilialiser
		  infowindow = new google.maps.InfoWindow({map: null,position:null});
		  inforec = new google.maps.InfoWindow({map:null,position:null});


		  map.controls[google.maps.ControlPosition.TOP_LEFT].push(document.getElementById('address'));


		  ///marker initialize
		  marker = new google.maps.Marker({map: map,position: null,animation: google.maps.Animation.DROP});

		  ///marker click listener
			marker.addListener('click', function(event) {
    display(marker.getPosition(), elevator, infowindow);
	infowindow.setPosition(null);
			infowindow.setContent('<img src="files/gears.gif">');


			 infowindow.open(map, marker);

  });

		  ///marker position changed listener
		  marker.addListener('position_changed', function(event) {
			  infowindow.setMap(null);
			   marker.setAnimation(google.maps.Animation.DROP);
		  });

		  ///marker togglebouncing
			function toggleBounce() {
			  if (marker.getAnimation() !== null) {
				marker.setAnimation(null);
			  } else {
				marker.setAnimation(google.maps.Animation.BOUNCE);
			  }
			}



  /*geocode btn event
  document.getElementById('go').addEventListener('click', function() {
    geocodeAddress(geocoder, map);
	marker.setPosition(null);
  });*/

  ///geocoder
	function geocodeAddress(geocoder, resultsMap) {
	  var address = document.getElementById('address').value;
	  geocoder.geocode({'address': address}, function(results, status) {
		if (status === google.maps.GeocoderStatus.OK) {
		  resultsMap.setCenter(results[0].geometry.location);
		  map.setZoom(15);

		  marker.setPosition(results[0].geometry.location);
		} else {
		  alert('Geocode was not successful for the following reason: ' + status);
		}
	  });
	}

	///reverse geocoder
	revgeocode=function geocodeLatLng(geocoder, map,loc,element) {
	  geocoder.geocode({'location':loc}, function(results, status) {
		if (status === google.maps.GeocoderStatus.OK) {
		  if (results[1]) {
		  document.getElementById(element).value=results[1].formatted_address;
			document.getElementById(element).innerHTML=results[1].formatted_address;
		  } else {
			window.alert('No results found');
		  }
		} else {
		  window.alert('Geocoder failed due to: ' + status);
		}
	  });
	};






	 /// click event on map
	  map.addListener('click', function(event) {
		marker.setAnimation(google.maps.Animation.DROP);
		  marker.setPosition(null);
		  if(map.getMapTypeId()=='OSM')
		  {map.setMapTypeId(google.maps.MapTypeId.ROADMAP);
			document.getElementById("setgmap").style.visibility="hidden";
		  }
		marker.setPosition(event.latLng);
		console.log(event.latLng.toString());

	  revgeocode(geocoder, map,event.latLng,"address");

	  });



	///display in info box items
	function display(location, elevator, infowindow) {
		var str_for_info="";
		var check_if_ele=false;
		toggleBounce();


	// Initiate the location request
	  elevator.getElevationForLocations({'locations': [location]},function(results, status) {

		if (status === google.maps.ElevationStatus.OK) {
		  // Retrieve the first result
		  if (results[0]) {
			str_for_info+="<u><b>Elevation_Of_Land</u></b>"+">"+results[0].elevation +'m.';

		  } else {alert('No elevation results found');}
		} else {alert('Elevation service failed due to: '+status);}




	   ///places search
				var check_all_type_places=0;
			var indez_for_places=0;
			 var places_search_service = new google.maps.places.PlacesService(map);

		  for(indez_for_places=0; indez_for_places<places_to_search.length;indez_for_places++)
		  {
			  search_fun(indez_for_places);
			}


		 function search_fun(indez_for_places)
		  {
		  places_search_service.nearbySearch({
			location:location,
			rankBy:google.maps.places.RankBy.DISTANCE,
			types: [places_to_search[indez_for_places].key]
		  }, function callback(results, status) {

			  check_all_type_places++;
			  str_for_info+="<hr><u><b>"+places_to_search[indez_for_places].name+":</u></b><br>";

			if (status === google.maps.places.PlacesServiceStatus.OK) {
			  var no_of_places=(results.length<min_places)?results.length:min_places; //no of min places
			//console.log("----------"+type+":"+y);
			for (var i = 0; i < no_of_places; i++) {
			  //console.log(results[i].name+">"+getDistance(pyrmont,results[i].geometry.location).toFixed(3)+"km");
			  str_for_info+=results[i].name+">"+getDistance(location,results[i].geometry.location).toFixed(3)+"km<br>";
			}
			}

			if (status === google.maps.places.PlacesServiceStatus.ZERO_RESULTS){
			str_for_info+="No nearby places<br>";

			  }

		  if(check_all_type_places==places_to_search.length)
		  {
			 ///open info
			 //infowindow.setPosition(null);
			 infowindow.setContent(str_for_info);
			 //infowindow.open(map, marker);

		  }

		  });
		}

		});}





		///autocomplete initialize
		var input = (
		document.getElementById('address'));

  		autocomplete = new google.maps.places.Autocomplete(input);

		///autocomplete for address
	   autocomplete.addListener('place_changed', function() {

		marker.setPosition(null);
		var place = autocomplete.getPlace();
		if (!place.geometry) {
		  window.alert("Autocomplete's returned place contains no geometry");
		  return;
		}
		// If the place has a geometry, then present it on a map.
		if (place.geometry.viewport) {
		  map.fitBounds(place.geometry.viewport);
		} else {
		  map.setCenter(place.geometry.location);
		  map.setZoom(15);
		}
		marker.setPosition(place.geometry.location);
		marker.setVisible(true);
	  });





///bounds comment
/*
var allowedBounds = new google.maps.LatLngBounds(
                  new google.maps.LatLng(12.919165,80.196598),
                  new google.maps.LatLng(12.876688, 80.265301)
                );
                var boundLimits = {
                    maxLat : allowedBounds.getNorthEast().lat(),
                    maxLng : allowedBounds.getNorthEast().lng(),
                    minLat : allowedBounds.getSouthWest().lat(),
                    minLng : allowedBounds.getSouthWest().lng()
                };

                var lastValidCenter = map.getCenter();
                var newLat, newLng;
                google.maps.event.addListener(map, 'center_changed', function() {
                    center = map.getCenter();
                    if (allowedBounds.contains(center)) {
                        // still within valid bounds, so save the last valid position
                        lastValidCenter = map.getCenter();
                        return;
                    }
                    newLat = lastValidCenter.lat();
                    newLng = lastValidCenter.lng();
                    if(center.lng() > boundLimits.minLng && center.lng() < boundLimits.maxLng){
                        newLng = center.lng();
                    }
                    if(center.lat() > boundLimits.minLat && center.lat() < boundLimits.maxLat){
                        newLat = center.lat();
                    }
                    map.panTo(new google.maps.LatLng(newLat, newLng));
                });

*/



}///initialise map closing



		function getDistance(p1, p2) {
			  var R = 6378137; // Earth’s mean radius in meter
			  var dLat = rad(p2.lat() - p1.lat());
			  var dLong = rad(p2.lng() - p1.lng());
			  var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(rad(p1.lat())) * Math.cos(rad(p2.lat())) *
				Math.sin(dLong / 2) * Math.sin(dLong / 2);
			  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			  var d = R * c;

			  //d  the distance in meter


			  return d/1000;

			  function rad(x){
				return x * Math.PI / 180;
				}
			}
