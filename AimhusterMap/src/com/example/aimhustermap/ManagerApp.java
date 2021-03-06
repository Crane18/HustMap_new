package com.example.aimhustermap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;


public class ManagerApp extends Application {
	
    private static ManagerApp mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

    public static final String strKey = "FB17e8f2c0b9e5cee82ce92507b0c507";
    
    private List<Activity> mainActivity = new ArrayList<Activity>();  
    
	
	@Override
    public void onCreate() {
		
		
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
	
	public List<Activity> MainActivity() {  
        return mainActivity;  
    }  
    public void addActivity(Activity act) {  
        mainActivity.add(act);  
    }  
    public void finishAll() {  
        for (Activity act : mainActivity) {  
            if (!act.isFinishing()) {  
                act.finish();  
            }  
        }  
        mainActivity = null;  
    }  
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(ManagerApp.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static ManagerApp getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(ManagerApp.getInstance().getApplicationContext(), "打开网络或开启GPS，定位更精确！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(ManagerApp.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(ManagerApp.getInstance().getApplicationContext(), 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                ManagerApp.getInstance().m_bKeyRight = false;
            }
        }
    }
}
