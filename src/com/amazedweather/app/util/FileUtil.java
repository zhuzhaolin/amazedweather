package com.amazedweather.app.util;

import java.io.Closeable;

public class FileUtil {

	/**
	 * ������ر���
	 * �ɱ������... ֻ���β����һ��λ��,����ʽ������һ��
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
	
	//ʹ�÷���
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
