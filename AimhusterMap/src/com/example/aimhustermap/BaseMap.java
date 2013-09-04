package com.example.aimhustermap;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.i;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.R;
import com.example.aimhustermap.HusterMain.MyOverlay;
import com.example.aimhustermap.db.DatabaseHust;
import com.example.aimhustermap.db.DatabaseSearcher;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class BaseMap extends Activity{
	
	DatabaseSearcher mySearcher;
	
	//private MyOverlay lableOverlay=null;
	boolean hasAdd=false;//是否添加了标注
	public TextOverlay mTextOverlay=null;
	
	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	/**
	 *  MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	
	 public ArrayList<OverlayItem> items1 = new ArrayList<OverlayItem>();
	
    private Handler handler  =new Handler()
    {
 	   @Override
 	   public void handleMessage(Message msg)
 	   {
 		  
 		   if (msg.what==2) {
 			   Toast.makeText(BaseMap.this, "定位不成功或不在武汉市内", Toast.LENGTH_SHORT).show();					
		}
 	   }
    };
    private Timer timer =new Timer();
	 
    ArrayList<OverlayItem>  items = new ArrayList<OverlayItem>();
	// 定位相关
		LocationClient mLocClient;
		LocationData locData = null;
		public MyLocationListenner myListener = new MyLocationListenner();
	    private boolean isLocked=true;
		//定位图层
		MyLocationOverlay myLocationOverlay = null;
		private MyOverlay  mOverlay = null;
		boolean isRequest = false;//是否手动触发请求定位
		boolean isLocationClientStop = false;
		boolean isFirstToast = true;
		ImageButton requestLocButton=null;
		
		String[] navigate_place ;
		int[] lat;
		int[] lon;
		
		int temp;
		
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        ManagerApp app = (ManagerApp)this.getApplication();
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
	      
	        
	        
	        setContentView(R.layout.basemap);
	        
	        mMapView=(MapView)findViewById(R.id.bmapView_1);
	        /**
	         * 获取地图控制器
	         */
	        mMapController = mMapView.getController();
	        /**
	         *  设置地图是否响应点击事件  .
	         */
	        mMapController.enableClick(true);
	        /**
	         * 设置地图缩放级别
	         */
	        mMapController.setZoom(15);
	        
	        mTextOverlay=new TextOverlay(mMapView);
	       // lableOverlay = new MyOverlay(getResources().getDrawable(R.drawable.lable_dot), mMapView);
	        
	        mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);
	        
	        navigate_place = getResources().getStringArray(R.array.navigate_places);
	        lat = getResources().getIntArray(R.array.lat_navigate);
	        lon = getResources().getIntArray(R.array.lon_navigate);
	        
	        GeoPoint point1 = new GeoPoint(lat[10]-60, lon[10]-25);
			//OverlayItem item1 = new OverlayItem(point1, "", navigate_place[10]);
			mTextOverlay.addText(DrawText(point1,navigate_place[10]));
			//lableOverlay.addItem(item1); 
	        
	        for(int i = 0 ;i<navigate_place.length;i++)
	        {
	        	GeoPoint point = new GeoPoint(lat[i], lon[i]);
	        	OverlayItem item = new OverlayItem(point, "", navigate_place[i]);
	        	items.add(item);
	        }
	        
	        
	        Button backbtnButton = (Button)findViewById(R.id.back_btn);
	        requestLocButton=(ImageButton)findViewById(R.id.loc_btn);
	        requestLocButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
						requestLocClick();
					if(isLocked)
					{
						Toast.makeText(BaseMap.this, "不在武汉市或定位不成功，开启GPS或网络！", Toast.LENGTH_SHORT).show();
					}
					else {
						
						mMapController.setZoom((float) 14.6);
						clearLable();
					}
					
				}
			});
	        backbtnButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					BaseMap.this.finish();
				}
			});
	        
	        GeoPoint p ;
	        double cLat =  30.51344 ;
	        double cLon = 114.419896 ;
	        Intent  intent = getIntent();
	        
	        if ( intent.hasExtra("key") ){
	        	//当用intent参数时，设置中心点为指定点
	        	Bundle b = intent.getExtras();
	        	 temp = (int)b.getInt("key");
	        	
	        	switch (temp) {
				case 0:
					items1.add(items.get(0));
					items1.add(items.get(1));
					items1.get(0).setMarker(getResources().getDrawable(R.drawable.icon_marka));
					items1.get(1).setMarker(getResources().getDrawable(R.drawable.icon_markb));
					break;
					
				case 1:
					items1.add(items.get(4));
					items1.add(items.get(3));
					items1.add(items.get(2));
					items1.get(0).setMarker(getResources().getDrawable(R.drawable.icon_marka));
					items1.get(1).setMarker(getResources().getDrawable(R.drawable.icon_markb));
					items1.get(2).setMarker(getResources().getDrawable(R.drawable.icon_markc));
					break;
				
				case 2:
					items1.add(items.get(5));
					items1.add(items.get(6));
					items1.get(0).setMarker(getResources().getDrawable(R.drawable.icon_marka));
					items1.get(1).setMarker(getResources().getDrawable(R.drawable.icon_markb));
					break;
					
				case 3:
					items1.add(items.get(9));
					items1.add(items.get(8));
					items1.add(items.get(7));
					items1.get(0).setMarker(getResources().getDrawable(R.drawable.icon_marka));
					items1.get(1).setMarker(getResources().getDrawable(R.drawable.icon_markb));
					items1.get(2).setMarker(getResources().getDrawable(R.drawable.icon_markc));
					break;	
 
				case 4:
					items1.add(items.get(10));
					items1.add(items.get(11));
					items1.add(items.get(12));
					items1.add(items.get(13));
					items1.get(0).setMarker(getResources().getDrawable(R.drawable.icon_marka));
					items1.get(1).setMarker(getResources().getDrawable(R.drawable.icon_markb));
					items1.get(2).setMarker(getResources().getDrawable(R.drawable.icon_markc));
					items1.get(3).setMarker(getResources().getDrawable(R.drawable.icon_markd));
					break;
					
				default:
					break;
				}
	        	
	        }
	        else {
	        	//设置中心点为华科
	        	 p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
	        	 mMapController.setCenter(p);
			}
	        	
	        
	       
	        autoLocation();
	       // mMapController.setCenter(items1.get(0).getPoint());
	        initOverlay(items1);
	        
	        
	      
	        /**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
	        mMapListener = new MKMapViewListener() {
				@Override
				public void onMapMoveFinish() {
					/**
					 * 在此处理地图移动完成回调
					 * 缩放，平移等操作完成后，此回调被触发
					 */
					
					 double zoomLever=mMapView.getZoomLevel();
				     if(!hasAdd&&zoomLever>18)
				     {
				        mMapView.getOverlays().add(mTextOverlay);
				       // mMapView.getOverlays().add(lableOverlay);
				        mMapView.refresh();
				        hasAdd=true;
				     }
				     if(hasAdd&&zoomLever<=18)
				     {
				    	 mMapView.getOverlays().remove(mTextOverlay);
				    	// mMapView.getOverlays().remove(lableOverlay);
				    	 mMapView.refresh();
				    	 hasAdd=false;
				     }
				}
				
				@Override
				public void onClickMapPoi(MapPoi mapPoiInfo) {
					/**
					 * 在此处理底图poi点击事件
					 * 显示底图poi名称并移动至该点
					 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
					 * 
					 */
					String title = "";
					if (mapPoiInfo != null){
						title = mapPoiInfo.strText;
						Toast.makeText(BaseMap.this,title,Toast.LENGTH_SHORT).show();
						mMapController.animateTo(mapPoiInfo.geoPt);
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
				}

				@Override
				public void onMapLoadFinish() {
					// TODO Auto-generated method stub
					
				}
			};
			mMapView.regMapViewListener(ManagerApp.getInstance().mBMapManager, mMapListener);
	        
	 }
	 
	 
	 
	 public void clearLable()
		{
			 double zoomLever=mMapView.getZoomLevel();
			
			 if(hasAdd&&zoomLever<=18.9)
		     {
		    	 mMapView.getOverlays().remove(mTextOverlay);
		    	// mMapView.getOverlays().remove(lableOverlay);
		    	 mMapView.refresh();
		    	 hasAdd=false;
		     }
		}
	 /*
		 * 
		 * 初始化时自动定位
		 * */
		public void autoLocation()
		{
			    mLocClient = new LocationClient( this );
		        locData=new LocationData();
		        mLocClient.registerLocationListener( myListener );
		        LocationClientOption option = new LocationClientOption();
		        option.setOpenGps(true);//打开gps
		        option.setCoorType("bd09ll"); //设置坐标类型
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
	     * 手动触发一次定位请求
	     */
	    public void requestLocClick(){
	    	isRequest = true;
	        mLocClient.requestLocation();
	        Toast.makeText(BaseMap.this, "正在定位…", Toast.LENGTH_SHORT).show();
	     
	    }
	 
	 /**
	     * 定位SDK监听函数
	     */
   public class MyLocationListenner implements BDLocationListener {
	    	
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            if (location == null || isLocationClientStop)
	                return ;
	            //int  locType;
	            double left_edge=113.997122;
	            double right_edge=114.609982;
	            double top_edge=30.683752;
	            double bottom_edge=30.439953;
//	            double left_edge=114.313325;
//	            double right_edge=114.330573;
//	            double top_edge=30.553471;
//	            double bottom_edge=30.503201;
	           
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
	 	           if (isRequest){
	 	            	//移动地图到定位点
	 	                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	 	                isRequest = false;
	 	            }
	            }
	            else {
	            	isLocked=true;
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
//	            locType=location.getLocType();
//	            System.out.println("------------->locType=="+String.valueOf(locType));
	           
	        }
	        
	        public void onReceivePoi(BDLocation poiLocation) {
	            if (poiLocation == null){
	                return ;
	            }
	        }
	    }
	 
	    /* 
	    自定义覆盖物图层
	    */
		
	    public class MyOverlay extends ItemizedOverlay{

	    	public MyOverlay(Drawable defaultMarker, MapView mapView) {
	    		super(defaultMarker, mapView);
	    	}
	    	

	    	@SuppressLint("NewApi")
			@Override
	    	public boolean onTap(int index){
	    		OverlayItem item = getItem(index);
	    		//Toast.makeText(HusterMain.this, "item: "+String.valueOf(index)+" "+"has been touched", Toast.LENGTH_SHORT).show();
	    		
	    		mySearcher=new DatabaseSearcher(BaseMap.this);
	    		List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
	    		databaseHusts=mySearcher.search(item.getPoint());
	    		boolean hasDetail = false;
	    		System.out.println("------------------>dataSize:"+databaseHusts.size());
	    		if(!databaseHusts.isEmpty()){
                    
	    			for (int i = 0; i < databaseHusts.size(); i++) {
						if(!databaseHusts.get(i).buildingName.equals(databaseHusts.get(i).officeNanme))
						{
							hasDetail=true;
							break;
						}
					}
	    			
	    			if(!hasDetail||(databaseHusts.size()==1&&databaseHusts.get(0).officePhone.isEmpty()))
	    			{
	    				Toast.makeText(BaseMap.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
	    				mMapController.animateTo(item.getPoint());
	    			}
	    			else {
	    				Intent intent=new Intent(BaseMap.this,ShowDetail_Activity.class);
		    			intent.putExtra("x", item.getPoint().getLongitudeE6());
		    			intent.putExtra("y", item.getPoint().getLatitudeE6());
		    			startActivity(intent);
					}
	    			
	    			
	    		return true;
	    		}
	    		else{
	    			Toast.makeText(BaseMap.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
	    			mMapController.animateTo(item.getPoint());
	    			return true;
	    		}
	    		
	    	}
	    	
	    	@Override
	    	public boolean onTap(GeoPoint pt , MapView mMapView){
//	    		if (pop != null){
//	                pop.hidePop();
//	                mMapView.refresh();
//	    		}
	    		return false;
	    	}
	    	
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
	    
	    
	    public TextItem DrawText(GeoPoint p,String poiName)
		{
			TextItem item=new TextItem();
			item.pt=p;
			item.text=poiName;
			//设文字大小
	    	item.fontSize = 28;
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
	  
	    
	    public void initOverlay(ArrayList<OverlayItem> mItems)
	    {
	    	clearOverlay();
	    	mOverlay.addItem(mItems);
	    	mMapView.getOverlays().add(mOverlay);
	    	mMapView.refresh();
	    	clearLable();
	    	int center_lat = 0;
	    	int center_lon = 0;
	    	for(int i = 0;i<mItems.size();i++)
	    	{
	    		center_lat += mItems.get(i).getPoint().getLatitudeE6();
	    		center_lon += mItems.get(i).getPoint().getLongitudeE6();
	    	}
	    	center_lat = center_lat/mItems.size();
	    	center_lon = center_lon/mItems.size();
	    	mMapController.setCenter(new GeoPoint(center_lat, center_lon));
	    	mMapController.setZoom((float) 14.6);
	    }
	    
	  @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		 
		  isLocationClientStop=true;
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
	    	isLocationClientStop=true;
	        mMapView.destroy();
	        super.onDestroy();
	    }
	    
	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    	mMapView.onSaveInstanceState(outState);
	    	
	    }
	    
	    @Override
	    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    	super.onRestoreInstanceState(savedInstanceState);
	    	mMapView.onRestoreInstanceState(savedInstanceState);
	    }

}
