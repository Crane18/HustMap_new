package com.example.aimhustermap.route;

import java.util.ArrayList;
import java.util.Hashtable;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class PlanCourse {
	int i = 0;
	private Hashtable htHashtable;
	public PlanCourse(ArrayList<Node> nodeList,GeoPoint origin_ID)
	{
		this.htHashtable = new Hashtable();
		Node originNode = null;
		
		
		
		for (Node node : nodeList) {
			if((node.getPoint_ID().getLatitudeE6()==origin_ID.getLatitudeE6()) && (node.getPoint_ID().getLongitudeE6()==origin_ID.getLongitudeE6()))
			{
				originNode = node;
				PassedPath pPath = new PassedPath(origin_ID);
				this.htHashtable.put(origin_ID, pPath);
			}
			else {
				PassedPath pPath = new PassedPath(node.getPoint_ID());
//				String ID = String.valueOf(node.getPoint_ID().getLatitudeE6()) + String.valueOf(node.getPoint_ID().getLongitudeE6());
//				System.out.println("-------------->ID = "+ID);
				this.htHashtable.put(node.getPoint_ID(), pPath);
				
			}
		}
		
		if(originNode == null)
		{
			try {
				System.out.println("the originNode id not exist");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		this.InitializeDistance(originNode);
		
	}
	
	private void InitializeDistance(Node originNode) 
	{
		if(originNode.getEdgeList() == null || originNode.getEdgeList().size() == 0)
		{
			return;
		}
		
		for (Edge edge : originNode.getEdgeList()) {
			PassedPath pPath = this.getPassedPath(edge.EndNodeID);
			if(pPath == null)
			{
				continue;
			}
			
			pPath.getPassedIDList().add(originNode.getPoint_ID());
			pPath.setDistance(edge.Distance);
			
		}
	}
	
	public PassedPath getPassedPath(GeoPoint point_ID)
	{
		
//		System.out.println("---------->11111htHashTable:"+i+" = "+this.htHashtable.get(point_ID));
//		i++;
//		System.out.println(i);
		return (PassedPath)this.htHashtable.get(point_ID);
		
	}

}
