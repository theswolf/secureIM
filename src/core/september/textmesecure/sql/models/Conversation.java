package core.september.textmesecure.sql.models;

import com.niusounds.sqlite.Persistence;
import com.niusounds.sqlite.PrimaryKey;


public class Conversation {
	
	@Persistence
    @PrimaryKey(autoIncrement = true)
	private long _id;
	
	@Persistence
	private String pubKey;
	
	@Persistence
	private long createdAtUnixTime;
	
	public Conversation(Long id, String pubkey, Long createdAt) {
		this._id = id;
		this.pubKey = pubkey;
		this.createdAtUnixTime = createdAt;
	}
	public long getId() {
		return _id;
	}
	public String getPubKey() {
		return pubKey;
	}
	public long getCreatedAtUnixTime() {
		return createdAtUnixTime;
	}
	public void setId(long id) {
		this._id = id;
	}
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
	public void setCreatedAtUnixTime(long createdAtUnixTime) {
		this.createdAtUnixTime = createdAtUnixTime;
	}
	
//	public ContentValues toContentValues() {
//		 ContentValues values = new ContentValues();
//		 values.put(PolicySQLHelper.id, getId());
//		 values.put(PolicySQLHelper.pubKey, getPubKey());
//		 values.put(PolicySQLHelper.createdAtUnixTime, getCreatedAtUnixTime());
//		 return values;
//	}
//	
//	public static PolicyDAO fromCursor(Cursor cursor) {
//		PolicyDAO retPolicy = new PolicyDAO();
//	    retPolicy.setId(cursor.getLong(0));
//	    retPolicy.setPubKey(cursor.getString(1));
//	    retPolicy.setCreatedAtUnixTime(cursor.getLong(2));
//	    return retPolicy;
//	}
}
