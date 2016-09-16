 var rec;
 var inforec;
 var infosel;
 var infolatlng;
 var recbounds;
 var geocoder;
 var revgeocode;
 var bounds;
 var map;
 var marker;
 var markers=[];
 var elevator;
 var autocomplete;
 var center_for_map={lat:13.010029,lng:80.235347};
 var places_to_search;
  var layers_to_find;
  var layers_names_find;
 var gplaces=gplaces;
 var osmplaces=osmplaces;
 var kmls=kmls;
 var min_places;
 var each_place_list;
 var each_place_list_str;

 var kml_arr=new Array();
 var shp_arr=new Array(); //url of shpe files
 var key_arr=new Array();	//table names of layers which have shape file
 var nam_arr=new Array();//display name of layers which have shape file
 var names_in_table=new Array();//names  in each table got back fromdb for drop down list box


 



		//clear and return button
		function set_gmap()
		{


			map.setMapTypeId(google.maps.MapTypeId.ROADMAP);
			document.getElementById("setgmap").style.visibility="hidden";
			clearmarkers();


			
			
		}

		function onload()
		{
			if (navigator.onLine) {
			createtoggle();
			createcheckboxes();
			createchk2();
			$('#myModal2').modal('show');
			document.getElementById("loader").style.visibility="hidden";
			} else {
					swal({
					 title: "No internet to load map!!!",
					 type: "error",
					 showConfirmButton: false,
					 allowEscapeKey:false
						});
					document.getElementById("loadertext").innerHTML="";
					}
		
		//swal("Oops...", "Something went wrong!", "error");
		
		}


		///ccreate toggle button and listener for kmls
		function createtoggle()
		{ //console.log(kml_arr);
			//var 
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
			$("#bbod2").append('<div class="row"><div class="[ col-xs-5 ]" ><b><h4 class="modal-title">&nbsp &nbsp Features</h4></b><hr style="background-color: #337ab7; height: 1px; border: 0;"></div></div>');
			

			 for(var i=0;i<osmplaces.length;i++)
			 {
			 	

			 	var template = $("#cboxtemplate").html();
		 
				if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) 
				{
				$("#bbod2").append(template.replace(/{{id}}/g,"c_"+i).replace("{{text}}",osmplaces[i].name).replace('{{class}}','[ col-xs-12 ]'));
				}
				
				else
				{2
				$("#bbod2").append(template.replace(/{{id}}/g,"c_"+i).replace("{{text}}",osmplaces[i].name).replace('{{class}}','[ col-xs-6 ]'));
				}
			
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
			  create_dropdown_box_layers();
		}
		

		///create dropdown list for layers search
		function create_dropdown_box_layers()
		{
			$("#bbod3").append('<div class="row"><div class="[ col-xs-5 ]" ><b><h4 class="modal-title">&nbsp &nbsp Layers</h4></b><hr style="background-color: #337ab7; height: 1px; border: 0;"></div></div>');
			var template = $("#searchlayers").html();
		 	for(var i=0;i<nam_arr.length;i++)
		 	{
				if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) 
				{
				$("#bbod3").append(template.replace(/{{id}}/g,"ser_lay_"+i).replace('{{class}}','[ col-xs-12 ]'));
				}
				
				else
				{
				$("#bbod3").append(template.replace(/{{id}}/g,"ser_lay_"+i).replace('{{class}}','[ col-xs-6 ]'));
				}

				 $("#ser_lay_"+i).attr("title",nam_arr[i]);

				 for(var j=0;j<names_in_table[i].length;j++)
				 {
				 		$("#ser_lay_"+i).append($('<option></option>').attr("value",names_in_table[i][j]).text(names_in_table[i][j]));
				 }


			}

				 $('.selectpicker').selectpicker({
				  style: 'btn-primary btn-md bttn',
				  size: 5,
				  selectedTextFormat:'static',
				  //showContent:'auto'
				});

		}
		///create model for gmaps features
		function createchk2()
		{
			$("#mod2").append('<div class="row"><div class="[ col-xs-5 ]" ><b><h4 class="modal-title">&nbsp &nbsp Features</h4></b><hr style="background-color: red; height: 1px; border: 0;"></div></div>');
			
			 for(var i=0;i<gplaces.length;i++)
			 {var template = $("#chkmod2").html();
		 
			if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) 
				{
				 $("#mod2").append(template.replace(/{{id}}/g,"gm_"+i).replace("{{text}}",gplaces[i].name).replace('{{class}}','[ col-xs-12 ]'));
				}
			else{
				$("#mod2").append(template.replace(/{{id}}/g,"gm_"+i).replace("{{text}}",gplaces[i].name).replace('{{class}}','[ col-xs-6 ]'));
			}
			}
			
		
			 createchklayers();
		}
		
		function createchklayers()
		{
			$("#mod3").append('<div class="row"><div class="[ col-xs-5 ]" ><b><h4 class="modal-title">&nbsp &nbsp Layers</h4></b><hr style="background-color:#337ab7; height: 1px; border: 0;"></div></div>');
			
			 for(var i=0;i<nam_arr.length;i++)
			 {var template = $("#chk_layers").html();
		 
			if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) 
				{
				 $("#mod3").append(template.replace(/{{id}}/g,"ly_"+i).replace("{{text}}",nam_arr[i]).replace('{{class}}','[ col-xs-12 ]'));
				}
			else{
				$("#mod3").append(template.replace(/{{id}}/g,"ly_"+i).replace("{{text}}",nam_arr[i]).replace('{{class}}','[ col-xs-6 ]'));
			}
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
			  //console.log('enter');
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
			
			layers_to_find=new Array();
			layers_names_find=new Array();
			for(var  i=0;i<nam_arr.length;i++)
			{
				if(document.getElementById("ly_"+i).checked==true)
				{
					layers_to_find.push(key_arr[i]);
					layers_names_find.push(nam_arr[i]);

				}
			}
			
			
			min_places=document.getElementById("n_of_feat").value;
			$('#myModal2').modal('hide');
		}

		///done btn listener
		




		 function donebtn()
		{	
			var send_data=new Array();
			var distance_buffer=document.getElementById("buffer").value;

			for(var  i=0;i<osmplaces.length;i++)
			{
				if(document.getElementById("c_"+i).checked==true)
				{
					send_data.push(osmplaces[i]);

				}
			}

			var snd_table_name="";
			var snd_table_req_values="";

			for(var i=0;i<nam_arr.length;i++)
			{
				var tem=new Array();
				tem=$('#ser_lay_'+i).val();

				if(tem!=null)
				{
					var str_tem="";
					for(var k=0;k<tem.length;k++)
					{
						str_tem+="''"+tem[k]+"'',"
					}
			
					str_tem=str_tem.substring(0,str_tem.length-1);
					snd_table_name+="'"+key_arr[i]+"',";
					snd_table_req_values+="'"+str_tem+"',";
				}
			}
			snd_table_name=snd_table_name.substring(0,snd_table_name.length-1);
			snd_table_req_values=snd_table_req_values.substring(0,snd_table_req_values.length-1);

			

			if((send_data.length!=0 && distance_buffer!="")||snd_table_name.length!=0)
			{

			var ret=0;
			if(document.getElementById("ret").checked==true)ret=1;
			marker.setPosition(null);
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
			
			
			
			
			
			

			
			$('#myModal').modal('hide');
			clearmarkers();

			markers=[];
			
			if(snd_table_name.length!=0)
			{
				var data= {
			id:1,
			places:send_data,
			distance:distance_buffer,
			top:bounds.getNorthEast().lat(),
			left:bounds.getSouthWest().lng(),
			right:bounds.getNorthEast().lng(),
			bottom:bounds.getSouthWest().lat(),
			tables:snd_table_name,
			tables_val:snd_table_req_values,
			ret:ret
        		}
			}
			else
			{
				var data= {
			id:1,
			places:send_data,
			distance:distance_buffer,
			top:bounds.getNorthEast().lat(),
			left:bounds.getSouthWest().lng(),
			right:bounds.getNorthEast().lng(),
			bottom:bounds.getSouthWest().lat(),
			ret:ret
        	}
			}
			console.log(data);

			//////to calc time
			var req_sent_tim=new Date().getTime()/1000;
			console.log("req sent time: "+req_sent_tim);

		   $.ajax({
			        url:"index.php",
			        type:'POST',
					//dataType: "json",
			        data:data,
			        success: function(msg)
			        {	var req_rec_time=new Date().getTime()/1000;
			        	console.log("req rece time: "+req_rec_time);
			        	console.log("time taken se: "+(req_rec_time-req_sent_tim)+"sec");

			        	console.log(msg);
						document.getElementById("loader").style.visibility="hidden";
						
						var obj = JSON.parse(msg);
						
						if(obj.result=="no_records")
							swal("Ooops!!","No map attributes in selected region!!","info");
						if(obj.points.length==0)
							swal("Sorry!!","No palces found as you wish!!","info");
						else
						{
							createmarkers(obj.points);
							console.log(obj.points.length);
						}
					
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
			document.getElementById("loader").style.visibility="visible";
			document.getElementById("loadertext").innerHTML="Geting data from OSM....";
			}
			else
			{
				if(send_data.length==0)
				{
					swal("Select atleast one feature or layer to proceed","","warning");
				}
				else
				{
					swal("Please enter Buffer distance i.e the distance within which selected features my appear","",'warning');
				}

			}
			
			
			///////////////////////////////

		 }
		 
		 
		 ///create markers for search result
		 function createmarkers(pt)
		 {
		 var i;
			 for(i=0;i<pt.length;i++)create(i);
			 function create(i)
			 {
				 window.setTimeout(function() {
				 var mark=new google.maps.Marker({
												map: map,
												animation: google.maps.Animation.DROP
												});
												mark.setPosition({lat:pt[i].lat,lng:pt[i].lng});
					markers.push(mark);		
			//marker click listener
			mark.addListener('click', function(event) {
				revgeocodeosm_mark(geocoder,map,event.latLng,infosel,mark);
				
				});
				   
				},i*10);
			 }
			 
		 }
		 ////
		 function setback()
		 {
			 var i;
			 for(i=0;i<markers.length;i++)
			 {
				markers[i].setMap(map);
			 }
		 }
		 
		 ///clear markers for search result
		  function clearmarkers()
		 {	var i;
			 for(i=0;i<markers.length;i++)
				markers[i].setMap(null);
			// setback();
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
				var dis=1500; //////rectange distance
				///creating rectangle
				var bounds = {
				north: latlan['lat']+((1.0/3600)*(dis)/30.0),
				south: latlan['lat']-((1.0/3600)*(dis)  /30.0),
				east:  latlan['lng']+((1.0/3600)*(dis)    /30.0),
				west:  latlan['lng']-((1.0/3600)*(dis)    /30.0)
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
			  if(kmls[i].v!="")
			  {
			  shp_arr.push(kmls[i].v);
			  key_arr.push(kmls[i].k);
			  nam_arr.push(kmls[i].name);
			  }
			  
		  }

		  $.ajax({
						url:"index.php",
						type:'POST',
						//dataType: "json",
						data:
						{
							id:2,
							urls:shp_arr,
							keys:key_arr
						},
						success: function(msg)
						{
						//console.log(msg);
						var s=JSON.parse(msg);
						
						for(var i=0;i<s.length;i++)
						{
							names_in_table.push(s[i]);
						}
						}
					});
					
		  
		  
		  
		  

		  


		  ///geocoder initialise
		  geocoder = new google.maps.Geocoder();

		  ///ELEVATOR CREATION
		  elevator = new google.maps.ElevationService;

		  ///info window inilialiser
		  infowindow = new google.maps.InfoWindow({map: null,position:null,maxWidth:300});
		  inforec = new google.maps.InfoWindow({map:null,position:null});
		  infosel = new google.maps.InfoWindow({map:null,position:null});
		infolatlng=new google.maps.InfoWindow({map:null,position:null});

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
		  console.log('Geocode was not successful for the following reason: ' + status);
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
			console.log('No results found');
		  }
		} else {
		  console.log('Geocoder failed due to: ' + status);
		}
	  });
	};

	///rev geocoder for osm search
	revgeocodeosm_mark=function geocodeLatLng2(geocoder, map,loc,infowin,mark) {
	  geocoder.geocode({'location':loc}, function(results, status) {
		if (status === google.maps.GeocoderStatus.OK) {
		  if (results[1]) {
		  	infosel.setPosition(null);
				infosel.setContent(results[1].formatted_address+"");
				infosel.open(map, mark);
		  
		  } else {
			console.log('No results found');
		  }
		} else {
		  console.log('Geocoder failed due to: ' + status);
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
			clearmarkers();
		  }
		  
		marker.setPosition(event.latLng);
		//console.log(event.latLng.toString());

	  revgeocode(geocoder, map,event.latLng,"address");

	  });
	  
	  
	  ///double click listener
	   map.addListener('rightclick', function(event) {
		infolatlng.setPosition(null);
		infolatlng.setPosition(event.latLng);
		infolatlng.setContent(""+event.latLng);
		infolatlng.open(map);
		

	  });



	///display in info box items
	function display(location, elevator, infowindow)
	{
		var str_for_info="";
		var check_if_ele=false;
		toggleBounce();


	 // Initiate the location request
	  elevator.getElevationForLocations({'locations': [location]},function(results, status) {

		if (status === google.maps.ElevationStatus.OK) {
		  // Retrieve the first result
		  if (results[0]) {
			str_for_info+="<u><b>Elevation_Of_Land</u></b>"+">"+results[0].elevation +'m.';

		  } else {swal('No elevation results found for this region');}
		} else {console.log('Elevation service failed due to: '+status);}




	   ///places search
				
			var indez_for_places=0;
			 var places_search_service = new google.maps.places.PlacesService(map);
			 each_place_list=new Array();
			 each_place_list_str=new Array();

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
		  }, function callback(results, status,pagination) {
				//console.log("in");
		  		if(each_place_list[indez_for_places]==null)
				{
				 each_place_list[indez_for_places]=0;
			  each_place_list_str[indez_for_places]="<hr><u><b>"+places_to_search[indez_for_places].name+":</u></b><br>";}

			if (status === google.maps.places.PlacesServiceStatus.OK) {
			  var no_of_places=results.length;//(results.length<min_places)?results.length:min_places; //no of min places
			//console.log("----------"+type+":"+y);
			var i = 0;
			for (i = 0; i < no_of_places&&each_place_list[indez_for_places]+i<min_places; i++) {
			  //console.log(results[i].name+">"+getDistance(pyrmont,results[i].geometry.location).toFixed(3)+"km");
			  each_place_list_str[indez_for_places]+=results[i].name+">"+getDistance(location,results[i].geometry.location).toFixed(3)+"km<br>";
			}
			
			each_place_list[indez_for_places]+=i;


			if (pagination.hasNextPage&&min_places>each_place_list[indez_for_places])
			  	{
			  		pagination.nextPage();
			  		//console.log("has");
			  	}
			  	else each_place_list_str[indez_for_places]+="-";
			}

			if (status === google.maps.places.PlacesServiceStatus.ZERO_RESULTS){
			str_for_info+="No nearby places<br>";

			  }
			  var bool=true;
			  for(var z=(places_to_search.length)-1;z>=0;z--)
			  {
			  	try{var x=each_place_list_str[z].length;

			  	if(each_place_list_str[z][x-1]!="-"){bool=false;break;}}catch(err){bool=false;break;}
			  }
			  	

		  if(bool)
		  {
			 ///open info
			 //infowindow.setPosition(null);
			 for(var z=0;z<each_place_list_str.length;z++)
			 str_for_info+=each_place_list_str[z];
		
			 if(layers_to_find.length>0)
			 intersect_layers_database(str_for_info,location.lat,location.lng);
			 else
			 {infowindow.setContent(str_for_info);
			 marker.setAnimation(null);}
			 //infowindow.open(map, marker);
		  }
		  

		  });
		}
		
		

		});
	}
	///layers intersect in database
	function intersect_layers_database(str_for_info,lat,lng)
	{var no=0;
	 str_for_info+="<hr><u><b><center>Layers data</center></u></b>";
		for(var i=0;i<layers_to_find.length;i++)
		{
			$.ajax({
						url:"index.php",
						type:'POST',
						data:
						{
							id:3,
							lat:lat,
							lng:lng,
							table:layers_to_find[i],
							i:i
							
						},
						success: function(msg)
						{	no++;
							console.log(msg);
							var obj = JSON.parse(msg);
							//console.log(layers_names_find[obj.i]+obj.data);
							str_for_info+="<hr><u><b>"+layers_names_find[obj.i]+":</u></b><br>";
							str_for_info+=obj.data+"<br>";
							
							if(no==layers_to_find.length)
							infowindow.setContent(str_for_info);
							marker.setAnimation(null);
						}
					});
					
		}
		
	}





		///autocomplete initialize
		var input = (document.getElementById('address'));
		
		

  		autocomplete = new google.maps.places.Autocomplete(input);
		
		
		input.addEventListener("keydown",function(event)
		{
			if(event.which == 13 || event.keyCode == 13)
			{
				marker.setPosition(null);
		var place = autocomplete.getPlace();
		if (!place.geometry) {
		  console.log("Autocomplete's returned place contains no geometry");
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
			}
		});

		///autocomplete for address
	   autocomplete.addListener('place_changed', function() {

		marker.setPosition(null);
		var place = autocomplete.getPlace();
		if (!place.geometry) {
		  console.log("Autocomplete's returned place contains no geometry");
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
