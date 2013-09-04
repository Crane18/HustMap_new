package com.example.aimhustermap.db;

import java.util.ArrayList;
import java.util.List;

import com.baidu.platform.comapi.basestruct.GeoPoint;


import android.R.interpolator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBRouteSearcher {
	
	
	private static String TABLESEARCH = "searcher" ;
	private static String DB_NAME = "hustmap.db" ;

	private static String ID = "_id" ;
	private static String NODELON = "nodelon" ;
	private static String NODELAT = "nodelat" ;
	private static String CHILDA = "childA" ;
	private static String CHILDB = "childB" ;
	private static String CHILDC = "childC" ;
	private static String CHILDD = "childD" ;
	private static String CHILDE = "childE" ;	
	private static String CHILDF = "childf" ;
	
	private static int IDINDEX = 0 ;
	private static int NODELONINDEX = 1 ;
	private static int NODELATINDEX = 2 ;
	private static int AINDEX =  3;
	private static int BINDEX =  4;
	private static int CINDEX =  5;
	private static int DINDEX =  6;
	private static int EINDEX =  7;
	private static int FINDEX = 8 ;
	private static int NUMBERINDEX = 9;
	
//	private static String[] CHILDREN = {CHILDA , CHILDB , CHILDC , CHILDD , CHILDE} ;
	private static int[] INDEX = {AINDEX , BINDEX , CINDEX , DINDEX , EINDEX , FINDEX};
	
	
	private SQLiteDatabase db ;
	private Activity activity ;
	
	public DBRouteSearcher(Context context){
		activity = (Activity)context;
		
	}
	
	
	public List<DBRouteNode> selectNode(){
		List<DBRouteNode> nodes = new ArrayList<DBRouteNode>();
		
		db = activity.openOrCreateDatabase(DB_NAME, activity.MODE_WORLD_WRITEABLE, null);
		db.beginTransaction();
		
		String select = " select * from " + TABLESEARCH ;
		Cursor cursor = db.rawQuery(select, null);
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
			DBRouteNode node ;
			
			GeoPoint point = new GeoPoint(cursor.getInt(NODELATINDEX), cursor.getInt(NODELONINDEX));
			int number = cursor.getInt(NUMBERINDEX);
			List<Integer> integers = new ArrayList<Integer>();
			for (int i = 0; i < number; i++) {
				integers.add(cursor.getInt(INDEX[i]));
			}
			
			node = new DBRouteNode(point, integers);
			nodes.add(node);
			
		}
		cursor.close();
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		return nodes ;
	}
	
	
	
	
	
	
	
	

}


