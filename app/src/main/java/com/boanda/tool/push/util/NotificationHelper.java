package com.boanda.tool.push.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**   
* @Title: NotificationUtils.java 
* @Package com.example.push.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 苏浩 
* @date 2015年11月24日 上午8:59:33 
* @version V1.0   
*/

public class NotificationHelper {

	private Context context;
	private NotificationManager nm;
	
	private static int id = 1;
	
	public NotificationHelper(Context context){
		this.context =  context;
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public PendingIntent activityPendingIntent(int requestCode,
			Intent intent){
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	
	public Notification build(PendingIntent intent, CharSequence title, CharSequence text){
		Notification.Builder builder = new Notification.Builder(context);
		builder.setDefaults(Notification.DEFAULT_LIGHTS);
		builder.setAutoCancel(true);
		builder.setContentIntent(intent);
		builder.setContentTitle(title);
		builder.setContentText(text);
		builder.setSmallIcon(context.getApplicationInfo().icon);
		return builder.build();
	}
	
	public int notify(Notification notification){
		nm.notify(++id, notification);
		return id;
	}
	
}
