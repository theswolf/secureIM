package core.september.textmesecure.algo;

import com.google.gson.Gson;

public class O9Message {
	
	public enum Type {
		KEY_EXCHANGE,
		KEY_ACCEPT,
		MESSAGE
	}
	
	private Type type;
	private String senderPublicKey;
	private String receiverPublicKey;
	private String encryptedMessage;
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getSenderPublicKey() {
		return senderPublicKey;
	}
	public void setSenderPublicKey(String myPublicKey) {
		this.senderPublicKey = myPublicKey;
	}
	public String getReceiverPublicKey() {
		return receiverPublicKey;
	}
	public void setReceiverPublicKey(String friendPublicKey) {
		this.receiverPublicKey = friendPublicKey;
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
	
	public static O9Message fromString(String input) {
		Gson gson = new Gson();
		return gson.fromJson(input, O9Message.class);
	}

	
}
