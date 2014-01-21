package core.september.textmesecure.services;

import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.packet.Message;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
//import android.os.Message;
import android.util.Log;

import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.module.chat.QBChat;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;

import core.september.textmesecure.configs.Messages;
import core.september.textmesecure.configs.Route;
import core.september.textmesecure.helpers.DBHelper;
import core.september.textmesecure.interfaces.O9LooperService;
import core.september.textmesecure.interfaces.OnComplete;
import core.september.textmesecure.sql.models.User;

public class O9IMService extends Service implements O9LooperService{
	
	private final String TAG = O9IMService.class.getSimpleName();
	private final IBinder mBinder = new IMBinder();
	private final O9Looper mLooper = new O9Looper(this);
	private User user = null;
	private QBUser qbUser = null;
	private Route route;
	private boolean signedIn = false;
	private XMPPConnection connection;
	
	public class IMBinder extends Binder {
		public O9IMService getService() {
			return O9IMService.this;
		}

	}
	
	private QBCallback callback = new QBCallback() {
		
		@Override
		public void onComplete(Result result, Object arg1) {
			if (result.isSuccess()) { 
				signedIn = true;
				postMessage(Messages.QBUSERSIGNED);
			}
			//sessionCreated = result.isSuccess();
			
		}
		
		@Override
		public void onComplete(Result result) {
			onComplete(result,null);
			
		}
	};
	
	private void postMessage(int message) {
		android.os.Message msg = new android.os.Message();
        msg.what = message;
        mLooper.postMessage(msg);
	}
	
	public Route getRoute() {
		return route;
	}
	
	@Override
    public void onCreate() {
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        //showNotification();
		mLooper.start();
		//SQLiteDAO dao = SQLiteDAO.getInstance(this, User.class);
        List<User> list = DBHelper.getInstance(this).getUserList();
        
        int msgType = Messages.SIGNUP;
        
        if(list != null && list.size() > 0) {
        	user = list.get(0);
        	if(user.getPassword() != null && user.getPassword().trim().length() > 0) {
        		route = Route.READY_TO_START;
        		 msgType = Messages.READY;
        	}
        	else {
        		route = Route.NEED_SIGNIN;
        		msgType = Messages.SIGNIN;
        	}
        }
        
        else {
        	route = Route.NEED_SIGNUP;
        }
        

        
        postMessage(msgType);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        //mNM.cancel(NOTIFICATION);
    	try {
			mLooper.join();
		} catch (InterruptedException e) {
			android.util.Log.e(TAG,e.getMessage(),e);
		}
        // Tell the user we stopped.
        //Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	////////////////////// Service Looper
	@Override
	public User getUser() {
		return user;
	}
	
	@Override
	public void signUpUser(String usernameText, String passwordText,String emailText) 
	{
		//storeUser(usernameText, passwordText, emailText);
		
		qbUser = new QBUser(usernameText, passwordText, emailText);
		QBUsers.signUpSignInTask(qbUser, callback);
		
	}
	
	@Override
	public void signInUser(String usernameText, String passwordText) 
	{
		//storeUser(usernameText, passwordText, null);
		
		qbUser = new QBUser(usernameText, passwordText);
		QBUsers.signIn(qbUser, callback);
	}
	
	public void connect() {
		if(connection == null) {
			_connect(new OnComplete() {
				
				@Override
				public void complete() {
					sendBroadcast(new Intent(Messages.CONNECTION_DONE));					
				}
			});
		}
	}
	private void _connect(final OnComplete onComplete) {

	    //final ProgressDialog dialog = ProgressDialog.show(this, "Connecting...", "Please wait...", false);
	    Thread t = new Thread(new Runnable() {
	      @Override
	      public void run() {
	        // Create a connection
	       //ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
	    	  ConnectionConfiguration connConfig = new ConnectionConfiguration(QBChat.getChatServerDomain());
	       XMPPConnection connection = new XMPPConnection(connConfig);
	         try {
	           connection.connect();
	           Log.i("XMPPChatDemoActivity",  "[SettingsDialog] Connected to "+connection.getHost());
	         } catch (XMPPException ex) {
	             Log.e("XMPPChatDemoActivity",  "[SettingsDialog] Failed to connect to "+ connection.getHost());
	             Log.e("XMPPChatDemoActivity", ex.toString());
	             setConnection(null);
	         }
	          try {
	            connection.login(qbUser.getLogin(), qbUser.getPassword());
	            Log.i("XMPPChatDemoActivity",  "Logged in as" + connection.getUser());

	            // Set the status to available
	            Presence presence = new Presence(Presence.Type.available);
	            connection.sendPacket(presence);
	            setConnection(connection);

	            Roster roster = connection.getRoster();
	            Collection<RosterEntry> entries = roster.getEntries();
	            for (RosterEntry entry : entries) {

	              Log.d("XMPPChatDemoActivity",  "--------------------------------------");
	              Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
	              Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
	              Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
	              Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
	              Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
	              Presence entryPresence = roster.getPresence(entry.getUser());

	              Log.d("XMPPChatDemoActivity", "Presence Status: "+ entryPresence.getStatus());
	              Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());

	              Presence.Type type = entryPresence.getType();
	              if (type == Presence.Type.available)
	                Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
	                Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);
	                
	              }
	              } catch (XMPPException ex) {
	                Log.e("XMPPChatDemoActivity", "Failed to log in as "+  qbUser.getLogin());
	                Log.e("XMPPChatDemoActivity", ex.toString());
	                setConnection(null);
	              }
	              //dialog.dismiss();
	           
	          if(onComplete != null) {
	        	  onComplete.complete();
	          }
	      	}
	      });
	    t.start();
	    //dialog.show();
	  }
	
	public void setConnection(XMPPConnection connection) {
	    this.connection = connection;
	    if (connection != null) {
	      // Add a packet listener to get messages sent to us
	      PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
	      connection.addPacketListener(new PacketListener() {
	        @Override
	        public void processPacket(Packet packet) {
	          Message message = (Message) packet;
	          if (message.getBody() != null) {
	            String fromName = StringUtils.parseBareAddress(message.getFrom());
	            Log.i("XMPPChatDemoActivity ", " Text Recieved " + message.getBody() + " from " +  fromName);
//	            messages.add(fromName + ":");
//	            messages.add(message.getBody());
	            // Add the incoming message to the list view
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

}
