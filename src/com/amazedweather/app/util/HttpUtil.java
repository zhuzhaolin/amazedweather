package com.amazedweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendHttpRequest(final String address , 
			                             final HttpCallbackListener listener){
		
		//�����̴߳�����������
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					//������ַ��������
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					//GET��ʾ���������ȡ����
					connection.setRequestMethod("GET");
					//�����������ӳ�ʱ������ʱ
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					//��ȡ���������ص�������
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line =reader.readLine()) != null){
						response.append(line);
					}
					if (listener != null) {
						//�ص�onFinish����
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						//�ص�onError()����
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
