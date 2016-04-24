package com.amazedweather.app.util;

import java.io.Closeable;

public class FileUtil {

	/**
	 * 工具类关闭流
	 * 可变参数：... 只能形参最后一个位置,处理方式与数组一致
	 * 
	 */
	public static void close(Closeable ...io){
		for (Closeable temp : io) {
			try{
				if(temp != null){
					temp.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	//使用泛型
   public static <T extends Closeable> void CloseAll(T ...io){
		for (Closeable temp : io) {
			try{
				if(temp != null){
					temp.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}  
   }
}
