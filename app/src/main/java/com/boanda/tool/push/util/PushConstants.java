package com.boanda.tool.push.util;
/**   
* @Description: 消息推送常量声明
<br/>客户端到服务端发送消息格式
<br/>VERSION(1)+COMMOND(1)+APPKEY(10)+IMEI(15)+ALIAS(20)=47(字节)
<br/>服务端到客户端
<br/>VERSION(1)+COMMOND(1)+CONTENTLENGTH(4)+EXTRALENGTH(4)+[EXTRA]=10+EXTRALENGTH
* @author 苏浩 
* @date 2015年11月24日 上午11:09:52 
* @version V1.0   
*/

public class PushConstants {
	/**心跳超时时间，单位毫秒*/
	public static final int BEAT_TIMEOUT = 60 * 1000;
	/**推送消息保存数据库*/
	public static final String DATABASE = "push.db";
	/**传递消息对象参数名*/
	public static final String PUSHMESSAGE = "PushMessage";
	/**配置文件名*/
	public static final String SHARE_PREFERENCE_NAME = "push_prefs";
	/**端口参数名*/
	public static final String PREF_ALIAS = "alias";
	/**端口参数名*/
	public static final String PREF_PORT = "port";
	/**主机参数名*/
	public static final String PREF_HOST = "host";
	/**应用授权key参数名*/
	public static final String PREF_APPKEY = "appkey";
	/**接收到消息后发送广播action名字*/
	public static final String ACTION_PUSH_MESSAGE = "boanda.action.PushMessage";
	/*别名长度，字符串*/
	public static final int ALIAS_BYTE_LENGTH = 20;
	/*系统版本号，数字*/
	public static final int VERSION_BYTE_LENGTH = 1;
	/*命令，数字*/
	public static final int COMMOND_BYTE_LENGTH = 1;
	/*应用密钥，字符串*/
	public static final int APPKEY_BYTE_LENGTH = 10;
	/*设备号，字符串*/
	public static final int IMEI_BYTE_LENGTH = 15;
	/**发送消息包字节长度*/
	public static final int SEND_PACKET_BYTE_LENGTH = VERSION_BYTE_LENGTH + COMMOND_BYTE_LENGTH + APPKEY_BYTE_LENGTH
			+ IMEI_BYTE_LENGTH + ALIAS_BYTE_LENGTH;
}
