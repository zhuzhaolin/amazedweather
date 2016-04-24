package com.amazedweather.app.util;

import com.amazedweather.app.model.AmazedWeatherDB;
import com.amazedweather.app.model.City;
import com.amazedweather.app.model.County;
import com.amazedweather.app.model.Province;

import android.text.TextUtils;

public class Utility {

	/*
	 * 解析和处理服务器返回的省级数据
	 */
	
	public synchronized static boolean handleProvincesResponse(AmazedWeatherDB 
			                             amazedWeatherDB , String response){
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到Province表
					amazedWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(AmazedWeatherDB amazedWeatherDB ,
			                               String response , int provinceId){
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c: allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析解析出来的数据存储到City
					amazedWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(AmazedWeatherDB amazedWeatherDB , 
			                                String response ,int cityId){
		
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据储存到County表
					amazedWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
		
		return false;
	}
}
