package com.example.aimhustermap;


import com.umeng.fb.FeedbackAgent;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

public class FeedbackActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 ManagerApp app = (ManagerApp)this.getApplication();
	        app.addActivity(this);	
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.startFeedbackActivity();
		

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
