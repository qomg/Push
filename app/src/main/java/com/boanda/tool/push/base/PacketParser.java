package com.boanda.tool.push.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.boanda.tool.push.bean.Command;
import com.boanda.tool.push.bean.PushMessage;

import android.os.Environment;
import android.text.TextUtils;

/**   
* @Title: PacketParser.java 
* @Package com.example.push.service 
* @Description: 消息包解析工具
* @author 苏浩 
* @date 2015年11月24日 下午12:04:12 
* @version V1.0   
*/

public class PacketParser {

	String fileDir;
	
	public PacketParser(){
		fileDir = Environment.getExternalStorageDirectory()+"/cache/file_recv/";
		try {
			new File(fileDir).mkdirs();
		} catch (Exception e) {
			// handle exception
		}
	}
	
	/**
	 * 
	 * @param bytes 低位在后
	 * @return [0,0,0,1] = 1 
	 */
	public int bytes2int(byte[] bytes){
		if (bytes.length == 1)
			return 0xFF & bytes[0];
		if (bytes.length == 2)
			return (0xFF & bytes[0]) << 8 | 0xFF & bytes[1];
		if (bytes.length == 4) {
			return (0xFF & bytes[0]) << 24 | (0xFF & bytes[1]) << 16 | (0xFF & bytes[2]) << 8 | 0xFF & bytes[3];
		}
		return 0;
	}
	
	
	/**
	 * 
	 * @param header 长度10
	 * @param in
	 * @throws IOException 
	 */
	public PushMessage parse(byte[] header, InputStream in) throws IOException{
		PushMessage pMsg = new PushMessage();
		int command = bytes2int(new byte[]{header[1]});
		if(command == Command.HEARTBEAT.value()){
		}else if(command == Command.PUSH.value()){
			int contentLength = bytes2int(new byte[]{header[2],header[3],header[4],header[5]});
			try {
				byte[] cbuf = new byte[contentLength];
				int len = in.read(cbuf);
				JSONObject pushJson = new JSONObject(new String(cbuf, 0, len));
				pMsg.fromJSONObject(pushJson);
			} catch (Exception e) {
				throw new IOException(e);
			}
			int extLength = bytes2int(new byte[]{header[6],header[7],header[8],header[9]});
			if(extLength > 0){
				try {
					String filename = pMsg.getContent().getText();
					if(!TextUtils.isEmpty(filename)){
						//耗时
						//FIXME pMsg.getContent().setText(FileUtils.createFile(fileDir+"/"+filename, in, extLength).getAbsolutePath());
					}
				} catch (Exception e) {
					//TODO 文件接收失败
					throw new IOException(e);
				}
			}
		}
		return pMsg;
	}
	
}
