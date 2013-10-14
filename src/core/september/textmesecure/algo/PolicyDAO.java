package core.september.textmesecure.algo;

import android.content.ContentValues;
import android.database.Cursor;

public class PolicyDAO {
	private long id;
	private String pubKey;
	private long createdAtUnixTime;
	public long getId() {
		return id;
	}
	public String getPubKey() {
		return pubKey;
	}
	public long getCreatedAtUnixTime() {
		return createdAtUnixTime;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
	public void setCreatedAtUnixTime(long createdAtUnixTime) {
		this.createdAtUnixTime = createdAtUnixTime;
	}
	
	public ContentValues toContentValues() {
		 ContentValues values = new ContentValues();
		 values.put(PolicySQLHelper.id, getId());
		 values.put(PolicySQLHelper.pubKey, getPubKey());
		 values.put(PolicySQLHelper.createdAtUnixTime, getCreatedAtUnixTime());
		 return values;
	}
	
	public static PolicyDAO fromCursor(Cursor cursor) {
		PolicyDAO retPolicy = new PolicyDAO();
	    retPolicy.setId(cursor.getLong(0));
	    retPolicy.setPubKey(cursor.getString(1));
	    retPolicy.setCreatedAtUnixTime(cursor.getLong(2));
	    return retPolicy;
	}
}
