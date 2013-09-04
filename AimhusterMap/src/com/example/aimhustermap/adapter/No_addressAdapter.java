package com.example.aimhustermap.adapter;

import java.util.List;  

import com.example.aimhustermap.R;
import com.example.aimhustermap.db.DatabaseHust;

import android.content.Context;  
import android.util.Log;  
import android.view.Gravity;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.Button; 
import android.widget.ImageButton;
import android.widget.LinearLayout;  
import android.widget.TextView;  
 
  
public class No_addressAdapter extends BaseAdapter {  
    private List<DatabaseHust> databaseHusts;  
    Context context;
      
    public No_addressAdapter(Context context,List<DatabaseHust> databaseHusts){  
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
        TextView staticView;  
        TextView phonenumTV;  
        ImageButton docallBtn;
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        final DatabaseHust databaseHust = (DatabaseHust)getItem(position);  
        ViewHolder viewHolder = null;  
        if(convertView==null){  
           // Log.d("MyBaseAdapter", "新建convertView,position="+position);  
            convertView = LayoutInflater.from(context).inflate(  
                    R.layout.noaddress_listview, null); 
              
            viewHolder = new ViewHolder();  
            viewHolder.staticView = (TextView)convertView.findViewById(  
                    R.id.staticview);  
            viewHolder.phonenumTV = (TextView)convertView.findViewById(  
                    R.id.phonenumber);  
            viewHolder.docallBtn = (ImageButton)convertView.findViewById(  
                    R.id.docall_noaddress); 
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(  
                    LinearLayout.LayoutParams.WRAP_CONTENT,  
                    LinearLayout.LayoutParams.WRAP_CONTENT);  
            mParams.gravity = Gravity.CENTER;  
            mParams.width=50;  
              
            convertView.setTag(viewHolder);  
        }else{  
            viewHolder = (ViewHolder)convertView.getTag();  
            Log.d("MyBaseAdapter", "旧的convertView,position="+position);  
        }  
          
        viewHolder.phonenumTV.setText(databaseHust.officePhone);  
        viewHolder.docallBtn.setBackgroundResource(R.drawable.call_btn_selector);
          
        return convertView;  
    }  
  
}  

