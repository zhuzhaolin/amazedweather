package com.amazedweather.app.util;

import android.app.Activity;
import android.os.Bundle;
/**
 * 
 * @author zhuzhaolin
 *���������֪����ǰ�����ĸ��֮��
 */
public class BaseActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("I'm is", getClass().getSimpleName());
	}

	
}
