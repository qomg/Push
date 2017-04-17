package com.boanda.tool.push.base;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.boanda.tool.push.bean.Command;
import com.boanda.tool.push.util.Async;
import com.boanda.tool.push.util.PushConstants;

import android.os.Build;

/**   
* @Title: PacketWriter.java 
* @Package com.boanda.tool.push.base 
* @Description: 写消息工具 
* @author 苏浩 
* @date 2015年12月21日 下午3:30:24 
* @version V1.0   
*/

public class PacketWriter {

	private DataOutputStream out;
	private ScheduledExecutorService heartbeatService;
	
	
	public PacketWriter(DataOutputStream out){
		this.out = out;
		heartbeatService = Executors.newSingleThreadScheduledExecutor();
	}

	public void sendPacket(Command command){
		Async.go(new SimpleSend(command));
	}
	
	public void destroy(){
		if(out != null){
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			out = null;
		}
		if(heartbeatService != null){
			heartbeatService.shutdownNow();
			heartbeatService = null;
		}
	}
	
	public void init(){
		// 发送心跳
		heartbeatService.scheduleAtFixedRate(new SimpleSend(Command.HEARTBEAT), 1000, 5000, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	private byte[] createPacket(Command command) {
		Configuration config = Connection.getInstance().getConfiguration();
		try {
			return new PacketBuilder().setOSVersion(Build.VERSION.SDK_INT).setCommand(command).setAppKey(config.getAppKey())
					.setDeviceId(config.getImei()).setAlias(config.getAlias()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	class PacketBuilder{
		
		private byte[] packet;
		
		public PacketBuilder(){
			packet = new byte[PushConstants.SEND_PACKET_BYTE_LENGTH];
		}
		
		public PacketBuilder setOSVersion(int sdkInt){
			packet[0] = (byte) sdkInt;// 注意长度变化
			return this;
		}
		
		public PacketBuilder setCommand(Command command){
			packet[1] = command.byteValue();
			return this;
		}
		
		public PacketBuilder setAppKey(String appKey){
			byte[] akey = appKey.getBytes();
			int start = PushConstants.VERSION_BYTE_LENGTH + PushConstants.COMMOND_BYTE_LENGTH;
			for (int j = 0; j < PushConstants.APPKEY_BYTE_LENGTH; j++) {
				packet[start++] = akey[j];
			}
			return this;
		}
		
		public PacketBuilder setDeviceId(String imei) {
			byte[] deviceid = imei.getBytes();
			int start = PushConstants.VERSION_BYTE_LENGTH + PushConstants.COMMOND_BYTE_LENGTH + PushConstants.APPKEY_BYTE_LENGTH;
			for (int j = 0; j < deviceid.length && j < PushConstants.IMEI_BYTE_LENGTH; j++) {
				packet[start++] = deviceid[j];//
			}
			return this;
		}
		
		public PacketBuilder setAlias(String alias) {
			byte[] name = alias.getBytes();// 空指针异常
			int start = PushConstants.VERSION_BYTE_LENGTH + PushConstants.COMMOND_BYTE_LENGTH
					+ PushConstants.APPKEY_BYTE_LENGTH + PushConstants.IMEI_BYTE_LENGTH;
			//如果别名长度小于总长度，重新计算起始位置
			if (name.length < PushConstants.ALIAS_BYTE_LENGTH) {
				start += PushConstants.ALIAS_BYTE_LENGTH - name.length;
			}
			for (int j = 0; j < PushConstants.ALIAS_BYTE_LENGTH && j < name.length; j++) {
				packet[start++] = name[j];
			}
			return this;
		}
		
		public byte[] build(){
			return packet;
		}
		
	}
	
	class SimpleSend implements Runnable{

		Command command ;
		
		SimpleSend(Command command ){
			this.command  = command;
		}
		
		@Override
		public void run() {
			byte[] packet = createPacket(command);
			if(out != null && packet != null){
				synchronized (out) {//lock
					try {
						out.write(packet);
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
						//发送失败
						destroy();
					}
				}
			}
		}
		
	}
}
