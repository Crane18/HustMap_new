package com.example.aimhustermap.route;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class Edge {
	
	public GeoPoint StartNodeID;
	public GeoPoint EndNodeID;
	public double Distance;
	
	
	public Edge(GeoPoint StartNodeID,GeoPoint EndNodeID,double Distance)
	{
		this.StartNodeID = StartNodeID;
		this.EndNodeID = EndNodeID;
		this.Distance = Distance;
	}

}
