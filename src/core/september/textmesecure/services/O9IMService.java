package core.september.textmesecure.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.spongycastle.crypto.digests.MD5Digest;
import org.xml.sax.SAXException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.niusounds.asd.SQLiteDAO;

import core.september.textmesecure.Login;
import core.september.textmesecure.Messaging;
import core.september.textmesecure.R;
import core.september.textmesecure.communication.O9SocketOperator;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.interfaces.ISocketOperator;
import core.september.textmesecure.interfaces.IUpdateData;
import core.september.textmesecure.sql.models.User;
import core.september.textmesecure.tools.FriendController;
import core.september.textmesecure.tools.XMLHandler;
import core.september.textmesecure.types.FriendInfo;

public class O9IMService extends Service implements IAppManager, IUpdateData {
	//	private NotificationManager mNM;

	public static final String TAG = O9IMService.class.getName();
	public static final String TAKE_MESSAGE = "Take_Message";
	public static final String FRIEND_LIST_UPDATED = "Take Friend List";
	public ConnectivityManager conManager = null; 
	private final int UPDATE_TIME_PERIOD = 15000;
	//	private static final int LISTENING_PORT_NO = 8956;
	private String rawFriendList = new String();


	//ISocketOperator socketOperator = new O9SocketOperator(this);

	private final IBinder mBinder = new IMBinder();
	private String username;
	private String password;
	private String userKey;
	private boolean authenticatedUser = false;
	// timer to take the updated data from server
	private Timer timer;

	private NotificationManager mNM;
	private Thread runner;


	public class IMBinder extends Binder {
		public IAppManager getService() {
			return O9IMService.this;
		}

	}

//	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//			String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//			boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//			NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//			NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//
//			if(runner != null) {
//				try {
//					runner.join(1000);
//					socketOperator.stopListening();
//					runThread();
//					SQLiteDAO dao = SQLiteDAO.getInstance(O9IMService.this, User.class);
//					User me = dao.get(User.class).get(0);
//					signUpUser(me.getUsername(), me.getPassword(), me.getEmail());
//				}
//				catch(Throwable t) {
//					android.util.Log.d(TAG, "Errors on changing connection events", t);
//				}
//				
//			}
//		}
//	}; 

	@Override
	public void onCreate() 
	{   	
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting.  We put an icon in the status bar.
		//   showNotification();
		conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);


		// Timer is used to take the friendList info every UPDATE_TIME_PERIOD;
		timer = new Timer();   

		//
		
		runThread();
		
		//registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	}

	/*
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
	 */	

	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}




	/**
	 * Show a notification while this service is running.
	 * @param msg 
	 **/
	private void showNotification(String username, String msg) 
	{       
		// Set the icon, scrolling text and timestamp
		String title = username + ": " + 
				((msg.length() < 5) ? msg : msg.substring(0, 5)+ "...");
		Notification notification = new Notification(R.drawable.stat_sample, 
				title,
				System.currentTimeMillis());

		Intent i = new Intent(this, Messaging.class);
		i.putExtra(FriendInfo.USERNAME, username);
		i.putExtra(FriendInfo.MESSAGE, msg);	

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				i, 0);

		// Set the info for the views that show in the notification panel.
		// msg.length()>15 ? msg : msg.substring(0, 15);
		notification.setLatestEventInfo(this, "New message from " + username,
				msg, 
				contentIntent);

		//TODO: it can be improved, for instance message coming from same user may be concatenated 
		// next version

		// Send the notification.
		// We use a layout id because it is a unique number.  We use it later to cancel.
		mNM.notify((username+msg).hashCode(), notification);
	}


	public String getUsername() {		
		return username;
	}

	public boolean sendMessage(String  username, String message) {
		FriendInfo friendInfo = FriendController.getFriendInfo(username);
		String IP = friendInfo.ip;
		//IP = "10.0.2.2";
		int port = Integer.parseInt(friendInfo.port);

		String msg = FriendInfo.USERNAME +"=" + URLEncoder.encode(this.username) +
				"&" + FriendInfo.USER_KEY + "=" + URLEncoder.encode(userKey) +
				"&" + FriendInfo.MESSAGE + "=" + URLEncoder.encode(message) +
				"&";

		return socketOperator.sendMessage(msg, IP,  port);
	}


	private String getFriendList() 	{		
		// after authentication, server replies with friendList xml

		rawFriendList = socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		if (rawFriendList != null) {
			this.parseFriendInfo(rawFriendList);
		}
		return rawFriendList;
	}

	/**
	 * authenticateUser: it authenticates the user and if succesful
	 * it returns the friend list or if authentication is failed 
	 * it returns the "0" in string type
	 * */
	public String authenticateUser(String usernameText, String passwordText) 
	{
		this.username = usernameText;
		this.password = passwordText;	

		this.authenticatedUser = false;

		String result = this.getFriendList(); //socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		if (result != null && !result.equals(Login.AUTHENTICATION_FAILED)) 
		{			
			// if user is authenticated then return string from server is not equal to AUTHENTICATION_FAILED
			this.authenticatedUser = true;
			rawFriendList = result;

			Intent i = new Intent(FRIEND_LIST_UPDATED);					
			i.putExtra(FriendInfo.FRIEND_LIST, rawFriendList);
			sendBroadcast(i);

			timer.schedule(new TimerTask()
			{			
				public void run() 
				{
					try {					
						//rawFriendList = IMService.this.getFriendList();
						// sending friend list 
						Intent i = new Intent(FRIEND_LIST_UPDATED);
						String tmp = O9IMService.this.getFriendList();
						if (tmp != null) {
							i.putExtra(FriendInfo.FRIEND_LIST, tmp);
							sendBroadcast(i);	
							Log.i("friend list broadcast sent ", "");
						}
						else {
							Log.i("friend list returned null", "");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}					
				}			
			}, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
		}

		return result;		
	}

	public void messageReceived(String message) 
	{				
		String[] params = message.split("&");
		String username= new String();
		String userKey = new String();
		String msg = new String();
		for (int i = 0; i < params.length; i++) {
			String[] localpar = params[i].split("=");
			if (localpar[0].equals(FriendInfo.USERNAME)) {
				username = URLDecoder.decode(localpar[1]);
			}
			else if (localpar[0].equals(FriendInfo.USER_KEY)) {
				userKey = URLDecoder.decode(localpar[1]);
			}
			else if (localpar[0].equals(FriendInfo.MESSAGE)) {
				msg = URLDecoder.decode(localpar[1]);
			}			
		}
		Log.i("Message received in service", message);

		FriendInfo friend = FriendController.checkFriend(username, userKey);
		if ( friend != null)
		{			
			Intent i = new Intent(TAKE_MESSAGE);

			i.putExtra(FriendInfo.USERNAME, friend.userName);			
			i.putExtra(FriendInfo.MESSAGE, msg);			
			sendBroadcast(i);
			String activeFriend = FriendController.getActiveFriend();
			if (activeFriend == null || activeFriend.equals(username) == false) 
			{
				showNotification(username, msg);
			}
			Log.i("TAKE_MESSAGE broadcast sent by im service", "");
		}	

	}  

	private String getAuthenticateUserParams(String usernameText, String passwordText) 
	{			
		String params = "username=" + URLEncoder.encode(usernameText) +
				"&password="+ URLEncoder.encode(passwordText) +
				"&action="  + URLEncoder.encode("authenticateUser")+
				"&port="    + URLEncoder.encode(Integer.toString(socketOperator.getListeningPort())) +
				"&";		

		return params;		
	}

	public void setUserKey(String value) 
	{
		this.userKey = value;		
	}

	public boolean isNetworkConnected() {
		return conManager.getActiveNetworkInfo().isConnected();
	}

	public boolean isUserAuthenticated(){
		return authenticatedUser;
	}

	public String getLastRawFriendList() {		
		return this.rawFriendList;
	}

	@Override
	public void onDestroy() {
		Log.i("IMService is being destroyed", "...");
		super.onDestroy();
	}

	public void exit() 
	{
		timer.cancel();
		socketOperator.exit(); 
		socketOperator = null;
		this.stopSelf();
	}

	public String signUpUser(String usernameText, String passwordText,String emailText) 
	{
		SQLiteDAO dao = SQLiteDAO.getInstance(O9IMService.this, User.class);
		dao.delete(User.class, "_id=?", "0");
		
		
		
		User user = new User(0, usernameText, passwordText, emailText, false);
		dao.insert(user);
		
		MD5Digest digest = new MD5Digest();
		digest.reset();
		digest.update(passwordText.getBytes(), 0, passwordText.getBytes().length);
		int length = digest.getDigestSize();
        byte[] md5 = new byte[length];
        digest.doFinal(md5, 0);

		
		String params = "username=" + usernameText +
				"&password=" + (new String(md5)) +
				"&action=" + "signUpUser"+
				"&email=" + emailText+
				"&";

		String result = socketOperator.sendHttpRequest(params);	

		return result;
	}

	public String addNewFriendRequest(String friendUsername) 
	{
		String params = "username=" + this.username +
				"&password=" + this.password +
				"&action=" + "addNewFriend" +
				"&friendUserName=" + friendUsername +
				"&";

		String result = socketOperator.sendHttpRequest(params);		

		return result;
	}

	public String sendFriendsReqsResponse(String approvedFriendNames,
			String discardedFriendNames) 
	{
		String params = "username=" + this.username +
				"&password=" + this.password +
				"&action=" + "responseOfFriendReqs"+
				"&approvedFriends=" + approvedFriendNames +
				"&discardedFriends=" +discardedFriendNames +
				"&";

		String result = socketOperator.sendHttpRequest(params);		

		return result;

	} 

	private void parseFriendInfo(String xml)
	{			
		try 
		{
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(O9IMService.this));		
		} 
		catch (ParserConfigurationException e) {			
			e.printStackTrace();
		}
		catch (SAXException e) {			
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}	
	}

	public void updateData(FriendInfo[] friends,
			FriendInfo[] unApprovedFriends, String userKey) 
	{
		this.setUserKey(userKey);
		//FriendController.		
		FriendController.setFriendsInfo(friends);
		FriendController.setUnapprovedFriendsInfo(unApprovedFriends);

	}
	
	private void runThread() {
		runner = new Thread()
		{
			@Override
			public void run() {			

				//socketOperator.startListening(LISTENING_PORT_NO);
				Random random = new Random();
				int tryCount = 0;
				while (socketOperator.startListening(10000 + random.nextInt(20000))  == 0 )
				{		
					tryCount++; 
					if (tryCount > 10)
					{
						// if it can't listen a port after trying 10 times, give up...
						break;
					}

				}
			}
		};		
		runner.start();
	}
}
