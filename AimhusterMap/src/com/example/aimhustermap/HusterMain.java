package com.example.aimhustermap;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.adapter.No_addressAdapter;
import com.example.aimhustermap.db.DatabaseHust;
import com.example.aimhustermap.db.DatabaseSearcher;
import com.example.aimhustermap.map.MapManager;
import com.example.aimhustermap.route.Edge;
import com.example.aimhustermap.route.Node;
import com.example.aimhustermap.route.RoutePlanResult;
import com.example.aimhustermap.route.RoutePlanner;
import com.umeng.update.UmengUpdateAgent;


@SuppressLint("HandlerLeak")
public class HusterMain extends Activity {
	
	public ArrayList<Node> nodeList;
	RoutePlanner planner;
	public RouteOverlay routeOverlay;
	//获取手机屏幕分辨率的类  
    private DisplayMetrics metrics = new DisplayMetrics();
    int fontSize;//标记文字字体大小
    
	boolean isFirstToast=true;//首次定位不成功时的Toast
	boolean isLocked=true;
    boolean fenleibtn_isLocked = false;
	String[] myPoies=new String[100];
	List<String> recievetextViewList=new ArrayList<String>();
	ArrayAdapter<String> adapter=null;
	PopupWindow pw;
	PopupWindow fenlei_pw;
	boolean fenlei_pw_isshowing=false;//
	public ListView listView=null;
	public EditText editView=null;
	List<DatabaseHust> poiSearch=new ArrayList<DatabaseHust>();
	List<DatabaseHust> datailList=new ArrayList<DatabaseHust>();
	List<GeoPoint> placeList=new ArrayList<GeoPoint>();
	
	private MyOverlay lableOverlay=null;
	String[] poi_nameStrings;
	int[] lat;
	int[] lon ;
	boolean hasAdd=false;//是否添加了标注
	public TextOverlay mTextOverlay=null;
	ArrayList<GeoPoint> poi_points = new ArrayList<GeoPoint>();
	private ArrayList<OverlayItem>  mItems =  new ArrayList<OverlayItem>(); 
	
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI= 6.28318530712; // 2*PI
	static double DEF_PI180= 0.01745329252; // PI/180.0
	static double DEF_R =6370693.5; // radius of earth
	
	        // 定位相关
			LocationClient mLocClient;
			LocationData locData = null;
			EditText editSearch;
			
			ArrayAdapter<String> adapter1;
			ListView list_dropdown;//搜索提示框
			public MyLocationListenner myListener = new MyLocationListenner();
			Button requestLocButton = null;
			boolean isRequest = false;//是否手动触发请求定位
			boolean isFirstLoc = true;//是否首次定位
			boolean locationFinish=false;
			boolean isLocationClientStop = false;
			//定位图层
			MyLocationOverlay myLocationOverlay = null;
            private Button searchButton=null;
            private Button classifyButton=null;
            private Button locButton=null;
            private Button settingButton=null;
            private ImageButton showButton=null;
            private LinearLayout layout;//下面菜单栏
            @SuppressLint("HandlerLeak")
			private  Handler handler  =new Handler()
            {
         	   @Override
         	   public void handleMessage(Message msg)
         	   {
         		  
         		   if (msg.what==2) {
         			   Toast.makeText(HusterMain.this, "不在武汉市或定位不成功，开启GPS或网络！", Toast.LENGTH_SHORT).show();					
				}
         	   }
            };
            private Timer timer =new Timer();
            DatabaseSearcher mySearcher ;
	private MapView mMapView = null;
	private MyOverlay  mOverlay = null;
	MKOfflineMap mOffline = null;//离线地图相关
	Drawable[] drawable =new Drawable[10] ;//覆盖物的图标
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	/**
	 *  MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        ManagerApp app = (ManagerApp)this.getApplication();
        app.addActivity(this);
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(ManagerApp.strKey,new ManagerApp.MyGeneralListener());
        }
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
        
		setContentView(R.layout.activity_huster_main);
		
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if(metrics.heightPixels<=960&&metrics.heightPixels>=800)
		{
			fontSize = 18;
		}
		else if (metrics.heightPixels>=480&&metrics.heightPixels<800) {
			fontSize = 14;
		}
		else {
			fontSize = 25;
		}
		
		mMapView=(MapView)findViewById(R.id.bmapView);
		
		 /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        
        mMapController.setOverlookingGesturesEnabled(false);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(15);
        
        //地图内置缩放控件
       // mMapView.setBuiltInZoomControls(true);
        
        mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);
		mLocClient = new LocationClient( this );
        mTextOverlay=new TextOverlay(mMapView);
        lableOverlay = new MyOverlay(getResources().getDrawable(R.drawable.lable_dot), mMapView);
        
        Resources res = getResources();
		drawable[0]=res.getDrawable(R.drawable.icon_marka);
		drawable[1]=res.getDrawable(R.drawable.icon_markb);
		drawable[2]=res.getDrawable(R.drawable.icon_markc);
		drawable[3]=res.getDrawable(R.drawable.icon_markd);
		drawable[4]=res.getDrawable(R.drawable.icon_marke);
		drawable[5]=res.getDrawable(R.drawable.icon_markf);
		drawable[6]=res.getDrawable(R.drawable.icon_markg);
		drawable[7]=res.getDrawable(R.drawable.icon_markh);
		drawable[8]=res.getDrawable(R.drawable.icon_marki);
		drawable[9]=res.getDrawable(R.drawable.icon_markj);
		
		 nodeList = new ArrayList<Node>();
		
		final GeoPoint point1 = new GeoPoint(30519978,114437019);
		GeoPoint point2 = new GeoPoint(30520086,114436224);
		final GeoPoint point3 = new GeoPoint(30520184,114435605);
		GeoPoint point4 = new GeoPoint(30520425,114433925);
		GeoPoint point5 = new GeoPoint(30520530,114433193);
		GeoPoint point6 = new GeoPoint(30519943,114433759);
		final GeoPoint point7 = new GeoPoint(30519826,114434459);
		GeoPoint point8 = new GeoPoint(30519577,114434450);
		GeoPoint point9 = new GeoPoint(30519701,114435160);
		final GeoPoint point10 = new GeoPoint(30519725,114435946);
		
		Node node1 = new Node(point1);
		Node node2 = new Node(point2);
		Node node3 = new Node(point3);
		Node node4 = new Node(point4);
		Node node5 = new Node(point5);
		Node node6 = new Node(point6);
		Node node7 = new Node(point7);
		Node node8 = new Node(point8);
		Node node9 = new Node(point9);
		Node node10 = new Node(point10);
		nodeList.add(node1);
		nodeList.add(node2);
		nodeList.add(node3);
		nodeList.add(node4);
		nodeList.add(node5);
		nodeList.add(node6);
		nodeList.add(node7);
		nodeList.add(node8);
		nodeList.add(node9);
		nodeList.add(node10);
		
		Edge edge1 = new Edge(point1, point2, 5);
		Edge edge2 = new Edge(point2, point1, 5);
		Edge edge3 = new Edge(point2, point3, 10);
		Edge edge4 = new Edge(point3, point2, 10);
		Edge edge5 = new Edge(point3, point4, 13);
		Edge edge6 = new Edge(point4, point3, 13);
		Edge edge7 = new Edge(point4, point5, 2);
		Edge edge8 = new Edge(point5, point4, 2);
		Edge edge9 = new Edge(point4, point6, 4);
		Edge edge10 = new Edge(point6, point4, 4);
		Edge edge11 = new Edge(point6, point7, 3);
		Edge edge12 = new Edge(point7, point6, 3);
		Edge edge13 = new Edge(point7, point3, 7);
		Edge edge14 = new Edge(point3, point7, 7);
		Edge edge15 = new Edge(point7, point8, 1);
		Edge edge16 = new Edge(point8, point7, 1);
		Edge edge17 = new Edge(point8, point9, 5);
		Edge edge18 = new Edge(point9, point8, 5);
		Edge edge19 = new Edge(point9, point10, 2);
		Edge edge20 = new Edge(point10, point9, 2);
		Edge edge21 = new Edge(point2, point10, 8);
		Edge edge22 = new Edge(point10, point2, 8);
		
		node1.getEdgeList().add(edge1);
		node2.getEdgeList().add(edge2);
		node2.getEdgeList().add(edge3);
		node2.getEdgeList().add(edge21);
		node3.getEdgeList().add(edge4);
		node3.getEdgeList().add(edge5);
		node3.getEdgeList().add(edge14);
		node4.getEdgeList().add(edge6);
		node4.getEdgeList().add(edge7);
		node4.getEdgeList().add(edge9);
		node5.getEdgeList().add(edge8);
		node6.getEdgeList().add(edge10);
		node6.getEdgeList().add(edge11);
		node7.getEdgeList().add(edge12);
		node7.getEdgeList().add(edge13);
		node7.getEdgeList().add(edge15);
		node8.getEdgeList().add(edge17);
		node8.getEdgeList().add(edge16);
		node9.getEdgeList().add(edge18);
		node9.getEdgeList().add(edge19);
		node10.getEdgeList().add(edge20);
		node10.getEdgeList().add(edge22);
		
		final MKRoute route = new MKRoute();
	    routeOverlay = new RouteOverlay(HusterMain.this, mMapView);
		planner = new RoutePlanner();
		
        
        poi_nameStrings=getResources().getStringArray(R.array.poi_name);
        lat=getResources().getIntArray(R.array.lat);
        lon=getResources().getIntArray(R.array.lon);
        for (int i = 0; i < poi_nameStrings.length; i++) {
        	if(i==23)
        	{
        		GeoPoint point = new GeoPoint(lat[i], lon[i]);
        		GeoPoint point_2 = new GeoPoint(lat[i], lon[i]+230);
    			OverlayItem item = new OverlayItem(point, "", poi_nameStrings[i]);
    			mTextOverlay.addText(DrawText(point_2,poi_nameStrings[i],fontSize));
    			lableOverlay.addItem(item);
    			poi_points.add(point);
        	}
        	else {
        		GeoPoint point = new GeoPoint(lat[i], lon[i]);
    			OverlayItem item = new OverlayItem(point, "", poi_nameStrings[i]);
    			mTextOverlay.addText(DrawText(point,poi_nameStrings[i],fontSize));
    			lableOverlay.addItem(item);
    			poi_points.add(point);
			}
			
		}
   
      
      //写在onCreate函数里  
        mOffline = new MKOfflineMap();  
        //offline 实始化方法用更改。  
        
        mOffline.init(mMapController, new MKOfflineMapListener() {  
            @Override  
            public void onGetOfflineMapState(int type, int state) {  
                switch (type) {  
                case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:  
                    {  
                        MKOLUpdateElement update = mOffline.getUpdateInfo(state);  
                        //mText.setText(String.format("%s : %d%%", update.cityName, update.ratio));  
                    }  
                    break;  
                case MKOfflineMap.TYPE_NEW_OFFLINE:  
                    Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));  
                    break;  
                case MKOfflineMap.TYPE_VER_UPDATE:  
                    Log.d("OfflineDemo", String.format("new offlinemap ver"));  
                    break;  
                }      
                  }  
        }  
        ); 
        
//        //检测到wifi且有更新时提示更新
//       if (((WifiManager)this.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled()) {
//        	
//        	//System.out.println("wifi is enable");
//			UmengUpdateAgent.update(this);
//	   	}

        MapManager mapManager=new MapManager(HusterMain.this);
        mapManager.includeMap();
        int num=mOffline.scan();//导入离线地图
        
        mySearcher = new DatabaseSearcher(HusterMain.this);
		
       autoLocation();//初始化时自动定位，定位不成功，则设置中心点为华科南大门
       GeoPoint point_1=new GeoPoint(30513441,114419896); 
       mMapController.setCenter(point_1);

			
		    
        /*地图监听事件
         * */
        mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调
				 * 缩放，平移等操作完成后，此回调被触发
				 */
				
				 double zoomLever=mMapView.getZoomLevel();
			     if(!hasAdd&&zoomLever>18.3)
			     {
			        mMapView.getOverlays().add(mTextOverlay);
			        mMapView.getOverlays().add(lableOverlay);
			        mMapView.refresh();
			        hasAdd=true;
			     }
			     if(hasAdd&&zoomLever<=18.3)
			     {
			    	 mMapView.getOverlays().remove(mTextOverlay);
			    	 mMapView.getOverlays().remove(lableOverlay);
			    	 mMapView.refresh();
			    	 hasAdd=false;
			     }
			   
			     if(layout.isShown())
					{
						layout_dismiss();
					}
			}
			
			@SuppressLint("NewApi")
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件
				 * 显示底图poi名称并移动至该点
				 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				if(layout.isShown())
				{
					 layout_dismiss();
					
				}
			    GeoPoint p=null; 
				if (mapPoiInfo != null){
					p=mapPoiInfo.geoPt;
//					System.out.println("------------->ClickItem:"+"lat="+String.valueOf(p.getLatitudeE6())+"  "+"lon="+
//				    		String.valueOf(p.getLongitudeE6()));
					mySearcher=new DatabaseSearcher(HusterMain.this);
		    		List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
		    		databaseHusts=mySearcher.search(p);
		    		System.out.println("------------------>hhhhhhhhhhhhhhh  dataSize:"+databaseHusts.size());

		    		boolean hasDetail=false;
		    		if(!databaseHusts.isEmpty()){
		                
		    			for (int i = 0; i < databaseHusts.size(); i++) {
							if(!databaseHusts.get(i).buildingName.equals(databaseHusts.get(i).officeNanme) )
							{
								hasDetail=true;
								break;
							}
						}
		    			
		    			if(!hasDetail||(databaseHusts.size()==1&&databaseHusts.get(0).officePhone.isEmpty()))
		    			{
		    				Toast.makeText(HusterMain.this, databaseHusts.get(0).buildingName, Toast.LENGTH_SHORT).show();
		    				mMapController.animateTo(p);
		    				
		    			}
		    			else {
		    				Intent intent=new Intent(HusterMain.this,ShowDetail_Activity.class);
			    			intent.putExtra("x", p.getLongitudeE6());
			    			intent.putExtra("y", p.getLatitudeE6());
			    			startActivity(intent);
						}
		    			
		    			
		    		
		    		}
		    		else{
		    			Toast.makeText(HusterMain.this, mapPoiInfo.strText, Toast.LENGTH_SHORT).show();
		    			
		    		}
					mMapController.animateTo(p);
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
				 *  可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
				if(!mItems.isEmpty())
				{
					OverlayItem item=mItems.get(0);
					item.setMarker(getResources().getDrawable(R.drawable.icon_marka));
					clearOverlay();
					mOverlay.addItem(item);
					mMapView.getOverlays().add(mOverlay);
					mMapView.refresh();
					mItems.clear();
				}
			}
		};
		mMapView.regMapViewListener(ManagerApp.getInstance().mBMapManager, mMapListener);
		
		
		Button test = (Button)findViewById(R.id.toRoute);
		test.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(routeOverlay!=null)
				{
					mMapView.getOverlays().remove(routeOverlay);
					mMapView.refresh();
				}
				RoutePlanResult result = planner.plan(nodeList, point1,point7);
				int size = result.passedNodeIDs.size();
				GeoPoint[] points =(GeoPoint[]) result.passedNodeIDs.toArray(new GeoPoint[size]);
				route.customizeRoute( point1, point7, points);
				routeOverlay.setData(route);

				mMapView.getOverlays().add(routeOverlay);
				mMapView.refresh();
				 // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    mMapController.animateTo(routeOverlay.getCenter());
			}
		});
	
        list_dropdown=(ListView)findViewById(R.id.listview1);
        editSearch = (EditText) findViewById(R.id.searchkey); 
	
		 adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myPoies);
		 list_dropdown.setAdapter(adapter1);
	     classifyButton=(Button)findViewById(R.id.fenlei);
		 locButton=(Button)findViewById(R.id.loc);
		 settingButton=(Button)findViewById(R.id.setting);
		 searchButton=(Button)findViewById(R.id.search);
		 showButton=(ImageButton)findViewById(R.id.showbtn);
		 layout =(LinearLayout)findViewById(R.id.layout1);
		 searchButton.setEnabled(false);
		 
		 searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list_dropdown.setVisibility(View.GONE);
				closeKeyboard();
				if(layout.isShown())
				{
					 
					  layout_dismiss();
					
				}
				ArrayList<OverlayItem> items=new ArrayList<OverlayItem>();
			    clearOverlay();
				 String place=null;
				 place =editSearch.getText().toString();
				 boolean isMatch=false;
				 mySearcher = new DatabaseSearcher(HusterMain.this);
				 placeList=mySearcher.searchGeo(place);
				System.out.println("------------->placeList.Size"+placeList.size());
				 if(!placeList.isEmpty())
				 {
                   int i;
				   for( i=0;i<placeList.size();i++)
				    {
					 if(placeList.get(i).getLatitudeE6()!=0||placeList.get(i).getLongitudeE6()!=0)
					 {
					 OverlayItem item=new OverlayItem(placeList.get(i),"","");
					
					    if(items.isEmpty())
					      {
						     items.add(item);
					       }
					   else {
						  
						   boolean hasExist=false;
						    for(int j=0;j<items.size();j++)
						      {
							     if((item.getPoint().getLatitudeE6()!=items.get(j).getPoint().getLatitudeE6())||(item.getPoint().getLongitudeE6()!=items.get(j).getPoint().getLongitudeE6()))
							    {
								 continue; 
							    }
							     else 
							     {
							    	 hasExist=true;
							    	 break;
								 }
						      }
						     if(!hasExist)
						    	 items.add(item);
					        }
					
					
					 }					
					 System.out.println("----------->RESULT"+String.valueOf(i)+":Lat="+String.valueOf(placeList.get(i).getLatitudeE6())+"   "+"Lon="+String.valueOf(placeList.get(i).getLongitudeE6()));
				    }
				   System.out.println("---------->Size:"+String.valueOf(items.size()));
				   
				 }				 
				 if(!items.isEmpty())
				 {
					 isMatch=true;
				 }
				
				if(!isMatch)
				{
				String iString="抱歉，没有您要的位置数据";
				Toast.makeText(HusterMain.this, iString, Toast.LENGTH_SHORT).show();
				
			     }
				else
				{
					for(int i=0;i<items.size();i++)
					{
						 System.out.println("----------->最终item"+String.valueOf(i)+":Lat="+String.valueOf(items.get(i).getPoint().getLatitudeE6())+"   "+"Lon="+String.valueOf(items.get(i).getPoint().getLongitudeE6()));
					}
					int distance;
					if(locationFinish&&(items.size()==1))
					{
						distance=(int)GetShortDistance(items.get(0).getPoint().getLongitudeE6()*1e-6, items.get(0).getPoint().getLatitudeE6()*1e-6, locData.longitude, locData.latitude);
						Toast.makeText(HusterMain.this,"与你所在位置相距"+ String.valueOf(distance)+"米", Toast.LENGTH_LONG).show();
					}
					initOverlay(items);
			        items.clear();			        
		         }	
			}
		});
		 
		 classifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list_dropdown.setVisibility(View.GONE);
				if(!fenleibtn_isLocked)
				{
					if(fenlei_pw_isshowing)
					{
						fenlei_pw.dismiss();
						if (layout.isShown()) {
							layout_dismiss();
						}
						fenlei_pw_isshowing=false;
					}
					else {
						showfenlei_PopupWindow(v);
						 classifyButton.setBackgroundResource(R.drawable.fenlei1);
					}
				}
				else return;

						               
			}
		});
		
         locButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				list_dropdown.setVisibility(View.GONE);
				if(layout.isShown())
				{
					layout_dismiss();
				}
				
      		    isRequest=true;
			    locationFinish=true;
			    mLocClient.requestLocation();
				Toast.makeText(HusterMain.this, "正在定位…", Toast.LENGTH_SHORT).show();
			   	   
				if(isLocked)
				 {
					 Toast.makeText(HusterMain.this, "不在武汉市或定位不成功，开启GPS或网络！", Toast.LENGTH_SHORT).show();
				}
				else {
					 mMapController.setZoom(15);
				}
				 clearLable();//清除标注物
			}
		});

         
         settingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			  Intent intent=new Intent(HusterMain.this,SettingActivity.class);
			  startActivity(intent);
			  if(layout.isShown())
			  {
				  layout_dismiss();
			  }
			}
		})	;	
       
		 showButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 
				if(!layout.isShown()){
				layout.setVisibility(View.VISIBLE);
				fenleibtn_isLocked = false;
				Animation animation = AnimationUtils.loadAnimation(HusterMain.this, R.anim.translate_display);
				layout.startAnimation(animation);

				}
				else {
					layout_dismiss();
				}
			}
		});
		 
		 list_dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override  
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
		                long arg3) {  
		              
					/**
			    	 * 创建自定义overlay
			    	 * 
			    	 */
					mySearcher = new DatabaseSearcher(HusterMain.this);
					
					 
					 closeKeyboard();
					 
					 ArrayList<OverlayItem> items=new ArrayList<OverlayItem>();
					
			         //mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);	
					System.out.println("----------->list1Size:"+list_dropdown.getCount());
					String place = list_dropdown.getItemAtPosition(arg2).toString();
					System.out.println("---------->1"+place);
			        DatabaseHust    p=mySearcher.clickSearch(place);
			      //  System.out.println("-------------->2"+"Lat="+String.valueOf(p.getLatitudeE6())+"   "+"Lon="+String.valueOf(p.getLongitudeE6()));
					if(p.geoPoint.getLatitudeE6()!=0||p.geoPoint.getLongitudeE6()!=0)
					{
						OverlayItem item=new OverlayItem(p.geoPoint,"",place);
		                  items.add(item);
					}
					if(items.isEmpty())
					{
						clearOverlay();
						ArrayList<DatabaseHust> databaseHusts=new ArrayList<DatabaseHust>();
					
						databaseHusts.add(p);
						showpopupListView(HusterMain.this,editSearch, databaseHusts);
						InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
						imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);//隐藏软键盘
						Toast.makeText(HusterMain.this, "抱歉，无该地的地址信息", Toast.LENGTH_SHORT).show();
					}
					else{
						System.out.println("------------->Size:"+items.size());
						int distance;
						if(locationFinish)
						{
							distance=(int)GetShortDistance(items.get(0).getPoint().getLongitudeE6()*1e-6, items.get(0).getPoint().getLatitudeE6()*1e-6, locData.longitude, locData.latitude);
							Toast.makeText(HusterMain.this,"与你所在位置相距"+ String.valueOf(distance)+"米", Toast.LENGTH_LONG).show();
						}
						
						initOverlay(items);	
						
					}
					editSearch.setText(list_dropdown.getItemAtPosition(arg2).toString());
					editSearch.selectAll();
					list_dropdown.setVisibility(View.GONE);
		        }  
			});
		 
		 editSearch.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable s) {
					
					 String flagString=s.toString().trim();
					if(flagString==null||flagString.length()<=0)
					{
						searchButton.setEnabled(false);
						 adapter1=new ArrayAdapter<String>(HusterMain.this,android.R.layout.simple_list_item_1,new String[]{});	
						 list_dropdown.setAdapter(adapter1);
			               mMapView.getOverlays().remove(mOverlay);
			               mMapView.refresh();
			               list_dropdown.setVisibility(View.GONE);
			             
			    
					}
					else 
					{
						searchButton.setEnabled(true);
						
						 /**
						  * 更新提示数据
                           */
						mySearcher = new DatabaseSearcher(HusterMain.this);
						recievetextViewList=mySearcher.search(editSearch.getText().toString());
						myPoies=recievetextViewList.toArray(new String[recievetextViewList.size()]);
					   
		                if(myPoies.length>0)
		                {
		                	 adapter1=new ArrayAdapter<String>(HusterMain.this,android.R.layout.simple_list_item_1,myPoies);
		                	 list_dropdown.setAdapter(adapter1);
		                	 list_dropdown.setVisibility(View.VISIBLE);
		                	 int  totalHeight = 0;
		                	 int  list_DividerHeight = list_dropdown.getDividerHeight();
				               if(myPoies.length>8)
				               {
				            	   
				            	   for (int i = 0; i < 8; i++) {
				                        View listItem = adapter1.getView(i, null, list_dropdown);
				                        listItem.measure(0, 0); // 计算子项View 的宽高
				                        int list_child_item_height = listItem.getMeasuredHeight()+list_DividerHeight;
				                        totalHeight += list_child_item_height; // 统计所有子项的总高度
				                     }
				            	   LayoutParams params=new LayoutParams(metrics.widthPixels, totalHeight);
				            	   list_dropdown.setLayoutParams(params);

				               }
				               else{
				            	  
				            	   LayoutParams params2=new LayoutParams(metrics.widthPixels, LayoutParams.WRAP_CONTENT);
				            	   list_dropdown.setLayoutParams(params2);
				               }
				               list_dropdown.bringToFront();
				              
				             
		                }
		                else {
		                	list_dropdown.setEmptyView(list_dropdown.getEmptyView());
		                	list_dropdown.setVisibility(View.GONE);
						}
					}
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int arg1,
						int arg2, int arg3) {
				
					
				}
				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2,
						int arg3) {
					
				}
	        });
		 editSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(layout.isShown())
				{
					 layout_dismiss();
					
				}
			}
		});
         
        
	}   
	
	
	
	/*
	 * 响应触摸屏事件
	 * 
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float touchX;
		float touchY;
		touchX = event.getX();
		touchY = event.getY();
		
		float top;
		float bottom;
		float right;
		ViewUtil mViewUtil = new ViewUtil(classifyButton);
		top = mViewUtil.getTop();
		bottom = mViewUtil.getBottom();
		right = mViewUtil.getRight();
		System.out.println("---------->top= "+top+"  bottom="+bottom+"   height="+(bottom-top));
		int screenHeight;
		screenHeight = metrics.heightPixels;
		System.out.println("----------->X="+touchX+"  Y="+touchY+"  screenHeight="+screenHeight);
		
		if(layout.isShown())
		{
			if(fenlei_pw_isshowing)
			{
				fenlei_pw.dismiss();
				fenlei_pw_isshowing=false;
				layout_dismiss();
			}
			else {
				if((touchY>top-12)&&(touchX<=right-5)&&!fenleibtn_isLocked)
				{
					classifyButton.setBackgroundResource(R.drawable.fenlei1);
					showfenlei_PopupWindow(classifyButton);
					fenlei_pw_isshowing = true;
				}
				else {
					layout_dismiss();
				}
			}
			 
			return true;
		}
		else if(list_dropdown.isShown())
		{
			list_dropdown.setVisibility(View.GONE);
			return true;
		}
		else return false;
	}
	
	/*
	 * 
	 * 初始化时自动定位
	 * */
	public void autoLocation()
	{
	        locData=new LocationData();
	        mLocClient.registerLocationListener( myListener );
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);//打开gps
	        option.setCoorType("bd09ll");     //设置坐标类型
	        option.setScanSpan(5000);//设置定时定位的时间间隔。单位ms
	        option.disableCache(false);//是否启用缓存定位
	        mLocClient.setLocOption(option);
	        mLocClient.start();	        
	        myLocationOverlay=new MyLocationOverlay(mMapView);	        
		      //设置定位数据
			    myLocationOverlay.setData(locData);			   
			    myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.icon_geo));
			    //添加定位图层
				mMapView.getOverlays().add(myLocationOverlay);
				myLocationOverlay.enableCompass();
				//修改定位数据后刷新图层生效
				mMapView.refresh();
				
	}
	
	
    /**
     * 清除所有Overlay
     * @param view
     */
    public void clearOverlay(){
    	if(mOverlay!=null)
    	{
    	mOverlay.removeAll();    	
    	mMapView.getOverlays().remove(mOverlay);
    	mMapView.refresh();
    	}
    	else return;
    }
    
    
	/*ListView中的按钮点击事件，直接绑定到标签
	 * 
	 * */
	public void OnFenleiItemBtnClick(View v)
	{
		RelativeLayout layout=(RelativeLayout)v.getParent();
		String phoneNum=((TextView)layout.findViewById(R.id.phonenum_fenlei)).getText().toString();
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"  
              + phoneNum)); 
     	HusterMain.this.startActivity(intent);
	}
	
	public void noaddress_click(View v)
	{
		RelativeLayout layout=(RelativeLayout)v.getParent();
		String phoneNum=((TextView)layout.findViewById(R.id.phonenumber)).getText().toString();
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"  
              + phoneNum)); 
     	HusterMain.this.startActivity(intent);
	}

   /* 
    自定义覆盖物图层
    */
    public class MyOverlay extends ItemizedOverlay{

    	public MyOverlay(Drawable defaultMarker, MapView mapView) {
    		super(defaultMarker, mapView);
    	}
    	

    	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@SuppressLint("NewApi")
		@Override
    	public boolean onTap(int index){
    		OverlayItem item = getItem(index);
    		//Toast.makeText(HusterMain.this, "item: "+String.valueOf(index)+" "+"has been touched", Toast.LENGTH_SHORT).show();
    		System.out.println("------------->ClickItem:"+String.valueOf(index)+"lat="+String.valueOf(item.getPoint().getLatitudeE6())+"  "+"lon="+
    		String.valueOf(item.getPoint().getLongitudeE6()));
    		mySearcher=new DatabaseSearcher(HusterMain.this);
    		List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
    		databaseHusts=mySearcher.search(item.getPoint());
    		System.out.println("------------------>dataSize:"+databaseHusts.size());
    		boolean hasDetail=false;
    		if(!databaseHusts.isEmpty()){
                
    			for (int i = 0; i < databaseHusts.size(); i++) {
					if(!databaseHusts.get(i).buildingName.equals(databaseHusts.get(i).officeNanme) )
					{
						hasDetail=true;
						break;
					}
				}
    			
    			if(!hasDetail||(databaseHusts.size()==1&&databaseHusts.get(0).officePhone.isEmpty()))
    			{
    				Toast.makeText(HusterMain.this, databaseHusts.get(0).buildingName, Toast.LENGTH_SHORT).show();
    				mMapController.animateTo(item.getPoint());
    				return true;
    			}
    			else {
    				Intent intent=new Intent(HusterMain.this,ShowDetail_Activity.class);
        			intent.putExtra("x", item.getPoint().getLongitudeE6());
        			intent.putExtra("y", item.getPoint().getLatitudeE6());
        			startActivity(intent);
				}
    			
    			
    		return true;
    		}
    		
    		else{
    			Toast.makeText(HusterMain.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
    			mMapController.animateTo(item.getPoint());
    			return true;
    		}
    	}
    	
    	@Override
    	public boolean onTap(GeoPoint pt , MapView mMapView){
//    		if (pop != null){
//                pop.hidePop();
//                mMapView.refresh();
//    		}
    		return false;
    	}
    	
    }
	  
	  
	
	  /*关于弹出悬浮框的方法
	   * 
	   * */	
	  private void showfenlei_PopupWindow(View anchor)
	    {        
	       
	        // 【Ⅰ】 获取自定义popupWindow布局文件
	
	        LayoutInflater inflater = (LayoutInflater) HusterMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
	        final View vPopupWindow = inflater.inflate(R.layout.popupwindow, null, false);
	        vPopupWindow.setBackgroundColor(Color.rgb(240, 255, 240));
	    
	        // 【Ⅳ】自定义布局中的事件响应
	    	// OK按钮及其处理事件
	    	  final Button btnmess = (Button) vPopupWindow.findViewById(R.id.mess);	    	                   
              final Button btnmarket=(Button)vPopupWindow.findViewById(R.id.market);
              final Button btnatm=(Button)vPopupWindow.findViewById(R.id.atm);
              final Button btnprint=(Button)vPopupWindow.findViewById(R.id.printshop);
                    
              ViewUtil myUtil1=new ViewUtil(btnmess);
              ViewUtil myUtil2=new ViewUtil(btnmarket);
              ViewUtil myUtil3=new ViewUtil(btnatm);
              ViewUtil myUtil4=new ViewUtil(btnprint);
              int height=myUtil1.getHeight()+myUtil2.getHeight()+myUtil3.getHeight()+myUtil4.getHeight();
	    	  // 【Ⅲ】 显示popupWindow对话框
	    	// 获取屏幕和对话框各自高宽
	          int screenWidth;
	          screenWidth = metrics.widthPixels;
	          fenlei_pw = new PopupWindow(vPopupWindow, screenWidth/3,height, false);// 声明一个弹出框 ，最后一个参数和setFocusable对应
	          fenlei_pw.setContentView(vPopupWindow);   // 为弹出框设定自定义的布局 
	          fenlei_pw.setAnimationStyle(R.style.PopupAnimation);
	          fenlei_pw.setOutsideTouchable(true);
	          fenlei_pw.setBackgroundDrawable(new BitmapDrawable());        
	          fenlei_pw.showAsDropDown(anchor, 0, 0);
			  fenlei_pw_isshowing=true;	
			  
			  
			  
	       btnmess.setOnClickListener(new OnClickListener()
	        {
	            @Override
	            public void onClick(View v)
	            {	            	
	            	
	            	fenlei_pw.dismiss();// 关闭
	            	fenlei_pw_isshowing = false;
	                String  pString=btnmess.getText().toString();
	                toClassifyListview(pString);
	            }
	        }); 
	       
	       btnmarket.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fenlei_pw.dismiss();// 关闭
				fenlei_pw_isshowing = false;
                String pString=btnmarket.getText().toString();
                toClassifyListview(pString);
			}
		});
	       
	       btnatm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fenlei_pw.dismiss();// 关闭
				fenlei_pw_isshowing = false;
                String pString=btnatm.getText().toString();
                toClassifyListview(pString);
			}
		});
	       
	       btnprint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fenlei_pw.dismiss();// 关闭
				fenlei_pw_isshowing = false;
                String pString=btnprint.getText().toString();
                toClassifyListview(pString);
			}
		});
	
	        
	    }
	  
	 /*
	  *  跳转到分类页面
	  * */
	  public void toClassifyListview(String pString)
	  {
		  Bundle data=new Bundle();
          data.putSerializable("pString", pString);	                
          Intent intent=new Intent(HusterMain.this,ClassifyListView.class);
          intent.putExtras(data);
          startActivityForResult(intent, 0);
          if(layout.isShown())
      	    {
      		   layout_dismiss();
      	    }
	  }

	  /*
	   *  接收另一个Activity的Intent 
	   * */	  
     @Override	  
       public void onActivityResult(int requestCode,int resultCode,Intent intent)
         {
	          if(requestCode==0&&resultCode==0)
	            {
		
		          try {
			             Bundle dataBundle=intent.getExtras();
			             String officename=dataBundle.getString("key");
			             mySearcher=new DatabaseSearcher(HusterMain.this);
			             editSearch.setText(officename);
			             editSearch.selectAll();
			             list_dropdown.setVisibility(View.GONE);	
		                 List<GeoPoint>  geoPoints=mySearcher.searchGeo(officename);
		                 OverlayItem item=new OverlayItem(geoPoints.get(0), "", officename);
			             mItems.add(item);
		                 mMapController.animateTo(geoPoints.get(0));
			             Toast.makeText(HusterMain.this, officename, Toast.LENGTH_SHORT).show();
			
		            } 
		          catch (Exception e) {
			// TODO: handle exception
			          e.printStackTrace();
		            }		
	          }
	
          }
   
	/*
	 *  无地址信息时弹出的悬浮框
	 * 
	 * */
  public void showpopupListView(Context context,View anchor,List<DatabaseHust>  databaseHusts)
	  {
		  
		
	        ///////////////////////////////////////////////////////
			// 【Ⅰ】 获取自定义popupWindow布局文件
			
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
		final View vPopupWindow = inflater.inflate(R.layout.popuplistview, null, false);
	    No_addressAdapter adapter=new No_addressAdapter(HusterMain.this, databaseHusts);
		listView = (ListView) vPopupWindow.findViewById(R.id.listView);
		listView.setAdapter(adapter);
		
			        vPopupWindow.setBackgroundColor(Color.WHITE);			     
			        // 获取屏幕和对话框各自高宽
			        pw = new PopupWindow(vPopupWindow,metrics.widthPixels,LayoutParams.WRAP_CONTENT, true); // 声明一个弹出框 ，最后一个参数和setFocusable对应
			        pw.setContentView(vPopupWindow);   // 为弹出框设定自定义的布局 
			        pw.setOutsideTouchable(true);
			        pw.setBackgroundDrawable(new BitmapDrawable());
			// 【Ⅲ】 显示popupWindow对话框
		           pw.update();
		           pw.showAsDropDown(anchor);
		  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override  
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
		                long arg3) { 
					
					Toast.makeText(HusterMain.this, "抱歉，无该地的地址信息！！", Toast.LENGTH_SHORT).show();
		        }  
			});
	
		       
	  }
	
     /*
      * 收回菜单栏
      * 
      * */ 
  public void layout_dismiss()
  {
	  layout.setVisibility(View.GONE);
	  classifyButton.setBackgroundResource(R.drawable.fenlei2);
	  fenleibtn_isLocked = true;
		Animation animation=AnimationUtils.loadAnimation(HusterMain.this,R.anim.translate_dismiss);
		layout.startAnimation(animation);
  }
 

  
    /**
     * 定位SDK监听函数
     */
	   public class MyLocationListenner implements BDLocationListener {
	    	
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            if (location == null || isLocationClientStop)
	                return ;
	  
	            double left_edge=113.997122;
	            double right_edge=114.609982;
	            double top_edge=30.683752;
	            double bottom_edge=30.439953;

	            if(location.getLongitude()>=left_edge&&location.getLongitude()<=right_edge&&location.getLatitude()>=bottom_edge&&location.getLatitude()<=top_edge)
	            {
	            	isLocked=false;
	            	locData.latitude = location.getLatitude();
	 	            locData.longitude = location.getLongitude();
	 	            //如果不显示定位精度圈，将accuracy赋值为0即可
	 	            if(location.getRadius()>100)
	 	            {
	 	            	locData.accuracy=100;
	 	            }
	 	            else {
	 	            	locData.accuracy = location.getRadius();
	 				}
	 	     
	 	            locData.direction = location.getDerect();
	 	            //更新定位数据
	 	            myLocationOverlay.setData(locData);
	 	            //更新图层数据执行刷新后生效
	 	            mMapView.refresh();
	 	            //是手动触发请求或首次定位时，移动到定位点
	 	            if (isRequest || isFirstLoc){
	 	            	//移动地图到定位点
	 	                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	 	                isRequest = false;
	 	            }
	 	            //首次定位完成
	 	            isFirstLoc = false;
	 	            locationFinish=true;
	            }
	            else {
	            	isLocked=true;
	            	locationFinish=false;
	            	if(isFirstToast)
	            	{
	            		timer.schedule(new TimerTask() {
		        			
		        			@Override
		        			public void run() {
		        				// TODO Auto-generated method stub
		        				Message msg=new Message();
		        				msg.what=2;
		        				handler.sendMessage(msg);
		        			}
		        		}, 0);
	            		isFirstToast=false;
	            	}
	            	
				}
	   
	        }
	        
	        public void onReceivePoi(BDLocation poiLocation) {
	            if (poiLocation == null){
	                return ;
	            }
	        }
	    }
 

	 @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		   isLocationClientStop = true;
		   int  cityID=Integer.parseInt("218");
		   mOffline.pause(cityID);
	        mMapView.onPause();
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
	    	 */

	    	isLocationClientStop=false;
	        mMapView.onResume();
	        super.onResume();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
	    	 */
	    	//退出时销毁定位
	    	ManagerApp app=(ManagerApp)this.getApplication();
	    	
	        if (mLocClient != null)
	            mLocClient.stop();
	        isLocationClientStop = true;
	        if(mOffline!=null)
	        {
	        	 mOffline.destroy();
	        }	       
	        mMapView.destroy();
	        if (app.mBMapManager != null) {
				app.mBMapManager.destroy();
				app.mBMapManager = null;
			}
	        super.onDestroy();
	        System.exit(0);
	    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_huster_main, menu);
		
	  Intent intent=new Intent(HusterMain.this,SettingActivity.class);
	  startActivity(intent);
	  if(layout.isShown())
	  {
		  layout_dismiss();
	  }
		return false;
	}
	
	
	/*初始化Overlay
	 * 
	 * */	
	public void initOverlay(ArrayList<OverlayItem> items)
	{
		int lonFlag;
	    ArrayList<OverlayItem>  overlayItems=new ArrayList<OverlayItem>();
		int start;
		int end = 0;
		int flag=items.size()>10?10:items.size();
		if(flag!=0){
		  for(int i=0;i<flag;i++)
		    {
			   items.get(i).setMarker(drawable[i]);
			   overlayItems.add(items.get(i));
		    }
		
		     clearOverlay();
		 
		     mOverlay.addItem(overlayItems);
		     mMapView.getOverlays().add(mOverlay);
		  if(locationFinish)
		   {
			   int locLon=(int)(locData.longitude *  1e6);
			   int temp1=findthelargestLon(overlayItems);
			   int temp2=findthesmallestLon(overlayItems);
//			 int temp3=findthelargestLat(items);
//			 int temp4=findthesmallestLat(items);
			   start=4800;
			    lonFlag=temp1-overlayItems.get(0).getPoint().getLongitudeE6();
			
			if(temp2>locLon)
			{
				
				if((overlayItems.get(0).getPoint().getLongitudeE6()-locLon)>lonFlag)
				{
					
					end=2*(overlayItems.get(0).getPoint().getLongitudeE6()-locLon);
					mMapController.zoomToSpan(start, end);
					System.out.println("---------->Start="+String.valueOf(start)+"  "+"end="+String.valueOf(end));
				}
				else {
					end=2*lonFlag;
					mMapController.zoomToSpan(start, end);
				}
			}
			else if(locLon>temp1)
			{
				if((locLon-overlayItems.get(0).getPoint().getLongitudeE6())>(overlayItems.get(0).getPoint().getLongitudeE6()-temp2))
				{ 
					end=2*(locLon-overlayItems.get(0).getPoint().getLongitudeE6());
				    mMapController.zoomToSpan(start, end);
				    }
				else  {
					end=2*(overlayItems.get(0).getPoint().getLongitudeE6()-temp2);
					mMapController.zoomToSpan(start, end);
				}
				System.out.println("---------->Start="+String.valueOf(start)+"  "+"end="+String.valueOf(end));
			}			
			
		}
		
		else {
			
			int temp1=findthelargestLon(overlayItems);
			
			int temp2=findthesmallestLon(overlayItems);
			
			start=4800;
			
			if((temp1-overlayItems.get(0).getPoint().getLongitudeE6())>(overlayItems.get(0).getPoint().getLongitudeE6()-temp2))
			{
				lonFlag=temp1-overlayItems.get(0).getPoint().getLongitudeE6();
				end=2*lonFlag;
			}
			else if ((temp1-overlayItems.get(0).getPoint().getLongitudeE6())<=(overlayItems.get(0).getPoint().getLongitudeE6()-temp2)) {
				lonFlag=overlayItems.get(0).getPoint().getLongitudeE6()-temp2;
				end=2*lonFlag;
			}
			if(end!=0)
			{
				mMapController.zoomToSpan(start, end);
			}
		
		}
		
		clearLable();//根据缩放级别清除标注
		//mMapView.refresh();
		mMapController.animateTo(items.get(0).getPoint());
		}
		
		else			
			return;		
	}
	
	/*找出搜到覆盖物的最大最小经度
	 * 
	 * */
	public int findthelargestLon(ArrayList<OverlayItem> items)
	{
		int  largestLon;
		largestLon=items.get(0).getPoint().getLongitudeE6();
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getPoint().getLongitudeE6()>largestLon)
			{
				largestLon=items.get(i).getPoint().getLongitudeE6();
			}
		}
		return largestLon;
	}
	public int findthesmallestLon(ArrayList<OverlayItem> items)
	{
		int  smallestLon;
		smallestLon=items.get(0).getPoint().getLongitudeE6();
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getPoint().getLongitudeE6()<smallestLon)
			{
				smallestLon=items.get(i).getPoint().getLongitudeE6();
			}
		}
		return smallestLon;
	}
	
	
	
	/*
	 *  在地图上标注地名
	 * 
	 * */		
 	public TextItem DrawText(GeoPoint p,String poiName,int fontSize)
	{
		TextItem item=new TextItem();
		item.pt=p;
		item.text=poiName;
		//设文字大小
    	item.fontSize = fontSize;
    	Symbol symbol = new Symbol();
    	Symbol.Color bgColor = symbol.new Color();
    	//设置文字背景色
    	bgColor.red = 255;
    	bgColor.blue = 255;
    	bgColor.green = 255;
    	bgColor.alpha = 0;
    	
    	Symbol.Color fontColor = symbol.new Color();
    	//设置文字着色
    	fontColor.alpha = 180;
    	fontColor.red = 0;
    	fontColor.green = 0;
    	fontColor.blue  = 0;
    	//设置对齐方式
    	item.align = TextItem.ALIGN_BOTTOM;
    	//设置文字颜色和背景颜色
    	item.fontColor = fontColor;
    	item.bgColor  = bgColor ; 
    	return item;
	}

 	
 	/*
 	 * 根据缩放级别判断是否删除标注物
 	 * */
	public void clearLable()
	{
		 double zoomLever=mMapView.getZoomLevel();
		
		 if(hasAdd&&zoomLever<=18.3)
	     {
	    	 mMapView.getOverlays().remove(mTextOverlay);
	    	 mMapView.getOverlays().remove(lableOverlay);
	    	 mMapView.refresh();
	    	 hasAdd=false;
	     }
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
			ew1 = lon1 * DEF_PI180;
			ns1 = lat1 * DEF_PI180;
			ew2 = lon2 * DEF_PI180;
			ns2 = lat2 * DEF_PI180;
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
	
	/*
	 * 控制软键盘消失
	 * 
	 * */
	public  void closeKeyboard()
	{
		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0); 
	}
	
	 
     @Override 
	 public boolean onKeyDown(int keyCode, KeyEvent event) { 
	        if (keyCode == KeyEvent.KEYCODE_BACK /*&& event.getRepeatCount() == 0*/) { 
	          if(fenlei_pw_isshowing)
	          {
	        	  fenlei_pw.dismiss();
	        	  fenlei_pw_isshowing=false;
	        	 if(layout.isShown())
	        	 {
	        		 layout_dismiss();
	        	 }
	        	 return true;
	          }
	          else if (layout.isShown()) {
	        	  layout_dismiss();
				return true;
			}
	          else {
				HusterMain.this.finish();
				return true;
			}
	        } 
	        return false; 
	    }
	 
}
