package com.example.aimhustermap.adapter;

import java.util.List;  

import com.example.aimhustermap.R;
import com.example.aimhustermap.db.DatabaseHust;

import android.R.string;
import android.content.Context;  
import android.util.Log;  
import android.view.Gravity;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;   
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;  
import android.widget.TextView;  
 
  
public class NavigateAdapter extends BaseAdapter {  
    private String[]  string;  
    Context context;
      
    public NavigateAdapter(Context context,String[] string){  
        this.string = string;  
        this.context = context; 

    }  
  
    @Override  
    public int getCount() {  
        return (string==null)?0:string.length;  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return string[position];  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
      
      
    public class ViewHolder{  
        TextView textView;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        final String nameString=(String)getItem(position); 
        ViewHolder viewHolder = null;  
        if(convertView==null){  
           // Log.d("MyBaseAdapter", "新建convertView,position="+position);  
            convertView = LayoutInflater.from(context).inflate(  
                    R.layout.navigate, null); 
              
            viewHolder = new ViewHolder();  
            viewHolder.textView = (TextView)convertView.findViewById(  
                    R.id.textview_navigate);  
         
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
          
        viewHolder.textView.setText(nameString);  
        
          
        return convertView;  
    }  
  
}  
