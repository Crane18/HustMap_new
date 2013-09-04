package com.example.aimhustermap;


import java.util.ArrayList;
import java.util.List;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.R;
import com.example.aimhustermap.adapter.ShowDetailAdapter;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ShowDetail_Activity extends Activity{
	
	public String phonenum;
	public int _id;
    public int image_id;
    public String name;
    public String mobilePhone;
    public String officePhone;
    public String familyPhone;
    public String position;
    public String company;
    public String address;
    public String zipCode;
    public String email;
    public String otherContact;
    public String remark;
	
	ShowDetailAdapter adapter=null;
	ListView listView;
	List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
	List<DatabaseHust>  final_daDatabaseHusts;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showdetail_activity);
		final TextView  textView=(TextView)findViewById(R.id.building_name);
		Intent intent=getIntent();
		Bundle dataBundle=intent.getExtras();
		GeoPoint p=new GeoPoint(dataBundle.getInt("y"), dataBundle.getInt("x"));
		
		System.out.println("--------------->"+"lat :y="+dataBundle.getInt("y")+"lon x="+dataBundle.getInt("x"));
		DatabaseSearcher mySearcher=new DatabaseSearcher(this);
		final_daDatabaseHusts = new ArrayList<DatabaseHust>();
		databaseHusts=mySearcher.search(p);
		
		System.out.println("------------>databaseHusts.Size=="+String.valueOf(databaseHusts.size()));
		
//		for (int i = 0; i < databaseHusts.size(); i++) {
//			System.out.println("-------->databaseHust"+"("+String.valueOf(i)+"):"+"officename="+databaseHusts.get(i).officeNanme
//					+"  "+"officePhone="+databaseHusts.get(i).officePhone+"  "+"officeroom="+databaseHusts.get(i).officeRoom+
//					"lat="+databaseHusts.get(i).geoPoint.getLatitudeE6()+",lon="+databaseHusts.get(i).geoPoint.getLongitudeE6());
//		}
		for (int i = 0; i < databaseHusts.size(); i++) {
			if(!databaseHusts.get(i).buildingName.equals(databaseHusts.get(i).officeNanme))
			{
				final_daDatabaseHusts.add(databaseHusts.get(i));
			}
			
		}
		
		for (int i = 0; i < final_daDatabaseHusts.size(); i++) {
			System.out.println("-------->databaseHust"+"("+String.valueOf(i)+"):"+"officename="+final_daDatabaseHusts.get(i).officeNanme
					+"  "+"officePhone="+final_daDatabaseHusts.get(i).officePhone+"  "+"officeroom="+final_daDatabaseHusts.get(i).officeRoom+
					"  lat="+final_daDatabaseHusts.get(i).geoPoint.getLatitudeE6()+",lon="+final_daDatabaseHusts.get(i).geoPoint.getLongitudeE6());
		}
		
		textView.setText(final_daDatabaseHusts.get(0).buildingName);
		
		adapter=new ShowDetailAdapter(this, final_daDatabaseHusts);
		listView=(ListView)findViewById(R.id.detail_listview);
		listView.setAdapter(adapter);
		   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override  
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
		                long arg3) {  
		              
					/**
			    	 * 创建自定义overlay
			    	 * 
			    	 */
							

		        }  
			});
		   listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			   @SuppressLint("NewApi")
			@Override
	            public boolean onItemLongClick(AdapterView<?> parent, View view,  
	                    int position, long id) {
				   TextView office_textView=(TextView)view.findViewById(R.id.officename1);
					name=office_textView.getText().toString();
					TextView phone_textView=(TextView)view.findViewById(R.id.phonenum1);
					officePhone=phone_textView.getText().toString();
					address=textView.getText().toString();
					if(!officePhone.isEmpty())
					{
						showPopup_ask(ShowDetail_Activity.this,officePhone);
						
					}
					
					return true;
			   }
		});
		   
		   
		   Button backbtn=(Button)findViewById(R.id.backBtn1);
		   backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowDetail_Activity.this.finish();
			}
		});

		
	}
	
	/*ListView中的按钮点击事件，直接绑定到标签
	 * 
	 * */
	
	public void OnDetailItemBtnClick(View v)
	{
		RelativeLayout layout=(RelativeLayout)v.getParent();
		String phoneNum=((TextView)layout.findViewById(R.id.phonenum1)).getText().toString();
		
	
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"  
		              + phoneNum)); 
		     	ShowDetail_Activity.this.startActivity(intent);		     	
		
     	
	}
	
	 private void showPopup_ask(Context context,String phoneNumber)
	    {        
	       
	        // 【Ⅰ】 获取自定义popupWindow布局文件
		  
		   final Dialog  dialog = new Dialog(context);
	     
	      dialog.setContentView(R.layout.popup_contacts);
	      dialog.setTitle(phoneNumber+" : ");
	      
	      
	        final Button addContacts = (Button) dialog.findViewById(R.id.add_contacts);	
	        final Button copyButton=(Button)dialog.findViewById(R.id.copy_phonenum);
	        
	       dialog.setCanceledOnTouchOutside(true);
	        
	       dialog.show();
	     
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
					copyText(ShowDetail_Activity.this, officePhone);
					Toast.makeText(ShowDetail_Activity.this, "亲，电话号码已复制哦！", Toast.LENGTH_SHORT).show();
				}
			});

	    }
	
	  public  void copyText(Context context, String text) {
		    ClipboardManager cm = (ClipboardManager) context
		            .getSystemService(Context.CLIPBOARD_SERVICE);
		    cm.setText(text);
		}
	  

}
