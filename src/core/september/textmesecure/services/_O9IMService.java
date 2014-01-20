package core.september.textmesecure.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

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
import core.september.textmesecure.interfaces.ServiceCallback;
import core.september.textmesecure.sql.models.EnquedMessage;
import core.september.textmesecure.sql.models.User;
import core.september.textmesecure.sql.models.User.SubscriptionType;

public class _O9IMService  { /*
	//	private NotificationManager mNM;

	public static final String TAG = O9IMService.class.getName();
	public static final String TAKE_MESSAGE = "Take_Message";
	public static final String FRIEND_LIST_UPDATED = "Take Friend List";
	public ConnectivityManager conManager = null; 
	private boolean ret = false;
	private final IBinder mBinder = new IMBinder();
	private static QBUser user;
	private static O9ChatController _controller;
	private Handler handler;
	private LinkedList<QBUser> rosterEntries;
	private Presence presence;

	public class IMBinder extends Binder {
		public O9IMService getService() {
			return O9IMService.this;
		}

	}
	
	public enum MessageDirection {
		SENT,
		RECEIVED
	}
	
	

	
	 public Presence getPresence() {
		return presence;
	}
	public void setPresence(Presence presence) {
		this.presence = presence;
	}
	public LinkedList<QBUser> getRosterEntries() {
		 if(rosterEntries == null) {
			 setRosterEntries(new LinkedList<QBUser>());
		 }
		return rosterEntries;
	}
	public void setRosterEntries(LinkedList<QBUser> rosterEntries) {
		this.rosterEntries = rosterEntries;
	}

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
					final String processedMessageString = O9KeyController.getInstance(O9IMService.this).processAcceptMessage(o9message,myNewKey);
					getController(new ServiceCallback() {
						
						@Override
						public void onComplete() {
							_controller.sendMessage(processedMessageString);
							
						}
					});
					break;
				case KEY_ACCEPT:
					String myKey = o9message.getReceiverPublicKey();
					O9KeyController.getInstance(O9IMService.this).updateKeyPair(myKey,friendKey,message.getFrom());
					for(EnquedMessage enquedMessage: getEnquedMessage(_controller.getActualFriendLogin())) {
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
	
	private void getController(ServiceCallback callback) {
	    	if(_controller == null) {
	    		setUpController(callback);
	    	}
	    	else {
	    		if(callback!= null)
	    		callback.onComplete();
	    	}
	    }
	@Override
	public void onCreate() 
	{   	
		setUpController(null);

	}

	/*
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
	 

	@Override
	public IBinder onBind(Intent intent) 
	{
		setUpController(null);
		return mBinder;
	}
	
	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    return Service.START_STICKY;
	  }




	/**
	 * Show a notification while this service is running.
	 * @param msg 
	 *
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
	
	private User getUser() {
		SQLiteDAO dao = SQLiteDAO.getInstance(O9IMService.this, User.class);
		List<User> userList = dao.get(User.class);
		return userList != null && userList.size() > 0 ? userList.get(0) : null;
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
	            //setUpController();
	            //startActivity(intent);
	            Intent intent2 = new Intent();
	    		   intent2.setAction(Config.LOGIN_SUCCESS);
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
	
	
	//@Override
	public void getFriendList() {
		
		final ArrayList<QBUser> listresult = new ArrayList<QBUser>();
		//SQLiteDAO dao = SQLiteDAO.getInstance(O9IMService.this, FriendId.class);
		//List<FriendId> friendsids = dao.get(FriendId.class);
		
		 getController(new ServiceCallback() {
			
			@Override
			public void onComplete() {
				Collection<RosterEntry> rosterEntries = _controller.getRoster().getEntries();
				LinkedList<String> list = new LinkedList<String>();
				for(RosterEntry entry: rosterEntries) {
					list.add(entry.getUser());
				}
				QBUsers.getUsersByIDs(list, new QBCallback() {
					
					@Override
					public void onComplete(Result result) {
						
						if(result.isSuccess()) {
							QBUserPagedResult pagedResult = (QBUserPagedResult) result;
							getRosterEntries().addAll(pagedResult.getUsers());
							Intent intent = new Intent();
				    		   intent.setAction(Config.USER_RETRIVED);
				    		   sendBroadcast(intent);
							
						}
						
						else {
							handleErrors(result);
						}
						
					}
					
					@Override
					public void onComplete(Result result, Object arg1) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		
	}

//	@Override
//	private void setUpController(String user, String password) throws XMPPException {
//		if(_controller == null) {
//			_controller = new O9ChatController(user, password);
//			_controller.setOnMessageReceivedListener(onMessageReceivedListener);
//		}
//		
//	}
	
	protected void setConnection(XMPPConnection connection) {
	    //this.connection = connection;
		_controller.setConnection(connection);
	    if (connection != null) {
	      // Add a packet listener to get messages sent to us
	      PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
	      connection.addPacketListener(new PacketListener() {
	        @Override
	        public void processPacket(Packet packet) {
	          Message message = (Message) packet;
	          if (message.getBody() != null) {
	            String fromName = StringUtils.parseBareAddress(message.getFrom());
	            Log.i("XMPPChatActivity ", " Text Recieved " + message.getBody() + " from " +  fromName);

	            //messages.add(message.getBody());


//	            mHandler.post(new Runnable() {
//	              public void run() {
//	                setListAdapter();
//	              }
//	            });
	          }
	        }


	      }, filter);
	    }
	  }
	
	
//	public void connect() {
//
//	    //final ProgressDialog dialog = ProgressDialog.show(this, "Connecting...", "Please wait...", false);
////	    Thread t = new Thread(new Runnable() {
////	      @Override
////	      public void run() {
//	        // Create a connection
//	       ConnectionConfiguration connConfig = new ConnectionConfiguration(O9ChatController.CHAT_SERVER);
//	       XMPPConnection connection = new XMPPConnection(connConfig);
//	         try {
//	           connection.connect();
//	           Log.i("XMPPChatActivity",  "[SettingsDialog] Connected to "+connection.getHost());
//	         } catch (XMPPException ex) {
//	             Log.e("XMPPChatActivity",  "[SettingsDialog] Failed to connect to "+ connection.getHost());
//	             Log.e("XMPPChatActivity", ex.toString());
//	             setConnection(null);
//	         }
//	          try {
//	            connection.login(USERNAME, PASSWORD);
//	            Log.i("XMPPChatActivity",  "Logged in as" + connection.getUser());
//
//	            // Set the status to available
//	            Presence presence = new Presence(Presence.Type.available);
//	            connection.sendPacket(presence);
//	            setConnection(connection);
//
//	            Roster roster = connection.getRoster();
//	            Collection<RosterEntry> entries = roster.getEntries();
//	            for (RosterEntry entry : entries) {
//
//	              Log.d("XMPPChatActivity",  "--------------------------------------");
//	              Log.d("XMPPChatActivity", "RosterEntry " + entry);
//	              Log.d("XMPPChatActivity", "User: " + entry.getUser());
//	              Log.d("XMPPChatActivity", "Name: " + entry.getName());
//	              Log.d("XMPPChatActivity", "Status: " + entry.getStatus());
//	              Log.d("XMPPChatActivity", "Type: " + entry.getType());
//	              Presence entryPresence = roster.getPresence(entry.getUser());
//
//	              Log.d("XMPPChatActivity", "Presence Status: "+ entryPresence.getStatus());
//	              Log.d("XMPPChatActivity", "Presence Type: " + entryPresence.getType());
//
//	              Presence.Type type = entryPresence.getType();
//	              if (type == Presence.Type.available)
//	                Log.d("XMPPChatActivity", "Presence AVIALABLE");
//	                Log.d("XMPPChatActivity", "Presence : " + entryPresence);
//	              }
//	              } catch (XMPPException ex) {
//	                Log.e("XMPPChatActivity", "Failed to log in as "+  USERNAME);
//	                Log.e("XMPPChatActivity", ex.toString());
//	                setConnection(null);
//	              }
//	           }
////	      });
////	    t.start();
////	    dialog.show();
//	  }

	private QBUser getLoggedUser() {
		return user;
	}
	
	private class LoginTask extends AsyncTask<User,Result,User> {
		public LoginTask(ServiceCallback callback) {
			super();
			this.callback = callback;
		}
		ServiceCallback callback;
		//XMPPConnection connection = _controller.getConnection();
		 ConnectionConfiguration connConfig = new ConnectionConfiguration(O9ChatController.CHAT_SERVER);
      	XMPPConnection connection = new XMPPConnection(connConfig);
		@Override
	     protected User doInBackground(User... users) {
	         try {
	        	 	SASLAuthentication.supportSASLMechanism("PLAIN", 0);
	  	       		connection.connect();
	  	       		//_controller.setConnection(connection);
	        	 
	        				
	         }
	         
	         catch  (XMPPException ex) {
	             Log.e("XMPPChatActivity",  "[SettingsDialog] Failed to connect to "+ _controller.getConnection().getHost());
	             Log.e("XMPPChatActivity", ex.toString());
	             O9IMService.this.setConnection(null);
				 android.util.Log.e(TAG,ex.getMessage(),ex);
				}
	         
	         return users[0];
	     }

		@Override
	     protected void onPostExecute(User user) {
			
			QBUsers.getUserByLogin(user.getUsername(), new QBCallback() {

				@Override
				public void onComplete(Result result) {
					 if (result != null && result.isSuccess()) { 
						 //XMPPConnection connection = _controller.getConnection();
						 QBUserResult userResult = (QBUserResult) result;
							QBUser resuser = userResult.getUser();
							User me = getUser();
							String realLogin = QBChat.getChatLoginShort(resuser);
							try {
								//connection.login(realLogin, user.getPassword());
								connection.login(realLogin, me.getPassword());
					            Log.i("XMPPChatActivity",  "Logged in as" + connection.getUser());

					            // Set the status to available
					            Presence presence = new Presence(Presence.Type.available);
					            connection.sendPacket(presence);
					            O9IMService.this.setConnection(connection);

					            Roster roster = connection.getRoster();
					            Collection<RosterEntry> entries = roster.getEntries();
					            for (RosterEntry entry : entries) {

					              Log.d("XMPPChatActivity",  "--------------------------------------");
					              Log.d("XMPPChatActivity", "RosterEntry " + entry);
					              Log.d("XMPPChatActivity", "User: " + entry.getUser());
					              Log.d("XMPPChatActivity", "Name: " + entry.getName());
					              Log.d("XMPPChatActivity", "Status: " + entry.getStatus());
					              Log.d("XMPPChatActivity", "Type: " + entry.getType());
					              Presence entryPresence = roster.getPresence(entry.getUser());

					              Log.d("XMPPChatActivity", "Presence Status: "+ entryPresence.getStatus());
					              Log.d("XMPPChatActivity", "Presence Type: " + entryPresence.getType());

					              Presence.Type type = entryPresence.getType();
					              if (type == Presence.Type.available)
					                Log.d("XMPPChatActivity", "Presence AVIALABLE");
					                Log.d("XMPPChatActivity", "Presence : " + entryPresence);
					              }
							}
					            catch (XMPPException ex) {
					            	_controller = null;
					                Log.e("XMPPChatActivity", "Failed to log in as "+  realLogin);
					                Log.e("XMPPChatActivity", ex.toString());
					                setConnection(null);
					              }
					 }
					
				}

				@Override
				public void onComplete(Result arg0, Object arg1) {
					if(callback!= null) {
						callback.onComplete();
					}
					
				} 
				
			});
			
		}
		
	}


	
	private void _setUpController(ServiceCallback callback) {
		if(_controller == null) {
			SQLiteDAO dao = SQLiteDAO.getInstance(this, User.class);
	        List<User> list = dao.get(User.class);
	        
	        if(list != null && list.size() > 0) {
	        	final User user = list.get(0);
	        	if(user.getPassword() != null && user.getPassword().trim().length() > 0) {
	        		try {
	        				_controller = new O9ChatController(user.getUsername(), user.getPassword());
	        				_controller.setOnMessageReceivedListener(onMessageReceivedListener);
	        				//_controller.setUpConnection();
	        				
	        				
//	        				
	        				(new LoginTask(callback)).execute(user);
	        		}
	        				
	        		catch (Exception e){
	        			android.util.Log.e(TAG, e.getMessage(), e);
	        		}
	        	}
	        }
		}
		
		else if(callback!= null) {
			callback.onComplete();
		}
		
		
		
	}

	//@Override
	public void startChat(final String friendLogin) {
		getController(new ServiceCallback() {
			
			@Override
			public void onComplete() {
				_controller.startChat(friendLogin);
				
			}
		});
	}

	//@Override
	public void sendMessage(String messageString) {
		String friendKey = getFriendKeyFromLocalDB(_controller.getActualFriendLogin());
		if(friendKey != null) {
			String processedMessageString = O9KeyController.getInstance(this).processTextMessage(messageString,_controller.getActualFriendLogin());
			_controller.sendMessage(processedMessageString);
			O9KeyController.getInstance(this).decreaseTTL(_controller.getActualFriendLogin());
		}
		
		else {
			enqueMessage(messageString, _controller.getActualFriendLogin());
			String newPublicKeyRepo = O9KeyController.getInstance(this).generatePublicKey(_controller.getActualFriendLogin(),null);
			String processedMessageString = O9KeyController.getInstance(this).processExchangeMessage(newPublicKeyRepo);
			_controller.sendMessage(processedMessageString);
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

	//@Override
	public void getPresence(final String user) {
		getController(new ServiceCallback() {
			
			@Override
			public void onComplete() {
				setPresence(_controller.getRoster().getPresence(user));
				Intent intent = new Intent();
	    		   intent.setAction(Config.PRESENCE_RETRIVED);
	    		   sendBroadcast(intent);
				
			}
		});
	}

	//@Override
	public void addFriend(final String login) throws XMPPException {
		
		getController(new ServiceCallback() {
			
			@Override
			public void onComplete() {
				try {
					_controller.getRoster().createEntry(login, login, new String[]{"friends"});
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					android.util.Log.e(TAG,e.getMessage(),e);
				}
				
			}
		});
		
	}
	

	
   */

}
