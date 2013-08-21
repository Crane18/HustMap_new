package com.example.aimhustermap;


import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.OverlayItem;
import com.example.aimhustermap.adapter.FenleiAdapter;
import com.example.aimhustermap.db.DatabaseHust;
import com.example.aimhustermap.db.DatabaseSearcher;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ClassifyListView extends Activity {
	
	private String name;
	private String officePhone;
	private String address;
	RelativeLayout hintLayout;
	List<DatabaseHust> databaseHusts;
	DatabaseSearcher mySearcher=new DatabaseSearcher(this);
	private String mess = "食堂";
	FenleiAdapter adapter=null;
	ListView list=null;
    ArrayList<OverlayItem>  items=new ArrayList<OverlayItem>();
    
    private Dialog dialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.classifylistview);
		
		TextView textView=(TextView)findViewById(R.id.show_placename);
		hintLayout=(RelativeLayout)findViewById(R.id.hint_layout);
		Button backButton=(Button)findViewById(R.id.back);
		list=(ListView)findViewById(R.id.listview);
		 Intent intent=getIntent();
		Bundle dataBundle=intent.getExtras();
		String pString=(String)dataBundle.getSerializable("pString");
		databaseHusts=mySearcher.sortData(pString);		
		if(pString.equals(mess))
		{			    
            	hintLayout.setVisibility(View.VISIBLE);			
		}
		
		adapter=new FenleiAdapter(ClassifyListView.this, databaseHusts);
		list.setAdapter(adapter);
		
		ImageButton clearButton=(ImageButton)findViewById(R.id.clear_btn);
		clearButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(hintLayout.isShown())
				{
					hintLayout.setVisibility(View.GONE);
				}
			}
		});
		
		textView.setText("分类"+"("+pString+")");
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent=getIntent();
	        		Bundle dataBundle=new Bundle();
	        		dataBundle.putString("key",null);
	        		intent.putExtras(dataBundle);
	        		ClassifyListView.this.setResult(1,intent);
	        		ClassifyListView.this.finish();
			}
		});
		 

		 list.setOnItemLongClickListener(new OnItemLongClickListener() {
	    	 @SuppressLint("NewApi")
			@Override
	            public boolean onItemLongClick(AdapterView<?> parent, View view,  
	                    int position, long id) {
           
	    		 TextView textView=(TextView)view.findViewById(R.id.phonenum_fenlei);
	    		 officePhone=(String)textView.getText();
	    		 TextView textView2=(TextView)view.findViewById(R.id.address_fenlei);
	    		 name=textView2.getText().toString();
	    		 if(!officePhone.isEmpty())
	    		 {
	    			 showPopup_ask(ClassifyListView.this,officePhone);
                    
	    		 }
	    		 return true;

                   }
		});
		
	    list.setOnItemClickListener(new OnItemClickListener() {
	    	@Override  
	        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                long arg3) {
	    			    		
	    		Intent intent=getIntent();
	    		Bundle dataBundle=new Bundle();
	    		dataBundle.putString("key", databaseHusts.get(arg2).officeNanme);
	    		intent.putExtras(dataBundle);
	    		ClassifyListView.this.setResult(0,intent);
	    		ClassifyListView.this.finish();
	    	    return; 
	    	}
		});
	   
	}
	  
	public  void copyText(Context context, String text) {
	    ClipboardManager cm = (ClipboardManager) context
	            .getSystemService(Context.CLIPBOARD_SERVICE);
	    cm.setText(text);
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
		     	ClassifyListView.this.startActivity(intent);
		     	
	}
	
	  private void showPopup_ask(Context context,String phoneNumber)
	    {        
	       
	        // 【Ⅰ】 获取自定义popupWindow布局文件
		  
		  dialog = new Dialog(context);
	     // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	      dialog.show();
	      dialog.setContentView(R.layout.popup_contacts);
	      dialog.setTitle(phoneNumber+" : ");
//	      WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
//	      layoutParams.alpha = 0.6f; 
//	      dialog.getWindow().setAttributes(layoutParams);
	      
	        final Button addContacts = (Button) dialog.findViewById(R.id.add_contacts);	
  	        final Button copyButton=(Button)dialog.findViewById(R.id.copy_phonenum);
  	        
	       dialog.setCanceledOnTouchOutside(true);
	        
	      
	     
	        addContacts.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
			           intent.setType(People.CONTENT_ITEM_TYPE);
			           intent.putExtra(Contacts.Intents.Insert.NAME, name);
			           intent.putExtra(Contacts.Intents.Insert.PHONE, officePhone);
			           intent.putExtra(Contacts.Intents.Insert.NOTES, address);
			           intent.putExtra(Contacts.Intents.Insert.PHONE_TYPE,Contacts.PhonesColumns.TYPE_MOBILE);
			          
			           startActivity(intent);
				
				}
			});
	        copyButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					copyText(ClassifyListView.this, officePhone);
					Toast.makeText(ClassifyListView.this, "亲，电话号码已复制哦！", Toast.LENGTH_SHORT).show();
				}
			});

	    }

	  @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
	    	 */
	      
	        super.onResume();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
	    	 */
	      
	        super.onDestroy();
	    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_huster_main, menu);
		return false;
	}
	

}
