package com.example.aimhustermap.route;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.baidu.platform.comapi.basestruct.GeoPoint;



public class RoutePlanner {
	
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI= 6.28318530712; // 2*PI
	static double DEF_PI180= 0.01745329252; // PI/180.0
	static double DEF_R =6370693.5; // radius of earth
	
	public Node startNode;
	public Node endNode;
	public ArrayList<Node> nodes;
	// 开启队列，用于存放待处理的节点
    public	Queue<Node> openQueue ;
	// 关闭队列，用于存放已经处理过的节点
	public Queue<Node> closedQueue ;
	
	public RoutePlanner(GeoPoint start,GeoPoint end,ArrayList<Node> nodes)
	{
		
		this.nodes = nodes;
		
		for (Node node : nodes) {
			if(node.getPointID().equals(start))
			{
				this.startNode = node;
			}
			else if(node.getPointID().equals(end))
			{
				this.endNode = node;
			}
		}
		
		openQueue = new LinkedList<Node>();
		closedQueue = new LinkedList<Node>();
		
		init();
	}
	
	/*
	 * 初始化
	 * 
	 *  将起始节点添加至开启列表，初始化：
	 *  1) 起始节点到当前节点（起始节点）的距离
	 *  2) 当前节点（起始节点）到目的节点的距离
	 *  3) 起始节点经过当前节点（起始节点）到目的节点的距离
	 */
	private void init() {
		openQueue.offer(startNode);
		int start_x = startNode.getPointID().getLatitudeE6();
		int start_y = startNode.getPointID().getLongitudeE6();
		int end_x = endNode.getPointID().getLatitudeE6();
		int end_y = endNode.getPointID().getLongitudeE6();

		// 起始节点到当前节点的距离
		startNode.GList = 0;
		// 当前节点到目的节点的距离
		startNode.HList = GetShortDistance(start_y, start_x, end_y, end_x);
		// f(x) = g(x) + h(x)
		startNode.FList = startNode.GList+startNode.HList;
				
	}
	
	
	/**
	 * 启动搜索迷宫过程主入口
	 * 
	 *   从开启列表中搜索F值最小（即：起始节点 经过某一节点 到目的节点 距离最短），
	 *   将选取的节点作为当前节点，并更新当前节点的邻居节点信息（G、H、F值）以及
	 *   开启列表与关闭列表的成员。   
	 */
	public void start() {
		Node currentPoint;
//       int i = 0;
		while ((currentPoint = findShortestFPoint()) != null) {
			if (currentPoint.equals(endNode))
				return;
			updateNeighborPoints(currentPoint);
//			System.out.println("-------->distance = "+ currentPoint.FList);
//			i++;
		}
//		System.out.println("--------->循环次数 = "+i);
	}
	
	/*
	 * 检查位置是否有效
	 * 
	 *   如果当前位置存在，且不在关闭列表中，则返回"true"，表示为有效位置；
	 *   否则，返回"false"。
	 * 
	 * 输入： 待检查位置的横坐标值
	 *       待检查位置的纵坐标值
	 *       
	 * 输出： 是否有效
	 */
	private boolean checkPosValid(Node point) {
		
		if (nodes.contains(point)) {
			// 检查当前节点是否已在关闭队列中，若存在，则返回 "false"
			Iterator<Node> it = closedQueue.iterator();
			Node node = null;
			while (it.hasNext()) {
				if ((node = it.next()) != null) {
					if (node.equals(point))
						return false;
				}
			}
			return true;
		}
		return false;
	}


	/*
	 * 找寻最短路径所经过的节点
	 * 
	 *   从开启列表中找寻F值最小的节点，将其从开启列表中移除，并置入关闭列表。
	 * 
	 * 输出：最短路径所经过的节点
	 */
	private Node findShortestFPoint() {
		Node currentPoint = null;
		Node shortestFPoint = null;
		double shortestFValue = Double.MAX_VALUE;

		Iterator<Node> it = openQueue.iterator();
		while (it.hasNext()) {
			currentPoint = it.next();
			if (currentPoint.FList <= shortestFValue) {
				shortestFPoint = currentPoint;
				shortestFValue = currentPoint.FList;
			}
		}

		if (shortestFValue != Double.MAX_VALUE) {
			openQueue.remove(shortestFPoint);
			closedQueue.offer(shortestFPoint);
		}

		return shortestFPoint;
	}

	/*
	 * 更新邻居节点
	 * 
	 *   依次判断邻居节点，如果邻居节点有效，则更新距离矢量表。
	 * 
	 * 输入： 当前节点
	 */
	private void updateNeighborPoints(Node currentNode) {
	
		
		for(int i = 0;i< currentNode.getChildNodes().size();i++)
		{
			if(checkPosValid(currentNode.getChildNodes().get(i)))
			{

				updatePoint(currentNode, currentNode.getChildNodes().get(i));
			}
		}
	}

	/*
	 * 更新节点
	 * 
	 *   依次计算：1) 起始节点到当前节点的距离; 2) 当前节点到目的位置的距离; 3) 起始节点经过当前节点到目的位置的距离
	 *   如果当前节点在开启列表中不存在，则：置入开启列表，并且“设置”1)/2)/3)值；
	 *   否则，判断 从起始节点、经过上一节点到当前节点、至目的地的距离 < 上一次记录的从起始节点、到当前节点、至目的地的距离，
	 *   如果有更短路径，则更新1)/2)/3)值
	 * 
	 * 输入： 上一跳节点（又：父节点）
	 *       当前节点
	 */
	private void updatePoint(Node lastPoint, Node currentPoint) {
		int last_x = lastPoint.getPointID().getLatitudeE6();
		int last_y = lastPoint.getPointID().getLongitudeE6();
		int current_x = currentPoint.getPointID().getLatitudeE6();
		int current_y = currentPoint.getPointID().getLongitudeE6();

		// 起始节点到当前节点的距离
		double temp_g = lastPoint.GList + GetShortDistance(last_y, last_x, current_y, current_x);

		// 当前节点到目的位置的距离
		double temp_h = GetShortDistance(current_y, current_x, endNode.getPointID().getLongitudeE6(),endNode.getPointID().getLatitudeE6());
		// f(x) = g(x) + h(x)
		double temp_f = temp_g + temp_h;

		// 如果当前节点在开启列表中不存在，则：置入开启列表，并且“设置”
		// 1) 起始节点到当前节点距离
		// 2) 当前节点到目的节点的距离
		// 3) 起始节点到目的节点距离
		if (!openQueue.contains(currentPoint)) {
			openQueue.offer(currentPoint);
			currentPoint.setFather(lastPoint);

			// 起始节点到当前节点的距离
			currentPoint.GList = temp_g;
			// 当前节点到目的节点的距离
			currentPoint.HList = temp_h;
			// f(x) = g(x) + h(x)
			currentPoint.FList = temp_f;
			
			
		} else {

			// 如果当前节点在开启列表中存在，并且，
			// 从起始节点、经过上一节点到当前节点、至目的地的距离 < 上一次记录的从起始节点、到当前节点、至目的地的距离，
			// 则：“更新”
			// 1) 起始节点到当前节点距离
			// 2) 当前节点到目的节点的距离
			// 3) 起始节点到目的节点距离
			if (temp_f < currentPoint.FList) {
				// 起始节点到当前节点的距离
				currentPoint.GList = temp_g;
				// 当前节点到目的节点的距离
				currentPoint.HList = temp_h;
				// f(x) = g(x) + h(x)
				currentPoint.FList = temp_f;
				// 更新当前节点的父节点
				currentPoint.setFather(lastPoint);
			}
		}
	}


	
	public RoutePlanResult  getRoutePlanResult()
	{
		Node father_point = null;
        ArrayList<GeoPoint>  points = new ArrayList<GeoPoint>();
        ArrayList<Node> passedPoints = new ArrayList<Node>();
        father_point = endNode;
		while (father_point != null) {

//           System.out.println("---------------------->aaaaaaa");
			passedPoints.add(father_point);
			father_point = father_point.getFather();
		}
//		System.out.println("--------passedPoints.size = "+passedPoints.size());
		
		for (int i = passedPoints.size()-1; i >=0 ; i--) {
			points.add(passedPoints.get(i).getPointID());
		}
		
		RoutePlanResult result = new RoutePlanResult(points, startNode.FList);
		
		return result;

	}

	
	/*
	获取地图上两点之间距离的方法
	*/	
	public double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
		{
			double ew1, ns1, ew2, ns2;
			double dx, dy, dew;
			double distance;
			// 角度转换为弧度
			ew1 = lon1 *(1e-6)* DEF_PI180;
			ns1 = lat1 *(1e-6)* DEF_PI180;
			ew2 = lon2 *(1e-6)* DEF_PI180;
			ns2 = lat2 *(1e-6)* DEF_PI180;
			// 经度差
			dew = ew1 - ew2;
			// 若跨东经和西经180 度，进行调整
			if (dew > DEF_PI)
			dew = DEF_2PI - dew;
			else if (dew < -DEF_PI)
			dew = DEF_2PI + dew;
			dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
			dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
			// 勾股定理求斜边长
			distance = Math.sqrt(dx * dx + dy * dy);
			return distance;
		}

}
