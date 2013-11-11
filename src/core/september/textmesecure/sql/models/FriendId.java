package core.september.textmesecure.sql.models;

import com.niusounds.asd.Persistent;
import com.niusounds.asd.PrimaryKey;

public class FriendId {
	@Persistent
    @PrimaryKey(autoIncrement = false)
	private String _id;

	
	
	public FriendId(String _id) {
		super();
		this._id = _id;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
	
	
}
