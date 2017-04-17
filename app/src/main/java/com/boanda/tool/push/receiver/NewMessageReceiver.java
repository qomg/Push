package com.boanda.tool.push.receiver;

import com.boanda.tool.push.bean.PushMessage;
import com.boanda.tool.push.util.NotificationHelper;
import com.boanda.tool.push.util.PushConstants;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**   
* @Title: NewMessageReceiver.java 
* @Package com.boanda.tool.push.receiver 
* @Description: 新消息广播接收器，模板
* @author 苏浩 
* @date 2015年12月21日 下午5:06:03 
* @version V1.0   
*/
public class NewMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(PushConstants.ACTION_PUSH_MESSAGE)){
			processReceive(context, intent);
		}
	}

	private void processReceive(Context context, Intent intent){
		if(PushConstants.ACTION_PUSH_MESSAGE.equals(intent.getAction())){
			PushMessage pushMessage = intent.getParcelableExtra(PushConstants.PUSHMESSAGE);
			Intent mIntent = new Intent("ACTION_MESSAGE_DETAIL");
			mIntent.putExtra(PushConstants.PUSHMESSAGE, pushMessage);
			try {
				NotificationHelper notiHelper = new NotificationHelper(context);
				Notification notification = process(notiHelper.build(notiHelper.activityPendingIntent((int) pushMessage.getId(), mIntent),
						pushMessage.getTitle(), pushMessage.getContent().getText()));
				if(notification != null)
					notiHelper.notify(notification);
			} catch (Exception e) {
				// handle exception
			}
		}
	}
	
		
	public Notification process(Notification notification){
		return notification;
	}
		
}
