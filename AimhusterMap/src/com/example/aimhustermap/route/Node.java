package com.example.aimhustermap.route;


import java.util.ArrayList;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class Node {
	
	private GeoPoint pointID;

	// 起始节点到此节点的距离（经过某条路线到该点的实际距离）
	double GList ;
	// 此节点到目的节点的距离（预估直线距离）
	double HList ;
	//起始节点经过此节点到目的节点的距离（预估）
	double FList ;
	
	private Node fatherNode;
	
	private ArrayList<Node> childNodes;
	
	public Node(GeoPoint point)
	{
		this.pointID = point;
		this.childNodes = new ArrayList<Node>();
		this.FList = Double.MAX_VALUE;
		this.GList = Double.MAX_VALUE;
		this.HList = Double.MAX_VALUE;
	}
	
	public GeoPoint getPointID()
	{
		return this.pointID;
	}
	
	public double getFlist()
	{
		return this.FList;
	}
	
	public double getGlist()
	{
		return this.GList;
	}
	
	public double getHlist()
	{
		return this.HList;
	}
	
	public Node getFather() {
		
		return this.fatherNode;
	}
	
	public void setFather(Node father)
	{
		this.fatherNode = father;
	}
	
	public ArrayList<Node>  getChildNodes()
	{
		return this.childNodes;
	}

	
	
	

}
