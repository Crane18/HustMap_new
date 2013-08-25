package com.example.aimhustermap.route;

import java.util.ArrayList;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class Node {
	
	private GeoPoint point_ID;
	private ArrayList<Edge> edgeList;
	
	public Node(GeoPoint point_ID)
	{
		this.point_ID = point_ID;
		this.edgeList = new ArrayList<Edge>();
	}
	
	public GeoPoint getPoint_ID()
	{
		return this.point_ID;
	}
	
	public ArrayList<Edge> getEdgeList()
	{
		return this.edgeList;
	}

}
