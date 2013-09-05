package com.example.aimhustermap;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
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
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.R;
import com.example.aimhustermap.adapter.No_addressAdapter;
import com.example.aimhustermap.db.DBRouteNode;
import com.example.aimhustermap.db.DBRouteSearcher;
import com.example.aimhustermap.db.DatabaseHust;
import com.example.aimhustermap.db.DatabaseSearcher;
import com.example.aimhustermap.map.MapManager;
import com.example.aimhustermap.route.Node;
import com.example.aimhustermap.route.RoutePlanResult;
import com.example.aimhustermap.route.RoutePlanner;
import com.example.aimhustermap.util.BMapUtil;
import com.example.aimhustermap.util.ViewUtil;
import com.umeng.update.UmengUpdateAgent;


@SuppressLint("HandlerLeak")
public class HusterMain extends Activity {
	
	public String fenleiString = null;//记录所选的分类类别
	public GeoPoint clickPoint = null;
	//地图区域边界
	static int Awedge = 114411773;
	static int Aeedge = 114423949;
	static int Asedge = 30512946;
	static int Anedge = 30515357;
	
	static int Bwedge = 114430156;
	static int Beedge = 114441399;
	static int Bsedge = 30511352;
	static int Bnedge = 30515871;
	
	static int SNBorder = 30520134;
	
	static int EWBorder = 114431917;
	static int EWBorder1 = 114428513;
	
	static int sideLenght = 2800;
	static int optimizedLenght = 600;
	
	RoutePlanner planner;
	public MyRouteOverlay routeOverlay;
	private MKRoute route = null;
	private ArrayList<Node> nodeList = null;
	
	private PopupOverlay pop = null;
	private TextView  popupText = null;
	private View viewCache = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	private OverlayItem  mCurItem = null;
	
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
         		   if (msg.what == 0) {
         			  if(!mItems.isEmpty())
      				   {
      					  clearOverlay();
      					  OverlayItem item= new OverlayItem(clickPoint, "", "");
      					  mCurItem = item;
      					  mOverlay.addItem(mItems);
      					  Bitmap[] bitMaps={
      							        BMapUtil.getBitmapFromView(popupLeft), 		
//      							    BMapUtil.getBitmapFromView(popupInfo), 		
      							        BMapUtil.getBitmapFromView(popupRight) 		
      						    };
      				      pop.showPopup(bitMaps,item.getPoint(),48);
      					  mMapView.getOverlays().add(mOverlay);
      					  mMapView.refresh();
//      					  mMapController.setZoom(17);
      					  mItems.clear();
      				 }
				}
         	   }
            };
    private Timer timer =new Timer();
    private DatabaseSearcher mySearcher ;
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
	MKMapTouchListener mapTouchListener = null; 
	/**
	 * 当前地点击点
	 */
	private GeoPoint currentPt = null; 
	
	/*
	 * 路线搜索相关
	 * */
	public List<DBRouteNode>  DBnodesList = null;
	public DBRouteSearcher myRouteSearcher = null;
	public ArrayList<Node> lableNodes = new ArrayList<Node>();
	public ArrayList<Node> slableNodes = new ArrayList<Node>();
	
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
        mMapController.setZoom(16);
        
//        mMapView.setSatellite(true);
        
        
        
        viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
//        popupInfo = (View) viewCache.findViewById(R.id.popinfo);
        popupLeft = (View) viewCache.findViewById(R.id.popleft);
        popupRight = (View) viewCache.findViewById(R.id.popright);
//        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        
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
		

	    route = new MKRoute();
	    routeOverlay = new MyRouteOverlay(HusterMain.this, mMapView);
		
        
        poi_nameStrings=getResources().getStringArray(R.array.poi_name);
        lat=getResources().getIntArray(R.array.lat);
        lon=getResources().getIntArray(R.array.lon);
        for (int i = 0; i < poi_nameStrings.length; i++) {
        	if(i==23)
        	{
        		GeoPoint point = new GeoPoint(lat[i], lon[i]);
        		GeoPoint point_2 = new GeoPoint(lat[i], lon[i]+230);
    			OverlayItem item = new OverlayItem(point, poi_nameStrings[i],"" );
    			mTextOverlay.addText(DrawText(point_2,poi_nameStrings[i],fontSize));
    			lableOverlay.addItem(item);
    			poi_points.add(point);
        	}
        	else {
        		GeoPoint point = new GeoPoint(lat[i], lon[i]);
    			OverlayItem item = new OverlayItem(point,poi_nameStrings[i], "");
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
        
       //实例化地图节点 
        myRouteSearcher = new DBRouteSearcher(HusterMain.this);
        DBnodesList = myRouteSearcher.selectNode();
        nodeList = new ArrayList<Node>();
        for (int i = 0; i < DBnodesList.size(); i++) {
			Node node1 = new Node(DBnodesList.get(i).node);
			nodeList.add(node1);
		}
        
        for (int i = 0; i < nodeList.size(); i++) {
			for (int j = 0; j < DBnodesList.get(i).nodeChildren.size(); j++) {
				nodeList.get(i).getChildNodes().add(nodeList.get(DBnodesList.get(i).nodeChildren.get(j)-1));
			}
		}
        
        GeoPoint point1 = new GeoPoint(30520985,114428513);
        GeoPoint point2 = new GeoPoint(30521055,114427780);
        GeoPoint point3 = new GeoPoint(30521152,114426756);
        GeoPoint point4 = new GeoPoint(30521432,114424084);
        GeoPoint point5 = new GeoPoint(30521634,114421596);
        GeoPoint point6 = new GeoPoint(30521848,114419503);
        GeoPoint point7 = new GeoPoint(30522023,114417657);
        GeoPoint point8 = new GeoPoint(30522202,114415779);
//        GeoPoint point9 = new GeoPoint(30520191,114435609);
//        GeoPoint point10 = new GeoPoint(30520580,114432905);
//        GeoPoint point11 = new GeoPoint(30520736,114431225);
//        GeoPoint point12 = new GeoPoint(30520806,114430579);
//        GeoPoint point13 = new GeoPoint(30520891,114429407);
        
        GeoPoint sPoint1 = new GeoPoint(30513448, 114415214);
        GeoPoint sPoint2 = new GeoPoint(30513619, 114416687);
        GeoPoint sPoint3 = new GeoPoint(30513440, 114419894);
        GeoPoint sPoint4 = new GeoPoint(30513074, 114423478);
        for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getPointID().equals(sPoint1)) {
				slableNodes.add(nodeList.get(i));
			}
			if (nodeList.get(i).getPointID().equals(sPoint2)) {
				slableNodes.add(nodeList.get(i));
			}
			if (nodeList.get(i).getPointID().equals(sPoint3)) {
				slableNodes.add(nodeList.get(i));
			}
			if (nodeList.get(i).getPointID().equals(sPoint4)) {
				slableNodes.add(nodeList.get(i));
			}
		}
        System.out.println("------------->slableNodes.size = "+slableNodes.size());
        
        
        ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        points.add(point5);
        points.add(point6);
        points.add(point7);
        points.add(point8);
//        points.add(point9);
//        points.add(point10);
//        points.add(point11);
//        points.add(point12);
//        points.add(point13);
//        int temp = 0;
        for (int i = 0; i < points.size(); i++) {
        	lableNodes.add(findnearestNode(points.get(i), nodeList));
		}
        
        
		
       autoLocation();//初始化时自动定位，定位不成功，则设置中心点为华科南大门
       GeoPoint point_1=new GeoPoint(30513441,114419896); 
       mMapController.setCenter(point_1);
       

       /**
        * 创建一个popupoverlay
        */
        PopupClickListener popListener = new PopupClickListener(){
			@SuppressLint("NewApi")
			@Override
			public void onClickedPopup(int index) {
				if ( index == 0){
				
		    		mySearcher=new DatabaseSearcher(HusterMain.this);
		    		List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
		    		databaseHusts=mySearcher.search(mCurItem.getPoint());
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
		    				mMapController.animateTo(mCurItem.getPoint());
		    				
		    			}
		    			else {

		    				Intent intent=new Intent(HusterMain.this,ShowDetail_Activity.class);
		        			intent.putExtra("x", mCurItem.getPoint().getLongitudeE6());
		        			intent.putExtra("y", mCurItem.getPoint().getLatitudeE6());
		        			startActivity(intent);
						}		
		    		
		    		}
		    		
		    		else{

		    			Toast.makeText(HusterMain.this, mCurItem.getTitle(), Toast.LENGTH_SHORT).show();
		    			mMapController.animateTo(mCurItem.getPoint());
		    		
		    		}
				}
				else if(index == 1){
						if (routeOverlay != null) {
							mMapView.getOverlays().remove(routeOverlay);
							mMapView.refresh();
						}	
						pop.hidePop();
						mMapView.refresh();
//					showRouteSearchPopupWindow();
					if (locationFinish) {
						
						double distance = Double.MAX_VALUE;
						double lat = locData.latitude;
						double lon = locData.longitude;
//						double lat = 30.517407;
//						double lon = 114.409271;		
						ArrayList<GeoPoint>  passedPoints = new ArrayList<GeoPoint>();
						GeoPoint locGeoPoint = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
//						GeoPoint transitonPoint = new GeoPoint(arg0, arg1)
						Node startNode = PointToNode(locGeoPoint, nodeList, mCurItem.getPoint());
						Node endNode = PointToNode(mCurItem.getPoint(), nodeList, locGeoPoint);

						Node tempNode = getTransitionNode(startNode, endNode);
						System.out.println(" --------> startNode = "+startNode.getPointID()+"\n -------> endNode = "+endNode.getPointID());
						System.out.println(" nodeList.Size = "+nodeList.size());
						if (tempNode != null) {
							planner = new RoutePlanner(startNode,tempNode,nodeList);
							RoutePlanResult result1 = planner.getRoutePlanResult();
							planner = new RoutePlanner(tempNode, endNode, nodeList);
							RoutePlanResult result2 = planner.getRoutePlanResult();
							for (int i = 0; i < result1.passedNodeIDs.size(); i++) {
								passedPoints.add(result1.passedNodeIDs.get(i));
							}
							for (int i = 0; i < result2.passedNodeIDs.size(); i++) {
								passedPoints.add(result2.passedNodeIDs.get(i));
							}
							distance = result1.getDistance() + result2.getDistance();
						}
						else {
							planner = new RoutePlanner(startNode,endNode, nodeList);
			                RoutePlanResult result = planner.getRoutePlanResult();
			                distance = result.getDistance();
			                for (int i = 0; i < result.passedNodeIDs.size(); i++) {
								passedPoints.add(result.passedNodeIDs.get(i));
							}
						}
						
						int size = passedPoints.size();
		                if(size > 1)
		                {
		    			    GeoPoint[] points = (GeoPoint[])passedPoints.toArray(new GeoPoint[size]);
		    			    route.customizeRoute( locGeoPoint, mCurItem.getPoint(), points);
		    				routeOverlay.setData(route);
		    				mMapView.getOverlays().add(routeOverlay);
		    				mMapView.refresh();
		    				Toast.makeText(HusterMain.this,"全程"+String.valueOf((int)distance)+"米", Toast.LENGTH_SHORT).show();
		    				 // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
		    			    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
		    			   
		    			    clearLable();
		    			    if (mOverlay != null) {
								mMapView.getOverlays().remove(mOverlay);
								mMapView.refresh();
							}
		    			    mMapController.animateTo(getTheCenter(locGeoPoint, mCurItem.getPoint()));
		                }
		                else {
							Toast.makeText(HusterMain.this, "抱歉，未找到路径！", Toast.LENGTH_SHORT).show();
						}
						
					}
					
					else {
						Toast.makeText(HusterMain.this, "请先定位确定起始点！", Toast.LENGTH_SHORT).show();
					}
				    
				}
			}
       };
        pop = new PopupOverlay(mMapView,popListener);	
		    
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
				if(mOverlay != null)
				{
					mMapView.getOverlays().remove(mOverlay);
					mMapView.refresh();
				}
			    GeoPoint p=null; 
				if (mapPoiInfo != null){
					p=mapPoiInfo.geoPt;
					
					mCurItem = new OverlayItem(p, mapPoiInfo.strText, "");
					
					System.out.println("mapPoi = "+mCurItem);
//					 popupText.setText(mapPoiInfo.strText);
					   Bitmap[] bitMaps={
						    BMapUtil.getBitmapFromView(popupLeft), 		
//						    BMapUtil.getBitmapFromView(popupInfo), 		
						    BMapUtil.getBitmapFromView(popupRight) 		
					    };
					    pop.showPopup(bitMaps,p,5);
					
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
//				if(!mItems.isEmpty())
//				{
//					clearOverlay();
//					OverlayItem item= new OverlayItem(clickPoint, "", "");
//					mCurItem = item;
////					item.setMarker(getResources().getDrawable(R.drawable.icon_en));
////					mOverlay.addItem(item);
//					mOverlay.addItem(mItems);
//					Bitmap[] bitMaps={
//							    BMapUtil.getBitmapFromView(popupLeft), 		
////							    BMapUtil.getBitmapFromView(popupInfo), 		
//							    BMapUtil.getBitmapFromView(popupRight) 		
//						    };
//				    pop.showPopup(bitMaps,item.getPoint(),48);
//					mMapView.getOverlays().add(mOverlay);
//					mMapView.refresh();
//					mItems.clear();
//					System.out.println(" ---------------->mItems.size = "+mItems.size());
//				}
			}
           
			@Override
			public void onMapLoadFinish() {
				
				//地图初始化完成时，此回调被触发
				// TODO Auto-generated method stub
				
			}
		};
		mMapView.regMapViewListener(ManagerApp.getInstance().mBMapManager, mMapListener);
		
		/*地图点击事件监听
		 * */
		mapTouchListener = new MKMapTouchListener() {
			
			@Override
			public void onMapLongClick(GeoPoint arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMapDoubleClick(GeoPoint arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMapClick(GeoPoint point) {
				// TODO Auto-generated method stub
				
				System.out.println("---------->mapTouched !");
				currentPt = point;
				if(mCurItem != null)
				{
					if(!currentPt.equals(mCurItem.getPoint()))
					{
						if(pop != null)
						{
							pop.hidePop();
							mMapView.refresh();
						}
					}
					else {
//						 popupText.setText(mCurItem.getTitle());
						   Bitmap[] bitMaps={
							    BMapUtil.getBitmapFromView(popupLeft), 		
//							    BMapUtil.getBitmapFromView(popupInfo), 		
							    BMapUtil.getBitmapFromView(popupRight) 		
						    };
						    pop.showPopup(bitMaps,mCurItem.getPoint(),32);
					}
				}
//				else {
//					Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。 
//
//					t.setToNow(); // 取得系统时间。 
//					int second = t.second; 
//					for (int i = 0; i < 1000000; i++) {
//						ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
////						System.out.println("-------------->i = "+i);
//					}
//					t.setToNow(); // 取得系统时间。 
//					int second_end = t.second; 
//					System.out.println("所需时间为： "+(second_end-second));
//				}
				
			}
		};
		mMapView.regMapTouchListner(mapTouchListener);
		
	
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
				if (mOverlay != null) {
					mMapView.getOverlays().remove(mOverlay);
					mMapView.refresh();
				}
				ArrayList<OverlayItem> items=new ArrayList<OverlayItem>();
			    clearOverlay();
				 String place=null;
				 place =editSearch.getText().toString();
//				 boolean isMatch=false;
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
					 
					 if (!items.contains(item)) {
						items.add(item);
					}
					
//					    if(items.isEmpty())
//					      {
//						     items.add(item);
//					       }
//					   else {
//						  
//						   boolean hasExist=false;
//						    for(int j=0;j<items.size();j++)
//						      {
//							     if((item.getPoint().getLatitudeE6()!=items.get(j).getPoint().getLatitudeE6())||(item.getPoint().getLongitudeE6()!=items.get(j).getPoint().getLongitudeE6()))
//							    {
//								 continue; 
//							    }
//							     else 
//							     {
//							    	 hasExist=true;
//							    	 break;
//								 }
//						      }
//						     if(!hasExist)
//						    	 items.add(item);
//					        }
					
					
					 }					
					 System.out.println("----------->RESULT"+String.valueOf(i)+":Lat="+String.valueOf(placeList.get(i).getLatitudeE6())+"   "+"Lon="+String.valueOf(placeList.get(i).getLongitudeE6()));
				    }
				   System.out.println("---------->Size:"+String.valueOf(items.size()));
				   
				 }				 
//				 if(!items.isEmpty())
//				 {
//					 isMatch=true;
//				 }
				
				if(items.isEmpty())
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
						double lat = locData.latitude;
						double lon = locData.longitude;
						GeoPoint locGeoPoint = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
						distance=(int)GetShortDistance(items.get(0).getPoint(), locGeoPoint);
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
//				else {
//					 mMapController.setZoom(15);
//				}
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
//					System.out.println("----------->list1Size:"+list_dropdown.getCount());
					String place = list_dropdown.getItemAtPosition(arg2).toString();
//					System.out.println("---------->1"+place);
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
//						System.out.println("------------->Size:"+items.size());
						int distance;
						if(locationFinish)
						{
							double lat = locData.latitude;
							double lon = locData.longitude;
							GeoPoint locGeoPoint = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
							distance=(int)GetShortDistance(items.get(0).getPoint(), locGeoPoint );
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
						if(pop != null)
						{
							pop.hidePop();
						}
						
						if(routeOverlay != null)
						{
							mMapView.getOverlays().remove(routeOverlay);
							mMapView.refresh();
						}
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
//		float bottom;
		float right;
		ViewUtil mViewUtil = new ViewUtil(classifyButton);
		top = mViewUtil.getTop();
//		bottom = mViewUtil.getBottom();
		right = mViewUtil.getRight();
//		System.out.println("---------->top= "+top+"  bottom="+bottom+"   height="+(bottom-top));
//		int screenHeight;
//		screenHeight = metrics.heightPixels;
//		System.out.println("----------->X="+touchX+"  Y="+touchY+"  screenHeight="+screenHeight);
		
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
//	        option.disableCache(false);//是否启用缓存定位
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
    	if (routeOverlay != null) {
			mMapView.getOverlays().remove(routeOverlay);
		}
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
	
	
	public class MyRouteOverlay extends RouteOverlay
	{
		public MyRouteOverlay(Activity activity,MapView mMapView)
		{
			super(activity, mMapView);
		}
		
		@Override
		protected boolean onTap(int index)
		{
			Toast.makeText(HusterMain.this, String.valueOf(index), Toast.LENGTH_SHORT).show();
			return true;
		}
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
    		mCurItem = item;
    		
    		System.out.println("Myoverlay.item = "+item.getPoint());
    		//Toast.makeText(HusterMain.this, "item: "+String.valueOf(index)+" "+"has been touched", Toast.LENGTH_SHORT).show();
//    		System.out.println("------------->ClickItem:"+String.valueOf(index)+"lat="+String.valueOf(item.getPoint().getLatitudeE6())+"  "+"lon="+
//    		String.valueOf(item.getPoint().getLongitudeE6()));
//    		mySearcher=new DatabaseSearcher(HusterMain.this);
//    		List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
//    		databaseHusts=mySearcher.search(item.getPoint());
//    		System.out.println("------------------>dataSize:"+databaseHusts.size());
//    		boolean hasDetail=false;
//    		if(!databaseHusts.isEmpty()){
//                
//    			for (int i = 0; i < databaseHusts.size(); i++) {
//					if(!databaseHusts.get(i).buildingName.equals(databaseHusts.get(i).officeNanme) )
//					{
//						hasDetail=true;
//						break;
//					}
//				}
//    			
//    			if(!hasDetail||(databaseHusts.size()==1&&databaseHusts.get(0).officePhone.isEmpty()))
//    			{
//    				Toast.makeText(HusterMain.this, databaseHusts.get(0).buildingName, Toast.LENGTH_SHORT).show();
//    				mMapController.animateTo(item.getPoint());
//    				return true;
//    			}
//    			else {
//    				Intent intent=new Intent(HusterMain.this,ShowDetail_Activity.class);
//        			intent.putExtra("x", item.getPoint().getLongitudeE6());
//        			intent.putExtra("y", item.getPoint().getLatitudeE6());
//        			startActivity(intent);
//				}
//    			
//    			
//    		return true;
//    		}
//    		
//    		else{
//    			Toast.makeText(HusterMain.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
//    			mMapController.animateTo(item.getPoint());
//    			return true;
//    		}
    		System.out.println("-------------------->111");
//    		 popupText.setText(getItem(index).getTitle());
			   Bitmap[] bitMaps={
				    BMapUtil.getBitmapFromView(popupLeft), 		
//				    BMapUtil.getBitmapFromView(popupInfo), 		
				    BMapUtil.getBitmapFromView(popupRight) 		
			    };
			    pop.showPopup(bitMaps,item.getPoint(),48);
			    System.out.println("------------------>222");
    		return true;
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
	                fenleiString = pString;
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
                fenleiString = pString;
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
                fenleiString = pString;
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
                fenleiString = pString;
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

			             List<DatabaseHust> databaseHusts;
			             databaseHusts = mySearcher.sortData(fenleiString);
			             GeoPoint flagPoint = null;
			             for (int i = 0; i < databaseHusts.size(); i++) {
			            	 if (databaseHusts.get(i).officeNanme.equals(officename)) {
			            		 flagPoint = databaseHusts.get(i).geoPoint;
			            		 clickPoint = databaseHusts.get(i).geoPoint;
								 OverlayItem item1 = new OverlayItem(databaseHusts.get(i).geoPoint, databaseHusts.get(i).officeNanme, "");
								 mItems.add(item1);
								 System.out.println("the officename =  "+databaseHusts.get(i).officeNanme);
								 break;
							}
							
						}
			            
			             for (int i = 0; i < databaseHusts.size(); i++) {
							if ( ! databaseHusts.get(i).geoPoint.equals(flagPoint)) {
								
								OverlayItem item2 = new OverlayItem(databaseHusts.get(i).geoPoint, databaseHusts.get(i).officeNanme, "");
								 item2.setMarker(getResources().getDrawable(R.drawable.icon_gcoding));
								 mItems.add(item2);
							}
						}
			             databaseHusts = null;
			             Message msg=new Message();
	        			 msg.what=0;
	        			 handler.sendMessage(msg);
		                 mMapController.animateTo(clickPoint);
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
//	 	            System.out.println(" _------------> locData.lat = "+locData.latitude +" \n locData.lon = "+locData.longitude );
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
		
	    ArrayList<OverlayItem>  overlayItems=new ArrayList<OverlayItem>();
		
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
		     mMapView.refresh();
		
		     clearLable();//根据缩放级别清除标注
		
		     mCurItem = items.get(0);
		     Bitmap[] bitMaps={
				      BMapUtil.getBitmapFromView(popupLeft), 		
//				      BMapUtil.getBitmapFromView(popupInfo), 		
				      BMapUtil.getBitmapFromView(popupRight) 		
			    };
			    pop.showPopup(bitMaps,items.get(0).getPoint(),48);
		     mMapController.animateTo(items.get(0).getPoint());
		}
		
		else			
			return;		
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
	 * 将检索的坐标点转换为搜索路径所需的节点
	 * */
	public Node PointToNode(GeoPoint point,ArrayList<Node> nodes , GeoPoint endPoint)
	{
		double tempDistance = Double.MAX_VALUE;
		double finalDistance = Double.MAX_VALUE;
		Node tempNode = new Node(point);
		Node finalNode = new Node(point);
		for (int i = 0; i < nodes.size(); i++) {
			if (finalDistance > GetShortDistance(point, nodes.get(i).getPointID())) {
				tempDistance = finalDistance;
				finalDistance = GetShortDistance(point, nodes.get(i).getPointID());
				tempNode = finalNode;
				finalNode = nodes.get(i);
			}
		}
		double tempNodeDistance = GetShortDistance(point, tempNode.getPointID()) + GetShortDistance(tempNode.getPointID(), endPoint);
		double finalNodeDistance = GetShortDistance(point, finalNode.getPointID()) + GetShortDistance(finalNode.getPointID(), endPoint);
		if ( tempNodeDistance > finalNodeDistance) {
			return finalNode;
		}
		else {
			if ((tempDistance - finalDistance ) > 19) {
				return finalNode;
			}
			else {
				return tempNode;
			}
		}
	}
	
	
	public Node findnearestNode(GeoPoint point,ArrayList<Node> nodes)
	{
		double tempDistance = Double.MAX_VALUE;
		Node node = new Node(point);
		for (int i = 0; i < nodes.size(); i++) {
			if (tempDistance > GetShortDistance(point, nodes.get(i).getPointID())) {
				tempDistance = GetShortDistance(point, nodes.get(i).getPointID());
				node = nodes.get(i);
			}
		}
		
		return node;
		
	}
	
	/*
	 * 找中转点
	 * */
	public Node getTransitionNode(Node startNode, Node endNode)
	{
		int startLat = startNode.getPointID().getLatitudeE6();
		int startLon = startNode.getPointID().getLongitudeE6();
		int endLat = endNode.getPointID().getLatitudeE6();
		int endLon = endNode.getPointID().getLongitudeE6();
		//起点在A区域
		if (startLon > Awedge && startLon < Aeedge && startLat > Asedge && startLat < Anedge) {
			if (endLon > Bwedge && endLon < Beedge && endLat > Bsedge && endLat < Bnedge) {
				GeoPoint tempPoint = new GeoPoint(Asedge, startLon);
				return PointToNode(tempPoint, slableNodes, endNode.getPointID());
//				return findnearestNode(tempPoint, nodeList);
			}
			else if (endLat > Bnedge && endLon >= EWBorder1) {
				return PointToNode(startNode.getPointID(), lableNodes, endNode.getPointID());
//				return findnearestNode(startNode.getPointID(), lableNodes);
			}
			
			else if (endLon < 114410466) {//紫淞公寓广场经度
				return null;
			}
			
			else {
				GeoPoint tempPoint = getTransitionPoint(startLat, startLon, endLat, endLon);
				if (tempPoint != null) {
					return PointToNode(tempPoint, nodeList, startNode.getPointID());
				}
				else {
					return null;
				}
//				return findnearestNode(temPoint, nodeList);
			}
		}
		
		else if (startLon > Bwedge && startLon < Beedge && startLat > Bsedge && startLat < Bnedge) {
			if (endLon > Awedge && endLon < Aeedge && endLat > Asedge && endLat < Anedge ) {
				GeoPoint tempPoint = new GeoPoint(Asedge, endLon);
				return PointToNode(tempPoint, slableNodes, startNode.getPointID());
//				return findnearestNode(tempPoint, nodeList);
			}
			else if (endLon < EWBorder1) {
				return PointToNode(endNode.getPointID(), lableNodes, startNode.getPointID());
//				return findnearestNode(endNode.getPointID(), lableNodes);
			}
			else return null;
		}
		
		else if (startLat > Bnedge && startLon > EWBorder) {
			if (endLon < EWBorder1) {
				return PointToNode(endNode.getPointID(), lableNodes, startNode.getPointID());
//				return findnearestNode(endNode.getPointID(), lableNodes);
			}
			else {
				return null;
			}
		}
		
		else if (startLon < EWBorder && startLon > Bwedge && startLat < SNBorder && startLat > Bnedge) {
			if (endLon < EWBorder1 && endLat > Anedge) {
				return PointToNode(endNode.getPointID(), lableNodes, startNode.getPointID());
			}
			else {
				return null;
			}
		}
		else if (startLat > SNBorder && startLon > EWBorder1 && startLon < EWBorder) {
			if (endLon < EWBorder1 && endLat > (SNBorder + 500)) {
				GeoPoint tempPoint = getTransitionPoint(startLat, startLon, endLat, endLon);
				if (tempPoint != null) {
					return PointToNode(tempPoint, nodeList, startNode.getPointID());
				}
				else {
					return null;
				}
			}
			else if(endLon < EWBorder1 && endLat <= (SNBorder +500)){
				return PointToNode(endNode.getPointID(), lableNodes, startNode.getPointID());
			}
			else {
				return null;
			}
			
		}
		
		else if(startLon < EWBorder1 && endLon > EWBorder){
			return PointToNode(startNode.getPointID(), lableNodes, endNode.getPointID());
//			return findnearestNode(tempPoint, nodeList);
		}
		else {
			
			GeoPoint tempPoint = getTransitionPoint(startLat, startLon, endLat, endLon);
			if (tempPoint != null) {
				return PointToNode(tempPoint, nodeList, startNode.getPointID());
			}
			else {
				return null;
			}
		}
	}
	
	public GeoPoint getTransitionPoint(int startLat, int startLon ,int endLat , int endLon)
	{
		if (Math.abs(startLat-endLat) > sideLenght && Math.abs(startLon-endLon) > sideLenght) {
			int finalLat = (startLat < endLat) ? (startLat + optimizedLenght) : (startLat - optimizedLenght);
			int finalLon = (startLon < endLon) ? (endLon - optimizedLenght) : (endLon + optimizedLenght);
			return new GeoPoint(finalLat, finalLon);
		}
		else return null;
	}
	
	public GeoPoint getTheCenter(GeoPoint point1, GeoPoint point2)
	{
		return new GeoPoint((point1.getLatitudeE6()+point2.getLatitudeE6())/2, (point1.getLongitudeE6()+point2.getLongitudeE6())/2);
	}
	
	/*
	获取地图上两点之间距离的方法
	*/	
	public double GetShortDistance(GeoPoint sPoint, GeoPoint ePoint)
	{
		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		double distance;
		// 角度转换为弧度
		ew1 = sPoint.getLongitudeE6() *(1e-6)* DEF_PI180;
		ns1 = sPoint.getLatitudeE6() *(1e-6)* DEF_PI180;
		ew2 = ePoint.getLongitudeE6() *(1e-6)* DEF_PI180;
		ns2 = ePoint.getLatitudeE6() *(1e-6)* DEF_PI180;
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
