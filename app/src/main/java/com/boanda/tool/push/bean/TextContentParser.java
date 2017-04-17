package com.boanda.tool.push.bean;

import org.json.JSONObject;

/**
 * @Title: PushUtils.java
 * @Package com.boanda.tool.push.util
 * @Description: 消息推送工具类
 * @author 苏浩
 * @date 2015年12月18日 下午2:53:07
 * @version V1.0
 */

public class TextContentParser {
	
	public static TextContent parseContent(JSONObject contentJson) {
		TextContent textContent = new TextContent();
		textContent.fromJSONObject(contentJson);
		return parseContent(textContent, contentJson);
	}

	/**
	 * 
	 * @param textContent 必须非空
	 * @param contentJson
	 * @return
	 */
	public static TextContent parseContent(TextContent textContent, JSONObject contentJson) {
		if (textContent.getType() == ContentType.ADDRESS) {
			AddressContent addressContent = new AddressContent(textContent);
			addressContent.fromJSONObject(contentJson);
			return addressContent;
		} else if (textContent.getType() == ContentType.WORKFLOW) {
			WorkflowContent workflowContent = new WorkflowContent(textContent);
			workflowContent.fromJSONObject(contentJson);
			return workflowContent;
		} else if (textContent.getType() == ContentType.DOCUMENT || textContent.getType() == ContentType.VOICE
				|| textContent.getType() == ContentType.PICTURE) {
			return new FileContent(textContent);
		}
		return textContent;
	}
}
