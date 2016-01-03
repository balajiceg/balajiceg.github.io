
var osmplaces=[
{name:'school',kv:[{key:'amenity',value:'school'}]},
{name:'Railway Station',kv:[{key:'railway',value:'station'}]},
{name:'College',kv:[{key:'amenity',value:'college'},{key:'amenity',value:'university'}]}
];


var gplaces=[
{name:'School',key:'school'},
{name:'Store',key:'store'},
{name:'ATM',key:'atm'},
{name:'Bus Stop',key:'bus_station'},
{name:'Temple',key:'hindu_temple'},
{name:'Hospital',key:'hospital'},
{name:'Railway Station',key:'train_station'},
{name:'Airport',key:'airport'},
{name:'Bank',key:'bank'},
{name:'Bar',key:'bar'},
{name:'Church',key:'church'},
{name:'Cemetery',key:'cemetery'},
{name:'Gym',key:'gym'},
{name:'Mosque',key:'mosque'},
{name:'Park',key:'park'},
{name:'Pharmacy',key:'pharmacy'},
{name:'Restaurant',key:'restaurant'},
{name:'University',key:'university'},
{name:'Mall',key:'shopping_mall'}
];

var kmls=[
{name:"GroundWater",url:"http://balajiceg.github.io/grndwater.kml",v:null},
{name:"Poly1",url:"http://balaji.apps19.com/kmls/poly1.kml",v:null},
{name:"Poly2",url:"http://balaji.apps19.com/kmls/poly2.kml",v:null},
{name:"Poly3",url:"http://balaji.apps19.com/kmls/poly3.kml",v:null}
];


function getgplaces()

{
	return gplaces;
}


function getosmplaces()
{
	return osmplaces;
}

function getkmls()
{
	return kmls;
}