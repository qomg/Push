package com.boanda.tool.push.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Title: Content.java
 * @Package com.example.push.bean
 * @Description: 推送消息文本内容
 * @author 苏浩
 * @date 2015年11月24日 上午10:11:48
 * @version V1.0
 */

public class TextContent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2024409883371753108L;
	/** 内容类型 */
	private ContentType type;
	/** 文本内容 */
	private String text;

	public TextContent(){
		
	}

	public TextContent(String type, String text){
		setType(type);
		setText(text);
	}
	
	public TextContent(TextContent content){
		setText(content.getText());
		setType(content.getType());
	}
	
	public ContentType getType() {
		return type;
	}
	
	public void setType(ContentType type) {
		this.type = type;
	}

	public void setType(String type) {
		ContentType temp = null;
		try {
			temp = ContentType.valueOf(type);
		} catch (Exception e) {
		}
		if(temp != null)
			this.type = temp;
		else
			this.type = ContentType.TEXT;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void fromJSONObject(JSONObject json){
		try {
			setType(json.optString("TYPE"));
			setText(json.optString("TEXT"));
		} catch (Exception e) {
			// handle exception
		}
	}
	
	public JSONObject toJSONObject(){
		JSONObject json = new JSONObject();
		try {
			json.put("TYPE", getType().toString());
			json.put("TEXT", getText());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	@Override
	public String toString() {
		return toJSONObject().toString();
	}

}
