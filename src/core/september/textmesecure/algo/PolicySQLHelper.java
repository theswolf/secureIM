package core.september.textmesecure.algo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PolicySQLHelper  extends SQLiteOpenHelper{

	public static final String dbname = "textmesecure.db";
	public static final String tablename = "policies_expires";
	public static final String id = "_id";
	public static final String pubKey = "pub_key";
	public static final String createdAtUnixTime = "createdAtUnixTime";
	public static final int databaseversion = 1;
	
	private static final String DATABASE_CREATE = "create table "
		      + tablename + "(" 
		      + id + " integer primary key," 
		      + pubKey + " text,"
		      + createdAtUnixTime + " integer,"
		      +");";
	
	public PolicySQLHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public PolicySQLHelper(Context context) {
		this(context,dbname,null,databaseversion);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		 Log.w(PolicySQLHelper.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
			    database.execSQL("DROP TABLE IF EXISTS " + tablename);
			    onCreate(database);
		
	}

}
