package core.september.textmesecure.interfaces;

import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.quickblox.module.users.model.QBUser;


public interface IAppManager {
	
	//public String getUsername();
//	public boolean sendMessage(String username, String message);

//	public String authenticateUser(String usernameText, String passwordText); 
//	public void messageReceived(String message);
////	public void setUserKey(String value);
//	public boolean isNetworkConnected();
//	public boolean isUserAuthenticated();
//	public String getLastRawFriendList();
//	public void exit();
	
	public void signUpUser(String usernameText, String passwordText, String email);
	public void signInUser(String usernameText, String passwordText);
	public List<QBUser> getFriendList();
	//public void setUpController(String user, String password) throws XMPPException;
//	public String addNewFriendRequest(String friendUsername);
//	public String sendFriendsReqsResponse(String approvedFriendNames,
//			String discardedFriendNames);
	public void startChat(String friendLogin);
	public void sendMessage(String messageString);
	public Presence getPresence(String user);
	public void addFriend(String login) throws XMPPException;

	
}
