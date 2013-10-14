package core.september.textmesecure.algo;

import org.w3c.dom.Comment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PolicyDataSource {

	private SQLiteDatabase database;
	private PolicySQLHelper dbHelper;
	private String[] allColumns = { PolicySQLHelper.id, PolicySQLHelper.pubKey,PolicySQLHelper.createdAtUnixTime };

	public PolicyDataSource(Context context) {
		dbHelper = new PolicySQLHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public PolicyDAO findById(long id) {
		Cursor cursor = database.query(PolicySQLHelper.tablename, allColumns,PolicySQLHelper.id + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		PolicyDAO retPolicy = PolicyDAO.fromCursor(cursor);
		cursor.close();
		return retPolicy;
	}

	public PolicyDAO createPolicy(PolicyDAO policy) {
		policy.setCreatedAtUnixTime(System.currentTimeMillis());
		ContentValues values = policy.toContentValues();
		database.insert(PolicySQLHelper.tablename, null,values);
		PolicyDAO retPolicy = findById(policy.getId());
		return retPolicy;
	}
	
	public void delete(long id) {
		database.delete(PolicySQLHelper.tablename,PolicySQLHelper.id + " = " + id, null);
	}

}
