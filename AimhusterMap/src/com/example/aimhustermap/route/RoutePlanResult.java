package com.example.aimhustermap.route;

import java.util.ArrayList;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class RoutePlanResult {
	
	public ArrayList<GeoPoint> passedNodeIDs;
	double distance;
	
	public RoutePlanResult(ArrayList<GeoPoint> passedNodeIDs,double distance)
	{
		this.passedNodeIDs = passedNodeIDs;
		this.distance = distance;
	}
	
	public double getDistance()
	{
		return this.distance;
	}

}
