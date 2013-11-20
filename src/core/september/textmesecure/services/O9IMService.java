package core.september.textmesecure.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.niusounds.asd.SQLiteDAO;
import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserPagedResult;

import core.september.textmesecure.UsersListActivity;
import core.september.textmesecure.algo.O9Message;
import core.september.textmesecure.configs.Config;
import core.september.textmesecure.interfaces.IAppManager;
import core.september.textmesecure.sql.models.EnquedMessage;
import core.september.textmesecure.sql.models.User;
import core.september.textmesecure.sql.models.User.SubscriptionType;

public class O9IMService extends Service implements IAppManager, QBCallback {
	//	private NotificationManager mNM;

	public static final String TAG = O9IMService.class.getName();
	public static final String TAKE_MESSAGE = "Take_Message";
	public static final String FRIEND_LIST_UPDATED = "Take Friend List";
	public ConnectivityManager conManager = null; 
	private boolean ret = false;
	private final IBinder mBinder = new IMBinder();
	private QBUser user;
	private O9ChatController controller;

	public class IMBinder extends Binder {
		public IAppManager getService() {
			return O9IMService.this;
		}

	}
	
	public enum MessageDirection {
		SENT,
		RECEIVED
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

	
	 private O9ChatController.OnMessageReceivedListener onMessageReceivedListener = new O9ChatController.OnMessageReceivedListener() {
	        @Override
	        public void onMessageReceived(final Message message) {
	            String messageString = message.getBody();
	            O9Message o9message = O9Message.fromString(messageString);
	            O9Message.Type type = o9message.getType();
	            String friendKey = o9message.getSenderPublicKey();
	            switch (type) {
				case KEY_EXCHANGE:
					String myNewKey = O9KeyController.getInstance(O9IMService.this).generatePublicKey(message.getFrom(), friendKey);
					String processedMessageString = O9KeyController.getInstance(O9IMService.this).processAcceptMessage(o9message,myNewKey);
					controller.sendMessage(processedMessageString);
					break;
				case KEY_ACCEPT:
					String myKey = o9message.getReceiverPublicKey();
					O9KeyController.getInstance(O9IMService.this).updateKeyPair(myKey,friendKey,message.getFrom());
					for(EnquedMessage enquedMessage: getEnquedMessage(controller.getActualFriendLogin())) {
						deleteEnquedMessage(enquedMessage);
						sendMessage(enquedMessage.getMessage());
					}
					break;
				
				case MESSAGE:
					
					break;
				default:
					break;
				
	            }
	            //showMessage(messageString, false);
	        }
	    };
	
	@Override
	public void onCreate() 
	{   	
		//mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting.  We put an icon in the status bar.
		//   showNotification();
		conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		SQLiteDAO dao = SQLiteDAO.getInstance(this, User.class);
        List<User> list = dao.get(User.class);
        
        if(list != null && list.size() > 0) {
        	User user = list.get(0);
        	if(user.getPassword() != null && user.getPassword().trim().length() > 0) {
        		try {
        			setUpController(user.getUsername(), user.getPassword());
        		}
        		catch (Exception e){
        			android.util.Log.d(TAG, e.getMessage(), e);
        		}
        		
        	}
        }

		// Timer is used to take the friendList info every UPDATE_TIME_PERIOD;
		//timer = new Timer();   

		//
		
		
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
//	private void showNotification(String username, String msg) 
//	{       
//		// Set the icon, scrolling text and timestamp
//		String title = username + ": " + 
//				((msg.length() < 5) ? msg : msg.substring(0, 5)+ "...");
//		Notification notification = new Notification(R.drawable.stat_sample, 
//				title,
//				System.currentTimeMillis());
//
//		Intent i = new Intent(this, Messaging.class);
//		i.putExtra(FriendInfo.USERNAME, username);
//		i.putExtra(FriendInfo.MESSAGE, msg);	
//
//		// The PendingIntent to launch our activity if the user selects this notification
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//				i, 0);
//
//		// Set the info for the views that show in the notification panel.
//		// msg.length()>15 ? msg : msg.substring(0, 15);
//		notification.setLatestEventInfo(this, "New message from " + username,
//				msg, 
//				contentIntent);
//
//		//TODO: it can be improved, for instance message coming from same user may be concatenated 
//		// next version
//
//		// Send the notification.
//		// We use a layout id because it is a unique number.  We use it later to cancel.
//		mNM.notify((username+msg).hashCode(), notification);
//	}


	

	public void signUpUser(String usernameText, String passwordText,String emailText) 
	{
		SQLiteDAO dao = SQLiteDAO.getInstance(O9IMService.this, User.class);
		dao.delete(User.class, "_id=?", "0");
		dao.insert(new User(0, usernameText, passwordText, emailText, SubscriptionType.BASIC));
		
		user = new QBUser(usernameText, passwordText, emailText);
		QBUsers.signUpSignInTask(user, O9IMService.this);
		
	}
	
	public void signInUser(String usernameText, String passwordText) 
	{

		user = new QBUser(usernameText, passwordText);
		QBUsers.signIn(user, O9IMService.this);
	}



	@Override
	public void onComplete(Result result) {
		 if (result.isSuccess()) {
	            Intent intent = new Intent(this, UsersListActivity.class);
	            intent.putExtra(Config.MY_ID, user.getId());
	            intent.putExtra(Config.MY_LOGIN, user.getLogin());
	            intent.putExtra(Config.MY_PASSWORD, user.getPassword());

	            startActivity(intent);
	            Toast.makeText(this, "You've been successfully logged in application",
	                    Toast.LENGTH_SHORT).show();
	        } else {
	            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	            dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
	                    "please. Errors: " + result.getErrors()).create().show();
	        }
		
	}

	@Override
	public void onComplete(Result arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public List<QBUser> getFriendList() {
		
		final ArrayList<QBUser> listresult = new ArrayList<QBUser>();
		//SQLiteDAO dao = SQLiteDAO.getInstance(O9IMService.this, FriendId.class);
		//List<FriendId> friendsids = dao.get(FriendId.class);
		LinkedList<String> list = new LinkedList<String>();
		Collection<RosterEntry> rosterEntries = controller.getRoster().getEntries();
		for(RosterEntry entry: rosterEntries) {
			list.add(entry.getUser());
		}
		QBUsers.getUsersByIDs(list, new QBCallback() {
			
			@Override
			public void onComplete(Result result) {
				
				if(result.isSuccess()) {
					QBUserPagedResult pagedResult = (QBUserPagedResult) result;
					listresult.addAll(pagedResult.getUsers());
					
				}
				
				else {
					 AlertDialog.Builder dialog = new AlertDialog.Builder(O9IMService.this);
			            dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
			                    "please. Errors: " + result.getErrors()).create().show();
				}
				ret = true;
				
			}
			
			@Override
			public void onComplete(Result result, Object arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
		while (!ret) {}
		ret = false;
		return listresult;
		
		
	}

	@Override
	public void setUpController(String user, String password) throws XMPPException {
		if(controller != null) {
			controller = new O9ChatController(user, password);
			controller.setOnMessageReceivedListener(onMessageReceivedListener);
		}
		
	}

	@Override
	public void startChat(String friendLogin) {
		controller.startChat(friendLogin);
	}

	@Override
	public void sendMessage(String messageString) {
		String friendKey = getFriendKeyFromLocalDB(controller.getActualFriendLogin());
		if(friendKey != null) {
			String processedMessageString = O9KeyController.getInstance(this).processTextMessage(messageString,controller.getActualFriendLogin());
			controller.sendMessage(processedMessageString);
			O9KeyController.getInstance(this).decreaseTTL(controller.getActualFriendLogin());
		}
		
		else {
			enqueMessage(messageString, controller.getActualFriendLogin());
			String newPublicKeyRepo = O9KeyController.getInstance(this).generatePublicKey(controller.getActualFriendLogin(),null);
			String processedMessageString = O9KeyController.getInstance(this).processExchangeMessage(newPublicKeyRepo);
			controller.sendMessage(processedMessageString);
		}
		
		
	}
	
	private String getFriendKeyFromLocalDB(String friend) {
		return O9KeyController.getInstance(this).getByFriendLogin(friend).getFriendKey();
	}
	
	private void enqueMessage(String message, String friendLogin) {
		SQLiteDAO enqueer = SQLiteDAO.getInstance(this, EnquedMessage.class);
		EnquedMessage enquedMessage = new EnquedMessage(System.currentTimeMillis(), message, friendLogin);
		enqueer.insert(enquedMessage);
	}
	
	private List<EnquedMessage> getEnquedMessage(String friendLogin) {
		SQLiteDAO enqueer = SQLiteDAO.getInstance(this, EnquedMessage.class);
		return enqueer.get(EnquedMessage.class, "to=?", new String[]{friendLogin}, "timestamp");
		
	}
	
	private void deleteEnquedMessage(EnquedMessage message) {
		SQLiteDAO enqueer = SQLiteDAO.getInstance(this, EnquedMessage.class);
		enqueer.delete(EnquedMessage.class, "_id=", message.get_id());
	}

	@Override
	public Presence getPresence(String user) {
		return controller.getRoster().getPresence(user);
	}

	@Override
	public void addFriend(String login) throws XMPPException {
		controller.getRoster().createEntry(login, login, new String[]{"friends"});
		
	}
	


	
   

}
