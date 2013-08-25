package com.example.aimhustermap.route;

import java.util.ArrayList;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class RoutePlanner {
	
	public RoutePlanner()
	{		
	}
	
	public RoutePlanResult plan(ArrayList<Node> nodeList ,GeoPoint originID,GeoPoint destID)
	{
//		System.out.println("---------------->nodeList.Size() = "+nodeList.size());
		PlanCourse planCourse = new PlanCourse(nodeList, originID);
		Node curNode = this.getMinDistanceRudeNode(planCourse,nodeList,originID);
//		String ID = String.valueOf(curNode.getPoint_ID().getLatitudeE6()) + String.valueOf(curNode.getPoint_ID().getLongitudeE6());
		//System.out.println("-------------->ID = "+ID);
		while (curNode != null) {
			
			PassedPath curPath = planCourse.getPassedPath(curNode.getPoint_ID());
//			System.out.println("------------->curPath.distance =  "+curPath.getDistance()+"\n passedNode.Size = "+
//			curPath.getPassedIDList().size());
			for(Edge edge: curNode.getEdgeList())
			{
				
//				System.out.println("---------->edge = ");
				PassedPath targetPath = planCourse.getPassedPath(edge.EndNodeID);
//				System.out.println("--------->edge.EndNodeID = "+edge.EndNodeID);
//				System.out.println("------------->targetPath =  "+targetPath);
				if(targetPath.getBeProcessed())
				{
					continue;
				}
				
				double tempDistance = curPath.getDistance()+edge.Distance;
				if(tempDistance < targetPath.getDistance())
				{
					targetPath.setDistance(tempDistance);
					targetPath.getPassedIDList().clear();
					for(int i = 0; i<curPath.getPassedIDList().size();i++)
					{
						targetPath.getPassedIDList().add(curPath.getPassedIDList().get(i));
					}
					
					targetPath.getPassedIDList().add(curNode.getPoint_ID());
				}
			}
				
			planCourse.getPassedPath(curNode.getPoint_ID()).setBeProcessed(true);
			curNode = this.getMinDistanceRudeNode(planCourse,nodeList,originID);
			
			//搜索到目的点退出
			if(curNode.getPoint_ID().equals(destID))
			{
				break;
			}
		}
		
		return this.getResult(planCourse,destID);
		
	}
	
	
	private RoutePlanResult getResult(PlanCourse planCourse,GeoPoint destID)
	{
		PassedPath pPath = planCourse.getPassedPath(destID);
		if(pPath.getDistance() == Double.MAX_VALUE)
		{
			RoutePlanResult result1 = new RoutePlanResult(null, Double.MAX_VALUE);
		    return result1;
		}
		
		ArrayList<GeoPoint> passedNodeIDs = new ArrayList<GeoPoint>();
		for(int i=0; i<=pPath.getPassedIDList().size();i++)
		{
			if (i==pPath.getPassedIDList().size()) {
				passedNodeIDs.add(destID);
			}
			else {
				passedNodeIDs.add(pPath.getPassedIDList().get(i));
			}
		}
		
		RoutePlanResult result = new RoutePlanResult(passedNodeIDs, pPath.getDistance());
		return result;
	}
	
	 //从PlanCourse取出一个当前累积权值最小，并且没有被处理过的节点
	private Node getMinDistanceRudeNode(PlanCourse PlanCourse,ArrayList<Node> nodeList,GeoPoint originID)
	{
		double distance = Double.MAX_VALUE;
		Node destNode = null;
		for (Node node : nodeList) {
			
			if((node.getPoint_ID().getLatitudeE6() == originID.getLatitudeE6()) && (node.getPoint_ID().getLongitudeE6() == originID.getLongitudeE6()))
			{
				continue;
			}
			
			PassedPath pPath = PlanCourse.getPassedPath(node.getPoint_ID());
			if(pPath.getBeProcessed())
			{
				continue;
			}
			
			if(pPath.getDistance() < distance)
			{
				distance = pPath.getDistance();
				destNode = node;
			}
			
		}
		return destNode;
	}

}
