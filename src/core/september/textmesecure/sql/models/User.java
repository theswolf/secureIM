package core.september.textmesecure.sql.models;

import com.niusounds.asd.Persistent;
import com.niusounds.asd.PrimaryKey;



public class User {
	
	public enum SubscriptionType {
		BASIC,
		PAID,
		VIP
	}
	
	
	@Persistent
    @PrimaryKey(autoIncrement = false)
	private long _id;
	@Persistent
	private String username;
	@Persistent
	private String password;
	@Persistent
	private String email;
	@Persistent
	private SubscriptionType subscriptionType;
	
	
	
	
	public User(long _id, String username, String password, String email,SubscriptionType subscriptionType) {
		super();
		this._id = _id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.subscriptionType = subscriptionType;
	}
	
	public User() {}

	
	
	public long get_id() {
		return _id;
	}



	public void set_id(long _id) {
		this._id = _id;
	}



	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}
	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
	
	
	
}
