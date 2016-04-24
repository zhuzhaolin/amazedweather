package com.amazedweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.amazedweather.app.model.AmazedWeatherDB;
import com.amazedweather.app.model.City;
import com.amazedweather.app.model.County;
import com.amazedweather.app.model.Province;
import com.amazedweather.app.util.HttpCallbackListener;
import com.amazedweather.app.util.HttpUtil;
import com.amazedweather.app.util.Utility;
import com.amazedweather.app.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
		
		adapterPrivince = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , dataList);
		
		
		listViewProvince.setAdapter(adapterPrivince);
		amazedWeatherDB = AmazedWeatherDB.getInstance(this);
		
		listViewProvince.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				}else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				}
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
			currentLevel = LEVEL_PROVINCE;
		}else {
			queryFromServer(null, "province");
		}
	}
	
	private void queryCities(){
		cityList = amazedWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapterPrivince.notifyDataSetChanged();
			listViewProvince.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else {
			
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	private void queryCounties(){
		countyList = amazedWeatherDB.loadCountyies(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
		adapterPrivince.notifyDataSetChanged();
		listViewProvince.setSelection(0);
		titleText.setText(selectedCity.getCityName());
		currentLevel = LEVEL_COUNTY;
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
