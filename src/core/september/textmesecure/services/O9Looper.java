package core.september.textmesecure.services;

import java.util.LinkedList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.quickblox.core.QBCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;

import core.september.textmesecure.configs.Messages;
import core.september.textmesecure.interfaces.O9LooperService;
import core.september.textmesecure.interfaces.OnComplete;
import core.september.textmesecure.sql.models.User;

public class O9Looper extends Thread{
	
	

	private boolean isReady = false;
	private Handler mHandler = null;
	private static boolean sessionCreated = false;
	private static boolean loggedIn = false;
	private LinkedList<Message> queue = new LinkedList<Message>();
	private O9LooperService looperService;
	
//	public O9Looper(O9IMService service) {
//		this.service = service;
//	}
	
	public O9Looper(O9LooperService looperService) {
		this.looperService = looperService;
	}
	@Override 
	public void run() {
		Looper.prepare();
		mHandler = new Handler(){
			@Override
			public void  handleMessage(Message msg) {
				int mType = msg.what;
				switch (mType) {
				
				case Messages.READY:
					long currentTime = System.currentTimeMillis();
					createSession(new OnComplete() {
						
						@Override
						public void complete() {
							User user = looperService.getUser();
							looperService.signInUser(user.getUsername(), user.getEmail());
							
						}
					});

					
					break;

				case Messages.QBUSERSIGNED:
					looperService.connect();
					break;
				default:
					break;
				}
			}
		};
		Looper.loop();
	}
	
	public void postMessage(Message msg) {
		if(mHandler != null) {
			for(Message queuedMsg: queue) {
				mHandler.sendMessage(queuedMsg);
			}
			mHandler.sendMessage(msg);
		}
		else {
			queue.add(msg);
		}
	}
	
	
	
	private void createSession(final OnComplete complete) {
		QBSettings.getInstance().fastConfigInit("4696", "OYQ2G3syJAYCbpD", "nevsq5jds-eyS7n");
        QBAuth.createSession(new QBCallback() {
			
			@Override
			public void onComplete(Result result, Object arg1) {
				if (result.isSuccess()) { 
					User user = looperService.getUser();
					looperService.signInUser(user.getUsername(), user.getEmail());
				}
				sessionCreated = result.isSuccess();
				if(complete != null) {
					complete.complete();
				}
				
			}
			
			@Override
			public void onComplete(Result result) {
				onComplete(result,null);
				
			}
		});
	}
}
