package com.boanda.tool.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @Title: ConnectivityReceiver.java
 * @Package com.example.push.receiver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 苏浩
 * @date 2015年11月25日 上午8:47:04
 * @version V1.0
 */

public class ConnectivityReceiver extends BroadcastReceiver {

	private static final String TAG = "ConnectivityReceiver";

	private OnNetworkChangedListener listener;
	
	public void setOnNetworkChangedListener(OnNetworkChangedListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("connectivity action ="+intent.getAction());
		System.out.println("occurs at="+System.currentTimeMillis());
		boolean connected = false;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
		if (activeInfo != null && activeInfo.isConnected()) {
			//TODO 支持自选某一个网络
			boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
			boolean mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
			if (wifiConnected) {
				Log.i(TAG, "wifi connected");
			} else if (mobileConnected) {
				Log.i(TAG, "mobile connected");
			}
			connected = true;
		} else {
			Log.i(TAG, "no wifi or mobile have been connected");
			connected = false;
		}
		if(listener != null){
			//TODO 防止前后重复执行
			listener.onNetworkChanged(connected);
		}
	}

	public interface OnNetworkChangedListener{
		void onNetworkChanged(boolean connected);
	}
}
