package com.boanda.tool.push.base;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

import com.boanda.tool.push.bean.PushMessage;
import com.boanda.tool.push.util.Async;
import com.boanda.tool.push.util.PushConstants;

/**   
* @Title: PacketReader.java 
* @Package com.boanda.tool.push.base 
* @Description: 消息包接收解析工具
* @author 苏浩 
* @date 2015年12月21日 下午3:30:15 
* @version V1.0   
*/

public class PacketReader {
	
	private DataInputStream in;
	private PacketParser packetParser;
	
	public PacketReader(DataInputStream in){
		this.in = in;
		packetParser = new PacketParser();
	}
	
	public void destroy(){
		if(in != null){
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			in = null;
		}
		packetParser = null;
	}
	
	public void init(){
		//开始接收
		Async.go(receiver, "Receive Thread");
	}
	
	private long beatReceiveMillis;
	
	Runnable receiver = new Runnable(){

		@Override
		public void run() {
			while(in != null){
				//TODO 重写，支持多通道，即同时发送多条消息，实际是一条消息分条发送，类似TCP
				try {// 只要没被置空，就去读。不能用while会阻塞
					byte[] header = new byte[10];
					//因为一直在发送心跳包，所以可以一直读消息
					int len = in.read(header);
					if (len == 10) {//头长度10
						PushMessage pushMessage = packetParser.parse(header, in);
						if(pushMessage != null)
							beatReceiveMillis = System.currentTimeMillis();
						Connection.getInstance().onMessageReceived(pushMessage);
					}
				} catch (IOException e) {
					e.printStackTrace();
					if(e instanceof SocketException){
						//TODO 连接断开
						Connection.getInstance().reconnect();
					}
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				long current = System.currentTimeMillis();
				if(current - beatReceiveMillis > PushConstants.BEAT_TIMEOUT){
					Connection.getInstance().reconnect();
				}
			}
		}
		
	};
}
