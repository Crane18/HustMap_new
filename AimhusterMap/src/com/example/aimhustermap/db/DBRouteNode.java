package com.example.aimhustermap.db;

import java.util.List;

import com.baidu.platform.comapi.basestruct.GeoPoint;



public class DBRouteNode {
	
	public GeoPoint node ;
	
	public List<Integer> nodeChildren ;
	
	public DBRouteNode(GeoPoint point, List<Integer> pointsIndex){
		
		node = point ;		
		nodeChildren = pointsIndex ;
	}

}
