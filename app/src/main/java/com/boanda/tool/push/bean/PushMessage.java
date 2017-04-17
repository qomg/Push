package com.boanda.tool.push.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;
import org.xutils.db.annotation.Table;
import org.xutils.db.annotation.Column;

/**
 * @Title: PushMessage.java
 * @Package com.example.push.bean
 * @Description: 推送消息实体类
 * @author 苏浩
 * @date 2015年11月24日 上午10:31:46
 * @version V1.0
 */

@Table(name = "PushMessage")
public class PushMessage implements Parcelable{
	
	@Column(name = "id", isId = true)
	private long id;
	@Column(name = "alias")
	private String alias;
	@Column(name = "date")
	private String date;
	@Column(name = "title")
	private String title;
	@Column(name = "status")
	private int status;
	@Column(name = "type")
	private String type;
	@Column(name = "contentCache")
	private String contentCache;

	private TextContent content;

	public PushMessage(){}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getType(){
		if(content != null && content.getType() != null)
			return content.getType().name();
		else
			return ContentType.TEXT.name();
	}
	
	public void setType(String type){
		this.type = type;
	}

	public String getContentCache() {
		return contentCache;
	}

	public void setContentCache(String contentCache) {
		this.contentCache = contentCache;
		try {
			JSONObject json = new JSONObject(contentCache);
			if(json != null){
				TextContent tc = new TextContent();
				tc.fromJSONObject(json);
				content = TextContentParser.parseContent(tc, json);
			}
		} catch (Exception e) {
			// handle exception
		}
	}

	public TextContent getContent() {
		return content;
	}

	public void setContent(TextContent content) {//
		this.content = content;
	}
	

	public void fromJSONObject(JSONObject json){
		setId(json.optLong("ID"));
		setTitle(json.optString("TITLE"));
		setStatus(json.optInt("STATUS"));
		try {
			setDate(json.optString("DATE"));
		} catch (Exception e) {
			// handle exception
		}
		try {
			JSONObject contentJson = json.optJSONObject("CONTENT");
			if(contentJson == null){
				contentJson = new JSONObject(json.optString("CONTENT"));
			}
			setContentCache(contentJson.toString());
		} catch (Exception e) {
			// handle exception
		}
		
	}
	
	public boolean isEmpty(){
		return getId() == 0 ;
	}
	
	
	@Override
	public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(getId());
        out.writeString(getAlias());
        out.writeString(getDate());
        out.writeString(getTitle());
        out.writeInt(getStatus());
        out.writeString(getType());
        out.writeString(getContentCache());
        out.writeSerializable(getContent());
    }
	
	private PushMessage(Parcel in){
		id = in.readLong();
		alias = in.readString();
		date = in.readString();
		title = in.readString();
		status = in.readInt();
		type = in.readString();
		contentCache = in.readString();
		content = (TextContent) in.readSerializable();
	}
	
	public static final Creator<PushMessage> CREATOR = new Creator<PushMessage>() {
		public PushMessage createFromParcel(Parcel in) {
			return new PushMessage(in);
		}

		public PushMessage[] newArray(int size) {
			return new PushMessage[size];
		}
	};
	
}
