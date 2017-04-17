package com.boanda.tool.push.base;

import com.boanda.tool.push.util.PushConstants;

import android.text.TextUtils;

/**
 * @Title Configuration.java
 * @Package com.boanda.tool.push.base
 * @Description 推送配置对象
 * @author 苏浩
 * @date 2015年12月21日 下午3:32:28
 * @version V1.0
 */

public class Configuration implements Cloneable{
	
	private String alias;
	private String imei;
	private String appKey = "1234567890";
	private String host;
	private int port;
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public Configuration clone(){
		Configuration obj = new Configuration();
		obj.alias = alias;
		obj.appKey = appKey;
		obj.host = host;
		obj.imei = imei;
		obj.port = port;
		return obj;
	}

	/**
	 * 比对配置是否相同
	 * @param other
	 * @return 0 相同，1 不同，-1 无效配置
	 */
	public static Configuration compare(Configuration first, Configuration second){
		boolean af = isActiveConfig(first);
		boolean as = isActiveConfig(second);
		if(!af && as){
			//TODO 释放持有对象
			return second;
		}else if(af && !as){
			return first;
		}else if(af && as){
			if(first.host.equals(second.host) && first.alias.equals(second.alias)
					&& first.imei.equals(second.imei) && first.port == second.port){
				return first;
			}else{
				return second;
			}
		}else{
			return null;
		}
	}
	
	
	/**是否有效配置*/
	public static boolean isActiveConfig(Configuration config){
		if (config == null) {
			return false;
		} else if (TextUtils.isEmpty(config.host) || "127.0.0.1".equals(config.host) 
				|| TextUtils.isEmpty(config.alias) || config.alias.length() > PushConstants.ALIAS_BYTE_LENGTH
				|| TextUtils.isEmpty(config.imei) || config.port <= 0) {
			//TODO 别名包含特殊字符[\*+.]
			System.out.println("连接配置参数无效，host=" + config.host + "\nalias=" + config.alias + "\nimei=" + config.imei
					+ "\nport=" + config.port);
			return false;
		} else {
			return true;
		}
	}
	
}
