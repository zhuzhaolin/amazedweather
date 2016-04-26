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
	 * ���ʡ���� �м��� �ؼ��б���
	 */
	private List<String> dataList = new ArrayList<String>();
	private List<String> dataListCity = new ArrayList<String>();
	private List<String> dataListCounty = new ArrayList<String>();
	
	
	/*
	 * 
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	
	/*
	 * ���б�
	 */
	private List<City> cityList;
	
	/*
	 * ���б�
	 */
	private List<County> countyList;
	
	/*
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	
	/*
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	
	/*
	 * ѡ�е���
	 */
	private County selectedCounty;
	
	/*
	 * ѡ�еļ���
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
		
		//��ȡ����ʡ�������ֵ�TextView��ID
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
		//ʡ�б�������
		listViewProvince.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
					selectedProvince = provinceList.get(position);
					queryCities();
			}
		});
		//���б�������
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
		queryProvince(); //����ʡ������
	}
	

	
	
   /*
    * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
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
			titleText.setText("�й�");
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
			//������ѡ��ʡ��ʱ��������ص�����
			cityName.setText("��");
			countyName.setText("��");
			
//			currentLevel = LEVEL_CITY;
		}else {
			
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	//�ж��Ƿ�ΪֱϽ��
	private void judgeDirectControlled(String cityName){
		if ("����".equals(cityName) || "�Ϻ�".equals(cityName) || 
			"���".equals(cityName) || "����".equals(cityName)) {
			provinceName.setText(selectedProvince.getProvinceName()+"ֱϽ��");
		}else {
			provinceName.setText(selectedProvince.getProvinceName()+"ʡ");
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
		cityName.setText(selectedCity.getCityName()+"��");
//		titleText.setText(selectedCity.getCityName());
//		currentLevel = LEVEL_COUNTY;
		}else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	/*
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ���ص����ݣ������뵽���ݿ�
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
					//ͨ��runOnUiThread()�����ص����߳�
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
				//ͨ��runOnUiThread()�����ص����߳��߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
						
					}
				});
				
			}
		});
	}
	
	/*
	 * ��ʾ������
	 */
	private void showProgressDialog(){
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/*
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog(){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}



	
	/*
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷����м��б�ʡ���б�����ֱ���˳�
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
