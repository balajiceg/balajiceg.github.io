<?php
//////////
$time=microtime(true);
$log_time_file=fopen("files/time.txt","w+");
fwrite($log_time_file,"start time:".$time.PHP_EOL);
//////////////////

ini_set('max_execution_time', 0);
$db_connection = pg_connect("host='localhost' dbname='osm' user='postgres' password='balaji07'");
$sq_len=100.0;   //length of side of each grid
$buff=5;  //buffer for each selecting point in decimal
// paces where selected points must not lie
$ret="";
$add_places=array(        
				  array('"highway"'),
				  array('"waterway"'),
				  array('"natural"~"water|wetland|wood"'),
				  array('"power"'),
				  array('"railway"'),
				  array('"amenity"'),
				  array('"landuse"="commercial"')
				 );
/*
$ret="_ret";
$add_places=array(        
				  array('"highway"'),
				  array('"waterway"'),
				  array('"natural"~"water|wetland|wood"'),
				  array('"power"'),
				  array('"railway"'),
				  array('"amenity"')
				 );
//*/


///////////////////////
function log_time($a)
{	
	$t=microtime(true);
	$s=$a."> time:".$t."    Elapsed:".($t-$GLOBALS['time']).PHP_EOL;
	fwrite($GLOBALS['log_time_file'],$s);
	$GLOBALS['time']=$t;

}
//////////////////////


function compute($top,$bottom,$left,$right,$distance,$places,$tables,$tables_val)
{	/////////////////
			function check_location($d,$lat,$lng,$places)
			{
				$result3=pg_query($GLOBALS['db_connection'],
						"select array_to_json(s.tagsarr),st_asgeojson(s.geom)from nodes s where 
						st_dwithin(ST_SetSRID(ST_Point(".$lng.",".$lat."),4326)::geography,s.geom::geography,".
						$d.")=true;"
						);
				//$places=array();
				//array_push($places,array(array("amenity","school")),array(array("amenity","college"),array("amenity","university")),array(array("railway","station")));
				$p=$places;
				
				while ($row3 = pg_fetch_row($result3)) 
				{
					 $r=(json_decode($row3[0]));
					for($i=0,$t=1;$i<count($r);$i++,$t=1)
					{
					 $s=$r[$i];
					 
						 while($a=current($p))
						 {
							 foreach($a as $b)
							 {
								 if($s[0]==$b[0]&&$s[1]==$b[1])
								 {
									 $t=0;
									 break;
								 }
									 
							 }
							 if($t==0)
							 {
								 array_splice($p,key($p),1);
								 break;
							 }
							next($p);
						 }
						 reset($p);	 
					}
				}
				if(count($p)==0) return true;
				else return false;	
			}
	///////////////////////

	$selected=array();
	/////////////////
	$deg_lat=(1.0/110574.0)*$GLOBALS['sq_len'];
	function deg_lng($lat)
	{
		$tem=(1.0/(111320.0*cos($lat)))*$GLOBALS['sq_len'];
		return $tem;
	}
	//////////////////
	for($lat=$bottom;$lat<=$top;$lat=$lat+$deg_lat)
	{
		for($lng=$left;$lng<=$right;$lng=$lng+deg_lng($lat))
		{	log_time("before database1");////////////////////////////////////////////////////////////////////////////////////////////////

			$result=pg_query($GLOBALS['db_connection'],'select chk_intersects_dwith('.$lat.','.$lng.','.$GLOBALS['buff'].');');
			$bool = pg_fetch_row($result);
			if($bool[0]=='f')
			{
				$boolean=true;

			log_time("after database1");////////////////////////////////////////////////////////////////////////////////////////////////
	
				for($x=0;$x<count($tables);$x=$x+1)
				{
					$result2=pg_query($GLOBALS['db_connection'],"select st_dwithin(ST_SetSRID(ST_Point(".$lng.",".$lat."),4326)::geography,geom::geography,0) from ".$tables[$x]." where name=any(array[".$tables_val[$x]."]) and st_dwithin(ST_SetSRID(ST_Point(".$lng.",".$lat."),4326)::geography,geom::geography,0)=true;");
					$row2=pg_fetch_row($result2);
					if($row2[0]!='t')
					{
						$boolean=false;
						break;
					}
				}
				log_time("after database2");////////////////////////////////////////////////////////////////////////////////////////////////
	
				if ($boolean) 
				{
					if(check_location($distance,$lat,$lng,$places))
					{
						array_push($selected,array("lat"=>$lat,"lng"=>$lng));

					}
					log_time("after check");////////////////////////////////////////////////////////////////////////////////////////////////
				}
				
			}
		}
	}
return $selected;			
}

////////////////////////////////////////////////////////


function upload_check($str)
{
	$ch = curl_init();
	$fp = fopen("files/map.xml", "w");
	$str=curl_escape($ch,$str);
	$str="http://overpass-api.de/api/interpreter?data={$str}";
	curl_setopt($ch, CURLOPT_FILE, $fp);
	curl_setopt($ch, CURLOPT_URL, $str);
	curl_setopt($ch, CURLOPT_HEADER, 0);
	curl_exec($ch);
	curl_close($ch);
	fclose($fp);

	//database
	$result = pg_query($GLOBALS['db_connection'],"truncate users,nodes,ways,way_nodes,relations,relation_members,lines,polygons,lines_bon,polygons_bon;") ;



	//osmosis
	echo shell_exec('osmosis --read-xml "files/map.xml" --log-progress --write-pgsql database=osm user=postgres password=balaji07');
	$result = pg_query($GLOBALS['db_connection'],"insert into polygons_bon (select * from polygons_all where st_dwithin(ST_GeomFromText('POLYGON((".$left." ".$top.",".$right." ".$top.",".$right." ".$bottom.",".$left." ".$bottom.",".$left." ".$top."))',4326)::geography,geom::geography,0)=true);") ;
	$result = pg_query($GLOBALS['db_connection'],"insert into lines_bon (select * from lines_all where st_dwithin(ST_GeomFromText('POLYGON((".$left." ".$top.",".$right." ".$top.",".$right." ".$bottom.",".$left." ".$bottom.",".$left." ".$top."))',4326)::geography,geom::geography,0)=true);") ;
	
	//database check and modify
	$result = pg_query($GLOBALS['db_connection'],"update nodes set tagsarr=fun(id);") ;
	$result=pg_query($GLOBALS['db_connection'],"insert into polygons (select id,ST_MakePolygon(linestring) from ways where nodes[1]=nodes[array_upper(nodes,1)]);");

	$result=pg_query($GLOBALS['db_connection'],"insert into lines (select id,linestring from ways where nodes[1]!=nodes[array_upper(nodes,1)]);");

	$result = pg_query($GLOBALS['db_connection'],"SELECT COUNT(*) FROM nodes;");
	$nodes = pg_fetch_row($result);
	$nodes=intval($nodes[0]);
	if($nodes==0)
		return "no_records";
	else
		return "success";

}

function config_omit_features($top,$bottom,$left,$right)
{	$add_places=$GLOBALS['add_places'];
	$ret=$GLOBALS['ret'];
	$str="(";
	///adding omiting features
		foreach ($add_places as $place)
		{
			
			$s="";
			foreach($place as $p)
			{
			$s=$s.'['.$p.']';
			}
			$str=$str.'way'.$s.'('.$bottom.','.$left.','.$top.','.$right.');';
			$str=$str.'rel'.$s.'('.$bottom.','.$left.','.$top.','.$right.');';


		}
	$str=$str.');(._;>;);out meta;';
	$file = fopen("files/log2.txt", "w");
	fwrite($file,$str);
	fclose($file);
	
	
	
	$ch = curl_init();
	$fp = fopen("files/mapomit.xml", "w");
	$str=curl_escape($ch,$str);
	$str="http://overpass-api.de/api/interpreter?data={$str}";
	curl_setopt($ch, CURLOPT_FILE, $fp);
	curl_setopt($ch, CURLOPT_URL, $str);
	curl_setopt($ch, CURLOPT_HEADER, 0);
	curl_exec($ch);
	curl_close($ch);
	fclose($fp);

	//database
	$result = pg_query($GLOBALS['db_connection'],"truncate users,nodes,ways,way_nodes,relations,relation_members,lines_all".$ret.",polygons_all".$ret.";") ;
	


	//osmosis
	echo shell_exec('osmosis --read-xml "files/mapomit.xml" --log-progress --write-pgsql database=osm user=postgres password=balaji07');

	//database check and modify
	//$result = pg_query($GLOBALS['db_connection'],"update nodes set tagsarr=fun(id);") ;
	$result=pg_query($GLOBALS['db_connection'],"insert into polygons_all".$ret." (select id,ST_MakePolygon(linestring) from ways where nodes[1]=nodes[array_upper(nodes,1)] and array_upper(nodes,1)>=4);");

	$result=pg_query($GLOBALS['db_connection'],"insert into lines_all".$ret." (select id,linestring from ways where nodes[1]!=nodes[array_upper(nodes,1)] or array_upper(nodes,1)<4);");

	//$result = pg_query($GLOBALS['db_connection'],"truncate users,nodes,ways,way_nodes,relations,relation_members") ;

	
}

/////////////////////////////////////////////////////////////////////

if(isset($_POST["id"]))
{	$a=$_POST["id"];

	if($a==3)
	{
		$lat=$_POST["lat"];
		$lng=$_POST["lng"];
		$table=$_POST["table"];
		
		
		
			$result=pg_query($GLOBALS['db_connection'],"select name from ".$table." where st_dwithin(ST_SetSRID(ST_Point(".$lng.",".$lat."),4326)::geography,geom::geography,0)");
	
		$bool = pg_fetch_row($result);
		
		if($bool[0]=="")
		{	
			print_r('{"table":"'.$table.'","data":"No data for this point","i":"'.$_POST["i"].'"}');
			
		}
		else
		{	print_r('{"table":"'.$table.'","data":"'.$bool[0].'","i":"'.$_POST["i"].'"}');
				
		}
			
		
		
		
	}
	
	else if($a==2)
	{
		$keys=$_POST["keys"];
		$urls=$_POST["urls"];
		$out=array();
		for($i=0;$i<count($keys);$i=$i+1)
		{
		
			$result=pg_query($GLOBALS['db_connection'],"SELECT EXISTS (
			SELECT 1 
			FROM   pg_catalog.pg_class c
			JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
			WHERE  n.nspname = 'public'
			AND    c.relname = '".$keys[$i]."'
			AND    c.relkind = 'r'
			);");
	
			$bool = pg_fetch_row($result);

			if($bool[0]=='f')
			{
				//print_r("k");
				shell_exec('shp2pgsql -I "'.$urls[$i].'" public.'.$keys[$i].' | psql -U postgres -d osm');
			}

			$result=pg_query($GLOBALS['db_connection'],"select distinct(name) from ".$keys[$i].";");
			$names=array();
			while ($row = pg_fetch_row($result)) 
				{
					array_push($names,$row[0]);
				}
			array_push($out,$names);
		}


		print_r(json_encode($out));
	}
	
	
	else if($a==1)
	{	
		

		$myfile = fopen("files/log.txt", "w");
		
		$places=$_POST["places"];
		$distance=$_POST["distance"];
		
		
		$top=(($distance+100)/(3600.0*30.0))+$_POST["top"];
		$bottom=$_POST["bottom"]-(($distance+100)/(3600.0*30.0));
		$left=$_POST["left"]-(($distance+100)/(3600.0*30.0));
		$right=(($distance+100)/(3600.0*30.0))+$_POST["right"];
		
		config_omit_features($top,$bottom,$left,$right);
		
		$tables=array();
		$tables_val=array();
		$tables=@$_POST['tables'];
		$tables_val=@$_POST['tables_val'];
		

		
		
		$tem=0;
		$result="success";
		/////////////////////////////////////////////////////////////
		/*
		$places_arr=array();
		$str="(";
		
		

		//lower latitude followed by lower longitude, then upper latitude then upper longitude
		foreach ($places as $place) {
			$key="";$value="";
			foreach($place as $k => $v)
			{
				if($k=="kv")
				{
				 $temp=array();
				 	foreach($v as $kv)
					{
						foreach($kv as $ke => $va)
						{
							if($ke=="key")
							{
							$key=$va;
							}
							if($ke=="value")
							{
							$value=$va;
							}
						}
						array_push($temp,array($key,$value));
						$str=$str.'node['.'"'.$key.'"='.'"'.$value.'"]('.$bottom.','.$left.','.$top.','.$right.');';
						$str=$str.'way['.'"'.$key.'"='.'"'.$value.'"]('.$bottom.','.$left.','.$top.','.$right.');';

					}
					array_push($places_arr,$temp);
				}
				
			}
			
		}
		
		
		//print_r($places_arr);
		$str=$str.');(._;>;);out meta;';
		//$result=upload_check($str);
		
		fwrite($myfile,$str);
		//$top=$_POST["top"];
		//$left=$_POST["left"];
		//$right=$_POST["right"];
		//$bottom=$_POST["bottom"];
		
		if($result=="success")
		{
		$tem=compute($top,$bottom,$left,$right,$distance,$places_arr,$tables,$tables_val);
		}*/
		///////////////////////////////////////////////////////////////////////////////////////////
		
		log_time("last");////////////////////////////////////////////////////////////////////////////////////////////////

		
		print_r(json_encode(array("result" => $result,"points" => $tem,"top" => $top,
		"bottom" => $bottom,"left" => $left,"right" => $right)));


	}
}
else
{

?>
















<html lang="en">

<head>
<!--my include-->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

   <title>Negletion</title>
	<link rel="shortcut icon" href="files/favicon.png" />
	<link rel="stylesheet" type="text/css" href="files/sweet_alert/sweetalert.css">
	<link rel="stylesheet" href="files/bootstrap/css/bootstrap.min.css" type="text/css" />
	<link rel="stylesheet" href="files/bootstrap/css/bootstrap-select.min.css">




	<link href="files/bootstrap/css/bootstrap-switch.css" rel="stylesheet">
	<script src="files/bootstrap/js/jquery.min.js"></script>
	<script src="files/bootstrap/js/bootstrap-switch.js"></script>
	<script src="files/bootstrap/js/spin.js"></script>
	<script src="files/bootstrap/js/bootstrap.min.js"></script>
	<script src="files/sweet_alert/sweetalert.min.js"></script>
	<script src="files/bootstrap/js/bootstrap-select.min.js"></script>
 
	
	<!--check internet-->
	
	</script>


	<style>
	
	
	.contain {
    width: 50px;
    height: 50px;
}

/* resize images */
.contain img {
    width: 100%;
    height: auto;
}
	
	.img-circle {
    border-radius: 50%;

	}
	.bttn:focus {
  outline:none !important;
}
		
		#loader{
	position:fixed;
	top:0;
	visibility:hidden;
	width:100%;
	background-color:rgb(0, 0, 0);
	height:100%;
	text-align:center;
	margin:0;
	padding:0;
	overflow:hidden;
	z-index:40000;
	opacity: 0.8;
	}
		ul.side-nav > li:first-child{
			border-top: 1px #e5e5e5 solid;
		}
		ul.side-nav > li{
			border-bottom: 1px #e5e5e5 solid;

		}

		ul.side-nav >li>ul>li{
			border-bottom: 1px #999999 solid;

		}

		ul.side-nav > li>ul>li:first-child{
			border-top: 1px #e5e5e5 solid;
		}

		

		 .controls {
  margin-top: 10px;
  border: 1px solid transparent;
  border-radius: 2px 2px 2px 2px;
  box-sizing: border-box;
  -moz-box-sizing: border-box;
  height: 32px;
  outline: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

#address {
  background-color: #fff;
  font-family: Roboto;
  font-size: 15px;
  font-weight: 300;
  margin-left: 12px;
  padding: 0 11px 0 13px;
  text-overflow: ellipsis;
  width: 45%;
}

#address:focus {
  border-color: #4d90fe;
}


.form-group input[type="checkbox"] {
    display: none;
}

.form-group input[type="checkbox"] + .btn-group > label span {
    width: 10px;
}

.form-group input[type="checkbox"] + .btn-group > label span:first-child {
    display: none;
}
.form-group input[type="checkbox"] + .btn-group > label span:last-child {
    display: inline-block;
}

.form-group input[type="checkbox"]:checked + .btn-group > label span:first-child {
    display: inline-block;
}
.form-group input[type="checkbox"]:checked + .btn-group > label span:last-child {
    display: none;

}

.enforce{
float: left !important;
left:10%;
}

.gmapbtn{
  background-color: #cc6699 !important; 
  border-color:  #c6538c !important;
}
.gmapbtn:hover{
  background-color: #bf4080 !important; 
  border-color: #bf4080 !important;
}


.feabtn{
  background-color:#ff944d !important; 
  border-color:  #ff8433 !important;
}
.feabtn:hover{
  background-color: #ff751a !important; 
  border-color: #ff751a !important;
}

.headtitle{
	color:#ffffff !important;
}

.headtitle:hover{
	color:#777 !important;
}


	</style>






<!--/my include-->


    <!-- Custom CSS -->
    <link href="files/bootstrap/css/sb-admin.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="files/bootstrap/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
	<div id="loader" >

	<!-- script for the spinner-->
	<script>

	var opts = {
 lines: 20 // The number of lines to draw
, length: 5 // The length of each line
, width: 2.5 // The line thickness
, radius: 10 // The radius of the inner circle
, scale: 5 // Scales overall size of the spinner
, corners: 1 // Corner roundness (0..1)
, color: 'rgb(255, 153, 51)' // #rgb or #rrggbb or array of colors
, opacity: 0.2 // Opacity of the lines
, rotate: 0 // The rotation offset
, direction: 1 // 1: clockwise, -1: counterclockwise
, speed: 1.3 // Rounds per second
, trail: 34 // Afterglow percentage
, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
, zIndex: 2e9 // The z-index (defaults to 2000000000)
, className: 'spinner' // The CSS class to assign to the spinner
, top: '50%' // Top position relative to parent
, left: '49%' // Left position relative to parent
, shadow: false // Whether to render a shadow
, hwaccel: false // Whether to use hardware acceleration
, position: 'absolute' // Element positioning
}
var target = document.getElementById('loader');
var spinner = new Spinner(opts).spin();
target.style.visibility="visible";
target.appendChild(spinner.el);
	</script>
		<div style="margin-top:32%;align:center;">
		<h2 id="loadertext" style="color:rgb(200,200,200);"> Loading...</h2>
		</div>
	</div>



</head>

<body onload="onload();">






    <div id="wrapper">

        <!-- Navigation -->
        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
				<span class="pull-left" style="left:50px important;">
					<div class="contain">
                    <img class="media-object img-rounded" src="files/icon.jpg" alt="">
					</div>
				</span>
                <a class="navbar-brand headtitle" href="index.html">Land Selection App</a>
            </div>
            <!-- Top Menu Items -->
            <?php /*
            <ul class="nav navbar-right top-nav">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-bell"></i> <b class="caret"></b></a>
                    <ul class="dropdown-menu alert-dropdown">
                        <li>
                            <a href="#">Alert Name <span class="label label-default">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-primary">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-success">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-info">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-warning">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-danger">Alert Badge</span></a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">View All</a>
                        </li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i> Developers <b class="caret"></b></a>
                     <ul class="dropdown-menu message-dropdown">
                        <li class="message-preview">
                            <a target="_blank" href="https://www.facebook.com/hari.sabari.77">
                                <div class="media">
                                    <span class="pull-left">
										<div class="contain">
										<img class="media-object img-circle" src="files/dev/hari.jpg" alt="">
										</div>
									</span>
                                    <div class="media-body">
                                        <h5 class="media-heading"><strong>Hari Hara Ganesh</strong>
                                        </h5>
                                        <!--p class="small text-muted"><i class="fa fa-clock-o"></i> Yesterday at 4:32 PM</p-->
                                        <p>B.E Geo-Informatics...</p>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="message-preview">
                            <a target="_blank" href="https://www.facebook.com/kiren.kumar.507">
                                <div class="media">
                                    <span class="pull-left">
										<div class="contain">
                                        <img class="media-object img-circle" src="files/dev/kiren.jpg" alt="">
										</div>
									</span>
                                    <div class="media-body">
                                        <h5 class="media-heading"><strong>Kiren Kumar</strong>
                                        </h5>
                                        <!--p class="small text-muted"><i class="fa fa-clock-o"></i> Yesterday at 4:32 PM</p-->
                                        <p>B.Tech Information Technology...</p>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="message-preview">
                            <a target="_blank" href="https://www.facebook.com/balaji.ramesh.3910">
                                <div class="media">
                                    <span class="pull-left">
										<div class="contain">
                                        <img class="media-object img-circle" src="files/dev/balaji.jpg" alt="">
										</div>
									</span>
                                    <div class="media-body">
                                        <h5 class="media-heading"><strong>R Balaji</strong>
                                        </h5>
                                        <!--p class="small text-muted"><i class="fa fa-clock-o"></i> Yesterday at 4:32 PM</p-->
                                        <p>B.E Geo-Informatics...</p>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <!--li class="message-footer">
                            <a href="#">Read All New Messages</a>
                        </li-->
                    </ul>
                </li>
            </ul>*/?>
            <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
            <div class="collapse navbar-collapse navbar-ex1-collapse">
                <ul class="nav navbar-nav side-nav" >
                    <li class="active">
                        <a href="index.html"><i class="fa fa-fw fa-dashboard"></i> Map</a>
                    </li>
                    <li>
                        <a href="info.html"><i class="fa fa-fw fa-bar-chart-o"></i> Info</a>
                    </li>

                    <li>
                        <a href="javascript:;" data-toggle="collapse" data-target="#kmls"><i class="fa fa-fw fa-arrows-v"></i> Overlays <i class="fa fa-fw fa-caret-down"></i></a>

						 <ul id="kmls" class="collapse" style=" list-style-type: none;">
                           
                        </ul>


					</li>
                    <!--li>
                        <a href="blank-page.html"><i class="fa fa-fw fa-file"></i> Blank Page</a>
                    </li-->
					
                    <li>
					<div>
						<div class="row"> 
						<a style="visibility:hidden">asdaaaaaaa</a>
						<div class="col-md-4" style="margin-top:5%;margin-bottom:5%;left:3%;float:left;">
                        <!--toggle button-->
						<input id="select"  type="button" class="btn btn-info bttn" data-toggle="collapse" data-target=".navbar-ex1-collapse" aria-pressed="false" autocomplete="off" value="select" onclick="selectbtn()"></input>
						</div>
						<div class="col-md-4" style="margin-top:5%;float:right;right:10%;margin-bottom:5%;">
                        <input id="done" type="button" class="btn btn-success bttn" style="visibility:hidden;" value="done" style="" data-toggle="modal" data-target="#myModal">
						
						</input><br>

						</div>
						</div>
					</div>
                    </li>
					
					<li id="xxxxxx">
					<div class="row">
					<a style="visibility:hidden">asdasdasd</a>
						<div class="col-md-6" style="margin-top:4%;margin-bottom:4%;left:10%;float:left;">
						<input type="button" class="btn btn-primary feabtn bttn" data-toggle="modal" data-target="#myModal2" autocomplete="off" value="Edit features to view"></input>
						</div>
					</div>
					</li>
					
					
					<li id="setgmap" style="visibility:hidden;">
					<div class="row">
					<a style="visibility:hidden">asdasdasd</a>
						<div class="col-md-6" style="margin-top:4%;margin-bottom:4%;left:10%;float:left;">
						<input type="button" class="btn btn-primary gmapbtn bttn" autocomplete="off" value="Clear & Back to Gmap" onclick="set_gmap()"></input>
						</div>
					</div>
					</li>
					
					

                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </nav>




           <div id="map" style="height:92vh"></div>





            <!-- /.container-fluid -->


        <!-- /#page-wrapper -->

    </div>




	 <input id="address" class="controls" type="text" placeholder="Search Box">
    <!-- /#wrapper -->
	 <!--- model for osm search-->
			<div class="modal fade" id="myModal" role="dialog">
				<div class="modal-dialog">

				  <!-- Modal content-->
				  <div class="modal-content">
					<div class="modal-header">
					 <button type="button" class="close" data-dismiss="modal">&times;</button>
					  <h4 class="modal-title">Select Elements</h4>

					</div>
					<div class="modal-body" style="overflow: auto;height: 70%;overflow-x: hidden;">
						<div class="row" id="bbod2">
						</div>
						<div class="row" id="bbod3">
						</div>
					</div>


					<div class="modal-footer">
					 <button type="button" class="btn btn-default" style="float:left;" data-dismiss="modal">Close</button>
					  <div class="col-xs-7 enforce" ><input id="buffer" required type="number" class="form-control" min="0" data-max="10000" step="100" placeholder="Buffer Distance in meters"></div>
					  <button type="button" class="btn btn-success" style="float:right;" onclick="donebtn();" data-toggle="collapse" data-target=".navbar-ex1-collapse">Done</button>

					</div>
				  </div>

				</div>
			  </div>
			<!--/modal-->
			
			
			
	 <!--- model2 for gmaps features-->
			<div class="modal fade" id="myModal2" role="dialog">
				<div class="modal-dialog">

				  <!-- Modal content-->
				  <div class="modal-content">
					<div class="modal-header">
					 <button type="button" class="close" data-dismiss="modal">&times;</button>
					  <h4 class="modal-title">Select features and layers to see at a point</h4>

					</div>
					<div class="modal-body" style="overflow: auto;height: 70%; overflow-x: hidden;" >
					<div class="row" id="mod2">
					</div>
					<div class="row" id="mod3">
					</div>
					</div>


					<div class="modal-footer">
					 <button type="button" class="btn btn-default" style="float:left;" data-dismiss="modal">Close</button>
					  <div class="col-xs-7 enforce" ><input id="n_of_feat" required type="number" class="form-control" min="0" data-max="20" step="1" placeholder="Size of each list"></div>
					  <button type="button" class="btn btn-success" style="float:right;" onclick="setattributes();" data-toggle="collapse" data-target=".navbar-ex1-collapse">Done</button>

					</div>
				  </div>

				</div>
			  </div>
			<!--/modal2-->












    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAvXaxZMwOQ7McdKYPRee-iiVTlQsvDf-A&callback=initMap&signed_in=true&libraries=places" async defer></script>
	
	<script src="files/places.js"></script>
	<script src="files/script.js"></script>


	<!--template for osm features-->
	<script type="text/template" id="cboxtemplate">
		<div class="{{class}}" >

		 <div class="[ form-group ]">
			<input type="checkbox" name="fancy-checkbox-default" id="{{id}}" autocomplete="off" />
			<div class="[ btn-group ]">
				<label for=" {{id}} " class="[ btn btn-primary ]">
					<span class="[ glyphicon glyphicon-ok ]" style="height:20px;"></span>
					<span>&nbsp </span>
				</label>
				<label for="{{id}}" class="[ btn btn-default ]">{{text}}</label>
			</div>
		 </div>
		 <hr>
		 </div>
	</script>


	<!--template for osm layers search-->
	<script type="text/template" id="searchlayers">
		<div class="{{class}}" >

		  <select id="{{id}}" class="selectpicker form-control" multiple data-done-button="true" data-done-button-text="Done" data-width="fit" >
		  </select>

		 <hr>
		 </div>
	</script>
	

	<!--template for kmls -->
	<script type="text/template" id="kmls_template">
		 <li>
			<div class="row">
			<div class="col-md-4" style="margin-top:1%;float:left;left:15px;font-size:16px;color:rgb(191, 191, 191);">{{name}}</div>
			<div class="clo-md-4" style="vertical-align:center;margin-bottom:3%;margin-top:2%;float:right;margin-right:35px;height:20px"><input id="{{id}}"  type="checkbox"></div>
			</div>
		 </li>
	</script>
	
	<!--template for gmaps features-->
		<script type="text/template" id="chkmod2">
		<div class="{{class}}" >

		 <div class="[ form-group ]">
			<input type="checkbox" name="fancy-checkbox" id="{{id}}" autocomplete="off" />
			<div class="[ btn-group ]">
				<label for=" {{id}} " class="[ btn btn-danger ]">
					<span class="[ glyphicon glyphicon-ok ]" style="height:20px;"></span>
					<span>&nbsp </span>
				</label>
				<label for="{{id}}" class="[ btn btn-default ]">{{text}}</label>
			</div>
		 </div>
		 <hr>
		 </div>
	</script>
	
	<!--template for gmap layers-->
		<script type="text/template" id="chk_layers">
		<div class="{{class}}" >

		 <div class="[ form-group ]">
			<input type="checkbox" name="fancy-checkbox" id="{{id}}" autocomplete="off" />
			<div class="[ btn-group ]">
				<label for=" {{id}} " class="[ btn btn-primary ]">
					<span class="[ glyphicon glyphicon-ok ]" style="height:20px;"></span>
					<span>&nbsp </span>
				</label>
				<label for="{{id}}" class="[ btn btn-default ]">{{text}}</label>
			</div>
		 </div>
		 <hr>
		 </div>
	</script>
	
	
	
	






</body>

</html>
<?php }  

fclose($GLOBALS['log_time_file']);
?>
