package com.boanda.tool.push.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;

import com.boanda.tool.push.bean.Command;
import com.boanda.tool.push.bean.PushMessage;

/**   
* @Title: Connection.java 
* @Package com.boanda.tool.push.base 
* @Description: 推送连接类
* @author 苏浩 
* @date 2015年12月21日 下午3:29:07 
* @version V1.0   
*/

public class Connection {
	/**socket连接配置*/
	private Configuration config;
	/**建立的socket对象*/
	private Socket socket;
	/**发消息工具*/
	private PacketWriter writer;
	/**收消息工具*/
	private PacketReader reader;
	/**定时执行服务*/
	private ScheduledExecutorService connectService;
	/**断线重现线程，仅第一次初始化*/
	private ReconnectionThread reconnectThread;
	/**锁*/
	private Object lock = new Object();
	/**接收到消息监听器*/
	private OnMessageReceivedListener listener;
	/**单例调用防止重复创建，因为持有socket对象，重复创建可能耗内存*/
	private static Connection instance;
	
	private Connection(){}
	
	public static Connection getInstance(){
		if(instance == null){
			instance = new Connection();
		}
		return instance;
	}
	
	
	public void registerListener(OnMessageReceivedListener listener){
		this.listener = listener;
	}
	
	public void onMessageReceived(PushMessage msg){
		if(listener != null)
			listener.onMessageReceived(msg);
	}
	
	public Configuration getConfiguration(){
		return config;
	}
	
	public void setConfiguration(Configuration other){
		Configuration temp = Configuration.compare(config, other);
		if(temp != null && temp != config){
			config = temp;
			// reset
			System.out.println("配置已更新，重连");
			start();
		}
	}
	

	public void start(){
		if(!Configuration.isActiveConfig(config))
			return;
		if(isConnected()){
			writer.sendPacket(Command.RENAME);
		}else{
			reconnect();
		}
	}
	
	public boolean isConnected(){
		//服务器关闭的时候的socket状态依然是连接的
		boolean con = socket != null && !socket.isClosed() && socket.isConnected();
		System.out.println("连接状态" + (con ? "已连接" : "断开"));
		return con;
	}
	
	public void login(){
		System.out.println("发送登录验证消息");
		writer.sendPacket(Command.VERIFY);
	}
	
	/**清除socket，断开连接*/
	public void disconnect(){
		destroy();
		if(reconnectThread != null){
			try {
				reconnectThread.safeInterrupt();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				reconnectThread = null;
			}
		}
	}
	
	/**重连，不需要调用{@link #destroy}销毁socket，
	 * 线程内部调用{@link #connect}时会自动调用*/
	public void reconnect(){
		synchronized (lock) {
			//直接中断，重新开启
			if(reconnectThread == null){
				//尽量到用的时候才创建对象
				reconnectThread = new ReconnectionThread();
			}
			//FIXME 调用重连的地方比较多，注意线程启动。
			//调用start之后isAlive=true
			if(!reconnectThread.isAlive()){
				reconnectThread.safeInterrupt();
				// 中断后重新创建一个重连线程
				reconnectThread = new ReconnectionThread();
				try {
					reconnectThread.start();
					System.out.println("重连线程启动");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//如果alive什么都不做
        }
	}
	
	/**
	 * 要在单独线程中调用
	 */
	public void connect(){
		destroy();//先销毁之前的对象
		try {
			socket = new Socket();
			socket.setKeepAlive(true);
			socket.setReceiveBufferSize(8192);
			long start = System.currentTimeMillis();
			System.out.println("开始socket连接");
			socket.connect(new InetSocketAddress(config.getHost(), config.getPort()), 10000);// 10秒连接不到服务器就算超时
			System.out.println("建立连接耗时= "+(System.currentTimeMillis()-start)+" 毫秒");
			socket.sendUrgentData(0xFF);
			reconnectThread.safeInterrupt();
			
			writer = new PacketWriter(new DataOutputStream(socket.getOutputStream()));
			reader = new PacketReader(new DataInputStream(socket.getInputStream()));
			writer.init();
			reader.init();
			
			login();
			//TODO 并不一定连接成功
			System.out.println("连接成功");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void destroy(){
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
			System.out.println("socket已销毁");
		}
		if(reader != null){
			reader.destroy();
			reader = null;
			System.out.println("reader已销毁");
		}
		if(writer != null){
			writer.destroy();
			writer = null;
			System.out.println("writer已销毁");
		}
		if(connectService != null){
			connectService.shutdownNow();
			connectService = null;
		}
	}
	
	public interface OnMessageReceivedListener {
		void onMessageReceived(PushMessage msg);
	}
}
