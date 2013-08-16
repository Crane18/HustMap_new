package com.example.aimhustermap;

import com.example.aimhustermap.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity{
	
	
	Button checkUpdate;
	Button back ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		 ManagerApp app = (ManagerApp)this.getApplication();
	        app.addActivity(this);
		setContentView(R.layout.activity_about);
		
		checkUpdate = (Button)findViewById(R.id.check_update);
		back = (Button)findViewById(R.id.back_about);
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AboutActivity.this.finish();
				
			}
		});
		
		checkUpdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("zheli bu ke neng jin lai ba ");
				UmengUpdateAgent.update(AboutActivity.this);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			        @Override
			        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
			            switch (updateStatus) {
			            case 0: // has update
			                UmengUpdateAgent.showUpdateDialog(AboutActivity.this, updateInfo);
			                
			                break;
			            case 1: // has no update
			                Toast.makeText(AboutActivity.this, "没有更新", Toast.LENGTH_SHORT)
			                        .show();
			                System.out.println("meiyou");
			                break;
			            case 2: // none wifi
			                Toast.makeText(AboutActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
			                        .show();
			                System.out.println("meiyou wifi");
			                break;
			            case 3: // time out
			                Toast.makeText(AboutActivity.this, "超时", Toast.LENGTH_SHORT)
			                        .show();
			                System.out.println("time out ");
			                break;
			            }
			        }

			});
				
			}
		});
		
		
	}
	

	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPause(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}

}
