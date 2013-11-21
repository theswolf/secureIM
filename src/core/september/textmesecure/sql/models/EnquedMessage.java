package core.september.textmesecure.sql.models;

import com.niusounds.asd.Persistent;
import com.niusounds.asd.PrimaryKey;

public class EnquedMessage {
	@Persistent
    @PrimaryKey(autoIncrement = true)
	private String _id;
	@Persistent
	private long timestamp;
	@Persistent
	private String message;
	@Persistent
	private String to;
	public EnquedMessage(long timestamp, String message, String to) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.to = to;
	}
	
	public EnquedMessage() {}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
	
	
}
