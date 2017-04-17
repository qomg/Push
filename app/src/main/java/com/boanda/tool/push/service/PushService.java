package com.boanda.tool.push.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.boanda.tool.push.base.Configuration;
import com.boanda.tool.push.base.Connection;
import com.boanda.tool.push.bean.PushMessage;
import com.boanda.tool.push.receiver.ConnectivityReceiver;
import com.boanda.tool.push.receiver.UserPresentReceiver;
import com.boanda.tool.push.util.PushConstants;

import org.xutils.DbManager;
import org.xutils.x;

/**   
* @Title: PushService.java 
* @Package com.example.push.service 
* @Description: 消息推送服务
* @author 苏浩 
* @date 2015年11月24日 上午11:07:20 
* @version V1.0   
*/

public class PushService extends Service
		implements Connection.OnMessageReceivedListener, ConnectivityReceiver.OnNetworkChangedListener {
	public static final int INTERVAL_MILLIS = 3 * 60 * 1000;//3分钟
	public static final int JOB_ID = 101;
	
	ConnectivityReceiver cReceiver;
	UserPresentReceiver uReceiver;
	Connection connection;
	
	@Override
	public void onCreate() {
		super.onCreate();
		//initial variable
		x.Ext.init(getApplication());
		Connection.getInstance().registerListener(this);
		
		uReceiver = new UserPresentReceiver();
		cReceiver = new ConnectivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction(Intent.ACTION_BOOT_COMPLETED);
		registerReceiver(uReceiver, filter);
		registerReceiver(cReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		cReceiver.setOnNetworkChangedListener(this);
	}
	
	@Override
	public void onDestroy() {
		try {
			unregisterReceiver(cReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			unregisterReceiver(uReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		startService(new Intent(this, DaemonService.class));
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		startService(new Intent(this, DaemonService.class));
		//添加定时检查并开始推送服务
		/*try {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				addShechuledJob();//第一种方式
			}else{
				addAlarm();//第二种方式 无效
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		setConfiguration(intent);
		flags = START_FLAG_RETRY;
		return super.onStartCommand(intent, flags, startId);
	}
	

	
	private void setConfiguration(Intent intent) {
		Configuration config = null;
		if(Connection.getInstance().getConfiguration() != null){
			config = Connection.getInstance().getConfiguration().clone();
		}else{
			config = new Configuration();
			TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			config.setImei(tel.getDeviceId());
		}
		SharedPreferences defs = PreferenceManager.getDefaultSharedPreferences(this);
		String appKey = defs.getString(PushConstants.PREF_APPKEY, "1234567890");
		String host = defs.getString(PushConstants.PREF_HOST, "127.0.0.1");
		String alias = defs.getString(PushConstants.PREF_ALIAS, "dev");
		int port = defs.getInt(PushConstants.PREF_PORT, 0);
		if(intent != null && intent.getExtras() != null){
			Bundle args = intent.getExtras();
			appKey = args.getString(PushConstants.PREF_APPKEY, appKey);
			host = args.getString(PushConstants.PREF_HOST, host);
			alias = args.getString(PushConstants.PREF_ALIAS, alias);
			port = args.getInt(PushConstants.PREF_PORT, port);
			//save to preference
			defs.edit()
				.putString(PushConstants.PREF_APPKEY, appKey)
				.putString(PushConstants.PREF_HOST, host)
				.putString(PushConstants.PREF_ALIAS, alias)
				.putInt(PushConstants.PREF_PORT, port)
				.commit();
		}
		config.setAppKey(appKey);
		config.setHost(host);
		config.setAlias(alias);
		config.setPort(port);
		Connection.getInstance().setConfiguration(config);
	}
	
	@Override
	public void onMessageReceived(PushMessage msg) {
		if(msg != null && !msg.isEmpty()){
			try {
				x.getDb(daoConfig).save(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(PushConstants.ACTION_PUSH_MESSAGE);
			intent.putExtra(PushConstants.PUSHMESSAGE, msg);
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//非必需
			sendBroadcast(intent);
		}
	}
	
	@Override
	public void onNetworkChanged(boolean connected) {
		if(connected){
			//只要发生网络变化，并且网络是连接状态，就重连
			Connection.getInstance().reconnect();
		}else{
			Connection.getInstance().disconnect();
		}
		
	}

	public static DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
			.setDbName(PushConstants.DATABASE)
			// 不设置dbDir时, 默认存储在app的私有目录.
			.setDbDir(Environment.getExternalStoragePublicDirectory("push-native"))
			.setDbVersion(2)
			.setDbOpenListener(new DbManager.DbOpenListener() {
				@Override
				public void onDbOpened(DbManager db) {
					// 开启WAL, 对写入加速提升巨大
					db.getDatabase().enableWriteAheadLogging();
				}
			})
			.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
				@Override
				public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
					// TODO: ...
					// db.addColumn(...);
					// db.dropTable(...);
					// ...
					// or
					// db.dropDb();
				}
			});

	
	public static ComponentName holdService(Context context){
		return context.startService(new Intent(context, PushService.class));
	}
	
	public static PendingIntent pendingIntent(Context context){
		return PendingIntent.getService(context, JOB_ID, new Intent(context, PushService.class), PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
}
