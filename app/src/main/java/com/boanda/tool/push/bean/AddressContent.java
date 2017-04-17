package com.boanda.tool.push.bean;

import org.json.JSONObject;

/**
 * @Title: AddressContent.java
 * @Package com.example.push.bean
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 苏浩
 * @date 2015年11月24日 上午10:19:08
 * @version V1.0
 */

public class AddressContent extends TextContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6813514818972681516L;
	private double latitude;
	private double longitude;
	
	public AddressContent(TextContent content){
		super(content);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		try {
			this.latitude = Double.parseDouble(latitude);
		} catch (Exception e) {
		}

	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		try {
			this.longitude = Double.parseDouble(longitude);
		} catch (Exception e) {
		}
	}

	@Override
	public String getText() {
		return "经纬度("+getLongitude()+","+getLatitude()+")";
	}
	
	@Override
	public void fromJSONObject(JSONObject json) {
		super.fromJSONObject(json);
		try {
			setLatitude(json.optString("LATITUDE"));
			setLongitude(json.optString("LONGITUDE"));
		} catch (Exception e) {
			// handle exception
		}
	}
	
	@Override
	public String toString() {
		JSONObject json = toJSONObject();
		try {
			json.put("LATITUDE", getLatitude()+"");
			json.put("LONGITUDE", getLongitude()+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
}
