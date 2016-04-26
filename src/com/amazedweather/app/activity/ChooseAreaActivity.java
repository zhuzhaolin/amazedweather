package com.amazedweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.amazedweather.app.R;
import com.amazedweather.app.model.AmazedWeatherDB;
import com.amazedweather.app.model.City;
import com.amazedweather.app.model.County;
import com.amazedweather.app.model.Province;
import com.amazedweather.app.util.HttpCallbackListener;
import com.amazedweather.app.util.HttpUtil;
import com.amazedweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private TextView provinceName;
	private TextView cityName;
	private TextView countyName;
	
	
	private ListView listViewProvince;
	private ListView listViewCity;
	private ListView listViewCounty;
	
	
	private ArrayAdapter<String> adapterPrivince;
	private ArrayAdapter<String> adapterCity;
	private ArrayAdapter<String> adapterCounty;
	
	private AmazedWeatherDB amazedWeatherDB;
	/*
	 * 存放省级， 市级， 县级列表名
	 */
	private List<String> dataList = new ArrayList<String>();
	private List<String> dataListCity = new ArrayList<String>();
	private List<String> dataListCounty = new ArrayList<String>();
	
	
	/*
	 * 
	 * 省列表
	 */
	private List<Province> provinceList;
	
	/*
	 * 市列表
	 */
	private List<City> cityList;
	
	/*
	 * 县列表
	 */
	private List<County> countyList;
	
	/*
	 * 选中的省份
	 */
	private Province selectedProvince;
	
	/*
	 * 选中的城市
	 */
	private City selectedCity;
	
	/*
	 * 选中的县
	 */
	private County selectedCounty;
	
	/*
	 * 选中的级别
	 */
	private int currentLevel;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		
		listViewProvince = (ListView) findViewById(R.id.list_view);
		listViewCity = (ListView) findViewById(R.id.list_city);
		listViewCounty = (ListView) findViewById(R.id.list_county);
		
		titleText = (TextView) findViewById(R.id.title_text);
		
		//获取设置省市县名字的TextView的ID
		provinceName = (TextView) findViewById(R.id.provinceName);
		cityName = (TextView) findViewById(R.id.cityName);
		countyName = (TextView) findViewById(R.id.countyName);
		
		adapterPrivince = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , dataList);
		adapterCity = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , dataListCity);
		adapterCounty = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , dataListCounty);
		
		listViewProvince.setAdapter(adapterPrivince);
		listViewCity.setAdapter(adapterCity);
		listViewCounty.setAdapter(adapterCounty);
		
		
		amazedWeatherDB = AmazedWeatherDB.getInstance(this);
		//省列表点击监听
		listViewProvince.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
					selectedProvince = provinceList.get(position);
					queryCities();
			}
		});
		//市列表点击监听
		listViewCity.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedCity = cityList.get(position);

				queryCounties();
				
			}
		});
		listViewCounty.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				selectedCounty = countyList.get(position);
				countyName.setText(selectedCounty.getCountyName());
				
			}
		});
		queryProvince(); //加载省级数据
	}
	

	
	
   /*
    * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
    */
	
	private void queryProvince(){
		provinceList = amazedWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province  : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapterPrivince.notifyDataSetChanged();
			listViewProvince.setSelection(0);
			titleText.setText("中国");
//			currentLevel = LEVEL_PROVINCE;
		}else {
			queryFromServer(null, "province");
		}
	}
	
	private void queryCities(){
		cityList = amazedWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataListCity.clear();
			for (City city : cityList) {
				dataListCity.add(city.getCityName());
			}
			adapterCity.notifyDataSetChanged();
			listViewCity.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			
			judgeDirectControlled(selectedProvince.getProvinceName());
			//当重新选择省的时候清除市县的名字
			cityName.setText("市");
			countyName.setText("县");
			
//			currentLevel = LEVEL_CITY;
		}else {
			
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	//判断是否为直辖市
	private void judgeDirectControlled(String cityName){
		if ("北京".equals(cityName) || "上海".equals(cityName) || 
			"天津".equals(cityName) || "重庆".equals(cityName)) {
			provinceName.setText(selectedProvince.getProvinceName()+"直辖市");
		}else {
			provinceName.setText(selectedProvince.getProvinceName()+"省");
		}
	}

	private void queryCounties(){
		countyList = amazedWeatherDB.loadCountyies(selectedCity.getId());
		if (countyList.size() > 0) {
			dataListCounty.clear();
			for (County county : countyList) {
				dataListCounty.add(county.getCountyName());
			}
		adapterCounty.notifyDataSetChanged();
		listViewCounty.setSelection(0);
		cityName.setText(selectedCity.getCityName()+"市");
//		titleText.setText(selectedCity.getCityName());
//		currentLevel = LEVEL_COUNTY;
		}else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	/*
	 * 根据传入的代号和类型从服务器上查询省市县的数据，并存入到数据库
	 */
	private void queryFromServer(final String code , final String type){
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(amazedWeatherDB, response);
				}else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(amazedWeatherDB, response, selectedProvince.getId());
				}else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(amazedWeatherDB, response, selectedCity.getId());
				}
				if (result) {
					//通过runOnUiThread()方法回到主线程
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvince();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//通过runOnUiThread()方法回到主线程逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
						
					}
				});
				
			}
		});
	}
	
	/*
	 * 显示进度条
	 */
	private void showProgressDialog(){
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/*
	 * 关闭进度对话宽
	 */
	private void closeProgressDialog(){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}



	
	/*
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市级列表，省级列表，还是直接退出
	 */
		@Override
	public void onBackPressed() {
		  if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		  }else if (currentLevel == LEVEL_CITY) {
			queryProvince();
	      }else {
			finish();
		}
   }
	
}
