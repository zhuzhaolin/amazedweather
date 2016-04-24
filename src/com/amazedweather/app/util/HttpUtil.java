package com.amazedweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendHttpRequest(final String address , 
			                             final HttpCallbackListener listener){
		
		//开启线程处理网络连接
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					//解析网址，打开连接
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					//GET表示向服务器获取数据
					connection.setRequestMethod("GET");
					//设置网络连接超时，读超时
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					//获取服务器返回的输入流
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line =reader.readLine()) != null){
						response.append(line);
					}
					if (listener != null) {
						//回调onFinish方法
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						//回调onError()方法
						listener.onError(e);
					}
				}finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
				
			}
		}).start();
		
	}
	
}
