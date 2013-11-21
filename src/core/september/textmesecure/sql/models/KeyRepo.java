package core.september.textmesecure.sql.models;

import com.niusounds.asd.Persistent;
import com.niusounds.asd.PrimaryKey;

public class KeyRepo {
	
	@Persistent
    @PrimaryKey(autoIncrement = true)
	private long _id;
	
	@Persistent
	private String friendLogin;
	
	@Persistent
	private int TTL;
	
	@Persistent
	private String myPrivateKey;
	
	@Persistent
	private String myPublicKey;
	
	@Persistent
	private String friendKey;
	
	
	public KeyRepo() {}
	
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	public String getFriendLogin() {
		return friendLogin;
	}
	public void setFriendLogin(String friendLogin) {
		this.friendLogin = friendLogin;
	}
	public int getTTL() {
		return TTL;
	}
	public void setTTL(int tTL) {
		TTL = tTL;
	}
	public String getMyPrivateKey() {
		return myPrivateKey;
	}
	public void setMyPrivateKey(String myPrivateKey) {
		this.myPrivateKey = myPrivateKey;
	}
	
	public String getMyPublicKey() {
		return myPublicKey;
	}
	public void setMyPublicKey(String myPublicKey) {
		this.myPublicKey = myPublicKey;
	}
	public String getFriendKey() {
		return friendKey;
	}
	public void setFriendKey(String friendKey) {
		this.friendKey = friendKey;
	}
	
	
}
