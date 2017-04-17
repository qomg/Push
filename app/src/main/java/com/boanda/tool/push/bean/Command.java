package com.boanda.tool.push.bean;
/**   
* @Title: Command.java 
* @Package com.example.push.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 苏浩 
* @date 2015年11月24日 上午9:32:34 
* @version V1.0   
*/

public enum Command {

	HEARTBEAT(0),
	VERIFY(1),
	PUSH(2),
	RENAME(20);
	
	private int value;
	
	private Command(int value){
		this.value = value;
	}
	
	public byte byteValue(){
		return (byte) value;
	}
	
	public int value(){
		return value;
	}
}
