package com.boanda.tool.push.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DaemonService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startService(new Intent(this, PushService.class));
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		startService(new Intent(this, PushService.class));
		super.onDestroy();
	}

	
}
