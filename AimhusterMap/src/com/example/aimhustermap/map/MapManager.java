package com.example.aimhustermap.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Loader.ForceLoadContentObserver;
import android.os.Environment;

public class MapManager {
	
	private String MAPNAMEL = "Wu_Han_Shi_218l.dat_svc";
	private String MAPNAMEH = "Wu_Han_Shi_218h.dat_svc";
	
	private String FODERL = "/l";
	private String FODERH = "/h";
	
	private Activity activity ;
	
	public MapManager(Context context){
		activity = (Activity)context;
	}
	
	public void includeMap(){
		includeMap(MAPNAMEH, FODERH);
		includeMap(MAPNAMEL,FODERL);
	}
	
    private void includeMap(String mapName ,String foder){
		  boolean b = false;
		  
		  String path = Environment.getExternalStorageDirectory().toString();
		  String mapPath = path+"/BaiduMapSdk/vmp" + foder;
//		  String mapName = "Wu_Han_Shi_21.dat_svc";
			// 检查 SQLite 数据库文件是否存在
					if ((new File(mapPath+"/"+mapName)).exists() == false) {
						// 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
						
						// 如 database 目录不存在，新建该目录
						if (!(new File(path+"/BaiduMapSdk")).exists()) {
							b = (new File(path)).mkdir();
						}
						if (!(new File(path+"BaiduMapSdk/vmp")).exists()) {
							b = (new File(path+"BaiduMapSdk/vmp")).mkdir();
						}
						if (!(new File(mapPath).exists())) {
							b = (new File(mapPath)).mkdir();
						}

						
						try {
							// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
							
							InputStream is = activity.getBaseContext().getAssets().open(mapName);
							// 输出流
							OutputStream os = new FileOutputStream(mapPath + "/" + mapName);

							// 文件写入
							byte[] buffer = new byte[1024];
							int length;
							while ((length = is.read(buffer)) > 0) {
								os.write(buffer, 0, length);
							}

							// 关闭文件流
							os.flush();
							os.close();
							is.close();
							b = true;
						} catch (Exception e) {
							e.printStackTrace();
							b = false;
						}
					}
					
	  }

}
