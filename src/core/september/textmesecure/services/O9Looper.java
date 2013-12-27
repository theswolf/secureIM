package core.september.textmesecure.services;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class O9Looper extends Thread{

	private boolean isReady = false;
	private Handler mHandler = null;
	@Override 
	public void run() {
		Looper.prepare();
		mHandler = new Handler(){
			@Override
			public void  handleMessage(Message msg) {
				
			}
		};
		Looper.loop();
	}
	
	public void postMessage(Message msg) {
		mHandler.sendMessage(msg);
	}
}
