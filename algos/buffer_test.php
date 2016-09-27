<?php	
///////////////////////////////////////////////////
$intersects=0;
$intersect_pt=array();
function st_dwithin($pat_text,$poi_text,$dis)
{	
	$poi_text=substr($poi_text,6,-1);	
	$temp=explode(" ",$poi_text);	
	$point["x"]=$temp[0];	
	$point["y"]=$temp[1];		
	$str=substr($pat_text,11,-1);	
	$str=explode(",",$str);	
	$path=array();	
	foreach($str as $s)	
	{		
		$temp=explode(" ",$s);		
		$temp_array["x"]=floatval($temp[0]);		
		$temp_array["y"]=floatval($temp[1]);		
		array_push($path,$temp_array);	
	}		
	$GLOBALS["intersects"]=0;	
	split_paths($path,$point,$dis);	
	if($GLOBALS["intersects"]==1)		
		return 1;	
	return 0;
}

function split_paths($path,$point,$dis)
{	
	if($GLOBALS["intersects"]==1)
		return;	
	if(!check_boundingbox($path,$point,$dis))
		return;	
	$path_len=count($path);	
	if($path_len<=2)		
		return check_buffer($path,$point,$dis);
	$mid=(int)($path_len/2)+1;
	$top=array_slice($path,0,$mid);
	$bottom=array_slice($path,$mid-1);
	split_paths($top,$point,$dis);
	split_paths($bottom,$point,$dis);
}

function check_boundingbox($path,$point,$dis)
{
	$max_x=$min_x=$path[0]["x"];
	$max_y=$min_y=$path[0]["y"];
	foreach($path as $p)
	{		
		if($p["x"]>$max_x)
			$max_x=$p["x"];
		if($p["y"]>$max_y)
			$max_y=$p["y"];
		if($p["x"]<$min_x)
			$min_x=$p["x"];
		if($p["y"]<$min_y)
			$min_y=$p["y"];
	}	
	$max_x+=$dis;
	$max_y+=$dis;
	$min_x-=$dis;
	$min_y-=$dis;
	if( $point["x"]>=$min_x && $point["x"]<=$max_x && $point["y"]>=$min_y && $point["y"]<=$max_y )
		return 1;
	return 0;
}

function check_buffer($path,$point,$dis)
{		
	$p1=$path[0];
	$p2=$path[1];
	$nume=($point["x"]-$p1["x"])*($p2["x"]-$p1["x"])+($point["y"]-$p1["y"])*($p2["y"]-$p1["y"]);
	$deno=($p2["x"]-$p1["x"])*($p2["x"]-$p1["x"])+($p2["y"]-$p1["y"])*($p2["y"]-$p1["y"]);
	$u=$nume/$deno;	
	$x=$p1["x"]+$u*($p2["x"]-$p1["x"]);	
	$y=$p1["y"]+$u*($p2["y"]-$p1["y"]);
	$d1=sqrt(($x-$point["x"])*($x-$point["x"])+($y-$point["y"])*($y-$point["y"]));
	if($d1<=$dis)
	{		
		$GLOBALS["intersects"]=1;
		//$GLOBALS["intersect_pt"]["x"]=$x;
		//$GLOBALS["intersect_pt"]["y"]=$y;
		$GLOBALS["intersect_pt"]=$p2;
		return 1;
	}		
	$d2=sqrt(($p1["x"]-$point["x"])*($p1["x"]-$point["x"])+($p1["y"]-$point["y"])*($p1["y"]-$point["y"]));
	if($d2<=$dis)
	{	
		$GLOBALS["intersects"]=1;
		$GLOBALS["intersect_pt"]=$p1;
		return 1;	
	}	
	$d3=sqrt(($p2["x"]-$point["x"])*($p2["x"]-$point["x"])+($p2["y"]-$point["y"])*($p2["y"]-$point["y"]));
	if($d3<=$dis)	
	{		
		$GLOBALS["intersects"]=1;
		$GLOBALS["intersect_pt"]=$p2;
		return 1;	
	}
	return 0;
}
////////////////////////////

function st_dwithin_pts($p1,$p2,$dis)
{
	$p1=substr($p1,6,-1);	
	$temp=explode(" ",$p1);
	$point1["x"]=$temp[0];	
	$point1["y"]=$temp[1];
	
	$p2=substr($p2,6,-1);	
	$temp=explode(" ",$p2);
	$point2["x"]=$temp[0];	
	$point2["y"]=$temp[1];
	
	$d=sqrt(pow(($point1["x"]-$point2["x"]),2)+pow(($point1["y"]-$point2["y"]),2));
	
	if($d<=$dis)
		return 1;
	return 0;
}

/////////////////////////////
function st_dwithin_retn_points($pat_text,$poi_text,$dis)
{	
	$poi_text=substr($poi_text,6,-1);	
	$temp=explode(" ",$poi_text);	
	$point["x"]=$temp[0];	
	$point["y"]=$temp[1];		
	$str=substr($pat_text,11,-1);	
	$str=explode(",",$str);	
	$path=array();	
	foreach($str as $s)	
	{		
		$temp=explode(" ",$s);		
		$temp_array["x"]=floatval($temp[0]);		
		$temp_array["y"]=floatval($temp[1]);		
		array_push($path,$temp_array);	
	}		
	$GLOBALS["intersects"]=0;	
	split_paths($path,$point,$dis);	
	if($GLOBALS["intersects"]==1)		
		return $GLOBALS["intersect_pt"];
	return 0;
}
////////////////////////////////
function distance_of_intersect($pt1,$pt2,$pat_text)
{
	$str=substr($pat_text,11,-1);	
	$str=explode(",",$str);	
	$path=array();	
	foreach($str as $s)	
	{		
		$temp=explode(" ",$s);		
		$temp_array["x"]=floatval($temp[0]);		
		$temp_array["y"]=floatval($temp[1]);		
		array_push($path,$temp_array);	
	}
	$p1_index=-1;
	$p2_index=-1;
	
	for($i=0;$i<count($path);$i++)
	{		
		if($path[$i]["x"]==$pt1["x"]&&$path[$i]["y"]==$pt1["y"])
		{
			$p1_index=$i;
		}
		if($path[$i]["x"]==$pt2["x"]&&$path[$i]["y"]==$pt2["y"])
		{
			$p2_index=$i;
		}
		if($p1_index!=-1&&$p2_index!=-1)
			break;
	}	
	
	$start = ($p1_index < $p2_index ? $p1_index : $p2_index);
	$stop  = ($p2_index >= $p1_index ? $p2_index : $p1_index);
	$dis=0;
	
	for($i=$start;$i<$stop;$i++)
	{	
		$a=$path[$i];
		$b=$path[$i+1];
		$dis+=sqrt(($a["x"]-$b["x"])*($a["x"]-$b["x"])+($a["y"]-$b["y"])*($a["y"]-$b["y"]));
	}
	return $dis;
	
		
}

 ?>

