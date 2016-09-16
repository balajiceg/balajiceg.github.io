<?php

ini_set('max_execution_time', 0);
$db_connection = pg_connect("host='localhost' dbname='osm' user='postgres' password='balaji07'");

if(isset($_POST["id"]))
{	$a=$_POST["id"];

	if($a==1)
	{

	$a=fopen("asa","w+");
	$s=array();
	$s=@$_POST['tables_val'];
	if(count($s)==0)
	{
		print_r("not selected");
	}
	else 
	{
		print_r($s[0]);
		$result2=pg_query($GLOBALS['db_connection'],"select st_dwithin(ST_SetSRID(ST_Point(80.22525787353516,13.009575453629898),4326)::geography,geom::geography,0) from grndwater where name=any(array[".$s[0]."]) and st_dwithin(ST_SetSRID(ST_Point(80.22525787353516,13.009575453629898),4326)::geography,geom::geography,0)=true;"
		);

		$row2=pg_fetch_row($result2);
		print_r($row2[0]);
		if($row2[0]!='t')
		{
			print_r("working");
		}
	}
	}
}

?>

		
		