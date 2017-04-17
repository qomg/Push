package com.boanda.tool.push.bean;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**   
* @Title: FileContent.java 
* @Package com.example.push.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 苏浩 
* @date 2015年11月24日 上午10:21:40 
* @version V1.0   
*/

public class FileContent extends TextContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2636814878086780355L;

	public FileContent(TextContent content){
		super(content);
	}
	
	public String getPath(){
		return super.getText();
	}
	
	@Override
	public String getText() {
		File file = new File(getPath());
		if(file.exists() && file.isFile())
			return file.getName();
		else
			return "文件不存在或已删除";//
	}
	
	public boolean exists(){
		try {
			return new File(getPath()).exists();
		} catch (Exception e) {
			//  handle exception
		}
		return false;
	}
	
	public Intent getIntent(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		try {
			File file =  new File(getText());
			intent.setData(Uri.fromFile(file));
		} catch (Exception e) {
			//  handle exception
		}
		return intent;
	}
	
}
