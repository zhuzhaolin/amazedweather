package com.amazedweather.app.util;

import android.app.Activity;
import android.os.Bundle;
/**
 * 
 * @author zhuzhaolin
 *这个类用于知晓当前进入哪个活动之中
 */
public class BaseActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("I'm is", getClass().getSimpleName());
	}

	
}
