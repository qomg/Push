/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boanda.tool.push.base;

/**
 * A thread class for recennecting the server.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ReconnectionThread extends Thread implements Thread.UncaughtExceptionHandler {

	private int tick;

	ReconnectionThread() {
		this.tick = 0;
		setName("Reconnection Thread");
		setUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(ex != null){
			System.out.println("重连线程出现异常");
			ex.printStackTrace();
		}
	}

	public void run() {
		try {
			System.out.println("tick start with=" + tick);
			while (tick >= 0 && !isInterrupted()) {
				rest();
				Connection.getInstance().connect();
				tick++;
				System.out.println("tick current value=" + tick);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void rest() throws InterruptedException{
		long millis = 1000;
		if (tick > 20) {
			millis *= 300;//300s rest longer
		}else if (tick > 13) {
			millis *= 150;//150s
		}else if (tick > 7){
			millis *= 60;//60s
		}else {
			millis *= 10;//10s
		}
		sleep(millis);
	}

	public void safeInterrupt(){
		tick = -100;
	}
	
}
