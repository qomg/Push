package com.boanda.tool.push.base;

import com.boanda.tool.push.bean.PushMessage;

public interface OnMessageReceivedListener {

	void onMessageReceived(PushMessage msg);
	
}
