package com.boanda.tool.push.receiver;

import com.boanda.tool.push.service.PushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**   
* @Title: UserPresentReceiver.java 
* @Package com.boanda.tool.push.receiver 
* @Description: 手机用户当前状态可用广播接收
* @author 苏浩 
* @date 2015年12月15日 下午5:51:42 
* @version V1.0   
*/

public class UserPresentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PushService.holdService(context);
	}

}
