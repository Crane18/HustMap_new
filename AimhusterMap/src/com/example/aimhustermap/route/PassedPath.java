package com.example.aimhustermap.route;

import java.util.ArrayList;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class PassedPath {
	
	private GeoPoint curNodeID;
	private boolean beProcessed;
	private double distance;
	private ArrayList<GeoPoint>  passedIDList;
	
	public PassedPath(GeoPoint curNodeID)
	{
		this.curNodeID = curNodeID;
		this.distance = Double.MAX_VALUE;
		this.passedIDList = new ArrayList<GeoPoint>();
		this.beProcessed = false;
	}
	
	public GeoPoint getCurNodeID()
	{
		return this.curNodeID;
	}
	
	public boolean getBeProcessed()
	{
		return this.beProcessed;
	}
	
	public void setBeProcessed(boolean beprocessed)
	{
		this.beProcessed = beprocessed;
	}
	
	public double getDistance()
	{
		return this.distance;
	}
	
	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public ArrayList<GeoPoint> getPassedIDList()
	{
		return this.passedIDList;
	}
}
