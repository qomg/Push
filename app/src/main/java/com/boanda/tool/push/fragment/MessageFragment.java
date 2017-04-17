package com.boanda.tool.push.fragment;

import com.boanda.tool.push.bean.AddressContent;
import com.boanda.tool.push.bean.FileContent;
import com.boanda.tool.push.bean.PushMessage;
import com.boanda.tool.push.bean.TextContent;
import com.boanda.tool.push.util.PushConstants;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**   
* @Title: MessageFragment.java 
* @Package com.boanda.tool.push.activity 
* @Description: 消息详情页面
* @author 苏浩 
* @date 2015年12月11日 下午3:03:41 
* @version V1.0   
*/

public class MessageFragment extends Fragment {

	PushMessage pushMessage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		pushMessage = (PushMessage) getArguments().getSerializable(PushConstants.PUSHMESSAGE);
		//TODO 根据类型加载不同的布局
		TextContent tc = pushMessage.getContent();
		if(tc instanceof FileContent){
			//加载文件内容，缩略图的布局
		}else if(tc instanceof AddressContent){
			//地图展示
			//new LatLng(ac.getLatitude(), ac.getLongitude());
		}else {
			//其他纯文本内容显示文字
		}
		return null;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		//绑定数据
	}
	
}
