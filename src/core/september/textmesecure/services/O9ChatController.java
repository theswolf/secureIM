package core.september.textmesecure.services;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.os.AsyncTask;

import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.module.chat.QBChat;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserPagedResult;

/**
 * Date: 24.10.12
 * Time: 22:16
 */

/**
 * This class implements simple chat feature. Two users can send messages to
 * each other. First user (you) should be logged in mobile device, but other one
 * can use any mobile or desktop XMPP chat client, e.g. Adium, iChat, etc.
 * <p/>
 * All chat logic can be implemented by yourself using ASMACK library
 * (https://github.com/Flowdalic/asmack/downloads) that is Android wrapper for
 * Java XMPP library (http://www.igniterealtime.org/projects/smack/).
 * <p/>
 * All specific documentation about SMACK you can find in
 * http://www.igniterealtime.org/builds/smack/docs/latest/documentation/
 * 
 * @author <a href="mailto:oleg@quickblox.com">Oleg Soroka</a>
 */
public class O9ChatController {

	// ================= QuickBlox ===== Step 8 =================
	// Get QuickBlox chat server domain.
	// There will be created connection with chat server below.
	public static final String CHAT_SERVER = QBChat.getChatServerDomain();

	private XMPPConnection connection;

	private ConnectionConfiguration config;
	private Chat chat;

	private String chatLogin;
	private String password;
	private String friendLogin;
	private String TAG = O9ChatController.class.getSimpleName();

	private ChatManager chatManager;

	private class ConnectionManager extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				connection.connect();
				QBUsers.getUserByLogin(chatLogin, new QBCallback() {

					@Override
					public void onComplete(Result arg0, Object arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete(Result result) {
						if (result.isSuccess()) {
							QBUserPagedResult pagedResult = (QBUserPagedResult) result;
							QBUser user = pagedResult.getUsers().get(0);
							String realLogin = QBChat.getChatLoginShort(user);
							try {
								connection.login(chatLogin, password);
								if (onMessageReceivedListener != null) {
									onMessageReceivedListener.onMessageReceived("LOGGED");
								}
								
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								android.util.Log.d(TAG,e.getMessage(),e);
							}

						}

					}
				});

			} catch (XMPPException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	public O9ChatController(String chatLogin, String password)
			throws XMPPException {
		this.chatLogin = chatLogin;
		this.password = password;
		Connection.DEBUG_ENABLED = true;
		config = new ConnectionConfiguration(CHAT_SERVER);
		connection = new XMPPConnection(config);
		new ConnectionManager().execute();
	}

	public Roster getRoster() {
		return connection.getRoster();
	}

	public void startChat(String buddyLogin) {
		this.friendLogin = buddyLogin;

		new Thread(new Runnable() {
			@Override
			public void run() {
				// Chat action 1 -- create connection.

				try {

					// Chat action 2 -- create chat manager.
					chatManager = connection.getChatManager();
					// connection.getRoster().getPresence(buddyLogin).
					// Chat action 3 -- create chat.
					chat = chatManager.createChat(friendLogin, messageListener);

					// Set listener for outcoming messages.
					chatManager.addChatListener(chatManagerListener);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private ChatManagerListener chatManagerListener = new ChatManagerListener() {
		@Override
		public void chatCreated(Chat chat, boolean createdLocally) {
			// Set listener for incoming messages.
			chat.addMessageListener(messageListener);
		}
	};

	public void sendMessage(String message) {
		try {
			if (chat != null) {
				chat.sendMessage(message);
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	private MessageListener messageListener = new MessageListener() {
		@Override
		public void processMessage(Chat chat, Message message) {
			// 'from' and 'to' fields contains senders ids, e.g.
			// 17792-1028@chat.quickblox.com/mac-167
			// 17744-1028@chat.quickblox.com/Smack
			String from = message.getFrom().split("@")[0];
			String to = message.getTo().split("@")[0];

			System.out.println(String.format(
					">>> Message received (from=%s, to=%s): %s", from, to,
					message.getBody()));

			if (onMessageReceivedListener != null) {
				onMessageReceivedListener.onMessageReceived(message);
			}
		}
	};

	public String getActualFriendLogin() {
		return friendLogin;
	}

	public static interface OnMessageReceivedListener {
		void onMessageReceived(Message message);

		void onMessageReceived(String status);
	}

	// Callback that performs when device retrieves incoming message.
	private OnMessageReceivedListener onMessageReceivedListener;

	public OnMessageReceivedListener getOnMessageReceivedListener() {
		return onMessageReceivedListener;
	}

	public void setOnMessageReceivedListener(
			OnMessageReceivedListener onMessageReceivedListener) {
		this.onMessageReceivedListener = onMessageReceivedListener;
	}
}
