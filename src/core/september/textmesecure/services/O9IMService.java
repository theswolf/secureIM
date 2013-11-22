package core.september.textmesecure.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.niusounds.asd.SQLiteDAO;
import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.module.chat.QBChat;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserPagedResult;
import com.quickblox.module.users.result.QBUserResult;

import core.september.textmesecure.UsersListActivity;
import core.september.textmesecure.algo.O9Message;
import core.september.textmesecure.configs.Config;
import core.september.textmesecure.fragments.UserListFragment;
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
	private static O9ChatController _controller;
	private Handler handler;

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
					getController().sendMessage(processedMessageString);
					break;
				case KEY_ACCEPT:
					String myKey = o9message.getReceiverPublicKey();
					O9KeyController.getInstance(O9IMService.this).updateKeyPair(myKey,friendKey,message.getFrom());
					for(EnquedMessage enquedMessage: getEnquedMessage(getController().getActualFriendLogin())) {
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
	        
	       @Override
	       public void onMessageReceived(String status) {
	    	   if(status.equalsIgnoreCase("LOGGED")) {
	    		   Intent intent = new Intent();
	    		   intent.setAction("core.september.textmesecure.fragments.UserListFragment.loggedInReceiver");
	    		   sendBroadcast(intent);
	    	   }
	       }
	    };
	
	private O9ChatController getController() {
	    	if(_controller == null) {
	    		setUpController();
	    	}
	    	return _controller;
	    }
	@Override
	public void onCreate() 
	{   	
		setUpController();

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
		setUpController();
		return mBinder;
	}
	
	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    return Service.START_STICKY;
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


	private void storeUser(String usernameText, String passwordText,String emailText)  {
		SQLiteDAO dao = SQLiteDAO.getInstance(O9IMService.this, User.class);
		dao.delete(User.class, "_id=?", "0");
		dao.insert(new User(0, usernameText, passwordText, emailText, SubscriptionType.BASIC));
	}

	public void signUpUser(String usernameText, String passwordText,String emailText) 
	{
		//storeUser(usernameText, passwordText, emailText);
		
		user = new QBUser(usernameText, passwordText, emailText);
		QBUsers.signUpSignInTask(user, O9IMService.this);
		
	}
	
	public void signInUser(String usernameText, String passwordText) 
	{
		//storeUser(usernameText, passwordText, null);
		
		user = new QBUser(usernameText, passwordText);
		QBUsers.signIn(user, O9IMService.this);
	}



	@Override
	public void onComplete(Result result) {
		 if (result.isSuccess()) {
	            final Intent intent = new Intent(this, UsersListActivity.class);
	            intent.putExtra(Config.MY_ID, user.getId());
	            intent.putExtra(Config.MY_LOGIN, user.getLogin());
	            intent.putExtra(Config.MY_PASSWORD, user.getPassword());
	            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            //setUpController();
	            storeUser(user.getLogin(), user.getPassword(), user.getEmail());
	            setUpController();
	            startActivity(intent);
	            Intent intent2 = new Intent();
	    		   intent2.setAction(UserListFragment.class.getName());
	    		   sendBroadcast(intent2);

	        } else {
	        	handleErrors(result);
	        }
		
	}
	
	private void handleErrors(Result result) {
		if(result.getErrors() != null && result.getErrors().size() > 0) {
       	for(String error: result.getErrors()) {
       		android.util.Log.d(TAG,error);
       	}
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
		Collection<RosterEntry> rosterEntries = getController().getRoster().getEntries();
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
					handleErrors(result);
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

//	@Override
//	private void setUpController(String user, String password) throws XMPPException {
//		if(_controller == null) {
//			_controller = new O9ChatController(user, password);
//			_controller.setOnMessageReceivedListener(onMessageReceivedListener);
//		}
//		
//	}
	


	
	private class LoginTask extends AsyncTask<User,Result,User> {
		XMPPConnection connection = _controller.getConnection();
		@Override
	     protected User doInBackground(User... users) {
	         try {
	        	 connection.connect();
	        	 
	        				
	         }
	         
	         catch (Exception e) {
					// TODO Auto-generated catch block
					android.util.Log.e(TAG,e.getMessage(),e);
				}
	         
	         return users[0];
	     }

		@Override
	     protected void onPostExecute(User user) {
			
			QBUsers.getUserByLogin(user.getUsername(), new QBCallback() {

				@Override
				public void onComplete(Result result, Object arg1) {
					

				}

				@Override
				public void onComplete(Result result) { 
					 if (result != null && result.isSuccess()) {
							QBUserResult userResult = (QBUserResult) result;
							QBUser user = userResult.getUser();
							String realLogin = QBChat.getChatLoginShort(user);
							try {
								try {
									connection.login(realLogin, user.getPassword());
								}
								catch (Throwable e) {
									android.util.Log.d(TAG,e.getMessage(),e);
								}
								//connection.login(realLogin, user.getPassword());
//								Intent intent = new Intent();
//					    		   intent.setAction(UserListFragment.class.getName());
//					    		   sendBroadcast(intent);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								android.util.Log.e(TAG,e.getMessage(),e);
							}

						}
				}
			});
			
	    	
	     }

	 }
	
	private void setUpController() {
		if(_controller == null) {
			SQLiteDAO dao = SQLiteDAO.getInstance(this, User.class);
	        List<User> list = dao.get(User.class);
	        
	        if(list != null && list.size() > 0) {
	        	final User user = list.get(0);
	        	if(user.getPassword() != null && user.getPassword().trim().length() > 0) {
	        		try {
	        				_controller = new O9ChatController(user.getUsername(), user.getPassword());
	        				_controller.setOnMessageReceivedListener(onMessageReceivedListener);
	        				_controller.setUpConnection();
	        				
	        				
//	        				
	        				(new LoginTask()).execute(user);
	        		}
	        				
	        		catch (Exception e){
	        			android.util.Log.e(TAG, e.getMessage(), e);
	        		}
	        	}
	        }
		}
		
		
		
	}

	@Override
	public void startChat(String friendLogin) {
		getController().startChat(friendLogin);
	}

	@Override
	public void sendMessage(String messageString) {
		String friendKey = getFriendKeyFromLocalDB(getController().getActualFriendLogin());
		if(friendKey != null) {
			String processedMessageString = O9KeyController.getInstance(this).processTextMessage(messageString,getController().getActualFriendLogin());
			getController().sendMessage(processedMessageString);
			O9KeyController.getInstance(this).decreaseTTL(getController().getActualFriendLogin());
		}
		
		else {
			enqueMessage(messageString, getController().getActualFriendLogin());
			String newPublicKeyRepo = O9KeyController.getInstance(this).generatePublicKey(getController().getActualFriendLogin(),null);
			String processedMessageString = O9KeyController.getInstance(this).processExchangeMessage(newPublicKeyRepo);
			getController().sendMessage(processedMessageString);
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
		return getController().getRoster().getPresence(user);
	}

	@Override
	public void addFriend(String login) throws XMPPException {
		getController().getRoster().createEntry(login, login, new String[]{"friends"});
		
	}
	


	
   

}
