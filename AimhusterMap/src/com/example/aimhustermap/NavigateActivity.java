package com.example.aimhustermap;

import com.example.aimhustermap.adapter.NavigateAdapter;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class NavigateActivity extends Activity{

	 public  ListView listView;
	 public  NavigateAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 ManagerApp app = (ManagerApp)this.getApplication();
	        app.addActivity(this);
	        setContentView(R.layout.activity_navigate);
	        Button back=(Button)findViewById(R.id.back_navigate);
	        listView = (ListView) findViewById(R.id.navigate_listview);
	        
	        String[] strings = getResources().getStringArray(R.array.navigate_array);
	        adapter = new NavigateAdapter(NavigateActivity.this, strings);
	        listView.setAdapter(adapter);
	        listView.setOnItemClickListener(new OnItemClickListener() {
	        	@Override  
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
		                long arg3) {
		    			    		Intent intent = new Intent(NavigateActivity.this,BaseMap.class);
		    			    		intent.putExtra("key", arg2);
		    			    		startActivity(intent);
		    		
		    	}
			});
	        back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					NavigateActivity.this.finish();
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
