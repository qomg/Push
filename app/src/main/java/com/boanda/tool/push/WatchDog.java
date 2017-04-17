package com.boanda.tool.push;

import java.util.ArrayList;

import com.boanda.tool.push.service.PushService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

/**
 * 监视器类，构造时将会在Native创建子进程来监视当前进程，
 * 
 * @author wangqiang
 * @date 2014-04-24
 */
public class WatchDog {
	
	private String mMonitoredService = PushService.class.getName();
	//private volatile boolean bHeartBreak = false;
	private Context mContext;
	private boolean mRunning = true;

	public void createAppMonitor(String userId) {
		String sock = mContext.getFilesDir().getPath()+"/my.sock";
		if (!createWatcher(userId, sock, mContext.getPackageName()+"/"+PushService.class.getName())) {
			Log.e("Watcher", "<<Monitor created failed>>");
		}
	}

	public WatchDog(Context context) {
		mContext = context;
	}

	private int isServiceRunning() {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) am.getRunningServices(1024);
		for (int i = 0; i < runningService.size(); ++i) {
			if (mMonitoredService.equals(runningService.get(i).service.getClassName().toString())) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Native方法，创建一个监视子进程.
	 * 
	 * @param userId
	 *            当前进程的用户ID,子进程重启当前进程时需要用到当前进程的用户ID.
	 * @param sock /data/data/xx/my.sock 其中xx对应包名
	 * @param service xx/com.boanda.tool.push.PushService 其中xx对应包名 
	 * @return 如果子进程创建成功返回true，否则返回false
	 */
	private native boolean createWatcher(String userId, String sock, String service);

	/**
	 * Native方法，让当前进程连接到监视进程.
	 * 
	 * @return 连接成功返回true，否则返回false
	 */
	private native boolean connectToMonitor();

	/**
	 * Native方法，向监视进程发送任意信息
	 * 
	 * @param 发给monitor的信息
	 * @return 实际发送的字节
	 */
	private native int sendMsgToMonitor(String msg);

	static {
		System.loadLibrary("daemon");
	}
	
}
