package core.september.textmesecure.sql.models;

import com.niusounds.sqlite.Persistence;
import com.niusounds.sqlite.PrimaryKey;

public class User {
	@Persistence
    @PrimaryKey(autoIncrement = false)
	private long _id;
	@Persistence
	private String username;
	@Persistence
	private String password;
	@Persistence
	private String email;
	@Persistence
	private boolean behindProxy;
	
	
	
	public User(long _id, String username, String password, String email,boolean behindProxy) {
		super();
		this._id = _id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.behindProxy = behindProxy;
	}
	public long getId() {
		return _id;
	}
	public void setId(long id) {
		this._id = id;
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
	public boolean isBehindProxy() {
		return behindProxy;
	}
	public void setBehindProxy(boolean behindProxy) {
		this.behindProxy = behindProxy;
	}
	
	
}
