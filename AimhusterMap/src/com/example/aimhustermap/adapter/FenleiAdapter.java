package com.example.aimhustermap.adapter;


import java.util.List;  

import com.example.aimhustermap.R;
import com.example.aimhustermap.R.color;
import com.example.aimhustermap.db.DatabaseHust;

import android.R.integer;
import android.content.Context;  
import android.graphics.Color;
import android.util.Log;  
import android.view.Gravity;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.Button; 
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;  
import android.widget.TextView;  
 
  
public class FenleiAdapter extends BaseAdapter {  
    private List<DatabaseHust> databaseHusts;  
    Context context;
    int flag;
      
    public FenleiAdapter(Context context,List<DatabaseHust> databaseHusts){  
        this.databaseHusts = databaseHusts; 
        this.context = context; 
       
    }  
  
    @Override  
    public int getCount() { 
    
    		return (databaseHusts==null)?0:databaseHusts.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return databaseHusts.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
      
      
    public class ViewHolder{  
        TextView addressTV;  
        TextView phonenumTV;  
        ImageButton docallBtn;
        ImageView image1;
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        final DatabaseHust databaseHust = (DatabaseHust)getItem(position);  
        ViewHolder viewHolder = null; 
        convertView = null;
        if(convertView==null){  
           // Log.d("MyBaseAdapter", "新建convertView,position="+position);  
            convertView = LayoutInflater.from(context).inflate(  
                    R.layout.fenlei_listview, null); 
              
            viewHolder = new ViewHolder();  
            viewHolder.addressTV = (TextView)convertView.findViewById(  
                    R.id.address_fenlei);  
            viewHolder.phonenumTV = (TextView)convertView.findViewById(  
                    R.id.phonenum_fenlei);  
            viewHolder.docallBtn = (ImageButton)convertView.findViewById(  
                    R.id.docall_fenlei); 
            viewHolder.image1 = (ImageView)convertView.findViewById(R.id.imageview1);
            
            System.out.println("----------->databaseHust.sort=="+String.valueOf(databaseHust.sort));
            System.out.println("--------------->officePhone=="+databaseHust.officePhone);
             if(databaseHust.sort.equals("食堂办卡"))
             {
            	 System.out.println("------------>meiyoujinlaimaaaaaa");
            	 viewHolder.image1.setBackgroundResource(R.drawable.card1);
             }
             else if(databaseHust.sort.equals("食堂充值"))
             {
            	 viewHolder.image1.setBackgroundResource(R.drawable.card2);
             }
             if(!databaseHust.officePhone.isEmpty())
             {
            	 viewHolder.phonenumTV.setText(databaseHust.officePhone);  
                 viewHolder.docallBtn.setBackgroundResource(R.drawable.call_btn_selector);
             }
             else {
				viewHolder.docallBtn.setBackgroundResource(R.drawable.docall_blank);
				viewHolder.docallBtn.setEnabled(false);
			}
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(  
                    LinearLayout.LayoutParams.WRAP_CONTENT,  
                    LinearLayout.LayoutParams.WRAP_CONTENT);  
            mParams.gravity = Gravity.CENTER;  
            mParams.width=50;  
              
            convertView.setTag(viewHolder);  
        }
          
        viewHolder.addressTV.setText(databaseHust.officeNanme);  
        
          
        return convertView;  
    }  
  
}  
