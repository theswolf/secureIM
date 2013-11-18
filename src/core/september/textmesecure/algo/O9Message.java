package core.september.textmesecure.algo;

import com.google.gson.Gson;

public class O9Message {
	
	public enum Type {
		KEY_EXCHANGE,
		MESSAGE
	}
	
	private Type type;
	private String myPublicKey;
	private String friendPublicKey;
	private String encryptedMessage;
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getMyPublicKey() {
		return myPublicKey;
	}
	public void setMyPublicKey(String myPublicKey) {
		this.myPublicKey = myPublicKey;
	}
	public String getFriendPublicKey() {
		return friendPublicKey;
	}
	public void setFriendPublicKey(String friendPublicKey) {
		this.friendPublicKey = friendPublicKey;
	}
	public String getEncryptedMessage() {
		return encryptedMessage;
	}
	public void setEncryptedMessage(String encryptedMessage) {
		this.encryptedMessage = encryptedMessage;
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	
}
